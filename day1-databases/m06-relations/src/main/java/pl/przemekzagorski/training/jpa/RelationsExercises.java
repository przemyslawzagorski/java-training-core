package pl.przemekzagorski.training.jpa;

import jakarta.persistence.*;
import pl.przemekzagorski.training.jpa.entity.*;

import java.util.List;
import java.util.Set;

/**
 * 🏴‍☠️ Ćwiczenia: Relacje JPA
 *
 * ═══════════════════════════════════════════════════════════════════
 * INSTRUKCJA DLA KURSANTA:
 * ═══════════════════════════════════════════════════════════════════
 *
 * Te ćwiczenia sprawdzą Twoje rozumienie relacji JPA:
 * - @OneToOne
 * - @OneToMany / @ManyToOne
 * - @ManyToMany
 * - Strona właściciela vs strona odwrotna
 * - Cascade i orphanRemoval
 *
 * KLUCZOWA ZASADA:
 * Relację zapisujesz TYLKO przez stronę WŁAŚCICIELA (bez mappedBy)!
 * Zmiany na stronie odwrotnej (z mappedBy) są IGNOROWANE!
 *
 * ═══════════════════════════════════════════════════════════════════
 */
public class RelationsExercises {

    private static EntityManagerFactory emf;

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Relations Exercises - Ćwiczenia");
        System.out.println("══════════════════════════════════════\n");

        emf = Persistence.createEntityManagerFactory("relations-pu");

