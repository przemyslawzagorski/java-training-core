# Moduł 02: Wzorce Projektowe (Design Patterns)

## 🎯 Cel modułu
Praktyczne poznanie najważniejszych wzorców projektowych - kiedy używać, jak implementować.

> **Kontekst:** Wszystkie przykłady osadzone są w tematyce pirackiej 🏴‍☠️

---

## 📁 Struktura modułu

```
m02-design-patterns/
├── src/main/java/pl/przemekzagorski/training/patterns/
│   ├── singleton/
│   │   └── SingletonDemo.java      # Jeden kapitan na statku
│   ├── factory/
│   │   └── FactoryDemo.java        # Stocznia produkuje różne statki
│   ├── builder/
│   │   └── BuilderDemo.java        # Budowanie złożonego statku
│   ├── strategy/
│   │   └── StrategyDemo.java       # Różne taktyki ataku
│   ├── decorator/
│   │   ├── Ship.java               # Interfejs statku
│   │   ├── BasicShip.java          # Podstawowy statek
│   │   ├── ShipDecorator.java      # Bazowy dekorator
│   │   ├── CannonUpgrade.java      # Ulepszenie: działa
│   │   ├── ArmorPlating.java       # Ulepszenie: pancerz
│   │   ├── FastSails.java          # Ulepszenie: szybkie żagle
│   │   └── DecoratorDemo.java      # Demo dekoratora
│   ├── observer/
│   │   ├── CrewObserver.java       # Interfejs obserwatora
│   │   ├── Captain.java            # Kapitan (subject)
│   │   ├── Gunner.java             # Kanonier (observer)
│   │   ├── Navigator.java          # Nawigator (observer)
│   │   ├── Cook.java               # Kucharz (observer)
│   │   └── ObserverDemo.java       # Demo observera
│   ├── cqrs/
│   │   ├── Command.java            # Marker interface dla komend
│   │   ├── Query.java              # Marker interface dla zapytań
│   │   ├── CreatePirateCommand.java
│   │   ├── UpdateBountyCommand.java
│   │   ├── GetPirateByIdQuery.java
│   │   ├── FindPiratesByRankQuery.java
│   │   ├── CommandHandler.java     # Handler dla komend
│   │   ├── QueryHandler.java       # Handler dla zapytań
│   │   ├── CommandBus.java         # Dispatcher komend
│   │   ├── QueryBus.java           # Dispatcher zapytań
│   │   ├── Pirate.java             # Model danych
│   │   ├── PirateDatabase.java     # In-memory storage
│   │   └── CQRSDemo.java           # Demo CQRS
│   ├── PatternExercises.java       # 📝 Ćwiczenia
│   └── PatternExercisesSolutions.java # ✅ Rozwiązania
└── src/test/java/
    └── PatternTest.java            # Testy jednostkowe
```

---

## 📚 Zawartość

| Folder | Wzorzec | Demo | Opis |
|--------|---------|------|------|
| `singleton/` | Singleton | `SingletonDemo` | Jeden kapitan na statku |
| `factory/` | Factory | `FactoryDemo` | Stocznia produkuje różne statki |
| `builder/` | Builder | `BuilderDemo` | Budowanie złożonego statku |
| `strategy/` | Strategy | `StrategyDemo` | Różne taktyki ataku |
| `decorator/` | Decorator | `DecoratorDemo` | Ulepszenia statku |
| `observer/` | Observer | `ObserverDemo` | Kapitan ogłasza, załoga reaguje |
| `cqrs/` | CQRS | `CQRSDemo` | Separacja Commands/Queries |

---

## 💡 Wzorce w pigułce

### 1. Singleton - jedna instancja

**Kiedy:** Potrzebujesz DOKŁADNIE jednej instancji (config, logger, cache).

```java
// Wariant ENUM (zalecany!)
public enum ShipConfig {
    INSTANCE;
    
    private int maxCrew = 100;
    public int getMaxCrew() { return maxCrew; }
}

// Użycie
ShipConfig config = ShipConfig.INSTANCE;
```

---

### 2. Factory - tworzenie obiektów

**Kiedy:** Nie wiesz z góry JAKI typ obiektu utworzyć (decyzja w runtime).

```java
public class ShipFactory {
    public static Ship create(String type) {
        return switch (type.toLowerCase()) {
            case "sloop" -> new Sloop();
            case "frigate" -> new Frigate();
            case "galleon" -> new Galleon();
            default -> throw new IllegalArgumentException("Unknown: " + type);
        };
    }
}

// Użycie
Ship ship = ShipFactory.create("frigate");
```

---

### 3. Builder - złożony konstruktor

**Kiedy:** Obiekt ma wiele OPCJONALNYCH parametrów.

