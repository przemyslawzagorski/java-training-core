# Moduł 03: Refactoring & SOLID

## 🎯 Cel modułu
Rozpoznawanie "zapachów kodu" i stosowanie zasad SOLID w praktyce.

> **Motto:** "Najpierw zrób żeby działało, potem zrób dobrze."

---

## 📁 Struktura modułu

```
m03-refactoring/
├── src/main/java/pl/przemekzagorski/training/refactoring/
│   ├── smells/
│   │   ├── before/
│   │   │   └── BadPirateService.java    # Zły kod 😱
│   │   └── after/
│   │       └── GoodBattleService.java   # Poprawiony kod ✅
│   ├── solid/
│   │   └── SolidDemo.java               # Przykłady SOLID
│   ├── RefactoringExercises.java        # 📝 Ćwiczenia
│   └── RefactoringExercisesSolutions.java # ✅ Rozwiązania
└── src/test/java/
    └── RefactoringTest.java              # Testy jednostkowe
```

---

## 🦨 Code Smells (Zapachy kodu)

### Najczęstsze problemy

| Smell | Opis | Rozwiązanie | Przykład |
|-------|------|-------------|----------|
| **Long Method** | Metoda > 20 linii | Extract Method | Podziel na mniejsze metody |
| **God Class** | Klasa wie wszystko | Split Class | Wydziel odpowiedzialności |
| **Feature Envy** | Używa danych innej klasy | Move Method | Przenieś do właściwej klasy |
| **Duplicate Code** | Kopiuj-wklej | Extract Method | DRY! |
| **Magic Numbers** | Liczby bez nazwy | Extract Constant | `DAMAGE_PER_CANNON = 10` |
| **Long Parameter List** | Wiele parametrów | Introduce Object | `BattleConfig config` |

---

### Przykład: Long Method → Extract Method

```java
// ❌ PRZED: Jedna długa metoda
public void processBattle(Ship ship1, Ship ship2) {
    // 1. Oblicz obrażenia (10 linii)
    int damage1 = ship1.cannons * 10;
    if (ship1.crew > 50) damage1 += 20;
    if (ship1.crew > 100) damage1 += 30;
    // ... więcej logiki ...
    
    // 2. Określ zwycięzcę (10 linii)
    String winner = ...
    
    // 3. Wyświetl raport (10 linii)
    System.out.println(...);
    
    // 4. Zapisz do bazy (5 linii)
    // 5. Wyślij powiadomienie (5 linii)
}

// ✅ PO: Wyodrębnione metody
public void processBattle(Ship ship1, Ship ship2) {
    int damage1 = calculateDamage(ship1);
    int damage2 = calculateDamage(ship2);
    String winner = determineWinner(ship1, damage2, ship2, damage1);
    printReport(ship1, ship2, winner);
    saveToDatabase(winner);
    sendNotification(winner);
}

private int calculateDamage(Ship ship) { ... }
private String determineWinner(...) { ... }
private void printReport(...) { ... }
```

---

### Przykład: Magic Numbers → Named Constants

```java
// ❌ PRZED: Co oznaczają te liczby?
int damage = cannons * 10;
if (crew > 50) damage += 20;
if (crew > 100) damage += 30;

// ✅ PO: Samodokumentujący się kod
private static final int DAMAGE_PER_CANNON = 10;
private static final int MEDIUM_CREW_THRESHOLD = 50;
private static final int LARGE_CREW_THRESHOLD = 100;
private static final int MEDIUM_CREW_BONUS = 20;
private static final int LARGE_CREW_BONUS = 30;

int damage = cannons * DAMAGE_PER_CANNON;
if (crew > MEDIUM_CREW_THRESHOLD) damage += MEDIUM_CREW_BONUS;
if (crew > LARGE_CREW_THRESHOLD) damage += LARGE_CREW_BONUS;
```

---

## 🏗️ SOLID w praktyce

### S - Single Responsibility Principle

> Klasa powinna mieć tylko JEDEN powód do zmiany.

```java
// ❌ PRZED: God Class
class PirateManager {
    void calculateDamage() { ... }
    void saveToDatabase() { ... }
    void sendEmail() { ... }
    void generateReport() { ... }
}

// ✅ PO: Każda klasa = jedna odpowiedzialność
class DamageCalculator { void calculate() { ... } }
class BattleRepository { void save() { ... } }
class NotificationService { void notify() { ... } }
class BattleReporter { void generateReport() { ... } }
```

---

### O - Open/Closed Principle

> Otwarte na rozszerzenia, zamknięte na modyfikacje.

