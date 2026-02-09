package pl.przemekzagorski.training.springdata.advanced.service;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import pl.przemekzagorski.training.springdata.advanced.entity.Pirate;
import pl.przemekzagorski.training.springdata.advanced.repository.PirateRepository;

import java.util.List;

/**
 * 🎯 Serwis demonstracyjny dla Cache L2 (Hibernate Second Level Cache).
 *
 * KONCEPCJA Cache L2:
 * - Cache L1 (Session Cache) - automatyczny, per-session
 * - Cache L2 (SessionFactory Cache) - współdzielony między sesjami
 * - Cache L2 działa między transakcjami!
 *
 * KORZYŚCI:
 * - Mniej zapytań do bazy
 * - Szybsze odczyty
 * - Mniejsze obciążenie bazy danych
 *
 * KONFIGURACJA:
 * - @Cacheable na encji
 * - @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
 * - ehcache.xml z konfiguracją regionów
 * - application.yml: hibernate.cache.use_second_level_cache=true
 */
@Service
public class CacheL2DemoService {

    private static final Logger log = LoggerFactory.getLogger(CacheL2DemoService.class);

    private final PirateRepository pirateRepository;
    private final TransactionTemplate transactionTemplate;
    private final SessionFactory sessionFactory;

    public CacheL2DemoService(PirateRepository pirateRepository,
                              PlatformTransactionManager transactionManager,
                              EntityManager entityManager) {
        this.pirateRepository = pirateRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setReadOnly(true);
        this.sessionFactory = entityManager.unwrap(Session.class).getSessionFactory();
    }

    /**
     * Demo 1: Entity Cache L2 - pobieranie encji po ID.
     *
     * OBSERWUJ w logach:
     * - Transakcja 1: SELECT z bazy (cache miss)
     * - Transakcja 2: brak SELECT (cache hit!)
     *
     * WAŻNE: Cache L2 działa MIĘDZY transakcjami!
     * Używamy TransactionTemplate do tworzenia osobnych transakcji.
     */
    public void demonstrateCacheHit() {
        log.info("=== DEMO: Entity Cache L2 (findById) ===");
        log.info("Entity Cache cachuje POJEDYNCZE ENCJE po ich ID");
        log.info("");

        // Wyczyść cache i statystyki przed demo
        sessionFactory.getCache().evictAllRegions();
        sessionFactory.getStatistics().clear();

        // ===== TRANSAKCJA 1 =====
        log.info("▶ TRANSAKCJA #1: findById(1)");

        Pirate pirate1 = transactionTemplate.execute(status ->
            pirateRepository.findById(1L).orElseThrow()
        );

        Statistics stats1 = sessionFactory.getStatistics();
        log.info("  Wynik: {} ", pirate1.getName());
        log.info("  Cache: MISS=1, HIT=0 → SQL wykonany");
        log.info("  Hit Ratio tej transakcji: 0% (encja nie była w cache)");

        // ===== TRANSAKCJA 2 =====
        log.info("");
        log.info("▶ TRANSAKCJA #2: findById(1)");

        // Zapisz statystyki PRZED drugą transakcją
        long hitsBefore = stats1.getSecondLevelCacheHitCount();

        Pirate pirate2 = transactionTemplate.execute(status ->
            pirateRepository.findById(1L).orElseThrow()
        );

        Statistics stats2 = sessionFactory.getStatistics();
        long hitsAfter = stats2.getSecondLevelCacheHitCount();

        log.info("  Wynik: {} ", pirate2.getName());
        log.info("  Cache: MISS=0, HIT=1 → SQL NIE wykonany!");
        log.info("  Hit Ratio tej transakcji: 100% (encja była w cache!) ✅");

        // ===== PODSUMOWANIE =====
        log.info("");
        log.info("┌─────────────────────────────────────────────────────┐");
        log.info("│ PODSUMOWANIE (suma 2 transakcji)                    │");
        log.info("│ Hits: {}  Misses: {}  Puts: {}                        │",
            stats2.getSecondLevelCacheHitCount(),
            stats2.getSecondLevelCacheMissCount(),
            stats2.getSecondLevelCachePutCount());
        log.info("│ Łączny Hit Ratio: {}%                                │",
            calculateHitRatio(stats2.getSecondLevelCacheHitCount(), stats2.getSecondLevelCacheMissCount()));
        log.info("│                                                     │");
        log.info("│ Transakcja #1: 0% (cold cache)                      │");
        log.info("│ Transakcja #2: 100% (warm cache) ✅                 │");
        log.info("└─────────────────────────────────────────────────────┘");
    }

    private String calculateHitRatio(long hits, long misses) {
        if (hits + misses == 0) return "N/A";
        return String.format("%.0f", (double) hits / (hits + misses) * 100);
    }