```java
public class Ship {
    private final String name;        // wymagane
    private final int cannons;        // opcjonalne
    private final int crew;           // opcjonalne
    
    private Ship(Builder b) { ... }
    
    public static Builder builder(String name) {
        return new Builder(name);
    }
    
    public static class Builder {
        private final String name;
        private int cannons = 10;
        private int crew = 20;
        
        public Builder(String name) { this.name = name; }
        public Builder cannons(int c) { this.cannons = c; return this; }
        public Builder crew(int c) { this.crew = c; return this; }
        public Ship build() { return new Ship(this); }
    }
}

// Użycie (fluent API)
Ship ship = Ship.builder("Black Pearl")
    .cannons(32)
    .crew(100)
    .build();
```

---

### 4. Strategy - wymienne algorytmy

**Kiedy:** Masz WIELE sposobów na to samo zadanie.

```java
public interface AttackStrategy {
    void attack(Ship target);
}

public class BroadsideAttack implements AttackStrategy {
    public void attack(Ship target) {
        System.out.println("Firing all cannons at " + target.getName());
    }
}

public class BoardingAttack implements AttackStrategy {
    public void attack(Ship target) {
        System.out.println("Boarding " + target.getName());
    }
}

// Użycie - zmiana strategii w runtime!
ship.setAttackStrategy(new BroadsideAttack());
ship.attack(enemy);

ship.setAttackStrategy(new BoardingAttack());
ship.attack(enemy);
```

---

### 5. Decorator - dynamiczne rozszerzanie

**Kiedy:** Chcesz dodawać funkcje BEZ dziedziczenia.

```java
// Bazowy statek
Ship ship = new BasicShip("Sloop", 50, 10);

// Dodajemy ulepszenia (dekoratory)
ship = new CannonUpgrade(ship);     // +5 dział
ship = new ArmorPlating(ship);      // +30% HP
ship = new FastSails(ship);         // +20% szybkość

// Wszystkie ulepszenia są aktywne!
ship.printStats();
```

**Diagram:**
```
┌─────────────────────────────────────────────┐
│              FastSails                       │
│  ┌───────────────────────────────────────┐  │
│  │           ArmorPlating                 │  │
│  │  ┌─────────────────────────────────┐  │  │
│  │  │         CannonUpgrade           │  │  │
│  │  │  ┌───────────────────────────┐  │  │  │
│  │  │  │       BasicShip           │  │  │  │
│  │  │  │   (Sloop, 50HP, 10dmg)    │  │  │  │
│  │  │  └───────────────────────────┘  │  │  │
│  │  └─────────────────────────────────┘  │  │
│  └───────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
```

---

### 6. Observer - powiadamianie

**Kiedy:** Zmiana w jednym obiekcie wymaga reakcji WIELU innych.

```java
// Kapitan (Subject)
Captain captain = new Captain("Jack Sparrow");

// Załoga (Observers)
captain.addObserver(new Gunner("Mr. Gibbs"));
captain.addObserver(new Navigator("Mr. Cotton"));
captain.addObserver(new Cook("Cookie"));

// Kapitan wydaje rozkaz - wszyscy reagują!
captain.giveOrder("Battle stations!");

// Output:
// 🎯 Gunner Mr. Gibbs: Loading cannons!
// 🧭 Navigator Mr. Cotton: Adjusting course!
// 🍳 Cook Cookie: Securing the galley!
```

---

### 7. CQRS - separacja Command/Query

**Kiedy:** Chcesz WYRAŹNIE oddzielić operacje ZMIENIAJĄCE stan od ODCZYTUJĄCYCH.

**CQRS = Command Query Responsibility Segregation**

```java
// Command = ZMIENIA stan (void)
public record CreatePirateCommand(String name, String rank, int bounty) implements Command {}

// Query = ODCZYTUJE dane (zwraca wynik)
public record GetPirateByIdQuery(Long id) implements Query<Optional<Pirate>> {}

// Użycie
CommandBus commandBus = new CommandBus();
QueryBus queryBus = new QueryBus();

// Wykonaj komendę (zmiana stanu)
commandBus.execute(new CreatePirateCommand("Jack Sparrow", "Captain", 10000));

// Wykonaj zapytanie (odczyt)
Optional<Pirate> pirate = queryBus.execute(new GetPirateByIdQuery(1L));
```

