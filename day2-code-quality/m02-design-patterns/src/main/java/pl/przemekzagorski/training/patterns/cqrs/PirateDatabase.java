package pl.przemekzagorski.training.patterns.cqrs;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Prosta "baza danych" piratów (in-memory).
 * 
 * 🏴‍☠️ W prawdziwej aplikacji to byłby JPA Repository lub DAO.
 */
public class PirateDatabase {
    private final Map<Long, Pirate> pirates = new HashMap<>();

    public void save(Pirate pirate) {
        pirates.put(pirate.getId(), pirate);
    }

    public Optional<Pirate> findById(Long id) {
        return Optional.ofNullable(pirates.get(id));
    }

    public List<Pirate> findByRank(String rank) {
        return pirates.values().stream()
            .filter(p -> p.getRank().equalsIgnoreCase(rank))
            .collect(Collectors.toList());
    }

    public List<Pirate> findAll() {
        return new ArrayList<>(pirates.values());
    }

    public void clear() {
        pirates.clear();
    }
}

