# 🏴‍☠️ m03-jdbc-crud - Wzorzec DAO (Data Access Object)

## 🎯 Cel modułu

Ten moduł wprowadza **wzorzec DAO (Data Access Object)** - profesjonalny sposób organizacji kodu dostępu do bazy danych.

### Czym jest wzorzec DAO?

**DAO** to wzorzec projektowy, który **oddziela logikę dostępu do danych od logiki biznesowej**. Zamiast rozrzucać zapytania SQL po całej aplikacji, centralizujemy je w dedykowanych klasach DAO.

### Dlaczego używamy DAO?

| ❌ Bez DAO | ✅ Z DAO |
|------------|----------|
| SQL rozrzucony po całej aplikacji | SQL w jednym miejscu (DAO) |
| Trudne testowanie (trzeba bazy) | Łatwe testowanie (mockowanie DAO) |
| Zmiana bazy = zmiana wszędzie | Zmiana bazy = zmiana tylko DAO |
| Duplikacja kodu SQL | Reużywalne metody DAO |
| Mieszanie warstw (logika + SQL) | Czyste warstwy (Service → DAO → DB) |

---

## 📖 Kontekst - Progresja nauki

### 🔙 Moduł m02-jdbc-connection (poprzedni)

W module **m02-jdbc-connection** nauczyliśmy się:
- ✅ Łączyć się z bazą danych (JDBC Connection)
- ✅ Używać **PreparedStatement** (ochrona przed SQL Injection)
- ✅ Zarządzać **transakcjami** (`setAutoCommit(false)`, `commit()`, `rollback()`)
- ✅ Rozumieć **ACID** i kiedy używać ręcznych transakcji

**Problem:** Cały kod SQL był w metodzie `main()` - nieczytelny, nieprzenośny, nietestowalny.

### ➡️ Moduł m03-jdbc-crud (obecny)

Teraz **organizujemy ten kod w strukturę DAO**, aby:
- 🎯 **Oddzielić logikę biznesową od SQL** - Service nie wie o SQL, tylko wywołuje DAO
- 🧪 **Ułatwić testowanie** - możemy mockować DAO bez prawdziwej bazy
- 🔄 **Umożliwić wymianę implementacji** - JDBC → JPA → MongoDB bez zmiany kodu biznesowego
- 📐 **Zastosować Single Responsibility Principle** - każda klasa ma jedną odpowiedzialność

---

## 🔑 Kluczowe pojęcia

| Pojęcie | Opis | Przykład |
|---------|------|----------|
| **DAO** | Data Access Object - klasa odpowiedzialna za dostęp do danych | `PirateDao`, `ShipDao` |
| **CRUD** | Create, Read, Update, Delete - podstawowe operacje na danych | `save()`, `findById()`, `update()`, `delete()` |
| **Interface** | Kontrakt definiujący metody DAO (co, nie jak) | `PirateDao` (interfejs) |
| **Implementation** | Konkretna implementacja DAO (jak) | `JdbcPirateDao` (JDBC), `JpaPirateDao` (JPA) |
| **Service Layer** | Warstwa logiki biznesowej - używa DAO | `PirateService` wywołuje `PirateDao` |
| **Model/Entity** | POJO reprezentujący wiersz z bazy | `Pirate` (id, name, nickname, rank, bounty) |
| **Optional<T>** | Java 8+ - bezpieczna obsługa wartości null | `Optional<Pirate> findById(Long id)` |
| **ResultSet mapping** | Konwersja wiersza bazy → obiekt Java | `mapRowToPirate(ResultSet rs)` |

---

## 📁 Struktura kodu

### 1️⃣ **Model: `Pirate.java`**
```java
// POJO (Plain Old Java Object) - reprezentuje pirata z bazy
public class Pirate {
    private Long id;
    private String name;
    private String nickname;
    private String rank;
    private BigDecimal bounty;
    private Long shipId;
    private LocalDate joinedAt;
    // + gettery, settery, konstruktory
}
```

**Rola:** Reprezentuje wiersz z tabeli `pirates`. Prosta klasa bez logiki biznesowej.

---

