# Moduł 05: Entity Lifecycle - Cykl życia encji JPA

## 🎯 Cel modułu

Zrozumienie cyklu życia encji JPA jest **kluczowe** dla poprawnej pracy z Hibernate/JPA.
To tutaj juniorzy najczęściej popełniają błędy prowadzące do:
- `LazyInitializationException`
- Niezapisanych zmian w bazie
- `DetachedEntityException`
- Nieoczekiwanych zachowań aplikacji

Po tym module będziesz:
- Rozumieć 4 stany encji i przejścia między nimi
- Wiedzieć dlaczego zmiany "magicznie" zapisują się w bazie
- Umieć debugować problemy z cyklem życia

---

## 📊 Diagram stanów encji

```
                    ┌──────────────────┐
         new()      │                  │
      ───────────►  │    TRANSIENT     │  ← Nowy obiekt Java
                    │     (NEW)        │     Nie jest w bazie
                    └────────┬─────────┘
                             │
                    persist()│
                             ▼
                    ┌──────────────────┐         find()
                    │                  │  ◄───────────────
                    │    MANAGED       │  ← Śledzony przez EntityManager
                    │   (PERSISTENT)   │     Zmiany synchronizowane z bazą
                    └────────┬─────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
      detach()/clear()       │         remove()
      close()                │              │
              ▼              │              ▼
     ┌──────────────┐        │     ┌──────────────────┐
     │              │        │     │                  │
     │   DETACHED   │        │     │    REMOVED       │
     │              │        │     │                  │
     └──────────────┘        │     └──────────────────┘
              │              │
       merge()│              │
              └──────────────┘
```

---

## 🔑 Cztery stany encji - szczegółowo

### 1. TRANSIENT (NEW) - "Nie znam Cię"

Encja właśnie utworzona przez `new`, nigdy nie była w bazie.

```java
// To jest obiekt TRANSIENT
Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("10000"));

System.out.println(jack.getId());     // null - nie ma ID
System.out.println(em.contains(jack)); // false - EntityManager go nie zna
```

**Cechy:**
- ❌ Nie ma ID (lub ma ręcznie ustawione, ale niezweryfikowane)
- ❌ Nie istnieje w bazie danych
- ❌ EntityManager nie wie o jego istnieniu
- ❌ Zmiany w obiekcie nie wpływają na bazę

**Jak wejść do tego stanu:**
- `new Entity()`

**Jak wyjść z tego stanu:**
- `em.persist(entity)` → MANAGED

---

### 2. MANAGED (PERSISTENT) - "Śledzę każdy Twój ruch"

Encja jest **zarządzana** przez EntityManager. To najważniejszy stan!

```java
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("10000"));
em.persist(jack);  // TRANSIENT → MANAGED

System.out.println(jack.getId());      // 1 - ID przypisane!
System.out.println(em.contains(jack)); // true - jest zarządzany

// MAGIA: Zmiany są automatycznie wykrywane!
jack.setBounty(new BigDecimal("25000"));  // Nie wołamy żadnego "update()"!

em.getTransaction().commit();
// Hibernate SAM wykryje zmianę i wykona UPDATE!
```

**Cechy:**
- ✅ Ma ID (przypisane przy persist lub pobrane z bazy)
- ✅ Istnieje w bazie danych
- ✅ EntityManager śledzi wszystkie zmiany ("dirty checking")
- ✅ Zmiany zapisują się przy commit() lub flush()

**Jak wejść do tego stanu:**
- `em.persist(transient)` - z TRANSIENT
- `em.find(Entity.class, id)` - z bazy
- `em.createQuery(...).getResultList()` - z bazy
- `em.merge(detached)` - z DETACHED (zwraca nową instancję MANAGED!)

**Jak wyjść z tego stanu:**
- `em.detach(entity)` → DETACHED
- `em.clear()` → wszystkie encje stają się DETACHED
- `em.close()` → wszystkie encje stają się DETACHED
- `em.remove(entity)` → REMOVED

---

### 3. DETACHED - "Kiedyś Cię znałem"

Encja była zarządzana, ale już nie jest. To się często zdarza!

