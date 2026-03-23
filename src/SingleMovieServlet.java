import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

@WebServlet(name = "SingleMovieServlet", urlPatterns="/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

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

        request.getServletContext().log("getting movie id: " + id);

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {

            // Minimize number of reads to avoid the N + 1 problem
            String query = "SELECT * FROM movies AS M," +
                           "genres_in_movies AS GIM, genres AS G," +
                           "stars_in_movies AS SIM, stars AS S, ratings AS R" +
                           "WHERE M.id = GIM.movie_id AND GIM.genre_id = G.id" +
                           "AND M.id = SIM.movie_id AND SIM.star_id = S.id" +
                           "AND M.id = R.movie_id AND M.id = ?";

            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, id);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

            }

            rs.close();
            statement.close();

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            request.getServletContext().log("Error:", e);
            request.setStatus(500);
        } finally {
            out.close();
        }
    }
}