### 2️⃣ **Interface: `PirateDao.java`**
```java
// Kontrakt - CO chcemy robić z piratami (nie JAK)
public interface PirateDao {
    Pirate save(Pirate pirate);              // CREATE
    Optional<Pirate> findById(Long id);      // READ (jeden)
    List<Pirate> findAll();                  // READ (wszyscy)
    List<Pirate> findByRank(String rank);    // READ (filtrowanie)
    void update(Pirate pirate);              // UPDATE
    void delete(Long id);                    // DELETE
    long count();                            // Pomocnicza
}
```

**Rola:** Definiuje **kontrakt** - jakie operacje są dostępne. Nie mówi JAK są zaimplementowane.

**Zalety interfejsu:**
- 🔄 Możemy mieć wiele implementacji (JDBC, JPA, MongoDB)
- 🧪 Łatwe mockowanie w testach
- 📐 Dependency Inversion Principle (zależność od abstrakcji, nie konkretów)

---

### 3️⃣ **Implementation: `JdbcPirateDao.java`**
```java
// Implementacja JDBC - JAK wykonujemy operacje
public class JdbcPirateDao implements PirateDao {
    private final Connection connection;

    @Override
    public Pirate save(Pirate pirate) {
        String sql = "INSERT INTO pirates (name, nickname, ...) VALUES (?, ?, ...)";
        try (PreparedStatement stmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            // Ustawienie parametrów, wykonanie, pobranie ID
        }
    }

    private Pirate mapRowToPirate(ResultSet rs) throws SQLException {
        // Konwersja wiersza bazy → obiekt Pirate
    }
}
```

**Rola:** Konkretna implementacja używająca **JDBC**. Zawiera cały kod SQL.

**Kluczowe techniki:**
- ✅ **PreparedStatement** - ochrona przed SQL Injection
- ✅ **RETURN_GENERATED_KEYS** - pobieranie auto-increment ID
- ✅ **Try-with-resources** - automatyczne zamykanie zasobów
- ✅ **mapRowToPirate()** - helper do konwersji ResultSet → Pirate
- ✅ **Optional<Pirate>** - bezpieczna obsługa braku wyniku
- ✅ **RuntimeException** - opakowywanie SQLException

---

### 4️⃣ **Demo: `CrudDemo.java`**
```java
public class CrudDemo {
    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection(...)) {
            setupDatabase(conn);

            // Tworzymy DAO
            PirateDao pirateDao = new JdbcPirateDao(conn);

            // CREATE
            Pirate jack = new Pirate("Jack Sparrow", "Captain Jack", ...);
            pirateDao.save(jack);

            // READ
            List<Pirate> all = pirateDao.findAll();
            Optional<Pirate> found = pirateDao.findById(jack.getId());

            // UPDATE
            jack.setBounty(new BigDecimal("25000.00"));
            pirateDao.update(jack);

            // DELETE
            pirateDao.delete(barbossa.getId());
        }
    }
}
```

**Rola:** Demonstracja wszystkich operacji CRUD. Pokazuje jak używać DAO.


**Kluczowe elementy:**
- 🏗️ **setupDatabase()** - tworzy tabelę `pirates` z AUTO_INCREMENT
- 🎯 **Demonstracja CRUD** - pokazuje wszystkie operacje w kolejności
- 📊 **Wyświetlanie wyników** - po każdej operacji pokazujemy stan bazy

---

### 5️⃣ **Demo: `TransactionDemo.java`**
```java
public class TransactionDemo {
    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection(...)) {
            setupDatabase(conn);

            // ✅ SUKCES - transfer 500 złota z Tortuga → Port Royal
            boolean success = transferTreasure(conn, "Tortuga", "Port Royal", 500);

            // ❌ PORAŻKA - transfer 2000 złota (za dużo!) → ROLLBACK
            boolean failure = transferTreasure(conn, "Tortuga", "Port Royal", 2000);
        }
    }

    private static boolean transferTreasure(Connection conn, String from, String to, int amount) {
        try {
            conn.setAutoCommit(false); // 🔒 Wyłączamy auto-commit

            // 1. Odejmij złoto z wyspy źródłowej
            // 2. Dodaj złoto do wyspy docelowej

            conn.commit(); // ✅ Zatwierdzamy obie operacje
            return true;
        } catch (SQLException e) {
            conn.rollback(); // ❌ Cofamy obie operacje
            return false;
        } finally {
            conn.setAutoCommit(true); // 🔓 Przywracamy auto-commit
        }
    }
}
```

