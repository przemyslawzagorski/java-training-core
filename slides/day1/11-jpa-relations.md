# Relacje w JPA

---

## 4 typy relacji

**@OneToOne** - jeden do jednego
```java
@Entity
public class Ship {
    @OneToOne
    @JoinColumn(name = "captain_id")
    private Pirate captain;
}
```

**@OneToMany** - jeden do wielu
```java
@Entity
public class Ship {
    @OneToMany(mappedBy = "ship")
    private List<Pirate> crew = new ArrayList<>();
}
```

**@ManyToOne** - wiele do jednego
```java
@Entity
public class Pirate {
    @ManyToOne
    @JoinColumn(name = "ship_id")
    private Ship ship;
}
```

**@ManyToMany** - wiele do wielu
```java
@Entity
public class Ship {
    @ManyToMany
    @JoinTable(name = "ship_island_visits",
        joinColumns = @JoinColumn(name = "ship_id"),
        inverseJoinColumns = @JoinColumn(name = "island_id"))
    private List<Island> visitedIslands = new ArrayList<>();
}
```

---

## Strona właścicielska vs odwrotna

**Strona właścicielska (owning side)**
- Ma `@JoinColumn` lub `@JoinTable`
- Zarządza relacją w bazie (FK)

**Strona odwrotna (inverse side)**
- Ma `mappedBy`
- Nie zarządza relacją (tylko odczyt)

**Przykład @OneToMany / @ManyToOne:**
```java
// Strona właścicielska (MANY)
@Entity
public class Pirate {
    @ManyToOne
    @JoinColumn(name = "ship_id")  // FK w tabeli pirates
    private Ship ship;
}

// Strona odwrotna (ONE)
@Entity
public class Ship {
    @OneToMany(mappedBy = "ship")  // mappedBy wskazuje pole w Pirate
    private List<Pirate> crew = new ArrayList<>();
}
```

---

## FetchType - LAZY vs EAGER

**LAZY (leniwe ładowanie)** - domyślne dla @OneToMany, @ManyToMany
```java
@OneToMany(mappedBy = "ship", fetch = FetchType.LAZY)
private List<Pirate> crew;

Ship ship = em.find(Ship.class, 1L);  // SELECT ships
ship.getCrew().size();  // SELECT pirates (dopiero teraz!)
```

**EAGER (natychmiastowe ładowanie)** - domyślne dla @OneToOne, @ManyToOne
```java
@ManyToOne(fetch = FetchType.EAGER)
private Ship ship;

Pirate pirate = em.find(Pirate.class, 1L);  // SELECT pirates + JOIN ships
```

**Problem N+1 SELECT:**
```java
List<Pirate> pirates = em.createQuery("SELECT p FROM Pirate p").getResultList();
// SELECT pirates (1 zapytanie)
for (Pirate p : pirates) {
    p.getShip().getName();  // SELECT ship (N zapytań!)
}
```

**Rozwiązanie - JOIN FETCH:**
```java
List<Pirate> pirates = em.createQuery(
    "SELECT p FROM Pirate p JOIN FETCH p.ship").getResultList();
// SELECT pirates JOIN ships (1 zapytanie!)
```

---

## Cascade i orphanRemoval

**Cascade** - propagacja operacji
```java
@OneToMany(mappedBy = "ship", cascade = CascadeType.ALL)
private List<Pirate> crew;

em.remove(ship);  // Usuwa ship + wszystkich pirates!
```

**orphanRemoval** - usuwanie osieroconych encji
```java
@OneToMany(mappedBy = "ship", orphanRemoval = true)
private List<Pirate> crew;

ship.getCrew().remove(pirate);  // Usuwa pirate z bazy!
```

---

## Wskazówka dla trenera
**Czas:** 20 minut

**Co mówię:**
- "4 typy relacji: @OneToOne, @OneToMany, @ManyToOne, @ManyToMany."
- "Strona właścicielska ma @JoinColumn, strona odwrotna ma mappedBy."
- "LAZY = ładowanie na żądanie. EAGER = ładowanie od razu."
- "Problem N+1 SELECT - rozwiązanie: JOIN FETCH."
- "Cascade propaguje operacje. orphanRemoval usuwa osierocone encje."

**Co pokazuję:**
- Klasy z relacjami (Ship, Pirate, Island)
- `RelationsDemo.java` - wszystkie typy relacji
- `LazyLoadingDemo.java` - LAZY vs EAGER
- `NPlusOneDemo.java` - problem N+1 i rozwiązanie JOIN FETCH
- Uruchamiam demo, pokazuję wygenerowane SQL

**Następny krok:** Slajd `12-hql-jpql.md`

