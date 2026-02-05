package pl.przemekzagorski.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import pl.przemekzagorski.training.jpa.dto.PirateDTO;
import pl.przemekzagorski.training.jpa.entity.Island;
import pl.przemekzagorski.training.jpa.entity.Pirate;
import pl.przemekzagorski.training.jpa.entity.Ship;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstracja Criteria API - bezpieczne, dynamiczne budowanie zapytań.
 *
 * KIEDY UŻYWAĆ CRITERIA API?
 * ✅ Dynamiczne filtry (np. wyszukiwarka z wieloma opcjami)
 * ✅ Zapytania budowane warunkowo
 * ✅ Gdy potrzebna jest type safety
 * ✅ Złożone warunki budowane w runtime
 *
 * KIEDY NIE UŻYWAĆ?
 * ❌ Proste, statyczne zapytania (JPQL jest czytelniejszy)
 * ❌ Gdy czytelność kodu jest priorytetem
 *
 * Porównanie z JPQL:
 * - Criteria: bezpieczne typowo, ale mniej czytelne
 * - JPQL: czytelne jak SQL, ale bez sprawdzania typów
 */
public class CriteriaApiDemo {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("queries-pu");
        EntityManager em = emf.createEntityManager();

        try {
            setupTestData(em);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("DEMO: CRITERIA API - DYNAMICZNE ZAPYTANIA");
            System.out.println("=".repeat(60));

            // 1. Podstawowe zapytanie
            demonstrateBasicQuery(em);

            // 2. Dynamiczne filtry
            demonstrateDynamicFilters(em);

            // 3. Sortowanie
            demonstrateSorting(em);

            // 4. Joiny i fetche
            demonstrateJoinsAndFetches(em);

            // 5. Agregacje
            demonstrateAggregations(em);

            // 6. Projekcje do DTO (multiselect)
            demonstrateProjections(em);

            // 7. Praktyczny przykład: wyszukiwarka
            demonstrateSearchService(em);

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
        em.persist(createPirate("Will Turner", "Blacksmith", "5000", null));

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
     * 1. Podstawowe zapytanie Criteria API
     */
    private static void demonstrateBasicQuery(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("1. PODSTAWOWE ZAPYTANIE CRITERIA");
        System.out.println("-".repeat(50));

        // Krok 1: CriteriaBuilder - fabryka elementów zapytania
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Krok 2: CriteriaQuery<T> - definicja zapytania
        CriteriaQuery<Pirate> cq = cb.createQuery(Pirate.class);

        // Krok 3: Root<T> - FROM clause
        Root<Pirate> pirate = cq.from(Pirate.class);

        // Krok 4: select + where
        cq.select(pirate)
          .where(cb.equal(pirate.get("rank"), "Captain"));

        // Krok 5: wykonanie
        TypedQuery<Pirate> query = em.createQuery(cq);
        List<Pirate> captains = query.getResultList();

        System.out.println("\n📋 Wszyscy kapitanowie (podstawowe Criteria):");
        captains.forEach(c -> System.out.printf("  ⚓ %s - $%s%n", c.getName(), c.getBounty()));

        // Odpowiednik JPQL:
        System.out.println("\n  Odpowiednik JPQL: SELECT p FROM Pirate p WHERE p.rank = 'Captain'");
    }

    /**
     * 2. Dynamiczne filtry - główna siła Criteria API
     */
    private static void demonstrateDynamicFilters(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("2. DYNAMICZNE FILTRY");
        System.out.println("-".repeat(50));

        // Symulacja parametrów z frontendu (mogą być null!)
        String rankFilter = "Captain";
        BigDecimal minBounty = new BigDecimal("90000");
        String nameContains = null;  // brak filtra

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pirate> cq = cb.createQuery(Pirate.class);
        Root<Pirate> pirate = cq.from(Pirate.class);

        // Lista warunków - dodajemy tylko te które nie są null
        List<Predicate> predicates = new ArrayList<>();

        if (rankFilter != null && !rankFilter.isBlank()) {
            predicates.add(cb.equal(pirate.get("rank"), rankFilter));
        }

        if (minBounty != null) {
            predicates.add(cb.greaterThanOrEqualTo(pirate.get("bounty"), minBounty));
        }

        if (nameContains != null && !nameContains.isBlank()) {
            predicates.add(cb.like(
                cb.lower(pirate.get("name")),
                "%" + nameContains.toLowerCase() + "%"
            ));
        }

        // Łączymy warunki przez AND
        cq.where(predicates.toArray(new Predicate[0]));

        List<Pirate> results = em.createQuery(cq).getResultList();

        System.out.println("\n📋 Wyniki dynamicznego filtrowania:");
        System.out.printf("   Filtry: rank=%s, minBounty=%s, name=%s%n",
                rankFilter, minBounty, nameContains);

        results.forEach(p -> System.out.printf("  - %s (%s): $%s%n",
                p.getName(), p.getRank(), p.getBounty()));
    }

    /**
     * 3. Dynamiczne sortowanie
     */
    private static void demonstrateSorting(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("3. DYNAMICZNE SORTOWANIE");
        System.out.println("-".repeat(50));

        // Parametry sortowania (mogą przyjść z UI)
        String sortField = "bounty";
        boolean ascending = false;

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pirate> cq = cb.createQuery(Pirate.class);
        Root<Pirate> pirate = cq.from(Pirate.class);

        cq.select(pirate);

        // Dynamiczne sortowanie
        Order order = ascending
                ? cb.asc(pirate.get(sortField))
                : cb.desc(pirate.get(sortField));
        cq.orderBy(order);

        List<Pirate> sorted = em.createQuery(cq).getResultList();

        System.out.printf("\n📋 Sortowanie po %s (%s):%n",
                sortField, ascending ? "ASC" : "DESC");
        sorted.forEach(p -> System.out.printf("  - %s: $%s%n", p.getName(), p.getBounty()));
    }

    /**
     * 4. Joiny i Fetch
     */
    private static void demonstrateJoinsAndFetches(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("4. JOINY I FETCH");
        System.out.println("-".repeat(50));

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ship> cq = cb.createQuery(Ship.class);
        Root<Ship> ship = cq.from(Ship.class);

        // FETCH JOIN - rozwiązanie N+1
        ship.fetch("crew", JoinType.LEFT);
        ship.fetch("homePort", JoinType.LEFT);

        // DISTINCT (bo fetch może duplikować)
        cq.distinct(true);

        List<Ship> ships = em.createQuery(cq).getResultList();

        System.out.println("\n📋 Statki z załogą (FETCH JOIN):");
        ships.forEach(s -> {
            System.out.printf("  🚢 %s (port: %s)%n",
                    s.getName(),
                    s.getHomePort() != null ? s.getHomePort().getName() : "brak");
            s.getCrew().forEach(p ->
                    System.out.printf("      👤 %s%n", p.getName()));
        });

        // JOIN z warunkiem
        System.out.println("\n📋 Statki z kapitanem (JOIN + WHERE):");

        CriteriaQuery<Ship> cq2 = cb.createQuery(Ship.class);
        Root<Ship> ship2 = cq2.from(Ship.class);
        Join<Ship, Pirate> crewJoin = ship2.join("crew");

        cq2.select(ship2).distinct(true)
           .where(cb.equal(crewJoin.get("rank"), "Captain"));

        em.createQuery(cq2).getResultList()
          .forEach(s -> System.out.printf("  🚢 %s%n", s.getName()));
    }

    /**
     * 5. Agregacje w Criteria API
     */
    private static void demonstrateAggregations(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("5. AGREGACJE");
        System.out.println("-".repeat(50));

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // COUNT
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Pirate> pirate = countQuery.from(Pirate.class);
        countQuery.select(cb.count(pirate));

        Long total = em.createQuery(countQuery).getSingleResult();
        System.out.println("\n📊 Liczba piratów: " + total);

        // SUM, AVG, MIN, MAX
        CriteriaQuery<Object[]> statsQuery = cb.createQuery(Object[].class);
        Root<Pirate> p2 = statsQuery.from(Pirate.class);

        statsQuery.multiselect(
            cb.sum(p2.get("bounty")),
            cb.avg(p2.get("bounty")),
            cb.min(p2.get("bounty")),
            cb.max(p2.get("bounty"))
        );

        Object[] stats = em.createQuery(statsQuery).getSingleResult();
        System.out.printf("📊 Suma nagród: $%s%n", stats[0]);
        System.out.printf("📊 Średnia: $%.2f%n", (Double) stats[1]);
        System.out.printf("📊 Min: $%s, Max: $%s%n", stats[2], stats[3]);

        // GROUP BY
        System.out.println("\n📊 Piraci per ranga (GROUP BY):");

        CriteriaQuery<Object[]> groupQuery = cb.createQuery(Object[].class);
        Root<Pirate> p3 = groupQuery.from(Pirate.class);

        groupQuery.multiselect(
            p3.get("rank"),
            cb.count(p3)
        ).groupBy(p3.get("rank"));

        em.createQuery(groupQuery).getResultList().forEach(row ->
            System.out.printf("  %s: %d osób%n", row[0], row[1]));
    }

    /**
     * 6. Projekcje do DTO
     */
    private static void demonstrateProjections(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("6. PROJEKCJE DO DTO (MULTISELECT)");
        System.out.println("-".repeat(50));

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PirateDTO> cq = cb.createQuery(PirateDTO.class);
        Root<Pirate> pirate = cq.from(Pirate.class);

        // Konstrukcja DTO przez construct()
        cq.select(cb.construct(
            PirateDTO.class,
            pirate.get("name"),
            pirate.get("rank"),
            pirate.get("bounty")
        ));

        cq.where(cb.greaterThan(pirate.get("bounty"), new BigDecimal("50000")));

        List<PirateDTO> dtos = em.createQuery(cq).getResultList();

        System.out.println("\n📋 Piraci jako DTO (bounty > 50000):");
        dtos.forEach(d -> System.out.printf("  - %s (%s): $%s%n",
                d.name(), d.rank(), d.bounty()));
    }

    /**
     * 7. Praktyczny przykład - serwis wyszukiwania
     */
    private static void demonstrateSearchService(EntityManager em) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("7. PRAKTYCZNY PRZYKŁAD: WYSZUKIWARKA");
        System.out.println("-".repeat(50));

        // Symulacja kryteriów wyszukiwania z formularza
        PirateSearchCriteria criteria = new PirateSearchCriteria();
        criteria.setMinBounty(new BigDecimal("20000"));
        criteria.setSortBy("bounty");
        criteria.setSortDesc(true);
        criteria.setPage(0);
        criteria.setPageSize(3);

        // Wywołanie serwisu
        PirateSearchResult result = searchPirates(em, criteria);

        System.out.println("\n📋 Wyniki wyszukiwania:");
        System.out.printf("   Strona %d/%d (łącznie %d rekordów)%n",
                result.currentPage + 1, result.totalPages, result.totalCount);

        result.pirates.forEach(p ->
            System.out.printf("  - %s: $%s%n", p.getName(), p.getBounty()));
    }

