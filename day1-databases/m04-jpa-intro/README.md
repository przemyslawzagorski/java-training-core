# Moduł 04: JPA Introduction

## 🎯 Cel modułu

Wprowadzenie do **JPA (Java Persistence API)** - standardu ORM (Object-Relational Mapping) w Javie. Kursant zrozumie różnicę między surowym JDBC a podejściem obiektowym, nauczy się mapować klasy Java na tabele bazy danych i wykonywać podstawowe operacje CRUD bez pisania SQL.

---

## 📚 Wprowadzenie teoretyczne

### Czym jest JPA?

**JPA (Java Persistence API)** to specyfikacja (interfejs), która definiuje jak mapować obiekty Java na tabele w relacyjnej bazie danych. Sama specyfikacja nie dostarcza implementacji - to robią **providerzy JPA**:

| Provider | Opis |
|----------|------|
| **Hibernate** | Najpopularniejszy, domyślny w Spring Boot |
| **EclipseLink** | Referencyjna implementacja JPA |
| **OpenJPA** | Implementacja Apache |

**W tym szkoleniu używamy Hibernate.**

### Czym jest ORM?

**ORM (Object-Relational Mapping)** to technika mapowania obiektów programistycznych na struktury relacyjnej bazy danych:

```
┌─────────────────────────────────────────────────────────────────┐
│                           JAVA                                  │
│  class Ship {                                                   │
│      Long id;          ←────────────────┐                       │
│      String name;      ←─────────────┐  │                       │
│      String type;      ←──────────┐  │  │                       │
│      Integer cannons;  ←───────┐  │  │  │                       │
│  }                             │  │  │  │                       │
└────────────────────────────────┼──┼──┼──┼───────────────────────┘
                                 │  │  │  │
                    ORM (Hibernate) mapuje
                                 │  │  │  │
┌────────────────────────────────┼──┼──┼──┼───────────────────────┐
│                           DATABASE                              │
│  TABLE ships (                 │  │  │  │                       │
│      id BIGINT PK,      ───────┘  │  │  │                       │
│      ship_name VARCHAR, ──────────┘  │  │                       │
│      ship_type VARCHAR, ─────────────┘  │                       │
│      cannons INT        ────────────────┘                       │
│  )                                                              │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 JDBC vs JPA - Porównanie

### Zapisywanie obiektu do bazy

#### ❌ JDBC - dużo kodu, ręczne mapowanie
```java
String sql = "INSERT INTO ships (name, type, cannons) VALUES (?, ?, ?)";
try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    stmt.setString(1, ship.getName());
    stmt.setString(2, ship.getType());
    stmt.setInt(3, ship.getCannons());
    stmt.executeUpdate();
    
    try (ResultSet keys = stmt.getGeneratedKeys()) {
        if (keys.next()) {
            ship.setId(keys.getLong(1));
        }
    }
}
```

#### ✅ JPA - jedna linia!
```java
em.persist(ship);  // ID zostanie automatycznie ustawione
```

### Pobieranie obiektu z bazy

#### ❌ JDBC - ręczne mapowanie ResultSet
```java
String sql = "SELECT * FROM ships WHERE id = ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setLong(1, id);
    try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
            Ship ship = new Ship();
            ship.setId(rs.getLong("id"));
            ship.setName(rs.getString("name"));
            ship.setType(rs.getString("type"));
            ship.setCannons(rs.getInt("cannons"));
            return ship;
        }
    }
}
```

#### ✅ JPA - jedna linia!
```java
Ship ship = em.find(Ship.class, id);
```

### Aktualizacja obiektu

#### ❌ JDBC - ręczny UPDATE
```java
String sql = "UPDATE ships SET cannons = ? WHERE id = ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setInt(1, ship.getCannons());
    stmt.setLong(2, ship.getId());
    stmt.executeUpdate();
}
```

#### ✅ JPA - automatyczne śledzenie zmian!
```java
Ship ship = em.find(Ship.class, 1L);  // Pobranie (MANAGED)
ship.setCannons(64);                   // Zmiana w pamięci
// Przy commit() Hibernate SAM wykryje zmianę i zrobi UPDATE!
```

---

## 🔑 Kluczowe pojęcia

| Pojęcie | Opis | Przykład |
|---------|------|----------|
| **Entity** | Klasa Java mapowana na tabelę | `@Entity class Ship` |
| **EntityManager** | Zarządza encjami, wykonuje operacje | `em.persist()`, `em.find()` |
| **EntityManagerFactory** | Tworzy EntityManagery, jeden na aplikację | `Persistence.createEntityManagerFactory()` |
| **Persistence Unit** | Konfiguracja w `persistence.xml` | `<persistence-unit name="pirates-pu">` |
| **@Id** | Klucz główny encji | `@Id Long id` |
| **@GeneratedValue** | Auto-increment ID | `@GeneratedValue(strategy = IDENTITY)` |
| **@Column** | Mapowanie na kolumnę | `@Column(name = "ship_name")` |
| **JPQL** | Język zapytań JPA (na obiektach) | `SELECT s FROM Ship s WHERE s.cannons > 30` |

---

## 🏗️ Anatomia encji JPA

```java
@Entity                                    // 1. Oznaczenie klasy jako encji
@Table(name = "ships")                     // 2. Nazwa tabeli (opcjonalne)
public class Ship {

