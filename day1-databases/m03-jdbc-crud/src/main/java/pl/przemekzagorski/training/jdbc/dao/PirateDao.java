package pl.przemekzagorski.training.jdbc.dao;

import pl.przemekzagorski.training.jdbc.model.Pirate;
import java.util.List;
import java.util.Optional;

/**
 * Interfejs DAO (Data Access Object) dla piratów.
 */
public interface PirateDao {
    Pirate save(Pirate pirate);
    Optional<Pirate> findById(Long id);
    List<Pirate> findAll();
    List<Pirate> findByRank(String rank);
    void update(Pirate pirate);
    void delete(Long id);
    long count();
}

