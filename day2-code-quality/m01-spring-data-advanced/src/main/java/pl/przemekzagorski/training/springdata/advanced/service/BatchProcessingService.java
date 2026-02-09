package pl.przemekzagorski.training.springdata.advanced.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.przemekzagorski.training.springdata.advanced.entity.Pirate;
import pl.przemekzagorski.training.springdata.advanced.repository.PirateRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 🎯 Serwis demonstracyjny dla Batch Processing.
 *
 * KONCEPCJA:
 * - Batch Processing = grupowanie operacji INSERT/UPDATE w batche
 * - Zamiast 1000 pojedynczych INSERT, wykonuje 20 batchy po 50 INSERT
 * - Wymaga flush() i clear() co N rekordów
 *
 * KONFIGURACJA:
 * - hibernate.jdbc.batch_size=50 (w application.yml)
 * - hibernate.order_inserts=true
 * - hibernate.order_updates=true
 *
 * KORZYŚCI:
 * - Znacznie szybszy import dużej ilości danych
 * - Mniejsze zużycie pamięci (clear() zwalnia Session cache)
 * - Mniejsze obciążenie bazy danych
 *
 * RÓŻNICA vs Pure JPA:
 * - Identyczna technika (flush/clear)
 * - Spring: @Transactional zamiast ręcznego zarządzania transakcją
 */
@Service
public class BatchProcessingService {

    private static final Logger log = LoggerFactory.getLogger(BatchProcessingService.class);
    private static final int BATCH_SIZE = 50;

    @PersistenceContext
    private EntityManager entityManager;

    private final PirateRepository pirateRepository;

    @Autowired
    private ApplicationContext applicationContext;

    public BatchProcessingService(PirateRepository pirateRepository) {
        this.pirateRepository = pirateRepository;
    }

    /**
     * Pobiera proxy tego serwisu, aby wywołania @Transactional działały poprawnie.
     * Rozwiązuje problem self-invocation w Spring AOP.
     */
    private BatchProcessingService getSelf() {
        return applicationContext.getBean(BatchProcessingService.class);
    }

    /**
     * Demo 1: Import bez batch processing (WOLNY).
     *
     * PROBLEM:
     * - Wszystkie encje trzymane w Session cache
     * - Duże zużycie pamięci
     * - Wolniejsze INSERT (pojedyncze)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importWithoutBatch(int count) {
        log.info("=== DEMO: Import BEZ batch processing ===");
        long start = System.currentTimeMillis();
        
        List<Pirate> pirates = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Pirate pirate = new Pirate(
                "Pirate " + i,
                "Sailor",
                BigDecimal.valueOf(1000 + i)
            );
            pirates.add(pirate);
        }
        
        pirateRepository.saveAll(pirates);
        
        long end = System.currentTimeMillis();
        log.info("✅ Zaimportowano {} piratów w {} ms (BEZ batch)", count, end - start);
    }

    /**
     * Demo 2: Import z batch processing (SZYBKI).
     *
     * ROZWIĄZANIE:
     * - flush() co BATCH_SIZE rekordów - wysyła batch do bazy
     * - clear() co BATCH_SIZE rekordów - zwalnia Session cache
     * - Hibernate grupuje INSERT w batche
     *
     * KLUCZOWE:
     * - Używamy persist() zamiast merge() (szybsze dla nowych encji)
     * - SEQUENCE generuje ID automatycznie (allocationSize=50)
     * - flush() + clear() co BATCH_SIZE zwalnia pamięć
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importWithBatch(int count) {
        log.info("=== DEMO: Import Z batch processing ===");
        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            Pirate pirate = new Pirate(
                "BatchPirate " + i,
                "Sailor",
                BigDecimal.valueOf(1000 + i)
            );
            entityManager.persist(pirate);  // ✅ persist() zamiast merge()!

            // Co BATCH_SIZE rekordów: flush + clear
            if ((i + 1) % BATCH_SIZE == 0) {
                entityManager.flush();  // Wyślij batch do bazy
                entityManager.clear();  // Wyczyść Session cache
                log.debug("Batch {} wysłany ({} rekordów)", (i + 1) / BATCH_SIZE, BATCH_SIZE);
            }
        }

        // Ostatni batch (jeśli count nie jest wielokrotnością BATCH_SIZE)
        entityManager.flush();
        entityManager.clear();

        long end = System.currentTimeMillis();
        log.info("✅ Zaimportowano {} piratów w {} ms (Z batch)", count, end - start);
    }

    /**
     * Demo 3: Porównanie wydajności.
     *
     * OBSERWUJ:
     * - Różnicę w czasie wykonania
     * - Logi SQL (batche vs pojedyncze INSERT)
     *
     * UWAGA:
     * - NIE ma @Transactional - każda metoda ma swoją transakcję
     * - Czyszczenie bazy między testami dla uczciwego porównania
     */
    public void comparePerformance() {
        log.info("=== DEMO: Porównanie wydajności ===");

        int testSize = 1000;

        // Test 1: Bez batch
        log.info("Test 1: Import BEZ batch processing...");
        long start1 = System.currentTimeMillis();
        getSelf().importWithoutBatch(testSize);  // ✅ Wywołanie przez proxy
        long time1 = System.currentTimeMillis() - start1;

        // Wyczyść bazę przed drugim testem
        getSelf().cleanupTestData();  // ✅ Wywołanie przez proxy

        // Test 2: Z batch
        log.info("Test 2: Import Z batch processing...");
        long start2 = System.currentTimeMillis();
        getSelf().importWithBatch(testSize);  // ✅ Wywołanie przez proxy
        long time2 = System.currentTimeMillis() - start2;

        log.info("=== WYNIKI ===");
        log.info("Bez batch: {} ms", time1);
        log.info("Z batch: {} ms", time2);
        log.info("Przyspieszenie: {}x", (double) time1 / time2);
    }

    /**
     * Pomocnicza metoda do czyszczenia danych testowych.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cleanupTestData() {
        log.debug("Czyszczenie danych testowych...");
        pirateRepository.deleteAll();
    }

    /**
     * Demo 4: Update w batch.
     */
    @Transactional
    public void updateInBatch() {
        log.info("=== DEMO: Update w batch ===");
        
        List<Pirate> pirates = pirateRepository.findByRank("Sailor");
        log.info("Znaleziono {} marynarzy do aktualizacji", pirates.size());
        
        long start = System.currentTimeMillis();
        
        for (int i = 0; i < pirates.size(); i++) {
            Pirate pirate = pirates.get(i);
            pirate.setBounty(pirate.getBounty().add(BigDecimal.valueOf(100)));
            
            if (i > 0 && i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        
        entityManager.flush();
        
        long end = System.currentTimeMillis();
        log.info("✅ Zaktualizowano {} piratów w {} ms", pirates.size(), end - start);
    }
}

