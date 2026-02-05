package pl.przemekzagorski.training.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import pl.przemekzagorski.training.hibernate.entity.Author;

import java.util.List;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║               ĆWICZENIA - WYDAJNOŚĆ HIBERNATE                    ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Czas: 20 minut                                                  ║
 * ║  Poziom: Progresywny (Łatwy → Średni → Trudny)                   ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 *
 * Te ćwiczenia pomogą Ci zrozumieć techniki optymalizacji Hibernate.
 *
 * PROGRESJA:
 * - Exercise 1-2 (ŁATWY): Gotowy kod - uruchom, obserwuj, eksperymentuj!
 * - Exercise 3 (ŚREDNI): Struktura + TODO - uzupełnij brakujące części
 * - Exercise 4-5 (TRUDNY): Tylko wskazówki - napisz od zera
 */
public class PerformanceExercises {

    private EntityManagerFactory emf;

    public PerformanceExercises() {
        this.emf = Persistence.createEntityManagerFactory("advanced-pu");
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 1: Cache L1 w praktyce
     * ════════════════════════════════════════════════════════════════
     * 🎯 POZIOM: ŁATWY (gotowy kod - uruchom i obserwuj!)
     *
     * KONTEKST:
     * Cache L1 (Persistence Context) to wbudowany mechanizm Hibernate.
     * Każdy EntityManager ma swój własny cache L1.
     *
     * ZADANIE:
     * 1. Uruchom metodę i OBSERWUJ logi SQL w konsoli
     * 2. Policz ile zapytań SELECT zostało wykonanych
     * 3. Sprawdź wynik porównania referencji (==)
     * 4. EKSPERYMENTUJ z odkomentowaniem linii poniżej!
     */
    public void exercise1_cacheL1Verification() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 1: Cache L1 - Obserwacja");
        System.out.println("=".repeat(60));

        EntityManager em = emf.createEntityManager();

        try {
            // Najpierw utwórz autora do testów
            em.getTransaction().begin();
            Author testAuthor = new Author("Jack Sparrow");
            em.persist(testAuthor);
            em.getTransaction().commit();
            Long authorId = testAuthor.getId();

            // Wyczyść cache i zacznij od nowa
            em.clear();

            System.out.println("\n🔍 OBSERWUJ LOGI SQL - ile zapytań SELECT zobaczysz?\n");

            // Pierwsze pobranie - idzie do bazy (SELECT)
            System.out.println("1️⃣ Pierwsze em.find() - POWINNO być zapytanie SQL:");
            Author author1 = em.find(Author.class, authorId);
            System.out.println("   ✅ Pobrano: " + author1.getName());

            // Drugie pobranie - z cache L1! (brak SQL)
            System.out.println("\n2️⃣ Drugie em.find() - BRAK zapytania SQL (cache L1!):");
            Author author2 = em.find(Author.class, authorId);
            System.out.println("   ✅ Pobrano: " + author2.getName());

            // Trzecie pobranie - również z cache L1!
            System.out.println("\n3️⃣ Trzecie em.find() - BRAK zapytania SQL (cache L1!):");
            Author author3 = em.find(Author.class, authorId);
            System.out.println("   ✅ Pobrano: " + author3.getName());

            // Weryfikacja - czy to ta sama instancja?
            System.out.println("\n📊 WERYFIKACJA:");
            System.out.println("   author1 == author2: " + (author1 == author2) + " (powinno być TRUE)");
            System.out.println("   author2 == author3: " + (author2 == author3) + " (powinno być TRUE)");
            System.out.println("   author1 == author3: " + (author1 == author3) + " (powinno być TRUE)");

            boolean cacheWorks = (author1 == author2) && (author2 == author3);
            System.out.println("\n✅ Cache L1 działa poprawnie: " + cacheWorks);

            // ═══════════════════════════════════════════════════════════
            // 💡 EKSPERYMENT 1: Co się stanie po em.clear()?
            // ═══════════════════════════════════════════════════════════
            // ODKOMENTUJ poniższe linie i zobacz co się stanie:
            //
            // System.out.println("\n🧪 EKSPERYMENT: Wywołuję em.clear()...");
            // em.clear();
            // System.out.println("4️⃣ Czwarte em.find() PO clear() - POWINNO być nowe zapytanie SQL:");
            // Author author4 = em.find(Author.class, authorId);
            // System.out.println("   author1 == author4: " + (author1 == author4) + " (powinno być FALSE!)");
            //
            // ❓ PYTANIE: Dlaczego author1 != author4?
            // 💡 ODPOWIEDŹ: em.clear() czyści cache L1, więc Hibernate tworzy NOWĄ instancję!

            // ═══════════════════════════════════════════════════════════
            // 💡 EKSPERYMENT 2: Modyfikacja i dirty checking
            // ═══════════════════════════════════════════════════════════
            // ODKOMENTUJ poniższe linie:
            //
            // System.out.println("\n🧪 EKSPERYMENT 2: Modyfikacja encji w cache:");
            // em.getTransaction().begin();
            // author1.setName("Captain Jack Sparrow");
            // System.out.println("   Zmieniono nazwę na: " + author1.getName());
            // System.out.println("   author2.getName(): " + author2.getName() + " (ta sama instancja!)");
            // em.flush(); // Hibernate wykryje zmianę i wyśle UPDATE
            // em.getTransaction().commit();
            //
            // ❓ PYTANIE: Dlaczego author2 też ma zmienioną nazwę?
            // 💡 ODPOWIEDŹ: To ta sama instancja w pamięci (cache L1)!

            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ Ćwiczenie 1 zakończone!");
            System.out.println("💡 Teraz odkomentuj EKSPERYMENTY i zobacz co się stanie!");
            System.out.println("=".repeat(60));

        } finally {
            em.close();
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 2: Paginacja - ładowanie danych stronami
     * ════════════════════════════════════════════════════════════════
     * 🎯 POZIOM: ŁATWY (gotowy kod - uruchom i obserwuj!)
     *
     * KONTEKST:
     * W aplikacjach webowych rzadko ładujemy WSZYSTKIE rekordy naraz.
     * Paginacja pozwala ładować dane "stronami" (np. po 10, 20, 50).
     *
     * ZADANIE:
     * 1. Uruchom metodę i OBSERWUJ logi SQL
     * 2. Sprawdź jak działa setFirstResult() i setMaxResults()
     * 3. EKSPERYMENTUJ z różnymi rozmiarami stron!
     */
    public void exercise2_pagination() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 2: Paginacja - Obserwacja");
        System.out.println("=".repeat(60));

        EntityManager em = emf.createEntityManager();

        try {
            // Utwórz dane testowe - 25 kapitanów
            em.getTransaction().begin();
            System.out.println("\n📝 Tworzę 25 kapitanów...");
            for (int i = 1; i <= 25; i++) {
                em.persist(new Author("Kapitan " + String.format("%02d", i)));
            }
            em.getTransaction().commit();
            em.clear();

            int pageSize = 5;

            // ═══════════════════════════════════════════════════════════
            // STRONA 1 (pageNumber = 0)
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n🔍 OBSERWUJ SQL - czy widzisz LIMIT i OFFSET?");
            System.out.println("\n📄 STRONA 1 (pageNumber=0, pageSize=5):");
            System.out.println("   Formuła: OFFSET = pageNumber * pageSize = 0 * 5 = 0");
            System.out.println("   SQL: ... LIMIT 5 OFFSET 0\n");

            List<Author> page1 = em.createQuery("SELECT a FROM Author a ORDER BY a.name", Author.class)
                    .setFirstResult(0 * pageSize)  // OFFSET = 0
                    .setMaxResults(pageSize)        // LIMIT = 5
                    .getResultList();

            page1.forEach(a -> System.out.println("   - " + a.getName()));

            // ═══════════════════════════════════════════════════════════
            // STRONA 3 (pageNumber = 2)
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n📄 STRONA 3 (pageNumber=2, pageSize=5):");
            System.out.println("   Formuła: OFFSET = pageNumber * pageSize = 2 * 5 = 10");
            System.out.println("   SQL: ... LIMIT 5 OFFSET 10\n");

            List<Author> page3 = em.createQuery("SELECT a FROM Author a ORDER BY a.name", Author.class)
                    .setFirstResult(2 * pageSize)  // OFFSET = 10 (pomijamy pierwsze 10)
                    .setMaxResults(pageSize)        // LIMIT = 5
                    .getResultList();

            page3.forEach(a -> System.out.println("   - " + a.getName()));

            // ═══════════════════════════════════════════════════════════
            // OBLICZANIE CAŁKOWITEJ LICZBY STRON
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n📊 STATYSTYKI:");
            Long totalCount = em.createQuery("SELECT COUNT(a) FROM Author a", Long.class)
                    .getSingleResult();
            long totalPages = (long) Math.ceil((double) totalCount / pageSize);

            System.out.println("   Całkowita liczba rekordów: " + totalCount);
            System.out.println("   Rozmiar strony: " + pageSize);
            System.out.println("   Całkowita liczba stron: " + totalPages);

            // ═══════════════════════════════════════════════════════════
            // 💡 EKSPERYMENT 1: Ostatnia strona (może mieć mniej elementów)
            // ═══════════════════════════════════════════════════════════
            // ODKOMENTUJ poniższe linie:
            //
            // System.out.println("\n🧪 EKSPERYMENT: Ostatnia strona (może być niepełna):");
            // int lastPageNumber = (int) totalPages - 1;
            // List<Author> lastPage = em.createQuery("SELECT a FROM Author a ORDER BY a.name", Author.class)
            //         .setFirstResult(lastPageNumber * pageSize)
            //         .setMaxResults(pageSize)
            //         .getResultList();
            // System.out.println("   Strona " + (lastPageNumber + 1) + " ma " + lastPage.size() + " elementów");
            // lastPage.forEach(a -> System.out.println("   - " + a.getName()));
            //
            // ❓ PYTANIE: Ile elementów ma ostatnia strona?
            // 💡 ODPOWIEDŹ: 25 % 5 = 0, więc ostatnia strona ma pełne 5 elementów!

            // ═══════════════════════════════════════════════════════════
            // 💡 EKSPERYMENT 2: Zmień rozmiar strony
            // ═══════════════════════════════════════════════════════════
            // ODKOMENTUJ i zmień pageSize na 10:
            //
            // System.out.println("\n🧪 EKSPERYMENT 2: Większy rozmiar strony (10):");
            // int biggerPageSize = 10;
            // List<Author> bigPage = em.createQuery("SELECT a FROM Author a ORDER BY a.name", Author.class)
            //         .setFirstResult(0)
            //         .setMaxResults(biggerPageSize)
            //         .getResultList();
            // System.out.println("   Strona 1 z pageSize=10 ma " + bigPage.size() + " elementów");
            // long newTotalPages = (long) Math.ceil((double) totalCount / biggerPageSize);
            // System.out.println("   Teraz mamy tylko " + newTotalPages + " strony!");
            //
            // ❓ PYTANIE: Jak rozmiar strony wpływa na liczbę stron?
            // 💡 ODPOWIEDŹ: Większy pageSize = mniej stron, ale więcej danych na raz!

            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ Ćwiczenie 2 zakończone!");
            System.out.println("💡 Teraz odkomentuj EKSPERYMENTY i pobaw się paginacją!");
            System.out.println("=".repeat(60));

        } finally {
            em.close();
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 3: Projekcja DTO - tylko potrzebne kolumny
     * ════════════════════════════════════════════════════════════════
     * 🎯 POZIOM: ŚREDNI (uzupełnij TODO)
     *
     * KONTEKST:
     * Do listy dropdown potrzebujesz tylko ID i nazwę autora.
     * Nie ma sensu ładować całej encji z wszystkimi relacjami!
     *
     * ZADANIE:
     * 1. Uzupełnij zapytanie JPQL z SELECT a.id, a.name
     * 2. Uruchom i OBSERWUJ SQL - ile kolumn jest w SELECT?
     * 3. Porównaj z pełną encją (więcej kolumn = więcej danych)
     */
    public void exercise3_projection() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 3: Projekcja DTO - Uzupełnij TODO");
        System.out.println("=".repeat(60));

        EntityManager em = emf.createEntityManager();

        try {
            // Utwórz dane testowe
            em.getTransaction().begin();
            em.persist(new Author("Jack Sparrow"));
            em.persist(new Author("Hector Barbossa"));
            em.persist(new Author("Davy Jones"));
            em.getTransaction().commit();
            em.clear();

            System.out.println("\n🔍 OBSERWUJ SQL - ile kolumn jest w SELECT?");

            // ═══════════════════════════════════════════════════════════
            // PORÓWNANIE: Pełna encja vs Projekcja
            // ═══════════════════════════════════════════════════════════

            System.out.println("\n1️⃣ PEŁNA ENCJA (wszystkie kolumny):");
            System.out.println("   SQL: SELECT id, name, created_at, ... FROM authors");
            List<Author> fullEntities = em.createQuery("SELECT a FROM Author a", Author.class)
                    .setMaxResults(3)
                    .getResultList();
            fullEntities.forEach(a -> System.out.println("   - " + a));

            // ═══════════════════════════════════════════════════════════
            // TODO: Uzupełnij projekcję
            // ═══════════════════════════════════════════════════════════

            System.out.println("\n2️⃣ PROJEKCJA (tylko id, name):");
            System.out.println("   SQL: SELECT id, name FROM authors (tylko 2 kolumny!)");

            // TODO: Uzupełnij zapytanie JPQL
            // Hint: SELECT a.id, a.name FROM Author a
            List<Object[]> projectionResults = em.createQuery(
                    "SELECT /* TODO: Uzupełnij: a.id, a.name */ FROM Author a",
                    Object[].class)
                    .setMaxResults(3)
                    .getResultList();

            // TODO: Wyświetl wyniki
            // Hint: Object[] row = ...; Long id = (Long) row[0]; String name = (String) row[1];
            for (Object[] row : projectionResults) {
                // TODO: Pobierz id i name z tablicy row[]
                // Long id = ...
                // String name = ...
                // System.out.println("   - AuthorDTO{id=" + id + ", name='" + name + "'}");
            }

            // ═══════════════════════════════════════════════════════════
            // KORZYŚCI PROJEKCJI
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n✅ KORZYŚCI PROJEKCJI:");
            System.out.println("   ✓ Mniej danych przesyłanych z bazy (tylko 2 kolumny zamiast wszystkich)");
            System.out.println("   ✓ Brak proxy Hibernate (czysty obiekt Java)");
            System.out.println("   ✓ Szybsze dla list dropdown, autocomplete, raportów");
            System.out.println("   ✓ Mniejsze zużycie pamięci");

            // ═══════════════════════════════════════════════════════════
            // 💡 BONUS: SELECT NEW z record DTO (Java 16+)
            // ═══════════════════════════════════════════════════════════
            // ODKOMENTUJ jeśli chcesz zobaczyć eleganckie rozwiązanie:
            //
            // record AuthorDTO(Long id, String name) {}
            //
            // System.out.println("\n🧪 BONUS: SELECT NEW z record DTO:");
            // List<AuthorDTO> dtos = em.createQuery(
            //         "SELECT NEW pl.przemekzagorski.training.hibernate.PerformanceExercises$AuthorDTO(a.id, a.name) FROM Author a",
            //         AuthorDTO.class)
            //         .setMaxResults(3)
            //         .getResultList();
            // dtos.forEach(dto -> System.out.println("   - " + dto));
            //
            // 💡 WSKAZÓWKA: Record DTO to najczystsze rozwiązanie (Java 16+)!

            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ Ćwiczenie 3 zakończone!");
            System.out.println("💡 Sprawdź rozwiązanie w PerformanceExercisesSolutions.java");
            System.out.println("=".repeat(60));

        } finally {
            em.close();
        }
    }

    // DTO dla Exercise 3 (odkomentuj jeśli chcesz użyć SELECT NEW)
    // public record AuthorDTO(Long id, String name) {}

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 4: Read-only mode - optymalizacja raportów
     * ════════════════════════════════════════════════════════════════
     * 🎯 POZIOM: TRUDNY (napisz od zera z pomocą wskazówek)
     *
     * KONTEKST:
     * Generujesz raport PDF z listą wszystkich kapitanów i ich statystyk.
     * NIE modyfikujesz żadnych danych - tylko czytasz.
     * Read-only mode wyłącza dirty checking = oszczędność pamięci i CPU!
     *
     * ZADANIE:
     * 1. Napisz zapytanie z hintem "org.hibernate.readOnly" = true
     * 2. Spróbuj zmodyfikować pobraną encję
     * 3. Wywołaj flush() i sprawdź czy zmiana została zapisana
     * 4. Wyświetl wynik eksperymentu
     *
     * STRUKTURA:
     * - Utwórz EntityManager i transakcję
     * - Pobierz autora z hintem readOnly=true
     * - Zmień nazwę autora
     * - Wywołaj flush()
     * - Sprawdź w bazie czy zmiana została zapisana (nie powinna!)
     *
     * 💡 WSKAZÓWKI:
     * - em.createQuery("SELECT a FROM Author a", Author.class)
     * - .setHint("org.hibernate.readOnly", true)
     * - author.setName("ZMIENIONA NAZWA");
     * - em.flush();
     * - em.clear(); // wyczyść cache
     * - Author reloaded = em.find(Author.class, authorId);
     * - Porównaj reloaded.getName() z oryginalną nazwą
     *
     * 🆘 Jeśli utkniesz, sprawdź PerformanceExercisesSolutions.solution4_readOnlyMode()
     */
    public void exercise4_readOnlyMode() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 4: Read-only Mode - Napisz od zera");
        System.out.println("=".repeat(60));

        // TODO: Utwórz EntityManager
        // EntityManager em = emf.createEntityManager();

        // TODO: Utwórz autora testowego
        // em.getTransaction().begin();
        // Author testAuthor = new Author("Original Name");
        // em.persist(testAuthor);
        // em.getTransaction().commit();
        // Long authorId = testAuthor.getId();
        // em.clear();

        // TODO: Pobierz autora z hintem readOnly=true
        // em.getTransaction().begin();
        // List<Author> readOnlyAuthors = em.createQuery("SELECT a FROM Author a WHERE a.id = :id", Author.class)
        //         .setParameter("id", authorId)
        //         .setHint("org.hibernate.readOnly", true)
        //         .getResultList();

        // TODO: Spróbuj zmodyfikować
        // Author author = readOnlyAuthors.get(0);
        // String originalName = author.getName();
        // author.setName("ZMIENIONA NAZWA");
        // em.flush();
        // em.getTransaction().commit();

        // TODO: Sprawdź w bazie
        // em.clear();
        // Author reloaded = em.find(Author.class, authorId);
        // System.out.println("Oryginalna nazwa: " + originalName);
        // System.out.println("Nazwa w bazie: " + reloaded.getName());
        // if (reloaded.getName().equals(originalName)) {
        //     System.out.println("✅ Read-only działa! Zmiana NIE została zapisana.");
        // }

        // TODO: Zamknij EntityManager
        // em.close();

        System.out.println("\n💡 WSKAZÓWKA: Sprawdź strukturę w komentarzach powyżej!");
        System.out.println("🆘 Jeśli utkniesz, zobacz PerformanceExercisesSolutions.java");
        System.out.println("=".repeat(60));
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * ĆWICZENIE 5: Batch processing - flush/clear dla dużych zbiorów
     * ════════════════════════════════════════════════════════════════
     * 🎯 POZIOM: TRUDNY (napisz od zera z pomocą wskazówek)
     *
     * KONTEKST:
     * Importujesz 10000 rekordów z CSV do bazy danych.
     * Po jakimś czasie aplikacja zwalnia i zużywa coraz więcej pamięci.
     * Problem: Persistence Context trzyma WSZYSTKIE encje w pamięci!
     *
     * ROZWIĄZANIE:
     * Batch processing z flush/clear co N rekordów.
     *
     * ZADANIE:
     * 1. Napisz pętlę która tworzy 1000 autorów (dla szybkości demo)
     * 2. Co 100 rekordów wywołaj flush() i clear()
     * 3. Zmierz czas wykonania
     * 4. Wyświetl postęp (batch 1/10, 2/10, ...)
     *
     * STRUKTURA:
     * - Utwórz EntityManager i transakcję
     * - Zmierz czas startowy (System.currentTimeMillis())
     * - Pętla for od 1 do 1000
     * - Twórz autora i persist()
     * - Co 100 rekordów: flush() + clear() + wyświetl postęp
     * - Commit transakcji
     * - Zmierz czas końcowy i wyświetl różnicę
     *
     * 💡 WSKAZÓWKI:
     * - int totalRecords = 1000;
     * - int batchSize = 100;
     * - for (int i = 1; i <= totalRecords; i++)
     * - if (i % batchSize == 0) { em.flush(); em.clear(); }
     * - System.out.println("Batch " + (i / batchSize) + "/" + (totalRecords / batchSize));
     * - long duration = endTime - startTime;
     *
     * ⚠️ UWAGA:
     * Bez flush/clear: OutOfMemoryError przy 10000+ rekordów!
     * Z flush/clear: Stała pamięć niezależnie od liczby rekordów!
     *
     * 🆘 Jeśli utkniesz, sprawdź PerformanceExercisesSolutions.solution5_batchProcessing()
     */
    public void exercise5_batchProcessing() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 ĆWICZENIE 5: Batch Processing - Napisz od zera");
        System.out.println("=".repeat(60));

        // TODO: Utwórz EntityManager
        // EntityManager em = emf.createEntityManager();

        // TODO: Zdefiniuj parametry
        // int totalRecords = 1000;
        // int batchSize = 100;

        // TODO: Rozpocznij transakcję i zmierz czas
        // em.getTransaction().begin();
        // long startTime = System.currentTimeMillis();

        // TODO: Pętla z batch processing
        // for (int i = 1; i <= totalRecords; i++) {
        //     Author author = new Author("Captain Batch " + i);
        //     em.persist(author);
        //
        //     if (i % batchSize == 0) {
        //         em.flush();  // wyślij INSERT do bazy
        //         em.clear();  // wyczyść Persistence Context (zwolnij pamięć!)
        //         System.out.println("Batch " + (i / batchSize) + "/" + (totalRecords / batchSize) + " - zapisano " + i + " rekordów");
        //     }
        // }

        // TODO: Commit i zmierz czas
        // em.getTransaction().commit();
        // long endTime = System.currentTimeMillis();
        // System.out.println("✅ Zakończono w " + (endTime - startTime) + " ms");

        // TODO: Zamknij EntityManager
        // em.close();

        System.out.println("\n💡 WSKAZÓWKA: Sprawdź strukturę w komentarzach powyżej!");
        System.out.println("⚠️ WAŻNE: flush() = zapis do bazy, clear() = zwolnienie pamięci!");
        System.out.println("🆘 Jeśli utkniesz, zobacz PerformanceExercisesSolutions.java");
        System.out.println("=".repeat(60));
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * 💡 ZADANIE DOMOWE: Analiza wydajności e-commerce
     * ════════════════════════════════════════════════════════════════
     *
     * SCENARIUSZ:
     * Masz aplikację e-commerce. Strona produktu ładuje:
     * - Produkt (1 zapytanie)
     * - Kategorie produktu (N zapytań - LAZY)
     * - Opinie użytkowników (N zapytań - LAZY)
     * - Zdjęcia produktu (N zapytań - LAZY)
     *
     * PYTANIA DO ANALIZY:
     * 1. Ile zapytań SQL generuje taka strona dla 1 produktu z 3 kategoriami, 10 opiniami, 5 zdjęciami?
     *    Odpowiedź: 1 (produkt) + 1 (kategorie) + 1 (opinie) + 1 (zdjęcia) = 4 zapytania (problem N+1!)
     *
     * 2. Jak zoptymalizować za pomocą technik poznanych dzisiaj?
     *    - JOIN FETCH: gdy ZAWSZE potrzebujesz tych danych (np. kategorie, główne zdjęcie)
     *    - @BatchSize: gdy czasami potrzebujesz (np. opinie w rozwijanej sekcji)
     *    - Projekcja DTO: gdy potrzebujesz tylko podstawowych info (lista produktów)
     *    - Paginacja: dla opinii (po 10 na stronie)
     *
     * 3. Kiedy użyć JOIN FETCH a kiedy @BatchSize?
     *    - JOIN FETCH: relacje ZAWSZE potrzebne, małe kolekcje (kategorie, główne zdjęcie)
     *    - @BatchSize: relacje CZASAMI potrzebne, duże kolekcje (opinie, wszystkie zdjęcia)
     *
     * PRZYKŁADOWE ROZWIĄZANIE:
     * ```java
     * // Strona produktu - JOIN FETCH dla kategorii i głównego zdjęcia
     * @Query("SELECT p FROM Product p " +
     *        "LEFT JOIN FETCH p.categories " +
     *        "LEFT JOIN FETCH p.mainImage " +
     *        "WHERE p.id = :id")
     * Optional<Product> findByIdWithDetails(@Param("id") Long id);
     *
     * // Opinie z paginacją
     * @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.createdAt DESC")
     * List<Review> findReviewsByProductId(@Param("productId") Long productId, Pageable pageable);
     *
     * // Lista produktów - tylko DTO (id, name, price, mainImageUrl)
     * @Query("SELECT NEW com.example.ProductDTO(p.id, p.name, p.price, p.mainImage.url) " +
     *        "FROM Product p")
     * List<ProductDTO> findAllProductsForList();
     * ```
     *
     * 🎯 WNIOSEK:
     * Nie ma jednego uniwersalnego rozwiązania!
     * Wybieraj technikę w zależności od use case:
     * - Strona produktu: JOIN FETCH (zawsze potrzebne)
     * - Lista produktów: Projekcja DTO (tylko podstawowe info)
     * - Opinie: Paginacja + @BatchSize (duże kolekcje)
     * - Raporty: Read-only + Projekcja (tylko odczyt)
     * - Import: Batch processing (flush/clear co 100)
     */

    /**
     * Uruchom wszystkie ćwiczenia.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║               ĆWICZENIA - WYDAJNOŚĆ HIBERNATE                    ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║  PROGRESJA TRUDNOŚCI:                                            ║");
        System.out.println("║  1. Cache L1 (ŁATWY) - uruchom i obserwuj                        ║");
        System.out.println("║  2. Paginacja (ŁATWY) - uruchom i eksperymentuj                  ║");
        System.out.println("║  3. Projekcja DTO (ŚREDNI) - uzupełnij TODO                      ║");
        System.out.println("║  4. Read-only mode (TRUDNY) - napisz od zera                     ║");
        System.out.println("║  5. Batch processing (TRUDNY) - napisz od zera                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("🚀 Uruchamiam ćwiczenia...\n");

        PerformanceExercises exercises = new PerformanceExercises();

        try {
            // Exercise 1-2: ŁATWE - gotowy kod, uruchom i obserwuj!
            exercises.exercise1_cacheL1Verification();
            exercises.exercise2_pagination();

            // Exercise 3: ŚREDNI - uzupełnij TODO
            exercises.exercise3_projection();

            // Exercise 4-5: TRUDNE - napisz od zera
            exercises.exercise4_readOnlyMode();
            exercises.exercise5_batchProcessing();

        } finally {
            exercises.emf.close();
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Wszystkie ćwiczenia wykonane!");
        System.out.println("💡 Sprawdź rozwiązania w PerformanceExercisesSolutions.java");
        System.out.println("=".repeat(60));
    }
}
