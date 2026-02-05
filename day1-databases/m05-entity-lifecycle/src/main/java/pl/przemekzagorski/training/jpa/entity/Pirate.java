package pl.przemekzagorski.training.jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Encja pirata do demonstracji cyklu życia i optimistic locking.
 *
 * OPTIMISTIC LOCKING (@Version):
 * - Zapobiega utracie zmian przy równoczesnej edycji
 * - Hibernate sprawdza wersję przy UPDATE
 * - Jeśli wersja się zmieniła, rzuca OptimisticLockException
 */
@Entity
@Table(name = "pirates")
public class Pirate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Pole wersji dla optimistic locking.
     * Hibernate automatycznie inkrementuje przy każdym UPDATE.
     * SQL wygląda tak: UPDATE pirates SET ... , version = version + 1
     *                  WHERE id = ? AND version = ?
     */
    @Version
    private Long version;

    @Column(nullable = false)
    private String name;

    @Column
    private String nickname;

    @Column
    private String rank;

    @Column(precision = 15, scale = 2)
    private BigDecimal bounty;

    public Pirate() {
    }

    public Pirate(String name, String rank, BigDecimal bounty) {
        this.name = name;
        this.rank = rank;
        this.bounty = bounty;
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVersion() { return version; }
    // Brak setVersion - zarządzane przez Hibernate

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public BigDecimal getBounty() { return bounty; }
    public void setBounty(BigDecimal bounty) { this.bounty = bounty; }

    @Override
    public String toString() {
        return String.format("Pirate{id=%d, name='%s', rank='%s', bounty=%s, version=%d}",
            id, name, rank, bounty, version);
    }
}

