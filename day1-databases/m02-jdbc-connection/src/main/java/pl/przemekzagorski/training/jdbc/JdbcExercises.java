package pl.przemekzagorski.training.jdbc;

import java.math.BigDecimal;
import java.sql.*;

/**
 * 🏴‍☠️ Ćwiczenia JDBC - Debugowanie i obserwacja
 *
 * 📋 INSTRUKCJA DEBUGOWANIA:
 * 1. Ustaw breakpointy w miejscach oznaczonych 🔴 BREAKPOINT
 * 2. Uruchom w trybie Debug (Shift+F9 w IntelliJ)
 * 3. Obserwuj zmienne w panelu "Variables"
 * 4. Używaj F8 (Step Over) do przechodzenia linia po linii
 * 5. Używaj F7 (Step Into) aby wejść do metody
 *
 * 🎯 CEL: Zrozumieć jak działa JDBC "pod maską"
 */
public class JdbcExercises {

    // 🔍 TRACE_LEVEL_SYSTEM_OUT=2 - pokazuje wszystkie zapytania SQL w konsoli!
    // Poziomy: 0=OFF, 1=ERROR, 2=INFO, 3=DEBUG, 4=TRACE
    private static final String JDBC_URL = "jdbc:h2:mem:exercises;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=2";
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
     * 🔍 CO OBSERWOWAĆ W DEBUGGERZE:
     * - Typ obiektu Connection (implementacja H2)
     * - Stan połączenia (isClosed, autoCommit)
     * - Co się dzieje po wyjściu z try-with-resources
     *
     * 📌 BREAKPOINTY:
     * 1. 🔴 Na linii z getConnection() - PRZED połączeniem
     * 2. 🔴 Na println "Połączono" - PO połączeniu, sprawdź obiekt conn
     * 3. 🔴 Na zamykającym nawiasie } catch - czy połączenie się zamknęło?
     */
    private static void exercise1_BasicConnection() {
        System.out.println("📝 Ćwiczenie 1: Podstawowe połączenie");

        // 🔴 BREAKPOINT 1: Ustaw tutaj - sprawdź wartości JDBC_URL, USER, PASSWORD
        // 👁️ OBSERWUJ: Zmienne statyczne w panelu Variables
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            // 🔴 BREAKPOINT 2: Ustaw tutaj - połączenie nawiązane
            // 👁️ OBSERWUJ w panelu Variables:
            //    - conn -> rozwiń i zobacz: isClosed=false, autoCommit=true
            //    - Typ: JdbcConnection (implementacja H2)
            // 💡 ZADANIE: W panelu Evaluate (Alt+F8) wpisz: conn.getMetaData().getDatabaseProductName()
            System.out.println("   ✅ Połączono z bazą!");

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd połączenia: " + e.getMessage());
        }
        // 🔴 BREAKPOINT 3: Ustaw tutaj (poza try)
        // 👁️ OBSERWUJ: Zmienna conn już nie istnieje - została automatycznie zamknięta!
        // 💡 To jest magia try-with-resources
    }

    /**
     * 🎯 ĆWICZENIE 2: Wykonanie zapytania SELECT
     *
     * 🔍 CO OBSERWOWAĆ W DEBUGGERZE:
     * - Jak ResultSet przechowuje dane (kursor)
     * - Jak zmienia się pozycja kursora po każdym next()
     * - Wartości pobierane z kolumn
     *
     * 📌 BREAKPOINTY:
     * 1. 🔴 Przed executeQuery() - zapytanie jeszcze nie wykonane
     * 2. 🔴 Na while(rs.next()) - obserwuj kursor ResultSet
     * 3. 🔴 Wewnątrz while - sprawdź pobrane wartości
     */
    private static void exercise2_SelectQuery() {
        System.out.println("📝 Ćwiczenie 2: Zapytanie SELECT");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE pirates (id INT, name VARCHAR(100), bounty DECIMAL(10,2))");
            stmt.execute("INSERT INTO pirates VALUES (1, 'Jack Sparrow', 10000)");
            stmt.execute("INSERT INTO pirates VALUES (2, 'Hector Barbossa', 8000)");
            stmt.execute("INSERT INTO pirates VALUES (3, 'Davy Jones', 15000)");

            System.out.println("   🏴‍☠️ Piraci w bazie:");

            // 🔴 BREAKPOINT 1: Przed executeQuery
            // 👁️ OBSERWUJ: stmt istnieje, ale rs jeszcze nie
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM pirates ORDER BY bounty DESC")) {

                // 🔴 BREAKPOINT 2: Na while - kursor jest PRZED pierwszym rekordem
                // 👁️ OBSERWUJ: rs -> currentRow (początkowo przed pierwszym wierszem)
                // 💡 ZADANIE: W Evaluate wpisz: rs.getRow() - powinno zwrócić 0
                while (rs.next()) {

                    // 🔴 BREAKPOINT 3: Wewnątrz pętli
                    // 👁️ OBSERWUJ: Po każdym next() kursor przesuwa się o 1
                    // 💡 ZADANIE: Sprawdź rs.getRow() - będzie 1, potem 2, potem 3
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    BigDecimal bounty = rs.getBigDecimal("bounty");

                    // 👁️ OBSERWUJ: Wartości id, name, bounty - porównaj z kolejnością ORDER BY
                    // 💡 PYTANIE: Dlaczego Davy Jones jest pierwszy? (najwyższa nagroda)
                    System.out.printf("   • [%d] %s - nagroda: %.2f złota%n", id, name, bounty);
                }// 💡 Po ostatnim next() zwraca false - koniec danych
            }

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd: " + e.getMessage());
        }
    }

    /**
     * 🎯 ĆWICZENIE 3: INSERT używając Statement
     *
     * 🔍 CO OBSERWOWAĆ W DEBUGGERZE:
     * - Jak wygląda SQL składany ze stringów
     * - ⚠️ PROBLEM BEZPIECZEŃSTWA: Co jeśli name = "Black'; DROP TABLE ships;--"
     *
     * 📌 ZADANIE DODATKOWE:
     * W Evaluate Expression (Alt+F8) wykonaj:
     * "INSERT INTO ships VALUES (2, '" + "Black'; DROP TABLE ships;--" + "', 10)"
     * Zobacz jak wygląda ten SQL - to jest SQL Injection!
     */
    private static void exercise3_InsertWithStatement() {
        System.out.println("📝 Ćwiczenie 3: INSERT używając Statement (⚠️ niebezpieczne)");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE ships (id INT, name VARCHAR(100), cannons INT)");

            // 🔴 BREAKPOINT: Zatrzymaj się tutaj
            // 👁️ OBSERWUJ: To jest "surowy" SQL - wartości są wklejone bezpośrednio
            // ⚠️ NIEBEZPIECZEŃSTWO: Co jeśli użytkownik poda złośliwy tekst?
            // 💡 ZADANIE: W Evaluate wpisz poniższy kod i zobacz wynikowy SQL:
            //    String malicious = "Black'; DROP TABLE ships;--";
            //    "INSERT INTO ships VALUES (2, '" + malicious + "', 10)"
            stmt.execute("INSERT INTO ships VALUES (1, 'Black Pearl', 32)");

            System.out.println("   ✅ Dodano statek do bazy");

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM ships")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int cannons = rs.getInt("cannons");
                    System.out.printf("   🚢 [%d] %s - %d armat%n", id, name, cannons);
                }
            }

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd: " + e.getMessage());
        }
    }

    /**
     * 🎯 ĆWICZENIE 4: INSERT używając PreparedStatement
     *
     * 🔍 CO OBSERWOWAĆ W DEBUGGERZE:
     * - Różnica między Statement a PreparedStatement
     * - Jak parametry są przechowywane osobno od SQL
     * - Stan pstmt przed i po ustawieniu parametrów
     *
     * 📌 BREAKPOINTY:
     * 1. 🔴 Po prepareStatement() - SQL jest skompilowany, parametry puste
     * 2. 🔴 Po każdym setXxx() - sprawdź jak parametry są dodawane
     * 3. 🔴 Po executeUpdate() - sprawdź wartość rows
     */
    private static void exercise4_InsertWithPreparedStatement() {
        System.out.println("📝 Ćwiczenie 4: INSERT używając PreparedStatement (✅ bezpieczne)");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE treasures (id INT, name VARCHAR(100), treasure_value DECIMAL(15,2))");

            String sql = "INSERT INTO treasures VALUES (?, ?, ?)";

            // 🔴 BREAKPOINT 1: Po prepareStatement
            // 👁️ OBSERWUJ: pstmt ma SQL z placeholderami ?, parametry jeszcze nie ustawione
            // 💡 ZADANIE: Rozwiń pstmt i znajdź pole z SQL
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // 🔴 BREAKPOINT 2: Po każdym set - obserwuj jak parametry są dodawane
                // 👁️ OBSERWUJ: pstmt -> parameters (tablica parametrów)
                pstmt.setInt(1, 1);
                // 💡 Teraz parametr[0] = 1

                pstmt.setString(2, "Aztec Gold");
                // 💡 Teraz parametr[1] = "Aztec Gold"

                pstmt.setBigDecimal(3, new BigDecimal("1000000"));
                // 💡 Teraz parametr[2] = 1000000

                // 🔴 BREAKPOINT 3: Przed executeUpdate
                // 👁️ OBSERWUJ: Wszystkie parametry ustawione
                // 💡 KLUCZOWA RÓŻNICA: Parametry są ODDZIELONE od SQL!
                //    Nawet jeśli name = "'; DROP TABLE treasures;--"
                //    to zostanie zapisane jako tekst, nie wykonane jako SQL
                int rows = pstmt.executeUpdate();

                // 👁️ OBSERWUJ: rows = 1 (jeden rekord wstawiony)
                System.out.println("   ✅ Wstawiono rekordów: " + rows);
            }

        } catch (SQLException e) {
            System.err.println("   ❌ Błąd: " + e.getMessage());
        }
    }

    /**
     * 🎯 ĆWICZENIE 5: Obsługa SQLException
     *
     * 🔍 CO OBSERWOWAĆ W DEBUGGERZE:
     * - Jak wygląda obiekt SQLException
     * - Jakie informacje zawiera (message, errorCode, SQLState)
     * - Jak działa przepływ try-catch
     *
     * 📌 BREAKPOINTY:
     * 1. 🔴 Na executeQuery() - zaraz zostanie rzucony wyjątek
     * 2. 🔴 W catch - obserwuj obiekt SQLException
     */
    private static void exercise5_ExceptionHandling() {
        System.out.println("📝 Ćwiczenie 5: Obsługa SQLException");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 🔴 BREAKPOINT 1: Przed executeQuery
            // 👁️ OBSERWUJ: Zaraz zostanie rzucony wyjątek
            // 💡 ZADANIE: Naciśnij F8 (Step Over) i zobacz jak debugger przeskakuje do catch
            stmt.executeQuery("SELECT * FROM nieistniejaca_tabela");

            System.out.println("   ⚠️ Jeśli widzisz tę linię, coś poszło nie tak!");

        } catch (SQLException e) {
            // 🔴 BREAKPOINT 2: W catch
            // 👁️ OBSERWUJ: Rozwiń obiekt 'e' w panelu Variables
            //    - message: informacja o błędzie
            //    - errorCode: kod specyficzny dla H2 (42102 = tabela nie istnieje)
            //    - SQLState: standardowy kod SQL (42S02 = tabela nie istnieje)
            // 💡 ZADANIE: W Evaluate wpisz: e.getCause() - sprawdź czy jest łańcuch wyjątków
            System.out.println("   ✅ Złapano wyjątek (to jest OK!)");
            System.out.println("   📝 Komunikat: " + e.getMessage());
            System.out.println("   🔢 Kod błędu: " + e.getErrorCode());
            System.out.println("   🏷️  SQL State: " + e.getSQLState());
        }
    }

    /**
     * 🎯 ĆWICZENIE 6: Transakcje
     *
     * 🔍 CO OBSERWOWAĆ W DEBUGGERZE:
     * - Stan autoCommit przed i po setAutoCommit(false)
     * - Dane w tabeli PRZED commit (są widoczne tylko w tej transakcji)
     * - Co się dzieje przy rollback
     *
     * 📌 BREAKPOINTY - zaawansowane:
     * 1. Po setAutoCommit(false) - transakcja rozpoczęta
     * 2. Po UPDATE - dane zmienione, ale NIE zatwierdzone
     * 3. Po commit() - zmiany trwałe
     *
     * 💡 ZADANIE: Zaimplementuj transfer i debuguj krok po kroku
     */
    private static void exercise6_Transactions() {
        System.out.println("📝 Ćwiczenie 6: Transakcje (commit/rollback)");

        // TODO: Zaimplementuj transakcję z transferem złota
        // 🔴 BREAKPOINT: Po setAutoCommit(false)
        // 👁️ OBSERWUJ: conn.getAutoCommit() = false
        //
        // 🔴 BREAKPOINT: Po pierwszym UPDATE
        // 👁️ OBSERWUJ: Dane zmienione, ale jeszcze nie zatwierdzone
        // 💡 EKSPERYMENT: Otwórz H2 Console i sprawdź dane - nie zobaczysz zmian!
        //    (bo transakcja nie jest jeszcze zatwierdzona)
        //
        // 🔴 BREAKPOINT: Po commit()
        // 👁️ OBSERWUJ: Teraz zmiany są trwałe

        System.out.println("   ⚠️ TODO: Zaimplementuj transakcję");
    }
}