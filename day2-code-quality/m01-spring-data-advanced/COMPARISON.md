# 🏴‍☠️ Porównanie: Pure JPA vs Spring Data

Ten dokument pokazuje różnice między **Pure JPA** (m01-hibernate-advanced) a **Spring Data** (m01-spring-data-advanced).

---

## 📋 Tabela porównawcza

| Funkcja | Pure JPA (m01-hibernate-advanced) | Spring Data (m01-spring-data-advanced) | Zalety Spring Data |
|---------|-----------------------------------|----------------------------------------|-------------------|
| **Paginacja** | `setFirstResult()`, `setMaxResults()` | `Pageable`, `Page<T>` | Czytelniejsze, więcej metadanych |
| **Projekcje** | `SELECT NEW dto.PirateDTO(...)` | Interfejsy projekcji | Automatyczna implementacja |
| **Transakcje** | `EntityTransaction`, `begin()`, `commit()` | `@Transactional` | Deklaratywne, mniej kodu |
| **Read-Only** | `setHint("org.hibernate.readOnly", true)` | `@Transactional(readOnly=true)` | Czytelniejsze |
| **Batch Processing** | `flush()`, `clear()` | `flush()`, `clear()` | Identyczne |
| **Cache L2** | Konfiguracja w `persistence.xml` | Konfiguracja w `application.yml` | Łatwiejsza konfiguracja |
| **Repozytoria** | Własne klasy DAO | Interfejsy Spring Data | Mniej kodu boilerplate |

---

## 1️⃣ Paginacja

### Pure JPA (m01-hibernate-advanced)
```java
// Ręczne ustawienie offset i limit
TypedQuery<Pirate> query = em.createQuery(
    "SELECT p FROM Pirate p ORDER BY p.bounty DESC", 
    Pirate.class
);
query.setFirstResult(0);      // offset
query.setMaxResults(10);      // limit
List<Pirate> pirates = query.getResultList();

// Brak informacji o total count, total pages, etc.
```

### Spring Data (m01-spring-data-advanced)
```java
// Pageable - czytelne i z metadanymi
Pageable pageable = PageRequest.of(0, 10, Sort.by("bounty").descending());
Page<Pirate> page = pirateRepository.findAll(pageable);

// Dostępne metadane:
int totalPages = page.getTotalPages();
long totalElements = page.getTotalElements();
boolean hasNext = page.hasNext();
```

**Zalety Spring Data:**
- ✅ Czytelniejszy kod
- ✅ Metadane (total, hasNext, etc.)
- ✅ Łatwa iteracja przez strony

---

## 2️⃣ Projekcje

### Pure JPA (m01-hibernate-advanced)
```java
// Trzeba stworzyć klasę DTO
public class PirateDTO {
    private String name;
    private String rank;
    
    public PirateDTO(String name, String rank) {
        this.name = name;
        this.rank = rank;
    }
    // gettery, settery...
}

// Zapytanie z SELECT NEW
TypedQuery<PirateDTO> query = em.createQuery(
    "SELECT NEW com.example.PirateDTO(p.name, p.rank) FROM Pirate p WHERE p.rank = :rank",
    PirateDTO.class
);
query.setParameter("rank", "Captain");
List<PirateDTO> captains = query.getResultList();
```

### Spring Data (m01-spring-data-advanced)
```java
// Interfejs projekcji - Spring Data generuje implementację!
public interface PirateNameOnly {
    String getName();
    String getRank();
}

// Użycie - automatyczne mapowanie
List<PirateNameOnly> captains = pirateRepository.findProjectedByRank("Captain");
```

**Zalety Spring Data:**
- ✅ Brak kodu boilerplate (DTO)
- ✅ Automatyczna implementacja
- ✅ Czytelniejszy kod

---

## 3️⃣ Transakcje

### Pure JPA (m01-hibernate-advanced)
```java
EntityTransaction tx = em.getTransaction();
try {
    tx.begin();
    
    Pirate pirate = new Pirate("Jack", "Captain", BigDecimal.valueOf(100000));
    em.persist(pirate);
    
    tx.commit();
} catch (Exception e) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw e;
}
```

### Spring Data (m01-spring-data-advanced)
```java
@Transactional
public void savePirate(Pirate pirate) {
    pirateRepository.save(pirate);
    // Automatyczny commit lub rollback!
}
```

**Zalety Spring Data:**
- ✅ Deklaratywne (adnotacja)
- ✅ Automatyczny rollback przy wyjątku
- ✅ Mniej kodu

---

## 4️⃣ Read-Only Mode

### Pure JPA (m01-hibernate-advanced)
```java
TypedQuery<Pirate> query = em.createQuery("SELECT p FROM Pirate p", Pirate.class);
query.setHint("org.hibernate.readOnly", true);
List<Pirate> pirates = query.getResultList();
```

### Spring Data (m01-spring-data-advanced)
```java
@Transactional(readOnly = true)
public List<Pirate> findAllPirates() {
    return pirateRepository.findAll();
}
```

**Zalety Spring Data:**
- ✅ Czytelniejsze
- ✅ Działa na poziomie metody/klasy
- ✅ Jasna intencja

---

## 5️⃣ Batch Processing

### Pure JPA (m01-hibernate-advanced)
```java
EntityTransaction tx = em.getTransaction();
tx.begin();

for (int i = 0; i < 1000; i++) {
    Pirate pirate = new Pirate("Pirate " + i, "Sailor", BigDecimal.valueOf(1000));
    em.persist(pirate);
    
    if (i % 50 == 0) {
        em.flush();
        em.clear();
    }
}

tx.commit();
```

### Spring Data (m01-spring-data-advanced)
```java
@Transactional
public void importPirates(int count) {
    for (int i = 0; i < count; i++) {
        Pirate pirate = new Pirate("Pirate " + i, "Sailor", BigDecimal.valueOf(1000));
        entityManager.persist(pirate);
        
        if (i % 50 == 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
```

**Zalety Spring Data:**
- ✅ Brak ręcznego zarządzania transakcją
- ⚠️ Technika identyczna (flush/clear)

---

## 🎯 Podsumowanie

| Aspekt | Pure JPA | Spring Data | Rekomendacja |
|--------|----------|-------------|--------------|
| **Prostota** | ⭐⭐ | ⭐⭐⭐⭐⭐ | Spring Data |
| **Czytelność** | ⭐⭐ | ⭐⭐⭐⭐⭐ | Spring Data |
| **Kontrola** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Pure JPA |
| **Produktywność** | ⭐⭐ | ⭐⭐⭐⭐⭐ | Spring Data |

**Kiedy używać Pure JPA:**
- Bardzo złożone zapytania
- Potrzebujesz pełnej kontroli
- Legacy code

**Kiedy używać Spring Data:**
- Nowe projekty
- Standardowe operacje CRUD
- Chcesz produktywności i czytelności
- **99% przypadków!** 🎯

