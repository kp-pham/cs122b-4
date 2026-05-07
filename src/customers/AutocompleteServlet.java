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

@WebServlet(name = "customers.AutocompleteServlet", urlPatterns="/api/customers/autocomplete")
public class AutocompleteServlet extends HttpServlet {
    private static final long SerialVersionUID = 2L;

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
        PrintWriter out = response.getWriter();

        JsonArray jsonArray = new JsonArray();

        String q = request.getParameter("q");

        String trimmedQuery = (q == null) ? null : q.trim();
        boolean hasQuery = (trimmedQuery != null && !trimmedQuery.isEmpty());

        if (!hasQuery || trimmedQuery.length() <= 3) {
            out.write(jsonArray.toString());
            response.setStatus(200);

            return;
        }
    }
}
