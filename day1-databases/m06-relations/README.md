# Moduł 06: Relacje JPA - Mapowanie powiązań między encjami

## 🎯 Cel modułu

Relacje między encjami to jeden z najtrudniejszych tematów JPA.
Juniorzy często popełniają błędy prowadzące do:
- Niezapisanych relacji (bo zmienili "złą stronę")
- N+1 SELECT problem
- `LazyInitializationException`
- Niespójności danych w bazie

Po tym module będziesz:
- Rozumieć różnicę między stroną właściciela a stroną odwrotną
- Wiedzieć kiedy używać jakiego typu relacji
- Poprawnie zarządzać obiema stronami relacji
- Rozumieć cascade i orphanRemoval

---

## 📊 Typy relacji

### @OneToOne (jeden do jednego)

```
┌──────────┐         ┌──────────┐
│ Captain  │─────────│   Ship   │
│   (1)    │         │   (1)    │
└──────────┘         └──────────┘
     FK ←─────────────────┘

Każdy kapitan ma JEDEN statek, każdy statek ma JEDNEGO kapitana.
```

**Przykład kodu:**
```java
@Entity
public class Ship {
    @OneToOne
    @JoinColumn(name = "captain_id")  // Tu jest FK!
    private Captain captain;          // Ship jest WŁAŚCICIELEM
}

@Entity
public class Captain {
    @OneToOne(mappedBy = "captain")   // mappedBy = strona odwrotna
    private Ship ship;                // Captain NIE jest właścicielem
}
```

---

### @OneToMany / @ManyToOne (jeden do wielu)

```
┌──────────┐         ┌─────────────┐
│   Ship   │─────────│ CrewMember  │
│   (1)    │         │    (N)      │
└──────────┘         └─────────────┘
                           │
                      FK ──┘

Jeden statek ma WIELU członków załogi.
Każdy członek załogi należy do JEDNEGO statku.
```

**⚠️ WAŻNE: Strona @ManyToOne jest ZAWSZE właścicielem!**

```java
@Entity
public class CrewMember {
    @ManyToOne
    @JoinColumn(name = "ship_id")  // CrewMember jest WŁAŚCICIELEM
    private Ship ship;              // Tu jest FK w tabeli!
}

@Entity
public class Ship {
    @OneToMany(mappedBy = "ship")   // Ship NIE jest właścicielem
    private List<CrewMember> crew;  // mappedBy = tylko odczyt
}
```

---

### @ManyToMany (wiele do wielu)

```
┌──────────┐         ┌──────────┐
│   Ship   │─────────│  Island  │
│   (N)    │         │   (M)    │
└──────────┘         └──────────┘
      │                    │
      └────┬────────┬──────┘
           │ JOIN   │
           │ TABLE  │
           └────────┘
         ship_visits

Wiele statków odwiedza wiele wysp.
```

**Wymaga tabeli łączącej (join table):**

```java
@Entity
public class Ship {
    @ManyToMany
    @JoinTable(
        name = "ship_visits",                              // Nazwa tabeli łączącej
        joinColumns = @JoinColumn(name = "ship_id"),       // FK do Ship
        inverseJoinColumns = @JoinColumn(name = "island_id") // FK do Island
    )
    private Set<Island> visitedIslands;  // Ship jest WŁAŚCICIELEM
}

@Entity
public class Island {
    @ManyToMany(mappedBy = "visitedIslands")  // Island NIE jest właścicielem
    private Set<Ship> ships;
}
```

---

## 🔑 Strona właściciela vs Strona odwrotna

To jest **KLUCZOWE** i źródło 90% błędów juniorów!

### Strona właściciela (Owning Side)

| Cecha | Opis |
|-------|------|
| Bez `mappedBy` | To ta strona, która NIE ma mappedBy |
| Ma FK w tabeli | Kolumna klucza obcego jest w jej tabeli |
| **Zapisuje relację** | Tylko zmiany TU są zapisywane do bazy! |

### Strona odwrotna (Inverse Side)

