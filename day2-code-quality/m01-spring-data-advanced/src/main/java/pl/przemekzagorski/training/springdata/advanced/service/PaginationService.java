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
import pl.przemekzagorski.training.springdata.advanced.repository.PirateRepository;

/**
 * 🎯 Serwis demonstracyjny dla Paginacji (Pageable i Page<T>).
 *
 * KONCEPCJA:
 * - Pageable - obiekt z parametrami paginacji (strona, rozmiar, sortowanie)
 * - Page<T> - wynik z danymi + metadane (total, totalPages, hasNext, etc.)
 *
 * KORZYŚCI:
 * - Nie ładujemy wszystkich danych naraz
 * - Mniejsze zużycie pamięci
 * - Szybsze zapytania
 * - Lepsze UX (infinite scroll, pagination controls)
 *
 * RÓŻNICA vs Pure JPA:
 * - Pure JPA: setFirstResult(), setMaxResults()
 * - Spring Data: Pageable (czytelniejsze, więcej metadanych)
 */
@Service
@Transactional(readOnly = true)
public class PaginationService {

    private static final Logger log = LoggerFactory.getLogger(PaginationService.class);

    private final PirateRepository pirateRepository;

    public PaginationService(PirateRepository pirateRepository) {
        this.pirateRepository = pirateRepository;
    }

    /**
     * Demo 1: Podstawowa paginacja.
     * 
     * UŻYCIE:
     * - PageRequest.of(page, size)
     * - page: numer strony (0-based!)
     * - size: ile elementów na stronie
     */
    public void demonstrateBasicPagination() {
        log.info("=== DEMO: Podstawowa paginacja ===");
        
        // Strona 0, 10 elementów
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pirate> page = pirateRepository.findAll(pageable);
        
        log.info("Strona: {}/{}", page.getNumber() + 1, page.getTotalPages());
        log.info("Elementów na stronie: {}", page.getNumberOfElements());
        log.info("Wszystkich elementów: {}", page.getTotalElements());
        log.info("Czy jest następna strona: {}", page.hasNext());
        
        page.getContent().forEach(p -> log.info("  - {}", p.getName()));
    }

    /**
     * Demo 2: Paginacja z sortowaniem.
     * 
     * SORTOWANIE:
     * - Sort.by("field") - rosnąco
     * - Sort.by("field").descending() - malejąco
     * - Sort.by("field1", "field2") - wiele pól
     */
    public void demonstratePaginationWithSorting() {
        log.info("=== DEMO: Paginacja + sortowanie ===");
        
        // Strona 0, 5 elementów, sortowanie po bounty malejąco
        Pageable pageable = PageRequest.of(0, 5, Sort.by("bounty").descending());
        Page<Pirate> page = pirateRepository.findAll(pageable);
        
        log.info("Top 5 piratów z największym bounty:");
        page.getContent().forEach(p -> 
            log.info("  - {} ({}): {}", p.getName(), p.getRank(), p.getBounty())
        );
    }

    /**
     * Demo 3: Iteracja przez wszystkie strony.
     */
    public void demonstratePageIteration() {
        log.info("=== DEMO: Iteracja przez strony ===");
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pirate> page = pirateRepository.findAll(pageable);
        
        int pageNum = 1;
        while (page.hasContent()) {
            log.info("Strona {}: {} piratów", pageNum, page.getNumberOfElements());
            
            if (!page.hasNext()) {
                break;
            }
            
            page = pirateRepository.findAll(page.nextPageable());
            pageNum++;
        }
        
        log.info("Łącznie {} stron", pageNum);
    }

    /**
     * Demo 4: Paginacja z filtrowaniem.
     * 
     * Łączy query method z paginacją.
     */
    public void demonstratePaginationWithFiltering() {
        log.info("=== DEMO: Paginacja + filtrowanie ===");
        
        // Kapitanowie, sortowani po bounty, strona 0, 5 elementów
        Pageable pageable = PageRequest.of(0, 5, Sort.by("bounty").descending());
        Page<Pirate> captains = pirateRepository.findByRank("Captain", pageable);
        
        log.info("Kapitanowie (strona 1):");
        captains.getContent().forEach(p -> 
            log.info("  - {}: {}", p.getName(), p.getBounty())
        );
        
        log.info("Wszystkich kapitanów: {}", captains.getTotalElements());
    }

    /**
     * Demo 5: Złożone sortowanie.
     */
    public void demonstrateComplexSorting() {
        log.info("=== DEMO: Złożone sortowanie ===");
        
        // Sortowanie: rank rosnąco, potem bounty malejąco
        Sort sort = Sort.by(
            Sort.Order.asc("rank"),
            Sort.Order.desc("bounty")
        );
        
        Pageable pageable = PageRequest.of(0, 20, sort);
        Page<Pirate> page = pirateRepository.findAll(pageable);
        
        log.info("Piraci posortowani po randze i bounty:");
        page.getContent().forEach(p -> 
            log.info("  - {} [{}]: {}", p.getName(), p.getRank(), p.getBounty())
        );
    }
}

