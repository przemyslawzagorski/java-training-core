package pl.przemekzagorski.training.jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Encja wyspy - dla demonstracji zapytań.
 */
@Entity
@Table(name = "islands")
public class Island {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String location;

    @Column(name = "has_treasure")
    private Boolean hasTreasure;

    @Column(name = "treasure_value", precision = 15, scale = 2)
    private BigDecimal treasureValue;

    public Island() {
    }

    /**
     * Konstruktor uproszczony - zakłada że wyspa ma skarb.
     */
    public Island(String name, String location, BigDecimal treasureValue) {
        this(name, location, true, treasureValue);
    }

    public Island(String name, String location, boolean hasTreasure, BigDecimal treasureValue) {
        this.name = name;
        this.location = location;
        this.hasTreasure = hasTreasure;
        this.treasureValue = treasureValue;
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Boolean getHasTreasure() { return hasTreasure; }
    public void setHasTreasure(Boolean hasTreasure) { this.hasTreasure = hasTreasure; }

    public BigDecimal getTreasureValue() { return treasureValue; }
    public void setTreasureValue(BigDecimal treasureValue) { this.treasureValue = treasureValue; }

    @Override
    public String toString() {
        return String.format("Island{id=%d, name='%s', hasTreasure=%s, treasureValue=%s}",
            id, name, hasTreasure, treasureValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Island island)) return false;
        return id != null && id.equals(island.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
