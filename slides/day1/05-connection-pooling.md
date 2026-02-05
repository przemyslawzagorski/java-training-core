# Connection Pooling - optymalizacja wydajności

---

## Problem: Tworzenie Connection jest drogie

**Koszt utworzenia Connection:** 100-200ms
- Nawiązanie połączenia TCP/IP
- Autentykacja
- Inicjalizacja sesji

**W aplikacji webowej:**
- 100 requestów/sekundę = 100 nowych Connection
- 100 × 100ms = 10 sekund (katastrofa!)

---

## Rozwiązanie: Connection Pool

**Pula gotowych połączeń**

```
┌─────────────────────────────┐
│   Connection Pool (10 conn) │
│  [C1] [C2] [C3] ... [C10]   │
└─────────────────────────────┘
         ↑           ↓
    Weź połączenie  Oddaj połączenie
```

**Przepływ:**
1. Request → Weź Connection z pool (1-2ms)
2. Wykonaj zapytanie
3. Oddaj Connection do pool (nie zamykaj!)

---

## HikariCP - najszybszy pool w Javie

```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:h2:mem:pirates");
config.setMaximumPoolSize(10);  // Max 10 połączeń
HikariDataSource dataSource = new HikariDataSource(config);

Connection conn = dataSource.getConnection();  // Szybkie! (1-2ms)
```

**Kluczowe parametry:**
- `maximumPoolSize` - max liczba połączeń (domyślnie 10)
- `connectionTimeout` - timeout na pobranie połączenia (30s)
- `idleTimeout` - czas życia nieużywanego połączenia (10min)

**Spring Boot używa HikariCP domyślnie!**

---

## Wskazówka dla trenera
**Czas:** 10 minut

**Co mówię:**
- "Tworzenie Connection to 100-200ms. W aplikacji webowej to katastrofa!"
- "Connection Pool = pula gotowych połączeń. Weź, użyj, oddaj."
- "HikariCP to najszybszy pool w Javie - używany przez Spring Boot domyślnie."

**Co pokazuję:**
- `ConnectionPoolDemo.java`
- Metoda `withoutPool()` - 100 requestów bez pool (wolne)
- Metoda `withPool()` - 100 requestów z HikariCP (szybkie)
- Uruchamiam demo, pokazuję różnicę w wydajności (20x szybciej!)

**Następny krok:** Slajd `06-transactions.md`

