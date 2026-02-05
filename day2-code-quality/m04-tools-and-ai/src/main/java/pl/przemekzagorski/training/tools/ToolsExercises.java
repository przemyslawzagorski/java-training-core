package pl.przemekzagorski.training.tools;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║               ĆWICZENIA - SONARLINT & AI                         ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Czas: 25 minut                                                  ║
 * ║  Poziom: Podstawowy do Średniego                                 ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
public class ToolsExercises {

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 1: SonarLint - Znajdź problemy
     * ════════════════════════════════════════════════════════════════
     *
     * PRZYGOTOWANIE:
     * 1. Zainstaluj wtyczkę SonarLint w IntelliJ/VS Code
     * 2. Otwórz plik CodeWithIssues.java
     *
     * ZADANIE:
     * Znajdź i napraw WSZYSTKIE problemy zgłoszone przez SonarLint.
     * Zapisz ile znalazłeś:
     *
     * - Bugs: ___
     * - Vulnerabilities: ___
     * - Code Smells: ___
     *
     * DLA KAŻDEGO PROBLEMU:
     * 1. Przeczytaj wyjaśnienie SonarLint (dlaczego to problem?)
     * 2. Napraw zgodnie z sugestią
     * 3. Sprawdź czy zniknął z listy
     */
    public void exercise1_sonarLintIssues() {
        // Otwórz CodeWithIssues.java i napraw problemy!
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 2: AI - Onboarding w projekt
     * ════════════════════════════════════════════════════════════════
     *
     * ZADANIE:
     * Użyj AI (GitHub Copilot/ChatGPT) do zrozumienia modułu m06-relations
     * z Day 1.
     *
     * KROKI:
     * 1. Wklej encje z m06-relations do AI
     * 2. Poproś o wyjaśnienie relacji między encjami
     * 3. Poproś o wygenerowanie diagramu ERD (Mermaid)
     *
     * PROMPT:
     * "Przeanalizuj te encje JPA i:
     *  1. Wyjaśnij jakie relacje między nimi istnieją
     *  2. Narysuj diagram ERD w formacie Mermaid
     *  3. Wskaż potencjalne problemy (np. N+1)
     *
     *  [wklej encje]"
     *
     * ZAPISZ:
     * - Diagram wygenerowany przez AI: ___
     * - Czy AI poprawnie zidentyfikował relacje? TAK / NIE
     * - Czy wskazał jakieś problemy? ___
     */
    public void exercise2_aiOnboarding() {
        // Użyj AI do analizy modułu m06-relations
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 3: AI - Generowanie dokumentacji
     * ════════════════════════════════════════════════════════════════
     *
     * ZADANIE:
     * Użyj AI do wygenerowania dokumentacji dla klasy JpaPirateDao
     * z modułu m04-jpa-intro.
     *
     * PROMPT:
     * "Napisz profesjonalny JavaDoc dla tej klasy DAO.
     *  Uwzględnij:
     *  - Opis klasy i jej odpowiedzialności
     *  - @param i @return dla każdej metody
     *  - @throws dla możliwych wyjątków
     *  - Przykłady użycia w blokach {@code}
     *
     *  [wklej JpaPirateDao.java]"
     *
     * OCEŃ WYNIK:
     * - Czy dokumentacja jest poprawna? TAK / NIE
     * - Czy wymaga poprawek? Jakich? ___
     */
    public void exercise3_aiDocumentation() {
        // Wygeneruj dokumentację dla JpaPirateDao
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 4: AI - Debugowanie
     * ════════════════════════════════════════════════════════════════
     *
     * SCENARIUSZ:
     * Dostajesz błąd przy pobieraniu pirata z załogą:
     *
     * org.hibernate.LazyInitializationException:
     *   failed to lazily initialize a collection of role: Pirate.crew
     *
     * ZADANIE:
     * 1. Wklej do AI encję Pirate z relacją @OneToMany do Crew
     * 2. Wklej kod który wywołuje błąd
     * 3. Poproś o 3 sposoby rozwiązania
     *
     * PROMPT:
     * "Dostaję LazyInitializationException. Oto mój kod:
     *
     *  Encja: [wklej]
     *  Serwis: [wklej]
     *  Kontroler: [wklej]
     *
     *  Wyjaśnij:
     *  1. Dlaczego występuje ten błąd?
     *  2. Podaj 3 sposoby rozwiązania z pros/cons
     *  3. Który sposób polecasz dla REST API?"
     *
     * ZAPISZ ODPOWIEDZI AI:
     * Sposób 1: ___
     * Sposób 2: ___
     * Sposób 3: ___
     * Rekomendacja: ___
     */
    public void exercise4_aiDebugging() {
        // Użyj AI do debugowania LazyInitializationException
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 5: AI - Diagram sekwencji
     * ════════════════════════════════════════════════════════════════
     *
     * ZADANIE:
     * Wygeneruj diagram sekwencji dla flow:
     * "Użytkownik tworzy nowego pirata przez REST API"
     *
     * Użyj kodu z day1-databases:
     * - Controller
     * - Service
     * - Repository/DAO
     *
     * PROMPT:
     * "Wygeneruj diagram sekwencji w formacie Mermaid dla tego flow:
     *  1. HTTP POST /api/pirates
     *  2. Controller przyjmuje request
     *  3. Service waliduje i tworzy pirata
     *  4. Repository zapisuje do bazy
     *  5. Response wraca do klienta
     *
     *  Oto kod: [wklej warstwy]"
     *
     * WKLEJ WYNIK (diagram Mermaid):
     *
     * ```mermaid
     * sequenceDiagram
     *     Client->>Controller: POST /api/pirates
     *     Controller->>Service: createPirate(dto)
     *     Service->>Repository: save(entity)
     *     Repository->>Database: INSERT
     *     Database-->>Repository: OK
     *     Repository-->>Service: savedEntity
     *     Service-->>Controller: responseDto
     *     Controller-->>Client: 201 Created
     * ```
     */
    public void exercise5_aiSequenceDiagram() {
        // Wygeneruj diagram sekwencji
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 6: CI/CD - Przeczytaj workflow
     * ════════════════════════════════════════════════════════════════
     *
     * ZADANIE:
     * Przeczytaj przykładowy GitHub Actions workflow w README.md
     * i odpowiedz na pytania:
     *
     * 1. Kiedy workflow się uruchamia?
     *    Odpowiedź: ___
     *
     * 2. Na jakim systemie operacyjnym działa?
     *    Odpowiedź: ___
     *
     * 3. Jakiej wersji Javy używa?
     *    Odpowiedź: ___
     *
     * 4. Jakie kroki wykonuje (build, test)?
     *    Odpowiedź: ___
     *
     * 5. Co się stanie jeśli testy nie przejdą?
     *    Odpowiedź: ___
     *
     * BONUS: Zmodyfikuj workflow aby:
     * - Używał Java 21
     * - Dodał krok "Run SonarLint"
     */
    public void exercise6_cicdWorkflow() {
        // Przeanalizuj workflow CI/CD
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║               ĆWICZENIA - SONARLINT & AI                         ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. SonarLint - znajdź i napraw problemy w CodeWithIssues        ║");
        System.out.println("║  2. AI Onboarding - zrozum moduł m06-relations                   ║");
        System.out.println("║  3. AI Dokumentacja - wygeneruj JavaDoc                          ║");
        System.out.println("║  4. AI Debugowanie - rozwiąż LazyInitializationException         ║");
        System.out.println("║  5. AI Diagramy - wygeneruj diagram sekwencji                    ║");
        System.out.println("║  6. CI/CD - przeanalizuj workflow                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Wykonaj ćwiczenia używając SonarLint i GitHub Copilot/ChatGPT!");
    }
}