**Diagram:**
```
┌─────────────────────────────────────────────────────────────┐
│                    CQRS PATTERN                             │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐         ┌──────────────────┐         │
│  │   COMMANDS       │         │    QUERIES       │         │
│  │  (Write Side)    │         │   (Read Side)    │         │
│  ├──────────────────┤         ├──────────────────┤         │
│  │ CreatePirate     │         │ GetPirateById    │         │
│  │ UpdateBounty     │         │ FindByRank       │         │
│  │ DeletePirate     │         │ CountPirates     │         │
│  └────────┬─────────┘         └────────┬─────────┘         │
│           │                            │                    │
│           ▼                            ▼                    │
│  ┌──────────────────┐         ┌──────────────────┐         │
│  │  CommandBus      │         │   QueryBus       │         │
│  └────────┬─────────┘         └────────┬─────────┘         │
│           │                            │                    │
│           ▼                            ▼                    │
│  ┌──────────────────┐         ┌──────────────────┐         │
│  │ CommandHandlers  │         │  QueryHandlers   │         │
│  └────────┬─────────┘         └────────┬─────────┘         │
│           │                            │                    │
│           └────────────┬───────────────┘                    │
│                        ▼                                     │
│                ┌──────────────┐                             │
│                │   DATABASE   │                             │
│                └──────────────┘                             │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Ewolucja: DAO → Repository → CQRS

### Porównanie wzorców dostępu do danych

| Aspekt | DAO (Dzień 1) | Repository (Spring Data) | CQRS |
|--------|---------------|--------------------------|------|
| **Cel** | Oddzielenie SQL od logiki | Zero boilerplate | Separacja read/write |
| **Kod** | Interface + implementacja | Tylko interface | Commands + Queries |
| **Operacje** | CRUD w jednym miejscu | CRUD w jednym miejscu | **Rozdzielone!** |
| **Złożoność** | ⭐⭐ | ⭐ | ⭐⭐⭐ |
| **Skalowalność** | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Kiedy używać** | Legacy, pełna kontrola | 99% projektów | Duże systemy, Event Sourcing |

### 1️⃣ DAO Pattern (Dzień 1, m03-jdbc-crud)

```java
// Interface
public interface PirateDao {
    Pirate save(Pirate pirate);           // CREATE
    Optional<Pirate> findById(Long id);   // READ
    void update(Pirate pirate);           // UPDATE
    void delete(Long id);                 // DELETE
}

// Implementacja JDBC
public class JdbcPirateDao implements PirateDao {
    // 50+ linii SQL, PreparedStatement, ResultSet...
}
```

**Zalety:**
- ✅ Oddzielenie SQL od logiki biznesowej
- ✅ Wymienność implementacji (JDBC → JPA → MongoDB)
- ✅ Łatwe testowanie (mock DAO)

**Wady:**
- ❌ Dużo boilerplate code
- ❌ Ręczne zarządzanie transakcjami
- ❌ CRUD w jednym miejscu (read + write razem)

---

### 2️⃣ Repository Pattern (Dzień 1, m09-spring-data)

```java
// To jest CAŁY KOD!
public interface PirateRepository extends JpaRepository<Pirate, Long> {
    List<Pirate> findByRank(String rank);
    List<Pirate> findByBountyGreaterThan(BigDecimal amount);
}

// Spring Data generuje implementację automatycznie!
```

**Zalety:**
- ✅ Zero boilerplate (Spring generuje kod)
- ✅ Query methods (SQL z nazwy metody)
- ✅ @Transactional (automatyczne zarządzanie)
- ✅ Produktywność ⭐⭐⭐⭐⭐

**Wady:**
- ❌ CRUD w jednym miejscu (read + write razem)
- ❌ Trudne skalowanie (jedna baza dla read i write)

---

### 3️⃣ CQRS Pattern (Dzień 2, m02-design-patterns)

```java
// COMMANDS (Write Side) - ZMIENIAJĄ stan
public record CreatePirateCommand(String name, String rank, int bounty) implements Command {}
public record UpdateBountyCommand(Long id, int newBounty) implements Command {}

// QUERIES (Read Side) - ODCZYTUJĄ dane
public record GetPirateByIdQuery(Long id) implements Query<Optional<Pirate>> {}
public record FindPiratesByRankQuery(String rank) implements Query<List<Pirate>> {}

