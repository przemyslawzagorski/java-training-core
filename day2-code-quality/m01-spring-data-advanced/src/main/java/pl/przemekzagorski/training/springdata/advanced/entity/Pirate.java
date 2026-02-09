package pl.przemekzagorski.training.springdata.advanced.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Encja Pirate - z Cache L2 i optymalizacjami.
 *
 * NOWE w tym module (vs m09-spring-data):
 * - @Cacheable - włącza Cache L2 (Hibernate Second Level Cache)
 * - @org.hibernate.annotations.Cache - strategia cache
 *
 * Zachowane z m09:
 * - @Version (optimistic locking)
 * - Bean Validation (@NotBlank, @Positive, etc.)
 * - Poprawne equals/hashCode
 * - Relacja @ManyToOne
 */
@Entity
@Table(name = "pirates")
@Cacheable  // ✅ NOWE: Włącz Cache L2
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Pirate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pirate_seq")
    @SequenceGenerator(name = "pirate_seq", sequenceName = "pirate_id_seq", allocationSize = 50)
    private Long id;

    @Version
    private Long version;

    @NotBlank(message = "Imię pirata jest wymagane")
    @Size(min = 2, max = 100, message = "Imię musi mieć 2-100 znaków")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 100, message = "Przydomek może mieć max 100 znaków")
    @Column(length = 100)
    private String nickname;

    @NotBlank(message = "Ranga jest wymagana")
    @Column(nullable = false, length = 50)
    private String rank;

    @NotNull(message = "Bounty jest wymagane")
    @PositiveOrZero(message = "Bounty nie może być ujemne")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal bounty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_id")
    private Ship ship;

    // Konstruktory
    public Pirate() {}

    public Pirate(String name, String rank, BigDecimal bounty) {
        this.name = name;
        this.rank = rank;
        this.bounty = bounty;
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public BigDecimal getBounty() { return bounty; }
    public void setBounty(BigDecimal bounty) { this.bounty = bounty; }

    public Ship getShip() { return ship; }
    public void setShip(Ship ship) { this.ship = ship; }

    // equals/hashCode oparte na ID - bezpieczne dla JPA
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pirate pirate = (Pirate) o;
        return id != null && Objects.equals(id, pirate.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Pirate{id=%d, name='%s', rank='%s', bounty=%s, version=%d}",
                id, name, rank, bounty, version);
    }
}

