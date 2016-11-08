import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class FlatsRunner {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/hwFlats?useUnicode=yes&characterEncoding=UTF-8";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "555282289";

    static Connection connection;

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        try {
            try {
                connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                FlatsUtils.initDB();

                while (true) {
                    System.out.println("1. List all flats");
                    System.out.println("2. Add/remove flat");
                    System.out.println("3. Search");
                    System.out.print("->");
                    String line = scanner.nextLine();

                    switch (line) {
                        case ("1"):
                            FlatsUtils.listAllFlats();
                            break;
                        case ("2"):
                            FlatsUtils.editDB();
                            break;
                        case ("3"):
                            FlatsUtils.searchFor(scanner);
                            break;
                        default:
                            return;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            scanner.close();
            if (connection != null) connection.close();
        }
    }

}