    /**
     * Metoda wyszukująca - wzorzec dla serwisów
     */
    private static PirateSearchResult searchPirates(EntityManager em, PirateSearchCriteria criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // 1. Zapytanie o dane
        CriteriaQuery<Pirate> dataQuery = cb.createQuery(Pirate.class);
        Root<Pirate> pirate = dataQuery.from(Pirate.class);

        List<Predicate> predicates = buildPredicates(cb, pirate, criteria);
        dataQuery.where(predicates.toArray(new Predicate[0]));

        // Sortowanie
        if (criteria.getSortBy() != null) {
            Order order = criteria.isSortDesc()
                    ? cb.desc(pirate.get(criteria.getSortBy()))
                    : cb.asc(pirate.get(criteria.getSortBy()));
            dataQuery.orderBy(order);
        }

        // Paginacja
        TypedQuery<Pirate> query = em.createQuery(dataQuery);
        query.setFirstResult(criteria.getPage() * criteria.getPageSize());
        query.setMaxResults(criteria.getPageSize());

        List<Pirate> pirates = query.getResultList();

        // 2. Zapytanie o count (dla paginacji)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Pirate> countRoot = countQuery.from(Pirate.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(buildPredicates(cb, countRoot, criteria).toArray(new Predicate[0]));

        Long totalCount = em.createQuery(countQuery).getSingleResult();

        // 3. Budowanie wyniku
        PirateSearchResult result = new PirateSearchResult();
        result.pirates = pirates;
        result.totalCount = totalCount;
        result.currentPage = criteria.getPage();
        result.totalPages = (int) Math.ceil((double) totalCount / criteria.getPageSize());

        return result;
    }

