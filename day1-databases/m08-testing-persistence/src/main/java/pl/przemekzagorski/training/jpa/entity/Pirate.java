package pl.przemekzagorski.training.jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Encja Pirate - używana w testach warstwy persystencji.
 */
@Entity
@Table(name = "pirates")
public class Pirate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String rank;

    @Column(precision = 15, scale = 2)
    private BigDecimal bounty;

    @Version
    private Long version;

    // Konstruktory
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public BigDecimal getBounty() { return bounty; }
    public void setBounty(BigDecimal bounty) { this.bounty = bounty; }

    public Long getVersion() { return version; }

    // equals/hashCode bazujące na id biznesowym
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pirate pirate)) return false;
        return id != null && id.equals(pirate.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Pirate{id=%d, name='%s', rank='%s', bounty=%s}",
                id, name, rank, bounty);
    }
}
