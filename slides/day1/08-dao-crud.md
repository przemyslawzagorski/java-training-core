# CRUD Operations w DAO

---

## CRUD - 4 podstawowe operacje

**C - Create** (INSERT)
```java
Pirate save(Pirate pirate);
```

**R - Read** (SELECT)
```java
Optional<Pirate> findById(Long id);
List<Pirate> findAll();
```

**U - Update** (UPDATE)
```java
Pirate save(Pirate pirate);  // Ta sama metoda co Create!
```

**D - Delete** (DELETE)
```java
void delete(Long id);
```

---

## save() - INSERT lub UPDATE?

**Logika:**
- Jeśli `id == null` → INSERT (nowy rekord)
- Jeśli `id != null` → UPDATE (istniejący rekord)

```java
public Pirate save(Pirate pirate) {
    if (pirate.getId() == null) {
        // INSERT
        String sql = "INSERT INTO pirates (name, rank, bounty) VALUES (?, ?, ?)";
        // ... RETURN_GENERATED_KEYS
    } else {
        // UPDATE
        String sql = "UPDATE pirates SET name = ?, rank = ?, bounty = ? WHERE id = ?";
        // ...
    }
    return pirate;
}
```

---

## Transakcje w Service, nie w DAO!

**DAO** - pojedyncze operacje (findById, save, delete)
```java
public class PirateDaoJdbc implements PirateDao {
    public Pirate save(Pirate pirate) {
        // Jedna operacja INSERT lub UPDATE
    }
}
```

**Service** - logika biznesowa + transakcje
```java
public class PirateService {
    @Transactional
    public void transferCrew(Long fromShipId, Long toShipId, Long pirateId) {
        Pirate pirate = pirateDao.findById(pirateId).orElseThrow();
        pirate.setShipId(toShipId);
        pirateDao.save(pirate);
        // Transakcja obejmuje całą metodę
    }
}
```

---

## Wskazówka dla trenera
**Czas:** 10 minut

**Co mówię:**
- "CRUD to 4 podstawowe operacje - Create, Read, Update, Delete."
- "save() obsługuje INSERT i UPDATE - sprawdza czy id == null."
- "Transakcje w Service, nie w DAO! DAO to pojedyncze operacje."
- "Service zarządza logiką biznesową i transakcjami."

**Co pokazuję:**
- `CrudDemo.java` - pokazuję wszystkie operacje CRUD
- `TransactionDemo.java` - pokazuję transakcję w Service
- Uruchamiam demo, pokazuję wyniki

**Ćwiczenia:**
- "Macie 5 ćwiczeń DAO (m03-jdbc-crud/DaoExercises.java)"
- "Exercise 1-2: findById, save (INSERT/UPDATE)"
- "Exercise 4: transferCrew (transakcja w Service!)"
- "40 minut na Exercises 1-4"

**Następny krok:** Po ćwiczeniach → Przerwa (15 minut) → M04 JPA Introduction

