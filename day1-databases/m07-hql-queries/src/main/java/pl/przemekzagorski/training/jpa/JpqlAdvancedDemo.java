package pl.przemekzagorski.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import pl.przemekzagorski.training.jpa.dto.PirateDTO;
import pl.przemekzagorski.training.jpa.dto.ShipSummaryDTO;
import pl.przemekzagorski.training.jpa.entity.Island;
import pl.przemekzagorski.training.jpa.entity.Pirate;
import pl.przemekzagorski.training.jpa.entity.Ship;

import java.math.BigDecimal;
import java.util.List;

/**
 * Demonstracja zaawansowanych zapytań JPQL/HQL.
 *
 * Tematy:
 * 1. Projekcje (SELECT NEW) - zwracanie DTO zamiast encji
 * 2. Agregacje (COUNT, SUM, AVG, MIN, MAX)
 * 3. JOIN FETCH - rozwiązanie problemu N+1
 * 4. Podzapytania (subqueries)
 * 5. Wyrażenia CASE WHEN
 * 6. Named Queries - predefiniowane zapytania
 * 7. Paginacja
 */
public class JpqlAdvancedDemo {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("queries-pu");
        EntityManager em = emf.createEntityManager();

        try {
            // Przygotowanie danych testowych
            setupTestData(em);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("DEMO: ZAAWANSOWANE JPQL/HQL");
            System.out.println("=".repeat(60));

            // 1. Projekcje z SELECT NEW
            demonstrateProjections(em);

            // 2. Agregacje
            demonstrateAggregations(em);

            // 3. JOIN FETCH vs zwykły JOIN
            demonstrateJoinFetch(em);

            // 4. Named Queries
            demonstrateNamedQueries(em);

            // 5. Wyrażenia CASE WHEN
            demonstrateCaseWhen(em);

            // 6. Podzapytania
            demonstrateSubqueries(em);

            // 7. Paginacja
            demonstratePagination(em);

            // 8. Bulk operations
            demonstrateBulkOperations(em);

        } finally {
            em.close();
            emf.close();
        }
    }

    private static void setupTestData(EntityManager em) {
        em.getTransaction().begin();

        // Wyspy
        Island tortuga = new Island("Tortuga", "Caribbean", new BigDecimal("50000"));
        Island skullIsland = new Island("Skull Island", "Pacific", new BigDecimal("200000"));
        Island treasureIsland = new Island("Treasure Island", "Atlantic", new BigDecimal("500000"));

        em.persist(tortuga);
        em.persist(skullIsland);
        em.persist(treasureIsland);

        // Statki
        Ship blackPearl = new Ship("Black Pearl", "Galleon", 40);
        blackPearl.setHomePort(tortuga);

        Ship flyingDutchman = new Ship("Flying Dutchman", "Man-of-War", 100);
        flyingDutchman.setHomePort(skullIsland);

        Ship queenAnne = new Ship("Queen Anne's Revenge", "Frigate", 30);
        queenAnne.setHomePort(tortuga);

        em.persist(blackPearl);
        em.persist(flyingDutchman);
        em.persist(queenAnne);

        // Piraci
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

        Pirate blackbeard = new Pirate("Edward Teach", "Captain", new BigDecimal("300000"));
        blackbeard.setShip(queenAnne);

        // Pirat bez statku
        Pirate willTurner = new Pirate("Will Turner", "Blacksmith", new BigDecimal("5000"));

        em.persist(jack);
        em.persist(barbossa);
        em.persist(gibbs);
        em.persist(davy);
        em.persist(maccus);
        em.persist(blackbeard);
        em.persist(willTurner);

        em.getTransaction().commit();
        em.clear(); // Czyścimy cache pierwszego poziomu

        System.out.println("✅ Dane testowe zostały załadowane");
    }

    /**
     * 1. Projekcje z SELECT NEW - zwracanie DTO zamiast encji
     *
     * ZALETY:
     * - Tylko potrzebne kolumny (wydajność)
     * - Bezpieczeństwo (nie zwracamy wrażliwych danych)
     * - Czytelna separacja warstw
     */
    private static void demonstrateProjections(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("1. PROJEKCJE (SELECT NEW)");
        System.out.println("-".repeat(50));

        // Projekcja do DTO z SELECT NEW
        String jpql = """
            SELECT NEW pl.przemekzagorski.training.jpa.dto.PirateDTO(
                p.name, p.rank, p.bounty
            )
            FROM Pirate p
            WHERE p.bounty > :minBounty
            ORDER BY p.bounty DESC
            """;

        List<PirateDTO> pirates = em.createQuery(jpql, PirateDTO.class)
                .setParameter("minBounty", new BigDecimal("50000"))
                .getResultList();

        System.out.println("\n📋 Piraci z nagrodą > 50,000 (jako DTO):");
        pirates.forEach(p -> System.out.printf("  - %s (%s): $%s%n",
                p.name(), p.rank(), p.bounty()));

        // Projekcja do Tuple
        System.out.println("\n📋 Projekcja do Tuple:");
        List<Tuple> tuples = em.createQuery("""
            SELECT p.name as name, p.rank as rank, p.bounty as bounty
            FROM Pirate p
            WHERE p.rank = 'Captain'
            """, Tuple.class)
                .getResultList();

        tuples.forEach(t -> System.out.printf("  - %s (rank=%s, bounty=$%s)%n",
                t.get("name"), t.get("rank"), t.get("bounty")));
    }

    /**
     * 2. Funkcje agregujące
     */
    private static void demonstrateAggregations(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("2. AGREGACJE (COUNT, SUM, AVG, MIN, MAX)");
        System.out.println("-".repeat(50));

        // Podstawowe agregacje
        Object[] stats = em.createQuery("""
            SELECT 
                COUNT(p),
                SUM(p.bounty),
                AVG(p.bounty),
                MIN(p.bounty),
                MAX(p.bounty)
            FROM Pirate p
            """, Object[].class)
                .getSingleResult();

        System.out.println("\n📊 Statystyki piratów:");
        System.out.printf("  Liczba: %s%n", stats[0]);
        System.out.printf("  Suma nagród: $%s%n", stats[1]);
        System.out.printf("  Średnia nagroda: $%.2f%n", (Double) stats[2]);
        System.out.printf("  Min nagroda: $%s%n", stats[3]);
        System.out.printf("  Max nagroda: $%s%n", stats[4]);

        // GROUP BY z HAVING
        System.out.println("\n📊 Statystyki per statek (GROUP BY + HAVING):");

        List<ShipSummaryDTO> summaries = em.createQuery("""
            SELECT NEW pl.przemekzagorski.training.jpa.dto.ShipSummaryDTO(
                s.name,
                s.type,
                COUNT(p),
                SUM(p.bounty)
            )
            FROM Ship s
            LEFT JOIN s.crew p
            GROUP BY s.id, s.name, s.type
            HAVING COUNT(p) > 0
            ORDER BY SUM(p.bounty) DESC
            """, ShipSummaryDTO.class)
                .getResultList();

        summaries.forEach(s -> System.out.printf("  🚢 %s (%s): %d piratów, $%s nagród%n",
                s.shipName(), s.shipType(), s.crewCount(), s.totalBounty()));
    }

    /**
     * 3. JOIN FETCH - rozwiązanie problemu N+1
     */
    private static void demonstrateJoinFetch(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("3. JOIN FETCH (rozwiązanie N+1)");
        System.out.println("-".repeat(50));

        // Problem N+1 - LAZY loading
        System.out.println("\n⚠️  Problem N+1 (zwykły query):");
        System.out.println("    Dla każdego statku osobne zapytanie o załogę!\n");

        // Rozwiązanie: JOIN FETCH
        System.out.println("✅ Rozwiązanie: JOIN FETCH (1 zapytanie):\n");

        List<Ship> ships = em.createQuery("""
            SELECT DISTINCT s FROM Ship s
            LEFT JOIN FETCH s.crew
            LEFT JOIN FETCH s.homePort
            """, Ship.class)
                .getResultList();

        ships.forEach(s -> {
            System.out.printf("🚢 %s (port: %s)%n",
                    s.getName(),
                    s.getHomePort() != null ? s.getHomePort().getName() : "brak");
            s.getCrew().forEach(p ->
                    System.out.printf("    👤 %s - %s%n", p.getName(), p.getRank()));
        });

        // Sprawdź statystyki Hibernate
        System.out.println("\n📈 Sprawdź logi SQL - powinno być tylko 1 zapytanie!");
    }

    /**
     * 4. Named Queries - predefiniowane zapytania
     */
    private static void demonstrateNamedQueries(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("4. NAMED QUERIES");
        System.out.println("-".repeat(50));

        System.out.println("\n📋 Named Query: findByMinBounty");
        List<Pirate> wanted = em.createNamedQuery("Pirate.findByMinBounty", Pirate.class)
                .setParameter("minBounty", new BigDecimal("100000"))
                .getResultList();

        wanted.forEach(p -> System.out.printf("  🏴‍☠️ %s - $%s%n", p.getName(), p.getBounty()));

        System.out.println("\n📋 Named Query: findCaptains");
        List<Pirate> captains = em.createNamedQuery("Pirate.findCaptains", Pirate.class)
                .getResultList();

        captains.forEach(p -> System.out.printf("  ⚓ Captain %s%n", p.getName()));

        System.out.println("\n📋 Named Query: countByRank");
        Long captainCount = em.createNamedQuery("Pirate.countByRank", Long.class)
                .setParameter("rank", "Captain")
                .getSingleResult();

        System.out.printf("  Liczba kapitanów: %d%n", captainCount);
    }

    /**
     * 5. Wyrażenia CASE WHEN
     */
    private static void demonstrateCaseWhen(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("5. WYRAŻENIA CASE WHEN");
        System.out.println("-".repeat(50));

        List<Object[]> categorized = em.createQuery("""
            SELECT p.name, p.bounty,
                CASE
                    WHEN p.bounty >= 100000 THEN 'WYSOKA'
                    WHEN p.bounty >= 20000 THEN 'ŚREDNIA'
                    ELSE 'NISKA'
                END as kategoria
            FROM Pirate p
            ORDER BY p.bounty DESC
            """, Object[].class)
                .getResultList();

        System.out.println("\n📋 Kategoryzacja nagród:");
        categorized.forEach(row ->
                System.out.printf("  %s: $%s [%s]%n", row[0], row[1], row[2]));

        // CASE z typem prostym
        List<Object[]> dangerLevel = em.createQuery("""
            SELECT s.name,
                CASE s.type
                    WHEN 'Man-of-War' THEN 5
                    WHEN 'Galleon' THEN 4
                    WHEN 'Frigate' THEN 3
                    ELSE 1
                END as dangerLevel
            FROM Ship s
            """, Object[].class)
                .getResultList();

        System.out.println("\n📋 Poziom zagrożenia statków:");
        dangerLevel.forEach(row ->
                System.out.printf("  🚢 %s: poziom %s/5%n", row[0], row[1]));
    }

    /**
     * 6. Podzapytania (Subqueries)
     */
    private static void demonstrateSubqueries(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("6. PODZAPYTANIA (SUBQUERIES)");
        System.out.println("-".repeat(50));

        // Pirat z najwyższą nagrodą
        System.out.println("\n📋 Pirat z najwyższą nagrodą (subquery w WHERE):");
        List<Pirate> mostWanted = em.createQuery("""
            SELECT p FROM Pirate p
            WHERE p.bounty = (SELECT MAX(p2.bounty) FROM Pirate p2)
            """, Pirate.class)
                .getResultList();

        mostWanted.forEach(p ->
                System.out.printf("  👑 %s - $%s%n", p.getName(), p.getBounty()));

        // Statki z załogą powyżej średniej
        System.out.println("\n📋 Piraci z nagrodą powyżej średniej:");
        List<Pirate> aboveAverage = em.createQuery("""
            SELECT p FROM Pirate p
            WHERE p.bounty > (SELECT AVG(p2.bounty) FROM Pirate p2)
            ORDER BY p.bounty DESC
            """, Pirate.class)
                .getResultList();

        aboveAverage.forEach(p ->
                System.out.printf("  ⭐ %s - $%s%n", p.getName(), p.getBounty()));

        // EXISTS subquery
        System.out.println("\n📋 Statki z kapitanem (EXISTS):");
        List<Ship> shipsWithCaptain = em.createQuery("""
            SELECT s FROM Ship s
            WHERE EXISTS (
                SELECT p FROM Pirate p
                WHERE p.ship = s AND p.rank = 'Captain'
            )
            """, Ship.class)
                .getResultList();

        shipsWithCaptain.forEach(s ->
                System.out.printf("  🚢 %s%n", s.getName()));
    }

    /**
     * 7. Paginacja
     */
    private static void demonstratePagination(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("7. PAGINACJA");
        System.out.println("-".repeat(50));

        int pageSize = 2;
        int pageNumber = 0;

        // Całkowita liczba rekordów
        Long totalCount = em.createQuery("SELECT COUNT(p) FROM Pirate p", Long.class)
                .getSingleResult();

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        System.out.printf("\n📋 Paginacja (strona %d/%d, po %d rekordów):%n",
                pageNumber + 1, totalPages, pageSize);

        // Paginowana lista
        TypedQuery<Pirate> query = em.createQuery(
                "SELECT p FROM Pirate p ORDER BY p.name", Pirate.class);

        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);

        List<Pirate> page = query.getResultList();

        page.forEach(p -> System.out.printf("  - %s%n", p.getName()));

        // Kolejna strona
        System.out.printf("\n📋 Strona 2/%d:%n", totalPages);
        query.setFirstResult(1 * pageSize);
        List<Pirate> page2 = query.getResultList();

        page2.forEach(p -> System.out.printf("  - %s%n", p.getName()));
    }

    /**
     * 8. Operacje masowe (Bulk Operations)
     */
    private static void demonstrateBulkOperations(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("8. OPERACJE MASOWE (BULK UPDATE/DELETE)");
        System.out.println("-".repeat(50));

        em.getTransaction().begin();

        // UPDATE masowy
        int updatedCount = em.createQuery("""
            UPDATE Pirate p
            SET p.bounty = p.bounty * 1.1
            WHERE p.rank = 'Captain'
            """)
                .executeUpdate();

        System.out.printf("\n✏️  Zaktualizowano nagrodę dla %d kapitanów (+10%%)%n", updatedCount);

        // DELETE masowy (ostrożnie!)
        // int deletedCount = em.createQuery("DELETE FROM Pirate p WHERE p.bounty < 1000")
        //         .executeUpdate();

        em.getTransaction().commit();

        // WAŻNE: Po bulk operations cache jest nieaktualny!
        em.clear();
        System.out.println("⚠️  Po operacjach masowych wyczyść cache: em.clear()");

        // Weryfikacja
        List<Pirate> captains = em.createNamedQuery("Pirate.findCaptains", Pirate.class)
                .getResultList();

        System.out.println("\n📋 Nowe nagrody kapitanów:");
        captains.forEach(c ->
                System.out.printf("  ⚓ %s: $%s%n", c.getName(), c.getBounty()));
    }
}
