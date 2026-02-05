# 📓 STUDENT WORKBOOK - DZIEŃ 2: Kod Wysokiej Jakości

## 🏴‍☠️ Java Training Core - Piraci z Karaibów

**Imię i nazwisko kursanta:** ________________________________

**Data szkolenia:** ________________________________

---

## 📋 AGENDA DNIA 2

| Blok | Czas | Temat | Status |
|------|------|-------|--------|
| 1 | 9:00-9:45 | Hibernate - Lazy vs Eager, N+1 | ⬜ |
| 2 | 9:45-10:30 | Hibernate - Cache L1 i L2 | ⬜ |
| ☕ | 10:30-10:45 | Przerwa kawowa | |
| 3 | 10:45-11:30 | Wzorce projektowe - Singleton, Factory | ⬜ |
| 4 | 11:30-12:15 | Wzorce projektowe - Builder, Strategy | ⬜ |
| 🍕 | 12:15-13:15 | Przerwa obiadowa | |
| 5 | 13:15-14:00 | Code Smells i Refaktoring | ⬜ |
| ☕ | 14:00-14:15 | Przerwa kawowa | |
| 6 | 14:15-15:00 | Zasady SOLID | ⬜ |
| 7 | 15:00-15:45 | Narzędzia - SonarLint | ⬜ |
| 8 | 15:45-16:30 | AI dla Programisty + CI/CD | ⬜ |

---

## 📚 MODEL NAUKI

```
┌─────────────────────────────────────────────────────────────────┐
│  🎯 POKAZ → 🔄 POWTÓRZ → 💪 ĆWICZ                               │
│                                                                  │
│  1. Trener pokazuje (10 min) - obserwuj i notuj                │
│  2. Powtarzasz z trenerem (15 min) - kopiuj kod                │
│  3. Ćwiczysz samodzielnie (20 min) - rozwiąż zadanie           │
└─────────────────────────────────────────────────────────────────┘
```

---

# 📘 BLOK 1: Hibernate - Lazy vs Eager, N+1 (9:00-9:45)

## 📝 NOTATKI Z TEORII

### Lazy vs Eager Loading

**FetchType.LAZY:**
- Dane są ładowane _________________ (kiedy?)
- Domyślne dla: _________________ (które relacje?)
- Zaleta: _________________
- Wada: może spowodować _________________

**FetchType.EAGER:**
- Dane są ładowane _________________
- Domyślne dla: @ManyToOne, @OneToOne
- Zaleta: dane zawsze _________________
- Wada: może ładować _________________

### Problem N+1 SELECT

**Co to jest:**
Chcemy pobrać N obiektów z relacją. Wykonuje się:
- 1 zapytanie po główne obiekty
- N zapytań po _________________ dla każdego obiektu
- RAZEM: _______ zapytań!

**Jak wykryć:**
1. Włącz `hibernate.show_sql=_______`
2. Policz zapytania w logach
3. Wiele podobnych SELECT = _______

**Jak naprawić:**
```java
// Zamiast: SELECT a FROM Author a
// Użyj:    SELECT a FROM Author a _______ _______ a.books
```

---

## 👀 OBSERWACJE Z DEMO

### Demo: NPlusOneDemo.java

**Plik:** `day2-code-quality/m01-hibernate-advanced/.../NPlusOneDemo.java`

**Problem N+1:**
```
Pobieranie 5 autorów:
- Zapytanie 1: SELECT * FROM authors
- Zapytanie 2: SELECT * FROM books WHERE author_id = 1
- Zapytanie 3: SELECT * FROM books WHERE author_id = 2
- ...
- TOTAL: _____ zapytań
```

**Rozwiązanie JOIN FETCH:**
```
Pobieranie 5 autorów z książkami:
- Zapytanie 1: SELECT ... FROM authors a JOIN books b ON a.id = b.author_id
- TOTAL: _____ zapytanie(a)
```

**Moje obserwacje:**
_________________________________________________________________
_________________________________________________________________

---

## 🔄 POWTÓRZ Z TRENEREM

### Zadanie: Analiza encji Author i Book

Otwórz pliki:
- `Author.java`
- `Book.java`

Odpowiedz na pytania:

1. Jaki FetchType ma relacja `@OneToMany` w Author?
   Odpowiedź: _________________

2. Jaki FetchType ma relacja `@ManyToOne` w Book?
   Odpowiedź: _________________

3. Dlaczego w Author używamy LAZY dla kolekcji books?
   Odpowiedź: _________________

---

## 💪 ĆWICZ SAMODZIELNIE

### Ćwiczenie 1.1: Napisz zapytanie JOIN FETCH

Masz encję `Ship` z relacją do `CrewMember`:

```java
@Entity
public class Ship {
    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "ship", fetch = FetchType.LAZY)
    private List<CrewMember> crew;
}
```

**Zadanie:** Napisz zapytanie JPQL, które pobierze wszystkie statki RAZEM z załogą w JEDNYM zapytaniu:

```java
// TODO: Uzupełnij zapytanie
String jpql = "SELECT s FROM Ship s _______ _______ s.crew";
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
String jpql = "SELECT s FROM Ship s JOIN FETCH s.crew";

// Jeśli chcesz uniknąć duplikatów, dodaj DISTINCT:
String jpql = "SELECT DISTINCT s FROM Ship s JOIN FETCH s.crew";
```

</details>

---

### Ćwiczenie 1.2: Kiedy LAZY, kiedy EAGER?

Dla każdego scenariusza napisz LAZY lub EAGER:

| Scenariusz | FetchType |
|------------|-----------|
| Lista zamówień klienta (rzadko potrzebna) | _________ |
| Kategoria produktu (zawsze wyświetlana) | _________ |
| Historia logowań użytkownika | _________ |
| Adres wysyłki zamówienia | _________ |

<details>
<summary>💡 Rozwiązanie</summary>

| Scenariusz | FetchType |
|------------|-----------|
| Lista zamówień klienta (rzadko potrzebna) | **LAZY** |
| Kategoria produktu (zawsze wyświetlana) | **EAGER** |
| Historia logowań użytkownika | **LAZY** |
| Adres wysyłki zamówienia | **EAGER** |

</details>

---

## ✅ CHECKPOINT 1

- [ ] Rozumiem różnicę między LAZY a EAGER
- [ ] Potrafię zidentyfikować problem N+1 w logach
- [ ] Umiem użyć JOIN FETCH do rozwiązania N+1
- [ ] Wiem kiedy używać LAZY, a kiedy EAGER

---

# 📘 BLOK 2: Hibernate - Cache L1 i L2 (9:45-10:30)

## 📝 NOTATKI Z TEORII

### Cache L1 (Session Cache)

**Zakres:** _________________ (Session/SessionFactory?)
**Czas życia:** od otwarcia do _________________ sesji
**Włączony:** _________________ (zawsze/opcjonalnie?)

**Jak działa:**
1. Pobierasz obiekt → trafia do cache L1
2. Pobierasz TEN SAM obiekt ponownie → Hibernate bierze z _____________
3. Zamykasz sesję → cache L1 jest _____________

**Kluczowe metody:**
- `session.clear()` - _________________ całą sesję
- `session.evict(obj)` - usuwa _________________ obiekt z cache

### Cache L2 (Second Level Cache)

**Zakres:** _________________ (Session/SessionFactory?)
**Czas życia:** cały czas działania _________________
**Włączony:** wymaga _________________ konfiguracji

**Popularne implementacje:**
- EhCache
- Hazelcast
- Redis

---

## 👀 OBSERWACJE Z DEMO

### Demo: CacheDemo.java

**Plik:** `day2-code-quality/m01-hibernate-advanced/.../CacheDemo.java`

**Scenariusz 1 - Ta sama sesja:**
```
session.find(Author, 1)  → SQL: SELECT... (idzie do bazy)
session.find(Author, 1)  → SQL: ________ (cache!)
```
Wniosek: W tej samej sesji obiekt jest pobierany raz.

**Scenariusz 2 - Po session.clear():**
```
session.find(Author, 1)  → SQL: SELECT...
session.clear()          → Cache L1 wyczyszczony
session.find(Author, 1)  → SQL: ________ (musi iść do bazy!)
```

**Scenariusz 3 - Nowa sesja:**
```
session1.find(Author, 1) → SQL: SELECT...
session1.close()
session2.find(Author, 1) → SQL: ________ (nowa sesja = nowy cache L1)
```

**Moje obserwacje:**
_________________________________________________________________
_________________________________________________________________

---

## 🔄 POWTÓRZ Z TRENEREM

### Zadanie: Testowanie Cache L1

Uruchom `CacheDemo.java` i zaobserwuj logi:

```java
// Krok 1: Pierwsze pobranie
Author author1 = session.find(Author.class, 1L);
// Ile zapytań SQL widzisz? _____

// Krok 2: Drugie pobranie tego samego obiektu
Author author2 = session.find(Author.class, 1L);
// Ile zapytań SQL widzisz teraz? _____

// Krok 3: Czy author1 == author2?
System.out.println(author1 == author2);
// Wynik: _____ (true/false)
```

**Dlaczego `author1 == author2` zwraca TRUE?**
Odpowiedź: _________________________________________________

---

## 💪 ĆWICZ SAMODZIELNIE

### Ćwiczenie 2.1: Przewidywanie zachowania cache

Dla każdego scenariusza napisz ile zapytań SQL wykona Hibernate:

```java
// Scenariusz A
Session session = sessionFactory.openSession();
session.find(Pirate.class, 1L);  // Zapytanie 1
session.find(Pirate.class, 1L);  // Zapytanie ?
session.find(Pirate.class, 2L);  // Zapytanie ?
session.close();
```
**Odpowiedź A:** _____ zapytań SQL

```java
// Scenariusz B
Session session = sessionFactory.openSession();
session.find(Pirate.class, 1L);  // Zapytanie 1
session.clear();
session.find(Pirate.class, 1L);  // Zapytanie ?
session.close();
```
**Odpowiedź B:** _____ zapytań SQL

<details>
<summary>💡 Rozwiązanie</summary>

**Scenariusz A: 2 zapytania**
- Pierwsze find(1L) → idzie do bazy (1)
- Drugie find(1L) → z cache L1 (0)
- Trzecie find(2L) → idzie do bazy, bo INNY obiekt (1)

**Scenariusz B: 2 zapytania**
- Pierwsze find(1L) → idzie do bazy (1)
- clear() → czyści cache
- Drugie find(1L) → musi iść do bazy ponownie (1)

</details>

---

### Ćwiczenie 2.2: Kiedy używać clear()?

Masz do przetworzenia 10,000 rekordów. Który kod jest lepszy?

**Opcja A:**
```java
for (int i = 0; i < 10000; i++) {
    Pirate pirate = session.find(Pirate.class, (long) i);
    pirate.setExperience(pirate.getExperience() + 1);
}
```

