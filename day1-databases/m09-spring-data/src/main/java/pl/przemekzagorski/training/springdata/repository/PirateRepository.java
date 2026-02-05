package pl.przemekzagorski.training.springdata.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.przemekzagorski.training.springdata.entity.Pirate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repozytorium piratów - pokazuje pełną moc Spring Data JPA.
 *
 * Rozszerzając JpaRepository<Pirate, Long> otrzymujesz ZA DARMO:
 * - save(entity), saveAll(entities)
 * - findById(id), findAll(), findAllById(ids)
 * - count(), existsById(id)
 * - delete(entity), deleteById(id), deleteAll()
 * - i wiele więcej!
 *
 * Dodatkowo możesz definiować własne metody:
 * - Query Methods (nazwy metod jako zapytania)
 * - @Query (JPQL lub native SQL)
 */
public interface PirateRepository extends JpaRepository<Pirate, Long> {

    // ==========================================
    // QUERY METHODS - Zapytania z nazw metod
    // ==========================================

    /**
     * Znajduje piratów według rangi.
     * Spring generuje: SELECT * FROM pirates WHERE rank = ?
     */
    List<Pirate> findByRank(String rank);

    /**
     * Znajduje piratów według nazwy (case insensitive).
     * Spring generuje: SELECT * FROM pirates WHERE LOWER(name) = LOWER(?)
     */
    List<Pirate> findByNameIgnoreCase(String name);

    /**
     * Znajduje piratów z bounty większym niż podana wartość.
     * Sortuje malejąco po bounty.
     */
    List<Pirate> findByBountyGreaterThanOrderByBountyDesc(BigDecimal amount);

    /**
     * Znajduje piratów z bounty w podanym zakresie.
     */
    List<Pirate> findByBountyBetween(BigDecimal min, BigDecimal max);

    /**
     * Znajduje piratów których imię zawiera podany tekst.
     * Spring generuje: SELECT * FROM pirates WHERE name LIKE '%?%'
     */
    List<Pirate> findByNameContaining(String namePart);

    /**
     * Znajduje piratów według rangi i minimalnego bounty.
     */
    List<Pirate> findByRankAndBountyGreaterThan(String rank, BigDecimal minBounty);

    /**
     * Znajduje piratów bez statku.
     */
    List<Pirate> findByShipIsNull();

    /**
     * Zlicza piratów według rangi.
     */
    long countByRank(String rank);

    /**
     * Sprawdza czy istnieje pirat o podanej nazwie.
     */
    boolean existsByName(String name);

    /**
     * Znajduje top 3 piratów z największym bounty.
     */
    List<Pirate> findTop3ByOrderByBountyDesc();

    /**
     * Znajduje pierwszego pirata o podanej randze.
     */
    Optional<Pirate> findFirstByRank(String rank);

    // ==========================================
    // @QUERY - Własne zapytania JPQL
    // ==========================================

    /**
     * Znajduje pirata z największym bounty.
     */
    @Query("SELECT p FROM Pirate p WHERE p.bounty = (SELECT MAX(p2.bounty) FROM Pirate p2)")
    Optional<Pirate> findMostWanted();

    /**
     * Znajduje piratów według rangi z JOIN FETCH na ship.
     * Rozwiązuje problem N+1!
     */
    @Query("SELECT p FROM Pirate p LEFT JOIN FETCH p.ship WHERE p.rank = :rank")
    List<Pirate> findByRankWithShip(@Param("rank") String rank);

    /**
     * Oblicza sumę bounty dla wszystkich piratów.
     */
    @Query("SELECT COALESCE(SUM(p.bounty), 0) FROM Pirate p")
    BigDecimal sumAllBounties();

    /**
     * Średnie bounty według rangi.
     */
    @Query("SELECT p.rank, AVG(p.bounty) FROM Pirate p GROUP BY p.rank")
    List<Object[]> avgBountyByRank();

    // ==========================================
    // @MODIFYING - Zapytania modyfikujące
    // ==========================================

    /**
     * Zwiększa bounty wszystkich piratów o podany procent.
     * WAŻNE: @Modifying + @Transactional
     */
    @Modifying
    @Transactional
    @Query("UPDATE Pirate p SET p.bounty = p.bounty * (1 + :percent/100) WHERE p.rank = :rank")
    int increaseBountyForRank(@Param("rank") String rank, @Param("percent") BigDecimal percent);

    /**
     * Usuwa piratów z bounty = 0.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Pirate p WHERE p.bounty = 0")
    int deleteZeroBountyPirates();

    // ==========================================
    // ENTITY GRAPH - Eager loading
    // ==========================================

    /**
     * Znajduje pirata po ID z załadowanym statkiem.
     * Alternatywa dla JOIN FETCH.
     */
    @EntityGraph(attributePaths = {"ship"})
    Optional<Pirate> findWithShipById(Long id);

    /**
     * Znajduje wszystkich piratów z załadowanymi statkami.
     */
    @EntityGraph(attributePaths = {"ship"})
    List<Pirate> findAllWithShipBy();
}