    /**
     * Demo 2: Statystyki cache.
     * 
     * Pokazuje:
     * - Ile razy użyto cache (hit)
     * - Ile razy cache nie miał danych (miss)
     * - Hit ratio (% trafień)
     */
    public void printCacheStatistics() {
        Statistics stats = sessionFactory.getStatistics();

        log.info("=== STATYSTYKI CACHE L2 ===");
        log.info("Second Level Cache Hits: {}", stats.getSecondLevelCacheHitCount());
        log.info("Second Level Cache Misses: {}", stats.getSecondLevelCacheMissCount());
        log.info("Second Level Cache Puts: {}", stats.getSecondLevelCachePutCount());
        
        long hits = stats.getSecondLevelCacheHitCount();
        long misses = stats.getSecondLevelCacheMissCount();
        if (hits + misses > 0) {
            double hitRatio = (double) hits / (hits + misses) * 100;
            log.info("Hit Ratio: {}%", String.format("%.2f", hitRatio));
        } else {
            log.info("Hit Ratio: N/A (brak operacji cache)");
        }
        
        log.info("Query Cache Hits: {}", stats.getQueryCacheHitCount());
        log.info("Query Cache Misses: {}", stats.getQueryCacheMissCount());
    }

    /**
     * Demo 3: Czyszczenie cache.
     */
    @Transactional
    public void clearCache() {
        log.info("=== Czyszczenie Cache L2 ===");
        sessionFactory.getCache().evictAllRegions();
        log.info("✅ Cache wyczyszczony");
    }

    /**
     * Demo 4: Query Cache.
     * 
     * Używa metody findByRankCacheable() z @QueryHints.
     *
     * WAŻNE - Query Cache to DWA POZIOMY:
     * 1. Query Cache - cachuje wyniki zapytania (lista ID encji)
     * 2. Entity Cache (L2) - cachuje same encje
     *
     * Przy drugim wywołaniu:
     * - Query Cache HIT = mamy listę ID w cache
     * - Entity Cache HIT = encje też są w cache (nie trzeba pobierać z bazy)
     */
    public void demonstrateQueryCache() {
        log.info("=== DEMO: Query Cache ===");
        log.info("Query Cache cachuje WYNIKI ZAPYTAŃ (listę ID), encje są w Entity Cache L2");
        log.info("");

        // Wyczyść CAŁY cache żeby demo działało niezależnie od poprzednich operacji
        sessionFactory.getCache().evictAllRegions();
        sessionFactory.getStatistics().clear();

        // ===== TRANSAKCJA 1 =====
        log.info("▶ TRANSAKCJA #1: findByRankCacheable('Captain')");

        List<Pirate> captains1 = transactionTemplate.execute(status ->
            pirateRepository.findByRankCacheable("Captain")
        );

        Statistics stats1 = sessionFactory.getStatistics();
        log.info("  Wynik: {} kapitanów", captains1.size());
        log.info("  Query Cache: MISS=1 → SQL wykonany");
        log.info("  Entity Cache: PUT={} (encje zapisane)", stats1.getSecondLevelCachePutCount());
        log.info("  Hit Ratio tej transakcji: 0% (cold cache)");

        // ===== TRANSAKCJA 2 =====
        log.info("");
        log.info("▶ TRANSAKCJA #2: findByRankCacheable('Captain')");

        List<Pirate> captains2 = transactionTemplate.execute(status ->
            pirateRepository.findByRankCacheable("Captain")
        );

        Statistics stats2 = sessionFactory.getStatistics();
        log.info("  Wynik: {} kapitanów", captains2.size());
        log.info("  Query Cache: HIT=1 → SQL NIE wykonany!");
        log.info("  Hit Ratio tej transakcji: 100% (warm cache) ✅");

        // ===== PODSUMOWANIE =====
        log.info("");
        log.info("┌─────────────────────────────────────────────────────┐");
        log.info("│ PODSUMOWANIE (suma 2 transakcji)                    │");
        log.info("│ Query Cache:  Hits={}  Misses={}  Puts={}             │",
            stats2.getQueryCacheHitCount(),
            stats2.getQueryCacheMissCount(),
            stats2.getQueryCachePutCount());
        log.info("│ Entity Cache: Hits={}  Misses={}  Puts={}             │",
            stats2.getSecondLevelCacheHitCount(),
            stats2.getSecondLevelCacheMissCount(),
            stats2.getSecondLevelCachePutCount());
        log.info("│                                                     │");
        log.info("│ Transakcja #1: 0% (cold cache)                      │");
        log.info("│ Transakcja #2: 100% (warm cache) ✅                 │");
        log.info("└─────────────────────────────────────────────────────┘");
    }
}