**Opcja B:**
```java
for (int i = 0; i < 10000; i++) {
    Pirate pirate = session.find(Pirate.class, (long) i);
    pirate.setExperience(pirate.getExperience() + 1);

    if (i % 100 == 0) {
        session.flush();
        session.clear();
    }
}
```

**Moja odpowiedź:** Opcja _____ jest lepsza

**Dlaczego?**
_________________________________________________________________

<details>
<summary>💡 Rozwiązanie</summary>

**Opcja B jest lepsza!**

Opcja A trzyma wszystkie 10,000 obiektów w pamięci (cache L1) → możliwy OutOfMemoryError

Opcja B:
- Co 100 obiektów wykonuje flush() (zapisuje zmiany do bazy)
- Potem clear() (czyści cache L1, zwalnia pamięć)
- Pamięć jest stale zwalniana

</details>

---

## ✅ CHECKPOINT 2

- [ ] Rozumiem różnicę między Cache L1 a L2
- [ ] Wiem że Cache L1 jest zawsze włączony
- [ ] Potrafię przewidzieć kiedy Hibernate wykona SQL
- [ ] Wiem kiedy używać clear() i evict()



---

## 🎯 BLOK 3: Wzorce projektowe - Singleton, Factory (10:45-11:30)

### 📝 Notatki z teorii

#### Wzorzec SINGLETON

**Cel:** Zapewnić, że klasa ma dokładnie __________ instancję i zapewnić do niej globalny punkt dostępu.

**Kiedy używać:**
- Konfiguracja aplikacji
- __________
- Connection pool
- __________

**Implementacja - kluczowe elementy:**
1. Prywatny konstruktor - nikt nie może wywołać `new __________`
2. Statyczna zmienna przechowująca jedyną instancję: `private static Captain __________`
3. Publiczna metoda dostępowa: `public static Captain __________()`

**Dwie wersje Singletona:**

| Wersja | Zalety | Wady |
|--------|--------|------|
| **Klasyczna** | Prosta implementacja | NIE jest __________ |
| **Enum** | Thread-safe, odporna na serializację | Mniej elastyczna |

**Zalecana implementacja:** `enum CaptainEnum { __________ }`

---

#### Wzorzec FACTORY

**Cel:** Ukryć logikę tworzenia obiektów i pozwolić klientowi używać tylko __________.

**Kiedy używać:**
- Gdy masz rodzinę powiązanych klas
- Gdy chcesz ukryć logikę __________
- Gdy wybór klasy zależy od parametrów

**Korzyści:**
- Kod klienta nie zna __________ klas
- Łatwo dodać nowy typ obiektu
- Centralizacja logiki tworzenia

**Przykład piracki:**
```java
// BEZ Factory - zły kod
Ship ship = new Galleon("Black Pearl");  // ❌ Zależność od konkretnej klasy

// Z Factory - dobry kod
Ship ship = ShipFactory.createShip(ShipType.GALLEON, "Black Pearl");  // ✅ Zależność tylko od interfejsu
```

---

### 👀 Obserwacje z demo

**Demo 1: SingletonDemo.java**

Trener uruchamia `SingletonDemo.main()`. Obserwuj:

1. **Podstawowy Singleton:**
   - Ile razy wypisuje się "Kapitan obejmuje dowodzenie"? __________
   - Czy `captain1 == captain2`? __________
   - Co się stanie gdy zmienisz imię przez `captain1.setName()`? __________

2. **Enum Singleton:**
   - Jak uzyskać dostęp do instancji? `CaptainEnum.__________`
   - Czy `captain == captain2`? __________

**Kluczowe wnioski:**
- Singleton gwarantuje __________ instancję w całej aplikacji
- Wszystkie referencje wskazują na __________ obiekt
- Enum Singleton jest __________ (thread-safe)

---

**Demo 2: FactoryDemo.java**

Trener uruchamia `FactoryDemo.main()`. Obserwuj:

1. **BEZ Factory:**
   - Ile konkretnych klas musi znać kod klienta? __________
   - Czy łatwo zmienić implementację? __________

2. **Z Factory:**
   - Czy kod klienta zna konkretne klasy (`Galleon`, `Frigate`)? __________
   - Jakiego typu jest zmienna `ship1`? __________ (interfejs czy klasa?)
   - Czy możemy używać wszystkich statków polimorficznie? __________

3. **Semantyczne metody Factory:**
   - Jakie metody są bardziej czytelne: `createShip(ShipType.GALLEON)` czy `createBattleship()`? __________

**Kluczowe wnioski:**
- Factory ukrywa __________ tworzenia obiektów
- Kod klienta zależy tylko od __________
- Semantyczne metody poprawiają __________

---

### 💻 Ćwiczenia praktyczne

#### ✏️ Ćwiczenie 3.1: Implementacja Singletona - Logger

**Zadanie:** Stwórz klasę `PirateLogger` jako Singleton, która loguje wydarzenia na statku.

**Wymagania:**
- Prywatny konstruktor
- Metoda `getInstance()`
- Metoda `log(String message)` wypisująca: `[LOG] message`
- Metoda `getLogCount()` zwracająca liczbę logów

**TODO - Uzupełnij kod:**

```java
package pl.przemekzagorski.training.patterns.singleton;

public class PirateLogger {

    // TODO: Dodaj prywatną statyczną zmienną instance
    private static PirateLogger __________ = null;

    private int logCount = 0;

    // TODO: Zrób konstruktor prywatnym
    __________ PirateLogger() {
        System.out.println("📜 Logger zainicjalizowany");
    }

    // TODO: Dodaj metodę getInstance()
    public static PirateLogger __________() {
        if (__________ == null) {
            __________ = new PirateLogger();
        }
        return __________;
    }

    public void log(String message) {
        System.out.println("[LOG] " + message);
        _________++;  // TODO: Zwiększ licznik
    }

    public int getLogCount() {
        return logCount;
    }
}
```

**Test:**
```java
public class LoggerTest {
    public static void main(String[] args) {
        PirateLogger logger1 = PirateLogger.getInstance();
        logger1.log("Statek wypłynął z portu");

        PirateLogger logger2 = PirateLogger.getInstance();
        logger2.log("Zauważono wrogi okręt");

        System.out.println("Liczba logów: " + logger1.getLogCount());  // Powinno być: 2
        System.out.println("logger1 == logger2: " + (logger1 == logger2));  // Powinno być: true
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
package pl.przemekzagorski.training.patterns.singleton;

public class PirateLogger {

    private static PirateLogger instance = null;

    private int logCount = 0;

    private PirateLogger() {
        System.out.println("📜 Logger zainicjalizowany");
    }

    public static PirateLogger getInstance() {
        if (instance == null) {
            instance = new PirateLogger();
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("[LOG] " + message);
        logCount++;
    }

    public int getLogCount() {
        return logCount;
    }
}
```

**Wyjaśnienie:**
- `instance` - statyczna zmienna przechowująca jedyną instancję
- Konstruktor `private` - zapobiega tworzeniu instancji przez `new`
- `getInstance()` - lazy initialization (tworzy instancję tylko gdy potrzebna)
- Wszystkie wywołania `getInstance()` zwracają TĘ SAMĄ instancję

</details>

---

#### ✏️ Ćwiczenie 3.2: Implementacja Factory - WeaponFactory

**Zadanie:** Stwórz fabrykę broni pirackich, która produkuje różne typy broni.

**Wymagania:**
- Interfejs `Weapon` z metodami: `getName()`, `attack()`, `getDamage()`
- Klasy: `Sword`, `Pistol`, `Cannon` implementujące `Weapon`
- Klasa `WeaponFactory` z metodą `createWeapon(WeaponType type)`

**TODO - Uzupełnij kod:**

```java
// Interfejs
public interface Weapon {
    String getName();
    void attack();
    int getDamage();
}

// Implementacja - Szabla
public class Sword implements Weapon {
    public String getName() { return "Szabla"; }
    public void attack() { System.out.println("⚔️ Cięcie szablą!"); }
    public int getDamage() { return 20; }
}

// TODO: Dodaj klasy Pistol i Cannon analogicznie
// Pistol: 🔫, damage 30
// Cannon: 💣, damage 100

// Factory
public class WeaponFactory {

    public enum WeaponType {
        SWORD, PISTOL, CANNON
    }

    // TODO: Uzupełnij metodę createWeapon
    public static Weapon createWeapon(WeaponType type) {
        return switch (type) {
            case SWORD -> new __________();
            case PISTOL -> new __________();
            case CANNON -> new __________();
        };
    }
}
```

**Test:**
```java
public class WeaponTest {
    public static void main(String[] args) {
        Weapon sword = WeaponFactory.createWeapon(WeaponType.SWORD);
        Weapon pistol = WeaponFactory.createWeapon(WeaponType.PISTOL);
        Weapon cannon = WeaponFactory.createWeapon(WeaponType.CANNON);

        sword.attack();   // ⚔️ Cięcie szablą!
        pistol.attack();  // 🔫 Strzał z pistoletu!
        cannon.attack();  // 💣 Wystrzał z armaty!

        System.out.println("Suma obrażeń: " +
            (sword.getDamage() + pistol.getDamage() + cannon.getDamage()));  // 150
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
// Pistol
public class Pistol implements Weapon {
    public String getName() { return "Pistolet"; }
    public void attack() { System.out.println("🔫 Strzał z pistoletu!"); }
    public int getDamage() { return 30; }
}

// Cannon
public class Cannon implements Weapon {
    public String getName() { return "Armata"; }
    public void attack() { System.out.println("💣 Wystrzał z armaty!"); }
    public int getDamage() { return 100; }
}

// Factory
public class WeaponFactory {

    public enum WeaponType {
        SWORD, PISTOL, CANNON
    }

    public static Weapon createWeapon(WeaponType type) {
        return switch (type) {
            case SWORD -> new Sword();
            case PISTOL -> new Pistol();
            case CANNON -> new Cannon();
        };
    }
}
```

**Wyjaśnienie:**
- Wszystkie bronie implementują interfejs `Weapon`
- Factory ukrywa logikę tworzenia - klient nie musi znać konkretnych klas
- Użycie `switch expression` (nowoczesna Java)
- Łatwo dodać nowy typ broni - wystarczy dodać klasę i case w switch

</details>

---

### ✅ CHECKPOINT 3 - Sprawdź swoją wiedzę

Zaznacz, co już opanowałeś:

- [ ] Rozumiem cel wzorca Singleton (jedna instancja w aplikacji)
- [ ] Potrafię zaimplementować Singleton z prywatnym konstruktorem
- [ ] Wiem, że enum Singleton jest thread-safe i zalecany
- [ ] Rozumiem cel wzorca Factory (ukrycie logiki tworzenia)
- [ ] Potrafię stworzyć Factory z metodą fabrykującą
- [ ] Rozumiem korzyść: kod klienta zależy tylko od interfejsu, nie od konkretnych klas

