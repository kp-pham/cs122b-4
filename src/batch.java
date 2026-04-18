import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public class BatchInsert {
    public static void main (String[] args) throws Exception {
        String username = System.getenv("USER");
        String password = System.getenv("PASSWORD");
        String url = System.getenv("URL");

        Class.forName(System.getenv("JDBC_DRIVER"));
        Connection conn = DriverManager.getConnection(url, username, password);

        String query = "DROP TABLE IF EXISTS ft;" +
                       "CREATE TABLE ft (" +
                       "    entryID INT AUTO_INCREMENT," +
                       "    entry text," +
                       "    PRIMARY KEY (entryID)," +
                       "    FULLTEXT (entry)) ENGINE=MyISAM";

    }
}
