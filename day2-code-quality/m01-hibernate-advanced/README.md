# Moduł 01: Wydajność Hibernate

## 🎯 Cel modułu
Zaawansowane techniki optymalizacji wydajności Hibernate w produkcyjnych aplikacjach.

> **UWAGA:** Problem N+1 i JOIN FETCH omówiliśmy w Dniu 1 (moduł m07).
> Ten moduł skupia się na DODATKOWYCH technikach optymalizacji!

---

## 📁 Struktura modułu

```
m01-hibernate-advanced/
├── src/main/java/pl/przemekzagorski/training/hibernate/
│   ├── entity/
│   │   ├── Author.java          # Encja autora (kapitan)
│   │   └── Book.java            # Encja książki (przygoda)
│   ├── CacheDemo.java           # Cache L1 - demonstracja
│   ├── BatchSizeDemo.java       # @BatchSize - kompromis
│   ├── QueryOptimizationDemo.java # Paginacja, projekcja
│   ├── PerformanceExercises.java  # Ćwiczenia
│   └── PerformanceExercisesSolutions.java # Rozwiązania
└── src/test/java/
    └── PerformanceTest.java     # Testy jednostkowe
```

---

## 📚 Zawartość

| Klasa | Opis | Uruchom |
|-------|------|---------|
| `CacheDemo` | Cache L1 - jak działa, kiedy clear() | ▶️ `main()` |
| `BatchSizeDemo` | @BatchSize - kompromis między N+1 a JOIN | ▶️ `main()` |
| `QueryOptimizationDemo` | Paginacja, projekcja, read-only | ▶️ `main()` |
| `PerformanceExercises` | 📝 Ćwiczenia do samodzielnego wykonania | |
| `PerformanceExercisesSolutions` | ✅ Rozwiązania ćwiczeń | ▶️ `main()` |

---

## 🔄 Co nowego vs Dzień 1?

| Dzień 1 (m07) | Dzień 2 (tu) |
|---------------|--------------|
| Problem N+1 | Cache L1 |
| JOIN FETCH | @BatchSize |
| @EntityGraph | Paginacja/Projekcja |
| | Read-only hints |
| | Batch processing |

---

## 💡 Kluczowe koncepcje

### 1. Cache L1 (Persistence Context)

```java
EntityManager em = emf.createEntityManager();

// Pierwsze pobranie - idzie do bazy (SELECT)
Author author1 = em.find(Author.class, 1L);

// Drugie pobranie - z cache L1! (brak SQL)
Author author2 = em.find(Author.class, 1L);

// To ta sama instancja!
assert author1 == author2;  // true!
```

**Kiedy cache L1 się czyści:**
- `em.clear()` - ręczne czyszczenie
- `em.close()` - zamknięcie EntityManager
- Po commit/rollback w niektórych konfiguracjach

---

### 2. @BatchSize - kompromis między N+1 a JOIN

```java
@Entity
public class Author {
    @OneToMany(mappedBy = "author")
    @BatchSize(size = 10)  // Ładuj po 10 kolekcji naraz!
    private List<Book> books;
}

// Zamiast N+1 zapytań, mamy ~N/10 zapytań
// Lepsze niż N+1, ale nie wymaga JOIN jak FETCH
```

**Kiedy użyć:**
- Relacje czasami potrzebne (nie zawsze)
- Listy z wieloma elementami (unikamy kartezjańskiego)

---

### 3. Paginacja

```java
// Pobierz stronę 2, po 10 elementów
List<Author> page = em.createQuery(
    "SELECT a FROM Author a ORDER BY a.name", Author.class)
    .setFirstResult(10)  // skip 10 (strona 2)
    .setMaxResults(10)   // limit 10
    .getResultList();
```

**Formułka:** `offset = pageNumber * pageSize`

---

### 4. Projekcja DTO

```java
// Pobierz tylko potrzebne kolumny:
record AuthorDTO(Long id, String name) {}

List<Object[]> results = em.createQuery(
    "SELECT a.id, a.name FROM Author a", Object[].class)
    .getResultList();
```

---

### 5. Read-only Mode

```java
List<Author> authors = em.createQuery("SELECT a FROM Author a", Author.class)
    .setHint("org.hibernate.readOnly", true)
    .getResultList();

// Zmiany NIE będą zapisane przy flush()!
```

---

### 6. Batch Processing (flush/clear)

```java
for (int i = 1; i <= 10000; i++) {
    em.persist(new Author("Author " + i));
    
    if (i % 100 == 0) {
        em.flush();  // wyślij INSERT do bazy
        em.clear(); // zwolnij pamięć!
    }
}
```

---

## 📝 Ćwiczenia (20 min)

Otwórz `PerformanceExercises.java` i wykonaj:

| # | Ćwiczenie | Czas | Poziom |
|---|-----------|------|--------|
| 1 | Cache L1 - zweryfikuj że działa | 3 min | ⭐ |
| 2 | Paginacja - zaimplementuj stronnicowanie | 5 min | ⭐⭐ |
| 3 | Projekcja - użyj SELECT dla DTO | 5 min | ⭐⭐ |
| 4 | Read-only - sprawdź czy zmiany się zapisują | 4 min | ⭐ |
| 5 | Batch processing - flush/clear co 100 | 3 min | ⭐⭐ |

**Rozwiązania:** `PerformanceExercisesSolutions.java`

---

## 🎯 Kiedy użyć której techniki?

| Scenariusz | Technika |
|------------|----------|
| Lista z paginacją | `setFirstResult/setMaxResults` |
| Dropdown (id + name) | Projekcja DTO |
| Raport tylko-odczyt | `readOnly = true` |
| Import 10000 rekordów | `flush/clear` co 100 |
| Relacje czasami potrzebne | `@BatchSize` |
| Relacje zawsze potrzebne | `JOIN FETCH` (Dzień 1) |

---

## 🧪 Testy

```bash
cd m01-hibernate-advanced
mvn test
```

---

## 📊 Diagram: Flow optymalizacji

```
┌─────────────────────────────────────────────────────────────┐
│                    ZAPYTANIE DO BAZY                        │
├─────────────────────────────────────────────────────────────┤
│  1. Czy potrzebuję relacji?                                  │
│     ├── NIE → Projekcja (SELECT id, name)                    │
│     └── TAK → JOIN FETCH lub @BatchSize                      │
│                                                              │
│  2. Czy to duża lista?                                       │
│     └── TAK → Paginacja (setFirstResult/setMaxResults)       │
│                                                              │
│  3. Czy modyfikuję dane?                                     │
│     └── NIE → readOnly = true                                │
│                                                              │
│  4. Czy bulk insert/update?                                  │
│     └── TAK → flush/clear co N rekordów                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔗 Powiązane moduły

- **Dzień 1, m07** - N+1, JOIN FETCH, @EntityGraph
- **Dzień 2, m02** - Wzorce projektowe
- **Dzień 2, m04** - AI może pomóc w optymalizacji zapytań