    private static List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<Pirate> pirate,
            PirateSearchCriteria criteria) {

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getName() != null && !criteria.getName().isBlank()) {
            predicates.add(cb.like(
                cb.lower(pirate.get("name")),
                "%" + criteria.getName().toLowerCase() + "%"
            ));
        }

        if (criteria.getRank() != null) {
            predicates.add(cb.equal(pirate.get("rank"), criteria.getRank()));
        }

        if (criteria.getMinBounty() != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                pirate.get("bounty"), criteria.getMinBounty()));
        }

        if (criteria.getMaxBounty() != null) {
            predicates.add(cb.lessThanOrEqualTo(
                pirate.get("bounty"), criteria.getMaxBounty()));
        }

        return predicates;
    }

    // DTO dla kryteriów wyszukiwania
    static class PirateSearchCriteria {
        private String name;
        private String rank;
        private BigDecimal minBounty;
        private BigDecimal maxBounty;
        private String sortBy;
        private boolean sortDesc;
        private int page = 0;
        private int pageSize = 10;

        // Getters & Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRank() { return rank; }
        public void setRank(String rank) { this.rank = rank; }
        public BigDecimal getMinBounty() { return minBounty; }
        public void setMinBounty(BigDecimal minBounty) { this.minBounty = minBounty; }
        public BigDecimal getMaxBounty() { return maxBounty; }
        public void setMaxBounty(BigDecimal maxBounty) { this.maxBounty = maxBounty; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        public boolean isSortDesc() { return sortDesc; }
        public void setSortDesc(boolean sortDesc) { this.sortDesc = sortDesc; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    }

    // DTO dla wyników wyszukiwania
    static class PirateSearchResult {
        List<Pirate> pirates;
        long totalCount;
        int currentPage;
        int totalPages;
    }
}