| Cecha | Opis |
|-------|------|
| Z `mappedBy` | Ma atrybut mappedBy wskazujący na pole właściciela |
| Brak FK | Nie ma kolumny FK w swojej tabeli |
| **Tylko odczyt** | Zmiany TU są IGNOROWANE! |

### Przykład - BŁĄD

```java
// ❌ ŹLE - zmiana na stronie odwrotnej
Ship ship = em.find(Ship.class, 1L);
CrewMember member = new CrewMember("Jack", "Sailor");
em.persist(member);

ship.getCrew().add(member);  // Ship ma mappedBy - to NIE ZADZIAŁA!

em.getTransaction().commit();
// member.ship jest NULL w bazie!
```

### Przykład - POPRAWNIE

```java
// ✅ DOBRZE - zmiana na stronie właściciela
Ship ship = em.find(Ship.class, 1L);
CrewMember member = new CrewMember("Jack", "Sailor");

member.setShip(ship);  // CrewMember jest właścicielem - TO ZADZIAŁA!
em.persist(member);

// Opcjonalnie: synchronizuj drugą stronę dla spójności w pamięci
ship.getCrew().add(member);

em.getTransaction().commit();
// member.ship_id = 1 w bazie!
```

---

## 🔄 Metody pomocnicze - synchronizacja obu stron

**Zawsze synchronizuj OBie strony relacji!**

```java
@Entity
public class Ship {

    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrewMember> crew = new ArrayList<>();

    /**
     * Metoda pomocnicza - synchronizuje OBie strony relacji.
     * To jest BEST PRACTICE!
     */
    public void addCrewMember(CrewMember member) {
        crew.add(member);         // Strona odwrotna (dla spójności w pamięci)
        member.setShip(this);     // Strona właściciela (zapisuje do bazy!)
    }

    public void removeCrewMember(CrewMember member) {
        crew.remove(member);
        member.setShip(null);
    }
}
```

**Użycie:**
```java
Ship ship = new Ship("Black Pearl", "Galleon", 32);
CrewMember jack = new CrewMember("Jack", "Captain");

ship.addCrewMember(jack);  // Synchronizuje obie strony!

em.persist(ship);  // cascade = ALL → jack też zostanie zapisany
```

---

## ⚡ Cascade - propagacja operacji

`CascadeType` określa, które operacje propagują się na powiązane encje.

| Typ | Opis | Przykład |
|-----|------|----------|
| `PERSIST` | persist() kaskaduje | Zapisujesz Ship → zapisuje się też Captain |
| `MERGE` | merge() kaskaduje | Merge Ship → merge też Captain |
| `REMOVE` | remove() kaskaduje | Usuwasz Ship → usuwa się też Captain |
| `REFRESH` | refresh() kaskaduje | Odświeżasz Ship → odświeża się Captain |
| `DETACH` | detach() kaskaduje | Odłączasz Ship → odłącza się Captain |
| `ALL` | Wszystkie powyższe | Najczęściej używane |

**Przykład:**
```java
@OneToMany(mappedBy = "ship", cascade = CascadeType.ALL)
private List<CrewMember> crew;

// Teraz:
Ship ship = new Ship("Black Pearl", "Galleon", 32);
ship.addCrewMember(new CrewMember("Jack", "Captain"));
ship.addCrewMember(new CrewMember("Will", "First Mate"));

em.persist(ship);  // Automatycznie zapisuje też wszystkich crew!
```

---

## 🗑️ orphanRemoval - usuwanie sierot

`orphanRemoval = true` oznacza: usunięcie z kolekcji = usunięcie z bazy.

```java
@OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, orphanRemoval = true)
private List<CrewMember> crew;
```

**Przykład:**
```java
Ship ship = em.find(Ship.class, 1L);

// Pobieramy członka załogi
CrewMember jack = ship.getCrew().get(0);

// Usuwamy z kolekcji
ship.getCrew().remove(jack);

em.getTransaction().commit();
// orphanRemoval = true → Jack zostaje USUNIĘTY z bazy!
// Bez orphanRemoval → Jack pozostaje w bazie z ship_id = NULL
```

