# Entity Lifecycle - cykl życia encji

---

## 4 stany encji w JPA

**1. TRANSIENT (Przejściowy)**
- Obiekt utworzony przez `new`, nie zarządzany przez EntityManager
- Nie ma ID w bazie
- Zmiany NIE są śledzone

**2. MANAGED (Zarządzany)**
- Obiekt zarządzany przez EntityManager (Persistence Context)
- Ma ID w bazie
- Zmiany SĄ śledzone (Dirty Checking)

**3. DETACHED (Odłączony)**
- Obiekt był zarządzany, ale EntityManager został zamknięty
- Ma ID w bazie
- Zmiany NIE są śledzone

**4. REMOVED (Usunięty)**
- Obiekt zaplanowany do usunięcia
- Zostanie usunięty przy commit()

---

## Przejścia między stanami

```
NEW Pirate()
    ↓ (TRANSIENT)
em.persist(pirate)
    ↓ (MANAGED)
em.close()
    ↓ (DETACHED)
em.merge(pirate)
    ↓ (MANAGED)
em.remove(pirate)
    ↓ (REMOVED)
em.flush()
    ↓ (usunięty z bazy)
```

---

## Dirty Checking - automatyczne UPDATE

**MANAGED encje są śledzone:**
```java
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

Pirate pirate = em.find(Pirate.class, 1L);  // MANAGED
pirate.setBounty(5000);  // Zmiana śledzona!

em.getTransaction().commit();  // Automatyczny UPDATE!
// Hibernate: UPDATE pirates SET bounty = 5000 WHERE id = 1
```

**Nie trzeba wywoływać em.merge()!**

---

## Persistence Context - cache L1

**Persistence Context = cache pierwszego poziomu**
- Każdy EntityManager ma swój Persistence Context
- Przechowuje MANAGED encje
- Gwarantuje unikalność (jedna instancja na ID)

```java
Pirate p1 = em.find(Pirate.class, 1L);  // SELECT z bazy
Pirate p2 = em.find(Pirate.class, 1L);  // Z cache! (brak SELECT)

System.out.println(p1 == p2);  // true (ta sama instancja!)
```

---

## Wskazówka dla trenera
**Czas:** 15 minut

**Co mówię:**
- "Encja ma 4 stany: TRANSIENT, MANAGED, DETACHED, REMOVED."
- "MANAGED encje są śledzone - zmiany automatycznie zapisywane (Dirty Checking)."
- "Persistence Context to cache L1 - gwarantuje unikalność obiektów."
- "Nie trzeba wywoływać merge() dla MANAGED encji - wystarczy zmienić pole!"

**Co pokazuję:**
- Diagram stanów encji
- `EntityLifecycleDemo.java` - przejścia między stanami
- `DirtyCheckingDemo.java` - automatyczny UPDATE
- `PersistenceContextDemo.java` - cache L1 (dwa find() = jeden SELECT)
- Uruchamiam demo, pokazuję wygenerowane SQL

**Następny krok:** Slajd `11-jpa-relations.md`

