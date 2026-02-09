package pl.przemekzagorski.training.jdbc;

import java.math.BigDecimal;
import java.sql.*;

/**
 * 🏴‍☠️ Rozwiązania ćwiczeń JDBC
 *
 * Ten plik zawiera pełne, działające implementacje wszystkich ćwiczeń.
 * Używaj go jako referencji gdy utkniesz w JdbcExercises.java
 */
public class JdbcExercisesSolutions {

    private static final String JDBC_URL = "jdbc:h2:mem:exercises;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ JDBC Exercises - Solutions");
        System.out.println("================================\n");

        exercise1_BasicConnection();
        System.out.println("\n" + "=".repeat(50) + "\n");

        exercise2_SelectQuery();
        System.out.println("\n" + "=".repeat(50) + "\n");

        exercise3_InsertWithStatement();
        System.out.println("\n" + "=".repeat(50) + "\n");

        exercise4_InsertWithPreparedStatement();
        System.out.println("\n" + "=".repeat(50) + "\n");

        exercise5_ExceptionHandling();
        System.out.println("\n" + "=".repeat(50) + "\n");

        exercise6_Transactions();
    }

    /**
     * ✅ ROZWIĄZANIE 1: Podstawowe połączenie z bazą
     *
     * Kluczowe elementy:
     * - try-with-resources automatycznie zamyka Connection
     * - DriverManager.getConnection() tworzy połączenie
     * - SQLException musi być obsłużony
     */
    private static void exercise1_BasicConnection() {
        System.out.println("📝 Ćwiczenie 1: Podstawowe połączenie");

        // try-with-resources - Connection zostanie automatycznie zamknięty
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            System.out.println("   ✅ Połączono z bazą!");
            System.out.println("   📊 Database: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("   🔗 URL: " + conn.getMetaData().getURL());

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd połączenia: " + e.getMessage());
        }
        // Output: ✅ Połączono z bazą!
    }

    /**
     * ✅ ROZWIĄZANIE 2: Wykonanie zapytania SELECT
     *
     * Kluczowe elementy:
     * - Statement.execute() dla CREATE TABLE i INSERT
     * - Statement.executeQuery() dla SELECT (zwraca ResultSet)
     * - ResultSet.next() przesuwa kursor do następnego rekordu
     * - rs.getInt(), rs.getString(), rs.getBigDecimal() pobierają wartości kolumn
     */
    private static void exercise2_SelectQuery() {
        System.out.println("📝 Ćwiczenie 2: Zapytanie SELECT");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Tworzenie tabeli
            stmt.execute("CREATE TABLE pirates (id INT, name VARCHAR(100), bounty DECIMAL(10,2))");

            // Wstawianie danych
            stmt.execute("INSERT INTO pirates VALUES (1, 'Jack Sparrow', 10000)");
            stmt.execute("INSERT INTO pirates VALUES (2, 'Hector Barbossa', 8000)");
            stmt.execute("INSERT INTO pirates VALUES (3, 'Davy Jones', 15000)");

            System.out.println("   🏴‍☠️ Piraci w bazie:");

            // Wykonanie SELECT i iteracja po wynikach
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM pirates ORDER BY bounty DESC")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    BigDecimal bounty = rs.getBigDecimal("bounty");

                    System.out.printf("   • [%d] %s - nagroda: %.2f złota%n", id, name, bounty);
                }
            }

            // Czyszczenie
            stmt.execute("DROP TABLE pirates");

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd SQL: " + e.getMessage());
        }
        // Output:
        // • [3] Davy Jones - nagroda: 15000.00 złota
        // • [1] Jack Sparrow - nagroda: 10000.00 złota
        // • [2] Hector Barbossa - nagroda: 8000.00 złota
    }

    /**
     * ✅ ROZWIĄZANIE 3: Wstawienie danych używając Statement
     *
     * ⚠️ UWAGA: Ten sposób jest podatny na SQL Injection!
     * Używamy go tylko do demonstracji - w produkcji ZAWSZE używaj PreparedStatement.
     *
     * Kluczowe elementy:
     * - Statement z konkatenacją stringów (niebezpieczne!)
     * - executeUpdate() zwraca liczbę zmienionych rekordów
     */
    private static void exercise3_InsertWithStatement() {
        System.out.println("📝 Ćwiczenie 3: INSERT używając Statement (⚠️ niebezpieczne)");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Tworzenie tabeli
            stmt.execute("CREATE TABLE ships (id INT, name VARCHAR(100), cannons INT)");

            // ⚠️ NIEBEZPIECZNE - konkatenacja stringów
            String shipName = "Black Pearl";
            int cannons = 32;
            String sql = "INSERT INTO ships VALUES (1, '" + shipName + "', " + cannons + ")";

            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("   ✅ Wstawiono " + rowsAffected + " statek");

            // Weryfikacja
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM ships")) {
                while (rs.next()) {
                    System.out.printf("   ⚓ Statek: %s (%d armat)%n",
                        rs.getString("name"), rs.getInt("cannons"));
                }
            }

            stmt.execute("DROP TABLE ships");

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd SQL: " + e.getMessage());
        }
        // Output: ⚓ Statek: Black Pearl (32 armat)
    }

    /**
     * ✅ ROZWIĄZANIE 4: Wstawienie danych używając PreparedStatement
     *
     * ✅ BEZPIECZNE - parametry są automatycznie escapowane
     *
     * Kluczowe elementy:
     * - PreparedStatement z parametrami (?)
     * - setInt(), setString(), setBigDecimal() ustawiają wartości parametrów
     * - Parametry są automatycznie escapowane - brak SQL Injection!
     * - executeUpdate() zwraca liczbę zmienionych rekordów
     */
    private static void exercise4_InsertWithPreparedStatement() {
        System.out.println("📝 Ćwiczenie 4: INSERT używając PreparedStatement (✅ bezpieczne)");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Tworzenie tabeli
            stmt.execute("CREATE TABLE treasures (id INT, name VARCHAR(100), treasure_value DECIMAL(15,2))");

            // ✅ BEZPIECZNE - PreparedStatement z parametrami
            String sql = "INSERT INTO treasures VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Ustawianie parametrów (indeksy od 1!)
                pstmt.setInt(1, 1);                              // Pierwszy ? (id)
                pstmt.setString(2, "Aztec Gold");                // Drugi ? (name)
                pstmt.setBigDecimal(3, new BigDecimal("1000000")); // Trzeci ? (treasure_value)

                int rowsAffected = pstmt.executeUpdate();
                System.out.println("   ✅ Wstawiono " + rowsAffected + " skarb");
                System.out.println("   🛡️ Parametry zostały automatycznie zabezpieczone!");
            }

            // Weryfikacja
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM treasures")) {
                while (rs.next()) {
                    System.out.printf("   💎 Skarb: %s (wartość: %.2f złota)%n",
                        rs.getString("name"), rs.getBigDecimal("treasure_value"));
                }
            }

            stmt.execute("DROP TABLE treasures");

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd SQL: " + e.getMessage());
        }
        // Output: 💎 Skarb: Aztec Gold (wartość: 1000000.00 złota)
    }

    /**
     * ✅ ROZWIĄZANIE 5: Obsługa SQLException
     *
     * Kluczowe elementy:
     * - SQLException zawiera szczegółowe informacje o błędzie
     * - getMessage() - czytelny komunikat
     * - getErrorCode() - kod błędu specyficzny dla bazy danych
     * - getSQLState() - standardowy kod stanu SQL (5 znaków)
     */
    private static void exercise5_ExceptionHandling() {
        System.out.println("📝 Ćwiczenie 5: Obsługa SQLException");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Celowo błędne zapytanie - tabela nie istnieje
            stmt.executeQuery("SELECT * FROM nieistniejaca_tabela");

        } catch (SQLException e) {
            // Wyświetlamy szczegółowe informacje o błędzie
            System.out.println("   ⚠️ Złapano SQLException:");
            System.out.println("   📝 Komunikat: " + e.getMessage());
            System.out.println("   🔢 Kod błędu: " + e.getErrorCode());
            System.out.println("   🏷️ Stan SQL: " + e.getSQLState());
            System.out.println("   💡 To jest ZAMIERZONE - uczymy się obsługi błędów!");
        }
        // Output:
        // ⚠️ Złapano SQLException:
        // 📝 Komunikat: Table "NIEISTNIEJACA_TABELA" not found; SQL statement: ...
        // 🔢 Kod błędu: 42102
        // 🏷️ Stan SQL: 42S02
    }

    /**
     * ✅ ROZWIĄZANIE 6: Transakcje (commit/rollback)
     *
     * Kluczowe elementy:
     * - setAutoCommit(false) - wyłącza automatyczne zatwierdzanie
     * - commit() - zatwierdza wszystkie zmiany od początku transakcji
     * - rollback() - cofa wszystkie zmiany (w przypadku błędu)
     * - Transakcja = grupa operacji wykonywanych atomowo (wszystkie albo żadna)
     */
    private static void exercise6_Transactions() {
        System.out.println("📝 Ćwiczenie 6: Transakcje (commit/rollback)");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // WYŁĄCZAMY auto-commit - ręczne zarządzanie transakcją
            conn.setAutoCommit(false);

            try {
                // Tworzenie tabeli i wstawianie początkowych danych
                stmt.execute("CREATE TABLE accounts (id INT, name VARCHAR(100), balance DECIMAL(10,2))");
                stmt.execute("INSERT INTO accounts VALUES (1, 'Jack', 1000)");
                stmt.execute("INSERT INTO accounts VALUES (2, 'Barbossa', 500)");

                System.out.println("   💰 Salda PRZED transferem:");
                displayBalances(stmt);

                // TRANSAKCJA: Transfer 200 złota od Jacka do Barbossy
                System.out.println("\n   🔄 Wykonuję transfer 200 złota...");
                stmt.executeUpdate("UPDATE accounts SET balance = balance - 200 WHERE name = 'Jack'");
                stmt.executeUpdate("UPDATE accounts SET balance = balance + 200 WHERE name = 'Barbossa'");

                // ZATWIERDZAMY transakcję
                conn.commit();
                System.out.println("   ✅ Transakcja zatwierdzona (commit)");

                System.out.println("\n   💰 Salda PO transferze:");
                displayBalances(stmt);

                stmt.execute("DROP TABLE accounts");
                conn.commit();

            } catch (SQLException e) {
                // W przypadku błędu - COFAMY wszystkie zmiany
                System.err.println("   ❌ Błąd! Cofam transakcję (rollback)");
                conn.rollback();
                throw e;
            } finally {
                // Przywracamy auto-commit
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd SQL: " + e.getMessage());
        }
        // Output:
        // 💰 Salda PRZED transferem:
        //   • Jack: 1000.00 złota
        //   • Barbossa: 500.00 złota
        // 🔄 Wykonuję transfer 200 złota...
        // ✅ Transakcja zatwierdzona (commit)
        // 💰 Salda PO transferze:
        //   • Jack: 800.00 złota
        //   • Barbossa: 700.00 złota
    }

    /**
     * Pomocnicza metoda do wyświetlania sald kont
     */
    private static void displayBalances(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT name, balance FROM accounts ORDER BY name")) {
            while (rs.next()) {
                System.out.printf("   • %s: %.2f złota%n",
                    rs.getString("name"), rs.getBigDecimal("balance"));
            }
        }
    }
}
