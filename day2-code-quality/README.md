# Dzien 2: Jakosc Kodu & Narzedzia

## Cel dnia
Nauczyc juniorow pisania czystego kodu i efektywnej pracy z nowoczesnymi narzedziami.

## Harmonogram (8 blokow x 45 min)

| Blok | Czas | Modul | Temat |
|------|------|-------|-------|
| 1 | 9:00-9:45 | m01 | Wydajnosc Hibernate (Cache L1, @BatchSize, Paginacja) |
| 2 | 9:45-10:30 | m02 | Wzorce: Singleton, Factory, Builder |
| 3 | 10:45-11:30 | m02 | Wzorce: Strategy, Decorator, Observer |
| 4 | 11:30-12:15 | m02 | **Cwiczenia wzorce projektowe** |
| 5 | 13:00-13:45 | m03 | Code Smells i refactoring |
| 6 | 13:45-14:30 | m03 | SOLID + **Cwiczenia refactoring** |
| 7 | 14:45-15:30 | m04 | SonarLint + AI Coaching |
| 8 | 15:30-16:15 | m04 | CI/CD podstawy + **Cwiczenia AI** |

## Struktura modulow

### m01-hibernate-advanced (Wydajnosc)
- `CacheDemo` - Cache L1 (automatyczny cache sesji)
- `BatchSizeDemo` - @BatchSize jako kompromis vs JOIN FETCH
- `QueryOptimizationDemo` - Paginacja, projekcja, read-only
- `PerformanceExercises` - Cwiczenia praktyczne

### m02-design-patterns (6 wzorcow!)
| Wzorzec | Demo | Kiedy uzywac? |
|---------|------|---------------|
| Singleton | `SingletonDemo` | Konfiguracja, logger |
| Factory | `FactoryDemo` | Tworzenie roznych typow |
| Builder | `BuilderDemo` | Wiele opcjonalnych parametrow |
| Strategy | `StrategyDemo` | Wymienne algorytmy |
| Decorator | `DecoratorDemo` | Dynamiczne rozszerzanie |
| Observer | `ObserverDemo` | Powiadamianie o zmianach |
| **Cwiczenia** | `PatternExercises` | 5 cwiczen praktycznych |

### m03-refactoring (SOLID & Clean Code)
- `smells/before/` - Zly kod do analizy
- `smells/after/` - Poprawiony kod
- `solid/SolidDemo` - Zasady SOLID
- `RefactoringExercises` - Cwiczenia praktyczne

### m04-tools-and-ai (Narzedzia)
- `CodeWithIssues` - Kod do analizy SonarLint
- `AICoachingPrompts` - Prompty dla Junior Developera:
  - Onboarding w nowy projekt
  - Zadawanie pytan o kod
  - Generowanie dokumentacji
  - Tworzenie diagramow (Mermaid/PlantUML)
  - Debugowanie z AI
  - Code Review przed PR
- `ToolsExercises` - Cwiczenia SonarLint + AI
- `.github/workflows/ci.yml` - Przyklad GitHub Actions

## Kluczowe tematy

### Wydajnosc Hibernate
- Cache L1 - zawsze wlaczony, w obrebie sesji
- @BatchSize - grupowanie lazy loading
- Paginacja - nie laduj wszystkiego naraz
- Read-only mode - optymalizacja raportow

### Wzorce projektowe
- Singleton - jedna instancja (uzyj enum!)
- Factory - abstrakcja tworzenia obiektow
- Builder - fluent API dla zlozonych obiektow
- Strategy - kompozycja > dziedziczenie
- Decorator - dynamiczne dodawanie funkcji
- Observer - luüne powiazanie (eventy)

### Refactoring
- Extract Method - rozbij dlugie metody
- Extract Class - usun God Class
- Replace Magic Numbers - stale z nazwami
- Dependency Injection - interfejsy

### Narzedzia
- SonarLint - statyczna analiza w IDE
- AI Coaching - prompty dla juniorow
- CI/CD - automatyczne budowanie i testy

## Nawigacja

- [Dzien 1: Bazy danych](../day1-databases/README.md)
- [Dokumenty dla trenera](../docs/)
