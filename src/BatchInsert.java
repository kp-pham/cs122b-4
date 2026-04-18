import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

/*

The SQL command to create the table ft.

DROP TABLE IF EXISTS ft;
CREATE TABLE ft (
    entryID INT AUTO_INCREMENT,
    entry text,
    PRIMARY KEY (entryID),
    FULLTEXT (entry)) ENGINE=MyISAM;

*/

/*

Note: Please change the username, password and the name of the database.

*/

public class BatchInsert {

    public static void main (String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        Connection conn = null;

        Class.forName(System.getenv("JDBC_DRIVER")).newInstance();

        try {
            conn = DriverManager.getConnection(System.getenv("URL"), System.getenv("USER"), System.getenv("PASSWORD"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement psInsertRecord = null;
        String sqlInsertRecord = null;

        int[] iNoRows = null;

        sqlInsertRecord = "INSERT INTO ft (entryID, entry) values (?, ?)";
        try {
			conn.setAutoCommit(false);

            psInsertRecord=conn.prepareStatement(sqlInsertRecord);


            for(int i=1;i<=50;i++)
            {
                psInsertRecord.setInt(1, i);
                psInsertRecord.setString(2,"My next text piece " + (i*i));
                psInsertRecord.addBatch();
            }

			iNoRows=psInsertRecord.executeBatch();
			conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if(psInsertRecord!=null) psInsertRecord.close();
            if(conn!=null) conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}