    @Id                                    // 3. Klucz główny (WYMAGANE!)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 4. Auto-increment
    private Long id;

    @Column(name = "ship_name", nullable = false, length = 100)  // 5. Mapowanie kolumny
    private String name;

    @Column(name = "ship_type", length = 50)
    private String type;

    @Column                                // Nazwa kolumny = nazwa pola
    private Integer cannons;

    public Ship() {}                       // 6. Konstruktor bezargumentowy (WYMAGANE!)

    // Gettery i settery...
}
```

### Wymagania dla encji:
1. ✅ Adnotacja `@Entity`
2. ✅ Pole z `@Id` (klucz główny)
3. ✅ Publiczny konstruktor bezargumentowy
4. ✅ Klasa nie może być `final`
5. ✅ Pola nie mogą być `final`

---

## 🔧 Konfiguracja - persistence.xml

Plik `src/main/resources/META-INF/persistence.xml` konfiguruje JPA:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.0">

    <persistence-unit name="pirates-pu" transaction-type="RESOURCE_LOCAL">
        <!-- Provider (implementacja JPA) -->
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        
        <!-- Lista encji -->
        <class>pl.przemekzagorski.training.jpa.entity.Ship</class>

        <properties>
            <!-- Połączenie z bazą -->
            <property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:pirates"/>
            <property name="jakarta.persistence.jdbc.user" value="sa"/>
            <property name="jakarta.persistence.jdbc.password" value=""/>

            <!-- Hibernate: automatyczne tworzenie tabel -->
            <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
            
            <!-- Hibernate: pokazuj SQL w konsoli -->
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
        </properties>
    </persistence-unit>

</persistence>
```

### Opcje `hibernate.hbm2ddl.auto`:

| Wartość | Opis | Użycie |
|---------|------|--------|
| `create-drop` | Tworzy tabele na start, usuwa na koniec | Development, testy |
| `create` | Tworzy tabele (usuwa istniejące dane!) | Development |
| `update` | Aktualizuje schemat (nie usuwa danych) | Development |
| `validate` | Tylko sprawdza schemat | Produkcja |
| `none` | Nic nie robi | Produkcja |

⚠️ **NIGDY nie używaj `create` lub `create-drop` na produkcji!**

---

## 📁 Zawartość modułu

| Klasa/Plik | Opis |
|------------|------|
| `Ship.java` | Encja statku - przykład mapowania |
| `JpaDemo.java` | Demonstracja operacji CRUD w JPA |
| `JpaExercises.java` | Ćwiczenia dla kursantów |
| `JpaExercisesSolutions.java` | Rozwiązania ćwiczeń |
| `persistence.xml` | Konfiguracja JPA |

