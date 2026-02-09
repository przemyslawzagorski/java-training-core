package pl.przemekzagorski.training.springdata.advanced.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.przemekzagorski.training.springdata.advanced.service.*;

/**
 * 🎯 Demo zaawansowanych funkcji Spring Data JPA.
 *
 * URUCHOMIENIE:
 * mvn spring-boot:run -pl day2-code-quality/m01-spring-data-advanced -Ddemo.enabled=true
 *
 * LUB w application.yml:
 * demo:
 *   enabled: true
 *
 * DEMONSTROWANE FUNKCJE:
 * 1. Cache L2 (Hibernate Second Level Cache)
 * 2. Paginacja (Pageable, Page<T>)
 * 3. Projekcje (Spring Data Projections)
 * 4. Batch Processing (flush/clear)
 * 5. Read-Only Mode (@Transactional(readOnly=true))
 */
@Component
@ConditionalOnProperty(name = "demo.enabled", havingValue = "true", matchIfMissing = false)
public class AdvancedFeaturesDemo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdvancedFeaturesDemo.class);

    private final CacheL2DemoService cacheL2DemoService;
    private final PaginationService paginationService;
    private final ProjectionService projectionService;
    private final BatchProcessingService batchProcessingService;
    private final ReadOnlyService readOnlyService;

    public AdvancedFeaturesDemo(
            CacheL2DemoService cacheL2DemoService,
            PaginationService paginationService,
            ProjectionService projectionService,
            BatchProcessingService batchProcessingService,
            ReadOnlyService readOnlyService) {
        this.cacheL2DemoService = cacheL2DemoService;
        this.paginationService = paginationService;
        this.projectionService = projectionService;
        this.batchProcessingService = batchProcessingService;
        this.readOnlyService = readOnlyService;
    }

    @Override
    public void run(String... args) {
        log.info("\n\n");
        log.info("=".repeat(80));
        log.info("🏴‍☠️ SPRING DATA ADVANCED - DEMO ZAAWANSOWANYCH FUNKCJI");
        log.info("=".repeat(80));
        log.info("\n");

        // 1. Cache L2
        //runCacheL2Demo();

        // 2. Paginacja
        //runPaginationDemo();

        // 3. Projekcje
        //runProjectionDemo();

        // 4. Batch Processing
        //runBatchProcessingDemo();

        // 5. Read-Only Mode
        runReadOnlyDemo();

        log.info("\n");
        log.info("=".repeat(80));
        log.info("✅ DEMO ZAKOŃCZONE");
        log.info("=".repeat(80));
        log.info("\n\n");
    }

    private void runCacheL2Demo() {
        log.info("\n" + "=".repeat(80));
        log.info("1️⃣ CACHE L2 DEMO");
        log.info("=".repeat(80) + "\n");

        // Demo 1: Entity Cache (findById) - pokazuje hit po drugim pobraniu
        cacheL2DemoService.demonstrateCacheHit();

        // Demo 2: Query Cache (findByRankCacheable) - pokazuje query cache hit
        cacheL2DemoService.demonstrateQueryCache();
    }

    private void runPaginationDemo() {
        log.info("\n" + "=".repeat(80));
        log.info("2️⃣ PAGINACJA DEMO");
        log.info("=".repeat(80) + "\n");

        paginationService.demonstrateBasicPagination();
        paginationService.demonstratePaginationWithSorting();
        paginationService.demonstratePaginationWithFiltering();
    }

    private void runProjectionDemo() {
        log.info("\n" + "=".repeat(80));
        log.info("3️⃣ PROJEKCJE DEMO");
        log.info("=".repeat(80) + "\n");

        projectionService.demonstrateBasicProjection();
        projectionService.demonstrateProjectionWithPagination();
        projectionService.comparePerformance();
        projectionService.demonstrateUseCases();
    }

    private void runBatchProcessingDemo() {
        log.info("\n" + "=".repeat(80));
        log.info("4️⃣ BATCH PROCESSING DEMO");
        log.info("=".repeat(80) + "\n");

        // Uwaga: to może trochę potrwać
        log.info("⏳ Import 100 piratów (test wydajności)...");
      //  batchProcessingService.importWithBatch(100);

        batchProcessingService.comparePerformance();
    }

    private void runReadOnlyDemo() {
        log.info("\n" + "=".repeat(80));
        log.info("5️⃣ READ-ONLY MODE DEMO");
        log.info("=".repeat(80) + "\n");

        readOnlyService.findAllPirates();
        //readOnlyService.attemptModificationInReadOnly();
        readOnlyService.demonstrateBestPractices();
    }
}

