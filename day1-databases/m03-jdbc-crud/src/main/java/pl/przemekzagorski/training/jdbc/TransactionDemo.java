package pl.przemekzagorski.training.jdbc;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Demonstracja transakcji w JDBC.
 * Transakcja = zestaw operacji wykonywanych jako jedna całość.
 */
public class TransactionDemo {

    private static final String JDBC_URL = "jdbc:h2:mem:transaction_demo";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) throws SQLException {
        System.out.println("🏴‍☠️ Transaction Demo - Transfer skarbu");
        System.out.println("========================================\n");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            setupDatabase(conn);
            showState(conn, "STAN POCZĄTKOWY");

            System.out.println("\nSCENARIUSZ 1: Udany transfer 5000 złota");
            boolean success = transferTreasure(conn, 1, 1, new BigDecimal("5000"));
            System.out.println(success ? "✅ Transfer udany!" : "❌ Transfer nieudany!");
            showState(conn, "PO TRANSFERZE");

            System.out.println("\nSCENARIUSZ 2: Transfer 999999 złota (za dużo!)");
            success = transferTreasure(conn, 1, 1, new BigDecimal("999999"));
            System.out.println(success ? "✅ Transfer udany!" : "❌ Transfer nieudany!");
            showState(conn, "PO NIEUDANYM TRANSFERZE");
        }
    }

    private static boolean transferTreasure(Connection conn, long islandId, long shipId,
                                            BigDecimal amount) throws SQLException {
        conn.setAutoCommit(false);
        try {
            BigDecimal islandTreasure = getIslandTreasure(conn, islandId);
            if (islandTreasure.compareTo(amount) < 0) {
                throw new SQLException("Za mało skarbu na wyspie!");
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE islands SET treasure_value = treasure_value - ? WHERE id = ?")) {
                stmt.setBigDecimal(1, amount);
                stmt.setLong(2, islandId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE ships SET gold = gold + ? WHERE id = ?")) {
                stmt.setBigDecimal(1, amount);
                stmt.setLong(2, shipId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("   ⚠️ BŁĄD: " + e.getMessage());
            conn.rollback();
            System.out.println("   ↩️ ROLLBACK!");
            return false;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static BigDecimal getIslandTreasure(Connection conn, long islandId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT treasure_value FROM islands WHERE id = ?")) {
            stmt.setLong(1, islandId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("treasure_value");
                throw new SQLException("Wyspa nie istnieje!");
            }
        }
    }

    private static void showState(Connection conn, String label) throws SQLException {
        System.out.println("\n📊 " + label + ":");
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT name, treasure_value FROM islands")) {
                while (rs.next()) {
                    System.out.printf("   🏝️ %s: %s złota%n", rs.getString("name"), rs.getBigDecimal("treasure_value"));
                }
            }
            try (ResultSet rs = stmt.executeQuery("SELECT name, gold FROM ships")) {
                while (rs.next()) {
                    System.out.printf("   ⛵ %s: %s złota%n", rs.getString("name"), rs.getBigDecimal("gold"));
                }
            }
        }
    }

    private static void setupDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE islands (id BIGINT PRIMARY KEY, name VARCHAR(100), treasure_value DECIMAL(15,2))");
            stmt.execute("CREATE TABLE ships (id BIGINT PRIMARY KEY, name VARCHAR(100), gold DECIMAL(15,2))");
            stmt.execute("INSERT INTO islands VALUES (1, 'Isla de Muerta', 50000.00)");
            stmt.execute("INSERT INTO ships VALUES (1, 'Black Pearl', 0.00)");
        }
    }
}