```java
EntityManager em = emf.createEntityManager();
Pirate jack = em.find(Pirate.class, 1L);  // MANAGED
em.close();  // Teraz jack jest DETACHED!

// Obiekt nadal istnieje, można go używać...
System.out.println(jack.getName());  // OK

// ...ale zmiany NIE zapisują się do bazy!
jack.setBounty(new BigDecimal("999999"));  // Ta zmiana ZNIKA!

// Lazy loading też nie działa:
jack.getShip().getName();  // LazyInitializationException!
```

**Cechy:**
- ✅ Ma ID
- ✅ Istnieje w bazie (prawdopodobnie)
- ❌ EntityManager nie śledzi zmian
- ❌ Lazy loading nie działa!

**Jak wejść do tego stanu:**
- `em.detach(entity)` - jawne odłączenie
- `em.clear()` - odłączenie wszystkich
- `em.close()` - zamknięcie EntityManagera
- Serializacja/deserializacja (np. HTTP)

**Jak wyjść z tego stanu:**
- `em.merge(entity)` → zwraca NOWĄ instancję MANAGED

---

### 4. REMOVED - "Zostaniesz usunięty"

Encja oznaczona do usunięcia. DELETE wykona się przy commit().

```java
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

Pirate jack = em.find(Pirate.class, 1L);  // MANAGED
em.remove(jack);  // MANAGED → REMOVED

System.out.println(em.contains(jack)); // true! (nadal technicznie zarządzany)

em.getTransaction().commit();  // Tu wykonuje się DELETE
// Teraz jack jest DETACHED (ale wiersz nie istnieje w bazie)
```

**Cechy:**
- ✅ Jeszcze ma ID
- ✅ Jeszcze istnieje w bazie (do commit)
- ✅ EntityManager go zna (do usunięcia)
- 🔜 Zostanie usunięty przy commit/flush

**Jak wejść do tego stanu:**
- `em.remove(managed)` - tylko z MANAGED!

**⚠️ WAŻNE:** Nie można usunąć encji DETACHED!
```java
Pirate jack = new Pirate();
jack.setId(1L);
em.remove(jack);  // IllegalArgumentException! To jest TRANSIENT, nie MANAGED!
```

---

## 🔄 Dirty Checking - Magia aktualizacji

Hibernate automatycznie wykrywa zmiany w encjach MANAGED.

### Jak to działa?

```java
em.getTransaction().begin();

// 1. Hibernate pobiera encję i robi "snapshot" jej stanu
Pirate jack = em.find(Pirate.class, 1L);
// Hibernate zapamiętuje: {name="Jack", bounty=10000}

// 2. Modyfikujesz encję normalnym setterem
jack.setBounty(new BigDecimal("25000"));
// Stan encji: {name="Jack", bounty=25000}
// Snapshot wciąż: {name="Jack", bounty=10000}

// 3. Przy commit() Hibernate porównuje stan z snapshot
em.getTransaction().commit();
// Różnica wykryta! → UPDATE pirates SET bounty=25000 WHERE id=1
```

### Kiedy NIE działa dirty checking?

1. **Encja DETACHED** - nie jest śledzona
2. **Poza transakcją** - brak synchronizacji
3. **Po clear()** - wszystkie encje odłączone

---

## 🔀 Merge vs Persist

To jest źródło wielu błędów!

### persist() - dla NOWYCH encji

```java
Pirate newPirate = new Pirate("Barbossa", "Captain", BigDecimal.ZERO);
em.persist(newPirate);  // INSERT

// persist() zmienia ORYGINALNY obiekt na MANAGED
System.out.println(em.contains(newPirate)); // true
```

### merge() - dla DETACHED encji

```java
// jack był pobrany z bazy, ale EntityManager został zamknięty
Pirate jack = ...;  // DETACHED

em.getTransaction().begin();
Pirate managedJack = em.merge(jack);  // Może być UPDATE lub INSERT!

// UWAGA: merge() zwraca NOWY obiekt!
System.out.println(em.contains(jack));        // false! (oryginalny wciąż DETACHED)
System.out.println(em.contains(managedJack)); // true (nowa kopia)

// Dalsze zmiany rób na managedJack, nie na jack!
managedJack.setBounty(new BigDecimal("50000"));  // ✅ Ta zmiana się zapisze
jack.setBounty(new BigDecimal("100000"));        // ❌ Ta zmiana ZNIKNIE!
```

**Częsty błąd:**
```java
em.merge(detachedEntity);
detachedEntity.setSomething("new value");  // ❌ BŁĄD! To wciąż DETACHED!
em.getTransaction().commit();  // Zmiana nie została zapisana!
```

