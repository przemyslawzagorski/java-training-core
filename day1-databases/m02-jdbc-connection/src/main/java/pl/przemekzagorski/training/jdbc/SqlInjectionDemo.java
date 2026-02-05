package pl.przemekzagorski.training.jdbc;

import java.sql.*;

/**
 * Demonstracja SQL Injection i jak się przed nim bronić.
 */
public class SqlInjectionDemo {

    private static final String JDBC_URL = "jdbc:h2:mem:injection_demo";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) throws SQLException {
        System.out.println("🏴‍☠️ SQL Injection Demo");
        System.out.println("======================\n");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            setupDatabase(conn);

            System.out.println("1️⃣ NORMALNY LOGIN:");
            unsafeLogin(conn, "jack", "sparrow123");

            System.out.println("\n2️⃣ ATAK SQL INJECTION #1 (komentarz --):");
            unsafeLogin(conn, "admin' --", "cokolwiek");

            System.out.println("\n3️⃣ ATAK SQL INJECTION #2 (OR 1=1):");
            unsafeLogin(conn, "' OR 1=1 --", "cokolwiek");

            System.out.println("\n4️⃣ BEZPIECZNY LOGIN (PreparedStatement):");
            safeLogin(conn, "admin' --", "cokolwiek");
        }
    }

    private static void setupDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE users (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50),
                    password VARCHAR(50),
                    role VARCHAR(20)
                )
            """);
            stmt.execute("INSERT INTO users (username, password, role) VALUES ('jack', 'sparrow123', 'pirate')");
            stmt.execute("INSERT INTO users (username, password, role) VALUES ('admin', 'secret', 'admin')");
            System.out.println("📊 Baza przygotowana - 2 użytkowników\n");
        }
    }

    /**
     * ❌ NIEBEZPIECZNA METODA - podatna na SQL Injection!
     *
     * Konkatenacja stringów pozwala atakującemu wstrzyknąć własny kod SQL.
     *
     * Przykłady ataków:
     * - username = "admin' --" → komentuje resztę zapytania
     * - username = "' OR 1=1 --" → zwraca wszystkich użytkowników
     */
    private static void unsafeLogin(Connection conn, String username, String password) {
        // ❌ NIEBEZPIECZNE - konkatenacja stringów!
        String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
        System.out.println("   SQL: " + sql);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("   ✅ Zalogowano jako: " + rs.getString("username")
                    + " (rola: " + rs.getString("role") + ")");
                int count = 1;
                while (rs.next()) {
                    count++;
                    System.out.println("   ⚠️ Znaleziono też: " + rs.getString("username"));
                }
                if (count > 1) {
                    System.out.println("   🚨 ATAK! Zwrócono " + count + " użytkowników!");
                }
            } else {
                System.out.println("   ❌ Błędne dane logowania");
            }
        } catch (SQLException e) {
            System.out.println("   💥 Błąd SQL: " + e.getMessage());
        }
    }

    /**
     * ✅ BEZPIECZNA METODA - PreparedStatement
     */
    private static void safeLogin(Connection conn, String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        System.out.println("   SQL template: " + sql);
        System.out.println("   Parametry: [" + username + "], [" + password + "]");

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("   ✅ Zalogowano jako: " + rs.getString("username"));
                } else {
                    System.out.println("   ❌ Błędne dane logowania");
                    System.out.println("   🛡️ Atak SQL Injection NIE ZADZIAŁAŁ!");
                }
            }
        } catch (SQLException e) {
            System.out.println("   Błąd: " + e.getMessage());
        }
    }
}

