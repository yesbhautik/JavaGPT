import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class DBUtil {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    static {
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
        if (input == null) {
            System.out.println("Sorry, unable to find db.properties");
            // Removed return statement
        } else {
            prop.load(input);
            URL = prop.getProperty("db.url");
            USER = prop.getProperty("db.user");
            PASSWORD = prop.getProperty("db.password");
        }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}