package pl.przemekzagorski.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import pl.przemekzagorski.training.jpa.entity.*;

/**
 * Demonstracja relacji w JPA.
 */
public class RelationsDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ JPA Relations Demo");
        System.out.println("========================\n");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("relations-pu");

        try {
            demonstrateOneToOne(emf);
            demonstrateOneToMany(emf);
            demonstrateManyToMany(emf);
            queryRelations(emf);
        } finally {
            emf.close();
        }
    }

    /**
     * Relacja @OneToOne: Captain ↔ Ship
     */
    private static void demonstrateOneToOne(EntityManagerFactory emf) {
        System.out.println("1️⃣ @OneToOne - Kapitan i Statek");
        System.out.println("   Każdy kapitan ma jeden statek\n");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Tworzymy kapitana
        Captain jackSparrow = new Captain("Jack Sparrow", "Captain Jack");
        em.persist(jackSparrow);

        // Tworzymy statek i przypisujemy kapitana
        Ship blackPearl = new Ship("Black Pearl", "Galleon", 32);
        blackPearl.setCaptain(jackSparrow);  // Ustawia obie strony relacji!
        em.persist(blackPearl);

        em.getTransaction().commit();

        System.out.println("   Statek: " + blackPearl);
        System.out.println("   Kapitan statku: " + blackPearl.getCaptain());
        System.out.println("   Statek kapitana: " + jackSparrow.getShip());
        System.out.println();

        em.close();
    }

    /**
     * Relacja @OneToMany / @ManyToOne: Ship → CrewMembers
     */
    private static void demonstrateOneToMany(EntityManagerFactory emf) {
        System.out.println("2️⃣ @OneToMany - Statek i Załoga");
        System.out.println("   Jeden statek ma wielu członków załogi\n");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Pobieramy Black Pearl
        Ship blackPearl = em.createQuery(
            "SELECT s FROM Ship s WHERE s.name = 'Black Pearl'", Ship.class)
            .getSingleResult();

        // Dodajemy załogę używając metody pomocniczej
        blackPearl.addCrewMember(new CrewMember("Joshamee Gibbs", "First Mate"));
        blackPearl.addCrewMember(new CrewMember("Hector Barbossa", "Quartermaster"));
        blackPearl.addCrewMember(new CrewMember("Pintel", "Gunner"));
        blackPearl.addCrewMember(new CrewMember("Ragetti", "Gunner"));
        blackPearl.addCrewMember(new CrewMember("Cotton", "Sailor"));

        // CascadeType.ALL oznacza, że załoga zostanie automatycznie zapisana!
        em.getTransaction().commit();

        System.out.println("   Statek: " + blackPearl.getName());
        System.out.println("   Załoga (" + blackPearl.getCrew().size() + " osób):");
        blackPearl.getCrew().forEach(c ->
            System.out.println("   • " + c.getName() + " - " + c.getRole()));
        System.out.println();

        em.close();
    }

    /**
     * Relacja @ManyToMany: Ships ↔ Islands
     */
    private static void demonstrateManyToMany(EntityManagerFactory emf) {
        System.out.println("3️⃣ @ManyToMany - Statki i Wyspy");
        System.out.println("   Wiele statków odwiedza wiele wysp\n");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Tworzymy wyspy
        Island tortuga = new Island("Tortuga", "Caribbean Sea", false);
        Island islaDeMuerta = new Island("Isla de Muerta", "Unknown", true);
        Island portRoyal = new Island("Port Royal", "Jamaica", false);
        em.persist(tortuga);
        em.persist(islaDeMuerta);
        em.persist(portRoyal);

        // Tworzymy drugi statek
        Captain davyJones = new Captain("Davy Jones", "Devil of the Sea");
        em.persist(davyJones);

        Ship flyingDutchman = new Ship("Flying Dutchman", "Galleon", 46);
        flyingDutchman.setCaptain(davyJones);
        em.persist(flyingDutchman);

        // Pobieramy Black Pearl
        Ship blackPearl = em.createQuery(
            "SELECT s FROM Ship s WHERE s.name = 'Black Pearl'", Ship.class)
            .getSingleResult();

        // Black Pearl odwiedza wyspy
        blackPearl.visitIsland(tortuga);
        blackPearl.visitIsland(islaDeMuerta);
        blackPearl.visitIsland(portRoyal);

        // Flying Dutchman też odwiedza niektóre wyspy
        flyingDutchman.visitIsland(islaDeMuerta);

        em.getTransaction().commit();

        System.out.println("   Black Pearl odwiedziła:");
        blackPearl.getVisitedIslands().forEach(i ->
            System.out.println("   🏝️ " + i.getName()));

        System.out.println("\n   Flying Dutchman odwiedził:");
        flyingDutchman.getVisitedIslands().forEach(i ->
            System.out.println("   🏝️ " + i.getName()));

        System.out.println("\n   Isla de Muerta - statki które ją odwiedziły:");
        islaDeMuerta.getShips().forEach(s ->
            System.out.println("   ⛵ " + s.getName()));
        System.out.println();

        em.close();
    }

    /**
     * Zapytania z relacjami
     */
    private static void queryRelations(EntityManagerFactory emf) {
        System.out.println("4️⃣ Zapytania z relacjami (JPQL)\n");

        EntityManager em = emf.createEntityManager();

        // Znajdź statki z załogą > 3 osób
        System.out.println("   Statki z załogą > 3 osób:");
        em.createQuery("""
            SELECT s FROM Ship s 
            WHERE SIZE(s.crew) > 3
            """, Ship.class)
            .getResultList()
            .forEach(s -> System.out.println("   ⛵ " + s.getName() +
                " (załoga: " + s.getCrew().size() + ")"));

        // Znajdź wyspy ze skarbem odwiedzone przez jakikolwiek statek
        System.out.println("\n   Wyspy ze skarbem, które były odwiedzone:");
        em.createQuery("""
            SELECT DISTINCT i FROM Island i 
            JOIN i.ships s 
            WHERE i.hasTreasure = true
            """, Island.class)
            .getResultList()
            .forEach(i -> System.out.println("   🏝️💰 " + i.getName()));

        // Znajdź kapitanów i ich statki
        System.out.println("\n   Kapitanowie i ich statki:");
        em.createQuery("""
            SELECT c.name, s.name FROM Captain c 
            JOIN c.ship s
            """, Object[].class)
            .getResultList()
            .forEach(row -> System.out.println("   👤 " + row[0] + " → ⛵ " + row[1]));

        em.close();
        System.out.println();
    }
}

