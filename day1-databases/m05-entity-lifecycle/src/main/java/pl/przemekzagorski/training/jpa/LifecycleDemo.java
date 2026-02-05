package pl.przemekzagorski.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import pl.przemekzagorski.training.jpa.entity.Pirate;

import java.math.BigDecimal;

/**
 * Demonstracja cyklu życia encji JPA.
 *
 * Stany: TRANSIENT → MANAGED → DETACHED → REMOVED
 */
public class LifecycleDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Entity Lifecycle Demo");
        System.out.println("==========================\n");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("lifecycle-pu");

        try {
            demonstrateTransient();
            demonstrateManaged(emf);
            demonstrateDetached(emf);
            demonstrateMerge(emf);
            demonstrateRemoved(emf);
        } finally {
            emf.close();
        }
    }

    /**
     * TRANSIENT - nowy obiekt, nie jest w bazie ani śledzony
     */
    private static void demonstrateTransient() {
        System.out.println("1️⃣ TRANSIENT (NEW)");
        System.out.println("   Obiekt utworzony przez 'new', nie ma ID, nie jest w bazie\n");

        // To jest obiekt TRANSIENT
        Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("10000"));

        System.out.println("   Pirat: " + jack);
        System.out.println("   ID = " + jack.getId() + " (null - nie ma jeszcze ID)");
        System.out.println("   Stan: TRANSIENT - istnieje tylko w pamięci Java\n");
    }

    /**
     * MANAGED - encja jest śledzona przez EntityManager
     */
    private static void demonstrateManaged(EntityManagerFactory emf) {
        System.out.println("2️⃣ MANAGED (PERSISTENT)");
        System.out.println("   Encja jest śledzona - zmiany automatycznie idą do bazy\n");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // TRANSIENT
        Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("10000"));
        System.out.println("   Przed persist(): " + jack);
        System.out.println("   em.contains(jack) = " + em.contains(jack) + " (TRANSIENT)\n");

        // TRANSIENT → MANAGED
        em.persist(jack);
        System.out.println("   Po persist(): " + jack);
        System.out.println("   em.contains(jack) = " + em.contains(jack) + " (MANAGED)\n");

        // Teraz zmiana jest AUTOMATYCZNIE śledzona!
        System.out.println("   Zmieniam bounty na 25000...");
        jack.setBounty(new BigDecimal("25000"));
        // NIE TRZEBA wołać update() ani nic - Hibernate sam to wykryje!

        em.getTransaction().commit();
        System.out.println("   Po commit - zmiana zapisana w bazie automatycznie!\n");

        em.close();
    }

    /**
     * DETACHED - encja była w bazie, ale nie jest już śledzona
     */
    private static void demonstrateDetached(EntityManagerFactory emf) {
        System.out.println("3️⃣ DETACHED");
        System.out.println("   Encja nie jest już śledzona - zmiany NIE idą do bazy\n");

        EntityManager em = emf.createEntityManager();

        // Pobierz encję - jest MANAGED
        Pirate jack = em.find(Pirate.class, 1L);
        System.out.println("   Pobrano: " + jack);
        System.out.println("   em.contains(jack) = " + em.contains(jack) + " (MANAGED)\n");

        // MANAGED → DETACHED
        em.detach(jack);
        System.out.println("   Po detach():");
        System.out.println("   em.contains(jack) = " + em.contains(jack) + " (DETACHED)\n");

        // Zmiana NIE zostanie zapisana!
        jack.setBounty(new BigDecimal("999999"));
        System.out.println("   Zmieniono bounty na 999999 (ale encja jest DETACHED!)");

        em.close();

        // Weryfikacja - bounty NIE zmieniło się w bazie
        EntityManager em2 = emf.createEntityManager();
        Pirate fromDb = em2.find(Pirate.class, 1L);
        System.out.println("   Wartość w bazie: " + fromDb.getBounty());
        System.out.println("   ⚠️ Zmiana została ZIGNOROWANA bo encja była DETACHED!\n");
        em2.close();
    }

    /**
     * MERGE - przywrócenie DETACHED encji do stanu MANAGED
     */
    private static void demonstrateMerge(EntityManagerFactory emf) {
        System.out.println("4️⃣ MERGE - przywrócenie DETACHED do MANAGED");
        System.out.println("   merge() tworzy NOWĄ zarządzaną kopię\n");

        EntityManager em1 = emf.createEntityManager();
        Pirate jack = em1.find(Pirate.class, 1L);
        em1.close(); // Teraz jack jest DETACHED

        // Modyfikacja DETACHED encji
        jack.setNickname("Captain Jack Sparrow");
        System.out.println("   Zmodyfikowano DETACHED encję: " + jack);

        // Merge - przywróć do zarządzania
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();

        // UWAGA: merge zwraca NOWY obiekt MANAGED!
        Pirate managedJack = em2.merge(jack);

        System.out.println("   Po merge():");
        System.out.println("   em.contains(jack) = " + em2.contains(jack) + " (oryginalny - wciąż DETACHED)");
        System.out.println("   em.contains(managedJack) = " + em2.contains(managedJack) + " (kopia - MANAGED)");

        em2.getTransaction().commit();
        em2.close();

        // Weryfikacja
        EntityManager em3 = emf.createEntityManager();
        Pirate fromDb = em3.find(Pirate.class, 1L);
        System.out.println("   Nickname w bazie: " + fromDb.getNickname());
        System.out.println("   ✅ Zmiana została zapisana przez merge!\n");
        em3.close();
    }

    /**
     * REMOVED - encja oznaczona do usunięcia
     */
    private static void demonstrateRemoved(EntityManagerFactory emf) {
        System.out.println("5️⃣ REMOVED");
        System.out.println("   Encja oznaczona do usunięcia przy commit\n");

        // Najpierw dodaj nowego pirata
        EntityManager em1 = emf.createEntityManager();
        em1.getTransaction().begin();
        Pirate barbossa = new Pirate("Hector Barbossa", "Captain", new BigDecimal("8000"));
        em1.persist(barbossa);
        em1.getTransaction().commit();
        Long barbossaId = barbossa.getId();
        System.out.println("   Utworzono: " + barbossa);
        em1.close();

        // Teraz usuń
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();

        Pirate toRemove = em2.find(Pirate.class, barbossaId);
        System.out.println("   Przed remove(): em.contains = " + em2.contains(toRemove));

        em2.remove(toRemove);
        System.out.println("   Po remove(): em.contains = " + em2.contains(toRemove) + " (wciąż MANAGED, ale REMOVED)");

        em2.getTransaction().commit();
        System.out.println("   Po commit: usunięto z bazy");
        em2.close();

        // Weryfikacja
        EntityManager em3 = emf.createEntityManager();
        Pirate deleted = em3.find(Pirate.class, barbossaId);
        System.out.println("   Próba pobrania: " + (deleted == null ? "nie istnieje" : deleted));
        System.out.println("   ✅ Pirat został usunięty z bazy!\n");
        em3.close();
    }
}

