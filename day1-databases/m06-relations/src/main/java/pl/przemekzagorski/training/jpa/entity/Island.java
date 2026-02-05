package pl.przemekzagorski.training.jpa.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Wyspa - demonstracja @ManyToMany.
 * 
 * UWAGA: Ta encja jest używana w HashSet (ManyToMany z Ship),
 * więc MUSI mieć poprawne equals/hashCode!
 */
@Entity
@Table(name = "islands")
public class Island {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String location;

    @Column(name = "has_treasure")
    private Boolean hasTreasure;

    /**
     * Relacja ManyToMany - wyspa jest odwiedzana przez wiele statków.
     *
     * mappedBy = "visitedIslands" oznacza, że Ship jest właścicielem.
     * Tabela łącząca jest zdefiniowana po stronie Ship.
     */
    @ManyToMany(mappedBy = "visitedIslands")
    private Set<Ship> ships = new HashSet<>();

    public Island() {
    }

    public Island(String name, String location, Boolean hasTreasure) {
        this.name = name;
        this.location = location;
        this.hasTreasure = hasTreasure;
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

    public Set<Ship> getShips() { return ships; }
    public void setShips(Set<Ship> ships) { this.ships = ships; }

    /**
     * equals() oparte na ID - bezpieczne dla JPA.
     * 
     * KRYTYCZNE dla @ManyToMany: Island jest w Set<Island> w Ship,
     * więc equals/hashCode MUSZĄ działać poprawnie!
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Island island = (Island) o;
        return id != null && Objects.equals(id, island.id);
    }

    /**
     * hashCode() stałe - bezpieczne dla Set/Map przed i po persist().
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Island{id=%d, name='%s', hasTreasure=%s}", id, name, hasTreasure);
    }
}

