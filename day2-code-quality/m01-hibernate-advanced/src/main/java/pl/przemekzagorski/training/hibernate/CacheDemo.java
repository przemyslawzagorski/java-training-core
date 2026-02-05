package pl.przemekzagorski.training.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import pl.przemekzagorski.training.hibernate.entity.Author;

/**
 * Demonstracja Cache Level 1 (Session Cache).
 *
 * Cache L1 jest ZAWSZE włączony i działa w obrębie jednej sesji.
 */
public class CacheDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Cache L1 (Session Cache) Demo");
        System.out.println("====================================\n");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("advanced-pu");

        try {
            // Przygotuj dane
            Long authorId = setupTestData(emf);

            demonstrateCacheL1(emf, authorId);
            demonstrateCacheL1Clear(emf, authorId);
            demonstrateCacheL1BetweenSessions(emf, authorId);

        } finally {
            emf.close();
        }
    }

    private static Long setupTestData(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Author author = new Author("Jack Sparrow");
        em.persist(author);

        em.getTransaction().commit();
        Long id = author.getId();
        em.close();

        System.out.println("📊 Utworzono autora z ID=" + id + "\n");
        return id;
    }

    /**
     * Cache L1 - drugie pobranie nie idzie do bazy!
     */
    private static void demonstrateCacheL1(EntityManagerFactory emf, Long authorId) {
        System.out.println("1️⃣ Cache L1 - ponowne pobranie w tej samej sesji");
        System.out.println("=".repeat(50) + "\n");

        EntityManager em = emf.createEntityManager();

        System.out.println(">>> Pierwsze pobranie - idzie do bazy:");
        Author author1 = em.find(Author.class, authorId);
        System.out.println("   Pobrano: " + author1);

        System.out.println("\n>>> Drugie pobranie - z cache L1 (BEZ zapytania SQL!):");
        Author author2 = em.find(Author.class, authorId);
        System.out.println("   Pobrano: " + author2);

        System.out.println("\n>>> Czy to ten sam obiekt?");
        System.out.println("   author1 == author2: " + (author1 == author2));
        System.out.println("   ✅ TAK! Cache L1 zwraca TEN SAM obiekt!\n");

        em.close();
    }

    /**
     * clear() czyści cache L1
     */
    private static void demonstrateCacheL1Clear(EntityManagerFactory emf, Long authorId) {
        System.out.println("2️⃣ Czyszczenie Cache L1 - em.clear()");
        System.out.println("=".repeat(50) + "\n");

        EntityManager em = emf.createEntityManager();

        System.out.println(">>> Pierwsze pobranie:");
        Author author1 = em.find(Author.class, authorId);
        System.out.println("   Pobrano: " + author1);

        System.out.println("\n>>> Czyszczę cache: em.clear()");
        em.clear();

        System.out.println("\n>>> Drugie pobranie - musi iść do bazy (cache wyczyszczony):");
        Author author2 = em.find(Author.class, authorId);
        System.out.println("   Pobrano: " + author2);

        System.out.println("\n>>> Czy to ten sam obiekt?");
        System.out.println("   author1 == author2: " + (author1 == author2));
        System.out.println("   ❌ NIE! To NOWY obiekt (cache był wyczyszczony)\n");

        em.close();
    }

    /**
     * Cache L1 NIE działa między sesjami
     */
    private static void demonstrateCacheL1BetweenSessions(EntityManagerFactory emf, Long authorId) {
        System.out.println("3️⃣ Cache L1 NIE działa między sesjami");
        System.out.println("=".repeat(50) + "\n");

        System.out.println(">>> Sesja 1 - pobranie:");
        EntityManager em1 = emf.createEntityManager();
        Author author1 = em1.find(Author.class, authorId);
        System.out.println("   Pobrano: " + author1);
        em1.close();
        System.out.println("   Sesja 1 zamknięta.\n");

        System.out.println(">>> Sesja 2 - nowe pobranie (nowe zapytanie SQL!):");
        EntityManager em2 = emf.createEntityManager();
        Author author2 = em2.find(Author.class, authorId);
        System.out.println("   Pobrano: " + author2);

        System.out.println("\n>>> Czy to ten sam obiekt?");
        System.out.println("   author1 == author2: " + (author1 == author2));
        System.out.println("   ❌ NIE! Różne sesje = różne obiekty");
        System.out.println("   ℹ️ Do współdzielenia między sesjami potrzeba Cache L2\n");

        em2.close();
    }
}

