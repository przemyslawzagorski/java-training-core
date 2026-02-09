package pl.przemekzagorski.training.springdata.advanced.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import pl.przemekzagorski.training.springdata.advanced.entity.Pirate;
import pl.przemekzagorski.training.springdata.advanced.projection.PirateNameOnly;
import pl.przemekzagorski.training.springdata.advanced.projection.PirateSummary;

import jakarta.persistence.QueryHint;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repozytorium piratów - zaawansowane techniki Spring Data.
 *
 * NOWE w tym module (vs m09-spring-data):
 * - Metody z Pageable (paginacja)
 * - Projekcje (interfejsy PirateNameOnly, PirateSummary)
 * - @QueryHints dla Cache L2
 * - Metody demonstracyjne dla różnych technik
 */
public interface PirateRepository extends JpaRepository<Pirate, Long> {

    // ==========================================
    // PAGINACJA - Pageable i Page<T>
    // ==========================================

    /**
     * Znajdź piratów według rangi z paginacją.
     * Spring Data automatycznie obsługuje Pageable!
     *
     * PRZYKŁAD UŻYCIA:
     * Pageable pageable = PageRequest.of(0, 10, Sort.by("bounty").descending());
     * Page<Pirate> page = repository.findByRank("Captain", pageable);
     */
    Page<Pirate> findByRank(String rank, Pageable pageable);

    /**
     * Znajdź piratów z bounty większym niż podana wartość - z paginacją.
     */
    Page<Pirate> findByBountyGreaterThan(BigDecimal amount, Pageable pageable);

    /**
     * Znajdź wszystkich piratów z paginacją.
     * Nadpisuje domyślną metodę findAll() aby zwracać Page<T>.
     */
    Page<Pirate> findAll(Pageable pageable);

    // ==========================================
    // PROJEKCJE - tylko wybrane pola
    // ==========================================

    /**
     * Projekcja - zwraca tylko name i rank (nie cały obiekt!).
     * Spring Data automatycznie wygeneruje SELECT p.name, p.rank FROM pirates...
     *
     * KORZYŚĆ: Mniej danych z bazy, szybsze zapytanie.
     */
    List<PirateNameOnly> findProjectedByRank(String rank);

    /**
     * Projekcja z paginacją - najlepsze z obu światów!
     */
    Page<PirateSummary> findProjectedByBountyGreaterThan(BigDecimal amount, Pageable pageable);

    // ==========================================
    // QUERY METHODS - podstawowe
    // ==========================================

    List<Pirate> findByRank(String rank);
    
    List<Pirate> findByNameContaining(String namePart);
    
    List<Pirate> findByBountyBetween(BigDecimal min, BigDecimal max);
    
    long countByRank(String rank);

    // ==========================================
    // @QUERY - własne zapytania
    // ==========================================

    /**
     * Znajdź pirata z największym bounty.
     */
    @Query("SELECT p FROM Pirate p WHERE p.bounty = (SELECT MAX(p2.bounty) FROM Pirate p2)")
    Optional<Pirate> findMostWanted();

    /**
     * Znajdź piratów według rangi z JOIN FETCH na ship.
     * Rozwiązuje problem N+1!
     */
    @Query("SELECT p FROM Pirate p LEFT JOIN FETCH p.ship WHERE p.rank = :rank")
    List<Pirate> findByRankWithShip(@Param("rank") String rank);

    /**
     * Zapytanie z Cache L2 - @QueryHints.
     * 
     * cacheable = true oznacza że wyniki zapytania będą cache'owane.
     */
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Query("SELECT p FROM Pirate p WHERE p.rank = :rank")
    List<Pirate> findByRankCacheable(@Param("rank") String rank);

    /**
     * Oblicza sumę bounty dla wszystkich piratów.
     */
    @Query("SELECT COALESCE(SUM(p.bounty), 0) FROM Pirate p")
    BigDecimal sumAllBounties();

    /**
     * Znajdź top N piratów z największym bounty.
     */
    List<Pirate> findTop10ByOrderByBountyDesc();
}

