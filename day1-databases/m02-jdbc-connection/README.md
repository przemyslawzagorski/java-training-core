# Moduł 02: JDBC Connection

## 🎯 Cel modułu
Nauczenie podstaw JDBC (Java Database Connectivity) - jak łączyć się z bazą danych, wykonywać zapytania SQL z poziomu Javy oraz jak robić to bezpiecznie. Kursant pozna różnicę między starym stylem zarządzania zasobami a nowoczesnym try-with-resources oraz zrozumie zagrożenie SQL Injection.

## 🔑 Kluczowe pojęcia

| Pojęcie | Opis |
|---------|------|
| **JDBC** | Java Database Connectivity - API do komunikacji z bazami danych |
| **DriverManager** | Zarządza sterownikami baz danych i tworzy połączenia (⚠️ nie dla produkcji!) |
| **Connection** | Reprezentuje połączenie z bazą danych |
| **Statement** | Wykonuje statyczne zapytania SQL (⚠️ podatne na SQL Injection) |
| **PreparedStatement** | Wykonuje parametryzowane zapytania (✅ bezpieczne) |
| **ResultSet** | Przechowuje wyniki zapytania SELECT |
| **try-with-resources** | Automatyczne zamykanie zasobów (Java 7+) |
| **SQL Injection** | Atak polegający na wstrzyknięciu złośliwego kodu SQL |
| **SQLException** | Wyjątek rzucany przy błędach SQL |
| **Connection Pool** | 🆕 Pula gotowych połączeń (wydajność produkcyjna!) |
| **HikariCP** | 🆕 Najszybszy connection pool - standard w Spring Boot |

## 📁 Zawartość

| Klasa | Opis |
|-------|------|
| `ConnectionDemo.java` | Demonstracja połączenia JDBC - stary styl vs try-with-resources |
| `ConnectionPoolDemo.java` | 🆕 **HikariCP** - Connection Pool (produkcyjny standard!) |
| `SqlInjectionDemo.java` | Pokazuje zagrożenie SQL Injection i jak się przed nim bronić |
| `JdbcExercises.java` | Ćwiczenia dla kursantów (szkielety metod z TODO) |
| `JdbcExercisesSolutions.java` | Pełne rozwiązania wszystkich ćwiczeń |

## 🚀 Jak uruchomić

### Uruchomienie demonstracji połączenia
```bash
# Z poziomu głównego katalogu projektu
mvn exec:java -pl day1-databases/m02-jdbc-connection -Dexec.mainClass="pl.przemekzagorski.training.jdbc.ConnectionDemo"
```

Lub uruchom klasę `ConnectionDemo.main()` bezpośrednio w IntelliJ IDEA.

### 🆕 Uruchomienie demonstracji Connection Pool (HikariCP)
```bash
mvn exec:java -pl day1-databases/m02-jdbc-connection -Dexec.mainClass="pl.przemekzagorski.training.jdbc.ConnectionPoolDemo"
```

### Uruchomienie demonstracji SQL Injection
```bash
mvn exec:java -pl day1-databases/m02-jdbc-connection -Dexec.mainClass="pl.przemekzagorski.training.jdbc.SqlInjectionDemo"
```

### Uruchomienie ćwiczeń
```bash
# Szkielety ćwiczeń (do wypełnienia przez kursanta)
mvn exec:java -pl day1-databases/m02-jdbc-connection -Dexec.mainClass="pl.przemekzagorski.training.jdbc.JdbcExercises"

# Rozwiązania ćwiczeń
mvn exec:java -pl day1-databases/m02-jdbc-connection -Dexec.mainClass="pl.przemekzagorski.training.jdbc.JdbcExercisesSolutions"
```

## 🛡️ Bezpieczeństwo - SQL Injection

### ❌ NIEBEZPIECZNE - Statement z konkatenacją
```java
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(sql);
```

**Atak:** `username = "' OR '1'='1"` → zwróci wszystkich użytkowników!

### ✅ BEZPIECZNE - PreparedStatement z parametrami
```java
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, username);  // Parametr jest automatycznie escapowany
ResultSet rs = stmt.executeQuery();
```