---

## 🔍 FetchType - LAZY vs EAGER

| Typ | Opis | Domyślne dla |
|-----|------|--------------|
| `LAZY` | Ładuj dopiero przy dostępie | @OneToMany, @ManyToMany |
| `EAGER` | Ładuj od razu z rodzicem | @OneToOne, @ManyToOne |

**⚠️ LAZY jest prawie zawsze lepsze!**

```java
// Zmiana na LAZY
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ship_id")
private Ship ship;

// Zmiana na EAGER (rzadko potrzebne)
@OneToMany(mappedBy = "ship", fetch = FetchType.EAGER)
private List<CrewMember> crew;
```

### Problem N+1 SELECT

```java
// ❌ N+1 problem
List<Ship> ships = em.createQuery("SELECT s FROM Ship s", Ship.class).getResultList();
// 1 SELECT dla ships

for (Ship ship : ships) {
    System.out.println(ship.getCrew().size());  // N SELECTów dla crew!
}
```

**Rozwiązanie: JOIN FETCH**
```java
// ✅ JOIN FETCH - 1 SELECT
String jpql = "SELECT s FROM Ship s JOIN FETCH s.crew";
List<Ship> ships = em.createQuery(jpql, Ship.class).getResultList();
// Tylko 1 SELECT - crew ładowane razem z ships
```

---

## ⚠️ Typowe błędy i jak ich unikać

### 1. Zmiana tylko na stronie odwrotnej

```java
// ❌ ŹLE
Ship ship = em.find(Ship.class, 1L);
CrewMember member = new CrewMember("Jack", "Sailor");
ship.getCrew().add(member);  // Ship ma mappedBy!
em.persist(member);
// member.ship_id = NULL w bazie!

// ✅ DOBRZE
member.setShip(ship);  // Ustaw na stronie właściciela
ship.getCrew().add(member);  // Opcjonalnie dla spójności
em.persist(member);
```

### 2. Brak synchronizacji obu stron

```java
// ❌ ŹLE - niespójny stan w pamięci
member.setShip(ship);
// ship.getCrew() nie zawiera member!

// ✅ DOBRZE - używaj metod pomocniczych
ship.addCrewMember(member);  // Synchronizuje obie strony
```

### 3. LazyInitializationException

```java
// ❌ ŹLE
EntityManager em = emf.createEntityManager();
Ship ship = em.find(Ship.class, 1L);
em.close();
ship.getCrew().size();  // LazyInitializationException!

// ✅ DOBRZE - pobierz przed zamknięciem lub użyj JOIN FETCH
String jpql = "SELECT s FROM Ship s JOIN FETCH s.crew WHERE s.id = :id";
Ship ship = em.createQuery(jpql, Ship.class)
    .setParameter("id", 1L)
    .getSingleResult();
em.close();
ship.getCrew().size();  // OK - crew było eager loaded
```

### 4. Cascade bez sensu

```java
// ❌ ŹLE - cascade REMOVE na ManyToOne
@ManyToOne(cascade = CascadeType.REMOVE)
private Ship ship;
// Usunięcie CrewMember usunie cały Ship!!!

// ✅ DOBRZE - cascade na OneToMany
@OneToMany(mappedBy = "ship", cascade = CascadeType.ALL)
private List<CrewMember> crew;
// Usunięcie Ship usuwa też crew (sensowne)
```

---

## ⚖️ equals() i hashCode() - Krytyczne dla relacji!

### Problem: Encje w kolekcjach Set/Map

Gdy używasz `Set<Island>` lub `Map<Ship, ...>`, Java używa `equals()` i `hashCode()` do porównywania obiektów. Domyślna implementacja (z Object) porównuje **referencje**, co prowadzi do problemów:

