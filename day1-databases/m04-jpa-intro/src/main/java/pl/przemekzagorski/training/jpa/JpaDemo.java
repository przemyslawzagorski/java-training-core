package pl.przemekzagorski.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import pl.przemekzagorski.training.jpa.entity.Ship;

import java.util.List;

/**
 * Demonstracja podstawowych operacji JPA.
 *
 * Porównaj to z JDBC - ile kodu mniej!
 */
public class JpaDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ JPA Demo - Pierwsza encja");
        System.out.println("==============================\n");

        // EntityManagerFactory - ciężki obiekt, jeden na aplikację
        // Nazwa "pirates-pu" musi zgadzać się z persistence.xml
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pirates-pu");

        try {
            demonstratePersist(emf);
            demonstrateFind(emf);
            demonstrateUpdate(emf);
            demonstrateQuery(emf);
            demonstrateRemove(emf);
        } finally {
            emf.close();
        }
    }

    /**
     * CREATE - persist()
     */
    private static void demonstratePersist(EntityManagerFactory emf) {
        System.out.println("1️⃣ PERSIST - Zapisywanie encji");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Tworzenie nowych statków
        Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
        blackPearl.setCrewCapacity(100);

        Ship flyingDutchman = new Ship("Flying Dutchman", "Galleon", 46);
        flyingDutchman.setCrewCapacity(150);

        Ship interceptor = new Ship("Interceptor", "Sloop", 16);
        interceptor.setCrewCapacity(40);

        // Zapis - jedna linia zamiast całego PreparedStatement!
        em.persist(blackPearl);
        em.persist(flyingDutchman);
        em.persist(interceptor);

        em.getTransaction().commit();
        em.close();

        System.out.println("   Zapisano: " + blackPearl);
        System.out.println("   Zapisano: " + flyingDutchman);
        System.out.println("   Zapisano: " + interceptor);
        System.out.println("   ✅ ID zostały automatycznie nadane!\n");
    }

    /**
     * READ - find()
     */
    private static void demonstrateFind(EntityManagerFactory emf) {
        System.out.println("2️⃣ FIND - Pobieranie encji po ID");

        EntityManager em = emf.createEntityManager();

        // Proste pobranie po ID
        Ship ship = em.find(Ship.class, 1L);

        if (ship != null) {
            System.out.println("   Znaleziono: " + ship);
        } else {
            System.out.println("   Nie znaleziono statku o ID=1");
        }

        // Próba znalezienia nieistniejącego
        Ship notFound = em.find(Ship.class, 999L);
        System.out.println("   Statek o ID=999: " + (notFound == null ? "nie istnieje" : notFound));

        em.close();
        System.out.println();
    }

    /**
     * UPDATE - automatyczne śledzenie zmian!
     */
    private static void demonstrateUpdate(EntityManagerFactory emf) {
        System.out.println("3️⃣ UPDATE - Aktualizacja encji");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Pobierz encję - jest teraz MANAGED
        Ship ship = em.find(Ship.class, 1L);
        System.out.println("   Przed zmianą: " + ship.getCannons() + " armat");

        // Po prostu zmień pole - Hibernate SAM wykryje zmianę!
        ship.setCannons(64);
        System.out.println("   Po zmianie: " + ship.getCannons() + " armat");

        // Przy commit Hibernate automatycznie zrobi UPDATE
        em.getTransaction().commit();
        em.close();

        // Weryfikacja
        EntityManager em2 = emf.createEntityManager();
        Ship updated = em2.find(Ship.class, 1L);
        System.out.println("   Weryfikacja z bazy: " + updated.getCannons() + " armat");
        em2.close();
        System.out.println("   ✅ Nie trzeba było wołać żadnej metody update!\n");
    }

    /**
     * JPQL - zapytania obiektowe
     */
    private static void demonstrateQuery(EntityManagerFactory emf) {
        System.out.println("4️⃣ QUERY - Zapytania JPQL");

        EntityManager em = emf.createEntityManager();

        // JPQL - jak SQL, ale na obiektach!
        List<Ship> allShips = em.createQuery("SELECT s FROM Ship s", Ship.class)
                .getResultList();

        System.out.println("   Wszystkie statki:");
        allShips.forEach(s -> System.out.println("   • " + s));

        // Zapytanie z parametrem
        List<Ship> galleons = em.createQuery(
                "SELECT s FROM Ship s WHERE s.type = :type", Ship.class)
                .setParameter("type", "Galleon")
                .getResultList();

        System.out.println("\n   Tylko galeony:");
        galleons.forEach(s -> System.out.println("   ⚓ " + s.getName()));

        // Zapytanie z warunkiem
        List<Ship> bigShips = em.createQuery(
                "SELECT s FROM Ship s WHERE s.cannons > :minCannons ORDER BY s.cannons DESC",
                Ship.class)
                .setParameter("minCannons", 30)
                .getResultList();

        System.out.println("\n   Statki z > 30 armatami:");
        bigShips.forEach(s -> System.out.println("   💣 " + s.getName() + " (" + s.getCannons() + " armat)"));

        em.close();
        System.out.println();
    }

    /**
     * DELETE - remove()
     */
    private static void demonstrateRemove(EntityManagerFactory emf) {
        System.out.println("5️⃣ REMOVE - Usuwanie encji");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Najpierw pobierz (encja musi być MANAGED)
        Ship ship = em.find(Ship.class, 3L);
        if (ship != null) {
            System.out.println("   Usuwam: " + ship.getName());
            em.remove(ship);
        }

        em.getTransaction().commit();
        em.close();

        // Weryfikacja
        EntityManager em2 = emf.createEntityManager();
        long count = em2.createQuery("SELECT COUNT(s) FROM Ship s", Long.class)
                .getSingleResult();
        System.out.println("   Pozostało statków: " + count);
        em2.close();
        System.out.println();
    }
}

