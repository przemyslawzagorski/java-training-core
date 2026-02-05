package pl.przemekzagorski.training.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Encja JPA reprezentująca statek piracki.
 *
 * Używa Bean Validation (JSR 380) do walidacji danych:
 * - @NotBlank - pole nie może być puste ani zawierać tylko białych znaków
 * - @Size - ograniczenie długości tekstu
 * - @Min, @Max - ograniczenie wartości liczbowych
 * - @Positive - wartość musi być dodatnia
 */
@Entity
@Table(name = "ships")
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa statku nie może być pusta")
    @Size(min = 2, max = 100, message = "Nazwa musi mieć od 2 do 100 znaków")
    @Column(name = "ship_name", nullable = false, length = 100)
    private String name;

    @Size(max = 50, message = "Typ statku może mieć maksymalnie 50 znaków")
    @Column(name = "ship_type", length = 50)
    private String type;

    @Min(value = 0, message = "Liczba dział nie może być ujemna")
    @Max(value = 200, message = "Statek może mieć maksymalnie 200 dział")
    @Column(name = "cannons")
    private Integer cannons;

    @Positive(message = "Pojemność załogi musi być dodatnia")
    @Column(name = "crew_capacity")
    private Integer crewCapacity;

    public Ship() {}

    public Ship(String name, String type, Integer cannons) {
        this.name = name;
        this.type = type;
        this.cannons = cannons;
    }

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

    @Override
    public String toString() {
        return String.format("Ship{id=%d, name='%s', type='%s', cannons=%d}", id, name, type, cannons);
    }
}