```java
// ❌ PRZED: Dodanie nowej roli = modyfikacja kodu
void hire(String role) {
    if (role.equals("captain")) { ... }
    else if (role.equals("navigator")) { ... }
    else if (role.equals("cook")) { ... }
    // Dodanie "surgeon" wymaga modyfikacji!
}

// ✅ PO: Dodanie nowej roli = nowa klasa
interface HiringStrategy { void hire(String name); }
class CaptainHiring implements HiringStrategy { ... }
class NavigatorHiring implements HiringStrategy { ... }
class SurgeonHiring implements HiringStrategy { ... } // Nowa rola bez modyfikacji!

Map<String, HiringStrategy> strategies = Map.of(
    "captain", new CaptainHiring(),
    "surgeon", new SurgeonHiring()
);
```

---

### L - Liskov Substitution Principle

> Podklasa może zastąpić klasę bazową bez zmiany zachowania.

```java
// ❌ PRZED: Pingwin nie może latać!
class Bird { void fly() { ... } }
class Penguin extends Bird { 
    void fly() { throw new UnsupportedOperationException(); } 
}

// ✅ PO: Rozdziel interfejsy
interface Bird { void eat(); }
interface FlyingBird extends Bird { void fly(); }

class Sparrow implements FlyingBird { ... }
class Penguin implements Bird { ... }  // Nie ma fly()!
```

---

### I - Interface Segregation Principle

> Małe, specjalizowane interfejsy zamiast jednego dużego.

```java
// ❌ PRZED: "Fat interface"
interface Ship {
    void sail();
    void fire();
    void repair();
    void cook();
    void navigate();
}

// ✅ PO: Segregacja
interface Saileable { void sail(); }
interface Armed { void fire(); }
interface Repairable { void repair(); }

class Warship implements Saileable, Armed, Repairable { ... }
class Raft implements Saileable { ... }  // Tylko to co potrzebuje!
```

---

### D - Dependency Inversion Principle

> Zależności od abstrakcji, nie od konkretnych klas.

```java
// ❌ PRZED: Tight coupling
class BattleService {
    private MySqlRepository repo = new MySqlRepository();
    private EmailService email = new EmailService();
}

// ✅ PO: Constructor Injection
class BattleService {
    private final BattleRepository repo;      // Interfejs!
    private final NotificationService notif;  // Interfejs!
    
    public BattleService(BattleRepository repo, NotificationService notif) {
        this.repo = repo;
        this.notif = notif;
    }
}

// Łatwe testowanie z mockami!
BattleService service = new BattleService(mockRepo, mockNotif);
```

---

## 📝 Ćwiczenia (30 min)

Otwórz `RefactoringExercises.java`:

| # | Ćwiczenie | Czas | Technika |
|---|-----------|------|----------|
| 1 | Extract Method | 5 min | Rozbij długą metodę |
| 2 | Extract Class | 7 min | Usuń God Class |
| 3 | Replace Magic Numbers | 5 min | Zamień na stałe |
| 4 | Dependency Inversion | 8 min | Interfejsy + DI |
| 5 | Code Review | 5 min | Znajdź smells! |

**Rozwiązania:** `RefactoringExercisesSolutions.java`

---

## 🎯 Skróty IDE do refaktoringu

| Akcja | IntelliJ IDEA | VS Code |
|-------|---------------|---------|
| Extract Method | `Ctrl+Alt+M` | `Ctrl+Shift+R` |
| Extract Variable | `Ctrl+Alt+V` | `Ctrl+Alt+V` |
| Extract Constant | `Ctrl+Alt+C` | - |
| Rename | `Shift+F6` | `F2` |
| Inline | `Ctrl+Alt+N` | - |
| Move | `F6` | - |

---

## 🧪 Testy

```bash
cd m03-refactoring
mvn test

# Testy sprawdzają:
# - calculateDamage() (wyodrębniona metoda)
# - determineWinner() (wyodrębniona metoda)
# - DamageCalculator (extract class)
# - Ship record (immutability)
# - DIP (mockowanie)
```

---

## 📊 Diagram: Proces refaktoringu

```
┌─────────────────────────────────────────────────────────────┐
│                   PROCES REFAKTORINGU                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. IDENTYFIKUJ CODE SMELLS                                  │
│     │                                                        │
│     ▼                                                        │
│  2. NAPISZ TESTY (jeśli ich nie ma!)                        │
│     │                                                        │
│     ▼                                                        │
│  3. ZASTOSUJ REFACTORING                                     │
│     ├── Extract Method                                       │
│     ├── Extract Class                                        │
│     ├── Replace Magic Numbers                                │
│     └── Apply SOLID                                          │
│     │                                                        │
│     ▼                                                        │
│  4. URUCHOM TESTY (red → green)                             │
│     │                                                        │
│     ▼                                                        │
│  5. POWTÓRZ                                                  │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔗 Powiązane moduły

- **Dzień 2, m02** - Wzorce projektowe często wynikają z refaktoringu
- **Dzień 2, m04** - AI może pomóc w identyfikacji smells

---

## 📖 Dodatkowe materiały

- [Refactoring.Guru](https://refactoring.guru/refactoring)
- [Martin Fowler - Refactoring](https://martinfowler.com/books/refactoring.html)
- [Clean Code - Robert C. Martin](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)