**Pytania do trenera:**
- _______________________________________________
- _______________________________________________


---

# 📦 BLOK 4: Wzorce projektowe - Builder, Strategy (11:30-12:15)

## 📝 Notatki z teorii

### Wzorzec Builder

**Builder** to wzorzec kreacyjny, który pozwala budować złożone obiekty krok po kroku.

**Problem:** Klasa z wieloma opcjonalnymi parametrami:
- Konstruktor z 10 parametrami = __________ (antypattern: Telescoping Constructor)
- Trudno zapamiętać __________ parametrów
- Nie wiadomo, który parametr jest __________
- Co jeśli chcę ustawić tylko niektóre parametry?

**Rozwiązanie - Builder:**
1. Prywatny __________ w klasie głównej (tylko Builder może tworzyć obiekty)
2. Wewnętrzna klasa statyczna __________
3. Metody buildera zwracają __________ (fluent API)
4. Metoda `build()` tworzy finalny __________
5. Obiekt jest __________ (immutable - tylko gettery, brak setterów)

**Zalety:**
- ✅ Każdy parametr jest __________ - wiadomo co ustawiamy
- ✅ Można pominąć __________ parametry
- ✅ __________ nie ma znaczenia
- ✅ Obiekt jest __________ (bezpieczny w wielowątkowym środowisku)
- ✅ Można dodać __________ w metodzie build()

**Kiedy używać:**
- Klasa ma więcej niż __________ parametrów
- Większość parametrów jest __________
- Chcemy mieć __________ obiekt

---

### Wzorzec Strategy

**Strategy** to wzorzec behawioralny, który pozwala wymieniać algorytmy w runtime.

**Problem:** Metoda z wieloma if/else dla różnych wariantów zachowania:
```java
void attack(String type, String target) {
    if (type.equals("cannon")) {
        // 20 linii kodu
    } else if (type.equals("boarding")) {
        // 20 linii kodu
    } else if (type.equals("ramming")) {
        // 20 linii kodu
    }
    // Metoda rośnie i rośnie...
}
```

**Problemy:**
- ❌ Naruszenie __________ Principle (Open/Closed)
- ❌ Trudne do __________
- ❌ Jedna wielka __________

**Rozwiązanie - Strategy:**
1. Interfejs __________ z metodą wykonującą algorytm
2. Konkretne klasy implementujące różne __________
3. Klasa kontekstowa przechowuje referencję do __________
4. Możliwość zmiany strategii w __________

**Zalety:**
- ✅ Każda strategia w osobnej __________
- ✅ Łatwo dodać nową strategię (nowa klasa, bez modyfikacji istniejącego kodu)
- ✅ Łatwo __________ każdą strategię osobno
- ✅ Można zmieniać strategię w __________
- ✅ Zgodność z __________ Principle (Open/Closed)

**Strategy a Functional Interface:**
- Jeśli interfejs ma tylko jedną metodę, można oznaczyć go jako __________
- Pozwala to używać __________ zamiast tworzenia osobnych klas
- Dla prostych strategii - lambda __________

---

## 👀 Obserwacje z demo

### Demo 1: BuilderDemo.java

**Bez Builder (antypattern):**
```java
new Ship("Black Pearl", "Galleon", 32, 100, true, true,
         "Jack Sparrow", 500, "Tortuga")
```

**Pytania:**
1. Który parametr oznacza liczbę armat? __________
2. Co oznacza piąty parametr (true)? __________
3. Jak stworzyć statek tylko z nazwą i armatami? __________

---

**Z Builder (dobry kod):**
```java
PirateShip blackPearl = new PirateShip.Builder("Black Pearl")
        .type("Galleon")
        .cannons(32)
        .crewCapacity(100)
        .captain("Jack Sparrow")
        .withJollyRoger()
        .homePort("Tortuga")
        .cargoCapacity(500)
        .build();
```

**Pytania:**
1. Jakie są wymagane parametry? __________
2. Co zwraca metoda `.cannons(32)`? __________
3. Czy można zmienić kolejność wywołań metod? __________
4. Czy można pominąć `.homePort()`? __________

---

### Demo 2: StrategyDemo.java

**Bez Strategy (zły kod):**
- Metoda `attack()` z wieloma if/else
- Dodanie nowego typu ataku wymaga modyfikacji metody

**Z Strategy (dobry kod):**
```java
BattleShip blackPearl = new BattleShip("Black Pearl");
blackPearl.attack(enemy);  // Domyślna strategia: armaty

blackPearl.setAttackStrategy(new BoardingAttack());
blackPearl.attack(enemy);  // Teraz: abordaż

blackPearl.setAttackStrategy(new RammingAttack());
blackPearl.attack(enemy);  // Teraz: taranowanie
```

**Pytania:**
1. Jaka jest domyślna strategia ataku? __________
2. Kiedy strategia jest zmieniana? __________
3. Czy trzeba modyfikować klasę BattleShip, żeby dodać nową strategię? __________

---

**Strategy z lambdą:**
```java
flyingDutchman.setAttackStrategy((attacker, target) -> {
    System.out.println("🦑 " + attacker + " uwalnia Krakena na " + target + "!");
});
```

**Pytania:**
1. Dlaczego można użyć lambdy? __________
2. Kiedy lepiej użyć lambdy, a kiedy osobnej klasy? __________

---

## 💪 Ćwiczenie 4.1: Implementacja Builder

**Zadanie:** Zaimplementuj wzorzec Builder dla klasy `PirateCrew` (załoga piracka).

**Wymagania:**
- Wymagany parametr: `shipName` (nazwa statku)
- Opcjonalne parametry:
  - `captainName` (domyślnie: "Unknown")
  - `sailorCount` (domyślnie: 10)
  - `cannoneerCount` (domyślnie: 5)
  - `cookName` (domyślnie: "Unknown")
  - `hasMusiciant` (domyślnie: false)
  - `totalGold` (domyślnie: 0)

**TODO - Uzupełnij kod:**

```java
package pl.przemekzagorski.training.exercises;

public class PirateCrew {

    // TODO: Dodaj pola (wszystkie final!)
    private final String shipName;
    // ... pozostałe pola

    // TODO: Prywatny konstruktor przyjmujący Builder
    private PirateCrew(Builder builder) {
        // ...
    }

    // TODO: Dodaj gettery (tylko gettery - immutable!)
    public String getShipName() { return shipName; }
    // ...

    @Override
    public String toString() {
        return String.format("""
            🏴‍☠️ Załoga statku: %s
               Kapitan: %s
               Żeglarze: %d
               Artylerzyści: %d
               Kucharz: %s
               Muzyk: %s
               Złoto: %d monet
            """, shipName, captainName, sailorCount, cannoneerCount,
                cookName, hasMusiciant ? "Tak" : "Nie", totalGold);
    }

    // TODO: Zaimplementuj Builder
    public static class Builder {

        // TODO: Wymagane pola (final)
        private final String shipName;

        // TODO: Opcjonalne pola z domyślnymi wartościami
        private String captainName = "Unknown";
        // ...

        // TODO: Konstruktor z wymaganym parametrem
        public Builder(String shipName) {
            this.shipName = shipName;
        }

        // TODO: Metody buildera (zwracają this!)
        public Builder captain(String captainName) {
            this.captainName = captainName;
            return this;
        }

        // ... pozostałe metody

        // TODO: Metoda build() z walidacją
        public PirateCrew build() {
            // Walidacja: shipName nie może być null ani pusty
            // Walidacja: sailorCount >= 0
            // Walidacja: cannoneerCount >= 0
            return new PirateCrew(this);
        }
    }
}
```

**Test:**
```java
public class BuilderTest {
    public static void main(String[] args) {
        // Pełna załoga
        PirateCrew fullCrew = new PirateCrew.Builder("Black Pearl")
                .captain("Jack Sparrow")
                .sailors(50)
                .cannoneers(20)
                .cook("Cookie")
                .withMusiciant()
                .gold(10000)
                .build();
        System.out.println(fullCrew);

        // Minimalna załoga
        PirateCrew minimalCrew = new PirateCrew.Builder("Little Boat")
                .build();
        System.out.println(minimalCrew);
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
package pl.przemekzagorski.training.exercises;

public class PirateCrew {

    private final String shipName;
    private final String captainName;
    private final int sailorCount;
    private final int cannoneerCount;
    private final String cookName;
    private final boolean hasMusiciant;
    private final int totalGold;

    private PirateCrew(Builder builder) {
        this.shipName = builder.shipName;
        this.captainName = builder.captainName;
        this.sailorCount = builder.sailorCount;
        this.cannoneerCount = builder.cannoneerCount;
        this.cookName = builder.cookName;
        this.hasMusiciant = builder.hasMusiciant;
        this.totalGold = builder.totalGold;
    }

    public String getShipName() { return shipName; }
    public String getCaptainName() { return captainName; }
    public int getSailorCount() { return sailorCount; }
    public int getCannoneerCount() { return cannoneerCount; }
    public String getCookName() { return cookName; }
    public boolean hasMusiciant() { return hasMusiciant; }
    public int getTotalGold() { return totalGold; }

    @Override
    public String toString() {
        return String.format("""
            🏴‍☠️ Załoga statku: %s
               Kapitan: %s
               Żeglarze: %d
               Artylerzyści: %d
               Kucharz: %s
               Muzyk: %s
               Złoto: %d monet
            """, shipName, captainName, sailorCount, cannoneerCount,
                cookName, hasMusiciant ? "Tak" : "Nie", totalGold);
    }

    public static class Builder {
        private final String shipName;
        private String captainName = "Unknown";
        private int sailorCount = 10;
        private int cannoneerCount = 5;
        private String cookName = "Unknown";
        private boolean hasMusiciant = false;
        private int totalGold = 0;

        public Builder(String shipName) {
            this.shipName = shipName;
        }

        public Builder captain(String captainName) {
            this.captainName = captainName;
            return this;
        }

        public Builder sailors(int sailorCount) {
            this.sailorCount = sailorCount;
            return this;
        }

        public Builder cannoneers(int cannoneerCount) {
            this.cannoneerCount = cannoneerCount;
            return this;
        }

        public Builder cook(String cookName) {
            this.cookName = cookName;
            return this;
        }

        public Builder withMusiciant() {
            this.hasMusiciant = true;
            return this;
        }

        public Builder gold(int totalGold) {
            this.totalGold = totalGold;
            return this;
        }

        public PirateCrew build() {
            if (shipName == null || shipName.isBlank()) {
                throw new IllegalStateException("Załoga musi należeć do jakiegoś statku!");
            }
            if (sailorCount < 0) {
                throw new IllegalArgumentException("Liczba żeglarzy nie może być ujemna!");
            }
            if (cannoneerCount < 0) {
                throw new IllegalArgumentException("Liczba artylerzy nie może być ujemna!");
            }
            return new PirateCrew(this);
        }
    }
}
```

