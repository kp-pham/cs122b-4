package loaders;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MovieLoader implements DataLoader {
    private final Connection conn;

    public MovieLoader(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void load(String file) throws Exception {
        createStagingTable();
        loadToStaging(file);
        validateAndTransform();
        reportErrors();
    }

    private void createStagingTable() throws SQLException {
        String dropQuery = "DROP TABLE IF EXISTS movies_staging";
        String createQuery = "CREATE TABLE movies_staging(" +
                             "    id TEXT, " +
                             "    title TEXT, " +
                             "    year TEXT, " +
                             "    director TEXT" +
                             ")";

        PreparedStatement statement = conn.prepareStatement(dropQuery);
        statement.executeUpdate();

        statement = conn.prepareStatement(createQuery);
        statement.executeUpdate();

        statement.close();
    }

    private void loadToStaging(String file) throws SQLException {
        String query = String.format("LOAD DATA LOCAL INFILE '%s' " +
                                     "INTO TABLE movies_staging " +
                                     "FIELDS TERMINATED BY ',' " +
                                     "ENCLOSED BY '\"' " +
                                     "LINES TERMINATED BY '\\r\\n' " +
                                     "IGNORE 1 ROWS", file);

        PreparedStatement statement = conn.prepareStatement(query);
        statement.executeUpdate(query);
        statement.close();
    }

    private void validateAndTransform() throws SQLException {
        String query = "INSERT INTO movies (id, title, year, director, price) " +
                       "SELECT id, " +
                       "       title, " +
                       "       CAST(year AS UNSIGNED), " +
                       "       director, " +
                       "       FLOOR(1 + RAND() * 30) + ELT(FLOOR(1 + RAND() * 3), 0.99, 0.49, 0.00) " +
                       "FROM movies_staging AS S " +
                       "WHERE id IS NOT NULL AND id != '' " +
                       "AND title IS NOT NULL AND title != '' " +
                       "AND year IS NOT NULL AND year != '' AND year REGEXP '^[0-9]+$' " +
                       "AND director IS NOT NULL AND director != '' " +
                       "AND NOT EXISTS ( " +
                       "    SELECT 1 FROM movies AS M WHERE M.id = S.id " +
                       ") " +
                       "AND id IN ( " +
                       "    SELECT id " +
                       "    FROM movies_staging " +
                       "    GROUP BY id " +
                       "    HAVING COUNT(*) = 1 " +
                       ")";

        PreparedStatement statement = conn.prepareStatement(query);
        statement.executeUpdate();
        statement.close();
    }

    private void reportErrors() throws SQLException {
        String query = "SELECT *, " +
                       "CASE " +
                       "    WHEN id IS NULL OR id = '' THEN 'Invalid or missing id' " +
                       "    WHEN title IS NULL OR title = '' THEN 'Invalid or missing title' " +
                       "    WHEN year IS NULL OR year = '' OR year NOT REGEXP '^[0-9]+$' THEN 'Invalid or missing year' " +
                       "    WHEN director IS NULL OR director = '' THEN 'Invalid or missing director' " +
                       "    WHEN id IN ( " +
                       "        SELECT id " +
                       "        FROM movies_staging " +
                       "        GROUP BY id " +
                       "        HAVING COUNT(*) > 1" +
                       "    ) THEN 'Duplicate in file' " +
                       "    WHEN EXISTS ( " +
                       "        SELECT 1 FROM movies AS M WHERE M.id = S.id " +
                       "    ) 'Movie already exists in database' " +
                       "   END AS error" +
                       "FROM movies_staging AS S " +
                       "WHERE id IS NULL OR id = '' " +
                       "OR title IS NULL OR title = '' " +
                       "OR year IS NULL OR year = '' OR year NOT REGEXP '^[0-9]+$' " +
                       "OR director IS NULL OR director = '' " +
                       "OR EXISTS ( " +
                       "    SELECT 1 FROM movies AS M WHERE M.id = S.id " +
                       ") " +
                       "OR id IN ( " +
                       "    SELECT id" +
                       "    FROM movies_staging " +
                       "    GROUP BY id " +
                       "    HAVING COUNT(*) > 1 " +
                       ")";

        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            System.out.printf("%s: %s, %s, %s, %s",
                              rs.getString("error"),
                              rs.getString("id"),
                              rs.getString("title"),
                              rs.getString("year"),
                              rs.getString("director")
            );
        }

        statement.close();
    }
}
