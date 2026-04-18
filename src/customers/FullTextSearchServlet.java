package customers;

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
import java.util.Set;

@WebServlet(name = "customers.FullTextSearchServlet", urlPatterns = "/api/customers/full-text")
public class FullTextSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    private DataSource dataSource;

    private static final String SORT_TITLE_DESC_RATING_ASC = "title-desc-rating-asc";
    private static final String SORT_TITLE_ASC_RATING_ASC = "title-asc-rating-asc";
    private static final String SORT_TITLE_DESC_RATING_DESC = "title-desc-rating-desc";

    private static final String SORT_RATING_ASC_TITLE_DESC = "rating-asc-title-desc";
    private static final String SORT_RATING_DESC_TITLE_ASC = "rating-desc-title-asc";
    private static final String SORT_RATING_ASC_TITLE_ASC = "rating-asc-title-asc";
    private static final String SORT_RATING_DESC_TITLE_DESC = "rating-desc-title-desc";

    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 25;

    Set<Integer> ALLOWED_PAGE_SIZES = Set.of(10, 25, 50, 100);

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String title = request.getParameter("title");

        PrintWriter out = response.getWriter();

        if (title == null || title.trim().isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", "Please enter a search term.");
            out.write(jsonObject.toString());

            response.setStatus(400);
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT M.id, M.title, M.year, M.director, R.rating, " +
                           "CONCAT('[', GROUP_CONCAT(DISTINCT G.name ORDER BY G.name ASC SEPARATOR ', '), ']') AS genres, " +
                           "CONCAT('[', GROUP_CONCAT(DISTINCT JSON_OBJECT('id', S.id, 'name', S.name) ORDER BY S.movie_count DESC, S.name ASC), ']') AS stars " +
                           "FROM movies AS M " +
                           "LEFT JOIN genres_in_movies AS GIM ON M.id = GIM.movieId " +
                           "LEFT JOIN genres AS G ON GIM.genreId = G.id " +
                           "LEFT JOIN stars_in_movies AS SIM ON M.id = SIM.movieId " +
                           "LEFT JOIN (" +
                           "    SELECT S.id, S.name, COUNT(SIM.movieId) AS movie_count " +
                           "    FROM stars_in_movies AS SIM " +
                           "    LEFT JOIN stars AS S ON SIM.starId = S.id " +
                           "    GROUP BY S.id " +
                           ") AS S ON SIM.starId = S.id " +
                           "LEFT JOIN ratings AS R ON R.movieId = M.id " +
                           "WHERE MATCH (M.title) AGAINST (? IN BOOLEAN MODE) " +
                           "GROUP BY M.id, M.title, M.year, M.director, R.rating ";

            switch(sort) {
                case SORT_TITLE_DESC_RATING_ASC:
                    query += "ORDER BY M.title DESC, R.rating ASC ";
                    break;

                case SORT_TITLE_ASC_RATING_ASC:
                    query += "ORDER BY M.title ASC, R.rating ASC ";
                    break;

                case SORT_TITLE_DESC_RATING_DESC:
                    query += "ORDER BY M.title DESC, R.rating DESC ";
                    break;

                case SORT_RATING_ASC_TITLE_DESC:
                    query += "ORDER BY R.rating ASC, M.title DESC ";
                    break;

                case SORT_RATING_DESC_TITLE_ASC:
                    query += "ORDER BY R.rating DESC, M.title ASC ";
                    break;

                case SORT_RATING_ASC_TITLE_ASC:
                    query += "ORDER BY R.rating ASC, M.title ASC ";
                    break;

                case SORT_RATING_DESC_TITLE_DESC:
                    query += "ORDER BY R.rating DESC, M.title DESC ";
                    break;

                default:
                    query += "ORDER BY M.title ASC, R.rating DESC ";
                    break;
            }

            query += "LIMIT ? OFFSET ?";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, title);
            statement.setInt(2, pageSize + 1);
            statement.setInt(3, offset);

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

            JsonObject jsonObject = new JsonObject();

            if (jsonArray.size() > pageSize) {
                jsonObject.addProperty("lastPage", false);
                jsonObject.addProperty("outOfBounds", false);
                jsonArray.remove(jsonArray.size() - 1);

            } else if (jsonArray.isEmpty()) {
                jsonObject.addProperty("lastPage", true);
                jsonObject.addProperty("outOfBounds", true);

            } else {
                jsonObject.addProperty("lastPage", true);
                jsonObject.addProperty("outOfBounds", false);
            }

            jsonObject.add("results", jsonArray);

            rs.close();
            statement.close();

            out.write(jsonObject.toString());
            response.setStatus(200);

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", e.getMessage());
            out.write(jsonObject.toString());

            request.getServletContext().log("Error:", e);
            response.setStatus(500);

        } finally {
            out.close();
        }
    }
}