</details>

---

## 💪 Ćwiczenie 4.2: Implementacja Strategy

**Zadanie:** Zaimplementuj wzorzec Strategy dla różnych strategii nawigacji statku pirackim.

**Wymagania:**
- Interfejs `NavigationStrategy` z metodą `navigate(String from, String to)`
- Trzy konkretne strategie:
  - `CompassNavigation` - nawigacja kompasem (bezpieczna, wolna)
  - `StarNavigation` - nawigacja po gwiazdach (szybka, tylko w nocy)
  - `MapNavigation` - nawigacja mapą (średnia prędkość, wymaga mapy)
- Klasa `PirateNavigator` używająca strategii

**TODO - Uzupełnij kod:**

```java
package pl.przemekzagorski.training.exercises;

// TODO: Zdefiniuj interfejs NavigationStrategy
@FunctionalInterface
public interface NavigationStrategy {
    void navigate(String from, String to);
}
```

```java
package pl.przemekzagorski.training.exercises;

// TODO: Implementacja strategii kompasowej
public class CompassNavigation implements NavigationStrategy {
    @Override
    public void navigate(String from, String to) {
        System.out.println("🧭 Nawigacja kompasem z " + from + " do " + to);
        System.out.println("   Bezpieczna trasa, czas podróży: 5 dni");
    }
}
```

```java
package pl.przemekzagorski.training.exercises;

// TODO: Implementacja strategii gwiazdowej
public class StarNavigation implements NavigationStrategy {
    @Override
    public void navigate(String from, String to) {
        // TODO: Wypisz informację o nawigacji po gwiazdach
        // Szybka trasa, czas podróży: 3 dni, tylko w nocy
    }
}
```

```java
package pl.przemekzagorski.training.exercises;

// TODO: Implementacja strategii mapowej
public class MapNavigation implements NavigationStrategy {
    @Override
    public void navigate(String from, String to) {
        // TODO: Wypisz informację o nawigacji mapą
        // Średnia trasa, czas podróży: 4 dni
    }
}
```

```java
package pl.przemekzagorski.training.exercises;

// TODO: Klasa używająca strategii
public class PirateNavigator {

    private String shipName;
    private NavigationStrategy strategy;

    public PirateNavigator(String shipName) {
        this.shipName = shipName;
        this.strategy = new CompassNavigation();  // Domyślna strategia
    }

    // TODO: Metoda zmiany strategii
    public void setNavigationStrategy(NavigationStrategy strategy) {
        // ...
    }

    // TODO: Metoda nawigacji
    public void navigateTo(String destination) {
        // ...
    }
}
```

**Test:**
```java
public class StrategyTest {
    public static void main(String[] args) {
        PirateNavigator navigator = new PirateNavigator("Black Pearl");

        // Domyślna strategia
        navigator.navigateTo("Tortuga");

        // Zmiana na nawigację gwiazdową
        navigator.setNavigationStrategy(new StarNavigation());
        navigator.navigateTo("Port Royal");

        // Zmiana na nawigację mapą
        navigator.setNavigationStrategy(new MapNavigation());
        navigator.navigateTo("Isla de Muerta");

        // Lambda dla niestandardowej strategii
        navigator.setNavigationStrategy((from, to) -> {
            System.out.println("🦜 Papuga wskazuje drogę z " + from + " do " + to + "!");
        });
        navigator.navigateTo("Shipwreck Cove");
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
// NavigationStrategy.java
@FunctionalInterface
public interface NavigationStrategy {
    void navigate(String from, String to);
}

// CompassNavigation.java
public class CompassNavigation implements NavigationStrategy {
    @Override
    public void navigate(String from, String to) {
        System.out.println("🧭 Nawigacja kompasem z " + from + " do " + to);
        System.out.println("   Bezpieczna trasa, czas podróży: 5 dni");
    }
}

// StarNavigation.java
public class StarNavigation implements NavigationStrategy {
    @Override
    public void navigate(String from, String to) {
        System.out.println("⭐ Nawigacja po gwiazdach z " + from + " do " + to);
        System.out.println("   Szybka trasa, czas podróży: 3 dni (tylko w nocy!)");
    }
}

// MapNavigation.java
public class MapNavigation implements NavigationStrategy {
    @Override
    public void navigate(String from, String to) {
        System.out.println("🗺️ Nawigacja mapą z " + from + " do " + to);
        System.out.println("   Średnia trasa, czas podróży: 4 dni");
    }
}

// PirateNavigator.java
public class PirateNavigator {

    private String shipName;
    private NavigationStrategy strategy;
    private String currentLocation = "Open Sea";

    public PirateNavigator(String shipName) {
        this.shipName = shipName;
        this.strategy = new CompassNavigation();
    }

    public void setNavigationStrategy(NavigationStrategy strategy) {
        System.out.println("⚙️ " + shipName + " zmienia metodę nawigacji!");
        this.strategy = strategy;
    }

    public void navigateTo(String destination) {
        System.out.println("\n🚢 " + shipName + " rozpoczyna podróż:");
        if (strategy == null) {
            System.out.println("❌ Brak strategii nawigacji!");
            return;
        }
        strategy.navigate(currentLocation, destination);
        currentLocation = destination;
    }
}
```

</details>

---

## ✅ CHECKPOINT 4

Zaznacz, co już umiesz:

- [ ] Rozumiem problem "Telescoping Constructor" i wiem, kiedy użyć Builder
- [ ] Potrafię zaimplementować wzorzec Builder z fluent API
- [ ] Rozumiem, dlaczego obiekty tworzone przez Builder są immutable
- [ ] Rozumiem problem wielkich metod z if/else i wiem, kiedy użyć Strategy
- [ ] Potrafię zaimplementować wzorzec Strategy z interfejsem i konkretnymi klasami
- [ ] Rozumiem, jak używać lambd jako strategii (dzięki @FunctionalInterface)

---

## 📌 Pytania do trenera

Zapisz tutaj pytania, które chcesz zadać trenerowi:

1. _______________________________________________
2. _______________________________________________
3. _______________________________________________



---

## 🦨 BLOK 5: Code Smells i Refaktoring (13:15-14:00)

### 📝 Notatki teoretyczne

#### Czym są "Code Smells" (Zapachy kodu)?

**Code Smell** to __________ w kodzie, który sugeruje głębszy problem w projekcie.
To nie jest błąd - kod działa, ale jest __________ do utrzymania, testowania i rozwijania.

#### 6 najczęstszych Code Smells:

**1. Long Method (Długa metoda)**
- Metoda ma więcej niż __________ linii kodu
- Robi zbyt wiele rzeczy naraz
- Rozwiązanie: **__________** (wydziel mniejsze metody)

**2. God Class (Klasa-Bóg)**
- Jedna klasa wie o __________ i robi __________
- Ma zbyt wiele odpowiedzialności
- Rozwiązanie: **__________** (podziel na mniejsze klasy)

**3. Magic Numbers (Magiczne liczby)**
- Hardkodowane wartości bez __________ (np. 0.1, 500, 100)
- Nikt nie wie co oznaczają
- Rozwiązanie: **Replace Magic Number with __________**

**4. Duplicate Code (Duplikacja kodu)**
- Ten sam kod powtórzony w __________ miejscach
- Zmiana wymaga edycji w wielu miejscach
- Rozwiązanie: **Extract Method** lub **__________**

**5. Feature Envy (Zazdrość o funkcjonalność)**
- Metoda używa więcej danych z __________ klasy niż z własnej
- Logika jest w złym miejscu
- Rozwiązanie: **__________ Method** (przenieś metodę do właściwej klasy)

**6. Primitive Obsession (Obsesja na punkcie prymitywów)**
- Używanie __________ typów zamiast obiektów (np. 8 parametrów int/String)
- Brak enkapsulacji logiki biznesowej
- Rozwiązanie: **Introduce __________ Object** (stwórz klasę Ship zamiast 8 parametrów)

---

#### Techniki refaktoringu:

| Technika | Kiedy stosować | Przykład |
|----------|----------------|----------|
| **Extract Method** | Metoda > 20 linii | Wydziel `calculateDamage()` |
| **Extract Class** | God Class | Wydziel `LootCalculator` |
| **Introduce Parameter Object** | Wiele parametrów | Zamień 8 parametrów na `Ship` |
| **Replace Magic Number** | Hardkodowane liczby | `DAMAGE_PER_CANNON = 10` |
| **Move Method** | Feature Envy | Przenieś logikę do `Ship` |
| **Dependency Injection** | Tight coupling | Wstrzykuj przez konstruktor |

---

### 👀 Obserwacje z demo

**Plik:** `RefactoringDemo.java`

Trener pokazuje porównanie złego i dobrego kodu. Odpowiedz na pytania:

#### Demo 1: BadPirateService (ZŁY KOD)

**Pytanie 1:** Ile linii ma metoda `processBattle()` w `BadPirateService`?
```
Odpowiedź: ___________
```

**Pytanie 2:** Wymień wszystkie odpowiedzialności klasy `BadPirateService`:
```
1. ___________
2. ___________
3. ___________
4. ___________
5. ___________
6. ___________
```

**Pytanie 3:** Znajdź duplikację kodu - które linie są skopiowane?
```
Linie _____ - _____ (obliczanie obrażeń statku 1)
Linie _____ - _____ (obliczanie obrażeń statku 2)
```

**Pytanie 4:** Jakie "magiczne liczby" widzisz w kodzie?
```
_____, _____, _____, _____, _____, _____, _____
```

**Pytanie 5:** Ile parametrów ma metoda `processBattle()`?
```
Odpowiedź: _____ parametrów (wszystkie typy prymitywne!)
```

---

#### Demo 2: GoodBattleService (DOBRY KOD - po refaktoringu)

**Pytanie 6:** Ile linii ma metoda `processBattle()` w `GoodBattleService`?
```
Odpowiedź: ___________
```

**Pytanie 7:** Jakie klasy zostały wydzielone z `BadPirateService`?
```
1. ___________
2. ___________
3. ___________
4. ___________
```

**Pytanie 8:** Gdzie teraz znajduje się logika obliczania obrażeń?
```
Odpowiedź: W klasie ___________, metoda ___________()
```

**Pytanie 9:** Jak nazywają się stałe zastępujące magiczne liczby w klasie `Ship`?
```
DAMAGE_PER_CANNON = _____
LARGE_CREW_THRESHOLD = _____
HUGE_CREW_THRESHOLD = _____
LARGE_CREW_BONUS = _____
HUGE_CREW_BONUS = _____
```

**Pytanie 10:** Jak wstrzykiwane są zależności w `GoodBattleService`?
```
Przez ___________ (Dependency Injection)
```

---

### 💻 Ćwiczenie 5.1: Znajdź wszystkie Code Smells

