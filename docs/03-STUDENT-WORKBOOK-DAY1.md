# 📚 PRZEWODNIK DLA KURSANTA - DZIEŃ 1: BAZY DANYCH

## 🏴‍☠️ Java Training Core - Piraci z Karaibów

**Data:** ________________
**Imię i nazwisko:** ________________
**Grupa:** ________________

---

## 📋 SPIS TREŚCI

1. [Blok 1: SQL - Podstawy (9:00-9:45)](#blok-1-sql---podstawy)
2. [Blok 2: SQL - Zaawansowane (9:45-10:30)](#blok-2-sql---zaawansowane)
3. [Blok 3: JDBC - Połączenie (10:45-11:30)](#blok-3-jdbc---połączenie)
4. [Blok 4: JDBC - PreparedStatement (11:30-12:15)](#blok-4-jdbc---preparedstatement)
5. [Blok 5: JDBC - Wzorzec DAO (13:15-14:00)](#blok-5-jdbc---wzorzec-dao)
6. [Blok 6: JPA - Entity i EntityManager (14:00-14:45)](#blok-6-jpa---entity-i-entitymanager)
7. [Blok 7: JPA - Relacje i JPQL (15:00-15:45)](#blok-7-jpa---relacje-i-jpql)
8. [Blok 8: Spring Data JPA (15:45-16:30)](#blok-8-spring-data-jpa)
9. [Notatki własne](#notatki-własne)
10. [Checklist - Co dzisiaj opanowałem?](#checklist)

---

## 🎯 CELE DNIA 1

Po dzisiejszym szkoleniu będziesz potrafił:

- [ ] Pisać zapytania SQL (SELECT, INSERT, UPDATE, DELETE)
- [ ] Łączyć tabele używając JOIN
- [ ] Agregować dane używając GROUP BY
- [ ] Łączyć się z bazą danych przez JDBC
- [ ] Unikać SQL Injection używając PreparedStatement
- [ ] Implementować wzorzec DAO
- [ ] Mapować encje JPA
- [ ] Tworzyć relacje między encjami
- [ ] Używać Spring Data JPA do uproszczenia kodu

---

## 📦 PRZYGOTOWANIE ŚRODOWISKA

### Sprawdź przed szkoleniem:

```bash
# 1. Java 17+
java -version

# 2. Maven 3.8+
mvn -version

# 3. IDE (IntelliJ IDEA zalecane)
```

### Struktura projektu:

```
java-training-core/
├── day1-databases/
│   ├── m01-sql-basics/      ← Blok 1-2
│   ├── m02-sql-advanced/    ← Blok 2
│   ├── m03-jdbc-crud/       ← Blok 3-5
│   ├── m04-jpa-intro/       ← Blok 6
│   ├── m05-jpa-relations/   ← Blok 7
│   └── m06-spring-data/     ← Blok 8
└── day2-code-quality/       ← Jutro!
```

---

## ⏰ AGENDA DNIA

| Czas | Blok | Temat |
|------|------|-------|
| 9:00-9:45 | 1 | SQL - Podstawy (SELECT, WHERE, JOIN) |
| 9:45-10:30 | 2 | SQL - Zaawansowane (GROUP BY, subqueries) |
| ☕ 10:30-10:45 | - | Przerwa kawowa |
| 10:45-11:30 | 3 | JDBC - Connection, Statement |
| 11:30-12:15 | 4 | JDBC - PreparedStatement, SQL Injection |
| 🍽️ 12:15-13:15 | - | Przerwa obiadowa |
| 13:15-14:00 | 5 | JDBC - Wzorzec DAO |
| 14:00-14:45 | 6 | JPA - Entity, EntityManager |
| ☕ 14:45-15:00 | - | Przerwa kawowa |
| 15:00-15:45 | 7 | JPA - Relacje, JPQL |
| 15:45-16:30 | 8 | Spring Data JPA |

---

# 🔷 BLOK 1: SQL - PODSTAWY (9:00-9:45)

## 📖 Notatki z teorii

**Kluczowe pojęcia:**
- **SELECT** - pobieranie danych
- **WHERE** - filtrowanie wyników
- **JOIN** - łączenie tabel
- **ORDER BY** - sortowanie

## 🎬 DEMO: Uruchomienie H2 Console

```bash
# W katalogu day1-databases/m01-sql-basics:
mvn spring-boot:run

# Otwórz w przeglądarce:
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:piratesdb
# User: sa, Password: (puste)
```

## ✍️ ĆWICZENIE 1.1: Podstawowe SELECT

**Zadanie:** Wykonaj poniższe zapytania w H2 Console:

```sql
-- 1. Wyświetl wszystkich piratów
SELECT * FROM pirates;

-- 2. Wyświetl imiona i rangi piratów
SELECT name, rank FROM pirates;

-- 3. Posortuj piratów po bounty malejąco
SELECT name, bounty FROM pirates ORDER BY bounty DESC;
```

**Twoje notatki:**
```
_______________________________________________
_______________________________________________
_______________________________________________
```

## ✍️ ĆWICZENIE 1.2: WHERE - Filtrowanie

**Zadanie:** Napisz zapytania:

```sql
-- 1. Znajdź piratów z bounty > 1000
SELECT * FROM pirates WHERE _______;

-- 2. Znajdź pirata o imieniu "Jack Sparrow"
SELECT * FROM pirates WHERE _______;

-- 3. Znajdź piratów z rangą "Captain" LUB bounty > 500
SELECT * FROM pirates WHERE _______ OR _______;
```



## ✍️ ĆWICZENIE 1.3: JOIN - Łączenie tabel

**Zadanie:** Połącz tabele pirates i ships:

```sql
-- 1. Wyświetl piratów wraz z nazwami ich statków
SELECT p.name, s.name AS ship_name
FROM pirates p
_______ ships s ON p.ship_id = s.id;

-- 2. Wyświetl wszystkich piratów, nawet tych bez statku
SELECT p.name, s.name AS ship_name
FROM pirates p
_______ JOIN ships s ON p.ship_id = s.id;

-- 3. Policz piratów na każdym statku
SELECT s.name, COUNT(p.id) AS crew_count
FROM ships s
LEFT JOIN pirates p ON s.id = p.ship_id
GROUP BY _______;
```

**Rozwiązanie:**
<details>
<summary>Pokaż rozwiązanie</summary>

```sql
-- 1. INNER JOIN (lub samo JOIN)
SELECT p.name, s.name AS ship_name
FROM pirates p
JOIN ships s ON p.ship_id = s.id;

-- 2. LEFT JOIN
SELECT p.name, s.name AS ship_name
FROM pirates p
LEFT JOIN ships s ON p.ship_id = s.id;

-- 3. GROUP BY s.name
SELECT s.name, COUNT(p.id) AS crew_count
FROM ships s
LEFT JOIN pirates p ON s.id = p.ship_id
GROUP BY s.name;
```
</details>

**✅ Checkpoint Blok 1:**
- [ ] Potrafię napisać SELECT z WHERE
- [ ] Rozumiem różnicę między INNER JOIN i LEFT JOIN
- [ ] Umiem sortować wyniki (ORDER BY)

---

# 🔷 BLOK 2: SQL - ZAAWANSOWANE (9:45-10:30)

## 📖 Notatki z teorii

**Kluczowe pojęcia:**
- **GROUP BY** - grupowanie wierszy
- **HAVING** - filtrowanie grup (po agregacji)
- **COUNT, SUM, AVG, MAX, MIN** - funkcje agregujące
- **Subquery** - zapytanie wewnątrz zapytania

**Różnica WHERE vs HAVING:**
```
WHERE  → filtruje PRZED grupowaniem
HAVING → filtruje PO grupowaniu (na agregatach)
```

## ✍️ ĆWICZENIE 2.1: GROUP BY i funkcje agregujące

**Zadanie:** Napisz zapytania:

```sql
-- 1. Policz piratów w każdej randze
SELECT rank, COUNT(*) AS count
FROM pirates
GROUP BY _______;

-- 2. Znajdź średnie bounty dla każdej rangi
SELECT rank, _______(bounty) AS avg_bounty
FROM pirates
GROUP BY rank;

-- 3. Znajdź sumę bounty dla każdego statku
SELECT s.name, SUM(p.bounty) AS total_bounty
FROM ships s
LEFT JOIN pirates p ON s.id = p.ship_id
GROUP BY s.name
ORDER BY total_bounty DESC;
```

**Rozwiązanie:**
<details>
<summary>Pokaż rozwiązanie</summary>

```sql
-- 1.
SELECT rank, COUNT(*) AS count
FROM pirates
GROUP BY rank;

-- 2.
SELECT rank, AVG(bounty) AS avg_bounty
FROM pirates
GROUP BY rank;

-- 3. (już kompletne)
SELECT s.name, SUM(p.bounty) AS total_bounty
FROM ships s
LEFT JOIN pirates p ON s.id = p.ship_id
GROUP BY s.name
ORDER BY total_bounty DESC;
```
</details>

## ✍️ ĆWICZENIE 2.2: HAVING - Filtrowanie grup

**Zadanie:** Napisz zapytania z HAVING:

```sql
-- 1. Znajdź rangi, które mają więcej niż 2 piratów
SELECT rank, COUNT(*) AS count
FROM pirates
GROUP BY rank
HAVING _______;

-- 2. Znajdź statki z łącznym bounty > 2000
SELECT s.name, SUM(p.bounty) AS total_bounty
FROM ships s
JOIN pirates p ON s.id = p.ship_id
GROUP BY s.name
HAVING _______;
```

**Rozwiązanie:**
<details>
<summary>Pokaż rozwiązanie</summary>

```sql
-- 1.
SELECT rank, COUNT(*) AS count
FROM pirates
GROUP BY rank
HAVING COUNT(*) > 2;

-- 2.
SELECT s.name, SUM(p.bounty) AS total_bounty
FROM ships s
JOIN pirates p ON s.id = p.ship_id
GROUP BY s.name
HAVING SUM(p.bounty) > 2000;
```
</details>

## ✍️ ĆWICZENIE 2.3: Subqueries (Podzapytania)

**Zadanie:** Napisz zapytania z podzapytaniami:

```sql
-- 1. Znajdź piratów z bounty wyższym niż średnia
SELECT name, bounty
FROM pirates
WHERE bounty > (SELECT _______ FROM pirates);

-- 2. Znajdź piratów na największym statku
SELECT * FROM pirates
WHERE ship_id = (
    SELECT id FROM ships
    ORDER BY crew_capacity DESC
    LIMIT 1
);
```

**Rozwiązanie:**
<details>
<summary>Pokaż rozwiązanie</summary>

```sql
-- 1.
SELECT name, bounty
FROM pirates
WHERE bounty > (SELECT AVG(bounty) FROM pirates);

-- 2. (już kompletne)
SELECT * FROM pirates
WHERE ship_id = (
    SELECT id FROM ships
    ORDER BY crew_capacity DESC
    LIMIT 1
);
```
</details>

**✅ Checkpoint Blok 2:**
- [ ] Potrafię używać GROUP BY z funkcjami agregującymi
- [ ] Rozumiem różnicę między WHERE i HAVING
- [ ] Umiem pisać podzapytania (subqueries)

---

# ☕ PRZERWA KAWOWA (10:30-10:45)



---

# 🔗 BLOK 3: JDBC - Connection i Statement (10:45-11:30)

## 📚 Notatki teoretyczne

### Architektura JDBC

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Aplikacja     │────▶│   JDBC Driver   │────▶│    Baza danych  │
│   Java          │     │   (H2, MySQL)   │     │    (H2)         │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

**Główne komponenty JDBC:**
1. `DriverManager` - zarządza _______________
2. `Connection` - reprezentuje _______________
3. `Statement` - służy do _______________
4. `ResultSet` - zawiera _______________

### Connection String

Format: `jdbc:h2:mem:pirates;DB_CLOSE_DELAY=-1`
- `jdbc:h2` - protokół i typ bazy
- `mem:pirates` - baza w pamięci o nazwie "pirates"
- `DB_CLOSE_DELAY=-1` - nie zamykaj bazy po rozłączeniu

**Moje notatki:**
```
_________________________________________________________________
_________________________________________________________________
```

---

## 👀 Demo 3.1: Pierwsze połączenie JDBC

**Obserwuj:**
1. Jak tworzymy połączenie z bazą?
2. Jak wykonujemy zapytanie SQL?
3. Jak przetwarzamy wyniki?
4. Jak zamykamy zasoby?

**Kod demo (z modułu m03-jdbc-crud):**
```java
String url = "jdbc:h2:mem:pirates";
Connection connection = DriverManager.getConnection(url);

Statement statement = connection.createStatement();
ResultSet resultSet = statement.executeQuery("SELECT * FROM pirates");

while (resultSet.next()) {
    String name = resultSet.getString("name");
    System.out.println(name);
}

// WAŻNE: Zamykanie zasobów!
resultSet.close();
statement.close();
connection.close();
```

**Pytanie do siebie:** Co się stanie, jeśli zapomnimy zamknąć Connection?

---

## ✍️ Ćwiczenie 3.1: Podstawowe połączenie JDBC

**Cel:** Utworzyć połączenie z bazą H2 i wyświetlić listę piratów

**Lokalizacja:** `day1-databases/m03-jdbc-crud/src/main/java/`

**Zadanie:** Uzupełnij poniższy kod:

```java
public class BasicJdbcDemo {

    private static final String URL = "jdbc:h2:mem:pirates";

    public static void main(String[] args) {
        // TODO 1: Utwórz połączenie używając DriverManager.getConnection()

        // TODO 2: Utwórz Statement

        // TODO 3: Wykonaj SELECT * FROM pirates

        // TODO 4: Iteruj po wynikach i wyświetl imiona piratów

        // TODO 5: Zamknij wszystkie zasoby w odpowiedniej kolejności
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public class BasicJdbcDemo {

    private static final String URL = "jdbc:h2:mem:pirates";

    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM pirates");

        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String rank = resultSet.getString("rank");
            System.out.printf("ID: %d, Imię: %s, Ranga: %s%n", id, name, rank);
        }

        resultSet.close();
        statement.close();
        connection.close();
    }
}
```
</details>

---

## ✍️ Ćwiczenie 3.2: Try-with-resources (bezpieczne zamykanie)

**Cel:** Przepisać kod używając try-with-resources

**Problem:** Ręczne zamykanie zasobów jest podatne na błędy!

**Zadanie:** Przepisz Ćwiczenie 3.1 używając try-with-resources:

```java
public class SafeJdbcDemo {

    private static final String URL = "jdbc:h2:mem:pirates";

    public static void main(String[] args) {
        // TODO: Użyj try-with-resources dla Connection, Statement i ResultSet
        // try (Connection conn = ...; Statement stmt = ...) {
        //     ResultSet rs = stmt.executeQuery(...);
        //     ...
        // }
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public class SafeJdbcDemo {

    private static final String URL = "jdbc:h2:mem:pirates";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM pirates")) {

            while (resultSet.next()) {
                System.out.println(resultSet.getString("name"));
            }

        } catch (SQLException e) {
            System.err.println("Błąd bazy danych: " + e.getMessage());
        }
    }
}
```
</details>

---

## ✍️ Ćwiczenie 3.3: INSERT, UPDATE, DELETE

**Cel:** Wykonać operacje modyfikujące dane

**Ważne:** Dla INSERT/UPDATE/DELETE używamy `executeUpdate()` zamiast `executeQuery()`!

```java
// TODO: Uzupełnij metody

public int addPirate(String name, String rank) {
    String sql = "INSERT INTO pirates (name, rank) VALUES ('" + name + "', '" + rank + "')";
    // TODO: Wykonaj zapytanie i zwróć liczbę zmodyfikowanych wierszy
    return _______________
}

public int updatePirateRank(long id, String newRank) {
    String sql = "UPDATE pirates SET rank = '" + newRank + "' WHERE id = " + id;
    // TODO: Wykonaj zapytanie
    return _______________
}

public int deletePirate(long id) {
    String sql = "DELETE FROM pirates WHERE id = " + id;
    // TODO: Wykonaj zapytanie
    return _______________
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public int addPirate(String name, String rank) throws SQLException {
    String sql = "INSERT INTO pirates (name, rank) VALUES ('" + name + "', '" + rank + "')";
    try (Statement stmt = connection.createStatement()) {
        return stmt.executeUpdate(sql);
    }
}

public int updatePirateRank(long id, String newRank) throws SQLException {
    String sql = "UPDATE pirates SET rank = '" + newRank + "' WHERE id = " + id;
    try (Statement stmt = connection.createStatement()) {
        return stmt.executeUpdate(sql);
    }
}

public int deletePirate(long id) throws SQLException {
    String sql = "DELETE FROM pirates WHERE id = " + id;
    try (Statement stmt = connection.createStatement()) {
        return stmt.executeUpdate(sql);
    }
}
```

⚠️ **UWAGA:** Ten kod ma poważny problem bezpieczeństwa! Jaki? Omówimy w następnym bloku!
</details>

---

## ✅ Checkpoint Bloku 3

- [ ] Rozumiem jak działa DriverManager.getConnection()
- [ ] Potrafię wykonać SELECT i przetworzyć ResultSet
- [ ] Wiem różnicę między executeQuery() i executeUpdate()
- [ ] Stosuję try-with-resources do zamykania zasobów




---

# 🛡️ BLOK 4: JDBC - PreparedStatement i SQL Injection (11:30-12:15)

## 📚 Notatki teoretyczne

### SQL Injection - największe zagrożenie!

**Co to jest SQL Injection?**
Atak polegający na wstrzyknięciu złośliwego kodu SQL przez dane wejściowe użytkownika.

**Przykład podatnego kodu:**
```java
String name = request.getParameter("name"); // Użytkownik podaje: ' OR '1'='1
String sql = "SELECT * FROM users WHERE name = '" + name + "'";
// Wynikowe SQL: SELECT * FROM users WHERE name = '' OR '1'='1'
// ⚠️ Zwraca WSZYSTKICH użytkowników!
```

**Moje notatki - jak działa atak:**
```
_________________________________________________________________
_________________________________________________________________
```

### PreparedStatement - rozwiązanie!

| Statement | PreparedStatement |
|-----------|-------------------|
| Konkatenacja stringów | Parametry `?` |
| Podatny na SQL Injection | ✅ Bezpieczny |
| Kompilowany za każdym razem | Prekompilowany (szybszy) |

**Moje notatki:**
```
_________________________________________________________________
```

---

## 👀 Demo 4.1: SQL Injection na żywo

**Obserwuj co się dzieje gdy wprowadzimy:**
1. Normalne imię: `Jack Sparrow`
2. Złośliwy input: `' OR '1'='1`
3. Destrukcyjny input: `'; DROP TABLE pirates; --`

**Co zaobserwowałem:**
```
_________________________________________________________________
_________________________________________________________________
```

---

## ✍️ Ćwiczenie 4.1: Wykryj podatność

**Cel:** Zidentyfikować podatny kod i przepisać go bezpiecznie

**Podatny kod:**
```java
public Pirate findByName(String name) throws SQLException {
    String sql = "SELECT * FROM pirates WHERE name = '" + name + "'";
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery(sql);
    // ...
}
```

**Zadanie:** Przepisz używając PreparedStatement:

```java
public Pirate findByName(String name) throws SQLException {
    // TODO 1: Napisz SQL z parametrem ? zamiast konkatenacji
    String sql = _______________

    // TODO 2: Utwórz PreparedStatement
    PreparedStatement pstmt = _______________

    // TODO 3: Ustaw parametr (indeks zaczyna się od 1!)
    pstmt._______________

    // TODO 4: Wykonaj zapytanie
    ResultSet rs = _______________

    // Reszta kodu...
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public Pirate findByName(String name) throws SQLException {
    String sql = "SELECT * FROM pirates WHERE name = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, name);  // Indeks od 1!

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Pirate(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("rank")
                );
            }
        }
    }
    return null;
}
```
</details>

---

## ✍️ Ćwiczenie 4.2: Bezpieczny INSERT

**Cel:** Napisać bezpieczną metodę dodawania pirata

```java
public long addPirateSafely(String name, String rank, int age) throws SQLException {
    // TODO: Użyj PreparedStatement z RETURN_GENERATED_KEYS
    String sql = "INSERT INTO pirates (name, rank, age) VALUES (?, ?, ?)";

    // TODO 1: Utwórz PreparedStatement z flagą RETURN_GENERATED_KEYS

    // TODO 2: Ustaw wszystkie 3 parametry

    // TODO 3: Wykonaj executeUpdate()

    // TODO 4: Pobierz wygenerowane ID
    ResultSet generatedKeys = pstmt.getGeneratedKeys();
    if (generatedKeys.next()) {
        return generatedKeys.getLong(1);
    }
    return -1;
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public long addPirateSafely(String name, String rank, int age) throws SQLException {
    String sql = "INSERT INTO pirates (name, rank, age) VALUES (?, ?, ?)";

    try (PreparedStatement pstmt = connection.prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS)) {

        pstmt.setString(1, name);
        pstmt.setString(2, rank);
        pstmt.setInt(3, age);

        pstmt.executeUpdate();

        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
        }
    }
    return -1;
}
```
</details>

---

## ✍️ Ćwiczenie 4.3: Batch Insert

**Cel:** Wstawić wielu piratów jednym zapytaniem (wydajność!)

```java
public void addPiratesBatch(List<Pirate> pirates) throws SQLException {
    String sql = "INSERT INTO pirates (name, rank) VALUES (?, ?)";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        for (Pirate pirate : pirates) {
            // TODO 1: Ustaw parametry dla każdego pirata

            // TODO 2: Dodaj do batcha używając addBatch()

        }
        // TODO 3: Wykonaj wszystkie naraz używając executeBatch()
        int[] results = _______________
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public void addPiratesBatch(List<Pirate> pirates) throws SQLException {
    String sql = "INSERT INTO pirates (name, rank) VALUES (?, ?)";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        for (Pirate pirate : pirates) {
            pstmt.setString(1, pirate.getName());
            pstmt.setString(2, pirate.getRank());
            pstmt.addBatch();
        }
        int[] results = pstmt.executeBatch();
        System.out.println("Dodano " + results.length + " piratów!");
    }
}
```
</details>

---

## ✅ Checkpoint Bloku 4

- [ ] Rozumiem czym jest SQL Injection i dlaczego jest niebezpieczny
- [ ] Zawsze używam PreparedStatement zamiast Statement dla danych użytkownika
- [ ] Umiem używać parametrów `?` i metod setString(), setInt(), setLong()
- [ ] Wiem jak pobrać wygenerowane ID po INSERT




# 🍽️ PRZERWA OBIADOWA (12:15-13:15)

---

# 🏗️ BLOK 5: JDBC - Wzorzec DAO (13:15-14:00)

## 📚 Notatki teoretyczne

### Co to jest DAO (Data Access Object)?

**Definicja:** Wzorzec projektowy oddzielający logikę dostępu do danych od logiki biznesowej.

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Serwis        │────▶│      DAO        │────▶│    Baza danych  │
│   (logika)      │     │   (dostęp)      │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

**Zalety DAO:**
1. _______________ - łatwa wymiana implementacji
2. _______________ - można testować z mockami
3. _______________ - czysty podział odpowiedzialności

**Moje notatki:**
```
_________________________________________________________________
```

### Struktura DAO

```java
// 1. Interfejs (kontrakt)
public interface PirateDao {
    Pirate findById(Long id);
    List<Pirate> findAll();
    void save(Pirate pirate);
    void update(Pirate pirate);
    void delete(Long id);
}

// 2. Implementacja JDBC
public class JdbcPirateDao implements PirateDao {
    // Szczegóły implementacji...
}
```

---

## 👀 Demo 5.1: Gotowe DAO w projekcie

**Lokalizacja:** `day1-databases/m03-jdbc-crud/src/main/java/.../dao/`

**Obserwuj:**
1. Jak zdefiniowany jest interfejs `PirateDao`?
2. Jak wygląda implementacja `JdbcPirateDao`?
3. Jak DAO zarządza Connection?

**Moje notatki:**
```
_________________________________________________________________
_________________________________________________________________
```

---

## ✍️ Ćwiczenie 5.1: Implementacja findById

**Cel:** Zaimplementować metodę wyszukiwania pirata po ID

```java
public class JdbcPirateDao implements PirateDao {

    private final Connection connection;

    public JdbcPirateDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Pirate> findById(Long id) {
        // TODO 1: Napisz SQL z parametrem ?
        String sql = _______________

        // TODO 2: Użyj PreparedStatement
        try (PreparedStatement pstmt = _______________) {

            // TODO 3: Ustaw parametr ID

            // TODO 4: Wykonaj zapytanie i zmapuj wynik na obiekt Pirate

        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy wyszukiwaniu pirata", e);
        }
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
@Override
public Optional<Pirate> findById(Long id) {
    String sql = "SELECT id, name, rank, age FROM pirates WHERE id = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, id);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Pirate pirate = new Pirate(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("rank"),
                    rs.getInt("age")
                );
                return Optional.of(pirate);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Błąd przy wyszukiwaniu pirata", e);
    }
    return Optional.empty();
}
```
</details>

---

## ✍️ Ćwiczenie 5.2: Implementacja findAll

**Cel:** Pobrać listę wszystkich piratów

```java
@Override
public List<Pirate> findAll() {
    List<Pirate> pirates = new ArrayList<>();
    String sql = "SELECT id, name, rank, age FROM pirates";

    // TODO: Wykonaj zapytanie i zmapuj wszystkie wyniki na listę

    return pirates;
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
@Override
public List<Pirate> findAll() {
    List<Pirate> pirates = new ArrayList<>();
    String sql = "SELECT id, name, rank, age FROM pirates";

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            pirates.add(new Pirate(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("rank"),
                rs.getInt("age")
            ));
        }
    } catch (SQLException e) {
        throw new RuntimeException("Błąd przy pobieraniu piratów", e);
    }
    return pirates;
}
```
</details>

---

## ✍️ Ćwiczenie 5.3: Metoda pomocnicza mapowania

**Cel:** Wyodrębnić powtarzający się kod mapowania do osobnej metody

```java
// TODO: Utwórz prywatną metodę mapującą ResultSet na Pirate
private Pirate mapRowToPirate(ResultSet rs) throws SQLException {
    // TODO: Zwróć nowy obiekt Pirate z danymi z ResultSet
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
private Pirate mapRowToPirate(ResultSet rs) throws SQLException {
    return new Pirate(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getString("rank"),
        rs.getInt("age")
    );
}

// Użycie w findById:
if (rs.next()) {
    return Optional.of(mapRowToPirate(rs));
}

// Użycie w findAll:
while (rs.next()) {
    pirates.add(mapRowToPirate(rs));
}
```
</details>

---

## ✅ Checkpoint Bloku 5

- [ ] Rozumiem wzorzec DAO i jego zalety
- [ ] Potrafię zdefiniować interfejs DAO
- [ ] Umiem zaimplementować metody CRUD w JDBC
- [ ] Stosuję metodę pomocniczą do mapowania ResultSet




---

# ☕ PRZERWA (14:00-14:15)

---

# 🗃️ BLOK 6: JPA - Entity i EntityManager (14:15-15:00)

## 📚 Notatki teoretyczne

### Co to jest JPA?

**JPA (Java Persistence API)** - standard Java do mapowania obiektowo-relacyjnego (ORM).

| JDBC | JPA |
|------|-----|
| Piszemy SQL ręcznie | SQL generowany automatycznie |
| Mapowanie ResultSet → Object | Automatyczne mapowanie |
| Zarządzanie Connection | EntityManager zarządza |
| Niskopoziomowe | Wysokopoziomowe |

**Moje notatki - różnice:**
```
_________________________________________________________________
```

### Podstawowe adnotacje JPA

```java
@Entity                           // Oznacza klasę jako encję JPA
@Table(name = "pirates")          // Nazwa tabeli w bazie
public class Pirate {

    @Id                           // Klucz główny
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
    private Long id;

    @Column(name = "pirate_name") // Nazwa kolumny (opcjonalne)
    private String name;

    @Column(nullable = false)     // Kolumna wymagana
    private String rank;
}
```

**Uzupełnij:**
- `@Entity` - oznacza, że klasa to _______________
- `@Id` - wskazuje pole będące _______________
- `@GeneratedValue` - wartość jest _______________
- `@Column` - konfiguracja _______________

---

## 👀 Demo 6.1: Encja Pirate w projekcie

**Lokalizacja:** `day1-databases/m04-jpa-intro/src/main/java/.../entity/Pirate.java`

**Obserwuj:**
1. Jakie adnotacje są użyte?
2. Jak wygląda konstruktor bezargumentowy (wymagany przez JPA)?
3. Jakie są gettery i settery?

**Moje notatki:**
```
_________________________________________________________________
_________________________________________________________________
```

---

## ✍️ Ćwiczenie 6.1: Tworzenie encji Ship

**Cel:** Stworzyć encję reprezentującą statek piracki

```java
// TODO: Dodaj odpowiednie adnotacje JPA
public class Ship {

    // TODO: Oznacz jako klucz główny z auto-generacją
    private Long id;

    // TODO: Kolumna wymagana (nullable = false)
    private String name;

    private int cannons;  // liczba dział

    private int crewCapacity;  // maksymalna załoga

    // TODO: Dodaj bezargumentowy konstruktor (wymagany przez JPA)

    // TODO: Dodaj konstruktor z wszystkimi polami

    // TODO: Dodaj gettery i settery
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
@Entity
@Table(name = "ships")
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int cannons;

    @Column(name = "crew_capacity")
    private int crewCapacity;

    // Konstruktor bezargumentowy - WYMAGANY przez JPA!
    public Ship() {}

    public Ship(String name, int cannons, int crewCapacity) {
        this.name = name;
        this.cannons = cannons;
        this.crewCapacity = crewCapacity;
    }

    // Gettery i settery...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    // ... pozostałe
}
```
</details>

---

## 📚 EntityManager - operacje CRUD

```java
// Konfiguracja
EntityManagerFactory emf = Persistence.createEntityManagerFactory("pirates-pu");
EntityManager em = emf.createEntityManager();

// CREATE (persist)
em.getTransaction().begin();
em.persist(new Pirate("Jack Sparrow", "Captain"));
em.getTransaction().commit();

// READ (find)
Pirate pirate = em.find(Pirate.class, 1L);

// UPDATE (encja zarządzana - automatyczne)
em.getTransaction().begin();
pirate.setRank("Admiral");  // Zmiana wykryta automatycznie!
em.getTransaction().commit();

// DELETE (remove)
em.getTransaction().begin();
em.remove(pirate);
em.getTransaction().commit();
```

**Moje notatki:**
```
_________________________________________________________________
```

---

## ✍️ Ćwiczenie 6.2: Operacje CRUD z EntityManager

**Cel:** Wykonać podstawowe operacje CRUD

```java
public class JpaCrudDemo {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pirates-pu");
        EntityManager em = emf.createEntityManager();

        // TODO 1: Dodaj nowego pirata (persist)
        em.getTransaction().begin();
        // _______________
        em.getTransaction().commit();

        // TODO 2: Znajdź pirata po ID
        Pirate found = _______________

        // TODO 3: Zaktualizuj rangę pirata
        em.getTransaction().begin();
        // _______________
        em.getTransaction().commit();

        // TODO 4: Usuń pirata
        em.getTransaction().begin();
        // _______________
        em.getTransaction().commit();

        em.close();
        emf.close();
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
// TODO 1: Dodaj nowego pirata
em.getTransaction().begin();
Pirate newPirate = new Pirate("Barbossa", "Captain", 55);
em.persist(newPirate);
em.getTransaction().commit();

// TODO 2: Znajdź pirata po ID
Pirate found = em.find(Pirate.class, newPirate.getId());
System.out.println("Znaleziono: " + found.getName());

// TODO 3: Zaktualizuj rangę
em.getTransaction().begin();
found.setRank("Admiral");  // Dirty checking - JPA wykryje zmianę!
em.getTransaction().commit();

// TODO 4: Usuń pirata
em.getTransaction().begin();
em.remove(found);
em.getTransaction().commit();
```
</details>

---

## ✅ Checkpoint Bloku 6

- [ ] Rozumiem różnicę między JDBC a JPA
- [ ] Znam podstawowe adnotacje: @Entity, @Id, @GeneratedValue, @Column
- [ ] Wiem, że encja musi mieć konstruktor bezargumentowy
- [ ] Umiem używać EntityManager do operacji CRUD




---

# 🔗 BLOK 7: JPA - Relacje i JPQL (15:00-15:45)

## 📚 Notatki teoretyczne

### Rodzaje relacji w JPA

| Relacja | Opis | Przykład piracki |
|---------|------|------------------|
| `@OneToOne` | 1:1 | Pirat ↔ Paszport |
| `@OneToMany` | 1:N | Kapitan → Załoga |
| `@ManyToOne` | N:1 | Piraci → Statek |
| `@ManyToMany` | N:N | Piraci ↔ Skarby |

**Moje notatki:**
```
_________________________________________________________________
```

### Adnotacje relacji

```java
@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "ship")  // Strona właściciela: Pirate
    private List<Pirate> crew = new ArrayList<>();
}

@Entity
public class Pirate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne                     // Strona właściciela relacji
    @JoinColumn(name = "ship_id") // Kolumna klucza obcego
    private Ship ship;
}
```

**Uzupełnij:**
- `mappedBy` wskazuje na _______________
- `@JoinColumn` definiuje _______________
- Strona właściciela to ta, która ma _______________

---

## 👀 Demo 7.1: Relacje w projekcie

**Lokalizacja:** `day1-databases/m05-jpa-relations/src/main/java/.../entity/`

**Obserwuj:**
1. Jak zdefiniowana jest relacja Ship ↔ Pirate?
2. Która strona jest właścicielem relacji?
3. Jak wygląda mappedBy?

**Moje notatki:**
```
_________________________________________________________________
_________________________________________________________________
```

---

## ✍️ Ćwiczenie 7.1: Definiowanie relacji ManyToOne

**Cel:** Dodać relację Pirate → Ship

```java
@Entity
public class Pirate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // TODO: Dodaj relację ManyToOne do Ship
    // TODO: Dodaj @JoinColumn z nazwą kolumny "ship_id"

}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
@Entity
public class Pirate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)  // LAZY - dobre praktyki!
    @JoinColumn(name = "ship_id")
    private Ship ship;

    // Gettery i settery...
    public Ship getShip() { return ship; }
    public void setShip(Ship ship) { this.ship = ship; }
}
```
</details>

---

## 📚 JPQL - Java Persistence Query Language

```java
// JPQL używa nazw klas i pól Java, NIE nazw tabel SQL!

// Proste zapytanie
String jpql = "SELECT p FROM Pirate p";
List<Pirate> pirates = em.createQuery(jpql, Pirate.class).getResultList();

// Z parametrem
String jpql = "SELECT p FROM Pirate p WHERE p.rank = :rank";
List<Pirate> captains = em.createQuery(jpql, Pirate.class)
    .setParameter("rank", "Captain")
    .getResultList();

// JOIN z relacją
String jpql = "SELECT p FROM Pirate p JOIN p.ship s WHERE s.name = :shipName";
```

**Różnice SQL vs JPQL:**
- SQL: `SELECT * FROM pirates WHERE rank = 'Captain'`
- JPQL: `SELECT p FROM Pirate p WHERE p.rank = 'Captain'`

**Moje notatki:**
```
_________________________________________________________________
```

---

## ✍️ Ćwiczenie 7.2: Zapytania JPQL

**Cel:** Napisać zapytania JPQL

```java
// TODO 1: Znajdź wszystkich piratów starszych niż 30 lat
String jpql1 = "SELECT p FROM Pirate p WHERE _______________";

// TODO 2: Znajdź piratów posortowanych po imieniu
String jpql2 = "SELECT p FROM Pirate p _______________";

// TODO 3: Znajdź piratów na statku "Black Pearl"
String jpql3 = "SELECT p FROM Pirate p JOIN p.ship s WHERE _______________";

// TODO 4: Policz piratów w randze "Captain"
String jpql4 = "SELECT COUNT(p) FROM Pirate p WHERE _______________";
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
// TODO 1: Piraci starsi niż 30
String jpql1 = "SELECT p FROM Pirate p WHERE p.age > 30";

// TODO 2: Sortowanie po imieniu
String jpql2 = "SELECT p FROM Pirate p ORDER BY p.name ASC";

// TODO 3: Piraci na Black Pearl
String jpql3 = "SELECT p FROM Pirate p JOIN p.ship s WHERE s.name = 'Black Pearl'";

// TODO 4: Liczba kapitanów
String jpql4 = "SELECT COUNT(p) FROM Pirate p WHERE p.rank = 'Captain'";
```
</details>

---

## ✅ Checkpoint Bloku 7

- [ ] Znam rodzaje relacji: @OneToOne, @OneToMany, @ManyToOne, @ManyToMany
- [ ] Rozumiem pojęcie "strony właściciela" relacji
- [ ] Wiem, że JPQL używa nazw klas Java, nie tabel SQL
- [ ] Potrafię napisać podstawowe zapytania JPQL





---

# 🌱 BLOK 8: Spring Data JPA (15:45-16:30)

## 📚 Notatki teoretyczne

### Co to jest Spring Data JPA?

Spring Data JPA to **warstwa abstrakcji nad JPA**, która automatyzuje tworzenie repozytoriów.

| Czyste JPA | Spring Data JPA |
|------------|-----------------|
| Piszemy EntityManager ręcznie | Repository generowane automatycznie |
| Własne metody CRUD | Gotowe metody CRUD |
| JPQL pisane ręcznie | Query methods z nazw metod |
| Więcej kodu | Mniej kodu |

**Moje notatki:**
```
_________________________________________________________________
```

### Tworzenie Repository

```java
// Wystarczy interfejs - Spring generuje implementację!
public interface PirateRepository extends JpaRepository<Pirate, Long> {

    // Metody CRUD są gotowe: save(), findById(), findAll(), delete()...

    // Query methods - Spring generuje zapytanie z nazwy metody!
    List<Pirate> findByRank(String rank);

    List<Pirate> findByAgeGreaterThan(int age);

    List<Pirate> findByNameContainingIgnoreCase(String name);

    // Własne JPQL gdy potrzebujemy
    @Query("SELECT p FROM Pirate p WHERE p.ship.name = :shipName")
    List<Pirate> findPiratesOnShip(@Param("shipName") String shipName);
}
```

**Uzupełnij:**
- `JpaRepository<Pirate, Long>` - pierwszy typ to _______________, drugi to _______________
- Query methods działają na zasadzie _______________
- Adnotacja `@Query` pozwala na _______________

---

## 👀 Demo 8.1: Spring Data Repository

**Lokalizacja:** `day1-databases/m06-spring-data/src/main/java/.../repository/`

**Obserwuj:**
1. Jak wygląda interfejs PirateRepository?
2. Jakie metody są dostępne bez pisania kodu?
3. Jak działa query method?

**Moje notatki:**
```
_________________________________________________________________
_________________________________________________________________
```

---

## ✍️ Ćwiczenie 8.1: Tworzenie Repository

**Cel:** Stworzyć repository dla statków

```java
// TODO: Uzupełnij interfejs
public interface ShipRepository extends _______________ {

    // TODO: Metoda do wyszukiwania statku po nazwie
    Ship _______________

    // TODO: Metoda do wyszukiwania statków z liczbą dział większą niż X
    List<Ship> _______________

    // TODO: Metoda do wyszukiwania statków po fragmencie nazwy
    List<Ship> _______________
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public interface ShipRepository extends JpaRepository<Ship, Long> {

    // Wyszukiwanie po nazwie
    Ship findByName(String name);

    // Statki z większą liczbą dział
    List<Ship> findByCannonsGreaterThan(int cannons);

    // Wyszukiwanie po fragmencie nazwy
    List<Ship> findByNameContainingIgnoreCase(String namePart);
}
```
</details>

---

## 📚 Użycie Repository w serwisie

```java
@Service
public class PirateService {

    private final PirateRepository pirateRepository;

    // Constructor injection - dobra praktyka!
    public PirateService(PirateRepository pirateRepository) {
        this.pirateRepository = pirateRepository;
    }

    public List<Pirate> getAllCaptains() {
        return pirateRepository.findByRank("Captain");
    }

    public Pirate hirePirate(Pirate pirate) {
        return pirateRepository.save(pirate);  // save() obsługuje CREATE i UPDATE
    }

    public void firePirate(Long id) {
        pirateRepository.deleteById(id);
    }
}
```

**Moje notatki:**
```
_________________________________________________________________
```

---

## ✍️ Ćwiczenie 8.2: Serwis z Repository

**Cel:** Stworzyć serwis używający repository

```java
@Service
public class ShipService {

    // TODO: Wstrzyknij ShipRepository przez konstruktor

    // TODO: Metoda zwracająca wszystkie statki
    public List<Ship> getAllShips() {
        // _______________
    }

    // TODO: Metoda dodająca nowy statek
    public Ship addShip(Ship ship) {
        // _______________
    }

    // TODO: Metoda wyszukująca statek po ID
    public Optional<Ship> findShip(Long id) {
        // _______________
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
@Service
public class ShipService {

    private final ShipRepository shipRepository;

    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public List<Ship> getAllShips() {
        return shipRepository.findAll();
    }

    public Ship addShip(Ship ship) {
        return shipRepository.save(ship);
    }

    public Optional<Ship> findShip(Long id) {
        return shipRepository.findById(id);
    }
}
```
</details>

---

## ✅ Checkpoint Bloku 8

- [ ] Rozumiem, czym Spring Data JPA różni się od czystego JPA
- [ ] Umiem stworzyć interfejs Repository
- [ ] Znam zasadę działania query methods
- [ ] Wiem, jak użyć @Query do własnych zapytań JPQL





---

# 📝 NOTATKI KOŃCOWE

## Moje pytania do trenera

```
1. ________________________________________________________________

2. ________________________________________________________________

3. ________________________________________________________________
```

## Rzeczy do zapamiętania

```
_________________________________________________________________
_________________________________________________________________
_________________________________________________________________
_________________________________________________________________
```

## Tematy do samodzielnego zgłębienia

- [ ] _______________________________________________
- [ ] _______________________________________________
- [ ] _______________________________________________

---

# 🏁 PODSUMOWANIE DNIA 1

## ✅ Finalna lista kontrolna

### SQL (Bloki 1-2)
- [ ] Potrafię napisać zapytanie SELECT z warunkami WHERE
- [ ] Rozumiem JOIN i potrafię łączyć tabele
- [ ] Umiem używać GROUP BY z funkcjami agregującymi
- [ ] Potrafię napisać podzapytanie (subquery)

### JDBC (Bloki 3-5)
- [ ] Rozumiem jak działa Connection, Statement, ResultSet
- [ ] Zawsze używam try-with-resources dla zasobów
- [ ] Wiem dlaczego PreparedStatement chroni przed SQL Injection
- [ ] Potrafię zaimplementować wzorzec DAO

### JPA (Bloki 6-8)
- [ ] Umiem mapować klasę Java na tabelę za pomocą @Entity
- [ ] Rozumiem adnotacje @Id, @GeneratedValue, @Column
- [ ] Znam rodzaje relacji i umiem je zdefiniować
- [ ] Potrafię napisać zapytanie JPQL
- [ ] Rozumiem jak Spring Data JPA upraszcza pracę z bazą danych

---

## 🎯 Moje główne wnioski z Dnia 1

**Co było dla mnie nowe:**
```
_________________________________________________________________
_________________________________________________________________
```

**Co chcę przećwiczyć:**
```
_________________________________________________________________
_________________________________________________________________
```

**Jak to wykorzystam w mojej pracy:**
```
_________________________________________________________________
_________________________________________________________________
```

---

## 🏴‍☠️ Certyfikat Pirata - Dzień 1

Po ukończeniu wszystkich ćwiczeń i zaznaczeniu checkpointów, możesz uznać się za:

```
╔═══════════════════════════════════════════════════════════════╗
║                                                               ║
║              🏴‍☠️ PIRAT BAZY DANYCH 🏴‍☠️                        ║
║                                                               ║
║    Imię: _____________________________________                ║
║                                                               ║
║    Data ukończenia: ________________________                  ║
║                                                               ║
║    Specjalizacja: SQL • JDBC • JPA • Spring Data              ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
```

---

**Gratulacje! Ukończyłeś Dzień 1 szkolenia "Piraci z Karaibów - Bazy Danych"!** 🎉

Do zobaczenia na Dniu 2, gdzie będziemy zajmować się jakością kodu, wzorcami projektowymi i narzędziami!

---
*Dokument wygenerowany dla szkolenia "Java Training Core - Piraci z Karaibów"*
