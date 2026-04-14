import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class UpdateSecurePassword {
    public statis void main(String[] args) throws Exception {
        String username = System.getenv("USER");
        String password = System.getenv("PASSWORD");
        String url = System.getenv("URL");

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(username, password, url);
        Statement statement = conn.createStatement();

        String alterQuery = "ALTER TABLE customers MODIFY COLUMN password VARCHAR(128)";
        int alterResult = statement.executeUpdate(alterQuery);
        System.out.println("altering customers table schema completed, " + alterResult + " rows affected");
    }
}