---

## ⚠️ Typowe błędy i jak ich unikać

### 1. LazyInitializationException

```java
// ❌ ŹLE
EntityManager em = emf.createEntityManager();
Pirate pirate = em.find(Pirate.class, 1L);
em.close();
pirate.getShip().getName();  // 💥 LazyInitializationException!

// ✅ DOBRZE - opcja 1: pobierz przed zamknięciem
EntityManager em = emf.createEntityManager();
Pirate pirate = em.find(Pirate.class, 1L);
String shipName = pirate.getShip().getName();  // OK - em wciąż otwarty
em.close();

// ✅ DOBRZE - opcja 2: użyj JOIN FETCH
String jpql = "SELECT p FROM Pirate p JOIN FETCH p.ship WHERE p.id = :id";
Pirate pirate = em.createQuery(jpql, Pirate.class)
    .setParameter("id", 1L)
    .getSingleResult();
em.close();
pirate.getShip().getName();  // OK - ship był eagerly załadowany
```

### 2. Nieużywanie zwróconej wartości merge()

```java
// ❌ ŹLE
em.merge(detachedEntity);
detachedEntity.setName("New Name");  // Zmiana nie będzie zapisana!

// ✅ DOBRZE
Pirate managed = em.merge(detachedEntity);
managed.setName("New Name");  // OK - managed jest MANAGED
```

### 3. Usuwanie DETACHED encji

```java
// ❌ ŹLE
Pirate pirate = new Pirate();
pirate.setId(1L);
em.remove(pirate);  // IllegalArgumentException!

// ✅ DOBRZE
Pirate pirate = em.find(Pirate.class, 1L);
em.remove(pirate);  // OK - pirate jest MANAGED
```

### 4. Brak transakcji dla modyfikacji

```java
// ❌ ŹLE - bez transakcji
EntityManager em = emf.createEntityManager();
Pirate pirate = em.find(Pirate.class, 1L);
pirate.setName("New Name");
em.close();  // Zmiana nie została zapisana!

// ✅ DOBRZE - z transakcją
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();
Pirate pirate = em.find(Pirate.class, 1L);
pirate.setName("New Name");
em.getTransaction().commit();  // UPDATE
em.close();
```

---

## 🧪 Sprawdzanie stanu encji

```java
// Czy encja jest zarządzana?
boolean isManaged = em.contains(entity);

// EntityManagerFactory utrzymuje metadane
PersistenceUnitUtil util = emf.getPersistenceUnitUtil();

// Sprawdzenie czy encja ma ID
boolean hasId = util.getIdentifier(entity) != null;

// Sprawdzenie czy kolekcja lazy jest załadowana
boolean loaded = util.isLoaded(pirate, "ships");
```

---

## � Optimistic Locking - Kontrola współbieżności

### Problem: Lost Update

Wyobraź sobie scenariusz:
1. Użytkownik A pobiera rekord pirata (bounty = 10,000)
2. Użytkownik B pobiera ten sam rekord (bounty = 10,000)
3. Użytkownik A zmienia bounty na 15,000 i zapisuje
4. Użytkownik B zmienia bounty na 12,000 i zapisuje
5. **Wynik: bounty = 12,000 - zmiany użytkownika A zostały nadpisane!**

To jest **Lost Update** - jeden z najbardziej podstępnych błędów w aplikacjach wieloużytkownikowych.

### Rozwiązanie: @Version

JPA oferuje **optymistyczne blokowanie** za pomocą adnotacji `@Version`:

```java
@Entity
public class Pirate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;  // Automatycznie zarządzane przez Hibernate

    private String name;
    private BigDecimal bounty;
    
    // Getter bez settera - Hibernate sam zarządza wersją
    public Long getVersion() {
        return version;
    }
}
```

### Jak działa @Version?

1. **Przy INSERT**: Hibernate ustawia version = 0
2. **Przy każdym UPDATE**: Hibernate zwiększa version o 1
3. **Przy UPDATE sprawdza**: `WHERE id = ? AND version = ?`
4. **Jeśli version się nie zgadza**: `OptimisticLockException`!

