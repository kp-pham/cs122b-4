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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
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
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT title, price FROM movies WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);

            JsonArray jsonArray = new JsonArray();

            BigDecimal total = BigDecimal.ZERO;

            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                int movieId = entry.getKey();
                int quantity = entry.getValue();

                statement.setInt(1, movieId);
                ResultSet rs = statement.executeQuery();

                if (!rs.next()) continue;

                String title = rs.getString("title");
                BigDecimal price = rs.getBigDecimal("price");

                BigDecimal subtotal = price.multiply(new BigDecimal(quantity));
                subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

                total = total.add(subtotal);

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", movieId);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("quantity", quantity);
                jsonObject.addProperty("price", price.setScale(2, RoundingMode.HALF_UP).doubleValue());
                jsonObject.addProperty("subtotal", subtotal.doubleValue());

                jsonArray.add(jsonObject);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("items", jsonArray);
            jsonObject.addProperty("total", total);

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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }


    }
}
