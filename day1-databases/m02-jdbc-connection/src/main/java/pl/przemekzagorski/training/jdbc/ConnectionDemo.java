package pl.przemekzagorski.training.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Demonstracja podstawowego połączenia JDBC z bazą H2.
 *
 * Kluczowe elementy:
 * - DriverManager - zarządza sterownikami i tworzy połączenia
 * - Connection - reprezentuje połączenie z bazą
 * - Statement - wykonuje zapytania SQL
 * - ResultSet - przechowuje wyniki zapytania
 */
public class ConnectionDemo {

    private static final String JDBC_URL = "jdbc:h2:mem:pirates;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ JDBC Connection Demo");
        System.out.println("========================\n");

        demonstrateOldStyle();
        System.out.println("\n" + "=".repeat(50) + "\n");
        demonstrateTryWithResources();
    }

    /**
     * Stary styl - pokazujemy dla zrozumienia, dlaczego try-with-resources jest lepsze.
     */
    private static void demonstrateOldStyle() {
        System.out.println("❌ Stary styl (nie używaj!):");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("   Połączono z bazą!");

            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS test_pirates (id INT, name VARCHAR(100))");
            stmt.execute("INSERT INTO test_pirates VALUES (1, 'Jack Sparrow')");

            rs = stmt.executeQuery("SELECT * FROM test_pirates");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.println("   Znaleziono: " + id + " - " + name);
            }

        } catch (SQLException e) {
            System.err.println("   Błąd SQL: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("   Błąd przy zamykaniu: " + e.getMessage());
            }
        }
    }

    /**
     * Nowy styl - try-with-resources (Java 7+). Zasoby są automatycznie zamykane!
     */
    private static void demonstrateTryWithResources() {
        System.out.println("✅ Try-with-resources (zalecane!):");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            System.out.println("   Połączono z bazą!");

            stmt.execute("CREATE TABLE IF NOT EXISTS pirates (id INT, name VARCHAR(100))");
            stmt.execute("INSERT INTO pirates VALUES (1, 'Jack Sparrow')");
            stmt.execute("INSERT INTO pirates VALUES (2, 'Hector Barbossa')");

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM pirates")) {
                System.out.println("   Piraci w bazie:");
                while (rs.next()) {
                    System.out.printf("   • [%d] %s%n", rs.getInt("id"), rs.getString("name"));
                }
            }

            stmt.execute("DROP TABLE pirates");
            stmt.execute("DROP TABLE IF EXISTS test_pirates");

        } catch (SQLException e) {
            System.err.println("   Błąd SQL: " + e.getMessage());
        }

        System.out.println("   Zasoby zamknięte automatycznie!");
    }
}