```sql
-- To wykonuje Hibernate przy update:
UPDATE pirates 
SET name = ?, bounty = ?, version = 2 
WHERE id = 1 AND version = 1

-- Jeśli ktoś już zmienił rekord (version = 2), UPDATE nic nie zmieni
-- Hibernate wykryje 0 zmienionych wierszy i rzuci wyjątek
```

### Obsługa OptimisticLockException

```java
try {
    em.getTransaction().begin();
    
    Pirate pirate = em.find(Pirate.class, pirateId);
    pirate.setBounty(newBounty);
    
    em.getTransaction().commit();
    
} catch (RollbackException e) {
    if (e.getCause() instanceof OptimisticLockException) {
        // Ktoś inny zmienił rekord - co teraz?
        
        // Opcja 1: Poinformuj użytkownika
        System.out.println("Rekord został zmieniony przez innego użytkownika. Odśwież i spróbuj ponownie.");
        
        // Opcja 2: Retry automatyczny (patrz wzorzec poniżej)
        
        // Opcja 3: Merge zmian (zaawansowane)
    }
}
```

### Wzorzec: Retry z wykładniczym backoff

```java
public void updateWithRetry(Long pirateId, BigDecimal newBounty) {
    int maxRetries = 3;
    int attempt = 0;
    
    while (attempt < maxRetries) {
        attempt++;
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            // Zawsze pobieramy AKTUALNĄ wersję
            Pirate pirate = em.find(Pirate.class, pirateId);
            pirate.setBounty(newBounty);
            
            em.getTransaction().commit();
            return;  // Sukces!
            
        } catch (RollbackException e) {
            if (e.getCause() instanceof OptimisticLockException) {
                // Eksponencjalny backoff
                try {
                    Thread.sleep((long) Math.pow(2, attempt) * 100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } else {
                throw e;  // Inny błąd - nie retry
            }
        } finally {
            em.close();
        }
    }
    
    throw new RuntimeException("Nie udało się zapisać po " + maxRetries + " próbach");
}
```

### @Version vs Pessimistic Locking

| Cecha | Optimistic (@Version) | Pessimistic (SELECT FOR UPDATE) |
|-------|----------------------|--------------------------------|
| Blokada | Brak blokady przy odczycie | Blokuje wiersz w bazie |
| Wydajność | ✅ Lepsza przy małej konkurencji | ❌ Blokady ograniczają throughput |
| Konflikty | Wykrywane przy zapisie | Zapobieganie przez blokadę |
| Kiedy używać | Większość aplikacji webowych | Systemy finansowe, rezerwacje |

### ⚠️ Ważne uwagi

1. **Nie twórz settera dla @Version** - Hibernate sam zarządza wartością
2. **@Version może być**: `Long`, `Integer`, `Short`, `Timestamp`
3. **Merge zachowuje version** - detached encja pamięta swoją wersję
4. **JPQL UPDATE nie zwiększa version** - tylko operacje na encjach MANAGED

```java
// ❌ To NIE zwiększy version!
em.createQuery("UPDATE Pirate p SET p.bounty = 0").executeUpdate();

// ✅ To zwiększy version
Pirate p = em.find(Pirate.class, 1L);
p.setBounty(BigDecimal.ZERO);
```

---

## 📁 Zawartość modułu

| Plik | Opis |
|------|------|
| `entity/Pirate.java` | Encja pirata z @Version |
| `LifecycleDemo.java` | Demonstracja wszystkich stanów |
| `OptimisticLockingDemo.java` | 🆕 Demonstracja @Version i konfliktów |
| `LifecycleExercises.java` | 🎯 Ćwiczenia do wykonania |
| `LifecycleExercisesSolutions.java` | Rozwiązania ćwiczeń |

---

## 🎓 Kluczowe wnioski

1. **MANAGED = śledzony** - zmiany automatycznie idą do bazy
2. **DETACHED = odłączony** - zmiany ZNIKAJĄ
3. **merge() zwraca NOWY obiekt** - używaj zwróconej wartości!
4. **remove() tylko na MANAGED** - najpierw find(), potem remove()
5. **Transakcja wymagana** dla persist, update, remove
6. **em.close() = wszystko DETACHED** - lazy loading przestaje działać

---

## 📚 Materiały dodatkowe

- [Hibernate User Guide - Entity States](https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#pc-managed-state)
- [JPA Specification - Entity Lifecycle](https://jakarta.ee/specifications/persistence/)

