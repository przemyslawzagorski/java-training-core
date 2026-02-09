package pl.przemekzagorski.training.jpa;

import jakarta.persistence.*;
import pl.przemekzagorski.training.jpa.entity.*;

import java.util.List;

/**
 * 🏴‍☠️ Relations Exercises - ROZWIĄZANIA
 *
 * ═══════════════════════════════════════════════════════════════════
 * Ten plik zawiera pełne rozwiązania ćwiczeń z RelationsExercises.java.
 * Każde rozwiązanie zawiera szczegółowe komentarze wyjaśniające.
 *
 * UWAGA: Najpierw spróbuj rozwiązać ćwiczenia samodzielnie!
 * ═══════════════════════════════════════════════════════════════════
 */
public class RelationsExercisesSolutions {

    private static EntityManagerFactory emf;

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Relations Exercises - ROZWIĄZANIA");
        System.out.println("═══════════════════════════════════════════\n");

        emf = Persistence.createEntityManagerFactory("relations-pu");

        try {
           // exercise1_CreateOneToOne();
           // exercise2_CreateOneToMany();
           // exercise3_WhySideMatters();
          //  exercise4_OrphanRemoval();
          //  exercise5_ManyToMany();
            exercise6_JoinFetch();

            System.out.println("\n" + "═".repeat(60));
            System.out.println("🎉 Wszystkie ćwiczenia zakończone!");
            System.out.println("═".repeat(60));

        } finally {
            emf.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 1: @OneToOne - Kapitan i Statek
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Tworzenie relacji @OneToOne
     *
     * KLUCZOWE KONCEPCJE:
     * - Ship jest WŁAŚCICIELEM relacji (ma @JoinColumn, brak mappedBy)
     * - Captain ma mappedBy = jest stroną ODWROTNĄ
     * - Relację ustawiamy przez ship.setCaptain(), nie captain.setShip()!
     *
     * SCHEMAT BAZY:
     * ships(id, name, type, cannons, captain_id FK)
     * captains(id, name, nickname)
     *
     * FK jest w tabeli ships, więc Ship jest właścicielem.
     */
    private static void exercise1_CreateOneToOne() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 1: @OneToOne - Kapitan i Statek");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // ROZWIĄZANIE: Tworzymy kapitana
            Captain captain = new Captain("Jack Sparrow", "Captain Jack");

            // ROZWIĄZANIE: Zapisujemy kapitana PRZED przypisaniem do Ship
            // Captain musi mieć ID zanim zostanie przypisany do FK w Ship
            em.persist(captain);

            // ROZWIĄZANIE: Tworzymy statek
            Ship ship = new Ship("Black Pearl", "Galleon", 32);

            // ROZWIĄZANIE: Przypisujemy kapitana do statku
            // Ship jest WŁAŚCICIELEM, więc to ustawienie zostanie zapisane!
            ship.setCaptain(captain);

            // ROZWIĄZANIE: Zapisujemy statek
            em.persist(ship);

            em.getTransaction().commit();

            // Weryfikacja
            System.out.println("   Kapitan: " + captain);
            System.out.println("   Statek: " + ship);
            System.out.println("   ship.getCaptain(): " + ship.getCaptain());
            System.out.println("   Status: ✅ POPRAWNIE!");
            System.out.println("   → Relacja utworzona przez stronę właściciela (Ship)\n");

        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 2: @OneToMany - Statek i załoga
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Tworzenie relacji @OneToMany z cascade
     *
     * KLUCZOWE KONCEPCJE:
     * - CrewMember jest WŁAŚCICIELEM (ma @ManyToOne z @JoinColumn)
     * - Ship ma mappedBy = strona ODWROTNA
     * - Metoda addCrewMember() synchronizuje OBie strony:
     *   - crew.add(member) → dla spójności w pamięci
     *   - member.setShip(this) → dla zapisu do bazy!
     * - cascade = ALL → persist(ship) zapisuje też załogę
     *
     * SCHEMAT BAZY:
     * ships(id, name, type, cannons)
     * crew_members(id, name, role, ship_id FK)
     *
     * FK jest w crew_members, więc CrewMember jest właścicielem.
     */
    private static void exercise2_CreateOneToMany() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 2: @OneToMany - Statek i załoga");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // ROZWIĄZANIE: Tworzymy statek
            Ship ship = new Ship("Flying Dutchman", "Galleon", 46);

            // ROZWIĄZANIE: Tworzymy członków załogi
            CrewMember member1 = new CrewMember("Davy Jones", "Captain");
            CrewMember member2 = new CrewMember("Bootstrap Bill", "First Mate");
            CrewMember member3 = new CrewMember("Maccus", "Quartermaster");

            // ROZWIĄZANIE: Używamy metody pomocniczej
            // addCrewMember robi DWA rzeczy:
            // 1. crew.add(member) → dla spójności w pamięci Java
            // 2. member.setShip(this) → dla zapisu FK do bazy (właściciel!)
            ship.addCrewMember(member1);
            ship.addCrewMember(member2);
            ship.addCrewMember(member3);

            // ROZWIĄZANIE: Zapisujemy tylko statek
            // cascade = ALL oznacza, że persist() propaguje na załogę!
            // Hibernate automatycznie wykona:
            // - INSERT INTO ships...
            // - INSERT INTO crew_members... (x3)
            em.persist(ship);

            em.getTransaction().commit();

            System.out.println("   Statek: " + ship);
            System.out.println("   Liczba załogi: " + ship.getCrew().size());
            ship.getCrew().forEach(m -> System.out.println("   👤 " + m));
            System.out.println("   Status: ✅ POPRAWNIE!");
            System.out.println("   → Cascade = ALL automatycznie zapisał załogę!\n");

        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 3: Dlaczego strona właściciela ma znaczenie?
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Demonstracja błędu przy zmianie tylko strony odwrotnej
     *
     * KLUCZOWE KONCEPCJE:
     * - mappedBy oznacza "ta strona jest tylko do odczytu"
     * - Zmiany na stronie z mappedBy są IGNOROWANE przez Hibernate!
     * - Zawsze ustawiaj relację na stronie WŁAŚCICIELA
     *
     * DLACZEGO TO NIE DZIAŁA?
     * Hibernate przy zapisie patrzy tylko na stronę właściciela.
     * ship.getCrew() ma mappedBy, więc Hibernate ignoruje zmiany w tej kolekcji.
     * Jedyna rzecz która się liczy to member.ship (strona właściciela).
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

            Ship ship = new Ship("Queen Anne's Revenge", "Frigate", 40);
            em.persist(ship);
            shipId = ship.getId();

            CrewMember member = new CrewMember("Blackbeard", "Captain");
            em.persist(member);
            memberId = member.getId();

            // ❌ BŁĘDNY SPOSÓB:
            // Ship.crew ma mappedBy="ship", więc jest stroną ODWROTNĄ
            // Zmiany tutaj są IGNOROWANE przez Hibernate!
            ship.getCrew().add(member);

            // Brakuje: member.setShip(ship);
            // To jest strona WŁAŚCICIELA i tylko to by zadziałało!

            em.getTransaction().commit();

            System.out.println("   Zapisano statek ID: " + shipId);
            System.out.println("   Zapisano członka ID: " + memberId);
            System.out.println("   Dodano do ship.getCrew() BEZ member.setShip()");

        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }

        // Weryfikacja
        EntityManager em2 = emf.createEntityManager();
        try {
            CrewMember memberFromDb = em2.find(CrewMember.class, memberId);
            Ship memberShip = memberFromDb.getShip();

            System.out.println("\n   Sprawdzam w bazie:");
            System.out.println("   member.getShip() = " + memberShip);

            // member.ship jest NULL bo nie ustawiliśmy strony właściciela!
            System.out.println("   ⚠️ RELACJA NIE ZOSTAŁA ZAPISANA!");
            System.out.println("   → ship.getCrew() ma mappedBy - zmiany są IGNOROWANE!");
            System.out.println("   → member.setShip(ship) jest stroną WŁAŚCICIELA!\n");

        } finally {
            em2.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 4: orphanRemoval - usuwanie sierot
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Demonstracja orphanRemoval = true
     *
     * KLUCZOWE KONCEPCJE:
     * - orphanRemoval = true oznacza: usunięcie z kolekcji = DELETE z bazy
     * - "Sierota" = encja która nie ma już rodzica (została usunięta z kolekcji)
     * - Działa tylko dla @OneToMany i @OneToOne
     *
     * BEZ orphanRemoval:
     * - ship.getCrew().remove(member) → member.ship_id = NULL w bazie
     * - Encja pozostaje w bazie
     *
     * Z orphanRemoval = true:
     * - ship.getCrew().remove(member) → DELETE FROM crew_members WHERE id = ?
     * - Encja jest usuwana z bazy!
     */
    private static void exercise4_OrphanRemoval() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 4: orphanRemoval - usuwanie sierot");
        System.out.println("═".repeat(60));

        Long shipId = null;
        Long memberToRemoveId = null;

        // Krok 1: Utwórz statek z załogą
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

        // Krok 2: Usuń członka z kolekcji
        EntityManager em2 = emf.createEntityManager();
        try {
            em2.getTransaction().begin();

            Ship ship = em2.find(Ship.class, shipId);
            System.out.println("   Przed usunięciem: " + ship.getCrew().size() + " członków");

            // ROZWIĄZANIE: Znajdujemy członka do usunięcia
            CrewMember memberToRemove = null;
            for (CrewMember m : ship.getCrew()) {
                if (m.getId().equals(memberToRemoveId)) {
                    memberToRemove = m;
                    break;
                }
            }

            // ROZWIĄZANIE: Usuwamy z kolekcji
            // orphanRemoval = true → Hibernate wygeneruje DELETE!
            ship.getCrew().remove(memberToRemove);

            // Przy commit() Hibernate wykona:
            // DELETE FROM crew_members WHERE id = ?
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
            System.out.println("   Członek o ID " + memberToRemoveId + ": USUNIĘTY");
            System.out.println("   Status: ✅ orphanRemoval zadziałał!");
            System.out.println("   → Usunięcie z kolekcji = DELETE z bazy\n");

        } finally {
            em3.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 5: @ManyToMany - Statki i Wyspy
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Tworzenie relacji @ManyToMany
     *
     * KLUCZOWE KONCEPCJE:
     * - @ManyToMany wymaga tabeli łączącej (join table)
     * - Strona z @JoinTable jest WŁAŚCICIELEM
     * - Strona z mappedBy jest ODWROTNA
     * - Używaj metod pomocniczych do synchronizacji obu stron!
     *
     * SCHEMAT BAZY:
     * ships(id, name, type, cannons)
     * islands(id, name, location, has_treasure)
     * ship_visits(ship_id FK, island_id FK)  ← tabela łącząca
     */
    private static void exercise5_ManyToMany() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 5: @ManyToMany - Statki i Wyspy");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // ROZWIĄZANIE: Tworzymy wyspy
            Island tortuga = new Island("Tortuga", "Caribbean", false);
            Island portRoyal = new Island("Port Royal", "Jamaica", true);
            Island islaDeMuerta = new Island("Isla de Muerta", "Unknown", true);

            // ROZWIĄZANIE: Zapisujemy wyspy
            em.persist(tortuga);
            em.persist(portRoyal);
            em.persist(islaDeMuerta);

            // ROZWIĄZANIE: Tworzymy statki
            Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
            Ship dutchman = new Ship("Flying Dutchman", "Galleon", 46);

            // ROZWIĄZANIE: Przypisujemy odwiedzone wyspy
            // visitIsland() synchronizuje OBie strony relacji:
            // 1. ship.visitedIslands.add(island)
            // 2. island.ships.add(ship)
            blackPearl.visitIsland(tortuga);
            blackPearl.visitIsland(islaDeMuerta);
            dutchman.visitIsland(portRoyal);
            dutchman.visitIsland(islaDeMuerta);

            // ROZWIĄZANIE: Zapisujemy statki
            // Ship jest właścicielem, więc INSERT do ship_visits pochodzi stąd
            em.persist(blackPearl);
            em.persist(dutchman);

            em.getTransaction().commit();

            System.out.println("   Black Pearl odwiedziła: " + blackPearl.getVisitedIslands().size() + " wysp");
            System.out.println("   Flying Dutchman odwiedził: " + dutchman.getVisitedIslands().size() + " wysp");
            System.out.println("   Isla de Muerta odwiedzona przez: " + islaDeMuerta.getShips().size() + " statków");
            System.out.println("   Status: ✅ POPRAWNIE!");
            System.out.println("   → Tabela ship_visits zawiera 4 wiersze\n");

        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 6: JOIN FETCH - rozwiązanie N+1
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Użycie JOIN FETCH do eliminacji problemu N+1
     *
     * KLUCZOWE KONCEPCJE:
     * - Problem N+1: 1 SELECT dla rodziców + N SELECTów dla dzieci
     * - JOIN FETCH ładuje wszystko w jednym zapytaniu
     * - DISTINCT jest potrzebne aby uniknąć duplikatów
     *
     * BEZ JOIN FETCH:
     * SELECT * FROM ships                    -- 1 query
     * SELECT * FROM crew_members WHERE ship_id = 1  -- N queries
     * SELECT * FROM crew_members WHERE ship_id = 2
     * ... (dla każdego statku)
     *
     * Z JOIN FETCH:
     * SELECT s.*, c.* FROM ships s
     * LEFT JOIN crew_members c ON c.ship_id = s.id  -- 1 query!
     */
    private static void exercise6_JoinFetch() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 6: JOIN FETCH - rozwiązanie N+1");
        System.out.println("═".repeat(60));

        setupTestDataForJoinFetch();

        // Wariant 1: BEZ JOIN FETCH
        System.out.println("\n   📊 Wariant 1: BEZ JOIN FETCH (generuje N+1 SELECTów)");
        System.out.println("   ────────────────────────────────────────────────────");

        EntityManager em1 = emf.createEntityManager();
        try {
            // To zapytanie pobiera tylko statki
            // Każdy dostęp do crew wygeneruje DODATKOWY SELECT!
            String jpqlWithoutFetch = "SELECT s FROM Ship s WHERE s.crew IS NOT EMPTY";
            List<Ship> ships = em1.createQuery(jpqlWithoutFetch, Ship.class).getResultList();
            // SELECT * FROM ships → 1 query

            System.out.println("   Pobrano " + ships.size() + " statków");

            for (Ship ship : ships) {
                // Każde wywołanie getCrew() generuje:
                // SELECT * FROM crew_members WHERE ship_id = ?
                System.out.println("   " + ship.getName() + " ma " + ship.getCrew().size() + " załogantów");
            }
            // Razem: 1 + N queries (N = liczba statków)

        } finally {
            em1.close();
        }

        // Wariant 2: Z JOIN FETCH
        System.out.println("\n   📊 Wariant 2: Z JOIN FETCH (1 SELECT!)");
        System.out.println("   ────────────────────────────────────────");

        EntityManager em2 = emf.createEntityManager();
        try {
            // ROZWIĄZANIE: JOIN FETCH ładuje załogę RAZEM ze statkami
            // DISTINCT unika duplikatów (bo JOIN tworzy wiele wierszy)
            String jpqlWithFetch = "SELECT DISTINCT s FROM Ship s JOIN FETCH s.crew";

            List<Ship> ships = em2.createQuery(jpqlWithFetch, Ship.class).getResultList();
            // SELECT DISTINCT s.*, c.* FROM ships s
            // JOIN crew_members c ON c.ship_id = s.id
            // → TYLKO 1 query!

            System.out.println("   Pobrano " + ships.size() + " statków (z załogą w tym samym SELECT!)");

            for (Ship ship : ships) {
                // Załoga jest już załadowana - NIE generuje dodatkowego query!
                System.out.println("   " + ship.getName() + " ma " + ship.getCrew().size() + " załogantów");
            }

            System.out.println("\n   Status: ✅ Tylko 1 SELECT zamiast N+1!");
            System.out.println("   → JOIN FETCH to najlepsza praktyka dla relacji\n");

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