**Zasada:** **ZAWSZE używaj PreparedStatement dla zapytań z danymi od użytkownika!**

## 📊 Stary styl vs Try-with-resources

### ❌ Stary styl (Java 6 i wcześniej)
```java
Connection conn = null;
Statement stmt = null;
ResultSet rs = null;
try {
    conn = DriverManager.getConnection(url, user, password);
    stmt = conn.createStatement();
    rs = stmt.executeQuery("SELECT * FROM pirates");
    // ... przetwarzanie
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    // Ręczne zamykanie - łatwo o błąd!
    if (rs != null) rs.close();
    if (stmt != null) stmt.close();
    if (conn != null) conn.close();
}
```

### ✅ Try-with-resources (Java 7+)
```java
try (Connection conn = DriverManager.getConnection(url, user, password);
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("SELECT * FROM pirates")) {

    // ... przetwarzanie
    // Zasoby zamykane AUTOMATYCZNIE w odwrotnej kolejności!

} catch (SQLException e) {
    e.printStackTrace();
}
```

**Zalety:**
- ✅ Automatyczne zamykanie zasobów
- ✅ Zamykanie w odwrotnej kolejności (rs → stmt → conn)
- ✅ Mniej kodu, mniej błędów
- ✅ Działa nawet gdy wystąpi wyjątek

## 🔄 Transakcje i ACID

### ⚠️ WAŻNE: Auto-commit w JDBC

**Domyślnie w JDBC każda operacja SQL jest commitowana NATYCHMIAST:**

```java
// Auto-commit = TRUE (domyślnie)
stmt.executeUpdate("UPDATE accounts SET balance = balance - 200 WHERE id = 1");
// ← COMMIT automatyczny TUTAJ! Dane już w bazie!

stmt.executeUpdate("UPDATE accounts SET balance = balance + 200 WHERE id = 2");
// ← COMMIT automatyczny TUTAJ!

// Jeśli druga operacja się nie powiedzie, pierwsza JUŻ JEST w bazie!
// Pieniądze zniknęły! 💸
```

**To NIE jest jak w metodzie Java** - commit nie czeka na koniec metody!

### 🎯 Kiedy używać ręcznych transakcji?

| Sytuacja | Auto-commit | Ręczna transakcja |
|----------|-------------|-------------------|
| **Pojedyncza operacja** (np. INSERT jednego rekordu) | ✅ OK | Niepotrzebne |
| **Wiele powiązanych operacji** (transfer, zamówienie) | ❌ NIEBEZPIECZNE | ✅ WYMAGANE |
| **Operacje finansowe** | ❌ NIGDY | ✅ ZAWSZE |
| **Operacje wymagające spójności danych** | ❌ NIE | ✅ TAK |

### 💼 Przykłady biznesowe wymagające transakcji

**1. Transfer pieniędzy:**
```java
conn.setAutoCommit(false);
try {
    stmt.executeUpdate("UPDATE accounts SET balance = balance - 200 WHERE id = 1");
    stmt.executeUpdate("UPDATE accounts SET balance = balance + 200 WHERE id = 2");
    conn.commit(); // ✅ Obie operacje razem
} catch (Exception e) {
    conn.rollback(); // ❌ Cofnij wszystko
}
```

**2. Zamówienie w sklepie:**
- Utwórz zamówienie
- Dodaj produkty
- Zmniejsz stan magazynowy
- Pobierz płatność
→ **Wszystko albo nic!**

**3. Rezerwacja biletów:**
- Zarezerwuj miejsce
- Utwórz rezerwację
- Pobierz płatność
- Wyślij potwierdzenie
→ **Atomowość!**

### 🔐 Zasada ACID

Transakcje zapewniają właściwości **ACID**:

| Właściwość | Opis | Przykład |
|------------|------|----------|
| **A**tomicity | Wszystkie operacje albo żadna | Transfer: odjęcie + dodanie = 1 transakcja |
| **C**onsistency | Dane zawsze spójne | Suma sald przed = suma sald po |
| **I**solation | Transakcje nie kolidują | Dwie osoby nie mogą zarezerwować tego samego miejsca |
| **D**urability | Po commit dane są trwałe | Po potwierdzeniu transfer nie zniknie |