Przeanalizuj poniższy kod i znajdź **wszystkie 6 code smells**:

```java
public class BadPirateService {

    public void processBattle(String ship1Name, int ship1Cannons, int ship1Crew, int ship1Health,
                              String ship2Name, int ship2Cannons, int ship2Crew, int ship2Health) {

        // Oblicz obrażenia statku 1
        int damage1 = ship1Cannons * 10;
        if (ship1Crew > 50) {
            damage1 = damage1 + 20;
        }
        if (ship1Crew > 100) {
            damage1 = damage1 + 30;
        }

        // Oblicz obrażenia statku 2
        int damage2 = ship2Cannons * 10;
        if (ship2Crew > 50) {
            damage2 = damage2 + 20;
        }
        if (ship2Crew > 100) {
            damage2 = damage2 + 30;
        }

        // ... (jeszcze 60 linii kodu!)

        saveToDatabase(winner, loot);
        sendNotification(winner);
    }
}
```

**Wypełnij tabelę:**

| Code Smell | Gdzie w kodzie? | Jak naprawić? |
|------------|-----------------|---------------|
| 1. Long Method | `processBattle()` ma ~80 linii | __________ |
| 2. God Class | Jedna klasa robi: obliczenia, raport, zapis, powiadomienia | __________ |
| 3. Magic Numbers | 10, 50, 100, 20, 30 | __________ |
| 4. Duplicate Code | __________ | Extract Method |
| 5. Feature Envy | __________ | Move Method do klasy Ship |
| 6. Primitive Obsession | __________ | __________ |

<details>
<summary>💡 Rozwiązanie</summary>

| Code Smell | Gdzie w kodzie? | Jak naprawić? |
|------------|-----------------|---------------|
| 1. Long Method | `processBattle()` ma ~80 linii | **Extract Method** - wydziel mniejsze metody |
| 2. God Class | Jedna klasa robi: obliczenia, raport, zapis, powiadomienia | **Split Class** - wydziel LootCalculator, BattleReporter, BattleRepository |
| 3. Magic Numbers | 10, 50, 100, 20, 30 | **Replace with Constants** - DAMAGE_PER_CANNON, LARGE_CREW_THRESHOLD |
| 4. Duplicate Code | Obliczanie obrażeń skopiowane dla ship1 i ship2 | **Extract Method** - Ship.calculateDamage() |
| 5. Feature Envy | Logika obrażeń operuje na danych Ship, ale jest w Service | **Move Method** do klasy Ship |
| 6. Primitive Obsession | 8 parametrów prymitywnych zamiast obiektów | **Introduce Parameter Object** - stwórz klasę Ship |

</details>

---

### 💻 Ćwiczenie 5.2: Refaktoring - Extract Method i Constants

Zrefaktoruj poniższy kod stosując techniki:
1. **Replace Magic Number with Constant**
2. **Extract Method**

**ZŁY KOD:**
```java
public class PirateTreasure {

    public int calculateValue(int gold, int silver, int gems) {
        int total = 0;

        // Przelicz złoto
        total = total + gold * 100;

        // Przelicz srebro
        total = total + silver * 10;

        // Przelicz klejnoty
        total = total + gems * 500;

        // Bonus za dużą ilość
        if (gold > 50) {
            total = total + 1000;
        }

        // Podatek piracki
        total = (int)(total * 0.9);

        return total;
    }
}
```

**TODO:** Zrefaktoruj kod poniżej:

```java
public class PirateTreasure {

    // TODO: Dodaj stałe zamiast magic numbers
    private static final int __________ = 100;
    private static final int __________ = 10;
    private static final int __________ = 500;
    private static final int __________ = 50;
    private static final int __________ = 1000;
    private static final double __________ = 0.9;

    public int calculateValue(int gold, int silver, int gems) {
        // TODO: Użyj Extract Method - wydziel obliczenia do osobnych metod
        int baseValue = __________();
        int bonusValue = __________();
        int finalValue = __________();

        return finalValue;
    }

    // TODO: Zaimplementuj wydzielone metody
    private int calculateBaseValue(int gold, int silver, int gems) {
        // ...
    }

    private int calculateBonus(int gold, int baseValue) {
        // ...
    }

    private int applyTax(int value) {
        // ...
    }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public class PirateTreasure {

    // ✅ Stałe zamiast magic numbers
    private static final int GOLD_VALUE = 100;
    private static final int SILVER_VALUE = 10;
    private static final int GEM_VALUE = 500;
    private static final int LARGE_TREASURE_THRESHOLD = 50;
    private static final int LARGE_TREASURE_BONUS = 1000;
    private static final double TAX_RATE = 0.9;

    public int calculateValue(int gold, int silver, int gems) {
        int baseValue = calculateBaseValue(gold, silver, gems);
        int bonusValue = calculateBonus(gold, baseValue);
        int finalValue = applyTax(bonusValue);

        return finalValue;
    }

    // ✅ Extract Method - każda metoda robi jedną rzecz
    private int calculateBaseValue(int gold, int silver, int gems) {
        return gold * GOLD_VALUE
             + silver * SILVER_VALUE
             + gems * GEM_VALUE;
    }

    private int calculateBonus(int gold, int baseValue) {
        if (gold > LARGE_TREASURE_THRESHOLD) {
            return baseValue + LARGE_TREASURE_BONUS;
        }
        return baseValue;
    }

    private int applyTax(int value) {
        return (int)(value * TAX_RATE);
    }
}
```

**Korzyści z refaktoringu:**
- ✅ Brak magic numbers - każda liczba ma nazwę
- ✅ Krótkie metody - każda robi jedną rzecz
- ✅ Łatwe testowanie - można testować każdą metodę osobno
- ✅ Łatwa zmiana - np. zmiana TAX_RATE w jednym miejscu

</details>

---

### ✅ CHECKPOINT 5

Zaznacz, czego się nauczyłeś:

- [ ] Potrafię rozpoznać **Long Method** (metoda > 20 linii)
- [ ] Potrafię rozpoznać **God Class** (klasa robi za dużo)
- [ ] Potrafię rozpoznać **Magic Numbers** i zastąpić je stałymi
- [ ] Potrafię rozpoznać **Duplicate Code** i wydzielić wspólną metodę
- [ ] Rozumiem **Feature Envy** (logika w złym miejscu)
- [ ] Rozumiem **Primitive Obsession** (za dużo prymitywów zamiast obiektów)
- [ ] Potrafię zastosować **Extract Method** do uproszczenia kodu
- [ ] Potrafię zastosować **Extract Class** do podziału odpowiedzialności
- [ ] Rozumiem korzyści z **Dependency Injection**

---

### 📌 Pytania do trenera

```
1. _______________________________________________

2. _______________________________________________

3. _______________________________________________
```




---

## 🏗️ BLOK 6: Zasady SOLID (14:15-15:00)

### 📝 Notatki teoretyczne

#### Czym jest SOLID?

**SOLID** to akronim 5 zasad projektowania obiektowego, które pomagają tworzyć kod:
- __________ (łatwy do zmiany)
- __________ (łatwy do testowania)
- __________ (łatwy do rozszerzania)

#### 5 zasad SOLID:

**S - Single Responsibility Principle (Zasada Pojedynczej Odpowiedzialności)**

Klasa powinna mieć tylko __________ powód do zmiany.

**Przykład:**
- ❌ ZŁE: Klasa `Ship` ma metody: `sail()`, `attack()`, `saveToDatabase()`, `printReport()`, `sendNotification()`
- ✅ DOBRE: Rozdzielone klasy - `Ship`, `__________`, `__________`, `__________`

**Korzyści:**
- Łatwiejsze __________ (każda klasa robi jedną rzecz)
- Łatwiejsze __________ (mniejsze klasy)
- Mniejsze ryzyko __________ (zmiana w jednej odpowiedzialności nie wpływa na inne)

---

**O - Open/Closed Principle (Zasada Otwarte/Zamknięte)**

Klasy powinny być:
- __________ na rozszerzenia (można dodawać nowe funkcje)
- __________ na modyfikacje (nie zmieniamy istniejącego kodu)

**Przykład:**
- ❌ ZŁE: `AttackCalculator` z `if/else` dla każdego typu ataku
- ✅ DOBRE: Interfejs `__________` + klasy `CannonAttack`, `BoardingAttack`, `TorpedoAttack`

**Korzyści:**
- Nowy typ ataku? Dodaj nową klasę - __________ zmiany w istniejącym kodzie!
- Zgodne z wzorcem __________ (Strategy Pattern)

---

**L - Liskov Substitution Principle (Zasada Podstawienia Liskov)**

Podklasa może __________ klasę bazową bez zmiany zachowania programu.

**Przykład:**
- ❌ ZŁE: `SunkenShip extends Ship` ale `sail()` rzuca wyjątek
- ✅ DOBRE: Interfejs `__________`, `Ship implements Sailable`, `SunkenShip` NIE implementuje `Sailable`

**Zasada:**
Jeśli coś nie może `sail()`, nie powinno __________ po `Ship`!

**Korzyści:**
- Brak __________ w runtime
- Kod działa poprawnie dla wszystkich podklas

---

**I - Interface Segregation Principle (Zasada Segregacji Interfejsów)**

Lepiej wiele __________ interfejsów niż jeden wielki.

**Przykład:**
- ❌ ZŁE: Interfejs `Ship` z metodami: `sail()`, `attack()`, `dive()`, `fly()` - wymusza `__________`
- ✅ DOBRE: Osobne interfejsy - `Sailable`, `Armed`, `__________`

**Przykład użycia:**
```java
class Galleon implements __________, __________ {
    void sail() { ... }
    void attack() { ... }
}

class Submarine implements Sailable, Armed, __________ {
    void sail() { ... }
    void attack() { ... }
    void dive() { ... }
}
```

**Korzyści:**
- Klasa implementuje tylko to, czego __________ potrzebuje
- Brak pustych implementacji lub `UnsupportedOperationException`

---

**D - Dependency Inversion Principle (Zasada Odwrócenia Zależności)**

Zależności od __________, nie od konkretnych implementacji.

**Przykład:**
- ❌ ZŁE: `BattleService` tworzy `new MySqlDatabase()` i `new EmailNotifier()` wewnątrz klasy
- ✅ DOBRE: `BattleService` przyjmuje interfejsy `Database` i `Notifier` przez __________

**Kod:**
```java
class BattleService {
    private final Database database;      // Interfejs!
    private final Notifier notifier;      // Interfejs!

    // Dependency Injection przez konstruktor
    BattleService(__________ db, __________ notifier) {
        this.database = db;
        this.notifier = notifier;
    }
}

// W produkcji:
new BattleService(new __________(), new __________());

// W testach:
new BattleService(new __________(), new __________());
```

