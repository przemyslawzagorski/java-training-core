package pl.przemekzagorski.training.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import pl.przemekzagorski.training.hibernate.entity.Author;

import java.util.List;

/**
 * Demonstracja optymalizacji zapytań w Hibernate.
 *
 * ╔═══════════════════════════════════════════════════════════════╗
 * ║  TECHNIKI OPTYMALIZACJI ZAPYTAŃ                               ║
 * ╠═══════════════════════════════════════════════════════════════╣
 * ║  1. Paginacja (setFirstResult/setMaxResults)                  ║
 * ║  2. Projekcja (SELECT new DTO)                                ║
 * ║  3. Read-only mode                                            ║
 * ║  4. Query hints                                               ║
 * ╚═══════════════════════════════════════════════════════════════╝
 *
 * URUCHOM I OBSERWUJ ZACHOWANIE HIBERNATE!
 */
public class QueryOptimizationDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Query Optimization Demo");
        System.out.println("==============================\n");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("advanced-pu");

        try {
            setupTestData(emf);

            demonstratePagination(emf);
            demonstrateProjection(emf);
            demonstrateReadOnly(emf);

        } finally {
            emf.close();
        }
    }

    private static void setupTestData(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // 20 autorów dla demonstracji paginacji
        for (int i = 1; i <= 20; i++) {
            Author author = new Author("Kapitan " + String.format("%02d", i));
            em.persist(author);
        }

        em.getTransaction().commit();
        em.close();

        System.out.println("📊 Utworzono 20 autorów do testów\n");
    }

    /**
     * 1️⃣ PAGINACJA - Nie ładuj wszystkiego do pamięci!
     */
    private static void demonstratePagination(EntityManagerFactory emf) {
        System.out.println("1️⃣ PAGINACJA - setFirstResult() / setMaxResults()");
        System.out.println("=".repeat(55) + "\n");

        EntityManager em = emf.createEntityManager();

        int pageSize = 5;
        int pageNumber = 0; // pierwsza strona

        System.out.println(">>> Pobieram stronę " + (pageNumber + 1) + " (rozmiar strony: " + pageSize + "):");

        TypedQuery<Author> query = em.createQuery("SELECT a FROM Author a ORDER BY a.name", Author.class);
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);

        List<Author> page1 = query.getResultList();

        for (Author author : page1) {
            System.out.println("   " + author.getName());
        }

        // Druga strona
        pageNumber = 1;
        System.out.println("\n>>> Pobieram stronę " + (pageNumber + 1) + ":");

        query.setFirstResult(pageNumber * pageSize);
        List<Author> page2 = query.getResultList();

        for (Author author : page2) {
            System.out.println("   " + author.getName());
        }

        System.out.println("\n✅ Paginacja pozwala ładować dane porcjami!\n");

        em.close();
    }

    /**
     * 2️⃣ PROJEKCJA - Pobieraj tylko potrzebne kolumny!
     */
    private static void demonstrateProjection(EntityManagerFactory emf) {
        System.out.println("2️⃣ PROJEKCJA - SELECT tylko potrzebne dane");
        System.out.println("=".repeat(55) + "\n");

        EntityManager em = emf.createEntityManager();

        System.out.println(">>> Zamiast SELECT a FROM Author a (cała encja):");
        System.out.println(">>> Używam SELECT a.id, a.name FROM Author a (tylko 2 kolumny):\n");

        // Projekcja - pobieramy tylko id i name jako Object[]
        List<Object[]> results = em.createQuery(
                "SELECT a.id, a.name FROM Author a WHERE a.id <= 5", Object[].class)
                .getResultList();

        for (Object[] row : results) {
            Long id = (Long) row[0];
            String name = (String) row[1];
            System.out.println("   ID=" + id + ", Name=" + name);
        }

        System.out.println("\n✅ Projekcja = mniej danych z bazy = szybsze zapytanie!\n");

        em.close();
    }

    /**
     * 3️⃣ READ-ONLY MODE - Hibernate nie śledzi zmian!
     */
    private static void demonstrateReadOnly(EntityManagerFactory emf) {
        System.out.println("3️⃣ READ-ONLY MODE - Brak dirty checking");
        System.out.println("=".repeat(55) + "\n");

        EntityManager em = emf.createEntityManager();

        System.out.println(">>> Zapytanie z hint 'org.hibernate.readOnly' = true:");
        System.out.println("    Hibernate NIE będzie śledzić zmian w encjach!\n");

        List<Author> authors = em.createQuery("SELECT a FROM Author a WHERE a.id <= 3", Author.class)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();

        for (Author author : authors) {
            System.out.println("   " + author.getName() + " (read-only)");
        }

        System.out.println("\n✅ Read-only = mniej pamięci (brak snapshot do porównania)");
        System.out.println("   Używaj gdy wiesz, że NIE będziesz modyfikować encji!\n");

        em.close();
    }
}
