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

public class MovieLoader implements DataLoader {
    private final Connection conn;

    public MovieLoader(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void load(String file) throws Exception {
        createStagingTable();
        loadToStaging(file);
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
        String query = String.format("LOAD DATA LOCAL INFILE %s " +
                                     "INTO TABLE movies_staging " +
                                     "FIELDS TERMINATED BY ',' " +
                                     "ENCLOSED BY '\"' " +
                                     "LINES TERMINATED BY '\r\n' " +
                                     "IGNORE 1 ROWS", file);

        PreparedStatement statement = conn.prepareStatement(query);
        statement.executeUpdate(query);
        statement.close();
    }
}

//public class MovieLoader implements DataLoader {
//    private final Connection conn;
//    private final Set<String> movieIds;
//
//    private final int EXPECTED_FIELDS_LENGTH = 4;
//
//    public MovieLoader(Connection conn) {
//        this.conn = conn;
//        this.movieIds = new HashSet<>();
//    }
//
//    public Set<String> load(String file) {
//        try (BufferedReader reader = new BufferedReader(new FileReader("movies.csv"))) {
//            String header = reader.readLine();
//
//            if (header == null) {
//                return Set.of();
//            }
//
//            String query = "INSERT INTO movies VALUES (?, ?, ?, ?, ?)";
//            PreparedStatement statement = conn.prepareStatement(query);
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] fields = line.split(",");
//                process(fields, statement);
//            }
//
//        } catch (Exception e) {
//           e.printStackTrace();
//        }
//    }
//
//    private boolean validHeader(String[] fields) {
//        return  fields.length == 4 &&
//                fields[0].equalsIgnoreCase("id") &&
//                fields[1].equalsIgnoreCase("title") &&
//                fields[2].equalsIgnoreCase("year") &&
//                fields[3].equalsIgnoreCase("director");
//    }
//
//    private void process(String[] fields, PreparedStatement statement) throws SQLException {
//        if (!validFields(fields)) {
//            System.out.printf("Expected %d fields, Received %d fields: %s\n", EXPECTED_FIELDS_LENGTH, fields.length, String.join(", ", fields));
//        }
//
//        for (int i = 0; i < fields.length; ++i) {
//            fields[i] = fields[i].trim();
//        }
//
//        String id = fields[0], title = fields[1], year = fields[2], director = fields[3];
//
//        if (!validId(id)) {
//            System.out.printf("Invalid id: %s\n", String.join(", ", fields));
//
//        } else if (!uniqueId(id)) {
//            System.out.printf("Duplicate movie: %s\n", String.join(", ", fields));
//
//        } else if (!validTitle(title)) {
//            System.out.printf("Invalid title: %s\n", String.join(",", fields));
//
//        } else if (!validYear(year)) {
//            System.out.printf("Invalid year: %s\n", String.join(",", fields));
//
//        } else if (!validDirector(director)) {
//            System.out.printf("Invalid director: %s\n", String.join(",", fields));
//
//        } else {
//            for (int i = 0; i < fields.length; ++i) {
//                statement.setObject(i + 1, fields[i]);
//            }
//
//            statement.setDouble(fields.length, getPrice());
//
//            statement.addBatch();
//
//            movieIds.add(id);
//        }
//    }
//
//    private boolean validFields(String[] fields) {
//        return fields.length == EXPECTED_FIELDS_LENGTH;
//    }
//
//    private boolean validId(String id) {
//        return id != null && !id.isEmpty();
//    }
//
//    private boolean uniqueId(String id) {
//        return !movieIds.contains(id);
//    }
//
//    private boolean validTitle(String title) {
//        return title != null && !title.isEmpty();
//    }
//
//    private boolean validYear(String year) {
//        if (year == null || year.isEmpty()) {
//            return false;
//        }
//
//        try {
//            Integer.parseInt(year);
//            return true;
//
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private boolean validDirector(String director) {
//        return director != null && !director.isEmpty();
//    }
//
//    private double getPrice() {
//        double[] values = {0.00, 0.49, 0.99};
//
//        int dollars = (int) (Math.random() * 30) + 1;
//        double cents = values[(int)(Math.random() * 3)];
//
//        return dollars + cents;
//    }
//}