**Rola:** Demonstracja **transakcji** - wiele operacji jako jedna atomowa jednostka.

**Kluczowe techniki:**
- 🔒 **setAutoCommit(false)** - wyłączenie automatycznego commitu
- ✅ **commit()** - zatwierdzenie wszystkich operacji
- ❌ **rollback()** - cofnięcie wszystkich operacji w przypadku błędu
- 🔓 **finally** - przywrócenie auto-commit

**Scenariusze:**
1. ✅ **Sukces** - transfer 500 złota (wystarczająco) → COMMIT
2. ❌ **Porażka** - transfer 2000 złota (za dużo!) → ROLLBACK

---

## 🚀 Jak uruchomić

### Uruchomienie CrudDemo (operacje CRUD)
```bash
cd day1-databases/m03-jdbc-crud
mvn clean compile exec:java -Dexec.mainClass="pl.przemekzagorski.training.jdbc.CrudDemo"
```

### Uruchomienie TransactionDemo (transakcje)
```bash
cd day1-databases/m03-jdbc-crud
mvn clean compile exec:java -Dexec.mainClass="pl.przemekzagorski.training.jdbc.TransactionDemo"
```

**Oczekiwany wynik:**
- 📊 Wyświetlenie wszystkich operacji CRUD (CREATE, READ, UPDATE, DELETE)
- ✅ Sukces transferu złota (500 złota)
- ❌ Porażka transferu złota (2000 złota - za dużo!) z rollbackiem

---

## ✅ Zalety wzorca DAO

### 1️⃣ **Abstrakcja - oddzielenie warstw**
```java
// ❌ BEZ DAO - logika biznesowa zmieszana z SQL
public class PirateService {
    public void promotePirate(Long pirateId) {
        String sql = "UPDATE pirates SET rank = 'Captain' WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, pirateId);
            stmt.executeUpdate();
        }
        // Logika biznesowa zmieszana z SQL!
    }
}

// ✅ Z DAO - czyste warstwy
public class PirateService {
    private PirateDao pirateDao;

    public void promotePirate(Long pirateId) {
        Pirate pirate = pirateDao.findById(pirateId).orElseThrow();
        pirate.setRank("Captain");
        pirateDao.update(pirate);
        // Service nie wie o SQL - tylko o obiektach!
    }
}
```

**Korzyść:** Service Layer nie wie o SQL - łatwiej czytać, testować, utrzymywać.

---

### 2️⃣ **Testowalność - mockowanie DAO**
```java
// ✅ Test bez prawdziwej bazy danych
@Test
public void shouldPromotePirate() {
    // Mock DAO - nie potrzebujemy bazy!
    PirateDao mockDao = mock(PirateDao.class);
    when(mockDao.findById(1L)).thenReturn(Optional.of(new Pirate(...)));

    PirateService service = new PirateService(mockDao);
    service.promotePirate(1L);

    verify(mockDao).update(any(Pirate.class));
}
```

**Korzyść:** Testy szybkie (bez bazy), niezależne, łatwe do pisania.

---

### 3️⃣ **Wymienność implementacji - JDBC → JPA → MongoDB**
```java
// Interfejs pozostaje ten sam
public interface PirateDao {
    Pirate save(Pirate pirate);
    Optional<Pirate> findById(Long id);
    // ...
}

// Implementacja 1: JDBC
public class JdbcPirateDao implements PirateDao { ... }

// Implementacja 2: JPA (Hibernate)
public class JpaPirateDao implements PirateDao { ... }

// Implementacja 3: MongoDB
public class MongoPirateDao implements PirateDao { ... }

// Service nie musi się zmieniać!
public class PirateService {
    private PirateDao pirateDao; // Może być JDBC, JPA, MongoDB!
}
```

**Korzyść:** Zmiana bazy danych = zmiana tylko implementacji DAO, nie całej aplikacji.

---

### 4️⃣ **Reużywalność - jedna metoda, wiele miejsc**
```java
// ✅ Metoda findByRank() użyta w wielu miejscach
public class PirateService {
    public List<Pirate> getCaptains() {
        return pirateDao.findByRank("Captain");
    }

    public List<Pirate> getQuartermasters() {
        return pirateDao.findByRank("Quartermaster");
    }
}

// Bez DAO musielibyśmy duplikować SQL w każdym miejscu!
```

