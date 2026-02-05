package pl.przemekzagorski.training.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.annotations.BatchSize;
import pl.przemekzagorski.training.hibernate.entity.Author;
import pl.przemekzagorski.training.hibernate.entity.Book;

import java.util.List;

/**
 * Demonstracja @BatchSize - optymalizacja ładowania kolekcji.
 *
 * ╔═══════════════════════════════════════════════════════════════╗
 * ║  @BatchSize - DLACZEGO TO WAŻNE?                              ║
 * ╠═══════════════════════════════════════════════════════════════╣
 * ║  Problem N+1 możemy rozwiązać na kilka sposobów:              ║
 * ║  1. JOIN FETCH - jedno duże zapytanie                         ║
 * ║  2. @BatchSize - kilka mniejszych zapytań (IN clause)         ║
 * ║  3. @EntityGraph - deklaratywne określenie co ładować         ║
 * ║                                                               ║
 * ║  @BatchSize jest KOMPROMISEM:                                 ║
 * ║  - Mniej zapytań niż N+1                                      ║
 * ║  - Mniejsze wyniki niż przy JOIN FETCH (Cartesian Product)    ║
 * ╚═══════════════════════════════════════════════════════════════╝
 *
 * URUCHOM I OBSERWUJ LOGI SQL!
 */
public class BatchSizeDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ @BatchSize Demo - Optymalizacja ładowania");
        System.out.println("===============================================\n");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("advanced-pu");

        try {
            setupTestData(emf);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("📊 Porównanie: bez @BatchSize vs z @BatchSize");
            System.out.println("=".repeat(60) + "\n");

            demonstrateBatchLoading(emf);

        } finally {
            emf.close();
        }
    }

    private static void setupTestData(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // 10 autorów, każdy z 2 książkami
        for (int i = 1; i <= 10; i++) {
            Author author = new Author("Kapitan #" + i);
            author.addBook(new Book("Przygoda " + i + " - Tom 1", 1700 + i));
            author.addBook(new Book("Przygoda " + i + " - Tom 2", 1700 + i));
            em.persist(author);
        }

        em.getTransaction().commit();
        em.close();

        System.out.println("📊 Utworzono 10 autorów, każdy z 2 książkami (20 książek)");
    }

    /**
     * Z @BatchSize(size = 5) na encji Author.books:
     *
     * Zamiast 10 zapytań (po jednym na autora), Hibernate wykona:
     * - 1 zapytanie na autorów
     * - 2 zapytania na książki (5 autorów * 2 = 2 batche)
     *
     * TOTAL: 3 zapytania zamiast 11!
     */
    private static void demonstrateBatchLoading(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();

        System.out.println(">>> Pobieram wszystkich autorów:");
        List<Author> authors = em.createQuery("SELECT a FROM Author a", Author.class)
                .getResultList();

        System.out.println(">>> Pobrano " + authors.size() + " autorów\n");

        System.out.println(">>> Iteruję po autorach i dostępuję do książek:");
        System.out.println("    (Obserwuj zapytania SQL - powinny być z IN clause!)\n");

        for (Author author : authors) {
            int bookCount = author.getBooks().size();
            System.out.println("   " + author.getName() + " → " + bookCount + " książek");
        }

        System.out.println("\n✅ Z @BatchSize mniej zapytań niż N+1!");
        System.out.println("   @BatchSize(size=5) + 10 autorów = ~3 zapytania");
        System.out.println("   Bez optymalizacji byłoby: 1 + 10 = 11 zapytań");

        em.close();
    }
}
