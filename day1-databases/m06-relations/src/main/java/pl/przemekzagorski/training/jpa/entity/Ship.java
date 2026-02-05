package pl.przemekzagorski.training.jpa.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Statek piracki - demonstracja wielu typów relacji.
 * 
 * UWAGA: Ship jest używany w Set<Ship> w Island,
 * więc MUSI mieć poprawne equals/hashCode!
 */
@Entity
@Table(name = "ships")
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String type;

    @Column
    private Integer cannons;

    /**
     * Relacja OneToOne - statek ma jednego kapitana.
     *
     * Ta strona jest WŁAŚCICIELEM relacji (brak mappedBy).
     * @JoinColumn określa nazwę kolumny FK w tabeli ships.
     */
    @OneToOne
    @JoinColumn(name = "captain_id")
    private Captain captain;

    /**
     * Relacja OneToMany - statek ma wielu członków załogi.
     *
     * mappedBy = "ship" oznacza, że CrewMember jest właścicielem.
     * cascade = ALL oznacza, że operacje kaskadują na załogę.
     * orphanRemoval = true oznacza, że usunięcie z listy = usunięcie z bazy.
     */
    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrewMember> crew = new ArrayList<>();

    /**
     * Relacja ManyToMany - statek odwiedza wiele wysp.
     *
     * @JoinTable definiuje tabelę łączącą.
     */
    @ManyToMany
    @JoinTable(
        name = "ship_visits",
        joinColumns = @JoinColumn(name = "ship_id"),
        inverseJoinColumns = @JoinColumn(name = "island_id")
    )
    private Set<Island> visitedIslands = new HashSet<>();

    public Ship() {
    }

    public Ship(String name, String type, Integer cannons) {
        this.name = name;
        this.type = type;
        this.cannons = cannons;
    }

    // ==========================================
    // Metody pomocnicze do zarządzania relacjami
    // ==========================================

    /**
     * Dodaje członka załogi - WAŻNE: ustawia obie strony relacji!
     */
    public void addCrewMember(CrewMember member) {
        crew.add(member);
        member.setShip(this);
    }

    /**
     * Usuwa członka załogi
     */
    public void removeCrewMember(CrewMember member) {
        crew.remove(member);
        member.setShip(null);
    }

    /**
     * Dodaje odwiedzoną wyspę
     */
    public void visitIsland(Island island) {
        visitedIslands.add(island);
        island.getShips().add(this);
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCannons() { return cannons; }
    public void setCannons(Integer cannons) { this.cannons = cannons; }

    public Captain getCaptain() { return captain; }
    public void setCaptain(Captain captain) {
        this.captain = captain;
        if (captain != null) {
            captain.setShip(this);
        }
    }

    public List<CrewMember> getCrew() { return crew; }
    public void setCrew(List<CrewMember> crew) { this.crew = crew; }

    public Set<Island> getVisitedIslands() { return visitedIslands; }
    public void setVisitedIslands(Set<Island> visitedIslands) { this.visitedIslands = visitedIslands; }

    /**
     * equals() oparte na ID - bezpieczne dla JPA.
     * 
     * KRYTYCZNE dla @ManyToMany: Ship jest w Set<Ship> w Island,
     * więc equals/hashCode MUSZĄ działać poprawnie!
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return id != null && Objects.equals(id, ship.id);
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
        return String.format("Ship{id=%d, name='%s', type='%s', cannons=%d}",
            id, name, type, cannons);
    }
}

