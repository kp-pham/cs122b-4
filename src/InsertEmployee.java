import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class InsertEmployee {
    public static void main(String[] args) throws Exception {
        String username = System.getenv("USER");
        String password = System.getenv("PASSWORD");
        String url = System.getenv("URL");

        String query = "INSERT INTO employees (email, password, fullName) VALUES (?, ?, ?)";

        Class.forName(System.getenv("JDBC_DRIVER"));
        Connection conn = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = conn.prepareStatement(query);


    }
}