**Korzyści:**
- Łatwe __________ (możemy wstrzyknąć mocki)
- Łatwa zmiana __________ (np. z MySQL na PostgreSQL)
- Zgodne z wzorcem __________ (Dependency Injection)

---

### 👀 Obserwacje z demo

**Plik:** `SolidDemo.java`

Trener pokazuje wszystkie 5 zasad SOLID z przykładami ZŁE ❌ i DOBRE ✅. Odpowiedz na pytania:

#### Demo 1: Single Responsibility Principle (SRP)

**Pytanie 1:** Ile odpowiedzialności ma klasa `Ship` w złym przykładzie?
```
Odpowiedź: _____ odpowiedzialności
```

**Pytanie 2:** Wymień wszystkie odpowiedzialności klasy `Ship` (ZŁY przykład):
```
1. ___________
2. ___________
3. ___________
4. ___________
5. ___________
```

**Pytanie 3:** Jakie klasy zostały wydzielone w dobrym przykładzie?
```
1. Ship (tylko logika statku)
2. ___________ (zapis do bazy)
3. ___________ (drukowanie raportów)
4. ___________ (wysyłanie powiadomień)
```

---

#### Demo 2: Open/Closed Principle (OCP)

**Pytanie 4:** Co jest złego w klasie `AttackCalculator` z `if/else`?
```
Odpowiedź: Przy każdym nowym typie ataku musimy ___________
```

**Pytanie 5:** Jaki wzorzec projektowy realizuje zasadę OCP w dobrym przykładzie?
```
Odpowiedź: Wzorzec ___________
```

**Pytanie 6:** Wymień 3 klasy implementujące `AttackStrategy`:
```
1. ___________
2. ___________
3. ___________
```

---

#### Demo 3: Liskov Substitution Principle (LSP)

**Pytanie 7:** Dlaczego `SunkenShip extends Ship` jest złym pomysłem?
```
Odpowiedź: Bo `SunkenShip` nie może ___________, więc rzuca wyjątek
```

**Pytanie 8:** Jak rozwiązano problem w dobrym przykładzie?
```
Odpowiedź: Stworzono interfejs ___________, który implementuje tylko Ship (nie SunkenShip)
```

---

#### Demo 4: Interface Segregation Principle (ISP)

**Pytanie 9:** Dlaczego wielki interfejs `Ship` z metodami `sail()`, `attack()`, `dive()`, `fly()` jest zły?
```
Odpowiedź: Bo klasa Galleon musi implementować ___________ i ___________, których nie potrzebuje
```

**Pytanie 10:** Jakie małe interfejsy zostały stworzone w dobrym przykładzie?
```
1. ___________
2. ___________
3. ___________
```

**Pytanie 11:** Które interfejsy implementuje klasa `Galleon`?
```
Odpowiedź: ___________ i ___________
```

---

#### Demo 5: Dependency Inversion Principle (DIP)

**Pytanie 12:** Co jest złego w kodzie `private MySqlDatabase database = new MySqlDatabase();`?
```
Odpowiedź: Klasa jest ściśle powiązana z ___________ implementacją (tight coupling)
```

**Pytanie 13:** Jak wstrzykiwane są zależności w dobrym przykładzie?
```
Odpowiedź: Przez ___________ (Dependency Injection)
```

**Pytanie 14:** Jakie interfejsy przyjmuje konstruktor `BattleService`?
```
1. ___________
2. ___________
```

**Pytanie 15:** Jaka jest korzyść z DIP przy testowaniu?
```
Odpowiedź: Możemy wstrzyknąć ___________ zamiast prawdziwej bazy danych
```

---

### 💻 Ćwiczenie 6.1: Znajdź naruszenia SOLID

Przeanalizuj poniższy kod i znajdź **naruszenia zasad SOLID**:

```java
public class PirateManager {

    public void managePirate(String name, int age, String rank, int gold) {

        // Walidacja
        if (age < 18) {
            throw new IllegalArgumentException("Pirat za młody!");
        }

        // Obliczenia
        int salary = 0;
        if (rank.equals("captain")) {
            salary = 1000;
        } else if (rank.equals("sailor")) {
            salary = 100;
        } else if (rank.equals("cook")) {
            salary = 200;
        }

        // Zapis do bazy
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/pirates");
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO pirates VALUES (?, ?, ?, ?)");
        stmt.setString(1, name);
        stmt.setInt(2, age);
        stmt.setString(3, rank);
        stmt.setInt(4, salary);
        stmt.executeUpdate();

        // Wysyłka emaila
        SmtpClient smtp = new SmtpClient("smtp.pirates.com");
        smtp.send("admin@pirates.com", "Nowy pirat: " + name);

        // Logowanie
        System.out.println("Dodano pirata: " + name);
    }
}
```

**Wypełnij tabelę:**

| Zasada SOLID | Naruszenie w kodzie | Jak naprawić? |
|--------------|---------------------|---------------|
| **S** - Single Responsibility | Klasa robi: walidację, obliczenia, zapis do bazy, email, logowanie | __________ |
| **O** - Open/Closed | __________ | Użyj wzorca Strategy dla SalaryCalculator |
| **D** - Dependency Inversion | __________ | __________ |

<details>
<summary>💡 Rozwiązanie</summary>

| Zasada SOLID | Naruszenie w kodzie | Jak naprawić? |
|--------------|---------------------|---------------|
| **S** - Single Responsibility | Klasa robi: walidację, obliczenia, zapis do bazy, email, logowanie | **Wydziel klasy:** PirateValidator, SalaryCalculator, PirateRepository, EmailService, Logger |
| **O** - Open/Closed | if/else dla każdego ranku - nowy rank wymaga zmiany kodu | **Użyj wzorca Strategy** - interfejs SalaryCalculator + CaptainSalary, SailorSalary, CookSalary |
| **D** - Dependency Inversion | Bezpośrednie tworzenie Connection, SmtpClient (tight coupling) | **Wstrzyknij interfejsy** Database i EmailSender przez konstruktor |

**Poprawiony kod:**

```java
// ✅ Single Responsibility - każda klasa robi jedną rzecz
public class PirateManager {
    private final PirateValidator validator;
    private final SalaryCalculator salaryCalculator;
    private final PirateRepository repository;
    private final EmailService emailService;

    // ✅ Dependency Inversion - zależności od interfejsów
    public PirateManager(PirateValidator validator,
                         SalaryCalculator salaryCalculator,
                         PirateRepository repository,
                         EmailService emailService) {
        this.validator = validator;
        this.salaryCalculator = salaryCalculator;
        this.repository = repository;
        this.emailService = emailService;
    }

    public void managePirate(Pirate pirate) {
        validator.validate(pirate);
        int salary = salaryCalculator.calculate(pirate.getRank());
        repository.save(pirate);
        emailService.sendNotification("Nowy pirat: " + pirate.getName());
    }
}

// ✅ Open/Closed - nowy rank? Nowa klasa!
interface SalaryCalculator {
    int calculate(String rank);
}
```

</details>

---

### 💻 Ćwiczenie 6.2: Zastosuj zasadę Interface Segregation

Zrefaktoruj poniższy kod stosując **Interface Segregation Principle**:

**ZŁY KOD:**
```java
interface Vehicle {
    void sail();
    void fly();
    void dive();
}

class Ship implements Vehicle {
    public void sail() { System.out.println("Płynę!"); }
    public void fly() { throw new UnsupportedOperationException(); }
    public void dive() { throw new UnsupportedOperationException(); }
}

class Submarine implements Vehicle {
    public void sail() { System.out.println("Płynę!"); }
    public void fly() { throw new UnsupportedOperationException(); }
    public void dive() { System.out.println("Nurkuję!"); }
}
```

**TODO:** Zrefaktoruj kod poniżej stosując małe interfejsy:

```java
// TODO: Stwórz małe, specjalizowane interfejsy
interface __________ {
    void sail();
}

interface __________ {
    void dive();
}

interface __________ {
    void fly();
}

// TODO: Klasy implementują tylko to, czego potrzebują
class Ship implements __________ {
    public void sail() { System.out.println("Płynę!"); }
}

class Submarine implements __________, __________ {
    public void sail() { System.out.println("Płynę!"); }
    public void dive() { System.out.println("Nurkuję!"); }
}

class FlyingDutchman implements __________, __________ {
    public void sail() { System.out.println("Płynę!"); }
    public void fly() { System.out.println("Lecę!"); }
}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
// ✅ Małe, specjalizowane interfejsy
interface Sailable {
    void sail();
}

interface Submersible {
    void dive();
}

interface Flyable {
    void fly();
}

// ✅ Każda klasa implementuje tylko to, czego potrzebuje
class Ship implements Sailable {
    public void sail() { System.out.println("Płynę!"); }
}

class Submarine implements Sailable, Submersible {
    public void sail() { System.out.println("Płynę!"); }
    public void dive() { System.out.println("Nurkuję!"); }
}

class FlyingDutchman implements Sailable, Flyable {
    public void sail() { System.out.println("Płynę!"); }
    public void fly() { System.out.println("Lecę!"); }
}
```

**Korzyści:**
- ✅ Brak `UnsupportedOperationException`
- ✅ Każda klasa implementuje tylko potrzebne metody
- ✅ Łatwe dodawanie nowych typów pojazdów
- ✅ Kod jest bardziej elastyczny i czytelny

</details>

---

### ✅ CHECKPOINT 6

Zaznacz, czego się nauczyłeś:

- [ ] Rozumiem zasadę **S** - Single Responsibility (jeden powód do zmiany)
- [ ] Rozumiem zasadę **O** - Open/Closed (otwarte na rozszerzenia, zamknięte na modyfikacje)
- [ ] Rozumiem zasadę **L** - Liskov Substitution (podklasa może zastąpić klasę bazową)
- [ ] Rozumiem zasadę **I** - Interface Segregation (małe, specjalizowane interfejsy)
- [ ] Rozumiem zasadę **D** - Dependency Inversion (zależności od abstrakcji)
- [ ] Potrafię rozpoznać naruszenia SOLID w kodzie
- [ ] Potrafię zastosować wzorzec Strategy (OCP)
- [ ] Potrafię zastosować Dependency Injection (DIP)

---

### 📌 Pytania do trenera

```
1. _______________________________________________

2. _______________________________________________

3. _______________________________________________
```



---

# 🔧 BLOK 7: Narzędzia - SonarLint (15:00-15:45)

## 📝 Notatki z teorii

### Czym jest SonarLint?

SonarLint to __________ do IDE (IntelliJ, VS Code), który analizuje kod __________ (na bieżąco / po kompilacji).

**SonarLint wykrywa 3 kategorie problemów:**

1. 🐛 **Bug** - __________
2. 🔓 **Vulnerability** - __________
3. 🦨 **Code Smell** - __________

### Instalacja SonarLint w IntelliJ

