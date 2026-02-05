package pl.przemekzagorski.training.tools;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║          AI COACHING - PROMPTY DLA JUNIOR DEVELOPERA             ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Przykładowe prompty do wykorzystania z GitHub Copilot,          ║
 * ║  ChatGPT, Claude lub dowolnym AI assistant.                      ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 *
 * Autor: Twój Coach AI - Przemek
 */
public class AICoachingPrompts {

    /**
     * ════════════════════════════════════════════════════════════════
     * 1️⃣ ONBOARDING W NOWY PROJEKT
     * ════════════════════════════════════════════════════════════════
     *
     * Sytuacja: Dołączasz do istniejącego projektu i chcesz go zrozumieć.
     *
     * PROMPTY:
     *
     * 📌 ARCHITEKTURA:
     * "Przeanalizuj strukturę tego projektu Maven/Gradle i wyjaśnij:
     *  - Jakie moduły zawiera?
     *  - Jakie frameworki są używane?
     *  - Gdzie znajduje się logika biznesowa?
     *  - Jak wygląda przepływ danych?
     *
     *  Oto pom.xml/build.gradle: [wklej]
     *  Oto struktura katalogów: [wklej wynik tree]"
     *
     * 📌 ZROZUMIENIE DOMENY:
     * "Oto pakiet z encjami JPA. Wyjaśnij:
     *  - Jakie są główne encje biznesowe?
     *  - Jakie relacje między nimi istnieją?
     *  - Narysuj diagram UML tych relacji.
     *
     *  [wklej encje]"
     *
     * 📌 KLUCZOWE KLASY:
     * "To jest główna klasa serwisowa projektu. Wyjaśnij:
     *  - Co robi każda publiczna metoda?
     *  - Jakie wzorce projektowe są użyte?
     *  - Czy widzisz potencjalne problemy?
     *
     *  [wklej kod]"
     *
     * 📌 FLOW REQUEST/RESPONSE:
     * "Prześledź ścieżkę requestu HTTP od kontrolera do bazy danych.
     *  Wyjaśnij co dzieje się na każdym etapie:
     *  - Controller: [kod]
     *  - Service: [kod]
     *  - Repository: [kod]
     *  - Entity: [kod]"
     */
    public void onboardingPrompts() {
        // To jest klasa dokumentacyjna - nie ma implementacji
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * 2️⃣ ZADAWANIE PYTAŃ O KOD
     * ════════════════════════════════════════════════════════════════
     *
     * Sytuacja: Nie rozumiesz jakiegoś fragmentu kodu.
     *
     * PROMPTY:
     *
     * 📌 WYJAŚNIENIE KODU:
     * "Wyjaśnij ten kod linia po linii dla Junior Developera.
     *  Użyj prostego języka i analogii:
     *
     *  [wklej kod]"
     *
     * 📌 DLACZEGO TAK?
     * "Dlaczego autor użył tutaj [wzorzec/technika]?
     *  Jakie są alternatywy i dlaczego ta jest lepsza/gorsza?
     *
     *  [wklej kod]"
     *
     * 📌 CO BY SIĘ STAŁO GDYBY...
     * "Co by się stało gdybym:
     *  - usunął @Transactional z tej metody?
     *  - zmienił FetchType z LAZY na EAGER?
     *  - nie zamknął EntityManager?
     *
     *  [wklej kod]"
     *
     * 📌 PORÓWNANIE:
     * "Porównaj te dwa podejścia. Które jest lepsze i dlaczego?
     *  Podejście A: [kod]
     *  Podejście B: [kod]"
     *
     * 📌 BEST PRACTICES:
     * "Czy ten kod jest zgodny z best practices dla Spring Boot?
     *  Co mógłbym poprawić? [wklej kod]"
     */
    public void questionPrompts() {
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * 3️⃣ DOKUMENTACJA
     * ════════════════════════════════════════════════════════════════
     *
     * Sytuacja: Potrzebujesz napisać dokumentację lub ją zrozumieć.
     *
     * PROMPTY:
     *
     * 📌 GENEROWANIE JAVADOC:
     * "Napisz profesjonalny JavaDoc dla tej klasy i wszystkich
     *  publicznych metod. Uwzględnij:
     *  - Opis klasy
     *  - @param dla każdego parametru
     *  - @return z opisem co zwraca
     *  - @throws dla wyjątków
     *
     *  [wklej kod]"
     *
     * 📌 README DLA MODUŁU:
     * "Napisz README.md dla tego modułu zawierające:
     *  - Cel modułu
     *  - Jak uruchomić
     *  - Główne klasy i ich opisy
     *  - Przykłady użycia
     *
     *  Oto struktura i główne klasy: [opis]"
     *
     * 📌 DOKUMENTACJA API:
     * "Wygeneruj dokumentację REST API w formacie Markdown
     *  dla tego kontrolera. Uwzględnij:
     *  - Endpointy
     *  - Metody HTTP
     *  - Parametry
     *  - Przykładowe requesty/responses
     *
     *  [wklej kontroler]"
     *
     * 📌 KOMENTARZE W KODZIE:
     * "Dodaj komentarze wyjaśniające do tego złożonego kodu.
     *  Komentarze powinny być zwięzłe i wyjaśniać DLACZEGO
     *  a nie CO (to widać z kodu).
     *
     *  [wklej kod]"
     */
    public void documentationPrompts() {
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * 4️⃣ GENEROWANIE DIAGRAMÓW
     * ════════════════════════════════════════════════════════════════
     *
     * Sytuacja: Potrzebujesz wizualizacji architektury lub flow.
     *
     * PROMPTY:
     *
     * 📌 DIAGRAM KLAS (PlantUML):
     * "Na podstawie tych encji JPA wygeneruj diagram klas w PlantUML.
     *  Pokaż relacje @OneToMany, @ManyToOne, @ManyToMany.
     *
     *  [wklej encje]"
     *
     * 📌 DIAGRAM SEKWENCJI:
     * "Wygeneruj diagram sekwencji (PlantUML/Mermaid) pokazujący
     *  przepływ żądania przez warstwy aplikacji:
     *  Controller → Service → Repository → Database
     *
     *  [wklej kod warstw]"
     *
     * 📌 DIAGRAM ARCHITEKTURY (Mermaid):
     * "Narysuj diagram architektury tej aplikacji używając Mermaid.
     *  Pokaż: komponenty, bazy danych, zewnętrzne serwisy, kolejki.
     *
     *  Oto opis aplikacji: [opis]"
     *
     * 📌 ERD (Entity Relationship Diagram):
     * "Na podstawie tych encji JPA wygeneruj diagram ERD.
     *  Użyj formatu Mermaid. Pokaż tabele, kolumny, klucze obce.
     *
     *  [wklej encje]"
     *
     * 📌 FLOWCHART LOGIKI BIZNESOWEJ:
     * "Narysuj flowchart (Mermaid) pokazujący logikę tej metody.
     *  Uwzględnij warunki, pętle, wywołania zewnętrzne.
     *
     *  [wklej metodę]"
     *
     *
     * PRZYKŁAD WYJŚCIA (Mermaid):
     *
     * ```mermaid
     * erDiagram
     *     PIRATE ||--o{ SHIP : captains
     *     SHIP ||--|{ CANNON : has
     *     PIRATE {
     *         Long id PK
     *         String name
     *         String rank
     *     }
     *     SHIP {
     *         Long id PK
     *         String name
     *         Long captain_id FK
     *     }
     * ```
     */
    public void diagramPrompts() {
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * 5️⃣ DEBUGOWANIE Z AI
     * ════════════════════════════════════════════════════════════════
     *
     * Sytuacja: Masz błąd i nie wiesz jak go naprawić.
     *
     * PROMPTY:
     *
     * 📌 ANALIZA STACK TRACE:
     * "Przeanalizuj ten stack trace i wyjaśnij:
     *  - Co jest przyczyną błędu?
     *  - W której linii mojego kodu jest problem?
     *  - Jak to naprawić?
     *
     *  Stack trace: [wklej]
     *  Mój kod: [wklej]"
     *
     * 📌 LazyInitializationException:
     * "Dostaję LazyInitializationException w Hibernate.
     *  Wyjaśnij dlaczego i podaj 3 sposoby rozwiązania:
     *
     *  Encja: [wklej]
     *  Serwis: [wklej]
     *  Kontroler: [wklej]"
     *
     * 📌 NIEOCZEKIWANE ZACHOWANIE:
     * "Ten kod powinien [oczekiwane zachowanie],
     *  ale zamiast tego [rzeczywiste zachowanie].
     *
     *  Co robię źle?
     *  [wklej kod]"
     *
     * 📌 OPTYMALIZACJA N+1:
     * "Ten kod generuje zbyt wiele zapytań SQL (problem N+1).
     *  Jak go zoptymalizować?
     *
     *  [wklej kod + logi SQL]"
     */
    public void debuggingPrompts() {
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * 6️⃣ CODE REVIEW Z AI
     * ════════════════════════════════════════════════════════════════
     *
     * Sytuacja: Chcesz sprawdzić swój kod przed Code Review.
     *
     * PROMPTY:
     *
     * 📌 PEŁNY REVIEW:
     * "Przeprowadź code review tego kodu. Sprawdź:
     *  - Potencjalne bugi
     *  - Naruszenia SOLID
     *  - Code smells
     *  - Bezpieczeństwo
     *  - Wydajność
     *  - Czytelność
     *
     *  [wklej kod]"
     *
     * 📌 BEZPIECZEŃSTWO:
     * "Sprawdź ten kod pod kątem bezpieczeństwa:
     *  - SQL Injection
     *  - XSS
     *  - Hardcoded credentials
     *  - Brakująca walidacja
     *
     *  [wklej kod]"
     *
     * 📌 PRZED PULL REQUESTEM:
     * "Przygotowuję PR z tym kodem. Co powinienem poprawić
     *  zanim wyślę do review? Bądź surowy jak Senior Developer.
     *
     *  [wklej kod]"
     */
    public void codeReviewPrompts() {
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ⚠️ ZASADY BEZPIECZEŃSTWA PRZY PRACY Z AI
     * ════════════════════════════════════════════════════════════════
     *
     * NIGDY NIE WKLEJAJ:
     * ❌ Haseł i kluczy API
     * ❌ Danych osobowych (RODO/GDPR!)
     * ❌ Danych klientów
     * ❌ Tajemnic firmy
     * ❌ Kodu objętego NDA
     *
     * ZAWSZE:
     * ✅ Anonimizuj dane przed wklejeniem
     * ✅ Używaj przykładowych danych zamiast prawdziwych
     * ✅ Sprawdź politykę firmy dot. AI
     * ✅ Weryfikuj odpowiedzi AI przed użyciem w produkcji
     */

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║          AI COACHING - PROMPTY DLA JUNIOR DEVELOPERA             ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Onboarding w nowy projekt                                    ║");
        System.out.println("║  2. Zadawanie pytań o kod                                        ║");
        System.out.println("║  3. Dokumentacja (JavaDoc, README)                               ║");
        System.out.println("║  4. Generowanie diagramów (UML, ERD, Mermaid)                    ║");
        System.out.println("║  5. Debugowanie z pomocą AI                                      ║");
        System.out.println("║  6. Code Review przed PR                                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Otwórz ten plik i użyj promptów w GitHub Copilot/ChatGPT!");
    }
}
