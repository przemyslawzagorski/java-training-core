package pl.przemekzagorski.training.jpa;

import jakarta.persistence.*;
import pl.przemekzagorski.training.jpa.entity.Island;
import pl.przemekzagorski.training.jpa.entity.Pirate;
import pl.przemekzagorski.training.jpa.entity.Ship;

import java.math.BigDecimal;
import java.util.List;

/**
 * Demonstracja Native SQL Queries w JPA.
 *
 * KIEDY UŻYWAĆ NATIVE SQL?
 * ✅ Funkcje specyficzne dla bazy (np. LATERAL, CTE, Window Functions)
 * ✅ Bardzo złożone zapytania trudne do wyrażenia w JPQL
 * ✅ Optymalizacja wydajności wymagająca specyficznego SQL
 * ✅ Migracja z czystego SQL do JPA (etap przejściowy)
 * ✅ Wywołanie stored procedures
 *
 * KIEDY UNIKAĆ?
 * ❌ Proste CRUD - użyj JPQL lub Criteria API
 * ❌ Gdy zależy ci na przenośności między bazami
 * ❌ Gdy JPQL jest wystarczający
 *
 * UWAGA: Native SQL omija cache Hibernate i może zwracać nieaktualne dane!
 */
public class NativeQueryDemo {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("queries-pu");
        EntityManager em = emf.createEntityManager();