**Korzyść:** Kod SQL w jednym miejscu - łatwiej utrzymywać, mniej błędów.

---

## 🎨 Wyjaśnienie wzorca DAO - Architektura

```
┌─────────────────────────────────────────────────────────────┐
│                         USER                                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                         │
│  (Obsługa HTTP, walidacja, routing)                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    SERVICE LAYER                            │
│  ✅ Logika biznesowa (promocja pirata, transfer złota)      │
│  ✅ Transakcje (wiele operacji DAO)                         │
│  ✅ Walidacja biznesowa                                     │
│  ❌ NIE WIE O SQL!                                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DAO LAYER                              │
│  ✅ Operacje CRUD (save, findById, update, delete)          │
│  ✅ Zapytania SQL                                           │
│  ✅ Mapowanie ResultSet → Object                            │
│  ❌ NIE WIE O LOGICE BIZNESOWEJ!                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DATABASE                              │
│  (PostgreSQL, MySQL, H2, MongoDB, ...)                      │
└─────────────────────────────────────────────────────────────┘
```

**Kluczowa zasada:** Każda warstwa ma **jedną odpowiedzialność** i **nie wie o szczegółach innych warstw**.

---

## 📊 Progresja nauki - m02 vs m03

| Aspekt | m02-jdbc-connection | m03-jdbc-crud |
|--------|---------------------|---------------|
| **Organizacja kodu** | Wszystko w `main()` | Oddzielne klasy (DAO, Model) |
| **SQL** | Rozrzucony po metodach | Centralizowany w DAO |
| **Testowalność** | Trudna (trzeba bazy) | Łatwa (mockowanie DAO) |
| **Reużywalność** | Duplikacja kodu SQL | Metody DAO wielokrotnego użytku |
| **Wymienność bazy** | Zmiana wszędzie | Zmiana tylko w DAO |
| **Zasady SOLID** | Brak | Single Responsibility, Dependency Inversion |
| **Poziom abstrakcji** | Niski (SQL) | Wysoki (obiekty Java) |

**Przykład - znalezienie pirata:**

```java
// ❌ m02 - SQL w main()
String sql = "SELECT * FROM pirates WHERE id = ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setLong(1, pirateId);
    ResultSet rs = stmt.executeQuery();
    if (rs.next()) {
        String name = rs.getString("name");
        // ... mapowanie ręczne
    }
}

// ✅ m03 - DAO
Optional<Pirate> pirate = pirateDao.findById(pirateId);
pirate.ifPresent(p -> System.out.println(p.getName()));
```

---

## 🧪 Testowanie DAO

### Dlaczego testujemy DAO?

DAO to **krytyczna warstwa** aplikacji - błąd tutaj może oznaczać:
- Utracone dane klientów
- Błędne raporty finansowe
- Naruszenie integralności bazy

### Wzorzec: Testy integracyjne z H2 in-memory

```java
@DisplayName("JdbcPirateDao - testy integracyjne")
class JdbcPirateDaoTest {

    private Connection connection;
    private JdbcPirateDao dao;

    @BeforeEach
    void setUp() throws SQLException {
        // Każdy test ma CZYSTĄ bazę danych
        connection = DriverManager.getConnection(
            "jdbc:h2:mem:testdb_" + System.nanoTime(),
            "sa", ""
        );
        
        // Tworzenie tabeli
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE pirates (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    rank VARCHAR(50),
                    bounty DECIMAL(15, 2)
                )
            """);
        }
        
        dao = new JdbcPirateDao(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    @DisplayName("save() powinien zapisać pirata i przypisać ID")
    void shouldSavePirateAndAssignId() {
        // Given
        Pirate pirate = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));

        // When
        Pirate saved = dao.save(pirate);

        // Then
        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getName()).isEqualTo("Jack Sparrow");
    }

    @Test
    @DisplayName("findById() powinien zwrócić empty dla nieistniejącego ID")
    void shouldReturnEmptyForNonExistentId() {
        // When
        Optional<Pirate> found = dao.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }
}
```

### Wzorzec AAA (Arrange-Act-Assert)

Każdy test powinien mieć trzy sekcje:

```java
@Test
void exampleTest() {
    // Arrange (Given) - przygotowanie danych
    Pirate pirate = new Pirate("Jack", "Captain", new BigDecimal("100000"));
    dao.save(pirate);

    // Act (When) - wykonanie testowanej operacji
    List<Pirate> captains = dao.findByRank("Captain");

    // Assert (Then) - weryfikacja wyników
    assertThat(captains).hasSize(1);
    assertThat(captains.get(0).getName()).isEqualTo("Jack");
}
```

### Testy w tym module

Plik `JdbcPirateDaoTest.java` zawiera pełny zestaw testów:

| Grupa testów | Co testuje |
|--------------|------------|
| `SaveTests` | Zapis pirata, przypisanie ID, null w polach opcjonalnych |
| `FindByIdTests` | Znajdowanie istniejącego, obsługa nieistniejącego |
| `FindAllTests` | Pusta lista, wszystkie rekordy |
| `FindByRankTests` | Filtrowanie według rangi |
| `UpdateTests` | Aktualizacja danych, niezmienność ID |
| `DeleteTests` | Usuwanie, brak wyjątku dla nieistniejącego |
| `CountTests` | Zliczanie, aktualizacja po operacjach |

### Uruchamianie testów

```bash
# Wszystkie testy w module
mvn test -pl day1-databases/m03-jdbc-crud

# Konkretna klasa testowa
mvn test -pl day1-databases/m03-jdbc-crud -Dtest=JdbcPirateDaoTest
```

---

## 💡 Wskazówki dla trenera

### 🎯 Kluczowe punkty do podkreślenia:

1. **DAO to nie magia** - to po prostu klasa, która grupuje operacje SQL
2. **Interface vs Implementation** - interfejs = kontrakt (CO), implementacja = szczegóły (JAK)
3. **Separation of Concerns** - Service nie wie o SQL, DAO nie wie o logice biznesowej
4. **Optional<T>** - bezpieczna obsługa braku wyniku (zamiast null)
5. **Transakcje w DAO** - zazwyczaj zarządzane przez Service Layer, nie DAO

### ⚠️ Częste błędy kursantów:

1. **Mieszanie warstw** - logika biznesowa w DAO lub SQL w Service
2. **Zapominanie o try-with-resources** - wycieki zasobów (Connection, Statement, ResultSet)
3. **Ignorowanie Optional** - używanie `.get()` zamiast `.orElseThrow()` lub `.ifPresent()`
4. **Transakcje w DAO** - transakcje powinny być w Service, nie w pojedynczych metodach DAO
5. **Brak obsługi null** - zapominanie o `setNull()` dla nullable kolumn

### 🏴‍☠️ Ćwiczenia dodatkowe:

1. Dodaj metodę `findByBountyGreaterThan(BigDecimal amount)`
2. Zaimplementuj `updateBounty(Long pirateId, BigDecimal newBounty)`
3. Stwórz `ShipDao` z analogicznymi metodami CRUD
4. Dodaj transakcję: transfer pirata między statkami

---

## 🎓 Następne kroki

Po opanowaniu tego modułu kursanci będą gotowi do:

1. **m04-spring-jdbc** - Spring JdbcTemplate (mniej boilerplate)
2. **m05-entity-lifecycle** - JPA/Hibernate (ORM - Object-Relational Mapping)
3. **m06-spring-data-jpa** - Spring Data JPA (DAO bez implementacji!)

**Progresja:**
- ✅ **m02** - Surowy JDBC (PreparedStatement, transakcje)
- ✅ **m03** - Wzorzec DAO (organizacja kodu)
- ⏭️ **m04** - Spring JdbcTemplate (mniej kodu)
- ⏭️ **m05** - JPA/Hibernate (mapowanie obiektowe)
- ⏭️ **m06** - Spring Data JPA (automatyczne DAO!)

---

## 📚 Dodatkowe zasoby

- [Oracle JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- [DAO Pattern - Oracle](https://www.oracle.com/java/technologies/data-access-object.html)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Optional in Java 8](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)

---

**🏴‍☠️ Powodzenia w opanowaniu wzorca DAO, młody piraci! Niech wasz kod będzie czysty jak woda w Zatoce Karaibskiej! ⚓**
