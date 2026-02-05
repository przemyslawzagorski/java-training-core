package pl.przemekzagorski.training.jdbc;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 🏴‍☠️ Ćwiczenia JDBC - Wzorzec DAO i operacje CRUD
 *
 * ═══════════════════════════════════════════════════════════════════
 * INSTRUKCJA DLA KURSANTA:
 * ═══════════════════════════════════════════════════════════════════
 *
 * 1. Każda metoda exercise_X ma komentarz z TODO opisującym co zrobić
 * 2. Uzupełnij kod w miejscach oznaczonych "// TODO:"
 * 3. Po uzupełnieniu uruchom metodę main() - zobaczysz wyniki
 * 4. Jeśli utkniesz - sprawdź PirateExercisesSolutions.java
 *
 * WAŻNE KONCEPCJE:
 * - PreparedStatement - bezpieczne zapytania z parametrami (?)
 * - try-with-resources - automatyczne zamykanie zasobów
 * - Transakcje - setAutoCommit(false), commit(), rollback()
 *
 * ═══════════════════════════════════════════════════════════════════
 */
public class PirateExercises {

    // Wspólna konfiguracja bazy danych
    private static final String JDBC_URL = "jdbc:h2:mem:pirate_exercises;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ JDBC Exercises - Ćwiczenia DAO");
        System.out.println("════════════════════════════════════\n");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            setupDatabase(conn);
            showInitialData(conn);

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 1: SELECT z PreparedStatement
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 1: Znajdź piratów z nagrodą > 5000");
            System.out.println("═".repeat(60));

            List<String> richPirates = exercise1_FindByBountyGreaterThan(conn, new BigDecimal("5000"));

            System.out.println("   Twój wynik: " + richPirates);
            System.out.println("   Oczekiwany: [Jack Sparrow, Davy Jones, Edward Teach, Hector Barbossa]");
            System.out.println("   Status: " + (richPirates.size() == 4 ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 2: UPDATE z PreparedStatement
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 2: Zaktualizuj nagrodę Jacka Sparrowa");
            System.out.println("═".repeat(60));

            int updatedRows = exercise2_UpdateBounty(conn, 1L, new BigDecimal("15000"));

            System.out.println("   Zaktualizowano rekordów: " + updatedRows);
            System.out.println("   Oczekiwany: 1");
            System.out.println("   Status: " + (updatedRows == 1 ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

            // Weryfikacja
            BigDecimal newBounty = getBountyById(conn, 1L);
            System.out.println("   Weryfikacja - nowa nagroda Jacka: " + newBounty);

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 3: DELETE z transakcją
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 3: Usuń wszystkich marynarzy (Sailor)");
            System.out.println("═".repeat(60));

            long beforeCount = countAllPirates(conn);
            System.out.println("   Piratów przed usunięciem: " + beforeCount);

            int deleted = exercise3_DeleteByRank(conn, "Sailor");

            long afterCount = countAllPirates(conn);
            System.out.println("   Usunięto rekordów: " + deleted);
            System.out.println("   Piratów po usunięciu: " + afterCount);
            System.out.println("   Status: " + (deleted == 2 ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 4: COUNT z PreparedStatement
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 4: Policz kapitanów");
            System.out.println("═".repeat(60));

            long captainCount = exercise4_CountByRank(conn, "Captain");

            System.out.println("   Liczba kapitanów: " + captainCount);
            System.out.println("   Oczekiwany: 3");
            System.out.println("   Status: " + (captainCount == 3 ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 5: Transakcja z walidacją
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 5: Transfer pirata między statkami (TRANSAKCJA)");
            System.out.println("═".repeat(60));

            // Przenosimy Gibbsa (id=4) na Flying Dutchman (ship_id=2)
            boolean success = exercise5_TransferPirate(conn, 4L, 2L);

            System.out.println("   Transfer udany: " + success);
            System.out.println("   Status: " + (success ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

            // Test błędu - nieistniejący statek
            System.out.println("\n   Test błędu - transfer na nieistniejący statek:");
            boolean shouldFail = exercise5_TransferPirate(conn, 4L, 999L);
            System.out.println("   Transfer udany: " + shouldFail);
            System.out.println("   Status: " + (!shouldFail ? "✅ POPRAWNIE (powinien się nie udać)!" : "❌ Sprawdź obsługę błędów"));

            // ═══════════════════════════════════════════════════════════
            // PODSUMOWANIE
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("🎉 PODSUMOWANIE");
            System.out.println("═".repeat(60));
            showFinalData(conn);

        } catch (SQLException e) {
            System.err.println("❌ Błąd połączenia z bazą: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 1: Znajdź piratów z nagrodą większą niż podana kwota
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Znajdź wszystkich piratów z nagrodą (bounty) większą niż podana kwota.
     *
     * 🎯 POZIOM: ŁATWY (gotowy kod - uruchom i obserwuj!)
     *
     * 🔍 OBSERWUJ:
     * - PreparedStatement chroni przed SQL Injection (parametr ?)
     * - try-with-resources automatycznie zamyka zasoby
     * - Indeksy parametrów zaczynają się od 1, nie od 0!
     * - ResultSet to kursor - next() przesuwa do następnego rekordu
     *
     * ❓ PYTANIE: Co się stanie jeśli użyjemy Statement zamiast PreparedStatement?
     * 💡 ODPOWIEDŹ: Ryzyko SQL Injection! Nigdy nie konkatenuj SQL z danymi użytkownika!
     *
     * 🆘 Jeśli chcesz zobaczyć więcej przykładów, sprawdź PirateExercisesSolutions.java
     *
     * @param conn połączenie do bazy
     * @param minBounty minimalna nagroda
     * @return lista imion piratów
     */
    private static List<String> exercise1_FindByBountyGreaterThan(Connection conn, BigDecimal minBounty) {
        List<String> names = new ArrayList<>();

        // 🔍 OBSERWUJ: SQL z parametrem ? - BEZPIECZNE przed SQL Injection!
        String sql = "SELECT name FROM pirates WHERE bounty > ?";

        // 🔍 OBSERWUJ: try-with-resources - PreparedStatement zostanie automatycznie zamknięty
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 🔍 OBSERWUJ: Ustawiamy parametr (indeksy zaczynają się od 1, nie od 0!)
            stmt.setBigDecimal(1, minBounty);

            // 🔍 OBSERWUJ: Wykonujemy zapytanie SELECT
            try (ResultSet rs = stmt.executeQuery()) {
                // 🔍 OBSERWUJ: Iterujemy po wynikach
                while (rs.next()) {
                    // 🔍 OBSERWUJ: Pobieramy wartość kolumny "name" i dodajemy do listy
                    names.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            // 🔍 OBSERWUJ: Opakowujemy SQLException w RuntimeException
            throw new RuntimeException("Błąd przy wyszukiwaniu piratów", e);
        }

        // 🧪 EKSPERYMENT: Odkomentuj poniższe linie i zobacz co się stanie!
        // System.out.println("\n🧪 EKSPERYMENT: Szukam piratów z nagrodą > 1000");
        // List<String> allRich = exercise1_FindByBountyGreaterThan(conn, new BigDecimal("1000"));
        // System.out.println("   Znaleziono: " + allRich);

        return names;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 2: Zaktualizuj nagrodę pirata
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Zaktualizuj nagrodę (bounty) pirata o podanym ID.
     *
     * 🎯 POZIOM: ŁATWY (gotowy kod - uruchom i obserwuj!)
     *
     * 🔍 OBSERWUJ:
     * - UPDATE ma składnię: UPDATE tabela SET kolumna = ? WHERE id = ?
     * - Kolejność parametrów: 1=bounty (SET), 2=id (WHERE)
     * - executeUpdate() zwraca liczbę zmienionych wierszy
     * - Dla istniejącego pirata powinno zwrócić 1
     *
     * ❓ PYTANIE: Co się stanie jeśli podamy nieistniejące ID?
     * 💡 ODPOWIEDŹ: executeUpdate() zwróci 0 (żaden wiersz nie został zmieniony)
     *
     * 🆘 Jeśli chcesz zobaczyć więcej przykładów, sprawdź PirateExercisesSolutions.java
     *
     * @param conn połączenie do bazy
     * @param pirateId ID pirata
     * @param newBounty nowa nagroda
     * @return liczba zaktualizowanych rekordów (powinna być 1)
     */
    private static int exercise2_UpdateBounty(Connection conn, Long pirateId, BigDecimal newBounty) {
        // 🔍 OBSERWUJ: SQL UPDATE z dwoma parametrami
        String sql = "UPDATE pirates SET bounty = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 🔍 OBSERWUJ: Ustawiamy parametry W KOLEJNOŚCI jak w SQL!
            stmt.setBigDecimal(1, newBounty);  // Pierwszy ? (SET bounty = ?)
            stmt.setLong(2, pirateId);          // Drugi ? (WHERE id = ?)

            // 🔍 OBSERWUJ: executeUpdate() zwraca liczbę zmienionych wierszy
            // Powinno być 1, jeśli pirat istnieje
            int updated = stmt.executeUpdate();

            // 🧪 EKSPERYMENT: Odkomentuj i zobacz co się stanie!
            // System.out.println("🧪 EKSPERYMENT: Zaktualizowano " + updated + " wierszy");
            // if (updated == 0) {
            //     System.out.println("   ⚠️ Pirat o ID " + pirateId + " nie istnieje!");
            // }

            return updated;

        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy aktualizacji nagrody", e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 3: Usuń piratów o danej randze (z transakcją)
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Usuń wszystkich piratów o podanej randze.
     *
     * 🎯 POZIOM: ŚREDNI (struktura + TODO)
     *
     * ⚠️ WAŻNE: To ćwiczenie wymaga użycia TRANSAKCJI!
     *
     * Kroki do wykonania:
     * 1. Napisz SQL DELETE
     * 2. Wyłącz auto-commit
     * 3. Wykonaj DELETE w PreparedStatement
     * 4. Zatwierdź transakcję (commit)
     * 5. W przypadku błędu - wycofaj (rollback)
     * 6. W finally - przywróć auto-commit
     *
     * 💡 Wskazówki:
     * - setAutoCommit(false) rozpoczyna transakcję
     * - commit() zatwierdza wszystkie zmiany
     * - rollback() cofa wszystkie zmiany
     * - ZAWSZE przywracaj auto-commit w finally!
     * - W tym prostym przypadku transakcja nie jest konieczna,
     *   ale pokazuje wzorzec dla bardziej złożonych operacji
     *
     * 🆘 Jeśli utkniesz, sprawdź PirateExercisesSolutions.java
     *
     * @param conn połączenie do bazy
     * @param rank ranga piratów do usunięcia
     * @return liczba usuniętych rekordów
     */
    private static int exercise3_DeleteByRank(Connection conn, String rank) {
        // TODO 1: Napisz SQL - DELETE FROM pirates WHERE rank = ?
        String sql = ""; // <-- UZUPEŁNIJ

        try {
            // TODO 2: Wyłącz auto-commit (rozpocznij transakcję)
            // Hint: conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // TODO 3: Ustaw parametr rank
                // Hint: stmt.setString(1, rank);

                // TODO 4: Wykonaj DELETE i zapisz wynik
                // Hint: int deleted = stmt.executeUpdate();

                // TODO 5: Zatwierdź transakcję
                // Hint: conn.commit();
                // Hint: System.out.println("   ✅ COMMIT - transakcja zatwierdzona");

                // TODO 6: Zwróć liczbę usuniętych rekordów
                // Hint: return deleted;
            }

        } catch (SQLException e) {
            // TODO 7: W przypadku błędu - wycofaj transakcję
            // Hint: System.out.println("   ⚠️ BŁĄD: " + e.getMessage());
            // Hint: try { conn.rollback(); System.out.println("   ↩️ ROLLBACK - zmiany cofnięte"); }
            // Hint: catch (SQLException rollbackEx) { System.err.println("Błąd rollback: " + rollbackEx.getMessage()); }
            throw new RuntimeException("Błąd przy usuwaniu piratów", e);

        } finally {
            // TODO 8: ZAWSZE przywróć auto-commit
            // Hint: try { conn.setAutoCommit(true); }
            // Hint: catch (SQLException e) { System.err.println("Błąd auto-commit: " + e.getMessage()); }
        }

        return 0; // <-- ZMIEŃ - zaimplementuj TODO 6
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 4: Policz piratów o danej randze
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Policz piratów o podanej randze.
     *
     * 🎯 POZIOM: ŚREDNI (struktura + TODO)
     *
     * Kroki do wykonania:
     * 1. Napisz SQL z COUNT(*)
     * 2. Ustaw parametr rank
     * 3. Wykonaj zapytanie
     * 4. Pobierz wynik jako long
     *
     * 💡 Wskazówki:
     * - COUNT(*) zawsze zwraca wynik (nawet 0)
     * - rs.getLong(1) pobiera pierwszą kolumnę jako long
     * - Indeks 1, bo JDBC liczy od 1, nie od 0!
     * - Można też użyć aliasu: SELECT COUNT(*) as total
     *   i potem rs.getLong("total")
     *
     * 🆘 Jeśli utkniesz, sprawdź PirateExercisesSolutions.java
     *
     * @param conn połączenie do bazy
     * @param rank ranga do zliczenia
     * @return liczba piratów o danej randze
     */
    private static long exercise4_CountByRank(Connection conn, String rank) {
        // TODO 1: Napisz SQL - SELECT COUNT(*) FROM pirates WHERE rank = ?
        String sql = ""; // <-- UZUPEŁNIJ

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // TODO 2: Ustaw parametr rank
            // Hint: stmt.setString(1, rank);

            try (ResultSet rs = stmt.executeQuery()) {
                // TODO 3: Sprawdź czy jest wynik i pobierz liczbę
                // Hint: COUNT(*) zawsze zwraca wynik, więc rs.next() zawsze będzie true
                if (rs.next()) {
                    // TODO 4: Zwróć wartość pierwszej kolumny jako long
                    // Hint: return rs.getLong(1);
                    // Alternatywnie: rs.getLong("nazwa_aliasu") jeśli użyłeś AS w SQL
                }
                return 0; // <-- Fallback (nie powinien się wykonać)
            }

        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy zliczaniu piratów", e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 5: Przenieś pirata między statkami (ZAAWANSOWANE)
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Przenieś pirata na inny statek.
     *
     * 🎯 POZIOM: TRUDNY (tylko wskazówki)
     *
     * ⚠️ WAŻNE: To ćwiczenie wymaga pełnej obsługi transakcji z walidacją!
     *
     * Wymagania:
     * 1. Sprawdź czy pirat istnieje - jeśli nie, rzuć SQLException
     * 2. Sprawdź czy statek docelowy istnieje - jeśli nie, rzuć SQLException
     * 3. Zaktualizuj ship_id pirata
     * 4. Wszystko w jednej transakcji
     * 5. W przypadku błędu - rollback i zwróć false
     * 6. W przypadku sukcesu - commit i zwróć true
     *
     * 💡 Wskazówki:
     * - Użyj 3 zapytań SQL: 2x SELECT COUNT(*), 1x UPDATE
     * - Walidacja MUSI być w transakcji (zapobiega race condition)
     * - SELECT COUNT(*) zwraca 0 jeśli rekord nie istnieje, 1 jeśli istnieje
     * - Rzuć SQLException z opisowym komunikatem jeśli walidacja nie przejdzie
     * - Pamiętaj o try-catch-finally dla transakcji
     * - W catch: rollback + return false
     * - W finally: setAutoCommit(true)
     *
     * 🔍 Struktura rozwiązania:
     * - try { setAutoCommit(false) + walidacja + UPDATE + commit + return true }
     * - catch { rollback + return false }
     * - finally { setAutoCommit(true) }
     *
     * 🆘 Jeśli utkniesz, sprawdź PirateExercisesSolutions.java
     *
     * @param conn połączenie do bazy
     * @param pirateId ID pirata do przeniesienia
     * @param newShipId ID statku docelowego
     * @return true jeśli transfer się udał, false w przeciwnym razie
     */
    private static boolean exercise5_TransferPirate(Connection conn, Long pirateId, Long newShipId) {
        // SQL do sprawdzenia czy pirat istnieje
        String checkPirateSql = "SELECT COUNT(*) FROM pirates WHERE id = ?";

        // SQL do sprawdzenia czy statek istnieje
        String checkShipSql = "SELECT COUNT(*) FROM ships WHERE id = ?";

        // TODO 1: Napisz SQL do aktualizacji ship_id pirata
        String updateSql = ""; // <-- UZUPEŁNIJ: UPDATE pirates SET ship_id = ? WHERE id = ?

        // TODO 2: Zaimplementuj pełną transakcję z walidacją
        // Struktura:
        // try {
        //     // Rozpocznij transakcję
        //     // Sprawdź czy pirat istnieje (jeśli COUNT = 0, rzuć SQLException)
        //     // Sprawdź czy statek istnieje (jeśli COUNT = 0, rzuć SQLException)
        //     // Wykonaj UPDATE
        //     // Zatwierdź transakcję
        //     // Zwróć true
        // } catch (SQLException e) {
        //     // Wypisz błąd
        //     // Wycofaj transakcję
        //     // Zwróć false
        // } finally {
        //     // Przywróć auto-commit
        // }

        return false; // <-- ZMIEŃ - zaimplementuj logikę powyżej
    }

    // ═══════════════════════════════════════════════════════════════════════
    // METODY POMOCNICZE (NIE MODYFIKUJ)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Inicjalizuje bazę danych z danymi testowymi.
     */
    private static void setupDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Tworzenie tabel
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

            // Dodanie statków
            stmt.execute("INSERT INTO ships (name, ship_type, cannons) VALUES ('Black Pearl', 'Galleon', 32)");
            stmt.execute("INSERT INTO ships (name, ship_type, cannons) VALUES ('Flying Dutchman', 'Galleon', 46)");
            stmt.execute("INSERT INTO ships (name, ship_type, cannons) VALUES ('Queen Annes Revenge', 'Frigate', 40)");

            // Dodanie piratów
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

    /**
     * Wyświetla początkowy stan danych.
     */
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

    /**
     * Wyświetla końcowy stan danych.
     */
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

    /**
     * Pobiera nagrodę pirata po ID.
     */
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

    /**
     * Liczy wszystkich piratów.
     */
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
