package pl.przemekzagorski.training.springdata.advanced.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Encja Ship - z Cache L2 i @BatchSize.
 *
 * NOWE w tym module (vs m09-spring-data):
 * - @Cacheable - włącza Cache L2
 * - @BatchSize na kolekcji crew - optymalizacja ładowania (kompromis między N+1 a JOIN FETCH)
 *
 * Zachowane z m09:
 * - @Version (optimistic locking)
 * - Bean Validation
 * - Relacja @OneToMany z cascade i orphanRemoval
 */
@Entity
@Table(name = "ships")
@Cacheable  // ✅ NOWE: Włącz Cache L2
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ship_seq")
    @SequenceGenerator(name = "ship_seq", sequenceName = "ship_id_seq", allocationSize = 50)
    private Long id;

    @Version
    private Long version;

    @NotBlank(message = "Nazwa statku jest wymagana")
    @Size(min = 2, max = 100, message = "Nazwa musi mieć 2-100 znaków")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 50, message = "Typ może mieć max 50 znaków")
    @Column(length = 50)
    private String type;

    @Min(value = 0, message = "Liczba dział nie może być ujemna")
    @Max(value = 200, message = "Maksymalnie 200 dział")
    @Column
    private Integer cannons;

    /**
     * ✅ NOWE: @BatchSize(size = 10)
     * 
     * Zamiast N+1 zapytań (po jednym na każdy statek), Hibernate wykona:
     * - 1 zapytanie na statki
     * - ~N/10 zapytań na załogi (ładuje po 10 kolekcji naraz)
     * 
     * PRZYKŁAD:
     * - 20 statków bez @BatchSize = 1 + 20 = 21 zapytań
     * - 20 statków z @BatchSize(10) = 1 + 2 = 3 zapytania!
     */
    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)  // ✅ NOWE: Batch loading
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Pirate> crew = new ArrayList<>();

    // Konstruktory
    public Ship() {}

    public Ship(String name, String type, Integer cannons) {
        this.name = name;
        this.type = type;
        this.cannons = cannons;
    }

    // Metody pomocnicze do zarządzania relacją
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

    public Long getVersion() { return version; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCannons() { return cannons; }
    public void setCannons(Integer cannons) { this.cannons = cannons; }

    public List<Pirate> getCrew() { return crew; }
    public void setCrew(List<Pirate> crew) { this.crew = crew; }

    // equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return id != null && Objects.equals(id, ship.id);
    }

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