// Użycie
commandBus.execute(new CreatePirateCommand("Jack", "Captain", 10000));  // Write
Optional<Pirate> pirate = queryBus.execute(new GetPirateByIdQuery(1L)); // Read
```

**Zalety:**
- ✅ **Wyraźna separacja** read/write
- ✅ **Niezależne skalowanie** (osobne bazy dla read i write)
- ✅ **Optymalizacja** (read model vs write model)
- ✅ **Event Sourcing** (łatwa integracja)
- ✅ **Testowanie** (łatwe mockowanie)

**Wady:**
- ❌ Większa złożoność
- ❌ Więcej kodu (Commands, Queries, Handlers, Buses)
- ❌ Eventual consistency (read model może być nieaktualny)

---

### 🎯 Kiedy używać którego wzorca?

```
┌─────────────────────────────────────────────────────────────┐
│                  WYBÓR WZORCA                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  DAO (Data Access Object)                                   │
│  ├─ Legacy projekty                                         │
│  ├─ Potrzebujesz pełnej kontroli nad SQL                    │
│  └─ Wymienność implementacji (JDBC ↔ JPA ↔ MongoDB)        │
│                                                              │
│  Repository (Spring Data)                                   │
│  ├─ 99% nowych projektów! ⭐⭐⭐⭐⭐                          │
│  ├─ Chcesz produktywności                                   │
│  ├─ Standardowe operacje CRUD                               │
│  └─ Jedna baza danych                                       │
│                                                              │
│  CQRS (Command Query Responsibility Segregation)            │
│  ├─ Duże systemy (miliony użytkowników)                    │
│  ├─ Różne wymagania dla read i write                       │
│  ├─ Event Sourcing                                          │
│  ├─ Niezależne skalowanie read/write                       │
│  └─ Mikroservices                                           │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**Przykład ewolucji:**
1. **Start projektu** → Repository (Spring Data) - szybki start
2. **Rośnie ruch** → Dodaj cache dla read (Redis)
3. **Miliony użytkowników** → CQRS (osobne bazy dla read/write)

---

## 📝 Ćwiczenia (30 min)

Otwórz `PatternExercises.java`:

| # | Ćwiczenie | Czas | Poziom |
|---|-----------|------|--------|
| 1 | Singleton - Konfiguracja aplikacji | 5 min | ⭐ |
| 2 | Factory - Bronie pirackie | 5 min | ⭐⭐ |
| 3 | Builder - Zamówienie w tawernie | 5 min | ⭐⭐ |
| 4 | Strategy - Nawigacja statku | 5 min | ⭐⭐ |
| 5 | Quiz - Rozpoznaj wzorzec | 5 min | ⭐ |
| 6 | CQRS - System zarządzania piratami | 5 min | ⭐⭐ |

**Rozwiązania:** `PatternExercisesSolutions.java`

---

## 🎯 Kiedy używać?

| Scenariusz | Wzorzec | Przykład z życia |
|------------|---------|------------------|
| Konfiguracja globalna | Singleton | `Logger.getInstance()` |
| Tworzenie różnych typów | Factory | `DocumentFactory.create("pdf")` |
| Wiele opcjonalnych pól | Builder | `HttpRequest.builder()` |
| Wymienne algorytmy | Strategy | `Collections.sort(list, comparator)` |
| Dodawanie funkcji dynamicznie | Decorator | `new BufferedReader(new FileReader())` |
| Powiadamianie o zmianach | Observer | `button.addActionListener()` |
| Separacja read/write | CQRS | Event Sourcing, Mikroservices |

---

## 🧪 Testy

```bash
cd m02-design-patterns
mvn test

# Testy sprawdzają:
# - Singleton (ta sama instancja)
# - Factory (tworzenie typów, wyjątki)
# - Builder (fluent API, walidacja)
# - Strategy (zmiana w runtime)
```

---

## 📊 Diagram: Wybór wzorca

```
┌─────────────────────────────────────────────────────────────┐
│                    WYBÓR WZORCA                             │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  "Potrzebuję tylko JEDNEJ instancji"                        │
│     └── SINGLETON                                            │
│                                                              │
│  "Nie wiem JAKIEGO typu obiekt utworzyć"                    │
│     └── FACTORY                                              │
│                                                              │
│  "Obiekt ma WIELE opcjonalnych parametrów"                  │
│     └── BUILDER                                              │
│                                                              │
│  "Mam KILKA ALGORYTMÓW do tego samego zadania"              │
│     └── STRATEGY                                             │
│                                                              │
│  "Chcę DODAWAĆ funkcje bez dziedziczenia"                   │
│     └── DECORATOR                                            │
│                                                              │
│  "Zmiana wymaga POWIADOMIENIA wielu obiektów"               │
│     └── OBSERVER                                             │
│                                                              │
│  "Chcę ODDZIELIĆ operacje read od write"                    │
│     └── CQRS                                                 │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔗 Powiązane moduły

- **Dzień 2, m03** - Refactoring często prowadzi do wzorców
- **Dzień 2, m04** - AI może sugerować wzorce

---

## 📖 Dodatkowe materiały

- [Refactoring.Guru - Design Patterns](https://refactoring.guru/design-patterns)
- [Head First Design Patterns](https://www.oreilly.com/library/view/head-first-design/0596007124/)
- [Gang of Four (GoF)](https://en.wikipedia.org/wiki/Design_Patterns)