        try {
            exercise1_CreateOneToOne();
            exercise2_CreateOneToMany();
            exercise3_WhySideMatters();
            exercise4_OrphanRemoval();
            exercise5_ManyToMany();
            exercise6_JoinFetch();

            System.out.println("\n" + "═".repeat(60));
            System.out.println("🎉 Wszystkie ćwiczenia wykonane!");
            System.out.println("═".repeat(60));

        } finally {
            emf.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 1: @OneToOne - Kapitan i Statek
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * 🎯 POZIOM: ŁATWY (szkielet kodu)
     *
     * Utwórz kapitana i przypisz mu statek.
     *
     * KLUCZOWA ZASADA:
     * - Ship jest WŁAŚCICIELEM relacji (ma @JoinColumn)
     * - Captain ma mappedBy = strona odwrotna
     * - Relację ustawiasz na Ship, NIE na Captain!
     *
     * Kroki do wykonania:
     * 1. Uzupełnij parametry konstruktora Captain
     * 2. Zapisz kapitana (musi mieć ID przed przypisaniem)
     * 3. Uzupełnij parametry konstruktora Ship
     * 4. Przypisz kapitana do statku
     * 5. Zapisz statek
     *
     * 💡 WSKAZÓWKI:
     * - Captain(String name, String nickname)
     * - Ship(String name, String type, int cannons)
     * - ship.setCaptain(captain) - Ship jest właścicielem!
     * - em.persist() zapisuje encję
     *
     * 🆘 Jeśli utkniesz, sprawdź RelationsExercisesSolutions.java
     */
    private static void exercise1_CreateOneToOne() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 1: @OneToOne - Kapitan i Statek");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // TODO 1: Utwórz kapitana
            Captain captain = new Captain(/* name */ "Jack Sparrow", /* nickname */ "Captain Jack");

            // TODO 2: Zapisz kapitana (musi mieć ID przed przypisaniem do Ship)
            em.persist(/* encja */ captain);

            // TODO 3: Utwórz statek
            Ship ship = new Ship(/* name */ "Black Pearl", /* type */ "Galleon", /* cannons */ 32);

            // TODO 4: Przypisz kapitana do statku (Ship jest WŁAŚCICIELEM!)
            ship.setCaptain(/* kapitan */ captain);

            // TODO 5: Zapisz statek
            em.persist(/* encja */ ship);

            em.getTransaction().commit();

            // Weryfikacja
            boolean success = ship != null && captain != null &&
                              ship.getCaptain() != null &&
                              ship.getCaptain().getId().equals(captain.getId());

            System.out.println("   Kapitan: " + captain);
            System.out.println("   Statek: " + ship);
            System.out.println("   ship.getCaptain(): " + (ship != null ? ship.getCaptain() : "null"));
            System.out.println("   Status: " + (success ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));
            System.out.println("   → Relacja @OneToOne utworzona przez stronę właściciela (Ship)\n");

        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 2: @OneToMany - Statek i załoga
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * 🎯 POZIOM: ŁATWY (szkielet kodu)
     *
     * Dodaj członków załogi do statku.
     *
     * KLUCZOWA ZASADA:
     * - CrewMember jest WŁAŚCICIELEM (ma @ManyToOne bez mappedBy)
     * - Ship ma mappedBy = strona odwrotna
     * - Używaj metody pomocniczej ship.addCrewMember() - synchronizuje obie strony!
     *
     * Kroki do wykonania:
     * 1. Uzupełnij parametry konstruktora Ship
     * 2. Uzupełnij parametry konstruktorów CrewMember (3 osoby)
     * 3. Dodaj załogę używając addCrewMember()
     * 4. Zapisz statek (cascade zapisze załogę automatycznie)
     *
     * 💡 WSKAZÓWKI:
     * - Ship(String name, String type, int cannons)
     * - CrewMember(String name, String role)
     * - ship.addCrewMember(member) - metoda pomocnicza synchronizuje obie strony
     * - cascade = ALL → persist(ship) zapisze też załogę!
     *
     * 🆘 Jeśli utkniesz, sprawdź RelationsExercisesSolutions.java
     */
    private static void exercise2_CreateOneToMany() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 2: @OneToMany - Statek i załoga");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // TODO 1: Utwórz statek
            Ship ship = new Ship(/* name */ "Flying Dutchman", /* type */ "Galleon", /* cannons */ 46);

            // TODO 2: Utwórz członków załogi
            CrewMember member1 = new CrewMember(/* name */ "Davy Jones", /* role */ "Captain");
            CrewMember member2 = new CrewMember(/* name */ "Bootstrap Bill", /* role */ "First Mate");
            CrewMember member3 = new CrewMember(/* name */ "Maccus", /* role */ "Quartermaster");

            // TODO 3: Dodaj załogę używając metody pomocniczej
            ship.addCrewMember(/* member */ member1);
            ship.addCrewMember(/* member */ member2);
            ship.addCrewMember(/* member */ member3);

            // TODO 4: Zapisz statek - cascade = ALL zapisze też załogę!
            em.persist(/* encja */ ship);

            em.getTransaction().commit();

            // Weryfikacja
            int crewSize = ship != null && ship.getCrew() != null ? ship.getCrew().size() : 0;
            boolean success = crewSize == 3;

            System.out.println("   Statek: " + ship);
            System.out.println("   Liczba załogi: " + crewSize);
            if (ship != null && ship.getCrew() != null) {
                ship.getCrew().forEach(m -> System.out.println("   👤 " + m));
            }
            System.out.println("   Status: " + (success ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));
            System.out.println("   → Cascade = ALL automatycznie zapisał załogę!\n");

        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 3: Dlaczego strona właściciela ma znaczenie?
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * 🎯 POZIOM: ŚREDNI (demonstracja BŁĘDU)
     *
     * To ćwiczenie pokazuje BŁĄD, gdy zmieniasz tylko stronę odwrotną.
     *
     * ⚠️ UWAGA: To ćwiczenie CELOWO pokazuje błędne podejście!
     * Obserwuj co się stanie gdy zmienisz tylko stronę odwrotną.
     *
     * Zadanie:
     * 1. Kod jest już napisany - URUCHOM i OBSERWUJ
     * 2. Statek i członek załogi są zapisywane osobno
     * 3. Członek jest dodawany do ship.getCrew() (strona odwrotna)
     * 4. NIE wołamy member.setShip(ship) (strona właściciela)
     * 5. Sprawdź czy relacja została zapisana w bazie
     *
     * ❓ PYTANIE: Czy relacja zostanie zapisana?
     * 💡 WSKAZÓWKA: Ship ma mappedBy = strona odwrotna (tylko odczyt!)
     * 💡 WSKAZÓWKA: CrewMember ma @ManyToOne = strona właściciela (zarządza FK!)
     *
     * SPODZIEWANY WYNIK: Relacja NIE zostanie zapisana!
     *
     * 🆘 Po wykonaniu sprawdź RelationsExercisesSolutions.java dla wyjaśnienia
     */
    private static void exercise3_WhySideMatters() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 3: Dlaczego strona właściciela ma znaczenie?");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();
        Long shipId = null;
        Long memberId = null;

        try {
            em.getTransaction().begin();

            // Utwórz i zapisz statek
            Ship ship = new Ship("Queen Anne's Revenge", "Frigate", 40);
            em.persist(ship);
            shipId = ship.getId();

            // Utwórz i zapisz członka załogi BEZ ustawiania ship
            CrewMember member = new CrewMember("Blackbeard", "Captain");
            em.persist(member);
            memberId = member.getId();

            // 🔴 BREAKPOINT 1: PRZED dodaniem do kolekcji
            // 👁️ OBSERWUJ w Variables:
            //    - ship.crew - rozwiń kolekcję, powinna być pusta (size = 0)
            //    - member.ship = null (brak relacji)
            // 💡 ZADANIE: Sprawdź ship.getCrew().size() w Evaluate - powinno być 0
            // 💡 KLUCZOWA OBSERWACJA: Obie strony relacji są niezależne w pamięci
            //    - Ship ma kolekcję crew (strona odwrotna, mappedBy="ship")
            //    - CrewMember ma pole ship (strona właściciela, @ManyToOne)

            // ❌ BŁĘDNY SPOSÓB: dodajemy tylko do kolekcji (strona odwrotna!)
            ship.getCrew().add(member);
            // NIE wołamy member.setShip(ship)!

            // 🔴 BREAKPOINT 2: PO dodaniu do kolekcji, PRZED commit()
            // 👁️ OBSERWUJ w Variables:
            //    - ship.crew.size() = 1 (w pamięci Java!)
            //    - member.ship = null (wciąż null!)
            // 💡 ZADANIE: Sprawdź w Evaluate:
            //    - ship.getCrew().contains(member) - zwróci true (w pamięci)
            //    - member.getShip() - zwróci null (nie ustawione!)
            // 💡 KLUCZOWA OBSERWACJA: Zmiana tylko w pamięci Java!
            //    - Ship ma mappedBy = "ship" → strona ODWROTNA (tylko odczyt!)
            //    - CrewMember ma @ManyToOne → strona WŁAŚCICIELA (zarządza FK!)
            //    - Hibernate zapisuje relację TYLKO przez stronę właściciela!
            // 💡 PYTANIE: Czy relacja zostanie zapisana w bazie?
            //    Odpowiedź: NIE! Hibernate ignoruje zmiany na stronie odwrotnej!
            //    Kolumna crew_member.ship_id pozostanie NULL w bazie!

            em.getTransaction().commit();

            // 🔴 BREAKPOINT 3: PO commit()
            // 👁️ OBSERWUJ: Logi SQL w konsoli
            // 💡 ZADANIE: Sprawdź logi - NIE zobaczysz UPDATE dla crew_member.ship_id!
            //    Hibernate wykonał tylko INSERT dla ship i member, ale BEZ relacji
            // 💡 KLUCZOWA OBSERWACJA: Zmiana na stronie odwrotnej jest IGNOROWANA!

            System.out.println("   Zapisano statek ID: " + shipId);
            System.out.println("   Zapisano członka ID: " + memberId);
            System.out.println("   ❌ Dodano do ship.getCrew() BEZ member.setShip()");

        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }

        // Weryfikacja - sprawdź czy relacja została zapisana
        EntityManager em2 = emf.createEntityManager();
        try {
            // 🔴 BREAKPOINT 4: Po pobraniu z bazy
            CrewMember memberFromDb = em2.find(CrewMember.class, memberId);
            Ship memberShip = memberFromDb.getShip();

            // 👁️ OBSERWUJ w Variables:
            //    - memberFromDb.ship = null (relacja NIE została zapisana!)
            // 💡 KLUCZOWA OBSERWACJA: Relacja nie istnieje w bazie!
            //    - W pamięci Java (przed commit) ship.crew zawierał member
            //    - Ale w bazie crew_member.ship_id = NULL
            //    - Hibernate zapisuje relację TYLKO przez stronę właściciela!
            // 💡 ROZWIĄZANIE - 3 sposoby:
            //    1. Ustaw stronę właściciela: member.setShip(ship);
            //    2. Synchronizuj stronę odwrotną: ship.getCrew().add(member);
            //    3. LUB użyj metody pomocniczej: ship.addCrewMember(member);
            //       (ta metoda robi oba kroki automatycznie!)

            boolean relationSaved = memberShip != null;

            System.out.println("\n   Sprawdzam w bazie:");
            System.out.println("   member.getShip() = " + memberShip);
            System.out.println("   Relacja zapisana: " + relationSaved);

            if (!relationSaved) {
                System.out.println("   ⚠️ RELACJA NIE ZOSTAŁA ZAPISANA!");
                System.out.println("   → Zmiana na stronie odwrotnej (ship.getCrew()) jest IGNOROWANA!");
                System.out.println("   → Musisz ustawić member.setShip(ship) - stronę WŁAŚCICIELA!");
                System.out.println("\n   ✅ POPRAWNY SPOSÓB:");
                System.out.println("      ship.addCrewMember(member);  // Synchronizuje obie strony!");
                System.out.println("      // LUB");
                System.out.println("      member.setShip(ship);  // Ustawia stronę właściciela");
                System.out.println("      ship.getCrew().add(member);  // Synchronizuje stronę odwrotną\n");
            } else {
                System.out.println("   Status: ❌ Nieoczekiwane - relacja nie powinna być zapisana\n");
            }

        } finally {
            em2.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 4: orphanRemoval - usuwanie sierot
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * 🎯 POZIOM: ŚREDNI (struktura + TODO)
     *
     * Sprawdź działanie orphanRemoval = true.
     *
     * KLUCZOWA ZASADA:
     * orphanRemoval = true oznacza:
     * Usunięcie z kolekcji = DELETE z bazy!
     *
     * Zadanie:
     * 1. Statek z załogą jest już utworzony (Krok 1)
     * 2. Znajdź członka załogi do usunięcia (pętla for)
     * 3. Usuń go z kolekcji ship.getCrew().remove()
     * 4. Zatwierdź transakcję
     * 5. Sprawdź czy został usunięty z bazy
     *
     * 💡 WSKAZÓWKI:
     * - Iteruj po ship.getCrew() używając for-each
     * - Porównaj m.getId().equals(memberToRemoveId)
     * - ship.getCrew().remove(member) wywoła DELETE!
     * - orphanRemoval działa tylko na stronie @OneToMany
     *
     * 🆘 Jeśli utkniesz, sprawdź RelationsExercisesSolutions.java
     */
    private static void exercise4_OrphanRemoval() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 4: orphanRemoval - usuwanie sierot");
        System.out.println("═".repeat(60));

        Long shipId = null;
        Long memberToRemoveId = null;

        // Krok 1: Utwórz statek z załogą (gotowe)
        EntityManager em1 = emf.createEntityManager();
        try {
            em1.getTransaction().begin();

            Ship ship = new Ship("Interceptor", "Sloop", 16);
            ship.addCrewMember(new CrewMember("James Norrington", "Captain"));
            ship.addCrewMember(new CrewMember("Gillette", "First Mate"));
            em1.persist(ship);

            shipId = ship.getId();
            memberToRemoveId = ship.getCrew().get(0).getId();

            em1.getTransaction().commit();
            System.out.println("   Utworzono statek z " + ship.getCrew().size() + " członkami załogi");
            System.out.println("   ID członka do usunięcia: " + memberToRemoveId);

        } finally {
            if (em1.getTransaction().isActive()) em1.getTransaction().rollback();
            em1.close();
        }

        // Krok 2: Usuń członka z kolekcji (TODO - uzupełnij)
        EntityManager em2 = emf.createEntityManager();
        try {
            em2.getTransaction().begin();

            Ship ship = em2.find(Ship.class, shipId);
            System.out.println("   Przed usunięciem: " + ship.getCrew().size() + " członków");

            // TODO 1: Znajdź członka do usunięcia
            CrewMember memberToRemove = null;
            for (CrewMember m : ship.getCrew()) {
                if (m.getId().equals(memberToRemoveId)) {
                    memberToRemove = m;
                    break;
                }
            }

            // TODO 2: Usuń z kolekcji - orphanRemoval spowoduje DELETE!
            ship.getCrew().remove(memberToRemove);

            em2.getTransaction().commit();
            System.out.println("   Po usunięciu z kolekcji: " + ship.getCrew().size() + " członków");

        } finally {
            if (em2.getTransaction().isActive()) em2.getTransaction().rollback();
            em2.close();
        }

        // Weryfikacja
        EntityManager em3 = emf.createEntityManager();
        try {
            CrewMember deletedMember = em3.find(CrewMember.class, memberToRemoveId);
            boolean wasDeleted = deletedMember == null;

            System.out.println("\n   Weryfikacja w bazie:");
            System.out.println("   Członek o ID " + memberToRemoveId + ": " +
                    (wasDeleted ? "USUNIĘTY" : "wciąż istnieje"));
            System.out.println("   Status: " + (wasDeleted ? "✅ orphanRemoval zadziałał!" : "❌ Sprawdź rozwiązanie"));
            System.out.println("   → orphanRemoval = usunięcie z kolekcji = DELETE z bazy\n");

        } finally {
            em3.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 5: @ManyToMany - Statki i Wyspy
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * 🎯 POZIOM: TRUDNY (tylko wskazówki)
     *
     * Utwórz relację wiele-do-wielu między statkami a wyspami.
     *
     * KLUCZOWA ZASADA:
     * - Ship jest WŁAŚCICIELEM (@JoinTable) - zarządza tabelą łączącą
     * - Island ma mappedBy = strona odwrotna (tylko odczyt)
     * - Używaj ship.visitIsland(island) do synchronizacji obu stron!
     *
     * Zadanie:
     * Zaimplementuj relację @ManyToMany według poniższej struktury.
     *
     * STRUKTURA:
     * 1. Utwórz 3 wyspy:
     *    - Tortuga (Caribbean, nie jest przeklęta)
     *    - Port Royal (Jamaica, nie jest przeklęty)
     *    - Isla de Muerta (Unknown, jest przeklęta)
     *
     * 2. Zapisz wszystkie wyspy (em.persist)
     *
     * 3. Utwórz 2 statki:
     *    - Black Pearl (Galleon, 32 armaty)
     *    - Flying Dutchman (Galleon, 46 armat)
     *
     * 4. Przypisz odwiedzone wyspy:
     *    - Black Pearl → Tortuga, Isla de Muerta
     *    - Flying Dutchman → Port Royal, Isla de Muerta
     *
     * 5. Zapisz oba statki (em.persist)
     *
     * 💡 WSKAZÓWKI:
     * - Island(String name, String location, boolean cursed)
     * - Ship(String name, String type, int cannons)
     * - ship.visitIsland(island) - synchronizuje obie strony relacji
     * - Ship jest właścicielem - ma @JoinTable(name = "ship_visits")
     * - Relacja @ManyToMany tworzy tabelę łączącą w bazie
     * - Każdy statek może odwiedzić wiele wysp
     * - Każda wyspa może być odwiedzona przez wiele statków
     *
     * 🆘 Jeśli utkniesz, sprawdź RelationsExercisesSolutions.java
     */
    private static void exercise5_ManyToMany() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 5: @ManyToMany - Statki i Wyspy");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // TODO: Zaimplementuj relację @ManyToMany
            // Struktura opisana w JavaDoc powyżej

            Island tortuga = null;
            Island portRoyal = null;
            Island islaDeMuerta = null;
            Ship blackPearl = null;
            Ship dutchman = null;

            em.getTransaction().commit();

            // Weryfikacja
            int blackPearlIslands = blackPearl != null ? blackPearl.getVisitedIslands().size() : 0;
            int dutchmanIslands = dutchman != null ? dutchman.getVisitedIslands().size() : 0;
            int islaDeMuertaShips = islaDeMuerta != null ? islaDeMuerta.getShips().size() : 0;

            System.out.println("   Black Pearl odwiedziła: " + blackPearlIslands + " wysp");
            System.out.println("   Flying Dutchman odwiedził: " + dutchmanIslands + " wysp");
            System.out.println("   Isla de Muerta odwiedzona przez: " + islaDeMuertaShips + " statków");

            boolean success = blackPearlIslands == 2 && dutchmanIslands == 2 && islaDeMuertaShips == 2;
            System.out.println("   Status: " + (success ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));
            System.out.println("   → Relacja @ManyToMany z tabelą łączącą ship_visits\n");

        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 6: JOIN FETCH - rozwiązanie N+1
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * 🎯 POZIOM: TRUDNY (tylko wskazówki)
     *
     * Porównaj zwykłe zapytanie z JOIN FETCH.
     *
     * PROBLEM N+1 SELECT:
     * - 1 SELECT dla statków
     * - N SELECTów dla załogi każdego statku (po jednym dla każdego ship.getCrew())
     * - Razem: 1 + N zapytań do bazy!
     *
     * ROZWIĄZANIE:
     * JOIN FETCH ładuje relacje w jednym zapytaniu.
     *
     * Zadanie:
     * 1. Wariant 1 jest gotowy - pokazuje problem N+1
     * 2. Napraw Wariant 2 używając JOIN FETCH
     * 3. Porównaj liczbę SELECTów w logach SQL
     *
     * STRUKTURA:
     * - Wariant 1: Zwykłe zapytanie (gotowe)
     * - Wariant 2: Zmień zapytanie na JOIN FETCH (TODO)
     *
     * 💡 WSKAZÓWKI:
     * - JPQL: "SELECT DISTINCT s FROM Ship s JOIN FETCH s.crew"
     * - DISTINCT eliminuje duplikaty (jeden statek może mieć wielu załogantów)
     * - JOIN FETCH ładuje ship + crew w jednym SELECT
     * - Bez JOIN FETCH: 1 SELECT dla ships + N SELECTów dla crew
     * - Z JOIN FETCH: 1 SELECT dla wszystkiego
     * - Obserwuj logi SQL w konsoli (Hibernate pokazuje wszystkie zapytania)
     * - FetchType.LAZY + dostęp do kolekcji = dodatkowy SELECT
     * - JOIN FETCH wymusza EAGER loading dla tego zapytania
     *
     * 🆘 Jeśli utkniesz, sprawdź RelationsExercisesSolutions.java
     */
    private static void exercise6_JoinFetch() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 6: JOIN FETCH - rozwiązanie N+1");
        System.out.println("═".repeat(60));

        // Najpierw utwórz dane testowe
        setupTestDataForJoinFetch();

        // Wariant 1: BEZ JOIN FETCH (gotowe - pokazuje problem)
        System.out.println("\n   📊 Wariant 1: BEZ JOIN FETCH");
        System.out.println("   ────────────────────────────");

        EntityManager em1 = emf.createEntityManager();
        try {
            // 🔴 BREAKPOINT 1: PRZED executeQuery
            // 👁️ OBSERWUJ: Zaraz wykona się SELECT dla statków
            // 💡 ZADANIE: Włącz logi SQL (hibernate.show_sql=true) i obserwuj konsolę
            // 💡 LICZNIK: Policz ile SELECT zostanie wykonanych

            // Proste zapytanie - każdy dostęp do crew wygeneruje dodatkowy SELECT
            String jpqlWithoutFetch = "SELECT s FROM Ship s WHERE s.crew IS NOT EMPTY";
            List<Ship> ships = em1.createQuery(jpqlWithoutFetch, Ship.class).getResultList();

            // 🔴 BREAKPOINT 2: PO executeQuery, PRZED pętlą
            // 👁️ OBSERWUJ: W konsoli zobaczysz 1 SELECT dla ships
            // 💡 LICZNIK: 1 zapytanie SQL (SELECT * FROM ship WHERE ...)
            // 💡 PYTANIE: Czy załoga jest już załadowana?
            //    Odpowiedź: NIE! FetchType.LAZY → załoga załaduje się przy dostępie
            // 💡 ZADANIE: Rozwiń ships[0] w Variables
            //    - ships[0].crew - zobaczysz PersistentBag (proxy Hibernate!)
            //    - To jest "leniwy" obiekt - dane załadują się przy pierwszym użyciu

            System.out.println("   Pobrano " + ships.size() + " statków");

            // 🔴 BREAKPOINT 3: PRZED pętlą for
            // 💡 KLUCZOWA OBSERWACJA: Zaraz zobaczymy PROBLEM N+1!
            //    - Mamy N statków (np. 3)
            //    - Każdy dostęp do getCrew() wygeneruje dodatkowy SELECT
            //    - Razem: 1 SELECT dla statków + N SELECT dla załóg = 1 + N zapytań!

            // Dostęp do załogi - tu generują się dodatkowe SELECTy!
            for (Ship ship : ships) {
                // 🔴 BREAKPOINT 4: Wewnątrz pętli, PRZED getCrew()
                // 👁️ OBSERWUJ: ship - pojedynczy statek
                // 💡 ZADANIE: Rozwiń ship.crew w Variables - zobaczysz PersistentBag (proxy!)

                System.out.println("   " + ship.getName() + " ma " + ship.getCrew().size() + " załogantów");

                // 🔴 BREAKPOINT 5: PO getCrew().size()
                // 👁️ OBSERWUJ: W konsoli zobaczysz dodatkowy SELECT dla załogi!
                //    Hibernate: select ... from crew_member where ship_id=?
                // 💡 KLUCZOWA OBSERWACJA: Każde wywołanie getCrew() generuje SELECT!
                //    To jest PROBLEM N+1:
                //    - 1 SELECT dla statków
                //    - N SELECTów dla załogi (po jednym dla każdego statku)
                //    - Razem: 1 + N zapytań!
                // 💡 LICZNIK: Jeśli mamy 3 statki → 1 + 3 = 4 zapytania SQL!
                //    Dla 100 statków → 1 + 100 = 101 zapytań! (KATASTROFA WYDAJNOŚCIOWA!)
            }

        } finally {
            em1.close();
        }

        // Wariant 2: Z JOIN FETCH (TODO - napraw zapytanie)
        System.out.println("\n   📊 Wariant 2: Z JOIN FETCH");
        System.out.println("   ────────────────────────────");

        EntityManager em2 = emf.createEntityManager();
        try {
            // 🔴 BREAKPOINT 6: PRZED executeQuery z JOIN FETCH
            // 👁️ OBSERWUJ: Zaraz wykona się SELECT z JOIN
            // 💡 ZADANIE: Obserwuj logi SQL - zobaczysz różnicę!

            // TODO: Zmień zapytanie na JOIN FETCH
            // Struktura: "SELECT DISTINCT s FROM Ship s JOIN FETCH s.crew"
            String jpqlWithFetch = "SELECT s FROM Ship s WHERE s.crew IS NOT EMPTY"; // <-- ZMIEŃ na JOIN FETCH

            List<Ship> ships = em2.createQuery(jpqlWithFetch, Ship.class).getResultList();

            // 🔴 BREAKPOINT 7: PO executeQuery z JOIN FETCH
            // 👁️ OBSERWUJ: W konsoli zobaczysz 1 SELECT z JOIN!
            //    SELECT s.*, c.* FROM ship s LEFT JOIN crew_member c ON s.id = c.ship_id
            // 💡 KLUCZOWA OBSERWACJA: Wszystko w JEDNYM zapytaniu!
            //    - Hibernate załadował statki I załogi w jednym SELECT
            //    - Użył LEFT JOIN aby pobrać wszystkie dane naraz
            // 💡 LICZNIK: 1 zapytanie SQL (zamiast 1 + N)
            // 💡 ZADANIE: Rozwiń ships[0] w Variables
            //    - ships[0].crew - NIE zobaczysz PersistentBag (proxy)!
            //    - Zobaczysz zwykłą listę z danymi - załoga jest już załadowana!

            System.out.println("   Pobrano " + ships.size() + " statków (z załogą w tym samym SELECT!)");

            // 🔴 BREAKPOINT 8: PRZED pętlą for
            // 💡 KLUCZOWA OBSERWACJA: Załoga jest już w pamięci!
            //    - JOIN FETCH załadował wszystko w jednym zapytaniu
            //    - Dostęp do getCrew() NIE wygeneruje dodatkowych SELECT

            for (Ship ship : ships) {
                System.out.println("   " + ship.getName() + " ma " + ship.getCrew().size() + " załogantów");

                // 🔴 BREAKPOINT 9: Wewnątrz pętli, PO getCrew()
                // 👁️ OBSERWUJ: W konsoli NIE MA dodatkowych SELECT!
                // 💡 KLUCZOWA OBSERWACJA: Załoga już jest w pamięci!
                //    - JOIN FETCH załadował wszystko w jednym zapytaniu
                //    - getCrew() zwraca dane z pamięci (bez SQL)
            }

            // 💡 PODSUMOWANIE:
            //    BEZ JOIN FETCH: 1 + N zapytań (np. 1 + 3 = 4)
            //    Z JOIN FETCH: 1 zapytanie
            //    Różnica: 4x mniej zapytań do bazy!
            //    Dla 100 statków: 101 vs 1 = 101x szybciej!

            System.out.println("\n   Status: Porównaj liczbę SELECTów w logach!");
            System.out.println("   → JOIN FETCH ładuje relacje w jednym zapytaniu\n");

        } finally {
            em2.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // METODY POMOCNICZE
    // ═══════════════════════════════════════════════════════════════════════

    private static void setupTestDataForJoinFetch() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Sprawdź czy są już dane
            Long count = em.createQuery("SELECT COUNT(s) FROM Ship s WHERE s.crew IS NOT EMPTY", Long.class)
                    .getSingleResult();

            if (count == 0) {
                Ship ship1 = new Ship("Test Ship 1", "Galleon", 20);
                ship1.addCrewMember(new CrewMember("Crew 1A", "Sailor"));
                ship1.addCrewMember(new CrewMember("Crew 1B", "Sailor"));
                em.persist(ship1);

                Ship ship2 = new Ship("Test Ship 2", "Frigate", 30);
                ship2.addCrewMember(new CrewMember("Crew 2A", "Sailor"));
                ship2.addCrewMember(new CrewMember("Crew 2B", "Sailor"));
                em.persist(ship2);

                Ship ship3 = new Ship("Test Ship 3", "Sloop", 10);
                ship3.addCrewMember(new CrewMember("Crew 3A", "Sailor"));
                em.persist(ship3);
            }

            em.getTransaction().commit();

        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }
}
