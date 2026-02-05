# SQL Injection - zagrożenie bezpieczeństwa

---

## Czym jest SQL Injection?

**Wstrzyknięcie złośliwego kodu SQL przez dane wejściowe**

**Podatny kod (❌ Statement + konkatenacja):**
```java
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(sql);
```

**Atak:**
```
username = "admin' OR '1'='1"
→ SELECT * FROM users WHERE username = 'admin' OR '1'='1'
→ Zwraca WSZYSTKICH użytkowników!
```

---

## Obrona - PreparedStatement

**Bezpieczny kod (✅ PreparedStatement):**
```java
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, username);  // Parametr automatycznie escapowany!
ResultSet rs = stmt.executeQuery();
```

**Ten sam atak:**
```
username = "admin' OR '1'='1"
→ SELECT * FROM users WHERE username = ?
→ Parametr: "admin' OR '1'='1" (traktowane jako tekst!)
→ Szuka użytkownika o nazwie "admin' OR '1'='1" (nie istnieje)
```

---

## Dlaczego PreparedStatement?

1. **Bezpieczeństwo** - automatyczne escapowanie parametrów
2. **Wydajność** - zapytanie kompilowane raz, wykonywane wiele razy
3. **Czytelność** - kod bardziej przejrzysty

**ZASADA:** Nigdy nie konkatenuj danych użytkownika do SQL!

---

## Wskazówka dla trenera
**Czas:** 10 minut

**Co mówię:**
- "SQL Injection to jeden z najgroźniejszych ataków na aplikacje webowe (OWASP Top 10)."
- "Statement + konkatenacja = otwarte drzwi dla atakujących."
- "PreparedStatement automatycznie escapuje parametry - zawsze używaj dla danych od użytkownika!"

**Co pokazuję:**
- `SqlInjectionDemo.java`
- Metoda `vulnerableLogin()` - pokazuję jak działa atak
- Metoda `secureLogin()` - pokazuję jak PreparedStatement blokuje atak
- Uruchamiam demo, pokazuję różnicę

**Następny krok:** Slajd `05-connection-pooling.md`