        try {
            setupTestData(em);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("DEMO: NATIVE SQL QUERIES");
            System.out.println("=".repeat(60));

            // 1. Podstawowe native query
            demonstrateBasicNativeQuery(em);

            // 2. Native query z mapowaniem na encję
            demonstrateEntityMapping(em);

            // 3. Scalar queries (pojedyncze wartości)
            demonstrateScalarQueries(em);

            // 4. SqlResultSetMapping
            demonstrateSqlResultSetMapping(em);

            // 5. Funkcje specyficzne dla bazy (H2)
            demonstrateDatabaseSpecificFunctions(em);

            // 6. Native query z parametrami
            demonstrateParameterizedQueries(em);

            // 7. INSERT/UPDATE/DELETE
            demonstrateModifyingQueries(em);

        } finally {
            em.close();
            emf.close();
        }
    }

    private static void setupTestData(EntityManager em) {
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

        em.persist(createPirate("Jack Sparrow", "Captain", "100000", blackPearl));
        em.persist(createPirate("Hector Barbossa", "Captain", "80000", blackPearl));
        em.persist(createPirate("Joshamee Gibbs", "First Mate", "20000", blackPearl));
        em.persist(createPirate("Davy Jones", "Captain", "500000", flyingDutchman));
        em.persist(createPirate("Maccus", "First Mate", "30000", flyingDutchman));

        em.getTransaction().commit();
        em.clear();
        System.out.println("✅ Dane testowe załadowane");
    }

    private static Pirate createPirate(String name, String rank, String bounty, Ship ship) {
        Pirate p = new Pirate(name, rank, new BigDecimal(bounty));
        p.setShip(ship);
        return p;
    }

    /**
     * 1. Podstawowe Native Query - zwraca Object[]
     */
    private static void demonstrateBasicNativeQuery(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("1. PODSTAWOWE NATIVE QUERY");
        System.out.println("-".repeat(50));

        @SuppressWarnings("unchecked")
        List<Object[]> results = em.createNativeQuery("""
            SELECT name, rank, bounty
            FROM pirate
            WHERE bounty > 50000
            ORDER BY bounty DESC
            """)
            .getResultList();

        System.out.println("\n📋 Wyniki (Object[]):");
        for (Object[] row : results) {
            System.out.printf("  - %s (%s): $%s%n", row[0], row[1], row[2]);
        }

        System.out.println("\n⚠️  Uwaga: Object[] jest podatny na błędy - preferuj mapowanie!");
    }

    /**
     * 2. Native Query z mapowaniem na encję
     */
    private static void demonstrateEntityMapping(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("2. MAPOWANIE NA ENCJĘ");
        System.out.println("-".repeat(50));

        // Hibernate automatycznie mapuje wynik na encję
        // WAŻNE: SELECT * lub wszystkie kolumny encji!
        @SuppressWarnings("unchecked")
        List<Pirate> pirates = em.createNativeQuery("""
            SELECT * FROM pirate
            WHERE rank = 'Captain'
            """, Pirate.class)
            .getResultList();

        System.out.println("\n📋 Kapitanowie (jako encje Pirate):");
        pirates.forEach(p -> System.out.printf("  ⚓ %s (id=%d): $%s%n",
                p.getName(), p.getId(), p.getBounty()));

        System.out.println("\n✅ Encje są zarządzane przez EntityManager!");
    }

    /**
     * 3. Scalar queries - pojedyncze wartości
     */
    private static void demonstrateScalarQueries(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("3. SCALAR QUERIES (AGREGACJE)");
        System.out.println("-".repeat(50));

        // Pojedyncza wartość
        Long count = (Long) em.createNativeQuery("""
            SELECT COUNT(*) FROM pirate
            """)
            .getSingleResult();

        System.out.println("\n📊 Liczba piratów: " + count);

        // Wiele wartości
        Object[] stats = (Object[]) em.createNativeQuery("""
            SELECT
                COUNT(*) as cnt,
                SUM(bounty) as total,
                AVG(bounty) as average,
                MIN(bounty) as minimum,
                MAX(bounty) as maximum
            FROM pirate
            """)
            .getSingleResult();

        System.out.println("\n📊 Statystyki nagród:");
        System.out.printf("   Liczba: %s%n", stats[0]);
        System.out.printf("   Suma: $%s%n", stats[1]);
        System.out.printf("   Średnia: $%.2f%n", ((Number) stats[2]).doubleValue());
        System.out.printf("   Min/Max: $%s / $%s%n", stats[3], stats[4]);
    }

    /**
     * 4. SqlResultSetMapping - zaawansowane mapowanie
     */
    private static void demonstrateSqlResultSetMapping(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("4. SQL RESULT SET MAPPING");
        System.out.println("-".repeat(50));

        // Możemy zdefiniować @SqlResultSetMapping na encji
        // lub użyć Tuple

        @SuppressWarnings("unchecked")
        List<Object[]> results = em.createNativeQuery("""
            SELECT
                p.name as pirate_name,
                p.bounty as pirate_bounty,
                s.name as ship_name,
                s.type as ship_type
            FROM pirate p
            LEFT JOIN ship s ON p.ship_id = s.id
            ORDER BY p.bounty DESC
            """)
            .getResultList();

        System.out.println("\n📋 Piraci ze statkami:");
        for (Object[] row : results) {
            String shipInfo = row[2] != null
                    ? String.format("%s (%s)", row[2], row[3])
                    : "brak statku";
            System.out.printf("  - %s ($%s) → %s%n", row[0], row[1], shipInfo);
        }

        // Dla czystszego kodu możesz użyć record i konstruktora
        System.out.println("\n💡 Tip: Dla produkcji użyj @SqlResultSetMapping lub records");
    }

    /**
     * 5. Funkcje specyficzne dla bazy danych
     */
    private static void demonstrateDatabaseSpecificFunctions(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("5. FUNKCJE SPECYFICZNE DLA BAZY (H2)");
        System.out.println("-".repeat(50));

        // Window function - ROW_NUMBER()
        System.out.println("\n📋 Ranking nagród (ROW_NUMBER):");

        @SuppressWarnings("unchecked")
        List<Object[]> ranked = em.createNativeQuery("""
            SELECT
                name,
                bounty,
                ROW_NUMBER() OVER (ORDER BY bounty DESC) as rank
            FROM pirate
            """)
            .getResultList();

        ranked.forEach(row ->
            System.out.printf("  #%s - %s: $%s%n", row[2], row[0], row[1]));

        // Ranking w grupach
        System.out.println("\n📋 Ranking per ranga (PARTITION BY):");

        @SuppressWarnings("unchecked")
        List<Object[]> partitioned = em.createNativeQuery("""
            SELECT
                rank,
                name,
                bounty,
                ROW_NUMBER() OVER (PARTITION BY rank ORDER BY bounty DESC) as position
            FROM pirate
            """)
            .getResultList();

        partitioned.forEach(row ->
            System.out.printf("  %s #%s: %s ($%s)%n", row[0], row[3], row[1], row[2]));

        // Funkcje tekstowe H2
        System.out.println("\n📋 Funkcje tekstowe (UPPER, LENGTH):");

        @SuppressWarnings("unchecked")
        List<Object[]> textFunctions = em.createNativeQuery("""
            SELECT
                UPPER(name) as upper_name,
                LENGTH(name) as name_length
            FROM pirate
            WHERE rank = 'Captain'
            """)
            .getResultList();

        textFunctions.forEach(row ->
            System.out.printf("  %s (długość: %s)%n", row[0], row[1]));
    }

    /**
     * 6. Native Query z parametrami
     */
    private static void demonstrateParameterizedQueries(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("6. PARAMETRYZOWANE ZAPYTANIA");
        System.out.println("-".repeat(50));

        // Parametry pozycyjne (?1, ?2, ...)
        System.out.println("\n📋 Parametry pozycyjne:");

        @SuppressWarnings("unchecked")
        List<Pirate> byBountyRange = em.createNativeQuery("""
            SELECT * FROM pirate
            WHERE bounty BETWEEN ?1 AND ?2
            ORDER BY bounty
            """, Pirate.class)
            .setParameter(1, new BigDecimal("20000"))
            .setParameter(2, new BigDecimal("100000"))
            .getResultList();

        byBountyRange.forEach(p ->
            System.out.printf("  - %s: $%s%n", p.getName(), p.getBounty()));

        // Parametry nazwane (Hibernate-specific extension)
        System.out.println("\n📋 Parametry nazwane:");

        @SuppressWarnings("unchecked")
        List<Pirate> byRank = em.createNativeQuery("""
            SELECT * FROM pirate
            WHERE rank = :rank
            """, Pirate.class)
            .setParameter("rank", "First Mate")
            .getResultList();

        byRank.forEach(p ->
            System.out.printf("  - %s: %s%n", p.getName(), p.getRank()));

        // LIKE z parametrem
        System.out.println("\n📋 LIKE z parametrem:");

        @SuppressWarnings("unchecked")
        List<Pirate> byNameLike = em.createNativeQuery("""
            SELECT * FROM pirate
            WHERE LOWER(name) LIKE LOWER(:pattern)
            """, Pirate.class)
            .setParameter("pattern", "%jack%")
            .getResultList();

        byNameLike.forEach(p ->
            System.out.printf("  - %s%n", p.getName()));
    }

    /**
     * 7. Modyfikujące zapytania (INSERT/UPDATE/DELETE)
     */
    private static void demonstrateModifyingQueries(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("7. MODYFIKUJĄCE ZAPYTANIA (DML)");
        System.out.println("-".repeat(50));

        em.getTransaction().begin();

        // UPDATE
        int updatedRows = em.createNativeQuery("""
            UPDATE pirate
            SET bounty = bounty * 1.15
            WHERE rank = 'Captain'
            """)
            .executeUpdate();

        System.out.printf("\n✏️  Zaktualizowano %d kapitanów (+15%% do nagrody)%n", updatedRows);

        // INSERT
        int insertedRows = em.createNativeQuery("""
            INSERT INTO pirate (name, rank, bounty, version)
            VALUES ('Cotton', 'Deckhand', 1000, 0)
            """)
            .executeUpdate();

        System.out.printf("➕ Wstawiono %d nowego pirata%n", insertedRows);

        // DELETE (ostrożnie!)
        int deletedRows = em.createNativeQuery("""
            DELETE FROM pirate
            WHERE name = 'Cotton'
            """)
            .executeUpdate();

        System.out.printf("➖ Usunięto %d piratów%n", deletedRows);

        em.getTransaction().commit();

        // WAŻNE: Cache jest nieaktualny po native DML!
        em.clear();
        System.out.println("\n⚠️  WAŻNE: Po native DML należy wyczyścić cache: em.clear()");

        // Weryfikacja
        Long count = (Long) em.createNativeQuery("SELECT COUNT(*) FROM pirate")
                .getSingleResult();
        System.out.printf("\n📊 Aktualnie piratów w bazie: %d%n", count);
    }
}
