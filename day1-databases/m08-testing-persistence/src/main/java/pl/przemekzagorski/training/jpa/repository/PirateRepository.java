package pl.przemekzagorski.training.jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import pl.przemekzagorski.training.jpa.entity.Pirate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repozytorium do operacji CRUD na encji Pirate.
 *
 * Wzorzec: Repository Pattern
 * - Ukrywa szczegóły implementacji dostępu do danych
 * - Udostępnia metody domenowe
 */
public class PirateRepository {

    private final EntityManager entityManager;

    public PirateRepository(EntityManager entityManager) {
        if (entityManager == null) {
            throw new IllegalArgumentException("EntityManager cannot be null");
        }
        this.entityManager = entityManager;
    }

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Zapisuje nowego pirata lub aktualizuje istniejącego.
     */
    public Pirate save(Pirate pirate) {
        if (pirate == null) {
            throw new IllegalArgumentException("Pirate cannot be null");
        }

        if (pirate.getId() == null) {
            entityManager.persist(pirate);
            return pirate;
        } else {
            return entityManager.merge(pirate);
        }
    }

    /**
     * Znajduje pirata po ID.
     */
    public Optional<Pirate> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return Optional.ofNullable(entityManager.find(Pirate.class, id));
    }

    /**
     * Znajduje pirata po nazwie.
     */
    public Optional<Pirate> findByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        TypedQuery<Pirate> query = entityManager.createQuery(
                "SELECT p FROM Pirate p WHERE p.name = :name", Pirate.class);
        query.setParameter("name", name);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Zwraca wszystkich piratów.
     */
    public List<Pirate> findAll() {
        return entityManager.createQuery(
                "SELECT p FROM Pirate p ORDER BY p.name", Pirate.class)
                .getResultList();
    }

    /**
     * Znajduje piratów po randze.
     */
    public List<Pirate> findByRank(String rank) {
        if (rank == null) {
            throw new IllegalArgumentException("Rank cannot be null");
        }

        return entityManager.createQuery(
                "SELECT p FROM Pirate p WHERE p.rank = :rank ORDER BY p.bounty DESC",
                Pirate.class)
                .setParameter("rank", rank)
                .getResultList();
    }

    /**
     * Znajduje piratów z nagrodą większą niż podana wartość.
     */
    public List<Pirate> findByBountyGreaterThan(BigDecimal minBounty) {
        if (minBounty == null) {
            throw new IllegalArgumentException("MinBounty cannot be null");
        }

        return entityManager.createQuery(
                "SELECT p FROM Pirate p WHERE p.bounty > :minBounty ORDER BY p.bounty DESC",
                Pirate.class)
                .setParameter("minBounty", minBounty)
                .getResultList();
    }

    /**
     * Usuwa pirata.
     */
    public void delete(Pirate pirate) {
        if (pirate == null) {
            throw new IllegalArgumentException("Pirate cannot be null");
        }

        // Upewnij się że encja jest zarządzana
        Pirate managed = entityManager.contains(pirate)
                ? pirate
                : entityManager.merge(pirate);

        entityManager.remove(managed);
    }

    /**
     * Usuwa pirata po ID.
     */
    public boolean deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<Pirate> pirate = findById(id);
        if (pirate.isPresent()) {
            entityManager.remove(pirate.get());
            return true;
        }
        return false;
    }

    /**
     * Liczy wszystkich piratów.
     */
    public long count() {
        return entityManager.createQuery(
                "SELECT COUNT(p) FROM Pirate p", Long.class)
                .getSingleResult();
    }

    /**
     * Sprawdza czy pirat o danym ID istnieje.
     */
    public boolean existsById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return findById(id).isPresent();
    }
}