**Kroki instalacji:**
1. `File` → `__________` → `Plugins`
2. Wyszukaj "__________"
3. Kliknij `Install` → `__________`

### Przykłady problemów wykrywanych przez SonarLint

**Kategoria: Bug (🐛)**
- Null pointer dereference - wywołanie metody na obiekcie który może być `__________`
- Resource leak - brak zamknięcia zasobów (Connection, Stream, __________)
- Division by zero - dzielenie przez `__________`
- equals() bez hashCode() - naruszenie kontraktu między `__________` i `__________`

**Kategoria: Vulnerability (🔓)**
- SQL Injection - konkatenacja stringa w zapytaniu SQL zamiast użycia `__________`
- Hardcoded password - hasło zapisane w kodzie jako `__________`

**Kategoria: Code Smell (🦨)**
- Unused variable - zmienna która nigdy nie jest `__________`
- Too complex method - metoda z wysoką złożonością `__________`
- Empty catch block - puste `__________` - połykanie wyjątków
- printStackTrace() - użycie `__________` zamiast loggera
- Duplicate code - zduplikowany kod (naruszenie zasady `__________`)
- Too many parameters - metoda z więcej niż __________ parametrami

---

## 👀 Demo - Obserwacje

**Trener uruchamia SonarLint na klasie `CodeWithIssues.java`**

Podczas demonstracji odpowiedz na pytania:

### Pytanie 1: Jak uruchomić SonarLint w IntelliJ?
**Odpowiedź:**
```
View → Tool Windows → __________
```

### Pytanie 2: Ile problemów wykrył SonarLint w klasie CodeWithIssues?
**Odpowiedź:**
- Bugs (🐛): __________
- Vulnerabilities (🔓): __________
- Code Smells (🦨): __________

### Pytanie 3: Jaki problem wykryto w metodzie `processData()`?
**Linia:** __________
**Problem:** __________
**Dlaczego to problem?** __________

### Pytanie 4: Jaki problem bezpieczeństwa wykryto w polu `DB_PASSWORD`?
**Linia:** __________
**Problem:** __________
**Jak to naprawić?** __________

### Pytanie 5: Jaki problem wykryto w metodzie `resourceLeak()`?
**Linia:** __________
**Problem:** __________
**Jak to naprawić?** __________

### Pytanie 6: Jaki problem wykryto w metodzie `sqlInjection()`?
**Linia:** __________
**Problem:** __________
**Jak to naprawić?** __________

### Pytanie 7: Jaki problem wykryto w metodzie `calculate()`?
**Linia:** __________
**Problem:** __________
**Jak to naprawić?** __________

### Pytanie 8: Jakie Code Smells wykryto w metodzie `tooComplex()`?
**Problem:** __________
**Dlaczego to Code Smell?** __________

### Pytanie 9: Jaki problem wykryto w metodach `duplicateCode1()` i `duplicateCode2()`?
**Problem:** __________
**Jak to naprawić?** __________

### Pytanie 10: Czy SonarLint pokazuje jak naprawić problemy?
**Odpowiedź:** TAK / NIE
**Jak zobaczyć sugestie?** __________

---

## 💻 Ćwiczenie 7.1: Identyfikacja problemów z SonarLint

**Cel:** Nauczyć się używać SonarLint do wykrywania problemów w kodzie.

**Zadanie:**
1. Otwórz klasę `CodeWithIssues.java` w IntelliJ
2. Uruchom SonarLint (View → Tool Windows → SonarLint)
3. Przeanalizuj wszystkie wykryte problemy
4. Wypełnij tabelę poniżej

**Tabela problemów:**

| Linia | Typ problemu | Opis problemu | Jak naprawić? |
|-------|--------------|---------------|---------------|
| 18    | 🦨 Code Smell | Unused private field | Usuń pole lub użyj go |
| 21    | 🔓 Vulnerability | __________ | __________ |
| 25    | 🐛 Bug | __________ | __________ |
| 28-32 | 🦨 Code Smell | __________ | __________ |
| 37-44 | 🐛 Bug | __________ | __________ |
| 43    | 🦨 Code Smell | __________ | __________ |
| 53    | 🔓 Vulnerability | __________ | __________ |
| 61    | 🐛 Bug | __________ | __________ |
| 66    | 🦨 Code Smell | __________ | __________ |
| 70-85 | 🦨 Code Smell | __________ | __________ |
| 88-90 | 🐛 Bug | __________ | __________ |
| 99    | 🦨 Code Smell | __________ | __________ |
| 102-117 | 🦨 Code Smell | __________ | __________ |
| 120-123 | 🦨 Code Smell | __________ | __________ |

<details>
<summary>💡 Rozwiązanie</summary>

| Linia | Typ problemu | Opis problemu | Jak naprawić? |
|-------|--------------|---------------|---------------|
| 18    | 🦨 Code Smell | Unused private field | Usuń pole lub użyj go |
| 21    | 🔓 Vulnerability | Hardcoded password | Przenieś do zmiennych środowiskowych lub pliku konfiguracyjnego |
| 25    | 🐛 Bug | Null pointer dereference | Dodaj sprawdzenie `if (input != null)` |
| 28-32 | 🦨 Code Smell | Empty catch block | Zaloguj wyjątek lub obsłuż go odpowiednio |
| 37-44 | 🐛 Bug | Resource leak - Connection i Statement nie są zamykane | Użyj try-with-resources |
| 43    | 🦨 Code Smell | printStackTrace() zamiast loggera | Użyj loggera (SLF4J, Log4j) |
| 53    | 🔓 Vulnerability | SQL Injection - konkatenacja stringa | Użyj PreparedStatement z parametrami |
| 61    | 🐛 Bug | Division by zero możliwe | Dodaj sprawdzenie `if (b != 0)` |
| 66    | 🦨 Code Smell | Unused local variable | Usuń zmienną |
| 70-85 | 🦨 Code Smell | Cognitive complexity too high - zagnieżdżone if'y | Użyj early return lub guard clauses |
| 88-90 | 🐛 Bug | equals() bez hashCode() | Dodaj metodę hashCode() |
| 99    | 🦨 Code Smell | Returning null | Zwróć Optional lub pusty String |
| 102-117 | 🦨 Code Smell | Duplicate code | Wydziel wspólny kod do osobnej metody |
| 120-123 | 🦨 Code Smell | Too many parameters (8) | Użyj obiektu parametrów lub Builder pattern |

</details>

---

## 💻 Ćwiczenie 7.2: Naprawa wybranych problemów

**Cel:** Nauczyć się naprawiać problemy wykryte przez SonarLint.

**Zadanie:**
Napraw poniższe problemy z klasy `CodeWithIssues.java`:

### Problem 1: Null pointer dereference (linia 25)

**Kod z problemem:**
```java
public void processData(String input) {
    System.out.println(input.length());  // ❌ input może być null!
}
```

**TODO: Napraw kod poniżej**
```java
public void processData(String input) {
    // TODO: Dodaj sprawdzenie czy input nie jest null



}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public void processData(String input) {
    if (input == null) {
        System.out.println("Input is null");
        return;
    }
    System.out.println(input.length());
}

// Lub z Optional:
public void processData(String input) {
    Optional.ofNullable(input)
            .ifPresent(s -> System.out.println(s.length()));
}
```

</details>

### Problem 2: Resource leak (linia 37-44)

**Kod z problemem:**
```java
public void resourceLeak() {
    try {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
        Statement stmt = conn.createStatement();
        stmt.execute("SELECT 1");
        // ❌ Brak close() - wyciek zasobów!
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

**TODO: Napraw kod używając try-with-resources**
```java
public void resourceLeak() {
    // TODO: Użyj try-with-resources






}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public void resourceLeak() {
    try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
         Statement stmt = conn.createStatement()) {

        stmt.execute("SELECT 1");

    } catch (Exception e) {
        // Użyj loggera zamiast printStackTrace()
        logger.error("Database error", e);
    }
}
```

**Wyjaśnienie:**
- `try-with-resources` automatycznie zamyka zasoby implementujące `AutoCloseable`
- Connection i Statement są zamykane automatycznie po wyjściu z bloku try
- Nawet jeśli wystąpi wyjątek, zasoby zostaną zamknięte

</details>

### Problem 3: SQL Injection (linia 53)

**Kod z problemem:**
```java
public void sqlInjection(String userInput) {
    try {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
        Statement stmt = conn.createStatement();
        // ❌ SQL Injection!
        stmt.execute("SELECT * FROM users WHERE name = '" + userInput + "'");
    } catch (Exception e) {
        // Swallowing exception
    }
}
```

**TODO: Napraw kod używając PreparedStatement**
```java
public void sqlInjection(String userInput) {
    // TODO: Użyj PreparedStatement z parametrami







}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
public void sqlInjection(String userInput) {
    String sql = "SELECT * FROM users WHERE name = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, userInput);  // ✅ Bezpieczne - parametr jest escapowany
        pstmt.execute();

    } catch (Exception e) {
        logger.error("Database error", e);
    }
}
```

**Wyjaśnienie:**
- `PreparedStatement` automatycznie escapuje parametry
- Nawet jeśli userInput = `"'; DROP TABLE users; --"`, zostanie potraktowane jako zwykły string
- Znak `?` to placeholder na parametr
- `setString(1, userInput)` ustawia pierwszy parametr

</details>

### Problem 4: Division by zero (linia 61)

**Kod z problemem:**
```java
public int calculate(int a, int b) {
    return a / b;  // ❌ b może być 0!
}
```

**TODO: Napraw kod**
```java
public int calculate(int a, int b) {
    // TODO: Dodaj sprawdzenie czy b != 0



}
```

<details>
<summary>💡 Rozwiązanie</summary>

```java
// Opcja 1: Rzuć wyjątek
public int calculate(int a, int b) {
    if (b == 0) {
        throw new IllegalArgumentException("Division by zero!");
    }
    return a / b;
}

// Opcja 2: Zwróć Optional
public Optional<Integer> calculate(int a, int b) {
    if (b == 0) {
        return Optional.empty();
    }
    return Optional.of(a / b);
}

