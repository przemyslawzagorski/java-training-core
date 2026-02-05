package pl.przemekzagorski.training.jpa.entity;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Członek załogi - demonstracja @ManyToOne.
 * 
 * UWAGA: Implementacja equals/hashCode oparta na ID.
 */
@Entity
@Table(name = "crew_members")
public class CrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String role;  // np. "First Mate", "Quartermaster", "Gunner", "Cook"

    /**
     * Relacja ManyToOne - wielu członków załogi należy do jednego statku.
     *
     * Ta strona jest WŁAŚCICIELEM relacji.
     * @JoinColumn określa kolumnę FK w tabeli crew_members.
     */
    @ManyToOne
    @JoinColumn(name = "ship_id")
    private Ship ship;

    public CrewMember() {
    }

    public CrewMember(String name, String role) {
        this.name = name;
        this.role = role;
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Ship getShip() { return ship; }
    public void setShip(Ship ship) { this.ship = ship; }

    /**
     * equals() oparte na ID - bezpieczne dla JPA.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrewMember that = (CrewMember) o;
        return id != null && Objects.equals(id, that.id);
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
        return String.format("CrewMember{id=%d, name='%s', role='%s'}", id, name, role);
    }
}

