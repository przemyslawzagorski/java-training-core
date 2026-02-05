package pl.przemekzagorski.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.*;
import pl.przemekzagorski.training.jpa.entity.Ship;

import java.util.Set;

/**
 * Demonstracja Bean Validation w JPA.
 *
 * Bean Validation (JSR 380) pozwala:
 * - Walidować dane przed zapisem do bazy
 * - Definiować reguły walidacji adnotacjami
 * - Otrzymywać czytelne komunikaty błędów
 *
 * Hibernate automatycznie waliduje encje przy persist/merge
 * jeśli Hibernate Validator jest na classpath.
 */
public class ValidationDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("DEMO: BEAN VALIDATION W JPA");
        System.out.println("=".repeat(60));

        // 1. Walidacja programowa (bez JPA)
        demonstrateProgrammaticValidation();

        // 2. Walidacja przez JPA/Hibernate
        demonstrateJpaValidation();
    }

    /**
     * Walidacja programowa - przydatna do testów
     */
    private static void demonstrateProgrammaticValidation() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("1. WALIDACJA PROGRAMOWA");
        System.out.println("-".repeat(50));

        // Utworzenie walidatora
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Prawidłowy statek
        Ship validShip = new Ship("Black Pearl", "Galleon", 32);
        validShip.setCrewCapacity(95);

        Set<ConstraintViolation<Ship>> violations = validator.validate(validShip);

        if (violations.isEmpty()) {
            System.out.println("✅ Black Pearl - walidacja OK");
        }

        // Nieprawidłowy statek - pusta nazwa
        Ship invalidName = new Ship("", "Frigate", 20);
        violations = validator.validate(invalidName);

        System.out.println("\n❌ Statek z pustą nazwą:");
        for (ConstraintViolation<Ship> v : violations) {
            System.out.printf("   Pole: %s%n", v.getPropertyPath());
            System.out.printf("   Błąd: %s%n", v.getMessage());
            System.out.printf("   Wartość: '%s'%n", v.getInvalidValue());
        }

        // Nieprawidłowy statek - za dużo dział
        Ship tooManyCannons = new Ship("Super Ship", "Battleship", 500);
        violations = validator.validate(tooManyCannons);

        System.out.println("\n❌ Statek z 500 działami:");
        for (ConstraintViolation<Ship> v : violations) {
            System.out.printf("   Pole: %s, Błąd: %s%n",
                    v.getPropertyPath(), v.getMessage());
        }

        // Nieprawidłowy statek - ujemna pojemność
        Ship negativeCrew = new Ship("Ghost Ship", "Phantom", 0);
        negativeCrew.setCrewCapacity(-10);
        violations = validator.validate(negativeCrew);

        System.out.println("\n❌ Statek z ujemną pojemnością załogi:");
        for (ConstraintViolation<Ship> v : violations) {
            System.out.printf("   Pole: %s, Błąd: %s%n",
                    v.getPropertyPath(), v.getMessage());
        }

        factory.close();
    }

    /**
     * Walidacja automatyczna przez JPA/Hibernate
     */
    private static void demonstrateJpaValidation() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("2. WALIDACJA PRZEZ JPA/HIBERNATE");
        System.out.println("-".repeat(50));

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-intro");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // Próba zapisu nieprawidłowego statku
            Ship invalidShip = new Ship("", "Invalid", -5);

            System.out.println("\n🔄 Próba zapisu nieprawidłowego statku...");

            try {
                em.persist(invalidShip);
                em.flush();  // Wymusza INSERT i walidację
                System.out.println("Zapisano (nie powinno dojść tutaj!)");
            } catch (ConstraintViolationException e) {
                System.out.println("❌ Wyjątek ConstraintViolationException:");

                for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                    System.out.printf("   - %s: %s%n",
                            violation.getPropertyPath(),
                            violation.getMessage());
                }

                // Rollback bo transakcja jest w błędnym stanie
                em.getTransaction().rollback();
                System.out.println("\n↩️  Transakcja wycofana");
            }

            // Prawidłowy zapis
            em.getTransaction().begin();

            Ship validShip = new Ship("Flying Dutchman", "Man-of-War", 50);
            validShip.setCrewCapacity(100);

            em.persist(validShip);
            em.getTransaction().commit();

            System.out.println("\n✅ Flying Dutchman zapisany pomyślnie (id=" + validShip.getId() + ")");

        } finally {
            em.close();
            emf.close();
        }
    }
}
