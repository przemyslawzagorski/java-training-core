package pl.przemekzagorski.training.jdbc;

import java.math.BigDecimal;
import java.sql.*;

/**
 * 🏴‍☠️ Ćwiczenia JDBC - Szkielety do wypełnienia
 * 
 * Instrukcja:
 * 1. Wypełnij każdą metodę zgodnie z opisem w TODO
 * 2. Uruchom metodę main() aby przetestować swoje rozwiązania
 * 3. Jeśli utkniesz - sprawdź JdbcExercisesSolutions.java
 * 
 * Baza danych: H2 in-memory (dane znikają po zakończeniu programu)
 */
public class JdbcExercises {

    private static final String JDBC_URL = "jdbc:h2:mem:exercises;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ JDBC Exercises");
        System.out.println("==================\n");

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
     * 🎯 ĆWICZENIE 1: Podstawowe połączenie z bazą
     *
     * 🎯 POZIOM: ŁATWY (szkielet kodu)
     *
     * Cel: Nauczyć się nawiązywać połączenie z bazą używając try-with-resources
     *
     * Kroki do wykonania:
     * 1. Uzupełnij DriverManager.getConnection() - podaj URL, USER, PASSWORD
     * 2. Wyświetl komunikat "✅ Połączono z bazą!"
     * 3. Obsługa błędów już jest - tylko uzupełnij komunikat
     *
     * 💡 Wskazówki:
     * - try-with-resources automatycznie zamyka Connection
     * - Connection implementuje AutoCloseable
     * - DriverManager.getConnection() wymaga 3 parametrów
     * - SQLException musi być obsłużony
     *
     * 🆘 Jeśli utkniesz, sprawdź JdbcExercisesSolutions.java
     */
    private static void exercise1_BasicConnection() {
        System.out.println("📝 Ćwiczenie 1: Podstawowe połączenie");

        // TODO 1: Uzupełnij parametry getConnection()
        try (Connection conn = DriverManager.getConnection(/* url */ JDBC_URL, /* user */ USER, /* password */ PASSWORD)) {

            // TODO 2: Wyświetl komunikat o sukcesie
            System.out.println("   ✅ Połączono z bazą!");

        } catch (SQLException e) {
            // TODO 3: Wyświetl błąd
            System.err.println("   ❌ Błąd połączenia: " + e.getMessage());
        }
    }

    /**
     * 🎯 ĆWICZENIE 2: Wykonanie zapytania SELECT
     *
     * 🎯 POZIOM: ŁATWY (szkielet kodu)
     *
     * Cel: Nauczyć się wykonywać zapytania SELECT i iterować po wynikach
     *
     * Kroki do wykonania:
     * 1. Uzupełnij CREATE TABLE
     * 2. Uzupełnij INSERT statements
     * 3. Uzupełnij SELECT query
     * 4. Uzupełnij iterację po ResultSet
     * 5. Uzupełnij pobieranie wartości z kolumn
     *
     * 💡 Wskazówki:
     * - stmt.execute() dla CREATE i INSERT (nie zwraca wyników)
     * - stmt.executeQuery() dla SELECT (zwraca ResultSet)
     * - rs.next() przesuwa kursor i zwraca true jeśli jest następny rekord
     * - rs.getInt("id"), rs.getString("name"), rs.getBigDecimal("bounty")
     *
     * 🆘 Jeśli utkniesz, sprawdź JdbcExercisesSolutions.java
     */
    private static void exercise2_SelectQuery() {
        System.out.println("📝 Ćwiczenie 2: Zapytanie SELECT");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // TODO 1: Utwórz tabelę pirates (id INT, name VARCHAR(100), bounty DECIMAL(10,2))
            stmt.execute("CREATE TABLE pirates (id INT, name VARCHAR(100), bounty DECIMAL(10,2))");

            // TODO 2: Wstaw 3 piratów
            stmt.execute("INSERT INTO pirates VALUES (1, 'Jack Sparrow', 10000)");
            stmt.execute("INSERT INTO pirates VALUES (2, 'Hector Barbossa', 8000)");
            stmt.execute("INSERT INTO pirates VALUES (3, 'Davy Jones', 15000)");

            System.out.println("   🏴‍☠️ Piraci w bazie:");

            // TODO 3: Wykonaj SELECT i iteruj po wynikach
            try (ResultSet rs = stmt.executeQuery(/* SQL */ "SELECT * FROM pirates ORDER BY bounty DESC")) {
                // TODO 4: Iteruj po wynikach (while + rs.next())
                while (/* warunek */ rs.next()) {
                    // TODO 5: Pobierz wartości z kolumn
                    int id = rs.getInt(/* kolumna */ "id");
                    String name = rs.getString(/* kolumna */ "name");
                    BigDecimal bounty = rs.getBigDecimal(/* kolumna */ "bounty");

                    System.out.printf("   • [%d] %s - nagroda: %.2f złota%n", id, name, bounty);
                }
            }

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd: " + e.getMessage());
        }
    }

    /**
     * 🎯 ĆWICZENIE 3: Wstawienie danych używając Statement
     *
     * 🎯 POZIOM: ŁATWY (szkielet kodu)
     *
     * Cel: Nauczyć się wstawiać dane do bazy (i zrozumieć dlaczego to NIE jest bezpieczne)
     *
     * Kroki do wykonania:
     * 1. Uzupełnij CREATE TABLE
     * 2. Uzupełnij INSERT statement
     * 3. Uzupełnij SELECT do weryfikacji
     *
     * 💡 Wskazówki:
     * - Statement.execute() dla CREATE i INSERT
     * - Wartości tekstowe w SQL muszą być w apostrofach: 'Black Pearl'
     * - Liczby bez apostrofów: 32
     * - executeQuery() dla SELECT zwraca ResultSet
     *
     * ⚠️ UWAGA: Ten sposób jest podatny na SQL Injection! Użyj go tylko do nauki.
     * W ćwiczeniu 4 przepiszemy to na bezpieczny PreparedStatement.
     *
     * 🆘 Jeśli utkniesz, sprawdź JdbcExercisesSolutions.java
     */
    private static void exercise3_InsertWithStatement() {
        System.out.println("📝 Ćwiczenie 3: INSERT używając Statement (⚠️ niebezpieczne)");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // TODO 1: Utwórz tabelę ships (id INT, name VARCHAR(100), cannons INT)
            stmt.execute("CREATE TABLE ships (id INT, name VARCHAR(100), cannons INT)");

            // TODO 2: Wstaw statek "Black Pearl" z 32 armatami (id=1)
            stmt.execute("INSERT INTO ships VALUES (1, 'Black Pearl', 32)");

            System.out.println("   ✅ Dodano statek do bazy");

            // TODO 3: Wykonaj SELECT i wyświetl dodany statek
            try (ResultSet rs = stmt.executeQuery(/* SQL */ "SELECT * FROM ships")) {
                while (/* warunek */ rs.next()) {
                    int id = rs.getInt(/* kolumna */ "id");
                    String name = rs.getString(/* kolumna */ "name");
                    int cannons = rs.getInt(/* kolumna */ "cannons");

                    System.out.printf("   🚢 [%d] %s - %d armat%n", id, name, cannons);
                }
            }

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd: " + e.getMessage());
        }
    }

    /**
     * 🎯 ĆWICZENIE 4: Wstawienie danych używając PreparedStatement
     *
     * 🎯 POZIOM: ŁATWY (szkielet kodu)
     *
     * Cel: Nauczyć się bezpiecznego wstawiania danych z parametrami
     *
     * Kroki do wykonania:
     * 1. Uzupełnij CREATE TABLE
     * 2. Uzupełnij INSERT z parametrami ?
     * 3. Uzupełnij setInt, setString, setBigDecimal
     * 4. Wykonaj executeUpdate()
     *
     * 💡 Wskazówki:
     * - PreparedStatement używa ? jako placeholderów
     * - Parametry numerowane od 1 (nie od 0!)
     * - setInt(1, wartość) - pierwszy ?
     * - setString(2, wartość) - drugi ?
     * - setBigDecimal(3, wartość) - trzeci ?
     * - executeUpdate() zwraca liczbę zmienionych rekordów
     *
     * ⚠️ UWAGA: Kolumna nazywa się treasure_value (nie value), bo "value" jest słowem zastrzeżonym w H2
     *
     * 🆘 Jeśli utkniesz, sprawdź JdbcExercisesSolutions.java
     */
    private static void exercise4_InsertWithPreparedStatement() {
        System.out.println("📝 Ćwiczenie 4: INSERT używając PreparedStatement (✅ bezpieczne)");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // TODO 1: Utwórz tabelę treasures (id INT, name VARCHAR(100), treasure_value DECIMAL(15,2))
            stmt.execute("CREATE TABLE treasures (id INT, name VARCHAR(100), treasure_value DECIMAL(15,2))");

            // TODO 2: Przygotuj INSERT z parametrami ?
            String sql = "INSERT INTO treasures VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // TODO 3: Ustaw parametry dla skarbu: (1, "Aztec Gold", 1000000)
                pstmt.setInt(/* indeks */ 1, /* wartość */ 1);
                pstmt.setString(/* indeks */ 2, /* wartość */ "Aztec Gold");
                pstmt.setBigDecimal(/* indeks */ 3, /* wartość */ new BigDecimal("1000000"));

                // TODO 4: Wykonaj INSERT
                int rows = pstmt.executeUpdate();

                System.out.println("   ✅ Wstawiono rekordów: " + rows);
            }

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd: " + e.getMessage());
        }
    }

    /**
     * 🎯 ĆWICZENIE 5: Obsługa SQLException
     *
     * 🎯 POZIOM: ŁATWY (szkielet kodu)
     *
     * Cel: Nauczyć się prawidłowo obsługiwać błędy SQL
     *
     * Kroki do wykonania:
     * 1. Uzupełnij błędne zapytanie SQL
     * 2. Uzupełnij wyświetlanie szczegółów błędu
     *
     * 💡 Wskazówki:
     * - To ćwiczenie POWINNO rzucić wyjątek - to jest zamierzone!
     * - SQLException zawiera szczegółowe informacje o błędzie
     * - getMessage() - czytelny komunikat
     * - getErrorCode() - kod błędu specyficzny dla bazy danych
     * - getSQLState() - standardowy kod SQL (5 znaków)
     *
     * 🆘 Jeśli utkniesz, sprawdź JdbcExercisesSolutions.java
     */
    private static void exercise5_ExceptionHandling() {
        System.out.println("📝 Ćwiczenie 5: Obsługa SQLException");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // TODO 1: Wykonaj BŁĘDNE zapytanie (tabela nie istnieje)
            stmt.executeQuery(/* błędny SQL */ "SELECT * FROM nieistniejaca_tabela");

            System.out.println("   ⚠️ Jeśli widzisz tę linię, coś poszło nie tak!");

        } catch (SQLException e) {
            // TODO 2: Wyświetl szczegóły błędu
            System.out.println("   ✅ Złapano wyjątek (to jest OK!)");
            System.out.println("   📝 Komunikat: " + e.getMessage());
            System.out.println("   🔢 Kod błędu: " + e.getErrorCode());
            System.out.println("   🏷️  SQL State: " + e.getSQLState());
        }
    }

    /**
     * 🎯 ĆWICZENIE 6: Transakcje (ZAAWANSOWANE)
     *
     * 🎯 POZIOM: TRUDNY (tylko wskazówki)
     *
     * Cel: Nauczyć się zarządzać transakcjami (commit/rollback)
     *
     * Wymagania:
     * 1. Wyłącz auto-commit (rozpocznij transakcję)
     * 2. Utwórz tabelę accounts
     * 3. Wstaw 2 konta: Jack (1000 złota), Barbossa (500 złota)
     * 4. Wykonaj transfer 200 złota od Jacka do Barbossy (2 UPDATE)
     * 5. Zatwierdź transakcję
     * 6. Wyświetl salda po transferze
     * 7. W przypadku błędu - wycofaj transakcję
     *
     * 💡 Wskazówki:
     * - Transakcja = grupa operacji wykonywanych atomowo (wszystkie albo żadna)
     * - setAutoCommit(false) - wyłącza automatyczne zatwierdzanie
     * - commit() - zatwierdza wszystkie zmiany od początku transakcji
     * - rollback() - cofa wszystkie zmiany od początku transakcji
     * - Transfer wymaga 2 UPDATE: jeden odejmuje, drugi dodaje
     * - Oba UPDATE muszą się udać albo żaden (atomowość!)
     * - W finally ZAWSZE przywróć auto-commit
     *
     * 🔍 Struktura rozwiązania:
     * - try { setAutoCommit(false) + CREATE + INSERT + UPDATE + UPDATE + commit }
     * - catch { rollback }
     * - finally { setAutoCommit(true) }
     *
     * 🆘 Jeśli utkniesz, sprawdź JdbcExercisesSolutions.java
     */
    private static void exercise6_Transactions() {
        System.out.println("📝 Ćwiczenie 6: Transakcje (commit/rollback)");

        // TODO: Zaimplementuj pełną transakcję z transferem złota
        // Struktura:
        // try (Connection conn = ...; Statement stmt = ...) {
        //     // Wyłącz auto-commit
        //     // Utwórz tabelę accounts (id INT, name VARCHAR(100), balance DECIMAL(10,2))
        //     // Wstaw 2 konta
        //     // Wykonaj 2 UPDATE (transfer)
        //     // Zatwierdź transakcję
        //     // Wyświetl salda (SELECT)
        // } catch (SQLException e) {
        //     // Wycofaj transakcję
        // } finally {
        //     // Przywróć auto-commit
        // }

    }
}

