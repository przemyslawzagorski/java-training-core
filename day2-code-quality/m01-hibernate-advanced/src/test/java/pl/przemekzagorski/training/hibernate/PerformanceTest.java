package pl.przemekzagorski.training.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import pl.przemekzagorski.training.hibernate.entity.Author;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║         TESTY - WYDAJNOŚĆ HIBERNATE                              ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  Testujemy cache L1, paginację, projekcję i batch processing     ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
@DisplayName("Performance Tests - Hibernate Advanced")
class PerformanceTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    static void setUpFactory() {
        emf = Persistence.createEntityManagerFactory("advanced-pu");
    }

    @AfterAll
    static void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        // Wyczyść dane przed każdym testem
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM Author").executeUpdate();
        em.getTransaction().commit();
        em.clear();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY CACHE L1
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Cache L1 - ta sama instancja przy wielokrotnym find()")
    void cacheL1_shouldReturnSameInstance() {
        // Given - utwórz autora
        em.getTransaction().begin();
        Author author = new Author("Jack Sparrow");
        em.persist(author);
        em.getTransaction().commit();
        Long authorId = author.getId();

        // Wyczyść i zacznij od nowa
        em.clear();

        // When - pobierz 3 razy
        Author author1 = em.find(Author.class, authorId);
        Author author2 = em.find(Author.class, authorId);
        Author author3 = em.find(Author.class, authorId);

        // Then - wszystkie referencje powinny być IDENTYCZNE
        assertNotNull(author1);
        assertSame(author1, author2, "Drugie pobranie powinno zwrócić tę samą instancję z cache L1");
        assertSame(author2, author3, "Trzecie pobranie powinno zwrócić tę samą instancję z cache L1");
    }

    @Test
    @DisplayName("Cache L1 - clear() czyści cache")
    void cacheL1_clearShouldInvalidateCache() {
        // Given
        em.getTransaction().begin();
        Author author = new Author("Barbossa");
        em.persist(author);
        em.getTransaction().commit();
        Long authorId = author.getId();
        em.clear();

        // When
        Author author1 = em.find(Author.class, authorId);
        em.clear(); // wyczyść cache!
        Author author2 = em.find(Author.class, authorId);

        // Then - po clear() mamy NOWĄ instancję
        assertNotNull(author1);
        assertNotNull(author2);
        assertNotSame(author1, author2, "Po em.clear() powinniśmy dostać nową instancję");
        assertEquals(author1.getName(), author2.getName());
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY PAGINACJI
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Paginacja - pierwsza strona z 5 elementów")
    void pagination_shouldReturnFirstPage() {
        // Given - utwórz 15 autorów
        em.getTransaction().begin();
        for (int i = 1; i <= 15; i++) {
            em.persist(new Author("Kapitan " + String.format("%02d", i)));
        }
        em.getTransaction().commit();
        em.clear();

        // When - pobierz pierwszą stronę (5 elementów)
        List<Author> page1 = em.createQuery("SELECT a FROM Author a ORDER BY a.name", Author.class)
                .setFirstResult(0)
                .setMaxResults(5)
                .getResultList();

        // Then
        assertEquals(5, page1.size());
        assertEquals("Kapitan 01", page1.get(0).getName());
        assertEquals("Kapitan 05", page1.get(4).getName());
    }

    @Test
    @DisplayName("Paginacja - druga strona z 5 elementów")
    void pagination_shouldReturnSecondPage() {
        // Given
        em.getTransaction().begin();
        for (int i = 1; i <= 15; i++) {
            em.persist(new Author("Kapitan " + String.format("%02d", i)));
        }
        em.getTransaction().commit();
        em.clear();

        // When - pobierz drugą stronę
        List<Author> page2 = em.createQuery("SELECT a FROM Author a ORDER BY a.name", Author.class)
                .setFirstResult(5)  // skip 5
                .setMaxResults(5)
                .getResultList();

        // Then
        assertEquals(5, page2.size());
        assertEquals("Kapitan 06", page2.get(0).getName());
        assertEquals("Kapitan 10", page2.get(4).getName());
    }

    @Test
    @DisplayName("Paginacja - ostatnia strona może mieć mniej elementów")
    void pagination_lastPageMayHaveLessElements() {
        // Given - 12 autorów, strona po 5 → ostatnia ma tylko 2
        em.getTransaction().begin();
        for (int i = 1; i <= 12; i++) {
            em.persist(new Author("Pirat " + i));
        }
        em.getTransaction().commit();
        em.clear();

        // When - trzecia strona
        List<Author> page3 = em.createQuery("SELECT a FROM Author a ORDER BY a.name", Author.class)
                .setFirstResult(10)
                .setMaxResults(5)
                .getResultList();

        // Then - tylko 2 elementy
        assertEquals(2, page3.size());
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY PROJEKCJI (DTO)
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Projekcja - zwraca tylko wybrane kolumny")
    void projection_shouldReturnOnlySelectedColumns() {
        // Given
        em.getTransaction().begin();
        Author author = new Author("Will Turner");
        em.persist(author);
        em.getTransaction().commit();
        em.clear();

        // When - projekcja id, name
        List<Object[]> results = em.createQuery(
                        "SELECT a.id, a.name FROM Author a", Object[].class)
                .getResultList();

        // Then
        assertEquals(1, results.size());
        Object[] row = results.get(0);
        assertNotNull(row[0]); // id
        assertEquals("Will Turner", row[1]); // name
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY READ-ONLY
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Read-only - zmiany nie są zapisywane")
    void readOnly_changesShouldNotBePersisted() {
        // Given
        em.getTransaction().begin();
        Author author = new Author("Elizabeth Swann");
        em.persist(author);
        em.getTransaction().commit();
        Long authorId = author.getId();
        em.clear();

        // When - pobierz jako read-only i zmień
        em.getTransaction().begin();
        List<Author> readOnlyAuthors = em.createQuery("SELECT a FROM Author a WHERE a.id = :id", Author.class)
                .setParameter("id", authorId)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();

        Author readOnlyAuthor = readOnlyAuthors.get(0);
        String originalName = readOnlyAuthor.getName();
        readOnlyAuthor.setName("CHANGED NAME");
        em.flush();
        em.getTransaction().commit();

        // Then - sprawdź w nowej sesji
        em.clear();
        Author reloaded = em.find(Author.class, authorId);
        assertEquals(originalName, reloaded.getName(),
                "Zmiana read-only encji nie powinna być zapisana");
    }

    // ════════════════════════════════════════════════════════════════
    // TESTY BATCH PROCESSING
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Batch processing - persist wielu rekordów z flush/clear")
    void batchProcessing_shouldHandleManyRecords() {
        // Given
        int totalRecords = 100;
        int batchSize = 20;

        // When
        em.getTransaction().begin();
        for (int i = 1; i <= totalRecords; i++) {
            em.persist(new Author("BatchAuthor " + i));

            if (i % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }
        em.getTransaction().commit();
        em.clear();

        // Then
        Long count = em.createQuery("SELECT COUNT(a) FROM Author a", Long.class)
                .getSingleResult();
        assertEquals(totalRecords, count.intValue());
    }

    @Test
    @DisplayName("Batch processing - flush/clear nie traci danych")
    void batchProcessing_shouldNotLoseData() {
        // Given
        em.getTransaction().begin();

        // When - batch z flush/clear co 10
        for (int i = 1; i <= 50; i++) {
            em.persist(new Author("Pirate " + i));
            if (i % 10 == 0) {
                em.flush();
                em.clear();
            }
        }
        em.getTransaction().commit();

        // Then - weryfikuj że wszystkie rekordy są w bazie
        em.clear();
        List<Author> allAuthors = em.createQuery("SELECT a FROM Author a ORDER BY a.id", Author.class)
                .getResultList();

        assertEquals(50, allAuthors.size());
    }

    // ════════════════════════════════════════════════════════════════
    // TEST INTEGRACYJNY
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Integracja - pełen workflow z paginacją")
    void integration_paginationWorkflow() {
        // Given - utwórz 30 autorów
        em.getTransaction().begin();
        for (int i = 1; i <= 30; i++) {
            em.persist(new Author("Captain " + String.format("%02d", i)));
        }
        em.getTransaction().commit();
        em.clear();

        int pageSize = 10;

        // When - pobierz wszystkie strony
        int totalPages = 0;
        int totalRecords = 0;

        for (int page = 0; page < 5; page++) {
            List<Author> authors = em.createQuery("SELECT a FROM Author a ORDER BY a.name", Author.class)
                    .setFirstResult(page * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

            if (authors.isEmpty()) break;

            totalPages++;
            totalRecords += authors.size();
        }

        // Then
        assertEquals(3, totalPages);
        assertEquals(30, totalRecords);
    }
}
