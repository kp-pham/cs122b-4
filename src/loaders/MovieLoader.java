package loaders;

import java.io.FileReader;
import java.sql.Connection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;
import java.util.HashSet;

public class MovieLoader implements DataLoader {
    private final Connection conn;

    public MovieLoader(Connection conn) {
        this.conn = conn;
    }

    public Set<String> load(String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader("movies.csv"))) {


        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public boolean validHeader(String[] fields) {
        return  fields.length == 4 &&
                fields[0].equalsIgnoreCase("id") &&
                fields[1].equalsIgnoreCase("title") &&
                fields[2].equalsIgnoreCase("year") &&
                fields[3].equalsIgnoreCase("director");
    }


}
