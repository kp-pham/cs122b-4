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

@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
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

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");

        String trimmedTitle = (title == null) ? null : title.trim();
        String trimmedYear = (year == null) ? null : year.trim();
        String trimmedDirector = (director == null) ? null : director.trim();
        String trimmedStar = (star == null) ? null : star.trim();

        boolean hasTitle = (title != null && !title.isEmpty());
        boolean hasYear = (title != null && !year.isEmpty());
        boolean hasDirector = (director != null && !director.isEmpty());
        boolean hasStar = (star != null && !star.isEmpty());

        PrintWriter out = response.getWriter();

        if (title == null &&  year == null && director == null && star == null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", "Please provide at least one search parameter");
            out.write(jsonObject.toString());

            response.setStatus(400);
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT M.id, M.title, M.year, M.director, M.rating, " +
                           "CONCAT('[', GROUP_CONCAT(DISTINCT G.name SEPARATOR ', '), ']') AS genres, " +
                           "CONCAT('[', GROUP_CONCAT(DISTINCT JSON_OBJECT('id', S.id, 'name', S.name)), ']') AS stars " +
                           "FROM movies AS M " +
                           "LEFT JOIN genres_in_movies AS GIM ON M.id = GIM.movieId " +
                           "LEFT JOIN genres AS G ON GIM.genreId = G.id " +
                           "LEFT JOIN stars_in_movies AS SIM ON M.id = SIM.movieId " +
                           "LEFT JOIN stars AS S ON SIM.starId = S.id " +
                           "WHERE 1 = 1 ";

            if (title != null && !title.isEmpty()) {
                query += "AND M.title ILIKE ? ";
            }

            if (year != null && !year.isEmpty()) {
                query += "AND M.year = ? ";
            }

            if (director != null && !director.isEmpty()) {
                query += "AND M.director ILIKE ? ";
            }

            if (star != null && !star.trim().isEmpty()) {
                query += "AND S.name ILIKE ? ";
            }

            query += "GROUP BY M.id, M.title, M.year, M.director, M.rating " +
                     "ORDER BY M.rating DESC";

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            request.getServletContext().log("Error:", e);
            response.setStatus(500);

        } finally {
            out.close();
        }
    }
}
