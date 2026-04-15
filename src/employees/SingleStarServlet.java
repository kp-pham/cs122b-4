package employees;

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
import java.sql.Types;
import java.sql.ResultSet;
import java.util.ArrayList;

@WebServlet(name = "employees.SingleStarServlet", urlPatterns = "/api/employees/star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String name = request.getParameter("name");
        String birthYear = request.getParameter("birthYear");

        PrintWriter out = response.getWriter();

        if (name == null || name.isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", "Please provide a name for the star.");

            out.write(jsonObject.toString());
            response.setStatus(400);

            out.close();
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            String query = "INSERT INTO (id, name, birthYear) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, id);
            statement.setString(2, name);

            if (birthYear == null || birthYear.isEmpty()) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, birthYear);
            }

            statement.executeUpdate();

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