// Opcja 3: Zwróć wartość domyślną
public int calculate(int a, int b) {
    return (b == 0) ? 0 : a / b;
}
```

</details>

---

## ✅ CHECKPOINT 7

Zaznacz co już umiesz:

- [ ] Wiem czym jest SonarLint i do czego służy
- [ ] Potrafię zainstalować SonarLint w IntelliJ
- [ ] Rozumiem różnicę między Bug, Vulnerability i Code Smell
- [ ] Potrafię uruchomić SonarLint i przeanalizować wyniki
- [ ] Potrafię naprawić null pointer dereference
- [ ] Potrafię naprawić resource leak używając try-with-resources
- [ ] Potrafię zapobiec SQL Injection używając PreparedStatement
- [ ] Rozumiem dlaczego printStackTrace() to Code Smell
- [ ] Potrafię używać SonarLint do poprawy jakości kodu

---

## 📌 Pytania do trenera

Zapisz tutaj pytania które chcesz zadać trenerowi:

1. _______________________________________________
2. _______________________________________________
3. _______________________________________________

---



---

# 🤖 BLOK 8: AI dla Programisty + CI/CD (15:45-16:30)

## 📝 Notatki z teorii

### AI dla Junior Developera

**AI może pomóc w:**

| Przypadek użycia | Przykład |
|------------------|----------|
| **Zrozumienie kodu** | "Co robi ta metoda?" |
| **Debugowanie** | "Dlaczego dostaję NullPointerException?" |
| **Refaktoring** | "Jak uprościć tę metodę?" |
| **Dokumentacja** | "Wygeneruj JavaDoc dla tej klasy" |
| **Nauka** | "Wyjaśnij czym jest Stream API" |
| **Code Review** | "Jakie problemy widzisz w tym kodzie?" |
| **Testy** | "Napisz testy jednostkowe dla tej metody" |

### Dobre praktyki z AI

✅ **CO ROBIĆ:**
1. **Bądź konkretny** - im więcej kontekstu, tym lepsza odpowiedź
2. **Weryfikuj odpowiedzi** - AI może się mylić, zawsze sprawdzaj kod
3. **Ucz się z odpowiedzi** - nie kopiuj ślepo, zrozum co robi kod
4. **Iteruj** - jeśli odpowiedź nie jest dobra, doprecyzuj pytanie
5. **Używaj do nauki** - pytaj "dlaczego?" i "jak to działa?"

❌ **CZEGO UNIKAĆ:**
1. **Nie ufaj ślepo** - AI może generować błędny kod
2. **Nie kopiuj bezmyślnie** - zrozum co wklejasz do projektu
3. **Nie używaj do omijania nauki** - AI to narzędzie, nie zastępstwo dla wiedzy
4. **Nie wklejaj wrażliwych danych** - hasła, tokeny, dane osobowe

### Przykładowe prompty dla AI

**Analiza kodu:**
```
Przeanalizuj tę metodę i powiedz:
1. Co ona robi?
2. Jakie są potencjalne problemy?
3. Jak można ją ulepszyć?

[wklej kod]
```

**Debugowanie:**
```
Dostaję błąd: [treść błędu]

Kod:
[wklej kod]

Co może być przyczyną i jak to naprawić?
```

**Refaktoring:**
```
Ten kod działa, ale jest nieczytelny:
[wklej kod]

Jak można go zrefaktorować zgodnie z zasadami SOLID?
```

**Nauka:**
```
Wyjaśnij mi jak działa [koncept] w Javie.
Podaj prosty przykład kodu i wyjaśnij krok po kroku.
```

---

### CI/CD - Continuous Integration / Continuous Deployment

**Czym jest CI/CD?**

CI/CD to praktyka automatyzacji procesu budowania, testowania i wdrażania aplikacji.

**Flow CI/CD:**
```
PUSH → BUILD → TEST → DEPLOY
```

1. **Developer pushuje kod** do repozytorium (GitHub, GitLab)
2. **Automatyczny build** - kompilacja projektu
3. **Automatyczne testy** - uruchomienie testów jednostkowych i integracyjnych
4. **Deployment** - wdrożenie na serwer (jeśli testy przeszły)

**Korzyści:**
- ✅ Automatyczne budowanie przy każdym pushu
- ✅ Automatyczne testy - błędy wykrywane od razu
- ✅ Szybkie wykrywanie błędów
- ✅ Pewność że kod działa przed merge'em

### GitHub Actions - przykład

**Plik `.github/workflows/build.yml`:**

```yaml
name: Java CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'

    - name: Build with Maven
      run: mvn clean install

    - name: Run tests
      run: mvn test
```

**Co się dzieje:**
1. Przy każdym pushu na `main` lub `develop` uruchamia się workflow
2. GitHub tworzy maszynę wirtualną z Ubuntu
3. Instaluje Javę 21
4. Buduje projekt (`mvn clean install`)
5. Uruchamia testy (`mvn test`)
6. Jeśli coś się nie powiedzie - dostaniesz email z błędem

---

## 👀 Demo - Obserwacje

**Trener pokazuje:**
1. Jak używać AI do analizy kodu
2. Jak skonfigurować GitHub Actions

Podczas demonstracji odpowiedz na pytania:

### Pytanie 1: Jaki prompt użył trener do analizy kodu?
**Odpowiedź:**
```
__________________________________________
__________________________________________
```

### Pytanie 2: Jakie problemy wykryło AI w kodzie?
**Odpowiedź:**
1. __________________________________________
2. __________________________________________
3. __________________________________________

### Pytanie 3: Czy AI zaproponowało poprawki?
**Odpowiedź:** TAK / NIE
**Jakie?** __________________________________________

### Pytanie 4: Gdzie znajduje się plik konfiguracyjny GitHub Actions?
**Odpowiedź:**
```
__________________________________________
```

### Pytanie 5: Co się dzieje gdy testy nie przejdą w CI/CD?
**Odpowiedź:**
```
__________________________________________
__________________________________________
```

---

## 💻 Ćwiczenie 8.1: Napisz efektywne prompty dla AI

**Cel:** Nauczyć się pisać dobre prompty do AI.

**Zadanie:**
Dla każdego scenariusza napisz dobry prompt do AI.

### Scenariusz 1: Zrozumienie kodu
Masz metodę, której nie rozumiesz. Napisz prompt do AI:

**Twój prompt:**
```
__________________________________________
__________________________________________
__________________________________________
```

<details>
<summary>💡 Przykładowy dobry prompt</summary>

```
Przeanalizuj tę metodę i wyjaśnij:
1. Co ona robi krok po kroku?
2. Jakie są parametry wejściowe i co zwraca?
3. Czy są jakieś potencjalne problemy?

public List<Pirate> findActivePirates(String shipName) {
    return pirateRepository.findAll().stream()
        .filter(p -> p.getShip() != null)
        .filter(p -> p.getShip().getName().equals(shipName))
        .filter(p -> p.isActive())
        .collect(Collectors.toList());
}
```

</details>

### Scenariusz 2: Debugowanie błędu
Dostajesz `NullPointerException`. Napisz prompt do AI:

**Twój prompt:**
```
__________________________________________
__________________________________________
__________________________________________
```

<details>
<summary>💡 Przykładowy dobry prompt</summary>

```
Dostaję błąd:
Exception in thread "main" java.lang.NullPointerException
    at pl.training.PirateService.findByShip(PirateService.java:25)

Kod metody (linia 25 to filter):
public List<Pirate> findByShip(String shipName) {
    return pirates.stream()
        .filter(p -> p.getShip().getName().equals(shipName))  // linia 25
        .collect(Collectors.toList());
}

Co może być przyczyną i jak to naprawić?
```

</details>

### Scenariusz 3: Refaktoring
Masz długą metodę z wieloma if'ami. Napisz prompt do AI:

**Twój prompt:**
```
__________________________________________
__________________________________________
__________________________________________
```

<details>
<summary>💡 Przykładowy dobry prompt</summary>

```
Ten kod działa, ale jest nieczytelny i ma wysoką złożoność cyklomatyczną:

public String getPirateRank(Pirate pirate) {
    if (pirate.getYearsOfService() > 20) {
        if (pirate.getTreasuresFound() > 100) {
            return "Legendary Captain";
        } else if (pirate.getTreasuresFound() > 50) {
            return "Veteran Captain";
        } else {
            return "Captain";
        }
    } else if (pirate.getYearsOfService() > 10) {
        if (pirate.getTreasuresFound() > 50) {
            return "First Mate";
        } else {
            return "Sailor";
        }
    } else {
        return "Deckhand";
    }
}

Jak zrefaktorować ten kod aby był:
1. Bardziej czytelny
2. Łatwiejszy w utrzymaniu
3. Zgodny z zasadami SOLID
```

</details>

---

## 💻 Ćwiczenie 8.2: Zrozumienie CI/CD

**Cel:** Zrozumieć jak działa CI/CD pipeline.

**Zadanie:**
Przeanalizuj poniższy plik GitHub Actions i odpowiedz na pytania.

```yaml
name: Java CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'

    - name: Build with Maven
      run: mvn clean install

    - name: Run tests
      run: mvn test

    - name: Check code quality
      run: mvn sonar:sonar
```

### Pytanie 1: Kiedy uruchamia się ten workflow?
**Odpowiedź:**
```
__________________________________________
```

<details>
<summary>💡 Rozwiązanie</summary>

Workflow uruchamia się:
1. Przy każdym pushu na branch `main` lub `develop`
2. Przy każdym pull requeście do brancha `main`

</details>

### Pytanie 2: Na jakim systemie operacyjnym uruchamia się build?
**Odpowiedź:**
```
__________________________________________
```

<details>
<summary>💡 Rozwiązanie</summary>

Ubuntu (najnowsza wersja) - `runs-on: ubuntu-latest`

</details>

### Pytanie 3: Jakie kroki wykonuje pipeline?
**Odpowiedź:**
1. __________________________________________
2. __________________________________________
3. __________________________________________
4. __________________________________________
5. __________________________________________

<details>
<summary>💡 Rozwiązanie</summary>

1. Checkout kodu z repozytorium (`actions/checkout@v3`)
2. Instalacja JDK 21 (`actions/setup-java@v3`)
3. Build projektu (`mvn clean install`)
4. Uruchomienie testów (`mvn test`)
5. Analiza jakości kodu (`mvn sonar:sonar`)

</details>

### Pytanie 4: Co się stanie jeśli testy nie przejdą?
**Odpowiedź:**
```
__________________________________________
```

<details>
<summary>💡 Rozwiązanie</summary>

Pipeline zakończy się błędem (fail) i:
- Nie wykona kolejnych kroków (sonar:sonar)
- Developer dostanie powiadomienie o błędzie
- Pull request będzie oznaczony jako "failing"
- Kod nie powinien być zmergowany do main

</details>

---

## ✅ CHECKPOINT 8

Zaznacz co już umiesz:

- [ ] Rozumiem jak AI może pomóc w codziennej pracy programisty
- [ ] Potrafię napisać dobry prompt do AI (konkretny, z kontekstem)
- [ ] Wiem jakich praktyk unikać przy pracy z AI
- [ ] Rozumiem czym jest CI/CD
- [ ] Rozumiem flow: PUSH → BUILD → TEST → DEPLOY
- [ ] Wiem czym jest GitHub Actions
- [ ] Potrafię odczytać podstawowy plik workflow GitHub Actions
- [ ] Rozumiem korzyści z automatyzacji testów w CI/CD

---

## 📌 Pytania do trenera

Zapisz tutaj pytania które chcesz zadać trenerowi:

1. _______________________________________________
2. _______________________________________________
3. _______________________________________________

---