```java
Ship ship1 = em.find(Ship.class, 1L);
em.close();

Ship ship2 = em2.find(Ship.class, 1L);  // Ten sam rekord, ALE inna referencja!

System.out.println(ship1.equals(ship2));  // false!!! (domyślnie)

Set<Ship> ships = new HashSet<>();
ships.add(ship1);
ships.contains(ship2);  // false!!! - nie znajdzie tego samego statku!
```

### Problem: Encja przed i po persist()

```java
Set<Island> islands = new HashSet<>();
Island tortuga = new Island("Tortuga");
islands.add(tortuga);  // hashCode obliczony gdy id = null

em.persist(tortuga);   // teraz id = 1

islands.contains(tortuga);  // false!!! - hashCode się zmienił!
```

### Rozwiązanie: Bezpieczne equals/hashCode dla JPA

```java
@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ... pola

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        // Porównuj tylko gdy OBA mają ID
        return id != null && Objects.equals(id, ship.id);
    }

    @Override
    public int hashCode() {
        // STAŁA wartość - bezpieczna przed i po persist()!
        return getClass().hashCode();
    }
}
```

### Dlaczego to działa?

| Aspekt | Wyjaśnienie |
|--------|-------------|
| `getClass()` zamiast `instanceof` | Hibernate tworzy proxy (podklasy) - `instanceof` może dać fałszywe pozytywne |
| `id != null` w equals | Dwa nowe (niezapisane) obiekty nie są równe, bo nie mają jeszcze tożsamości |
| Stałe `hashCode()` | hashCode NIE MOŻE zależeć od id, bo zmieni się po persist() |
| `return getClass().hashCode()` | Wszystkie Ship mają ten sam hashCode - to OK dla małych kolekcji |

### ⚠️ Kompromis z hashCode()

Stałe `hashCode()` sprawia, że wszystkie encje tej samej klasy trafiają do jednego "bucket" w HashMap/HashSet. To degraduje wydajność z O(1) do O(n), ale:
- Dla typowych kolekcji (10-100 elementów) - nieistotne
- Alternatywa (business key) często niewykonalna
- **Poprawność > Wydajność**

### Alternatywa: Natural/Business Key

Jeśli encja ma **unikalny, niemutowalny atrybut biznesowy** (np. email, PESEL, ISBN):

```java
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;  // Natural key

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);  // Porównanie po email
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);  // Hash z email
    }
}
```

**Kiedy używać Natural Key:**
- ✅ Email, PESEL, NIP, ISBN - unikalne i niezmienne
- ❌ Imię, nazwa - mogą się powtarzać
- ❌ Data urodzenia - nie jest unikalna

---

## 📁 Zawartość modułu

| Plik | Opis |
|------|------|
| `entity/Captain.java` | Kapitan (@OneToOne inverse) z equals/hashCode |
| `entity/Ship.java` | Statek (właściciel relacji) z equals/hashCode |
| `entity/CrewMember.java` | Członek załogi (@ManyToOne owner) z equals/hashCode |
| `entity/Island.java` | Wyspa (@ManyToMany inverse) z equals/hashCode |
| `RelationsDemo.java` | Demonstracja wszystkich relacji |
| `RelationsExercises.java` | 🎯 Ćwiczenia do wykonania |
| `RelationsExercisesSolutions.java` | Rozwiązania ćwiczeń |

---

## 🎓 Kluczowe wnioski

1. **Strona bez `mappedBy` jest właścicielem** - tylko tu zapisujesz relację
2. **@ManyToOne jest ZAWSZE właścicielem** - bo ma FK w tabeli
3. **Synchronizuj OBie strony** - dla spójności w pamięci
4. **Używaj metod pomocniczych** - `addX()`, `removeX()`
5. **LAZY > EAGER** - ładuj tylko to co potrzebne
6. **JOIN FETCH rozwiązuje N+1** - jeden SELECT zamiast wielu
7. **orphanRemoval = usunięcie z kolekcji = DELETE** - uważaj!

---

## 📚 Materiały dodatkowe

- [Hibernate User Guide - Associations](https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#associations)
- [Vlad Mihalcea - Relationships Best Practices](https://vladmihalcea.com/jpa-hibernate-associations/)

