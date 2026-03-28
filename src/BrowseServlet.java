import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


@WebServlet(name = "BrowseServlet", urlPatterns = "/api/browse")
public class BrowseServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String genre = request.getParameter("genre");
        String prefix = request.getParameter("prefix");

        String query = "SELECT M.id, M.title, M.year, M.director, M.rating, " +
                       "CONCAT('[', GROUP_CONCAT(DISTINCT G.name SEPARATOR ', '), ']') AS genres, " +
                       "CONCAT('[', GROUP_CONCAT(DISTINCT JSON_OBJECT('id', S.id, 'name', S.name)), ']') AS stars " +
                       "FROM movies AS M " +
                       "LEFT JOIN genres_in_movies AS GIM ON M.id = GIM.movieId " +
                       "LEFT JOIN genres AS G ON GIM.genreId = G.id " +
                       "LEFT JOIN stars_in_movies AS SIM ON M.id = SIM.movieId " +
                       "LEFT JOIN stars AS S ON SIM.starId = S.id ";

        if (genre != null) {
            query += "WHERE M.genre = ? ";
        } else {
            query += "WHERE M.name ILIKE ? ";
        }

        query += "GROUP BY M.id, M.title, M.year, M.director, M.rating";
    }
}
