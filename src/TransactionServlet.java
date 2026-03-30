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
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet(name = "TransactionServlet", urlPatterns = "/api/transactions")
public class TransactionServlet extends HttpServlet {
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

        HttpSession session = request.getSession();

        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return;
        }

        PrintWriter out = response.getWriter();

        BigDecimal total = BigDecimal.ZERO;

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT price FROM movies WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);

            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                int movieId = entry.getKey();
                int quantity = entry.getValue();

                statement.setInt(1, movieId);
                ResultSet rs = statement.executeQuery();

                if (!rs.next()) continue;

                BigDecimal price = rs.getBigDecimal("price");

                BigDecimal subtotal = price.multiply(new BigDecimal(quantity));
                total = total.add(subtotal);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("total", total.doubleValue());

            out.write(jsonObject.toString());
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        HttpSession session = request.getSession();

        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return;
        }

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String card = request.getParameter("card");
        String expiration = request.getParameter("expiration");

        String trimmedFirstName = (firstName == null) ? null : firstName.trim();
        String trimmedLastName = (lastName == null) ? null : lastName.trim();
        String trimmedCard = (card == null) ? null : card.trim();
        String trimmedExpiration = (expiration == null) ? null : expiration.trim();

        boolean hasFirstName = (trimmedFirstName != null && !trimmedFirstName.isEmpty());
        boolean hasLastName = (trimmedLastName != null && !trimmedLastName.isEmpty());
        boolean hasCard = (trimmedCard != null && !trimmedCard.isEmpty());
        boolean hasExpiration = (trimmedExpiration != null && !trimmedExpiration.isEmpty());

        PrintWriter out = response.getWriter();

        if (!hasFirstName || !hasLastName || !hasCard || !hasExpiration) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", "Please provide the required payment information");
            out.write(jsonObject.toString());

            response.setStatus(400);
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            String query = "INSERT INTO sales (customerId, movieId, saleDate, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);

            Customer customer = (Customer) session.getAttribute("customer");
            int customerId = customer.getId();

            Date date = new Date(System.currentTimeMillis());

            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                int movieId = entry.getKey();
                int quantity = entry.getValue();

                statement.setInt(1, customerId);
                statement.setString(2, movieId.toString());
                statement.setDate(3, date);
                statement.setInt(4, quantity);
            }


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