**Bez ręcznych transakcji nie ma Atomicity przy wielu operacjach!**

## 🏊 Connection Pooling - HikariCP

### ⚠️ Problem z DriverManager

```java
// ❌ PRODUKCYJNY ANTYPATTERN!
for (int i = 0; i < 1000; i++) {
    try (Connection conn = DriverManager.getConnection(url, user, pass)) {
        // Każde wywołanie = nowe połączenie TCP (~50-100ms!)
    }
}
// 1000 × 100ms = 100 sekund tylko na łączenie!
```

**Problemy:**
- Nawiązanie połączenia TCP to ~50-100ms
- Baza danych ma limit jednoczesnych połączeń
- 1000 requestów/s = 1000 połączeń = katastrofa wydajnościowa

### ✅ Rozwiązanie - Connection Pool

**Connection Pool** to pula gotowych, otwartych połączeń:
- `getConnection()` = pobranie z puli (~0.1ms zamiast 100ms!)
- `close()` = zwrot do puli (nie zamyka fizycznie!)
- Automatyczne zarządzanie limitem połączeń

### 🚀 HikariCP - standard produkcyjny

```java
// Konfiguracja (zazwyczaj raz w aplikacji)
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:h2:mem:pirates");
config.setUsername("sa");
config.setPassword("");
config.setMaximumPoolSize(10);    // Max 10 połączeń w puli

HikariDataSource dataSource = new HikariDataSource(config);

// Użycie (bardzo szybkie!)
try (Connection conn = dataSource.getConnection()) {
    // Połączenie pobrane z puli - błyskawicznie!
}
// close() zwraca do puli, NIE zamyka połączenia!
```

**Dlaczego HikariCP?**
- ⚡ Najszybszy connection pool w Java
- 🌿 Domyślny w Spring Boot
- 🔧 Zero konfiguracji dla podstawowego użycia
- ❤️ Automatyczne health checking połączeń

### 📊 Porównanie wydajności

| Metoda | 100 połączeń | Koszt na połączenie |
|--------|--------------|---------------------|
| DriverManager | ~500ms | ~5ms |
| HikariCP | ~10ms | ~0.1ms |
| **Przyspieszenie** | **~50x szybciej!** | |

**Uruchom `ConnectionPoolDemo.java` aby zobaczyć porównanie na własne oczy!**

---

## 📚 Powiązane materiały

- **Przewodnik trenera:** `docs/01-TRAINER-GUIDE-DAY1.md` - sekcja "BLOK 3: JDBC BASICS"
- **Workbook kursanta:** `docs/03-STUDENT-WORKBOOK-DAY1.md`
- **Moduł poprzedni:** `m01-sql-basics` - podstawy SQL
- **Moduł następny:** `m03-crud-operations` - operacje CRUD w JDBC

## 💡 Wskazówki

- Zawsze używaj try-with-resources dla zasobów JDBC
- Nigdy nie konkatenuj danych użytkownika do zapytań SQL
- PreparedStatement to nie tylko bezpieczeństwo, ale też wydajność (query plan caching)
- SQLException zawiera szczegółowe informacje: `getErrorCode()`, `getSQLState()`, `getMessage()`
- Baza H2 in-memory (`jdbc:h2:mem:`) - dane znikają po zakończeniu programu

## 🎓 Ćwiczenia

Moduł zawiera 6 ćwiczeń stopniowanych trudnością:

1. **Ćwiczenie 1:** Podstawowe połączenie z bazą (try-with-resources)
2. **Ćwiczenie 2:** Wykonanie zapytania SELECT i iteracja po ResultSet
3. **Ćwiczenie 3:** Wstawienie danych używając Statement
4. **Ćwiczenie 4:** Przepisanie na PreparedStatement (bezpieczeństwo)
5. **Ćwiczenie 5:** Obsługa SQLException z wyświetleniem szczegółów
6. **Ćwiczenie 6:** Transakcje - commit i rollback (zaawansowane)

Zacznij od `JdbcExercises.java`, a gdy utkniesz - sprawdź `JdbcExercisesSolutions.java`.

---

🏴‍☠️ **Bezpiecznego kodowania!** 🛡️
