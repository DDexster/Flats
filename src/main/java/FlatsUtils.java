import java.sql.*;
import java.util.*;

public class FlatsUtils {

    private static Scanner scanner = new Scanner(System.in);

    public static void initDB() throws SQLException {
        Statement statement = FlatsRunner.connection.createStatement();
        try {
            statement.execute("DROP TABLE IF EXISTS Flats");
            statement.execute("CREATE TABLE Flats (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, district TINYTEXT NOT NULL, " +
                    "address MEDIUMTEXT NOT NULL, area INT NOT NULL, rooms INT NOT NULL , price INT NOT NULL)");
            statement.execute("SET NAMES utf8");
            statement.execute("SET CHARACTER SET utf8");
        } finally {
            statement.close();
        }

        initRandomFlats();
    }

    private static void initRandomFlats() throws SQLException {
        Map<String, List<String>> streetContainer = initDistricts();
        final int FLAT_NUM = 100;
        Random random = new Random();

        String[] districts = {"Голосеевский", "Днепровский", "Деснянский", "Дарницкий", "Оболонский", "Печерский",
                "Подольский", "Святошинский", "Соломенский", "Шевченковский"};

        FlatsRunner.connection.setAutoCommit(false);
        try {
            try {
                PreparedStatement ps = FlatsRunner.connection.prepareStatement("INSERT INTO Flats (district, address, area, rooms, price) VALUES (?,?,?,?,?)");
                try {
                    for (int i = 0; i < FLAT_NUM; i++) {
                        String district = districts[random.nextInt(districts.length)];
                        String street = streetContainer.get(district).get(random.nextInt(streetContainer.get(district).size()));
                        ps.setString(1, district);
                        ps.setString(2, "ул. " + street + " д. " + (random.nextInt(20) + 1) + ", кв. " + (random.nextInt(150) + 1));
                        ps.setInt(3, (20 + random.nextInt(100)));
                        ps.setInt(4, random.nextInt(4) + 1);
                        ps.setInt(5, 150 + random.nextInt(800));
                        ps.executeUpdate();
                    }
                    FlatsRunner.connection.commit();
                } finally {
                    ps.close();
                }

            } catch (Exception ex) {
                FlatsRunner.connection.rollback();
            }
        } finally {
            FlatsRunner.connection.setAutoCommit(true);
        }
    }

    private static Map<String, List<String>> initDistricts() {
        Map<String, List<String>> container = new HashMap<>();
        container.put("Голосеевский", new LinkedList<>(Arrays.asList("Васильковская", "Голосеевская", "Вильямса", "Демеевская", "Касияна")));
        container.put("Днепровский", new LinkedList<>(Arrays.asList("Верховного совета", "Строителей", "Попудренка", "Малышка", "Красноткацкая")));
        container.put("Деснянский", new LinkedList<>(Arrays.asList("Петра Запорожца", "Маяковского", "Милославская", "Ватутина", "Перова")));
        container.put("Дарницкий", new LinkedList<>(Arrays.asList("Бориспольская", "Харьковская", "Ревуцкого", "Ахматовой", "Здолбуновская")));
        container.put("Оболонский", new LinkedList<>(Arrays.asList("Героев Сталинграда", "Богатырская", "Бандеры", "Маршала Тимошенко", "Приозерная")));
        container.put("Печерский", new LinkedList<>(Arrays.asList("Старонаводницкая", "Генерала Алмазова", "Леси Украинки", "Арсенальная", "Цитадельная")));
        container.put("Подольский", new LinkedList<>(Arrays.asList("Верхний Вал", "Нижний Вал", "Межигорская", "Спасская", "Константиновская")));
        container.put("Святошинский", new LinkedList<>(Arrays.asList("Федоры Пушиной", "Стуса", "Академика Вернадского", "Академика Доброхотова", "Святошинская")));
        container.put("Соломенский", new LinkedList<>(Arrays.asList("Ивана Лепсе", "Василенко", "Урицкого", "Васильченко", "Лебедева-Кумача")));
        container.put("Шевченковский", new LinkedList<>(Arrays.asList("Богдана Хмельницкого", "Гоголевская", "Тургеневская", "Дмитривская", "Гончара")));
        return container;
    }

    public static void listAllFlats() throws SQLException {
        PreparedStatement ps = FlatsRunner.connection.prepareStatement("SELECT * FROM Flats");
        getResults(ps);
    }

