package pl.przemekzagorski.training.springdata.advanced.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import pl.przemekzagorski.training.springdata.advanced.entity.Ship;
import pl.przemekzagorski.training.springdata.advanced.projection.ShipSummary;

import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

/**
 * Repozytorium statków - zaawansowane techniki Spring Data.
 *
 * NOWE w tym module (vs m09-spring-data):
 * - Metody z Pageable (paginacja)
 * - Projekcje (ShipSummary)
 * - @QueryHints dla Cache L2
 * - Metody demonstrujące @BatchSize
 */
public interface ShipRepository extends JpaRepository<Ship, Long> {

    // ==========================================
    // PODSTAWOWE QUERY METHODS
    // ==========================================

    /**
     * Znajduje statek po nazwie.
     */
    Optional<Ship> findByName(String name);

    /**
     * Znajduje statki według typu.
     */
    List<Ship> findByType(String type);

    /**
     * Znajduje statki według typu z paginacją.
     */
    Page<Ship> findByType(String type, Pageable pageable);

    /**
     * Znajduje wszystkie statki z paginacją.
     */
    Page<Ship> findAll(Pageable pageable);

    // ==========================================
    // PROJEKCJE
    // ==========================================

    /**
     * Projekcja - tylko nazwa i typ statku.
     * Szybsze zapytanie, mniej danych.
     */
    List<ShipSummary> findProjectedByType(String type);

    /**
     * Projekcja z paginacją.
     */
    Page<ShipSummary> findProjectedBy(Pageable pageable);

    // ==========================================
    // @QUERY - własne zapytania
    // ==========================================

    /**
     * Znajduje statki z załogą (mające przynajmniej jednego pirata).
     */
    @Query("SELECT DISTINCT s FROM Ship s JOIN s.crew c")
    List<Ship> findShipsWithCrew();

    /**
     * Znajduje statek z całą załogą (JOIN FETCH).
     * Rozwiązuje problem N+1!
     */
    @Query("SELECT s FROM Ship s LEFT JOIN FETCH s.crew WHERE s.id = :id")
    Optional<Ship> findByIdWithCrew(@Param("id") Long id);

    /**
     * Zlicza piratów na statku.
     */
    @Query("SELECT SIZE(s.crew) FROM Ship s WHERE s.id = :id")
    int countCrewMembers(@Param("id") Long id);

    /**
     * Znajduje statki z załogą większą niż podana liczba.
     */
    @Query("SELECT s FROM Ship s WHERE SIZE(s.crew) > :minCrew")
    List<Ship> findShipsWithCrewLargerThan(@Param("minCrew") int minCrew);

    /**
     * Znajduje statki z załogą - z Cache L2.
     * 
     * UWAGA: To zapytanie będzie cache'owane!
     */
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Query("SELECT s FROM Ship s WHERE s.type = :type")
    List<Ship> findByTypeCacheable(@Param("type") String type);

    // ==========================================
    // DEMONSTRACJA @BatchSize
    // ==========================================

    /**
     * Znajduje wszystkie statki (bez JOIN FETCH).
     * 
     * DEMONSTRACJA @BatchSize:
     * - Bez @BatchSize: 1 + N zapytań (N+1 problem)
     * - Z @BatchSize(10): 1 + ceil(N/10) zapytań
     * 
     * PRZYKŁAD dla 20 statków:
     * - Bez @BatchSize: 1 + 20 = 21 zapytań
     * - Z @BatchSize(10): 1 + 2 = 3 zapytania!
     * 
     * UŻYCIE:
     * List<Ship> ships = repository.findAllShipsForBatchSizeDemo();
     * ships.forEach(ship -> {
     *     System.out.println(ship.getName() + " crew: " + ship.getCrew().size());
     *     // Tutaj @BatchSize robi robotę!
     * });
     */
    @Query("SELECT s FROM Ship s")
    List<Ship> findAllShipsForBatchSizeDemo();

    /**
     * Znajduje top 5 statków z największą liczbą dział.
     */
    List<Ship> findTop5ByOrderByCannonsDesc();
}

