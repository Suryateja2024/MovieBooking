import java.sql.*;
import java.util.Scanner;

public class MovieBooking {
    static final String URL = "jdbc:mysql://localhost:3306/mydb";
    static final String USER = "root";
    static final String PASS = "heist@2024";

    static int[][][] seats = new int[3][5][10]; // 3 screens, 5 rows, 10 columns

    public static void main(String[] args) {
        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement stmt = conn.createStatement();
            Scanner sc = new Scanner(System.in);
        ) {
            System.out.println("Connected to MySQL!");

            // Create table if not exists
            String createTable = "CREATE TABLE IF NOT EXISTS Ticket (ScreenNumber INT, RowNumber INT, SeatNumber INT, Seat INT)";
            stmt.execute(createTable);

            // Insert initial data for all 3 screens
            for (int screen = 1; screen <= 3; screen++) {
                for (int row = 1; row <= 5; row++) {
                    for (int seat = 1; seat <= 10; seat++) {
                        String insertQuery = "INSERT INTO Ticket (ScreenNumber, RowNumber, SeatNumber, Seat) " +
                                "SELECT " + screen + ", " + row + ", " + seat + ", 1 " +
                                "WHERE NOT EXISTS (SELECT * FROM Ticket WHERE ScreenNumber = " + screen +
                                " AND RowNumber = " + row + " AND SeatNumber = " + seat + ")";
                        stmt.execute(insertQuery);
                    }
                }
            }

            boolean exit = false;

            while (!exit) {
                loadSeats(stmt);

                System.out.print("\nEnter screen number (1-3): ");
                int screen = sc.nextInt();

                if (screen < 1 || screen > 3) {
                    System.out.println("Invalid screen number!");
                    continue;
                }

                showSeats(screen);

                System.out.println("1. Reserve Seat");
                System.out.println("2. Cancel Seat");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();

                if (choice == 1) {
                    System.out.print("Enter row (1-5): ");
                    int row = sc.nextInt();
                    System.out.print("Enter seat number (1-10): ");
                    int col = sc.nextInt();

                    if (row < 1 || row > 5 || col < 1 || col > 10) {
                        System.out.println("Invalid seat!");
                        continue;
                    }

                    if (seats[screen - 1][row - 1][col - 1] == 0) {
                        System.out.println("Seat already booked!");
                        continue;
                    }

                    String update = "UPDATE Ticket SET Seat = 0 WHERE ScreenNumber = " + screen +
                            " AND RowNumber = " + row + " AND SeatNumber = " + col;
                    stmt.executeUpdate(update);
                    System.out.println("Seat booked successfully!");

                } else if (choice == 2) {
                    System.out.print("Enter row (1-5): ");
                    int row = sc.nextInt();
                    System.out.print("Enter seat number (1-10): ");
                    int col = sc.nextInt();

                    if (row < 1 || row > 5 || col < 1 || col > 10) {
                        System.out.println("Invalid seat!");
                        continue;
                    }

                    if (seats[screen - 1][row - 1][col - 1] == 1) {
                        System.out.println("Seat is already free!");
                        continue;
                    }

                    String update = "UPDATE Ticket SET Seat = 1 WHERE ScreenNumber = " + screen +
                            " AND RowNumber = " + row + " AND SeatNumber = " + col;
                    stmt.executeUpdate(update);
                    System.out.println("Seat cancellation successful!");

                } else if (choice == 3) {
                    exit = true;
                    System.out.println("Exiting. Thank you!");
                } else {
                    System.out.println("Invalid option.");
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
            int screen = rs.getInt("ScreenNumber") - 1;
            int row = rs.getInt("RowNumber") - 1;
            int col = rs.getInt("SeatNumber") - 1;
            int seat = rs.getInt("Seat");
            seats[screen][row][col] = seat;
        }
    }

    static void showSeats(int screenNum) {
        System.out.println("\n Seat Layout for Screen " + screenNum + ":");
        System.out.print("    ");
        for (int i = 1; i <= 10; i++) System.out.print(i + " ");
        System.out.println();

        for (int i = 0; i < 5; i++) {
            System.out.print("R" + (i + 1) + ": ");
            for (int j = 0; j < 10; j++) {
                System.out.print((seats[screenNum - 1][i][j] == 1 ? "-" : "X") + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
