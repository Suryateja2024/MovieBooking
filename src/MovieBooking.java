import java.sql.*;
import java.util.Scanner;

public class MovieBooking {
    static final String URL = "jdbc:mysql://localhost:3306/mydb";
    static final String USER = "root";
    static final String PASS = "heist@2024"; // Replace with your actual password

    static int[][] seats = new int[5][10];

    public static void main(String[] args) {
        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement stmt = conn.createStatement();
            Scanner sc = new Scanner(System.in);
        ) {
            System.out.println("✅ Connected to MySQL!");

            // 1. Create table if not exists
            String createTable = "CREATE TABLE IF NOT EXISTS Ticket (RowNumber INT, SeatNumber INT, Seat INT)";
            stmt.execute(createTable);

            // 2. Insert initial data if not present
            for (int row = 1; row <= 5; row++) {
                for (int seat = 1; seat <= 10; seat++) {
                    String insertQuery = "INSERT INTO Ticket (RowNumber, SeatNumber, Seat) " +
                            "SELECT " + row + ", " + seat + ", 1 " +
                            "WHERE NOT EXISTS (SELECT * FROM Ticket WHERE RowNumber = " + row +
                            " AND SeatNumber = " + seat + ")";
                    stmt.execute(insertQuery);
                }
            }

            boolean exit = false;

            while (!exit) {
                loadSeats(stmt);
                showSeats();

                System.out.println("1. Reserve Seat");
                System.out.println("2. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();

                if (choice == 1) {
                    System.out.print("Enter row (1-5): ");
                    int row = sc.nextInt();
                    System.out.print("Enter seat number (1-10): ");
                    int col = sc.nextInt();

                    if (row < 1 || row > 5 || col < 1 || col > 10) {
                        System.out.println("❌ Invalid seat!");
                        continue;
                    }

                    if (seats[row - 1][col - 1] == 0) {
                        System.out.println("❌ Seat already booked!");
                        continue;
                    }

                    String update = "UPDATE Ticket SET Seat = 0 WHERE RowNumber = " + row + " AND SeatNumber = " + col;
                    stmt.executeUpdate(update);
                    System.out.println("✅ Seat booked successfully!");
                } else if (choice == 2) {
                    exit = true;
                    System.out.println("👋 Exiting. Thank you!");
                } else {
                    System.out.println("❌ Invalid option.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadSeats(Statement stmt) throws SQLException {
        String query = "SELECT * FROM Ticket";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            int row = rs.getInt("RowNumber") - 1;
            int col = rs.getInt("SeatNumber") - 1;
            int seat = rs.getInt("Seat");
            seats[row][col] = seat;
        }
    }

    static void showSeats() {
        System.out.println("\n🎟️ Seat Layout:");
        System.out.print("    ");
        for (int i = 1; i <= 10; i++) System.out.print(i + " ");
        System.out.println();

        for (int i = 0; i < 5; i++) {
            System.out.print("R" + (i + 1) + ": ");
            for (int j = 0; j < 10; j++) {
                System.out.print((seats[i][j] == 1 ? "-" : "X") + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}