    private static void getResults(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                System.out.print(md.getColumnName(i) + "\t\t");
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t\t");
                }
                System.out.println();
            }
        }
    }

    static void editDB() throws SQLException {
        while (true) {
            System.out.println("1. Add new flat to DB");
            System.out.println("2. Remove flat from DB");
            System.out.println("3. Back to previous menu");
            System.out.print("-> ");

            String line = scanner.nextLine();
            switch (line) {
                case ("1"):
                    addFlat();
                    return;
                case ("2"):
                    removeFlat();
                    return;
                default:
                    return;
            }
        }
    }

    private static void addFlat() throws SQLException {
        System.out.println("Enter flat district:");
        String district = scanner.nextLine();
        System.out.println("Enter flat address:");
        String address = scanner.nextLine();
        System.out.println("Enter flat area (in square meters):");
        Integer area = scanner.nextInt();
        System.out.println("Enter number of rooms:");
        Integer rooms = scanner.nextInt();
        System.out.println("Enter the price of rent:");
        Integer price = scanner.nextInt();

        try (PreparedStatement ps = FlatsRunner.connection.prepareStatement("INSERT INTO Flats (district, address, area, rooms, price) VALUES (?,?,?,?,?)")) {
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setInt(3, area);
            ps.setInt(4, rooms);
            ps.setInt(5, price);
            ps.executeUpdate();
        }
    }

    private static void removeFlat() throws SQLException {
        while (true) {
            System.out.println("1. List all flats");
            System.out.println("2. Remove by ID");
            System.out.println("3. Back to previous menu");
            System.out.print("-> ");

            String line = scanner.nextLine();
            switch (line) {
                case ("1"):
                    listAllFlats();
                    break;
                case ("2"):
                    removeByID();
                    break;
                default:
                    return;
            }
        }
    }

    private static void removeByID() throws SQLException {
        System.out.println("Enter the ID of the flat you want to remove:");
        Integer id = scanner.nextInt();

        try (PreparedStatement ps = FlatsRunner.connection.prepareStatement("DELETE FROM Flats WHERE id = ?")) {
            ps.setInt(1, id);
            ps.execute();
        }
    }

    static void searchFor(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("Search by:");
            System.out.println("\t1. District");
            System.out.println("\t2. Area");
            System.out.println("\t3. Room number");
            System.out.println("\t4. Price");
            System.out.println("5. Return to previous menu");

            System.out.print("-> ");

            String line = scanner.nextLine();
            switch (line) {
                case ("1"):
                    listDistricts();
                    searchByDistrict();
                    return;
                case ("2"):
                    searchByArea();
                    return;
                case ("3"):
                    searchByRooms();
                    return;
                case ("4"):
                    searchByPrice();
                    return;
                default:
                    return;
            }
        }
    }

    static void searchByDistrict() throws SQLException {
        System.out.println("Please enter the district name:");
        String districtS = scanner.nextLine();

        System.out.println("FlatsRunner in district (" + districtS + "):");
        try (PreparedStatement ps = FlatsRunner.connection.prepareStatement("SELECT * FROM Flats WHERE district = ?")) {
            ps.setString(1, districtS);
            getResults(ps);
        }
    }

    static void listDistricts() throws SQLException {
        Set<String> districts = new TreeSet<>();
        System.out.println("Districts:");
        try (PreparedStatement ps = FlatsRunner.connection.prepareStatement("SELECT DISTINCT district FROM Flats")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    districts.add(rs.getString("district"));
                }
            }
        }
        for (String district : districts) {
            System.out.println("\t" + district);
        }
    }

    static void searchByArea() throws SQLException {
        System.out.print("Search area from: ");
        Integer start = scanner.nextInt();
        System.out.print("to: ");
        Integer end = scanner.nextInt();

        try (PreparedStatement ps = FlatsRunner.connection.prepareStatement("SELECT * FROM Flats WHERE area BETWEEN ? and ?")) {
            ps.setInt(1, start);
            ps.setInt(2, end);
            getResults(ps);
        }
    }

    static void searchByRooms() throws SQLException {
        System.out.println("Enter the number of rooms: ");
        Integer num = scanner.nextInt();
        try (PreparedStatement ps = FlatsRunner.connection.prepareStatement("SELECT * FROM Flats WHERE rooms = ?")) {
            ps.setInt(1, num);
            FlatsUtils.getResults(ps);
        }
    }

    static void searchByPrice() throws SQLException {
        System.out.println("Search for flat, that is:");
        System.out.println("\t1. Is more expensive than");
        System.out.println("\t2. Is cheaper than");
        System.out.println("\t3. Is in between");
        String line = scanner.nextLine();

        switch (line) {
            case ("1"):
                searchByGreaterPrice();
                break;
            case ("2"):
                searchByLowerPrice();
                break;
            case ("3"):
                searchByGapPrice();
                break;
            default:
                return;
        }
    }

    private static void searchByGreaterPrice() throws SQLException {
        System.out.print("Search flats that are more expensive than -> ");
        Integer res = scanner.nextInt();

        try (PreparedStatement ps = FlatsRunner.connection.prepareStatement("SELECT * FROM Flats WHERE price >= ?")) {
            ps.setInt(1, res);
            getResults(ps);
        }
    }

    private static void searchByLowerPrice() throws SQLException {
        System.out.print("Search flats that are cheaper than -> ");
        Integer res = scanner.nextInt();

        try (PreparedStatement ps = FlatsRunner.connection.prepareStatement("SELECT * FROM Flats WHERE price <= ?")) {
            ps.setInt(1, res);
            getResults(ps);
        }
    }

    private static void searchByGapPrice() throws SQLException {
        System.out.print("Search flats that are more expensive that -> ");
        Integer start = scanner.nextInt();
        System.out.print("and cheaper than -> ");
        Integer end = scanner.nextInt();

        try (PreparedStatement ps = FlatsRunner.connection.prepareStatement("SELECT * FROM Flats WHERE price BETWEEN ? AND ?")) {
            ps.setInt(1, start);
            ps.setInt(2, end);
            getResults(ps);
        }
    }
}