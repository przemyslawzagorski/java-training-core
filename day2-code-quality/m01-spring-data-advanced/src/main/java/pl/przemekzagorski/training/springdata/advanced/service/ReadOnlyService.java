package pl.przemekzagorski.training.springdata.advanced.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.przemekzagorski.training.springdata.advanced.entity.Pirate;
import pl.przemekzagorski.training.springdata.advanced.repository.PirateRepository;

import java.util.List;

/**
 * 🎯 Serwis demonstracyjny dla Read-Only Mode.
 *
 * KONCEPCJA:
 * - @Transactional(readOnly = true) - optymalizacja dla zapytań tylko do odczytu
 * - Hibernate nie sprawdza dirty checking (czy encje się zmieniły)
 * - Baza danych może zoptymalizować transakcję (np. nie tworzyć undo log)
 *
 * KORZYŚCI:
 * - Szybsze zapytania (brak dirty checking)
 * - Mniejsze zużycie pamięci
 * - Lepsza wydajność bazy danych
 * - Jasna intencja kodu (to jest tylko odczyt!)
 *
 * RÓŻNICA vs Pure JPA:
 * - Pure JPA: session.setDefaultReadOnly(true) lub query.setHint("org.hibernate.readOnly", true)
 * - Spring: @Transactional(readOnly = true) - czytelniejsze!
 *
 * KIEDY UŻYWAĆ:
 * - Wszystkie metody serwisowe tylko do odczytu
 * - Raporty, statystyki, listy
 * - API GET endpoints
 */
@Service
@Transactional(readOnly = true)  // ✅ Domyślnie wszystkie metody read-only
public class ReadOnlyService {

    private static final Logger log = LoggerFactory.getLogger(ReadOnlyService.class);

    private final PirateRepository pirateRepository;

    public ReadOnlyService(PirateRepository pirateRepository) {
        this.pirateRepository = pirateRepository;
    }

    /**
     * Demo 1: Odczyt z readOnly=true.
     * 
     * OBSERWUJ w logach:
     * - Brak dirty checking
     * - Transakcja oznaczona jako read-only
     */
    public List<Pirate> findAllPirates() {
        log.info("=== DEMO: Read-only query ===");
        List<Pirate> pirates = pirateRepository.findAll();
        log.info("Znaleziono {} piratów (read-only mode)", pirates.size());
        return pirates;
    }

    /**
     * Demo 2: Próba modyfikacji w read-only (NIE ZADZIAŁA).
     * 
     * UWAGA:
     * - Modyfikacja encji w read-only mode NIE zostanie zapisana
     * - Hibernate pominie dirty checking
     * - Brak UPDATE w bazie danych
     */
    public void attemptModificationInReadOnly() {
        log.info("=== DEMO: Próba modyfikacji w read-only ===");
        
        Pirate pirate = pirateRepository.findById(1L).orElseThrow();
        log.info("Przed: {}", pirate.getName());
        
        // Ta zmiana NIE zostanie zapisana!
        pirate.setName("Modified Name");
        log.info("Po modyfikacji: {}", pirate.getName());
        
        log.warn("⚠️ Zmiana NIE zostanie zapisana (read-only mode)");
    }

    /**
     * Demo 3: Porównanie read-only vs read-write.
     */
    public void demonstrateReadOnlyBenefit() {
        log.info("=== DEMO: Korzyści read-only ===");
        
        // Read-only (ta metoda)
        long start1 = System.currentTimeMillis();
        List<Pirate> pirates1 = pirateRepository.findAll();
        long time1 = System.currentTimeMillis() - start1;
        log.info("Read-only: {} piratów w {} ms", pirates1.size(), time1);
        
        log.info("✅ Read-only: brak dirty checking, szybsze zapytanie");
    }

    /**
     * Demo 4: Kiedy NIE używać readOnly=true.
     * 
     * NIE UŻYWAJ gdy:
     * - Planujesz modyfikować encje
     * - Wywołujesz metody @Transactional (bez readOnly)
     * - Potrzebujesz zapisać zmiany
     */
    @Transactional  // ✅ Nadpisuje domyślne readOnly=true
    public void saveNewPirate(Pirate pirate) {
        log.info("=== DEMO: Zapis (read-write mode) ===");
        pirateRepository.save(pirate);
        log.info("✅ Pirat zapisany: {}", pirate.getName());
    }

    /**
     * Demo 5: Best practices.
     * 
     * BEST PRACTICES:
     * 1. Domyślnie @Transactional(readOnly=true) na klasie serwisu
     * 2. Nadpisz @Transactional (bez readOnly) na metodach modyfikujących
     * 3. Używaj w repozytoriach query (findAll, findBy...)
     * 4. Używaj w serwisach raportowych
     */
    public void demonstrateBestPractices() {
        log.info("=== DEMO: Best practices ===");
        
        // ✅ DOBRZE: read-only dla odczytu
        List<Pirate> allPirates = pirateRepository.findAll();
        log.info("Wszystkich piratów: {}", allPirates.size());
        
        // ✅ DOBRZE: read-only dla statystyk
        long captainCount = pirateRepository.countByRank("Captain");
        log.info("Kapitanów: {}", captainCount);
        
        // ✅ DOBRZE: read-only dla raportów
        List<Pirate> topPirates = pirateRepository.findTop10ByOrderByBountyDesc();
        log.info("Top 10 piratów: {}", topPirates.size());
        
        log.info("✅ Wszystkie operacje w read-only mode - optymalne!");
    }
}

