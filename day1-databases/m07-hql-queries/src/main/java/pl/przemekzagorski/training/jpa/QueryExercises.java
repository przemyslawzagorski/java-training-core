package pl.przemekzagorski.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import pl.przemekzagorski.training.jpa.dto.PirateDTO;
import pl.przemekzagorski.training.jpa.dto.ShipSummaryDTO;
import pl.przemekzagorski.training.jpa.entity.Island;
import pl.przemekzagorski.training.jpa.entity.Pirate;
import pl.przemekzagorski.training.jpa.entity.Ship;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

/**
 * ĆWICZENIA: Zapytania JPQL, Criteria API i Native SQL
 *
 * Instrukcje:
 * 1. Uzupełnij metody exercise1() - exercise6()
 * 2. Uruchom klasę i sprawdź wyniki
 * 3. Każde ćwiczenie ma oczekiwany wynik opisany w komentarzu
 *
 * Poziomy trudności:
 * ⭐ - łatwe (JPQL podstawy)
 * ⭐⭐ - średnie (JPQL zaawansowane)
 * ⭐⭐⭐ - trudne (Criteria API / Native SQL)
 */
public class QueryExercises {

    private static EntityManager em;

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("queries-pu");
        em = emf.createEntityManager();

        try {
            setupTestData();

            System.out.println("\n" + "=".repeat(60));
            System.out.println("ĆWICZENIA: ZAPYTANIA JPQL/HQL/CRITERIA/NATIVE");
            System.out.println("=".repeat(60));

            runExercise("1 ⭐ - Podstawowe JPQL WHERE", QueryExercises::exercise1);
            runExercise("2 ⭐ - JPQL z ORDER BY i LIMIT", QueryExercises::exercise2);
            runExercise("3 ⭐⭐ - JOIN FETCH", QueryExercises::exercise3);
            runExercise("4 ⭐⭐ - Projekcja SELECT NEW", QueryExercises::exercise4);
            runExercise("5 ⭐⭐⭐ - Agregacja GROUP BY HAVING", QueryExercises::exercise5);
            runExercise("6 ⭐⭐⭐ - Criteria API dynamiczne filtry", QueryExercises::exercise6);

        } finally {
            em.close();
            emf.close();
        }
    }

    private static void runExercise(String name, Runnable exercise) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("ĆWICZENIE " + name);
        System.out.println("-".repeat(50));
        try {
            exercise.run();
        } catch (Exception e) {
            System.out.println("❌ Błąd: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========================================================================
    // ĆWICZENIE 1 ⭐: Podstawowe JPQL z WHERE
    // ========================================================================
    /**
     * 🎯 POZIOM: ŁATWY (gotowy kod do uruchomienia)
     *
     * Znajdź wszystkich piratów z nagrodą (bounty) większą niż 50,000.
     * Posortuj malejąco po nagrodzie.
     *
     * KLUCZOWE ELEMENTY JPQL:
     * - SELECT p FROM Pirate p - wybiera encje Pirate (alias 'p')
     * - WHERE p.bounty > :minBounty - filtrowanie (parametr nazwany)
     * - ORDER BY p.bounty DESC - sortowanie malejące
     * - :minBounty - parametr nazwany (bezpieczny przed SQL injection)
     *
     * Zadanie:
     * 1. URUCHOM kod i obserwuj wynik
     * 2. Zmień :minBounty na inną wartość (np. 100000)
     * 3. Zmień ORDER BY DESC na ASC (rosnąco)
     * 4. Dodaj drugi warunek: AND p.rank = 'Captain'
     *
     * Oczekiwany wynik:
     * - Davy Jones: $500,000
     * - Jack Sparrow: $100,000
     * - Hector Barbossa: $80,000
     *
     * 💡 WSKAZÓWKI:
     * - JPQL operuje na ENCJACH (Pirate), nie tabelach (pirates)
     * - Używaj nazw pól z klasy Java (bounty), nie kolumn SQL (bounty_amount)
     * - Parametry nazwane (:nazwa) są bezpieczniejsze niż konkatenacja stringów
     *
     * 🆘 Porównaj z QueryExercisesSolutions.java dla wyjaśnień
     */
    private static void exercise1() {
        // Zapytanie JPQL - gotowe do uruchomienia
        String jpql = """
            SELECT p FROM Pirate p
            WHERE p.bounty > :minBounty
            ORDER BY p.bounty DESC
            """;

        List<Pirate> pirates = em.createQuery(jpql, Pirate.class)
                .setParameter("minBounty", new BigDecimal("50000"))
                .getResultList();

        System.out.println("📋 Piraci z bounty > 50,000:");
        pirates.forEach(p -> System.out.printf("  - %s: $%s%n", p.getName(), p.getBounty()));

        System.out.println("\n✅ Ćwiczenie 1 wykonane! Eksperymentuj ze zmianami w zapytaniu.");
    }

    // ========================================================================
    // ĆWICZENIE 2 ⭐: JPQL z ORDER BY i paginacją
    // ========================================================================
    /**
     * 🎯 POZIOM: ŁATWY (gotowy kod do uruchomienia)
     *
     * Znajdź 3 najlepiej opłacanych piratów (TOP 3).
     * Użyj paginacji (setFirstResult, setMaxResults).
     *
     * KLUCZOWE ELEMENTY PAGINACJI:
     * - setFirstResult(offset) - od którego rekordu zacząć (0 = pierwszy)
     * - setMaxResults(limit) - ile rekordów pobrać
     * - ORDER BY - sortowanie PRZED paginacją!
     *
     * Zadanie:
     * 1. URUCHOM kod i obserwuj wynik (TOP 3)
     * 2. Zmień setMaxResults(3) na setMaxResults(2) - TOP 2
     * 3. Zmień setFirstResult(0) na setFirstResult(3) - pomija TOP 3, pokazuje 4-6
     * 4. Oblicz: jak pobrać stronę 2 (rekordy 4-6)? Odpowiedź: setFirstResult(3), setMaxResults(3)
     *
     * Oczekiwany wynik:
     * 1. Davy Jones: $500,000
     * 2. Jack Sparrow: $100,000
     * 3. Hector Barbossa: $80,000
     *
     * 💡 WSKAZÓWKI:
     * - Paginacja: offset = (strona - 1) * rozmiar_strony
     * - Strona 1: setFirstResult(0), setMaxResults(10)
     * - Strona 2: setFirstResult(10), setMaxResults(10)
     * - Strona 3: setFirstResult(20), setMaxResults(10)
     * - ZAWSZE sortuj przed paginacją (ORDER BY)!
     *
     * 🆘 Porównaj z QueryExercisesSolutions.java dla wyjaśnień
     */
    private static void exercise2() {
        // Zapytanie JPQL - gotowe do uruchomienia
        String jpql = """
            SELECT p FROM Pirate p
            ORDER BY p.bounty DESC
            """;

        TypedQuery<Pirate> query = em.createQuery(jpql, Pirate.class);
        query.setFirstResult(0);  // offset: od którego rekordu (0 = pierwszy)
        query.setMaxResults(3);   // limit: ile rekordów pobrać

        List<Pirate> top3 = query.getResultList();

        System.out.println("📋 TOP 3 najdrożsi piraci:");
        int rank = 1;
        for (Pirate p : top3) {
            System.out.printf("  %d. %s: $%s%n", rank++, p.getName(), p.getBounty());
        }

        System.out.println("\n✅ Ćwiczenie 2 wykonane! Eksperymentuj z paginacją.");
    }

    // ========================================================================
    // ĆWICZENIE 3 ⭐⭐: JOIN FETCH - rozwiązanie N+1
    // ========================================================================
    /**
     * 🎯 POZIOM: ŚREDNI (struktura + TODO)
     *
     * Pobierz wszystkie statki wraz z ich załogą i portem macierzystym
     * w JEDNYM zapytaniu (unikaj problemu N+1).
     *
     * PROBLEM N+1 SELECT:
     * Bez JOIN FETCH:
     * - 1 SELECT dla statków
     * - N SELECTów dla załogi każdego statku (lazy loading!)
     * - M SELECTów dla portów każdego statku
     * Razem: 1 + N + M zapytań!
     *
     * ROZWIĄZANIE:
     * JOIN FETCH ładuje relacje w jednym zapytaniu.
     *
     * Zadanie:
     * 1. Kod jest gotowy - URUCHOM i obserwuj logi SQL
     * 2. Policz ile zapytań SELECT zostało wykonanych (powinno być 1!)
     * 3. Usuń jedno JOIN FETCH i zobacz różnicę w logach
     * 4. Dlaczego używamy DISTINCT? (JOIN tworzy duplikaty wierszy)
     *
     * Oczekiwany wynik (1 zapytanie SQL, nie N+1):
     * - Black Pearl (Tortuga): Jack Sparrow, Hector Barbossa, Joshamee Gibbs
     * - Flying Dutchman (Skull Island): Davy Jones, Maccus
     *
     * 💡 WSKAZÓWKI:
     * - LEFT JOIN FETCH s.crew - pobiera załogę w tym samym SELECT
     * - LEFT JOIN FETCH s.homePort - pobiera port w tym samym SELECT
     * - DISTINCT - usuwa duplikaty (JOIN może tworzyć wiele wierszy dla jednego statku)
     * - LEFT JOIN - pobiera statki nawet bez załogi/portu
     * - Bez FETCH byłoby lazy loading → N+1 problem!
     *
     * 🆘 Porównaj z QueryExercisesSolutions.java dla wyjaśnień
     */
    private static void exercise3() {
        // TODO: Uzupełnij zapytanie z JOIN FETCH
        String jpql = """
            SELECT DISTINCT s FROM Ship s
            LEFT JOIN FETCH s.crew
            LEFT JOIN FETCH s.homePort
            """;

        List<Ship> ships = em.createQuery(jpql, Ship.class).getResultList();

        System.out.println("📋 Statki z załogą (JOIN FETCH):");
        ships.forEach(s -> {
            System.out.printf("  🚢 %s (port: %s)%n",
                    s.getName(),
                    s.getHomePort() != null ? s.getHomePort().getName() : "brak");
            s.getCrew().forEach(p ->
                    System.out.printf("      👤 %s%n", p.getName()));
        });

        System.out.println("\n✅ Ćwiczenie 3 wykonane! Sprawdź logi SQL - ile zapytań?");
    }

    // ========================================================================
    // ĆWICZENIE 4 ⭐⭐: Projekcja SELECT NEW do DTO
    // ========================================================================
    /**
     * 🎯 POZIOM: ŚREDNI (struktura + TODO)
     *
     * Pobierz piratów jako DTO (PirateDTO) zamiast encji.
     * Tylko piraci z rangą "Captain".
     *
     * DLACZEGO DTO?
     * - Encja: Pobiera WSZYSTKIE kolumny + relacje (ciężkie!)
     * - DTO: Pobiera TYLKO potrzebne pola (lekkie, szybkie!)
     * - DTO: Immutable (record) - bezpieczne do przekazywania
     * - DTO: Nie jest zarządzane przez EntityManager (brak dirty checking)
     *
     * Zadanie:
     * 1. Kod jest gotowy - URUCHOM i obserwuj wynik
     * 2. Sprawdź PirateDTO.java - to record z 3 polami
     * 3. Zmień WHERE p.rank = 'Captain' na 'First Mate'
     * 4. Dodaj do SELECT: p.id (wymaga zmiany konstruktora DTO!)
     *
     * Oczekiwany wynik (obiekty PirateDTO, nie encje Pirate):
     * - Davy Jones (Captain): $500,000
     * - Jack Sparrow (Captain): $100,000
     * - Hector Barbossa (Captain): $80,000
     *
     * 💡 WSKAZÓWKI:
     * - SELECT NEW <pełna.nazwa.klasy>(...) - tworzy instancje DTO
     * - Argumenty muszą pasować do konstruktora DTO
     * - PirateDTO(String name, String rank, BigDecimal bounty)
     * - Kolejność argumentów MUSI się zgadzać!
     * - DTO to record - kompaktowy i immutable
     * - ZALETA: Pobieramy tylko 3 kolumny, nie całą encję
     *
     * 🆘 Porównaj z QueryExercisesSolutions.java dla wyjaśnień
     */
    private static void exercise4() {
        // 🔴 BREAKPOINT 1: PRZED utworzeniem zapytania
        // 👁️ OBSERWUJ: Zaraz wykonamy zapytanie z SELECT NEW (projekcja do DTO)
        // 💡 KLUCZOWA OBSERWACJA: DTO vs Encja - różnica w wydajności!
        //    - Encja (Pirate): Hibernate ładuje WSZYSTKIE kolumny + zarządza stanem
        //    - DTO (PirateDTO): Hibernate ładuje TYLKO wybrane kolumny, bez zarządzania stanem
        // 💡 PYTANIE: Kiedy używać DTO zamiast encji?
        //    Odpowiedź: Gdy potrzebujesz tylko ODCZYTU danych (bez modyfikacji)
        //    - Raporty, listy, eksport danych
        //    - API REST (zwracanie JSON)
        //    - Wydajność (mniej danych z bazy, brak dirty checking)

        // TODO: Uzupełnij zapytanie z SELECT NEW
        String jpql = """
            SELECT NEW pl.przemekzagorski.training.jpa.dto.PirateDTO(
                p.name, p.rank, p.bounty
            )
            FROM Pirate p
            WHERE p.rank = 'Captain'
            ORDER BY p.bounty DESC
            """;

        // 🔴 BREAKPOINT 2: PO utworzeniu zapytania, PRZED getResultList()
        // 👁️ OBSERWUJ w Variables:
        //    - jpql - zapytanie z SELECT NEW (pełna nazwa klasy DTO!)
        // 💡 ZADANIE: Sprawdź jpql w Evaluate - zobacz strukturę SELECT NEW
        // 💡 KLUCZOWA OBSERWACJA: SELECT NEW wymaga:
        //    - Pełnej nazwy klasy (pl.przemekzagorski.training.jpa.dto.PirateDTO)
        //    - Argumentów pasujących do konstruktora DTO
        //    - Kolejność argumentów musi się zgadzać!

        List<PirateDTO> captains = em.createQuery(jpql, PirateDTO.class)
                .getResultList();

        // 🔴 BREAKPOINT 3: PO getResultList()
        // 👁️ OBSERWUJ w Variables:
        //    - captains - lista obiektów PirateDTO (NIE Pirate!)
        //    - captains[0] - rozwińcie i zobacz pola: name, rank, bounty
        // 💡 ZADANIE: Sprawdź w Evaluate:
        //    - captains.get(0).getClass() - zwróci PirateDTO (nie Pirate!)
        //    - captains.get(0) instanceof PirateDTO - zwróci true
        //    - captains.get(0) instanceof Pirate - zwróci false
        // 💡 KLUCZOWA OBSERWACJA: DTO to zwykły obiekt Java!
        //    - NIE jest zarządzany przez EntityManager (em.contains() = false)
        //    - NIE ma dirty checking (zmiany nie są śledzone)
        //    - NIE ma lazy loading (wszystkie dane załadowane od razu)
        //    - To jest PROJEKCJA - tylko wybrane kolumny z bazy
        // 💡 WYDAJNOŚĆ: Porównaj SQL w logach:
        //    - SELECT NEW: SELECT p.name, p.rank, p.bounty FROM pirates...
        //    - Encja: SELECT p.* FROM pirates... (wszystkie kolumny!)
        //    Dla tabeli z 50 kolumnami różnica jest OGROMNA!

        System.out.println("📋 Kapitanowie jako DTO:");
        captains.forEach(dto ->
                System.out.printf("  ⚓ %s (%s): $%s%n",
                        dto.name(), dto.rank(), dto.bounty()));

        // 🔴 BREAKPOINT 4: Po wyświetleniu wyników
        // 💡 EKSPERYMENT: Porównaj z zapytaniem zwracającym encje:
        //    String jpqlEntity = "SELECT p FROM Pirate p WHERE p.rank = 'Captain'";
        //    List<Pirate> entities = em.createQuery(jpqlEntity, Pirate.class).getResultList();
        //    - entities[0] - zobaczysz WSZYSTKIE pola (ship, island, etc.)
        //    - em.contains(entities[0]) - zwróci true (zarządzane przez EM)
        //    - entities[0].setName("X") - zmiana będzie śledzona (dirty checking)
        // 💡 KLUCZOWA OBSERWACJA: Wybór DTO vs Encja zależy od przypadku użycia:
        //    - DTO: Odczyt danych, raporty, API (wydajność!)
        //    - Encja: Modyfikacja danych, logika biznesowa (zarządzanie stanem!)

        System.out.println("\n✅ Ćwiczenie 4 wykonane! DTO vs Encja - widzisz różnicę?");
    }

    // ========================================================================
    // ĆWICZENIE 5 ⭐⭐⭐: Agregacja GROUP BY z HAVING
    // ========================================================================
    /**
     * 🎯 POZIOM: TRUDNY (tylko wskazówki)
     *
     * Dla każdego statku policz liczbę członków załogi i sumę ich nagród.
     * Zwróć tylko statki z co najmniej 2 członkami załogi.
     *
     * KLUCZOWE POJĘCIA:
     * - GROUP BY - grupuje wiersze (tutaj: po statku)
     * - COUNT(p) - liczy piratów w każdej grupie
     * - SUM(p.bounty) - sumuje nagrody w każdej grupie
     * - HAVING - filtr PO agregacji (WHERE byłoby PRZED!)
     *
     * Zadanie:
     * Napisz zapytanie JPQL według poniższej struktury.
     *
     * STRUKTURA:
     * 1. SELECT NEW ShipSummaryDTO(s.name, s.type, COUNT(p), SUM(p.bounty))
     * 2. FROM Ship s LEFT JOIN s.crew p
     * 3. GROUP BY s.id, s.name, s.type
     * 4. HAVING COUNT(p) >= 2
     * 5. ORDER BY SUM(p.bounty) DESC
     *
     * Oczekiwany wynik:
     * - Flying Dutchman (Man-of-War): 2 piratów, $530,000
     * - Black Pearl (Galleon): 3 piratów, $200,000
     *
     * 💡 WSKAZÓWKI:
     * - ShipSummaryDTO(String shipName, String shipType, Long crewCount, BigDecimal totalBounty)
     * - LEFT JOIN s.crew p - łączy statki z załogą
     * - GROUP BY s.id, s.name, s.type - grupuje po statku (wszystkie pola nie-agregatowe!)
     * - COUNT(p) - liczy piratów, SUM(p.bounty) - sumuje nagrody
     * - HAVING COUNT(p) >= 2 - filtr PO agregacji (min. 2 piratów)
     * - WHERE vs HAVING:
     *   * WHERE - filtruje PRZED grupowaniem (np. WHERE p.rank = 'Captain')
     *   * HAVING - filtruje PO grupowaniu (np. HAVING COUNT(p) >= 2)
     * - Dlaczego LEFT JOIN? Bo chcemy statki nawet bez załogi (ale HAVING je odfiltruje)
     * - Dlaczego GROUP BY s.id, s.name, s.type? Bo wszystkie pola nie-agregatowe muszą być w GROUP BY!
     *
     * 🆘 Jeśli utkniesz, sprawdź QueryExercisesSolutions.java
     */
    private static void exercise5() {
        // 🔴 BREAKPOINT 1: PRZED utworzeniem zapytania
        // 👁️ OBSERWUJ: Zaraz wykonamy zapytanie z GROUP BY i HAVING (agregacja!)
        // 💡 KLUCZOWA OBSERWACJA: GROUP BY vs WHERE - różnica!
        //    - WHERE: filtruje POJEDYNCZE wiersze PRZED grupowaniem
        //      Przykład: WHERE p.rank = 'Captain' (tylko kapitanowie)
        //    - HAVING: filtruje GRUPY PO agregacji
        //      Przykład: HAVING COUNT(p) >= 2 (tylko statki z min. 2 piratami)
        // 💡 PYTANIE: Dlaczego nie możemy użyć WHERE COUNT(p) >= 2?
        //    Odpowiedź: COUNT() to funkcja agregująca - działa na GRUPACH, nie wierszach!
        //    WHERE działa PRZED grupowaniem, więc COUNT() jeszcze nie istnieje!

        // TODO: Napisz zapytanie z GROUP BY i HAVING
        String jpql = """
            SELECT NEW pl.przemekzagorski.training.jpa.dto.ShipSummaryDTO(
                s.name,
                s.type,
                COUNT(p),
                SUM(p.bounty)
            )
            FROM Ship s
            LEFT JOIN s.crew p
            GROUP BY s.id, s.name, s.type
            HAVING COUNT(p) >= 2
            ORDER BY SUM(p.bounty) DESC
            """;

        // 🔴 BREAKPOINT 2: PO utworzeniu zapytania, PRZED getResultList()
        // 👁️ OBSERWUJ w Variables:
        //    - jpql - zapytanie z GROUP BY, COUNT(), SUM(), HAVING
        // 💡 ZADANIE: Przeanalizuj strukturę zapytania:
        //    1. SELECT NEW ShipSummaryDTO(...) - projekcja do DTO
        //    2. COUNT(p) - liczy piratów w każdej grupie (statku)
        //    3. SUM(p.bounty) - sumuje nagrody w każdej grupie
        //    4. LEFT JOIN s.crew p - łączy statki z załogą (LEFT = statki bez załogi też!)
        //    5. GROUP BY s.id, s.name, s.type - grupuje po statku
        //    6. HAVING COUNT(p) >= 2 - filtruje grupy (min. 2 piratów)
        //    7. ORDER BY SUM(p.bounty) DESC - sortuje po sumie nagród
        // 💡 KLUCZOWA OBSERWACJA: GROUP BY wymaga WSZYSTKICH pól nie-agregatowych!
        //    - Pola agregatowe: COUNT(p), SUM(p.bounty) - obliczane dla grupy
        //    - Pola nie-agregatowe: s.id, s.name, s.type - muszą być w GROUP BY!
        //    - Dlaczego? Bo SQL musi wiedzieć jak grupować wiersze
        // 💡 PYTANIE: Dlaczego LEFT JOIN zamiast INNER JOIN?
        //    Odpowiedź: LEFT JOIN zwraca statki NAWET BEZ załogi
        //    (ale HAVING COUNT(p) >= 2 i tak je odfiltruje)

        List<ShipSummaryDTO> summaries = em.createQuery(jpql, ShipSummaryDTO.class)
                .getResultList();

        // 🔴 BREAKPOINT 3: PO getResultList()
        // 👁️ OBSERWUJ w Variables:
        //    - summaries - lista ShipSummaryDTO (statystyki statków)
        //    - summaries[0] - rozwińcie i zobacz:
        //      * shipName - nazwa statku (z GROUP BY)
        //      * shipType - typ statku (z GROUP BY)
        //      * crewCount - COUNT(p) - liczba piratów w grupie
        //      * totalBounty - SUM(p.bounty) - suma nagród w grupie
        // 💡 ZADANIE: Sprawdź w Evaluate:
        //    - summaries.size() - liczba statków (tylko te z >= 2 piratami!)
        //    - summaries.get(0).crewCount() - liczba piratów na pierwszym statku
        //    - summaries.get(0).totalBounty() - suma nagród na pierwszym statku
        // 💡 KLUCZOWA OBSERWACJA: Agregacja w SQL!
        //    - Hibernate wykonał JEDNO zapytanie SQL z GROUP BY
        //    - Baza danych policzyła COUNT() i SUM() (nie Java!)
        //    - To jest WYDAJNE - agregacja w bazie, nie w pamięci
        // 💡 WYDAJNOŚĆ: Porównaj z alternatywą w Javie:
        //    - Pobrać wszystkie statki: SELECT s FROM Ship s
        //    - Dla każdego statku pobrać załogę: s.getCrew() (N+1 problem!)
        //    - W Javie policzyć: crew.size(), crew.stream().map(p -> p.getBounty()).reduce(...)
        //    - To byłoby WOLNE i NIEEFEKTYWNE!
        //    GROUP BY robi to w JEDNYM zapytaniu SQL!

        System.out.println("📋 Statystyki statków (min. 2 piratów):");
        summaries.forEach(s ->
                System.out.printf("  🚢 %s (%s): %d piratów, $%s%n",
                        s.shipName(), s.shipType(), s.crewCount(), s.totalBounty()));

        // 🔴 BREAKPOINT 4: Po wyświetleniu wyników
        // 💡 EKSPERYMENT: Sprawdź logi SQL w konsoli:
        //    SELECT s.name, s.type, COUNT(p.id), SUM(p.bounty)
        //    FROM ship s LEFT JOIN pirate p ON s.id = p.ship_id
        //    GROUP BY s.id, s.name, s.type
        //    HAVING COUNT(p.id) >= 2
        //    ORDER BY SUM(p.bounty) DESC
        // 💡 KLUCZOWA OBSERWACJA: JPQL → SQL
        //    - JPQL operuje na encjach (Ship, Pirate)
        //    - Hibernate tłumaczy na SQL (ship, pirate)
        //    - GROUP BY, HAVING, COUNT, SUM - to standardowy SQL!
        // 💡 ZASTOSOWANIA GROUP BY:
        //    - Statystyki (liczba zamówień na klienta)
        //    - Raporty (suma sprzedaży na region)
        //    - Agregacje (średnia ocena na produkt)
        //    - Top N (10 najlepszych sprzedawców)

        System.out.println("\n✅ Ćwiczenie 5 wykonane! GROUP BY i HAVING - potężne narzędzie!");
    }

    // ========================================================================
    // ĆWICZENIE 6 ⭐⭐⭐: Criteria API - dynamiczne filtry
    // ========================================================================
    /**
     * 🎯 POZIOM: TRUDNY (tylko wskazówki)
     *
     * Zbuduj dynamiczne zapytanie używając Criteria API.
     * Filtruj piratów gdzie:
     * - nazwa zawiera "a" (case insensitive) ORAZ
     * - bounty >= 30,000
     *
     * Posortuj po bounty malejąco.
     *
     * DLACZEGO CRITERIA API?
     * - JPQL = statyczne zapytania (String)
     * - Criteria API = dynamiczne zapytania (type-safe!)
     * - Możesz budować zapytania warunkowo (if/else)
     * - Kompilator sprawdza poprawność (brak błędów w runtime)
     * - Idealne do filtrów w UI (użytkownik wybiera co filtrować)
     *
     * Zadanie:
     * Uzupełnij kod Criteria API według wskazówek poniżej.
     *
     * STRUKTURA:
     * 1. CriteriaBuilder cb - fabryka elementów zapytania (gotowe)
     * 2. CriteriaQuery<Pirate> cq - zapytanie zwracające Pirate (gotowe)
     * 3. Root<Pirate> pirate - odpowiednik FROM Pirate p (gotowe)
     * 4. Predicate nameLike - warunek LIKE (TODO)
     * 5. Predicate minBounty - warunek >= (TODO)
     * 6. cq.where(cb.and(...)) - łączenie warunków (TODO)
     * 7. cq.orderBy(cb.desc(...)) - sortowanie (TODO)
     * 8. em.createQuery(cq).getResultList() - wykonanie (TODO)
     *
     * Oczekiwany wynik:
     * - Davy Jones: $500,000 (zawiera 'a')
     * - Jack Sparrow: $100,000 (zawiera 'a')
     * - Hector Barbossa: $80,000 (zawiera 'a')
     * - Maccus: $30,000 (zawiera 'a')
     *
     * 💡 WSKAZÓWKI:
     * - cb.like(expression, pattern) - warunek LIKE
     * - cb.lower(expression) - konwersja do małych liter (LOWER)
     * - pirate.get("name") - dostęp do pola encji
     * - cb.greaterThanOrEqualTo(x, y) - warunek x >= y
     * - cb.and(predicate1, predicate2) - łączenie warunków przez AND
     * - cb.desc(expression) - sortowanie malejące (DESC)
     * - cq.where(predicate) - dodaje warunek WHERE
     * - cq.orderBy(order) - dodaje sortowanie ORDER BY
     *
     * ODPOWIEDNIK JPQL:
     * SELECT p FROM Pirate p
     * WHERE LOWER(p.name) LIKE '%a%' AND p.bounty >= 30000
     * ORDER BY p.bounty DESC
     *
     * 🆘 Jeśli utkniesz, sprawdź QueryExercisesSolutions.java
     */
    private static void exercise6() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pirate> cq = cb.createQuery(Pirate.class);
        Root<Pirate> pirate = cq.from(Pirate.class);

        // TODO: Dodaj warunki i sortowanie
        // Warunek 1: nazwa zawiera 'a' (case insensitive)
        Predicate nameLike = null; // cb.like(cb.lower(pirate.get("name")), "%a%");

        // Warunek 2: bounty >= 30,000
        Predicate minBounty = null; // cb.greaterThanOrEqualTo(pirate.get("bounty"), new BigDecimal("30000"));

        // Łączenie warunków przez AND
        // cq.where(cb.and(nameLike, minBounty));

        // Sortowanie malejące po bounty
        // cq.orderBy(cb.desc(pirate.get("bounty")));

        List<Pirate> results = em.createQuery(cq).getResultList();

        System.out.println("📋 Piraci (nazwa zawiera 'a', bounty >= 30,000):");
        results.forEach(p ->
                System.out.printf("  - %s: $%s%n", p.getName(), p.getBounty()));

        System.out.println("\n⏳ TODO: Uzupełnij Criteria API - odkomentuj i uzupełnij kod!");
    }

    // ========================================================================
    // DANE TESTOWE
    // ========================================================================
    private static void setupTestData() {
        em.getTransaction().begin();

        Island tortuga = new Island("Tortuga", "Caribbean", new BigDecimal("50000"));
        Island skullIsland = new Island("Skull Island", "Pacific", new BigDecimal("200000"));
        em.persist(tortuga);
        em.persist(skullIsland);

        Ship blackPearl = new Ship("Black Pearl", "Galleon", 40);
        blackPearl.setHomePort(tortuga);
        Ship flyingDutchman = new Ship("Flying Dutchman", "Man-of-War", 100);
        flyingDutchman.setHomePort(skullIsland);
        em.persist(blackPearl);
        em.persist(flyingDutchman);

        Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));
        jack.setShip(blackPearl);
        Pirate barbossa = new Pirate("Hector Barbossa", "Captain", new BigDecimal("80000"));
        barbossa.setShip(blackPearl);
        Pirate gibbs = new Pirate("Joshamee Gibbs", "First Mate", new BigDecimal("20000"));
        gibbs.setShip(blackPearl);
        Pirate davy = new Pirate("Davy Jones", "Captain", new BigDecimal("500000"));
        davy.setShip(flyingDutchman);
        Pirate maccus = new Pirate("Maccus", "First Mate", new BigDecimal("30000"));
        maccus.setShip(flyingDutchman);

        em.persist(jack);
        em.persist(barbossa);
        em.persist(gibbs);
        em.persist(davy);
        em.persist(maccus);

        em.getTransaction().commit();
        em.clear();

        System.out.println("✅ Dane testowe załadowane");
    }
}
