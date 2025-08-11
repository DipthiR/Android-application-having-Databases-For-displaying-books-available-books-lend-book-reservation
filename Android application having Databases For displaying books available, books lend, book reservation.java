import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
public class LibrarySystem {
    private static final String DB_URL = "jdbc:sqlite:library.db";

    public static void main(String[] args) {
        try {
            // Create or connect to the SQLite database
            Connection connection = DriverManager.getConnection(DB_URL);

            // Create tables if they don't exist
            createTables(connection);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Library Management System");
                System.out.println("1. Display Available Books");
                System.out.println("2. Lend a Book");
                System.out.println("3. Reserve a Book");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline

                switch (choice) {
                    case 1:
                        displayAvailableBooks(connection);
                        break;
                    case 2:
                        lendBook(connection, scanner);
                        break;
                    case 3:
                        reserveBook(connection, scanner);
                        break;
                    case 4:
                        System.out.println("Goodbye!");
                        connection.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        // Create "books" table
        String createBooksTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "is_available BOOLEAN DEFAULT 1)";
        connection.createStatement().execute(createBooksTableSQL);

        // Create "loans" table
        String createLoansTableSQL = "CREATE TABLE IF NOT EXISTS loans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "book_id INTEGER NOT NULL," +
                "user_name TEXT NOT NULL," +
                "FOREIGN KEY (book_id) REFERENCES books (id))";
        connection.createStatement().execute(createLoansTableSQL);
    }

    private static void displayAvailableBooks(Connection connection) throws SQLException {
        String selectSQL = "SELECT id, title FROM books WHERE is_available = 1";
        ResultSet resultSet = connection.createStatement().executeQuery(selectSQL);

        System.out.println("Available Books:");
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String title = resultSet.getString("title");
            System.out.println(id + ". " + title);
        }
    }

    private static void lendBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter the ID of the book you want to lend: ");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        // Check if the book is available
        String checkAvailabilitySQL = "SELECT is_available FROM books WHERE id = ?";
        PreparedStatement availabilityStatement = connection.prepareStatement(checkAvailabilitySQL);
        availabilityStatement.setInt(1, bookId);
        ResultSet availabilityResult = availabilityStatement.executeQuery();

        if (availabilityResult.next()) {
            boolean isAvailable = availabilityResult.getBoolean("is_available");
            if (isAvailable) {
                // Book is available, lend it
                System.out.print("Enter your name: ");
                String userName = scanner.nextLine();

                // Update book availability
                String updateAvailabilitySQL = "UPDATE books SET is_available = 0 WHERE id = ?";
                PreparedStatement updateAvailabilityStatement = connection.prepareStatement(updateAvailabilitySQL);
                updateAvailabilityStatement.setInt(1, bookId);
                updateAvailabilityStatement.executeUpdate();

                // Record the loan
                String insertLoanSQL = "INSERT INTO loans (book_id, user_name) VALUES (?, ?)";
                PreparedStatement insertLoanStatement = connection.prepareStatement(insertLoanSQL);
                insertLoanStatement.setInt(1, bookId);
                insertLoanStatement.setString(2, userName);
                insertLoanStatement.executeUpdate();

                System.out.println("Book lent successfully!");
            } else {
                System.out.println("Sorry, this book is already lent.");
            }
        } else {
            System.out.println("Invalid book ID.");
        }
    }
    private static void reserveBook(Connection connection, Scanner scanner) throws SQLException {
        // Similar logic to lending a book can be implemented here
        // You can check availability and reserve the book if it's available
        // This is left as an exercise for further development
        System.out.println("Reservation functionality not implemented in this example.");
    }
}
