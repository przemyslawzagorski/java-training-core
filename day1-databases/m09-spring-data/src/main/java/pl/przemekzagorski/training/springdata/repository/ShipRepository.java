package pl.przemekzagorski.training.springdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.przemekzagorski.training.springdata.entity.Ship;

import java.util.List;
import java.util.Optional;

/**
 * Repozytorium statków.
 */
public interface ShipRepository extends JpaRepository<Ship, Long> {

    /**
     * Znajduje statek po nazwie.
     */
    Optional<Ship> findByName(String name);

    /**
     * Znajduje statki według typu.
     */
    List<Ship> findByType(String type);

    /**
     * Znajduje statki z załogą (mające przynajmniej jednego pirata).
     */
    @Query("SELECT DISTINCT s FROM Ship s JOIN s.crew c")
    List<Ship> findShipsWithCrew();

    /**
     * Znajduje statek z całą załogą (JOIN FETCH).
     */
    @Query("SELECT s FROM Ship s LEFT JOIN FETCH s.crew WHERE s.id = :id")
    Optional<Ship> findByIdWithCrew(Long id);

    /**
     * Zlicza piratów na statku.
     */
    @Query("SELECT SIZE(s.crew) FROM Ship s WHERE s.id = :id")
    int countCrewMembers(Long id);
}
