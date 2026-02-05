# Refactoring i SOLID

---

## Czym jest refactoring?

**Zmiana struktury kodu bez zmiany funkcjonalności**

**Cele:**
- Poprawa czytelności
- Redukcja złożoności
- Ułatwienie utrzymania
- Przygotowanie do nowych funkcji

**Zasada:** Zawsze miej testy przed refactoringiem!

---

## Code Smells - zapachy kodu

**Long Method** - metoda za długa (>20 linii)
```java
// ❌ Źle
public void processOrder(Order order) {
    // 100 linii kodu...
}

// ✅ Dobrze
public void processOrder(Order order) {
    validateOrder(order);
    calculateTotal(order);
    applyDiscount(order);
    sendConfirmation(order);
}
```

**God Class** - klasa robi za dużo (>500 linii)
```java
// ❌ Źle
public class OrderManager {
    // Walidacja, kalkulacje, email, baza danych, logowanie...
}

// ✅ Dobrze
public class OrderValidator { }
public class OrderCalculator { }
public class EmailService { }
public class OrderRepository { }
```

**Feature Envy** - metoda używa więcej danych z innej klasy
```java
// ❌ Źle
public class Order {
    public double calculateShipping(Customer customer) {
        return customer.getAddress().getDistance() * 0.5;
    }
}

// ✅ Dobrze
public class Customer {
    public double calculateShipping() {
        return address.getDistance() * 0.5;
    }
}
```

**Duplicate Code** - powtarzający się kod
```java
// ❌ Źle
public void processA() {
    // 10 linii wspólnego kodu
    // 5 linii specyficznych dla A
}
public void processB() {
    // 10 linii wspólnego kodu (duplikat!)
    // 5 linii specyficznych dla B
}

// ✅ Dobrze
public void processA() {
    commonProcess();
    specificA();
}
public void processB() {
    commonProcess();
    specificB();
}
private void commonProcess() {
    // 10 linii wspólnego kodu
}
```

---

## SOLID - 5 zasad projektowania

**S - Single Responsibility Principle**
- Klasa powinna mieć tylko jeden powód do zmiany
- Jedna odpowiedzialność

```java
// ❌ Źle - 3 odpowiedzialności
public class User {
    public void save() { /* zapis do bazy */ }
    public void sendEmail() { /* wysyłka email */ }
    public void generateReport() { /* raport PDF */ }
}

// ✅ Dobrze - 1 odpowiedzialność na klasę
public class User { /* tylko dane */ }
public class UserRepository { /* zapis do bazy */ }
public class EmailService { /* wysyłka email */ }
public class ReportGenerator { /* raport PDF */ }
```

**O - Open/Closed Principle**
- Otwarty na rozszerzenia, zamknięty na modyfikacje

```java
// ❌ Źle - modyfikacja klasy przy dodaniu nowego typu
public class PaymentProcessor {
    public void process(String type) {
        if (type.equals("CARD")) { /* ... */ }
        else if (type.equals("CASH")) { /* ... */ }
        // Dodanie nowego typu wymaga modyfikacji!
    }
}

// ✅ Dobrze - rozszerzenie przez nową klasę
public interface Payment {
    void process();
}
public class CardPayment implements Payment { }
public class CashPayment implements Payment { }
// Dodanie nowego typu = nowa klasa (bez modyfikacji istniejących)
```

**L - Liskov Substitution Principle**
- Podklasa powinna być zamienna z klasą bazową

```java
// ❌ Źle - Square narusza kontrakt Rectangle
public class Rectangle {
    protected int width, height;
    public void setWidth(int w) { width = w; }
    public void setHeight(int h) { height = h; }
}
public class Square extends Rectangle {
    public void setWidth(int w) { width = height = w; }  // Naruszenie!
    public void setHeight(int h) { width = height = h; }  // Naruszenie!
}

// ✅ Dobrze - osobne klasy
public interface Shape {
    int getArea();
}
public class Rectangle implements Shape { }
public class Square implements Shape { }
```

**I - Interface Segregation Principle**
- Wiele małych interfejsów zamiast jednego dużego

```java
// ❌ Źle - duży interfejs
public interface Worker {
    void work();
    void eat();
    void sleep();
}
public class Robot implements Worker {
    public void work() { /* OK */ }
    public void eat() { /* Robot nie je! */ }
    public void sleep() { /* Robot nie śpi! */ }
}

// ✅ Dobrze - małe interfejsy
public interface Workable { void work(); }
public interface Eatable { void eat(); }
public interface Sleepable { void sleep(); }

public class Robot implements Workable { }
public class Human implements Workable, Eatable, Sleepable { }
```

**D - Dependency Inversion Principle**
- Zależność od abstrakcji, nie od konkretnych klas

```java
// ❌ Źle - zależność od konkretnej klasy
public class OrderService {
    private MySqlDatabase database = new MySqlDatabase();  // Konkretna klasa!
}

// ✅ Dobrze - zależność od interfejsu
public class OrderService {
    private Database database;  // Interfejs!
    
    public OrderService(Database database) {
        this.database = database;
    }
}
```

---

## Techniki refactoringu

**Extract Method** - wydziel metodę
```java
// ❌ Przed
public void printOwing() {
    printBanner();
    System.out.println("name: " + name);
    System.out.println("amount: " + getOutstanding());
}

// ✅ Po
public void printOwing() {
    printBanner();
    printDetails(getOutstanding());
}
private void printDetails(double outstanding) {
    System.out.println("name: " + name);
    System.out.println("amount: " + outstanding);
}
```

**Extract Class** - wydziel klasę
```java
// ❌ Przed
public class Person {
    private String name;
    private String officeAreaCode;
    private String officeNumber;
}

// ✅ Po
public class Person {
    private String name;
    private TelephoneNumber officeTelephone;
}
public class TelephoneNumber {
    private String areaCode;
    private String number;
}
```

**Remove Magic Numbers** - usuń magiczne liczby
```java
// ❌ Przed
if (age > 18) { /* ... */ }
if (price * 0.23 > 100) { /* ... */ }

// ✅ Po
private static final int ADULT_AGE = 18;
private static final double VAT_RATE = 0.23;

if (age > ADULT_AGE) { /* ... */ }
if (price * VAT_RATE > 100) { /* ... */ }
```

---

## Wskazówka dla trenera
**Czas:** 30 minut

**Co mówię:**
- "Refactoring = zmiana struktury bez zmiany funkcjonalności. Zawsze z testami!"
- "Code Smells: Long Method, God Class, Feature Envy, Duplicate Code."
- "SOLID to 5 zasad projektowania - Single Responsibility najważniejsza!"
- "Extract Method, Extract Class - najpopularniejsze techniki refactoringu."
- "Remove Magic Numbers - stałe zamiast literałów."

**Co pokazuję:**
- `RefactoringDemo.java` - before/after dla każdej techniki
- `SolidExamples.java` - przykłady naruszenia i zgodności z SOLID
- Uruchamiam testy - pokazuję że funkcjonalność się nie zmieniła

**Ćwiczenia:**
- "Macie 5 ćwiczeń refactoringu (m03-refactoring/RefactoringExercises.java)"
- "Exercises 1-3: Extract Method, Extract Class, Remove Magic Numbers"
- "Exercise 4: Dependency Inversion (SOLID)"
- "40 minut na Exercises 1-4"

**Następny krok:** Po ćwiczeniach → Slajd `04-tools-and-ai.md`

