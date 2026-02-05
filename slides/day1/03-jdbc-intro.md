# JDBC - Java Database Connectivity

---

## Czym jest JDBC?

**API do komunikacji Java ↔ Baza danych**

Kluczowe interfejsy:
- **DriverManager** - zarządzanie sterownikami baz danych
- **Connection** - połączenie z bazą
- **Statement / PreparedStatement** - wykonywanie zapytań SQL
- **ResultSet** - wyniki zapytania SELECT

---

## Architektura JDBC

```
┌─────────────────┐
│   JAVA APP      │  ← Twój kod
└────────┬────────┘
         │
┌────────▼────────┐
│   JDBC API      │  ← DriverManager, Connection, Statement
└────────┬────────┘
         │
┌────────▼────────┐
│  JDBC Driver    │  ← H2, PostgreSQL, MySQL
└────────┬────────┘
         │
┌────────▼────────┐
│    DATABASE     │
└─────────────────┘
```

**JDBC API** = interfejs (standard)  
**JDBC Driver** = implementacja dla konkretnej bazy

---

## Try-with-resources (Java 7+)

**Stary styl (Java 6)** - ręczne zamykanie
```java
Connection conn = null;
try {
    conn = DriverManager.getConnection(url);
    // ...
} finally {
    if (conn != null) conn.close();
}
```

**Nowy styl (Java 7+)** - automatyczne zamykanie
```java
try (Connection conn = DriverManager.getConnection(url)) {
    // ...
}  // Automatyczne conn.close()
```

---

## Wskazówka dla trenera
**Czas:** 10 minut

**Co mówię:**
- "JDBC to niskopoziomowe API - daje pełną kontrolę, ale wymaga dużo kodu."
- "Connection, Statement, ResultSet - to 3 kluczowe interfejsy."
- "Try-with-resources (Java 7+) automatycznie zamyka zasoby - mniej błędów."

**Co pokazuję:**
- `ConnectionDemo.java` - porównanie starego vs nowego stylu
- Uruchamiam demo, pokazuję wyniki (lista piratów z bazy)

**Następny krok:** Slajd `04-sql-injection.md`

