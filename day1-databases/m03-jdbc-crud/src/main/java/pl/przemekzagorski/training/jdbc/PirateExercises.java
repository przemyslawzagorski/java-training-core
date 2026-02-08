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
 * ═══════════════════════════════════════════════════════════════════
 * 📋 INSTRUKCJA DEBUGOWANIA:
 * ═══════════════════════════════════════════════════════════════════
 * 1. Ustaw breakpointy w miejscach oznaczonych 🔴 BREAKPOINT
 * 2. Uruchom w trybie Debug (Shift+F9 w IntelliJ)
 * 3. Obserwuj zmienne w panelu "Variables"
 * 4. Używaj F8 (Step Over) do przechodzenia linia po linii
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
     * 📋 INSTRUKCJA DEBUGOWANIA:
     * ════════════════════════════════════════════════════════════════
     * 1. Ustaw breakpointy w miejscach oznaczonych 🔴 BREAKPOINT
     * 2. Uruchom w trybie Debug (Shift+F9)
     * 3. Obserwuj zmienne w panelu "Variables"
     * 4. Używaj F8 (Step Over) do przechodzenia linia po linii
     *
     * 🔍 CO OBSERWOWAĆ:
     * - Jak PreparedStatement przechowuje SQL z parametrem ?
     * - Jak parametr jest ustawiany (indeks zaczyna się od 1!)
     * - Jak ResultSet iteruje po wynikach (kursor)
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

        // 🔴 BREAKPOINT 1: Ustaw tutaj - SQL z parametrem ? (BEZPIECZNE!)
        // 👁️ OBSERWUJ: Zmienna sql zawiera "?" zamiast wartości
        // 💡 To chroni przed SQL Injection - parametr NIE jest częścią SQL!
        String sql = "SELECT name FROM pirates WHERE bounty > ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 🔴 BREAKPOINT 2: Ustaw tutaj - PO prepareStatement, PRZED setBigDecimal
            // 👁️ OBSERWUJ w panelu Variables:
            //    - stmt -> rozwiń i znajdź pole z SQL
            //    - Parametry jeszcze nie ustawione!
            // 💡 ZADANIE: W Evaluate (Alt+F8) wpisz: stmt.toString()

            stmt.setBigDecimal(1, minBounty);

            // 🔴 BREAKPOINT 3: Ustaw tutaj - PO setBigDecimal
            // 👁️ OBSERWUJ: Teraz parametr jest ustawiony
            // 💡 ZADANIE: W Evaluate wpisz: stmt.toString() - zobacz różnicę!
            // 💡 UWAGA: Indeks parametru to 1, nie 0! (JDBC liczy od 1)

            try (ResultSet rs = stmt.executeQuery()) {
                // 🔴 BREAKPOINT 4: Ustaw tutaj - wewnątrz while
                // 👁️ OBSERWUJ: rs -> currentRow (pozycja kursora)
                // 💡 ZADANIE: Sprawdź rs.getRow() w Evaluate - zmienia się po każdym next()
                while (rs.next()) {
                    String name = rs.getString("name");
                    // 👁️ OBSERWUJ: Wartość name - porównaj z danymi w bazie
                    names.add(name);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy wyszukiwaniu piratów", e);
        }

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
     * 📋 INSTRUKCJA DEBUGOWANIA:
     * ════════════════════════════════════════════════════════════════
     * Breakpointy pokazują KOLEJNOŚĆ parametrów i wartość zwracaną przez executeUpdate()
     *
     * 🔍 CO OBSERWOWAĆ:
     * - Kolejność parametrów: 1=bounty (SET), 2=id (WHERE)
     * - executeUpdate() zwraca liczbę zmienionych wierszy
     *
     * ❓ PYTANIE: Co się stanie jeśli podamy nieistniejące ID?
     * 💡 ODPOWIEDŹ: executeUpdate() zwróci 0 (żaden wiersz nie został zmieniony)
     *
     * @param conn połączenie do bazy
     * @param pirateId ID pirata
     * @param newBounty nowa nagroda
     * @return liczba zaktualizowanych rekordów (powinna być 1)
     */
    private static int exercise2_UpdateBounty(Connection conn, Long pirateId, BigDecimal newBounty) {
        String sql = "UPDATE pirates SET bounty = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 🔴 BREAKPOINT 1: Ustaw tutaj - PRZED ustawieniem parametrów
            // 👁️ OBSERWUJ: Kolejność parametrów MUSI odpowiadać kolejności ? w SQL!
            //    SQL: UPDATE ... SET bounty = ? WHERE id = ?
            //                        ↑ param 1    ↑ param 2

            stmt.setBigDecimal(1, newBounty);  // Pierwszy ? (SET bounty = ?)
            stmt.setLong(2, pirateId);          // Drugi ? (WHERE id = ?)

            // 🔴 BREAKPOINT 2: Ustaw tutaj - PO executeUpdate
            // 👁️ OBSERWUJ: Wartość updated - ile wierszy zostało zmienionych?
            // 💡 ZADANIE: Zmień pirateId na 999 i zobacz że updated = 0
            int updated = stmt.executeUpdate();


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
            // 🔴 BREAKPOINT 1: PRZED setAutoCommit(false)
            // 👁️ OBSERWUJ w Variables:
            //    - conn.getAutoCommit() - sprawdź w Evaluate Expression (powinno być true)
            // 💡 KLUCZOWA OBSERWACJA: autoCommit = true oznacza:
            //    - Każde SQL (INSERT/UPDATE/DELETE) jest natychmiast zatwierdzane
            //    - Nie możesz cofnąć zmian (brak rollback)
            //    - Dla transakcji MUSISZ wyłączyć autoCommit!

            // TODO 2: Wyłącz auto-commit (rozpocznij transakcję)
            // Hint: conn.setAutoCommit(false);

            // 🔴 BREAKPOINT 2: PO setAutoCommit(false)
            // 👁️ OBSERWUJ w Variables:
            //    - conn.getAutoCommit() - sprawdź w Evaluate (powinno być false)
            // 💡 KLUCZOWA OBSERWACJA: Transakcja rozpoczęta!
            //    - Zmiany NIE będą widoczne dla innych sesji do commit()
            //    - Możesz cofnąć zmiany przez rollback()
            // 💡 ZADANIE: Otwórz H2 Console w przeglądarce (http://localhost:8082)
            //    i wykonaj: SELECT * FROM pirates WHERE rank = 'Sailor'
            //    Zobaczysz 2 rekordy (Pintel, Ragetti)

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // TODO 3: Ustaw parametr rank
                // Hint: stmt.setString(1, rank);

                // 🔴 BREAKPOINT 3: PRZED executeUpdate()
                // 👁️ OBSERWUJ w Variables:
                //    - stmt - PreparedStatement z parametrem rank
                //    - sql - zapytanie DELETE FROM pirates WHERE rank = ?
                // 💡 ZADANIE: Sprawdź stmt.toString() w Evaluate
                //    Zobaczysz zapytanie z podstawionym parametrem

                // TODO 4: Wykonaj DELETE i zapisz wynik
                // Hint: int deleted = stmt.executeUpdate();

                // 🔴 BREAKPOINT 4: PO executeUpdate(), PRZED commit()
                // 👁️ OBSERWUJ w Variables:
                //    - deleted - liczba usuniętych rekordów (powinno być 2)
                // 💡 KLUCZOWA OBSERWACJA: DELETE wykonany, ale NIE zatwierdzony!
                //    - W tej sesji: rekordy usunięte (w pamięci transakcji)
                //    - W innych sesjach: rekordy WCIĄŻ WIDOCZNE!
                // 💡 EKSPERYMENT: Sprawdź w H2 Console:
                //    SELECT * FROM pirates WHERE rank = 'Sailor'
                //    WCIĄŻ zobaczysz 2 rekordy! (izolacja transakcji!)
                // 💡 PYTANIE: Dlaczego inne sesje nie widzą zmian?
                //    Odpowiedź: Transakcja NIE została zatwierdzona (brak commit)
                //    Poziom izolacji READ_COMMITTED - inne sesje widzą tylko zatwierdzone dane

                // TODO 5: Zatwierdź transakcję
                // Hint: conn.commit();
                // Hint: System.out.println("   ✅ COMMIT - transakcja zatwierdzona");

                // 🔴 BREAKPOINT 5: PO commit()
                // 👁️ OBSERWUJ: Logi w konsoli - zobaczysz "✅ COMMIT"
                // 💡 KLUCZOWA OBSERWACJA: Transakcja zatwierdzona!
                //    - Zmiany są TRWAŁE w bazie danych
                //    - Inne sesje TERAZ zobaczą zmiany
                // 💡 EKSPERYMENT: Odśwież zapytanie w H2 Console:
                //    SELECT * FROM pirates WHERE rank = 'Sailor'
                //    TERAZ zobaczysz 0 rekordów! (commit wykonany!)

                // TODO 6: Zwróć liczbę usuniętych rekordów
                // Hint: return deleted;
            }

        } catch (SQLException e) {
            // 🔴 BREAKPOINT 6: W bloku catch (tylko jeśli wystąpi błąd)
            // 👁️ OBSERWUJ w Variables:
            //    - e.getMessage() - komunikat błędu SQL
            // 💡 KLUCZOWA OBSERWACJA: Błąd SQL - trzeba cofnąć transakcję!
            //    - Bez rollback() zmiany mogą pozostać w nieokreślonym stanie
            //    - rollback() cofa WSZYSTKIE zmiany od setAutoCommit(false)

            // TODO 7: W przypadku błędu - wycofaj transakcję
            System.out.println("   ⚠️ BŁĄD: " + e.getMessage());
            try {
                // Hint: conn.rollback();
                System.out.println("   ↩️ ROLLBACK - zmiany cofnięte");
            } catch (Exception rollbackEx) {
                System.err.println("Błąd podczas rollback: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Błąd przy usuwaniu piratów", e);

        } finally {
            // 🔴 BREAKPOINT 7: W bloku finally (ZAWSZE wykonywany)
            // 👁️ OBSERWUJ: Ten blok wykona się ZAWSZE (sukces lub błąd)
            // 💡 KLUCZOWA OBSERWACJA: Przywracanie autoCommit w finally!
            //    - finally wykonuje się ZAWSZE (nawet po return lub exception)
            //    - Bez tego kolejne operacje działałyby w trybie transakcyjnym
            //    - To jest WZORZEC - zawsze przywracaj stan początkowy!

            // TODO 8: ZAWSZE przywróć auto-commit
            try {
                // Hint: conn.setAutoCommit(true);
            } catch (Exception e) {
                System.err.println("Błąd przy przywracaniu auto-commit: " + e.getMessage());
            }
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

        // 🔴 BREAKPOINT 1: PRZED rozpoczęciem transakcji
        // 👁️ OBSERWUJ w Variables:
        //    - pirateId = 4 (Joshamee Gibbs)
        //    - newShipId = 2 (Flying Dutchman) lub 999 (nieistniejący)
        // 💡 KLUCZOWA OBSERWACJA: Transakcja wieloetapowa!
        //    - Krok 1: Sprawdź czy pirat istnieje
        //    - Krok 2: Sprawdź czy statek istnieje
        //    - Krok 3: Wykonaj UPDATE
        //    - Wszystko w JEDNEJ transakcji (atomowość!)
        // 💡 PYTANIE: Dlaczego walidacja w transakcji?
        //    Odpowiedź: Zapobiega "race condition" - między sprawdzeniem
        //    a UPDATE ktoś inny mógłby usunąć pirata lub statek!

        // TODO 2: Zaimplementuj pełną transakcję z walidacją
        // Struktura:
        // try {
        //     // 🔴 BREAKPOINT 2: PO setAutoCommit(false)
        //     // 👁️ OBSERWUJ: Transakcja rozpoczęta
        //     // 💡 KLUCZOWA OBSERWACJA: Wszystkie kroki będą w JEDNEJ transakcji
        //     //    - Jeśli którykolwiek krok się nie powiedzie → ROLLBACK wszystkiego
        //     //    - Jeśli wszystkie kroki OK → COMMIT wszystkiego
        //     //    To jest ATOMOWOŚĆ (all-or-nothing)!
        //
        //     // Rozpocznij transakcję
        //     // conn.setAutoCommit(false);
        //
        //     // 🔴 BREAKPOINT 3: PO sprawdzeniu czy pirat istnieje
        //     // 👁️ OBSERWUJ w Variables:
        //     //    - rs.getLong(1) - COUNT(*) z zapytania (0 = nie istnieje, 1 = istnieje)
        //     // 💡 ZADANIE: Sprawdź w Evaluate: rs.getLong(1)
        //     // 💡 KLUCZOWA OBSERWACJA: Walidacja PRZED UPDATE
        //     //    - Jeśli pirat nie istnieje → rzuć SQLException
        //     //    - SQLException spowoduje ROLLBACK (w bloku catch)
        //
        //     // Sprawdź czy pirat istnieje (jeśli COUNT = 0, rzuć SQLException)
        //
        //     // 🔴 BREAKPOINT 4: PO sprawdzeniu czy statek istnieje
        //     // 👁️ OBSERWUJ w Variables:
        //     //    - rs.getLong(1) - COUNT(*) dla statku
        //     // 💡 EKSPERYMENT: Jeśli newShipId = 999 (nieistniejący statek):
        //     //    - COUNT będzie 0
        //     //    - Zostanie rzucony SQLException
        //     //    - Transakcja zostanie wycofana (ROLLBACK)
        //     //    - Metoda zwróci false
        //
        //     // Sprawdź czy statek istnieje (jeśli COUNT = 0, rzuć SQLException)
        //
        //     // 🔴 BREAKPOINT 5: PRZED executeUpdate()
        //     // 👁️ OBSERWUJ w Variables:
        //     //    - stmt - PreparedStatement z UPDATE
        //     //    - Parametry: ship_id = newShipId, id = pirateId
        //     // 💡 KLUCZOWA OBSERWACJA: Walidacja przeszła pomyślnie!
        //     //    - Pirat istnieje
        //     //    - Statek istnieje
        //     //    - Możemy bezpiecznie wykonać UPDATE
        //
        //     // Wykonaj UPDATE
        //
        //     // 🔴 BREAKPOINT 6: PRZED commit()
        //     // 👁️ OBSERWUJ: UPDATE wykonany, ale NIE zatwierdzony
        //     // 💡 KLUCZOWA OBSERWACJA: Wszystkie kroki OK!
        //     //    - Walidacja pirata: ✅
        //     //    - Walidacja statku: ✅
        //     //    - UPDATE wykonany: ✅
        //     //    - Teraz możemy zatwierdzić CAŁĄ transakcję
        //     // 💡 ATOMOWOŚĆ: Albo wszystko (commit), albo nic (rollback)
        //
        //     // Zatwierdź transakcję
        //     // conn.commit();
        //     // System.out.println("   ✅ COMMIT - transfer zakończony pomyślnie");
        //
        //     // Zwróć true
        //     // return true;
        //
        // } catch (SQLException e) {
        //     // 🔴 BREAKPOINT 7: W bloku catch (tylko jeśli błąd)
        //     // 👁️ OBSERWUJ w Variables:
        //     //    - e.getMessage() - komunikat błędu (np. "Statek o ID=999 nie istnieje!")
        //     // 💡 KLUCZOWA OBSERWACJA: Błąd w KTÓRYMKOLWIEK kroku!
        //     //    - Może być błąd walidacji (pirat/statek nie istnieje)
        //     //    - Może być błąd SQL (constraint violation)
        //     //    - ROLLBACK cofa WSZYSTKIE zmiany (nawet jeśli UPDATE się wykonał)
        //     // 💡 ATOMOWOŚĆ: Jeśli cokolwiek się nie powiedzie → cofamy WSZYSTKO
        //
        //     // Wypisz błąd
        //     // System.out.println("   ⚠️ BŁĄD: " + e.getMessage());
        //
        //     // Wycofaj transakcję
        //     // try {
        //     //     conn.rollback();
        //     //     System.out.println("   ↩️ ROLLBACK - transfer anulowany");
        //     // } catch (SQLException rollbackEx) {
        //     //     System.err.println("Błąd podczas rollback: " + rollbackEx.getMessage());
        //     // }
        //
        //     // Zwróć false
        //     // return false;
        //
        // } finally {
        //     // 🔴 BREAKPOINT 8: W bloku finally (ZAWSZE)
        //     // 👁️ OBSERWUJ: Ten blok wykona się ZAWSZE
        //     // 💡 KLUCZOWA OBSERWACJA: Przywracanie stanu początkowego
        //     //    - finally wykonuje się ZAWSZE (sukces lub błąd)
        //     //    - Przywracamy autoCommit = true
        //     //    - Następne operacje będą działać normalnie (bez transakcji)
        //
        //     // Przywróć auto-commit
        //     // try {
        //     //     conn.setAutoCommit(true);
        //     // } catch (SQLException e) {
        //     //     System.err.println("Błąd przy przywracaniu auto-commit: " + e.getMessage());
        //     // }
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
