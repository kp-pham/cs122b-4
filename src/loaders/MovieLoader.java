package loaders;

import java.io.FileReader;
import java.sql.Connection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class MovieLoader implements DataLoader {
    private final Connection conn;
    private final Set<String> movieIds;

    private final int EXPECTED_FIELDS_LENGTH = 4;

    public MovieLoader(Connection conn) {
        this.conn = conn;
        this.movieIds = new HashSet<String>();
    }

    public Set<String> load(String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader("movies.csv"))) {
            String header = reader.readLine();

            if (header == null) {
                return Set.of();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                process(fields);
            }

        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private boolean validHeader(String[] fields) {
        return  fields.length == 4 &&
                fields[0].equalsIgnoreCase("id") &&
                fields[1].equalsIgnoreCase("title") &&
                fields[2].equalsIgnoreCase("year") &&
                fields[3].equalsIgnoreCase("director");
    }

    private void process(String[] fields) {
        if (!validFields(fields)) {
            System.out.printf("Incorrect number of fields. Expected %d, Received %d%n.", EXPECTED_FIELDS_LENGTH, fields.length);
        }

        for (int i = 0; i < fields.length; ++i) {
            fields[i] = fields[i].trim();
        }

        String id = fields[0], title = fields[1], year = fields[2], director = fields[3];

        if (!validId(id)) {
            System.out.printf("Invalid id: %s", String.join(", ", fields));
        } else if (!validTitle(title)) {
            System.out.printf("Invalid title: %s", String.join(",", fields));
        } else if (!validYear(year)) {
            System.out.printf("Invalid year: %s", String.join(",", fields));
        }
        // Length 4
        // Id is non null and nonempty and unique
        // Title is non null and nonempty
        // Year is non null and non empty and valid integer
        // Director is non null and non empty
    }

    private boolean validFields(String[] fields) {
        return fields.length == EXPECTED_FIELDS_LENGTH;
    }

    private boolean validId(String id) {
        return id != null && !id.isEmpty();
    }

    private boolean validTitle(String title) {
        return title != null && !title.isEmpty();
    }

    private boolean validYear(String year) {
        if (year == null || !year.isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(year);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean validDirector(String director) {

    }
}
