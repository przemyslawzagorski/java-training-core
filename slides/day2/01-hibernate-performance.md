# Hibernate - optymalizacja wydajności

---

## Problem N+1 SELECT

**Najczęstszy problem wydajnościowy w Hibernate**

```java
List<Pirate> pirates = em.createQuery("SELECT p FROM Pirate p").getResultList();
// SELECT * FROM pirates (1 zapytanie)

for (Pirate p : pirates) {
    System.out.println(p.getShip().getName());
    // SELECT * FROM ships WHERE id = ? (N zapytań!)
}
```

**100 piratów = 1 + 100 = 101 zapytań!**

**Rozwiązanie - JOIN FETCH:**
```java
List<Pirate> pirates = em.createQuery(
    "SELECT p FROM Pirate p JOIN FETCH p.ship").getResultList();
// SELECT p.*, s.* FROM pirates p JOIN ships s ON p.ship_id = s.id (1 zapytanie!)
```

---

## Cache L1 (Session Cache)

**Persistence Context = cache pierwszego poziomu**
- Automatyczny (nie można wyłączyć)
- Zakres: EntityManager (Session)
- Gwarantuje unikalność obiektów

```java
Pirate p1 = em.find(Pirate.class, 1L);  // SELECT z bazy
Pirate p2 = em.find(Pirate.class, 1L);  // Z cache! (brak SELECT)
System.out.println(p1 == p2);  // true
```

**Czyszczenie cache:**
```java
em.flush();   // Zapisz zmiany do bazy
em.clear();   // Wyczyść cache
```

---

## Paginacja - setFirstResult / setMaxResults

**Problem: Ładowanie wszystkich rekordów**
```java
List<Pirate> pirates = em.createQuery("SELECT p FROM Pirate p").getResultList();
// SELECT * FROM pirates (10 000 rekordów!)
```

**Rozwiązanie - paginacja:**
```java
List<Pirate> pirates = em.createQuery("SELECT p FROM Pirate p")
    .setFirstResult(0)    // Offset (od którego rekordu)
    .setMaxResults(20)    // Limit (ile rekordów)
    .getResultList();
// SELECT * FROM pirates LIMIT 20 OFFSET 0
```

**Spring Data:**
```java
Page<Pirate> page = pirateRepository.findAll(PageRequest.of(0, 20));
```

---

## Projekcja - SELECT NEW DTO

**Problem: Ładowanie całych encji gdy potrzebujemy tylko kilku pól**
```java
List<Pirate> pirates = em.createQuery("SELECT p FROM Pirate p").getResultList();
// Ładuje wszystkie pola (name, rank, bounty, ship, ...)
```

**Rozwiązanie - projekcja na DTO:**
```java
List<PirateDto> pirates = em.createQuery(
    "SELECT NEW com.example.PirateDto(p.name, p.bounty) FROM Pirate p",
    PirateDto.class).getResultList();
// SELECT p.name, p.bounty FROM pirates (tylko 2 kolumny!)
```

---

## Read-only mode - optymalizacja odczytu

**Problem: Dirty Checking dla encji tylko do odczytu**
```java
List<Pirate> pirates = em.createQuery("SELECT p FROM Pirate p").getResultList();
// Hibernate śledzi zmiany (Dirty Checking) - koszt pamięci i CPU
```

**Rozwiązanie - read-only:**
```java
List<Pirate> pirates = em.createQuery("SELECT p FROM Pirate p")
    .setHint(QueryHints.HINT_READONLY, true)
    .getResultList();
// Brak Dirty Checking - szybsze, mniej pamięci
```

---

## Batch processing - masowe operacje

**Problem: Flush po każdej operacji**
```java
for (int i = 0; i < 10000; i++) {
    Pirate pirate = new Pirate("Pirate " + i, "Sailor", 100);
    em.persist(pirate);  // Flush co 10-20 rekordów (wolne!)
}
```

**Rozwiązanie - batch processing:**
```java
for (int i = 0; i < 10000; i++) {
    Pirate pirate = new Pirate("Pirate " + i, "Sailor", 100);
    em.persist(pirate);
    
    if (i % 50 == 0) {
        em.flush();  // Zapisz co 50 rekordów
        em.clear();  // Wyczyść cache
    }
}
```

**Konfiguracja:**
```properties
hibernate.jdbc.batch_size=50
```

---

## Wskazówka dla trenera
**Czas:** 20 minut

**Co mówię:**
- "N+1 SELECT to najczęstszy problem wydajnościowy - rozwiązanie: JOIN FETCH."
- "Cache L1 automatyczny - gwarantuje unikalność, ale może rosnąć (flush/clear)."
- "Paginacja - nie ładuj 10 000 rekordów, ładuj 20 na stronę."
- "Projekcja - ładuj tylko potrzebne pola (DTO zamiast encji)."
- "Read-only mode - wyłącz Dirty Checking dla danych tylko do odczytu."
- "Batch processing - flush/clear co N rekordów."

**Co pokazuję:**
- `PerformanceDemo.java` - wszystkie techniki optymalizacji
- Uruchamiam demo, pokazuję różnicę w liczbie zapytań SQL
- Pokazuję logi Hibernate (hibernate.show_sql=true)

**Ćwiczenia:**
- "Macie 5 ćwiczeń optymalizacji (m01-hibernate-advanced/PerformanceExercises.java)"
- "Exercises 1-3: N+1 SELECT, paginacja, projekcja"
- "30 minut na Exercises 1-3"

**Następny krok:** Po ćwiczeniach → Slajd `02-design-patterns.md`

