package pl.przemekzagorski.training.jpa.entity;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Kapitan statku - demonstracja relacji @OneToOne.
 * 
 * UWAGA: Implementacja equals/hashCode oparta na ID.
 * @see <a href="https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/">
 *      Best practices for equals/hashCode</a>
 */
@Entity
@Table(name = "captains")
public class Captain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String nickname;

    /**
     * Relacja OneToOne - kapitan ma jeden statek.
     *
     * mappedBy = "captain" oznacza:
     * - Strona Ship jest WŁAŚCICIELEM relacji
     * - Kolumna FK jest w tabeli ships
     * - Ta strona jest tylko do odczytu relacji
     */
    @OneToOne(mappedBy = "captain")
    private Ship ship;

    public Captain() {
    }

    public Captain(String name) {
        this.name = name;
    }

    public Captain(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public Ship getShip() { return ship; }
    public void setShip(Ship ship) { this.ship = ship; }

    /**
     * equals() oparte na ID - bezpieczne dla JPA.
     * 
     * WAŻNE: Używamy getClass() zamiast instanceof, bo Hibernate
     * może tworzyć proxy klasy (podklasy).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Captain captain = (Captain) o;
        // Porównujemy tylko gdy oba mają ID (są zapisane w bazie)
        return id != null && Objects.equals(id, captain.id);
    }

    /**
     * hashCode() musi być stałe dla encji JPA!
     * 
     * DLACZEGO 31? Bo encja może trafić do HashSet PRZED persist()
     * (gdy id = null), a potem po persist() (gdy id != null).
     * Gdyby hashCode zależał od id, encja "zaginęłaby" w Set.
     */
    @Override
    public int hashCode() {
        // Stała wartość - bezpieczna dla Set/Map przed i po persist()
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Captain{id=%d, name='%s', nickname='%s'}", id, name, nickname);
    }
}

