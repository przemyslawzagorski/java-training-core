package pl.przemekzagorski.training.jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Encja pirata - rozbudowana wersja z Named Queries.
 *
 * Named Queries są prekompilowane przy starcie aplikacji,
 * co daje lepszą wydajność i wczesne wykrywanie błędów.
 */
@Entity
@Table(name = "pirates")
@NamedQueries({
    @NamedQuery(
        name = "Pirate.findAll",
        query = "SELECT p FROM Pirate p"
    ),
    @NamedQuery(
        name = "Pirate.findByRank",
        query = "SELECT p FROM Pirate p WHERE p.rank = :rank"
    ),
    @NamedQuery(
        name = "Pirate.findByBountyGreaterThan",
        query = "SELECT p FROM Pirate p WHERE p.bounty > :minBounty ORDER BY p.bounty DESC"
    ),
    @NamedQuery(
        name = "Pirate.findWithShip",
        query = "SELECT p FROM Pirate p JOIN FETCH p.ship WHERE p.id = :id"
    ),
    @NamedQuery(
        name = "Pirate.countByRank",
        query = "SELECT p.rank, COUNT(p) FROM Pirate p GROUP BY p.rank"
    )
})
public class Pirate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String nickname;

    @Column(length = 50)
    private String rank;

    @Column(precision = 12, scale = 2)
    private BigDecimal bounty;

    @Column(name = "joined_at")
    private LocalDate joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_id")
    private Ship ship;

    @Version
    private Long version;

    public Pirate() {
    }

    public Pirate(String name, String rank, BigDecimal bounty) {
        this.name = name;
        this.rank = rank;
        this.bounty = bounty;
        this.joinedAt = LocalDate.now();
    }

    public Pirate(String name, String nickname, String rank, BigDecimal bounty) {
        this(name, rank, bounty);
        this.nickname = nickname;
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public BigDecimal getBounty() { return bounty; }
    public void setBounty(BigDecimal bounty) { this.bounty = bounty; }

    public LocalDate getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDate joinedAt) { this.joinedAt = joinedAt; }

    public Ship getShip() { return ship; }
    public void setShip(Ship ship) { this.ship = ship; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    @Override
    public String toString() {
        return String.format("Pirate{id=%d, name='%s', rank='%s', bounty=%s}",
            id, name, rank, bounty);
    }

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
}
