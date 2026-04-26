package loaders;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StarLoader extends DataLoader {
    public StarLoader(Connection conn) {
        super(conn);
    }

    @Override
    protected void createStagingTable() throws SQLException {
        String dropQuery = "DROP TABLE IF EXISTS stars_staging";
        String createQuery = "CREATE TABLE stars_staging( " +
                             "    id TEXT, " +
                             "    name TEXT " +
                             "    birthYear TEXT " +
                             ")";

        PreparedStatement statement = conn.prepareStatement(dropQuery);
        statement.executeUpdate();

        statement = conn.prepareStatement(createQuery);
        statement.executeUpdate();

        statement.close();
    }

    @Override
    protected void loadToStaging(String file) throws SQLException {
        String query = "LOAD DATA LOCAL INFILE ? " +
                       "INTO TABLE stars_staging " +
                       "FIELDS TERMINATED BY ',' " +
                       "ENCLOSED BY '\"' " +
                       "LINES TERMINATED BY '\\r\\n' " +
                       "IGNORE 1 ROWS";

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, file);

        statement.executeUpdate();
        statement.close();
    }

    @Override
    protected void validateAndTransform() throws SQLException {
        String query = "INSERT INTO stars (id, name, birthYear) " +
                       "WITH deduped AS (" +
                       "    SELECT id " +
                       "    FROM stars_staging " +
                       "    GROUP BY id " +
                       "    HAVING COUNT(*) = 1 " +
                       "), " +
                       "cleaned AS ( " +
                       "    SELECT S.id, S.name " +
                       "    FROM stars_staging AS S " +
                       "    INNER JOIN deduped AS D ON D.id = S.id " +
                       "    WHERE S.id IS NOT NULL AND S.id != '' " +
                       "    AND S.name IS NOT NULL AND S.name != '' " +
                       ") " +
                       "SELECT C.id, C.name, NULL " +
                       "FROM cleaned AS C " +
                       "LEFT JOIN stars AS S ON C.id = S.id " +
                       "WHERE S.id IS NULL";

        PreparedStatement statement = conn.prepareStatement(query);
        statement.executeUpdate();
        statement.close();
    }

    @Override
    protected void reportErrors() throws SQLException {
        String query = "WITH dupes AS ( " +
                       "    SELECT id " +
                       "    FROM stars_staging " +
                       "    GROUP BY id " +
                       "    HAVING COUNT(*) > 1 " +
                       ") " +
                       "SELECT S.id, S.name " +
                       "CASE " +
                       "    WHEN S.id IS NULL OR S.id = '' THEN 'Invalid or missing id' " +
                       "    WHEN S.name IS NULL OR S.name = '' THEN 'Invalid or missing name' " +
                       "END AS error " +
                       "FROM stars_staging AS S " +
                       "LEFT JOIN dupes AS D ON D.id = S.id " +
                       "LEFT JOIN stars ON stars.id = S.id " +
                       "WHERE S.id IS NULL OR S.id = '' " +
                       "OR S.name IS NULL OR S.name = ''";

        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            System.out.printf("%s: %s, %s, %s%n",
                              rs.getString("error"),
                              rs.getString("id"),
                              rs.getString("name"),
                              rs.getString("birthYear")

            );
        }

        rs.close();
        statement.close();
    }
}
