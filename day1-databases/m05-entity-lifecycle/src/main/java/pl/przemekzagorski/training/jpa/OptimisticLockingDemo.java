package pl.przemekzagorski.training.jpa;

import jakarta.persistence.*;
import pl.przemekzagorski.training.jpa.entity.Pirate;

import java.math.BigDecimal;

/**
 * Demonstracja Optimistic Locking z @Version.
 *
 * PROBLEM: Lost Update
 * - Dwóch użytkowników jednocześnie edytuje ten sam rekord
 * - Każdy zapisuje swoje zmiany
 * - Zmiany pierwszego użytkownika są nadpisane!
 *
 * ROZWIĄZANIE: @Version
 * - Hibernate sprawdza czy wersja encji się nie zmieniła
 * - Jeśli ktoś inny zmienił rekord, rzuca OptimisticLockException
 * - Transakcja jest wycofywana, można ponowić próbę
 */
public class OptimisticLockingDemo {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("lifecycle-pu");

        try {
            System.out.println("=".repeat(60));
            System.out.println("DEMO: OPTIMISTIC LOCKING (@Version)");
            System.out.println("=".repeat(60));

            // 1. Zapisz pirata
            Long pirateId = createPirate(emf);

            // 2. Symuluj conflict - dwa równoczesne edytowania
            simulateConcurrentUpdate(emf, pirateId);

            // 3. Pokaż jak obsłużyć OptimisticLockException
            demonstrateConflictHandling(emf, pirateId);

        } finally {
            emf.close();
        }
    }

    private static Long createPirate(EntityManagerFactory emf) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("1. TWORZENIE PIRATA Z @VERSION");
        System.out.println("-".repeat(50));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));
            em.persist(jack);

            em.getTransaction().commit();

            System.out.println("✅ Utworzono: " + jack);
            System.out.println("   Początkowa wersja: " + jack.getVersion());

            return jack.getId();
        } finally {
            em.close();
        }
    }

    private static void simulateConcurrentUpdate(EntityManagerFactory emf, Long pirateId) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("2. SYMULACJA KONFLIKTU (LOST UPDATE)");
        System.out.println("-".repeat(50));

        // Session 1 - pobiera pirata
        EntityManager em1 = emf.createEntityManager();
        em1.getTransaction().begin();
        Pirate pirateSession1 = em1.find(Pirate.class, pirateId);
        System.out.println("\n📖 Session 1 pobrała: " + pirateSession1);

        // Session 2 - pobiera tego samego pirata
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Pirate pirateSession2 = em2.find(Pirate.class, pirateId);
        System.out.println("📖 Session 2 pobrała: " + pirateSession2);

        // Session 1 - zmienia bounty i zapisuje
        pirateSession1.setBounty(new BigDecimal("150000"));
        System.out.println("\n✏️  Session 1 zmienia bounty na 150,000");
        em1.getTransaction().commit();
        em1.close();
        System.out.println("✅ Session 1 zapisała zmiany (version=" +
                emf.createEntityManager().find(Pirate.class, pirateId).getVersion() + ")");

        // Session 2 - próbuje zapisać swoje zmiany
        pirateSession2.setBounty(new BigDecimal("200000"));
        System.out.println("\n✏️  Session 2 zmienia bounty na 200,000");

        try {
            em2.getTransaction().commit();
            System.out.println("❓ Session 2 zapisała zmiany - BEZ @Version byłby Lost Update!");
        } catch (RollbackException e) {
            if (e.getCause() instanceof OptimisticLockException) {
                System.out.println("❌ OptimisticLockException! Ktoś inny zmienił rekord.");
                System.out.println("   Transakcja Session 2 wycofana - brak Lost Update!");
            }
        } finally {
            em2.close();
        }

        // Pokaż aktualny stan
        EntityManager em = emf.createEntityManager();
        Pirate current = em.find(Pirate.class, pirateId);
        System.out.println("\n📊 Aktualny stan w bazie: bounty=" + current.getBounty());
        em.close();
    }

    private static void demonstrateConflictHandling(EntityManagerFactory emf, Long pirateId) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("3. OBSŁUGA KONFLIKTU - RETRY PATTERN");
        System.out.println("-".repeat(50));

        int maxRetries = 3;
        int attempt = 0;
        boolean success = false;

        while (!success && attempt < maxRetries) {
            attempt++;
            EntityManager em = emf.createEntityManager();

            try {
                em.getTransaction().begin();

                // Pobierz aktualną wersję
                Pirate pirate = em.find(Pirate.class, pirateId);
                System.out.printf("%n🔄 Próba %d: pobrano wersję %d, bounty=%s%n",
                        attempt, pirate.getVersion(), pirate.getBounty());

                // Symuluj opóźnienie (inny użytkownik może zmienić)
                if (attempt == 1) {
                    // Symuluj że ktoś inny zmienił w międzyczasie
                    simulateOtherUserChange(emf, pirateId);
                }

                // Nasza zmiana
                pirate.setBounty(pirate.getBounty().add(new BigDecimal("10000")));
                System.out.println("   Zwiększam bounty o 10,000...");

                em.getTransaction().commit();
                success = true;
                System.out.println("✅ Sukces! Nowa wersja: " + pirate.getVersion());

            } catch (RollbackException e) {
                System.out.println("❌ Konflikt wykryty - ponawiam za chwilę...");
                // W prawdziwej aplikacji: Thread.sleep() lub eksponencjalny backoff
            } finally {
                em.close();
            }
        }

        if (!success) {
            System.out.println("❌ Nie udało się po " + maxRetries + " próbach!");
        }
    }

    private static void simulateOtherUserChange(EntityManagerFactory emf, Long pirateId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Pirate pirate = em.find(Pirate.class, pirateId);
        pirate.setBounty(pirate.getBounty().add(new BigDecimal("5000")));

        em.getTransaction().commit();
        em.close();

        System.out.println("   [Inny użytkownik zmienił bounty w międzyczasie]");
    }
}