---

## 🚀 Jak uruchomić

### Uruchomienie demonstracji
```bash
cd day1-databases/m04-jpa-intro
mvn clean compile exec:java -Dexec.mainClass="pl.przemekzagorski.training.jpa.JpaDemo"
```

Lub uruchom `JpaDemo.main()` bezpośrednio w IntelliJ IDEA.

### Uruchomienie ćwiczeń
```bash
# Szkielety do wypełnienia
mvn exec:java -Dexec.mainClass="pl.przemekzagorski.training.jpa.JpaExercises"

# Rozwiązania
mvn exec:java -Dexec.mainClass="pl.przemekzagorski.training.jpa.JpaExercisesSolutions"
```

---

## 📊 Operacje CRUD w JPA

### CREATE - persist()
```java
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

Ship ship = new Ship("Black Pearl", "Galleon", 32);
em.persist(ship);  // Zapisuje do bazy, nadaje ID

em.getTransaction().commit();
em.close();

System.out.println("ID: " + ship.getId());  // ID jest już ustawione!
```

### READ - find()
```java
EntityManager em = emf.createEntityManager();

Ship ship = em.find(Ship.class, 1L);  // Pobiera po ID

if (ship != null) {
    System.out.println(ship.getName());
}

em.close();
```

### UPDATE - automatyczne śledzenie!
```java
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

Ship ship = em.find(Ship.class, 1L);  // Encja jest MANAGED
ship.setCannons(64);                   // Zmiana w pamięci

em.getTransaction().commit();  // Hibernate SAM zrobi UPDATE!
em.close();
```

⚠️ **Ważne:** Automatyczne śledzenie działa tylko dla encji w stanie **MANAGED** (pobranych w aktywnej transakcji).

### DELETE - remove()
```java
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

Ship ship = em.find(Ship.class, 1L);
em.remove(ship);  // Oznacza do usunięcia

em.getTransaction().commit();  // DELETE wykonany
em.close();
```

---

## 🔍 JPQL - Zapytania obiektowe

JPQL (Java Persistence Query Language) to język zapytań podobny do SQL, ale operuje na **obiektach**, nie na tabelach:

### SQL vs JPQL

| SQL | JPQL |
|-----|------|
| `SELECT * FROM ships` | `SELECT s FROM Ship s` |
| `SELECT * FROM ships WHERE cannons > 30` | `SELECT s FROM Ship s WHERE s.cannons > 30` |
| `SELECT name FROM ships` | `SELECT s.name FROM Ship s` |

### Przykłady JPQL

```java
// Wszystkie statki
List<Ship> allShips = em.createQuery("SELECT s FROM Ship s", Ship.class)
    .getResultList();

// Statki z warunkiem
List<Ship> galleons = em.createQuery(
    "SELECT s FROM Ship s WHERE s.type = :type", Ship.class)
    .setParameter("type", "Galleon")
    .getResultList();

// Sortowanie
List<Ship> sorted = em.createQuery(
    "SELECT s FROM Ship s ORDER BY s.cannons DESC", Ship.class)
    .getResultList();

// Jeden wynik
Ship strongest = em.createQuery(
    "SELECT s FROM Ship s ORDER BY s.cannons DESC", Ship.class)
    .setMaxResults(1)
    .getSingleResult();
```

---

## ⚠️ Częste błędy początkujących

### 1. Brak konstruktora bezargumentowego
```java
// ❌ BŁĄD - Hibernate nie może utworzyć obiektu
@Entity
public class Ship {
    public Ship(String name) { ... }  // Tylko ten konstruktor!
}

// ✅ POPRAWNIE
@Entity
public class Ship {
    public Ship() {}                   // Wymagany!
    public Ship(String name) { ... }   // Opcjonalny
}
```

