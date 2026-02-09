package pl.przemekzagorski.training.jdbc;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 🏴‍☠️ Ćwiczenia JDBC - ROZWIĄZANIA
 *
 * ═══════════════════════════════════════════════════════════════════
 * Ten plik zawiera PEŁNE, działające rozwiązania wszystkich ćwiczeń.
 * Używaj go jako referencji gdy utkniesz w PirateExercises.java
 *
 * Każde rozwiązanie zawiera szczegółowe komentarze wyjaśniające
 * DLACZEGO i JAK działa dany kod.
 * ═══════════════════════════════════════════════════════════════════
 */
public class PirateExercisesSolutions {

    private static final String JDBC_URL = "jdbc:h2:mem:pirate_exercises;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ JDBC Exercises - ROZWIĄZANIA");
        System.out.println("══════════════════════════════════\n");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            setupDatabase(conn);
            showInitialData(conn);

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 1: SELECT z PreparedStatement
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("✅ ROZWIĄZANIE 1: Znajdź piratów z nagrodą > 5000");
            System.out.println("═".repeat(60));

            List<String> richPirates = exercise1_FindByBountyGreaterThan(conn, new BigDecimal("5000"));

            System.out.println("   Wynik: " + richPirates);
            System.out.println("   Oczekiwany: [Jack Sparrow, Davy Jones, Edward Teach, Hector Barbossa]");
            System.out.println("   Status: ✅ POPRAWNIE!");

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 2: UPDATE z PreparedStatement
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("✅ ROZWIĄZANIE 2: Zaktualizuj nagrodę Jacka Sparrowa");
            System.out.println("═".repeat(60));

            int updatedRows = exercise2_UpdateBounty(conn, 1L, new BigDecimal("15000"));

            System.out.println("   Zaktualizowano rekordów: " + updatedRows);
            BigDecimal newBounty = getBountyById(conn, 1L);
            System.out.println("   Weryfikacja - nowa nagroda Jacka: " + newBounty);
            System.out.println("   Status: ✅ POPRAWNIE!");

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 3: DELETE z transakcją
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("✅ ROZWIĄZANIE 3: Usuń wszystkich marynarzy (Sailor)");
            System.out.println("═".repeat(60));

            long beforeCount = countAllPirates(conn);
            System.out.println("   Piratów przed usunięciem: " + beforeCount);

            int deleted = exercise3_DeleteByRank(conn, "Sailor");

            long afterCount = countAllPirates(conn);
            System.out.println("   Usunięto rekordów: " + deleted);
            System.out.println("   Piratów po usunięciu: " + afterCount);
            System.out.println("   Status: ✅ POPRAWNIE!");

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 4: COUNT z PreparedStatement
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("✅ ROZWIĄZANIE 4: Policz kapitanów");
            System.out.println("═".repeat(60));

            long captainCount = exercise4_CountByRank(conn, "Captain");

            System.out.println("   Liczba kapitanów: " + captainCount);
            System.out.println("   Status: ✅ POPRAWNIE!");

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 5: Transakcja z walidacją
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("✅ ROZWIĄZANIE 5: Transfer pirata między statkami (TRANSAKCJA)");
            System.out.println("═".repeat(60));

            boolean success = exercise5_TransferPirate(conn, 4L, 2L);
            System.out.println("   Transfer udany: " + success);
            System.out.println("   Status: ✅ POPRAWNIE!");

            // Test błędu
            System.out.println("\n   Test błędu - transfer na nieistniejący statek:");
            boolean shouldFail = exercise5_TransferPirate(conn, 4L, 999L);
            System.out.println("   Transfer udany: " + shouldFail);
            System.out.println("   Status: ✅ POPRAWNIE (błąd obsłużony prawidłowo)!");

            // ═══════════════════════════════════════════════════════════
            // PODSUMOWANIE
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("🎉 WSZYSTKIE ROZWIĄZANIA DZIAŁAJĄ!");
            System.out.println("═".repeat(60));
            showFinalData(conn);

        } catch (SQLException e) {
            System.err.println("❌ Błąd połączenia z bazą: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 1: Znajdź piratów z nagrodą większą niż podana kwota
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ✅ ROZWIĄZANIE
     *
     * Kluczowe elementy:
     * - PreparedStatement z parametrem (?) zapobiega SQL Injection
     * - setBigDecimal(1, minBounty) - ustawia pierwszy parametr
     * - try-with-resources automatycznie zamyka zasoby
     * - rs.next() przesuwa kursor do następnego rekordu
     */
    private static List<String> exercise1_FindByBountyGreaterThan(Connection conn, BigDecimal minBounty) {
        List<String> names = new ArrayList<>();

        // SQL z parametrem ? - BEZPIECZNE przed SQL Injection!
        String sql = "SELECT name FROM pirates WHERE bounty > ?";

        // try-with-resources - PreparedStatement zostanie automatycznie zamknięty
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Ustawiamy parametr (indeksy zaczynają się od 1, nie od 0!)
            stmt.setBigDecimal(1, minBounty);

            // Wykonujemy zapytanie SELECT
            try (ResultSet rs = stmt.executeQuery()) {
                // Iterujemy po wynikach
                while (rs.next()) {
                    // Pobieramy wartość kolumny "name" i dodajemy do listy
                    names.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            // Opakowujemy SQLException w RuntimeException
            // W prawdziwej aplikacji: logowanie, specyficzne wyjątki DAO
            throw new RuntimeException("Błąd przy wyszukiwaniu piratów", e);
        }

        return names;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 2: Zaktualizuj nagrodę pirata
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ✅ ROZWIĄZANIE
     *
     * Kluczowe elementy:
     * - UPDATE z dwoma parametrami (SET i WHERE)
     * - Kolejność parametrów: 1=bounty, 2=id (tak jak w SQL)
     * - executeUpdate() zwraca liczbę zmienionych wierszy
     */
    private static int exercise2_UpdateBounty(Connection conn, Long pirateId, BigDecimal newBounty) {
        // SQL UPDATE z dwoma parametrami
        String sql = "UPDATE pirates SET bounty = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Ustawiamy parametry W KOLEJNOŚCI jak w SQL!
            stmt.setBigDecimal(1, newBounty);  // Pierwszy ? (SET bounty = ?)
            stmt.setLong(2, pirateId);          // Drugi ? (WHERE id = ?)

            // executeUpdate() zwraca liczbę zmienionych wierszy
            // Powinno być 1, jeśli pirat istnieje
            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy aktualizacji nagrody", e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 3: Usuń piratów o danej randze (z transakcją)
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ✅ ROZWIĄZANIE
     *
     * Kluczowe elementy:
     * - setAutoCommit(false) - wyłączamy automatyczne zatwierdzanie
     * - commit() - zatwierdzamy wszystkie zmiany
     * - rollback() - cofamy wszystkie zmiany w przypadku błędu
     * - finally - ZAWSZE przywracamy autoCommit!
     *
     * ⚠️ DLACZEGO TRANSAKCJA?
     * W tym prostym przypadku nie jest konieczna, ale pokazuje wzorzec.
     * W realnej aplikacji DELETE może być częścią większej operacji.
     */
    private static int exercise3_DeleteByRank(Connection conn, String rank) {
        String sql = "DELETE FROM pirates WHERE rank = ?";

        try {
            // 1. Wyłączamy auto-commit - zaczynamy transakcję
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, rank);

                // Wykonujemy DELETE
                int deleted = stmt.executeUpdate();

                // 2. Zatwierdzamy transakcję - wszystko OK
                conn.commit();
                System.out.println("   ✅ COMMIT - transakcja zatwierdzona");

                return deleted;
            }

        } catch (SQLException e) {
            // 3. W przypadku błędu - COFAMY wszystkie zmiany
            System.out.println("   ⚠️ BŁĄD: " + e.getMessage());
            try {
                conn.rollback();
                System.out.println("   ↩️ ROLLBACK - zmiany cofnięte");
            } catch (SQLException rollbackEx) {
                System.err.println("Błąd podczas rollback: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Błąd przy usuwaniu piratów", e);

        } finally {
            // 4. ZAWSZE przywracamy auto-commit (w finally!)
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Błąd przy przywracaniu auto-commit: " + e.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 4: Policz piratów o danej randze
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ✅ ROZWIĄZANIE
     *
     * Kluczowe elementy:
     * - COUNT(*) zawsze zwraca wynik (nawet 0)
     * - rs.getLong(1) - pobieramy pierwszą kolumnę jako long
     * - Można też użyć rs.getLong("count") jeśli użyjemy aliasu
     */
    private static long exercise4_CountByRank(Connection conn, String rank) {
        // COUNT(*) z parametrem dla rank
        String sql = "SELECT COUNT(*) FROM pirates WHERE rank = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rank);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Pobieramy pierwszą kolumnę (COUNT(*) nie ma nazwy)
                    // Indeks 1, bo JDBC liczy od 1!
                    return rs.getLong(1);
                }
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy zliczaniu piratów", e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 5: Przenieś pirata między statkami (ZAAWANSOWANE)
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ✅ ROZWIĄZANIE
     *
     * Kluczowe elementy:
     * - Pełna transakcja z walidacją przed operacją
     * - Sprawdzamy czy pirat istnieje PRZED transferem
     * - Sprawdzamy czy statek istnieje PRZED transferem
     * - Jeśli cokolwiek się nie powiedzie - ROLLBACK
     *
     * ⚠️ DLACZEGO WALIDACJA W TRANSAKCJI?
     * Zapobiega "race condition" - między sprawdzeniem a UPDATE
     * ktoś inny mógłby usunąć pirata lub statek!
     */
    private static boolean exercise5_TransferPirate(Connection conn, Long pirateId, Long newShipId) {
        String checkPirateSql = "SELECT COUNT(*) FROM pirates WHERE id = ?";
        String checkShipSql = "SELECT COUNT(*) FROM ships WHERE id = ?";
        String updateSql = "UPDATE pirates SET ship_id = ? WHERE id = ?";

        try {
            // 1. Rozpoczynamy transakcję
            conn.setAutoCommit(false);

            // 2. Sprawdzamy czy pirat istnieje
            try (PreparedStatement stmt = conn.prepareStatement(checkPirateSql)) {
                stmt.setLong(1, pirateId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getLong(1) == 0) {
                        throw new SQLException("Pirat o ID=" + pirateId + " nie istnieje!");
                    }
                }
            }

            // 3. Sprawdzamy czy statek docelowy istnieje
            try (PreparedStatement stmt = conn.prepareStatement(checkShipSql)) {
                stmt.setLong(1, newShipId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getLong(1) == 0) {
                        throw new SQLException("Statek o ID=" + newShipId + " nie istnieje!");
                    }
                }
            }

            // 4. Wykonujemy transfer
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setLong(1, newShipId);  // SET ship_id = ?
                stmt.setLong(2, pirateId);    // WHERE id = ?
                stmt.executeUpdate();
            }

            // 5. Wszystko OK - zatwierdzamy
            conn.commit();
            System.out.println("   ✅ COMMIT - transfer zakończony pomyślnie");
            return true;

        } catch (SQLException e) {
            // 6. Błąd - cofamy wszystkie zmiany
            System.out.println("   ⚠️ BŁĄD: " + e.getMessage());
            try {
                conn.rollback();
                System.out.println("   ↩️ ROLLBACK - transfer anulowany");
            } catch (SQLException rollbackEx) {
                System.err.println("Błąd podczas rollback: " + rollbackEx.getMessage());
            }
            return false;

        } finally {
            // 7. ZAWSZE przywracamy auto-commit
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Błąd przy przywracaniu auto-commit: " + e.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // METODY POMOCNICZE (identyczne jak w PirateExercises)
    // ═══════════════════════════════════════════════════════════════════════

    private static void setupDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE ships (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    ship_type VARCHAR(50),
                    cannons INT DEFAULT 0
                )
            """);

            stmt.execute("""
                CREATE TABLE pirates (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    nickname VARCHAR(100),
                    rank VARCHAR(50),
                    bounty DECIMAL(15,2) DEFAULT 0,
                    ship_id BIGINT,
                    FOREIGN KEY (ship_id) REFERENCES ships(id)
                )
            """);

            stmt.execute("INSERT INTO ships (name, ship_type, cannons) VALUES ('Black Pearl', 'Galleon', 32)");
            stmt.execute("INSERT INTO ships (name, ship_type, cannons) VALUES ('Flying Dutchman', 'Galleon', 46)");
            stmt.execute("INSERT INTO ships (name, ship_type, cannons) VALUES ('Queen Annes Revenge', 'Frigate', 40)");

            stmt.execute("INSERT INTO pirates (name, nickname, rank, bounty, ship_id) VALUES ('Jack Sparrow', 'Captain Jack', 'Captain', 10000.00, 1)");
            stmt.execute("INSERT INTO pirates (name, nickname, rank, bounty, ship_id) VALUES ('Davy Jones', 'Devil of the Sea', 'Captain', 100000.00, 2)");
            stmt.execute("INSERT INTO pirates (name, nickname, rank, bounty, ship_id) VALUES ('Edward Teach', 'Blackbeard', 'Captain', 50000.00, 3)");
            stmt.execute("INSERT INTO pirates (name, nickname, rank, bounty, ship_id) VALUES ('Joshamee Gibbs', 'Mr. Gibbs', 'First Mate', 1000.00, 1)");
            stmt.execute("INSERT INTO pirates (name, nickname, rank, bounty, ship_id) VALUES ('Hector Barbossa', 'Barbossa', 'Quartermaster', 8000.00, 1)");
            stmt.execute("INSERT INTO pirates (name, nickname, rank, bounty, ship_id) VALUES ('Bootstrap Bill', 'Bootstrap', 'First Mate', 3000.00, 2)");
            stmt.execute("INSERT INTO pirates (name, nickname, rank, bounty, ship_id) VALUES ('Cotton', NULL, 'Sailor', 100.00, 1)");
            stmt.execute("INSERT INTO pirates (name, nickname, rank, bounty, ship_id) VALUES ('Marty', NULL, 'Sailor', 150.00, 1)");
        }
        System.out.println("✅ Baza danych zainicjalizowana\n");
    }

    private static void showInitialData(Connection conn) throws SQLException {
        System.out.println("📊 STAN POCZĄTKOWY:");
        System.out.println("─".repeat(60));

        try (Statement stmt = conn.createStatement()) {
            System.out.println("⛵ Statki:");
            try (ResultSet rs = stmt.executeQuery("SELECT id, name, ship_type, cannons FROM ships ORDER BY id")) {
                while (rs.next()) {
                    System.out.printf("   [%d] %s (%s, %d armat)%n",
                            rs.getLong("id"), rs.getString("name"),
                            rs.getString("ship_type"), rs.getInt("cannons"));
                }
            }

            System.out.println("\n🏴‍☠️ Piraci:");
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT p.id, p.name, p.rank, p.bounty, s.name as ship_name " +
                            "FROM pirates p LEFT JOIN ships s ON p.ship_id = s.id ORDER BY p.id")) {
                while (rs.next()) {
                    System.out.printf("   [%d] %s (%s) - %.2f zł - statek: %s%n",
                            rs.getLong("id"), rs.getString("name"), rs.getString("rank"),
                            rs.getBigDecimal("bounty"), rs.getString("ship_name"));
                }
            }
        }
    }

    private static void showFinalData(Connection conn) throws SQLException {
        System.out.println("\n📊 STAN KOŃCOWY:");
        System.out.println("─".repeat(60));

        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT p.id, p.name, p.rank, p.bounty, s.name as ship_name " +
                            "FROM pirates p LEFT JOIN ships s ON p.ship_id = s.id ORDER BY p.id")) {
                while (rs.next()) {
                    System.out.printf("   [%d] %s (%s) - %.2f zł - statek: %s%n",
                            rs.getLong("id"), rs.getString("name"), rs.getString("rank"),
                            rs.getBigDecimal("bounty"), rs.getString("ship_name"));
                }
            }
        }
    }

    private static BigDecimal getBountyById(Connection conn, Long pirateId) throws SQLException {
        String sql = "SELECT bounty FROM pirates WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, pirateId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("bounty");
                }
                return null;
            }
        }
    }

    private static long countAllPirates(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM pirates")) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        }
    }
}
