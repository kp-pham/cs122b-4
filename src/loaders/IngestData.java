package loaders;

import java.sql.Connection;
import java.sql.DriverManager;

public class IngestData {
    public static void main(String[] args) throws Exception {
        String username = System.getenv("USER");
        String password = System.getenv("PASSWORD");
        String url = System.getenv("URL") + "?allowLoadLocalInfile=true";

        Class.forName(System.getenv("JDBC_DRIVER"));
        Connection conn = DriverManager.getConnection(url, username, password);

        System.out.println("Loading movies...");

        MovieLoader movieLoader = new MovieLoader(conn);
        movieLoader.load("data/movies.csv");

        System.out.println("\nLoading movies completed.\n");

        System.out.println("Loading stars...");

        StarLoader starLoader = new StarLoader(conn);
        starLoader.load("data/stars.csv");

        System.out.println("\nLoading stars completed.\n");

        System.out.println("Loading genres...");

        GenreLoader genreLoader = new GenreLoader(conn);
        genreLoader.load("data/genres.csv");

        System.out.println("\nLoading genres completed.\n");

        System.out.println("\nLoading movie-star relationships...");

        StarsInMoviesLoader starsInMoviesLoader = new StarsInMoviesLoader(conn);
        starsInMoviesLoader.load("data/stars_in_movies.csv");

        System.out.println("\nLoading movie-star relationships completed.\n");

        conn.close();
    }
}
