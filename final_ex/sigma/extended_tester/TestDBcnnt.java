import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class TestDBcnnt {
public static void main(String[] args) {
// Database connection parameters

String url = "jdbc:mysql://localhost:3306/25git1";
String user = "root";
String password = "";
try (Connection conn = DriverManager.getConnection(url, user, password)) {
System.out.println("Connected to database: " + conn.getCatalog());
} catch (SQLException e) {
e.printStackTrace();
}
}
}