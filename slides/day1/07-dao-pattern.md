# DAO Pattern - Data Access Object

---

## Problem: Kod JDBC rozproszony po całej aplikacji

**Bez DAO:**
```java
public class UserController {
    public User getUser(Long id) {
        // Kod JDBC tutaj (Connection, PreparedStatement, ResultSet)
        // Kod biznesowy tutaj (walidacja, logika)
        // Wszystko w jednym miejscu = chaos!
    }
}
```

**Problemy:**
- Kod JDBC powtarzany w wielu miejscach
- Trudne testowanie (jak zamockować bazę?)
- Trudna zmiana technologii (JDBC → JPA)
- Naruszenie Single Responsibility Principle

---

## Rozwiązanie: Wzorzec DAO

**Separacja warstw:**
- **Controller** - obsługa HTTP (REST API)
- **Service** - logika biznesowa (walidacja, transakcje)
- **DAO** - dostęp do danych (CRUD)
- **Database** - przechowywanie danych

**DAO = Interface + Implementation**
```java
// Interface (kontrakt)
public interface PirateDao {
    Optional<Pirate> findById(Long id);
    List<Pirate> findAll();
    Pirate save(Pirate pirate);
    void delete(Long id);
}

// Implementation (JDBC)
public class PirateDaoJdbc implements PirateDao {
    // Kod JDBC tutaj
}
```

---

## Korzyści DAO Pattern

1. **Separacja warstw** - Service nie wie że to JDBC (widzi tylko interface)
2. **Łatwe testowanie** - można zamockować DAO
3. **Łatwa zmiana technologii** - JDBC → JPA bez zmiany Service
4. **Single Responsibility** - każda warstwa robi swoje

**Przepływ danych:**
```
Controller → Service → DAO → Database
    ↓          ↓        ↓        ↓
  HTTP    Logika    JDBC    Fizyczne dane
```

---

## Wskazówka dla trenera
**Czas:** 10 minut

**Co mówię:**
- "DAO to wzorzec projektowy - oddziela logikę biznesową od dostępu do danych."
- "Interface = kontrakt. Implementation = szczegóły (JDBC, JPA, MongoDB)."
- "Service nie wie że DAO używa JDBC - widzi tylko interface!"
- "Dzięki DAO możesz zmienić JDBC na JPA bez zmiany Service."

**Co pokazuję:**
- `PirateDao.java` (interface) - pokazuję metody
- `PirateDaoJdbc.java` (implementacja) - pokazuję jak używa JDBC
- `PirateService.java` - pokazuję jak używa DAO (nie wie że to JDBC!)
- Diagram architektury warstwowej

**Następny krok:** Slajd `08-dao-crud.md`

