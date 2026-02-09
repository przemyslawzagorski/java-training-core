package pl.przemekzagorski.training.springdata.advanced.exercises;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
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
import static org.junit.jupiter.api.Assertions.fail;

/**
 * 🏴‍☠️ ĆWICZENIA: Zaawansowane techniki Spring Data JPA
 *
 * KONTEKST:
 * - Dzień 1, m09: Spring Data podstawy ✅
 * - Dzień 2, m01: Spring Data zaawansowane (TO ĆWICZENIE)
 *
 * TEMATY:
 * 1. Paginacja (Pageable, Page<T>)
 * 2. Projekcje (Spring Data Projections)
 * 3. Cache L2 (Hibernate Second Level Cache)
 * 4. Batch Processing (flush/clear)
 * 5. Read-Only Mode (@Transactional(readOnly=true))
 *
 * INSTRUKCJA:
 * 1. Usuń @Disabled z ćwiczenia
 * 2. Przeczytaj komentarze i TODO
 * 3. Zaimplementuj rozwiązanie
 * 4. Uruchom test (powinien być zielony ✅)
 * 5. Sprawdź logi SQL - czy widzisz optymalizację?
 *
 * PODPOWIEDZI:
 * - Sprawdź PirateRepository - tam są gotowe metody!
 * - Sprawdź serwisy (CacheL2DemoService, PaginationService, etc.)
 * - Sprawdź rozwiązania w PerformanceExercisesSolutions.java
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
    "spring.jpa.properties.hibernate.cache.use_query_cache=false"
})
@DisplayName("🏴‍☠️ Ćwiczenia: Zaawansowane techniki Spring Data")
class PerformanceExercises {

    @Autowired
    private PirateRepository pirateRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ==========================================
    // ĆWICZENIE 1: Paginacja ⭐
    // ==========================================

    /**
     * 🎯 CEL: Naucz się używać Pageable i Page<T>
     *
     * ZADANIE:
     * Pobierz pierwszą stronę piratów (10 elementów), posortowanych po bounty malejąco.
     *
     * PODPOWIEDZI:
     * - Użyj PageRequest.of(page, size, sort)
     * - page = 0 (pierwsza strona)
     * - size = 10
     * - Sort.by("bounty").descending()
     * - pirateRepository.findAll(pageable)
     *
     * CO OBSERWOWAĆ W LOGACH:
     * - SELECT ... FROM pirates ORDER BY bounty DESC LIMIT 10
     * - SELECT COUNT(*) FROM pirates (dla totalElements)
     */
    @Test
    @DisplayName("⭐ Ćwiczenie 1: Podstawowa paginacja")
    @Disabled("Usuń @Disabled aby rozwiązać ćwiczenie")
    void exercise1_basicPagination() {
        // TODO: Stwórz Pageable dla pierwszej strony, 10 elementów, sortowanie po bounty DESC
        Pageable pageable = null;  // <-- ZMIEŃ TO

        // TODO: Pobierz stronę piratów
        Page<Pirate> page = null;  // <-- ZMIEŃ TO

        // Asercje
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(0);  // Pierwsza strona
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isGreaterThan(10);

        // Sprawdź sortowanie (pierwszy powinien mieć największe bounty)
        BigDecimal firstBounty = page.getContent().get(0).getBounty();
        BigDecimal secondBounty = page.getContent().get(1).getBounty();
        assertThat(firstBounty).isGreaterThanOrEqualTo(secondBounty);

        fail("Usuń fail() po zaimplementowaniu");
    }

    // ==========================================
    // ĆWICZENIE 2: Paginacja z filtrowaniem ⭐⭐
    // ==========================================

    /**
     * 🎯 CEL: Połącz paginację z query methods
     *
     * ZADANIE:
     * Znajdź kapitanów (rank="Captain"), pierwsza strona, 5 elementów, sortowanie po name.
     *
     * PODPOWIEDZI:
     * - Użyj pirateRepository.findByRank(rank, pageable)
     * - PageRequest.of(0, 5, Sort.by("name"))
     *
     * CO OBSERWOWAĆ:
     * - WHERE rank = 'Captain' ORDER BY name LIMIT 5
     */
    @Test
    @DisplayName("⭐⭐ Ćwiczenie 2: Paginacja + filtrowanie")
    @Disabled("Usuń @Disabled aby rozwiązać ćwiczenie")
    void exercise2_paginationWithFiltering() {
        // TODO: Stwórz Pageable
        Pageable pageable = null;

        // TODO: Znajdź kapitanów z paginacją
        Page<Pirate> captains = null;

        // Asercje
        assertThat(captains).isNotNull();
        assertThat(captains.getContent()).allMatch(p -> p.getRank().equals("Captain"));
        assertThat(captains.getSize()).isEqualTo(5);

        fail("Usuń fail() po zaimplementowaniu");
    }

    // ==========================================
    // ĆWICZENIE 3: Projekcje ⭐⭐
    // ==========================================

    /**
     * 🎯 CEL: Użyj projekcji aby pobrać tylko wybrane pola
     *
     * ZADANIE:
     * Pobierz tylko imiona i rangi kapitanów (bez pełnej encji).
     *
     * PODPOWIEDZI:
     * - Użyj pirateRepository.findProjectedByRank("Captain")
     * - Zwraca List<PirateNameOnly>
     * - PirateNameOnly ma tylko getName() i getRank()
     *
     * CO OBSERWOWAĆ:
     * - SELECT p.name, p.rank FROM pirates WHERE rank = 'Captain'
     * - Brak innych pól (bounty, nickname, version, ship_id)!
     */
    @Test
    @DisplayName("⭐⭐ Ćwiczenie 3: Projekcje")
    @Disabled("Usuń @Disabled aby rozwiązać ćwiczenie")
    void exercise3_projections() {
        // TODO: Pobierz projekcję kapitanów
        List<PirateNameOnly> captains = null;

        // Asercje
        assertThat(captains).isNotEmpty();
        assertThat(captains).allMatch(p -> p.getRank().equals("Captain"));
        assertThat(captains.get(0).getName()).isNotBlank();

        fail("Usuń fail() po zaimplementowaniu");
    }

    // ==========================================
    // ĆWICZENIE 4: Projekcje + Paginacja ⭐⭐⭐
    // ==========================================

    /**
     * 🎯 CEL: Połącz projekcje z paginacją
     *
     * ZADANIE:
     * Pobierz top 10 piratów z bounty > 10000, tylko name i bounty, sortowanie po bounty DESC.
     *
     * PODPOWIEDZI:
     * - Użyj pirateRepository.findProjectedByBountyGreaterThan(amount, pageable)
     * - Zwraca Page<PirateSummary>
     * - PirateSummary ma getName() i getBounty()
     */
    @Test
    @DisplayName("⭐⭐⭐ Ćwiczenie 4: Projekcje + Paginacja")
    @Disabled("Usuń @Disabled aby rozwiązać ćwiczenie")
    void exercise4_projectionsWithPagination() {
        // TODO: Stwórz Pageable
        Pageable pageable = null;

        // TODO: Pobierz projekcję z paginacją
        Page<PirateSummary> topPirates = null;

        // Asercje
        assertThat(topPirates).isNotNull();
        assertThat(topPirates.getContent()).hasSizeLessThanOrEqualTo(10);
        assertThat(topPirates.getContent()).allMatch(p -> p.getBounty().compareTo(BigDecimal.valueOf(10000)) > 0);

        fail("Usuń fail() po zaimplementowaniu");
    }

    // ==========================================
    // ĆWICZENIE 5: Iteracja przez strony ⭐⭐⭐
    // ==========================================

    /**
     * 🎯 CEL: Naucz się iterować przez wszystkie strony
     *
     * ZADANIE:
     * Policz wszystkich piratów iterując przez strony (po 10 na stronie).
     *
     * PODPOWIEDZI:
     * - Zacznij od strony 0
     * - Użyj page.hasNext() aby sprawdzić czy jest następna strona
     * - Użyj page.nextPageable() aby pobrać następną stronę
     * - Sumuj page.getNumberOfElements() z każdej strony
     */
    @Test
    @DisplayName("⭐⭐⭐ Ćwiczenie 5: Iteracja przez strony")
    @Disabled("Usuń @Disabled aby rozwiązać ćwiczenie")
    void exercise5_pageIteration() {
        // TODO: Stwórz Pageable dla pierwszej strony
        Pageable pageable = PageRequest.of(0, 10);

        // TODO: Pobierz pierwszą stronę
        Page<Pirate> page = pirateRepository.findAll(pageable);

        int totalCounted = 0;

        // TODO: Iteruj przez wszystkie strony i sumuj elementy
        while (page.hasContent()) {
            // TODO: Dodaj liczbę elementów na tej stronie
            totalCounted += 0;  // <-- ZMIEŃ TO

            // TODO: Sprawdź czy jest następna strona, jeśli nie - break
            // TODO: Pobierz następną stronę

            fail("Zaimplementuj iterację");
        }

        // Asercje
        assertThat(totalCounted).isEqualTo(page.getTotalElements());

        fail("Usuń fail() po zaimplementowaniu");
    }

    // ==========================================
    // ĆWICZENIE 6: Cache L2 ⭐⭐⭐⭐
    // ==========================================

    /**
     * 🎯 CEL: Zrozum działanie Cache L2
     *
     * ZADANIE:
     * Pobierz pirata dwa razy i sprawdź czy drugie pobranie użyło cache.
     *
     * PODPOWIEDZI:
     * - Pobierz pirata po ID
     * - Wyczyść Cache L1: entityManager.clear()
     * - Pobierz tego samego pirata ponownie
     * - Sprawdź logi SQL - drugie pobranie NIE powinno wykonać SELECT!
     *
     * CO OBSERWOWAĆ:
     * - Pierwsze pobranie: SELECT ... FROM pirates WHERE id = ?
     * - entityManager.clear()
     * - Drugie pobranie: BRAK SELECT (dane z Cache L2!)
     */
    @Test
    @DisplayName("⭐⭐⭐⭐ Ćwiczenie 6: Cache L2")
    @Disabled("Usuń @Disabled aby rozwiązać ćwiczenie")
    void exercise6_cacheL2() {
        // TODO: Pobierz pirata ID=1
        Pirate pirate1 = null;

        // TODO: Wyczyść Cache L1
        // entityManager.clear();

        // TODO: Pobierz tego samego pirata ponownie
        Pirate pirate2 = null;

        // Asercje
        assertThat(pirate1).isNotNull();
        assertThat(pirate2).isNotNull();
        assertThat(pirate1.getId()).isEqualTo(pirate2.getId());
        assertThat(pirate1.getName()).isEqualTo(pirate2.getName());

        // UWAGA: pirate1 != pirate2 (różne instancje), ale dane te same!

        fail("Usuń fail() po zaimplementowaniu");
    }

    // ==========================================
    // BONUS: Złożone sortowanie ⭐⭐⭐⭐⭐
    // ==========================================

    /**
     * 🎯 CEL: Zaawansowane sortowanie
     *
     * ZADANIE:
     * Pobierz piratów posortowanych po:
     * 1. rank (rosnąco)
     * 2. bounty (malejąco)
     *
     * PODPOWIEDZI:
     * - Sort.by(Sort.Order.asc("rank"), Sort.Order.desc("bounty"))
     */
    @Test
    @DisplayName("⭐⭐⭐⭐⭐ BONUS: Złożone sortowanie")
    @Disabled("Usuń @Disabled jeśli chcesz rozwiązać bonus")
    void exerciseBonus_complexSorting() {
        // TODO: Stwórz złożone sortowanie
        Sort sort = null;

        // TODO: Stwórz Pageable z tym sortowaniem
        Pageable pageable = null;

        // TODO: Pobierz stronę
        Page<Pirate> page = null;

        // Asercje
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();

        // Sprawdź sortowanie
        List<Pirate> pirates = page.getContent();
        for (int i = 0; i < pirates.size() - 1; i++) {
            Pirate current = pirates.get(i);
            Pirate next = pirates.get(i + 1);

            // Jeśli rank taki sam, bounty powinno być większe lub równe
            if (current.getRank().equals(next.getRank())) {
                assertThat(current.getBounty()).isGreaterThanOrEqualTo(next.getBounty());
            }
        }

        fail("Usuń fail() po zaimplementowaniu");
    }
}

