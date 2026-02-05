# Transakcje - ACID

---

## Czym jest transakcja?

**Grupa operacji wykonanych jako całość (wszystko albo nic)**

Przykład: Przelew pieniędzy
1. Odejmij 100 zł z konta A
2. Dodaj 100 zł do konta B

**Bez transakcji:**
- Krok 1 wykonany ✅
- Krok 2 błąd ❌
- Efekt: 100 zł zniknęło! 💸

**Z transakcją:**
- Krok 1 wykonany ✅
- Krok 2 błąd ❌
- ROLLBACK → Krok 1 cofnięty ✅
- Efekt: Nic się nie zmieniło (bezpiecznie!)

---

## ACID - 4 właściwości transakcji

**A - Atomicity (Atomowość)**
- Wszystko albo nic
- Nie ma "pół-transakcji"

**C - Consistency (Spójność)**
- Dane zawsze poprawne
- Suma kont przed = suma kont po

**I - Isolation (Izolacja)**
- Transakcje nie kolidują
- T1 i T2 wykonują się równolegle, ale nie widzą swoich zmian

**D - Durability (Trwałość)**
- Zapis na dysk (nie RAM)
- Przetrwa restart serwera

---

## JDBC - auto-commit

**Domyślnie auto-commit = true**
- Każda operacja commituje NATYCHMIAST (nie na końcu metody!)

```java
conn.setAutoCommit(true);  // Domyślne
stmt.executeUpdate("INSERT ...");  // ✅ COMMIT natychmiast!
```

**Ręczne transakcje (auto-commit = false)**
```java
conn.setAutoCommit(false);
try {
    stmt1.executeUpdate("UPDATE accounts SET balance = balance - 100 WHERE id = 1");
    stmt2.executeUpdate("UPDATE accounts SET balance = balance + 100 WHERE id = 2");
    conn.commit();  // ✅ Zatwierdź obie operacje
} catch (Exception e) {
    conn.rollback();  // ❌ Cofnij obie operacje
}
```

---

## Wskazówka dla trenera
**Czas:** 10 minut

**Co mówię:**
- "Transakcja = wszystko albo nic. Jak przelew - nie ma pół-przelewu!"
- "ACID to 4 filary transakcji - Atomowość, Spójność, Izolacja, Trwałość."
- "JDBC domyślnie ma auto-commit = true - każda operacja commituje NATYCHMIAST!"
- "Dla ręcznych transakcji: setAutoCommit(false) → commit() lub rollback()"

**Co pokazuję:**
- `TransactionDemo.java`
- Metoda `transferMoneyWithCommit()` - przelew z commit
- Metoda `transferMoneyWithRollback()` - przelew z błędem i rollback
- Uruchamiam demo, pokazuję różnicę

**UWAGA:** Podkreśl różnicę: auto-commit = true → commit NATYCHMIAST (nie na końcu metody!)

**Ćwiczenia:**
- "Macie 5 ćwiczeń JDBC (m02-jdbc-connection/JdbcExercises.java)"
- "Exercise 5 to transakcja - pamiętajcie: setAutoCommit(false)!"
- "30 minut na Exercises 1-3"

**Następny krok:** Po ćwiczeniach → Slajd `07-dao-pattern.md`

