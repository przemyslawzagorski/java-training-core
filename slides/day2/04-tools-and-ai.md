# Narzędzia i AI w rozwoju oprogramowania

---

## SonarLint - statyczna analiza kodu

**Plugin do IntelliJ IDEA**
- Wykrywa błędy w czasie pisania kodu
- Sugeruje poprawki
- Sprawdza zgodność z best practices

**Kategorie problemów:**
- **Bug** - błąd w kodzie (NullPointerException, resource leak)
- **Vulnerability** - luka bezpieczeństwa (SQL Injection, hardcoded password)
- **Code Smell** - zły zapach kodu (Long Method, Duplicate Code)

**Przykłady:**
```java
// ❌ SonarLint: "Null pointer dereference"
String name = null;
System.out.println(name.length());  // NullPointerException!

// ❌ SonarLint: "Resource should be closed"
FileInputStream fis = new FileInputStream("file.txt");
// Brak close()!

// ❌ SonarLint: "Magic number should be replaced with constant"
if (age > 18) { }
```

**Instalacja:**
1. IntelliJ IDEA → Settings → Plugins
2. Szukaj "SonarLint"
3. Install → Restart IDE

---

## Checkstyle - formatowanie kodu

**Sprawdza zgodność z konwencjami kodowania**
- Nazewnictwo (camelCase, PascalCase)
- Wcięcia (4 spacje, nie taby)
- Długość linii (max 120 znaków)
- Importy (kolejność, brak *)

**Konfiguracja Maven:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <configLocation>google_checks.xml</configLocation>
    </configuration>
</plugin>
```

**Uruchomienie:**
```bash
mvn checkstyle:check
```

---

## GitHub Actions - CI/CD

**Continuous Integration / Continuous Deployment**
- Automatyczne budowanie kodu
- Automatyczne testy
- Automatyczne deployment

**Przykład workflow (.github/workflows/ci.yml):**
```yaml
name: Java CI

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build with Maven
        run: mvn clean install
      - name: Run tests
        run: mvn test
```

**Korzyści:**
- Wykrywa błędy wcześnie (przed merge)
- Gwarantuje że kod się buduje
- Gwarantuje że testy przechodzą

---

## AI Coaching - prompty dla juniorów

**Problem:** Junior developer nie wie jak poprawić kod

**Rozwiązanie:** Gotowe prompty do ChatGPT/Claude

**Prompt 1: Code Review**
```
Jestem junior Java developerem. Przejrzyj poniższy kod i wskaż:
1. Błędy i potencjalne problemy
2. Naruszenia SOLID
3. Code smells
4. Sugestie poprawy

[wklej kod]
```

**Prompt 2: Refactoring**
```
Mam kod który działa, ale jest nieczytelny. Pomóż mi go zrefaktorować:
1. Wydziel metody (Extract Method)
2. Usuń duplikaty
3. Popraw nazewnictwo
4. Dodaj komentarze

[wklej kod]
```

**Prompt 3: Design Pattern**
```
Mam problem: [opisz problem]
Który wzorzec projektowy powinienem użyć?
Pokaż przykład implementacji w Javie.
```

**Prompt 4: Debugging**
```
Mam błąd: [wklej stack trace]
Kod: [wklej kod]

Pomóż mi:
1. Zrozumieć przyczynę błędu
2. Naprawić kod
3. Dodać testy żeby to się nie powtórzyło
```

**Prompt 5: Performance**
```
Mój kod jest wolny: [wklej kod]

Pomóż mi:
1. Znaleźć wąskie gardła
2. Zaproponować optymalizacje
3. Zmierzyć poprawę wydajności
```

---

## Dobre praktyki pracy z AI

**DO:**
- ✅ Używaj AI do nauki (wyjaśnienia, przykłady)
- ✅ Używaj AI do code review
- ✅ Używaj AI do generowania testów
- ✅ Weryfikuj kod wygenerowany przez AI
- ✅ Ucz się z sugestii AI

**DON'T:**
- ❌ Nie kopiuj kodu bez zrozumienia
- ❌ Nie ufaj AI w 100% (może się mylić!)
- ❌ Nie używaj AI do wrażliwych danych (hasła, klucze API)
- ❌ Nie zastępuj myślenia AI
- ❌ Nie ignoruj testów (AI może generować błędny kod)

---

## Wskazówka dla trenera
**Czas:** 20 minut

**Co mówię:**
- "SonarLint wykrywa błędy w czasie pisania - zainstalujcie w IntelliJ!"
- "Checkstyle sprawdza formatowanie - używajcie w projektach."
- "GitHub Actions = CI/CD - automatyczne testy przy każdym push."
- "AI to narzędzie do nauki, nie zastępstwo myślenia!"
- "Zawsze weryfikujcie kod wygenerowany przez AI - może się mylić!"

**Co pokazuję:**
- SonarLint w IntelliJ - pokazuję wykryte problemy
- `mvn checkstyle:check` - pokazuję raport
- `.github/workflows/ci.yml` - pokazuję konfigurację
- ChatGPT/Claude - pokazuję przykładowe prompty i odpowiedzi

**Ćwiczenia:**
- "Macie 6 ćwiczeń narzędziowych (m04-tools-and-ai/ToolsExercises.java)"
- "Exercise 1: Zainstaluj SonarLint i napraw problemy"
- "Exercise 2: Skonfiguruj Checkstyle"
- "Exercise 3: Użyj AI do code review"
- "30 minut na Exercises 1-3"

**Podsumowanie Day 2:**
- "Przeszliśmy: Hibernate Performance → Design Patterns → Refactoring → Tools & AI"
- "Jakość kodu to nie tylko działający kod, ale czytelny, testowalny, utrzymywalny!"
- "Narzędzia pomagają, ale nie zastępują myślenia!"

**Koniec szkolenia!**

