package pl.przemekzagorski.training.springdata.advanced.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.przemekzagorski.training.springdata.advanced.entity.Pirate;
import pl.przemekzagorski.training.springdata.advanced.projection.PirateNameOnly;
import pl.przemekzagorski.training.springdata.advanced.projection.PirateSummary;
import pl.przemekzagorski.training.springdata.advanced.repository.PirateRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 🎯 Serwis demonstracyjny dla Projekcji (Spring Data Projections).
 *
 * KONCEPCJA:
 * - Projekcja = pobieranie tylko wybranych pól z encji
 * - Zamiast SELECT * FROM pirates, wykonuje SELECT name, rank FROM pirates
 * - Interfejs z getterami - Spring Data generuje implementację
 *
 * KORZYŚCI:
 * - Mniej danych z bazy
 * - Szybsze zapytania
 * - Mniejsze zużycie pamięci
 * - Można łączyć z paginacją
 *
 * RÓŻNICA vs Pure JPA:
 * - Pure JPA: SELECT NEW dto.PirateDTO(p.name, p.rank) FROM Pirate p
 * - Spring Data: Interfejs + automatyczna implementacja
 */
@Service
@Transactional(readOnly = true)
public class ProjectionService {

    private static final Logger log = LoggerFactory.getLogger(ProjectionService.class);

    private final PirateRepository pirateRepository;

    public ProjectionService(PirateRepository pirateRepository) {
        this.pirateRepository = pirateRepository;
    }

    /**
     * Demo 1: Podstawowa projekcja.
     * 
     * Porównuje pełną encję vs projekcję.
     */
    public void demonstrateBasicProjection() {
        log.info("=== DEMO: Podstawowa projekcja ===");
        
        // Pełna encja - wszystkie pola
        log.info("1. Pełna encja (wszystkie pola):");
        List<Pirate> fullPirates = pirateRepository.findByRank("Captain");
        log.info("Pobranych kapitanów: {}", fullPirates.size());
        fullPirates.forEach(p -> log.info("  - {} [{}]", p.getName(), p.getRank()));
        
        // Projekcja - tylko name i rank
        log.info("2. Projekcja (tylko name i rank):");
        List<PirateNameOnly> projectedPirates = pirateRepository.findProjectedByRank("Captain");
        log.info("Pobranych kapitanów: {}", projectedPirates.size());
        projectedPirates.forEach(p -> log.info("  - {} [{}]", p.getName(), p.getRank()));
        
        log.info("✅ Projekcja: mniej danych, szybsze zapytanie!");
    }

    /**
     * Demo 2: Projekcja z paginacją.
     * 
     * Najlepsza kombinacja - tylko potrzebne pola + paginacja!
     */
    public void demonstrateProjectionWithPagination() {
        log.info("=== DEMO: Projekcja + paginacja ===");
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bounty").descending());
        Page<PirateSummary> page = pirateRepository.findProjectedByBountyGreaterThan(
            BigDecimal.valueOf(10000), 
            pageable
        );
        
        log.info("Top 10 piratów z bounty > 10000:");
        log.info("Strona: {}/{}", page.getNumber() + 1, page.getTotalPages());
        log.info("Wszystkich: {}", page.getTotalElements());
        
        page.getContent().forEach(p -> 
            log.info("  - {}: {}", p.getName(), p.getBounty())
        );
    }

    /**
     * Demo 3: Porównanie wydajności.
     * 
     * OBSERWUJ w logach SQL:
     * - Pełna encja: SELECT id, name, rank, bounty, nickname, version, ship_id FROM pirates
     * - Projekcja: SELECT name, rank FROM pirates
     */
    public void comparePerformance() {
        log.info("=== DEMO: Porównanie wydajności ===");
        
        long start, end;
        
        // Test 1: Pełna encja
        start = System.currentTimeMillis();
        List<Pirate> fullList = pirateRepository.findByRank("Sailor");
        end = System.currentTimeMillis();
        log.info("Pełna encja: {} rekordów w {} ms", fullList.size(), end - start);
        
        // Test 2: Projekcja
        start = System.currentTimeMillis();
        List<PirateNameOnly> projectedList = pirateRepository.findProjectedByRank("Sailor");
        end = System.currentTimeMillis();
        log.info("Projekcja: {} rekordów w {} ms", projectedList.size(), end - start);
        
        log.info("✅ Projekcja jest szybsza (mniej danych do transferu i mapowania)");
    }

    /**
     * Demo 4: Kiedy używać projekcji?
     * 
     * UŻYJ PROJEKCJI gdy:
     * - Potrzebujesz tylko kilku pól
     * - Tworzysz listy/raporty
     * - Optymalizujesz wydajność
     * - Masz duże encje z wieloma polami
     *
     * NIE UŻYWAJ PROJEKCJI gdy:
     * - Potrzebujesz pełnej encji do modyfikacji
     * - Potrzebujesz relacji (@ManyToOne, @OneToMany)
     * - Encja jest mała (niewielka różnica wydajności)
     */
    public void demonstrateUseCases() {
        log.info("=== DEMO: Przypadki użycia projekcji ===");
        
        // Use case 1: Dropdown list (tylko nazwy)
        log.info("1. Dropdown list - tylko nazwy:");
        List<PirateNameOnly> dropdown = pirateRepository.findProjectedByRank("Captain");
        dropdown.forEach(p -> log.info("  - {}", p.getName()));
        
        // Use case 2: Ranking (nazwa + bounty)
        log.info("2. Ranking piratów:");
        Pageable top10 = PageRequest.of(0, 10, Sort.by("bounty").descending());
        Page<PirateSummary> ranking = pirateRepository.findProjectedByBountyGreaterThan(
            BigDecimal.ZERO, 
            top10
        );
        ranking.getContent().forEach(p -> 
            log.info("  - {}: {}", p.getName(), p.getBounty())
        );
    }
}

