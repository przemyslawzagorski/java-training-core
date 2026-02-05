package pl.przemekzagorski.training.jpa.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Encja statku - z relacją do piratów.
 */
@Entity
@Table(name = "ships")
@NamedQueries({
    @NamedQuery(
        name = "Ship.findAll",
        query = "SELECT s FROM Ship s"
    ),
    @NamedQuery(
        name = "Ship.findWithCrew",
        query = "SELECT DISTINCT s FROM Ship s LEFT JOIN FETCH s.crew WHERE s.id = :id"
    ),
    @NamedQuery(
        name = "Ship.findByType",
        query = "SELECT s FROM Ship s WHERE s.type = :type"
    )
})
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String type;

    @Column
    private Integer cannons;

    @Column(name = "crew_capacity")
    private Integer crewCapacity;

    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pirate> crew = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_island_id")
    private Island homePort;

    public Ship() {
    }

    public Ship(String name, String type, Integer cannons) {
        this.name = name;
        this.type = type;
        this.cannons = cannons;
    }

    // Metody pomocnicze
    public void addCrewMember(Pirate pirate) {
        crew.add(pirate);
        pirate.setShip(this);
    }

    public void removeCrewMember(Pirate pirate) {
        crew.remove(pirate);
        pirate.setShip(null);
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

    public Integer getCrewCapacity() { return crewCapacity; }
    public void setCrewCapacity(Integer crewCapacity) { this.crewCapacity = crewCapacity; }

    public List<Pirate> getCrew() { return crew; }
    public void setCrew(List<Pirate> crew) { this.crew = crew; }

    public Island getHomePort() { return homePort; }
    public void setHomePort(Island homePort) { this.homePort = homePort; }

    @Override
    public String toString() {
        return String.format("Ship{id=%d, name='%s', type='%s', cannons=%d}",
            id, name, type, cannons);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ship ship)) return false;
        return id != null && id.equals(ship.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
