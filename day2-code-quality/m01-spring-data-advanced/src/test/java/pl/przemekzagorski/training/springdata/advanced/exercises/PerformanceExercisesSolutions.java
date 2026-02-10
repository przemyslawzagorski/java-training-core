package pl.przemekzagorski.training.springdata.advanced.exercises;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import pl.przemekzagorski.training.springdata.advanced.entity.Pirate;
import pl.przemekzagorski.training.springdata.advanced.projection.PirateNameOnly;
import pl.przemekzagorski.training.springdata.advanced.projection.PirateSummary;
import pl.przemekzagorski.training.springdata.advanced.repository.PirateRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 🏴‍☠️ ROZWIĄZANIA: Zaawansowane techniki Spring Data JPA
 *
 * To są kompletne rozwiązania ćwiczeń z PerformanceExercises.java.
 * Sprawdź je jeśli utknąłeś lub chcesz porównać swoje rozwiązanie.
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
    "spring.jpa.properties.hibernate.cache.use_query_cache=false"
})
@DisplayName("🏴‍☠️ Rozwiązania: Zaawansowane techniki Spring Data")
class PerformanceExercisesSolutions {

    @Autowired
    private PirateRepository pirateRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("⭐ Rozwiązanie 1: Podstawowa paginacja")
    void solution1_basicPagination() {
        // Stwórz Pageable: strona 0, 10 elementów, sortowanie po bounty DESC
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bounty").descending());

        // Pobierz stronę
        Page<Pirate> page = pirateRepository.findAll(pageable);

        // Asercje
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isGreaterThan(10);

        // Sprawdź sortowanie
        BigDecimal firstBounty = page.getContent().get(0).getBounty();
        BigDecimal secondBounty = page.getContent().get(1).getBounty();
        assertThat(firstBounty).isGreaterThanOrEqualTo(secondBounty);
    }

    @Test
    @DisplayName("⭐⭐ Rozwiązanie 2: Paginacja + filtrowanie")
    void solution2_paginationWithFiltering() {
        // Pageable: strona 0, 5 elementów, sortowanie po name
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));

        // Znajdź kapitanów
        Page<Pirate> captains = pirateRepository.findByRank("Captain", pageable);

        // Asercje
        assertThat(captains).isNotNull();
        assertThat(captains.getContent()).allMatch(p -> p.getRank().equals("Captain"));
        assertThat(captains.getSize()).isEqualTo(5);
    }

    @Test
    @DisplayName("⭐⭐ Rozwiązanie 3: Projekcje")
    void solution3_projections() {
        // Pobierz projekcję kapitanów
        List<PirateNameOnly> captains = pirateRepository.findProjectedByRank("Captain");

        // Asercje
        assertThat(captains).isNotEmpty();
        assertThat(captains).allMatch(p -> p.getRank().equals("Captain"));
        assertThat(captains.get(0).getName()).isNotBlank();

        // OBSERWUJ w logach: SELECT p.name, p.rank FROM pirates WHERE rank = 'Captain'
    }

    @Test
    @DisplayName("⭐⭐⭐ Rozwiązanie 4: Projekcje + Paginacja")
    void solution4_projectionsWithPagination() {
        // Pageable: strona 0, 10 elementów, sortowanie po bounty DESC
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bounty").descending());

        // Pobierz projekcję z paginacją
        Page<PirateSummary> topPirates = pirateRepository.findProjectedByBountyGreaterThan(
            BigDecimal.valueOf(10000),
            pageable
        );

        // Asercje
        assertThat(topPirates).isNotNull();
        assertThat(topPirates.getContent()).hasSizeLessThanOrEqualTo(10);
        assertThat(topPirates.getContent()).allMatch(p -> p.getBounty().compareTo(BigDecimal.valueOf(10000)) > 0);
    }

    @Test
    @DisplayName("⭐⭐⭐ Rozwiązanie 5: Iteracja przez strony")
    void solution5_pageIteration() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pirate> page = pirateRepository.findAll(pageable);

        int totalCounted = 0;

        // Iteruj przez wszystkie strony
        while (page.hasContent()) {
            totalCounted += page.getNumberOfElements();

            if (!page.hasNext()) {
                break;
            }

            page = pirateRepository.findAll(page.nextPageable());
        }

        // Asercje
        assertThat(totalCounted).isEqualTo(page.getTotalElements());
    }

    @Test
    @DisplayName("⭐⭐⭐⭐ Rozwiązanie 6: Cache L2")
    void solution6_cacheL2() {
        // Pierwsze pobranie - z bazy
        Pirate pirate1 = pirateRepository.findById(1L).orElseThrow();

        // Wyczyść Cache L1 (Session cache)
        entityManager.clear();

        // Drugie pobranie - z Cache L2!
        Pirate pirate2 = pirateRepository.findById(1L).orElseThrow();

        // Asercje
        assertThat(pirate1).isNotNull();
        assertThat(pirate2).isNotNull();
        assertThat(pirate1.getId()).isEqualTo(pirate2.getId());
        assertThat(pirate1.getName()).isEqualTo(pirate2.getName());

        // OBSERWUJ w logach:
        // - Pierwsze pobranie: SELECT ... FROM pirates WHERE id = 1
        // - Drugie pobranie: BRAK SELECT (dane z Cache L2!)
    }

    @Test
    @DisplayName("⭐⭐⭐⭐⭐ BONUS: Złożone sortowanie")
    void solutionBonus_complexSorting() {
        // Złożone sortowanie: rank ASC, bounty DESC
        Sort sort = Sort.by(
            Sort.Order.asc("rank"),
            Sort.Order.desc("bounty")
        );

        Pageable pageable = PageRequest.of(0, 20, sort);
        Page<Pirate> page = pirateRepository.findAll(pageable);

        // Asercje
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();

        // Sprawdź sortowanie
        List<Pirate> pirates = page.getContent();
        for (int i = 0; i < pirates.size() - 1; i++) {
            Pirate current = pirates.get(i);
            Pirate next = pirates.get(i + 1);

            if (current.getRank().equals(next.getRank())) {
                assertThat(current.getBounty()).isGreaterThanOrEqualTo(next.getBounty());
            }
        }
    }
}

