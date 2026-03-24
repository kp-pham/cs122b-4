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

@WebServlet(name = "MovieListServlet", urlPatterns="/api/")
public class MovieListServlet extends HttpServlet {
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

        String id = request.getParameter("id");

        request.getServletContext().log("getting top 20 rated movies");

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT M.id, M.title, M.year, M.director, R.rating, " +
                           "CONCAT('[', GROUP_CONCAT(DISTINCT G.name SEPARATOR ', '), ']') AS genres, " +
                           "CONCAT('[', GROUP_CONCAT(DISTINCT JSON_OBJECT('id', S.id, 'name', S.name)), ']') AS stars " +
                           "FROM ( " +
                           "    SELECT M.id, M.title, M.year, M.director, R.rating " +
                           "    FROM movies AS M " +
                           "    LEFT JOIN ratings R ON M.id = R.movieId " +
                           "    ORDER BY R.rating DESC " +
                           "    LIMIT 20" +
                           ") AS M " +
                           "LEFT JOIN genres_in_movies AS GIM ON M.id = GIM.movieId " +
                           "LEFT JOIN genres AS G ON GIM.genreId = G.id " +
                           "LEFT JOIN stars_in_movies AS SIM ON M.id = SIM.movieId " +
                           "LEFT JOIN stars AS S ON SIM.starId = S.id " +
                           "LEFT JOIN ratings AS R ON M.id = R.movieId " +
                           "GROUP BY M.id, M.title, M.year, M.director, R.rating";

            PreparedStatement statement = conn.prepareStatement(query);

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("id", rs.getString("M.id"));
                jsonObject.addProperty("title", rs.getString("M.title"));
                jsonObject.addProperty("year", rs.getString("M.year"));
                jsonObject.addProperty("director", rs.getString("M.director"));
                jsonObject.addProperty("rating", rs.getString("R.rating"));

                JsonArray genresArray = JsonParser.parseString(rs.getString("genres")).getAsJsonArray();
                jsonObject.add("genres", genresArray);

                JsonArray starsArray = JsonParser.parseString(rs.getString("stars")).getAsJsonArray();
                jsonObject.add("stars", starsArray);

                jsonArray.add(jsonObject);
            }

            rs.close();
            statement.close();

            out.write(jsonArray.toString());
            response.setStatus(200);

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