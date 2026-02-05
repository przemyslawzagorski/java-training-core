# Wzorce projektowe (Design Patterns)

---

## Czym są wzorce projektowe?

**Sprawdzone rozwiązania typowych problemów**
- Katalog 23 wzorców (Gang of Four, 1994)
- 3 kategorie: Creational, Structural, Behavioral
- Wspólny język dla programistów

**Korzyści:**
- Kod łatwiejszy do zrozumienia
- Kod łatwiejszy do utrzymania
- Unikanie typowych błędów

---

## Singleton - jedna instancja klasy

**Problem:** Potrzebujemy dokładnie jednej instancji klasy (np. konfiguracja, logger)

**Rozwiązanie - Enum (najlepsze):**
```java
public enum DatabaseConnection {
    INSTANCE;
    
    private Connection connection;
    
    public Connection getConnection() {
        if (connection == null) {
            connection = DriverManager.getConnection("...");
        }
        return connection;
    }
}

// Użycie
DatabaseConnection.INSTANCE.getConnection();
```

**Dlaczego Enum?**
- Thread-safe (gwarantowane przez JVM)
- Serializacja bezpieczna
- Nie można utworzyć drugiej instancji (nawet przez reflection)

**Klasyczny Singleton (gorszy):**
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {}
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
```

---

## Factory - tworzenie obiektów

**Problem:** Tworzenie obiektów zależy od warunków (if/switch)

**Rozwiązanie - Factory:**
```java
public interface Weapon {
    void attack();
}

public class WeaponFactory {
    public static Weapon createWeapon(String type) {
        return switch (type) {
            case "SWORD" -> new Sword();
            case "PISTOL" -> new Pistol();
            case "CANNON" -> new Cannon();
            default -> throw new IllegalArgumentException("Unknown weapon: " + type);
        };
    }
}

// Użycie
Weapon weapon = WeaponFactory.createWeapon("SWORD");
weapon.attack();
```

**Korzyści:**
- Centralizacja logiki tworzenia
- Łatwe dodanie nowych typów
- Klient nie zna konkretnych klas

---

## Builder - budowanie złożonych obiektów

**Problem:** Konstruktor z wieloma parametrami (trudny do użycia)

**Rozwiązanie - Builder:**
```java
public class Ship {
    private String name;
    private int cannons;
    private int crew;
    private String captain;
    
    private Ship(Builder builder) {
        this.name = builder.name;
        this.cannons = builder.cannons;
        this.crew = builder.crew;
        this.captain = builder.captain;
    }
    
    public static class Builder {
        private String name;
        private int cannons;
        private int crew;
        private String captain;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder cannons(int cannons) {
            this.cannons = cannons;
            return this;
        }
        
        public Builder crew(int crew) {
            this.crew = crew;
            return this;
        }
        
        public Builder captain(String captain) {
            this.captain = captain;
            return this;
        }
        
        public Ship build() {
            return new Ship(this);
        }
    }
}

// Użycie
Ship ship = new Ship.Builder()
    .name("Black Pearl")
    .cannons(32)
    .crew(100)
    .captain("Jack Sparrow")
    .build();
```

**Korzyści:**
- Czytelne (fluent API)
- Opcjonalne parametry
- Walidacja w build()

---

## Strategy - wymienne algorytmy

**Problem:** Różne algorytmy dla tej samej operacji (if/switch)

**Rozwiązanie - Strategy:**
```java
public interface PaymentStrategy {
    void pay(int amount);
}

public class CreditCardPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid " + amount + " with credit card");
    }
}

public class CashPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid " + amount + " with cash");
    }
}

public class PaymentProcessor {
    private PaymentStrategy strategy;
    
    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void processPayment(int amount) {
        strategy.pay(amount);
    }
}

// Użycie
PaymentProcessor processor = new PaymentProcessor();
processor.setStrategy(new CreditCardPayment());
processor.processPayment(100);
```

**Korzyści:**
- Wymienne algorytmy w runtime
- Open/Closed Principle (dodanie nowej strategii bez zmiany kodu)

---

## Decorator - dynamiczne dodawanie funkcjonalności

**Problem:** Potrzebujemy różnych kombinacji funkcjonalności

**Rozwiązanie - Decorator:**
```java
public interface Coffee {
    String getDescription();
    double getCost();
}

public class SimpleCoffee implements Coffee {
    public String getDescription() { return "Simple coffee"; }
    public double getCost() { return 5.0; }
}

public class MilkDecorator implements Coffee {
    private Coffee coffee;
    
    public MilkDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
    
    public String getDescription() {
        return coffee.getDescription() + ", milk";
    }
    
    public double getCost() {
        return coffee.getCost() + 1.5;
    }
}

// Użycie
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
System.out.println(coffee.getDescription() + " = " + coffee.getCost());
// "Simple coffee, milk, sugar = 7.0"
```

---

## Observer - powiadomienia o zmianach

**Problem:** Wiele obiektów musi reagować na zmiany w innym obiekcie

**Rozwiązanie - Observer:**
```java
public interface Observer {
    void update(String event);
}

public class Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void notifyObservers(String event) {
        for (Observer observer : observers) {
            observer.update(event);
        }
    }
}

// Użycie
Subject ship = new Subject();
ship.attach(event -> System.out.println("Logger: " + event));
ship.attach(event -> System.out.println("Alert: " + event));
ship.notifyObservers("Ship under attack!");
```

---

## Wskazówka dla trenera
**Czas:** 30 minut

**Co mówię:**
- "Wzorce projektowe to sprawdzone rozwiązania typowych problemów."
- "Singleton - jedna instancja (Enum najlepsze). Factory - tworzenie obiektów."
- "Builder - czytelne budowanie złożonych obiektów (fluent API)."
- "Strategy - wymienne algorytmy. Decorator - dynamiczne dodawanie funkcjonalności."
- "Observer - powiadomienia o zmianach (pub/sub)."

**Co pokazuję:**
- `PatternDemo.java` - wszystkie 6 wzorców
- Uruchamiam demo, pokazuję jak działają
- Pokazuję przykłady z JDK (Collections.sort - Strategy, InputStream - Decorator)

**Ćwiczenia:**
- "Macie 5 ćwiczeń wzorców (m02-design-patterns/PatternExercises.java)"
- "Exercises 1-5: Singleton, Factory, Builder, Strategy, Quiz"
- "40 minut na Exercises 1-4"

**Następny krok:** Po ćwiczeniach → Slajd `03-refactoring.md`