### 2. Brak @Id
```java
// ❌ BŁĄD - każda encja musi mieć klucz główny
@Entity
public class Ship {
    private Long id;  // Brak @Id!
}

// ✅ POPRAWNIE
@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

### 3. Zapominanie o transakcji przy zapisie
```java
// ❌ BŁĄD - persist bez transakcji
em.persist(ship);  // TransactionRequiredException!

// ✅ POPRAWNIE
em.getTransaction().begin();
em.persist(ship);
em.getTransaction().commit();
```

### 4. Używanie encji po zamknięciu EntityManager
```java
// ❌ BŁĄD - Lazy loading po zamknięciu EM
Ship ship = em.find(Ship.class, 1L);
em.close();
ship.getCrew();  // LazyInitializationException! (jeśli crew jest @OneToMany LAZY)
```

---

## �️ Bean Validation (JSR 380)

Bean Validation pozwala walidować dane **przed zapisem do bazy** za pomocą adnotacji. Hibernate automatycznie waliduje encje przy `persist()` i `merge()`.

### Podstawowe adnotacje

| Adnotacja | Opis | Przykład |
|-----------|------|----------|
| `@NotNull` | Pole nie może być null | `@NotNull String name` |
| `@NotBlank` | Tekst nie może być pusty (ani same spacje) | `@NotBlank String name` |
| `@Size` | Ograniczenie długości | `@Size(min=2, max=100)` |
| `@Min` / `@Max` | Wartość liczbowa min/max | `@Min(0) @Max(200)` |
| `@Positive` | Wartość dodatnia | `@Positive Integer count` |
| `@Email` | Poprawny format email | `@Email String email` |
| `@Pattern` | Wzorzec regex | `@Pattern(regexp="[A-Z]+")` |

### Przykład encji z walidacją

```java
@Entity
public class Ship {
    @Id @GeneratedValue
    private Long id;

    @NotBlank(message = "Nazwa statku nie może być pusta")
    @Size(min = 2, max = 100, message = "Nazwa musi mieć od 2 do 100 znaków")
    private String name;

    @Min(value = 0, message = "Liczba dział nie może być ujemna")
    @Max(value = 200, message = "Statek może mieć maksymalnie 200 dział")
    private Integer cannons;

    @Positive(message = "Pojemność załogi musi być dodatnia")
    private Integer crewCapacity;
}
```

### Obsługa błędów walidacji

```java
try {
    em.persist(invalidShip);
    em.flush();
} catch (ConstraintViolationException e) {
    for (ConstraintViolation<?> v : e.getConstraintViolations()) {
        System.out.println(v.getPropertyPath() + ": " + v.getMessage());
    }
}
```

### Uruchomienie demo walidacji

```bash
cd m04-jpa-intro
mvn compile exec:java -Dexec.mainClass="pl.przemekzagorski.training.jpa.ValidationDemo"
```

---

## �🔗 Powiązane materiały

- **Poprzedni moduł:** `m03-jdbc-crud` - Wzorzec DAO w JDBC
- **Następny moduł:** `m05-entity-lifecycle` - Stany encji JPA
- **Dokumentacja:** [Jakarta Persistence 3.0](https://jakarta.ee/specifications/persistence/3.0/)
- **Hibernate docs:** [hibernate.org/orm/documentation](https://hibernate.org/orm/documentation/)

---

## 💡 Wskazówki

1. **EntityManagerFactory jest ciężki** - twórz jeden na aplikację
2. **EntityManager jest lekki** - twórz nowy dla każdej operacji/transakcji
3. **Zawsze zamykaj zasoby** - używaj try-with-resources lub try-finally
4. **JPQL operuje na klasach**, nie na tabelach - używaj nazw klas i pól Java
5. **Sprawdzaj SQL w konsoli** - `hibernate.show_sql=true` pokazuje co naprawdę idzie do bazy

---

🏴‍☠️ **Powodzenia w opanowaniu JPA! Od teraz Twój kod będzie znacznie czystszy!** ⚓
