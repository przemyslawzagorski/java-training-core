package pl.przemekzagorski.training.springdata.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.przemekzagorski.training.springdata.entity.Pirate;
import pl.przemekzagorski.training.springdata.entity.Ship;
import pl.przemekzagorski.training.springdata.repository.PirateRepository;
import pl.przemekzagorski.training.springdata.repository.ShipRepository;
import pl.przemekzagorski.training.springdata.service.PirateService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Demo pokazujące możliwości Spring Data JPA.
 * Uruchamiane automatycznie przy starcie aplikacji.
 */
@Component
public class SpringDataDemo implements CommandLineRunner {

    private final PirateRepository pirateRepository;
    private final ShipRepository shipRepository;
    private final PirateService pirateService;

    public SpringDataDemo(PirateRepository pirateRepository,
                          ShipRepository shipRepository,
                          PirateService pirateService) {
        this.pirateRepository = pirateRepository;
        this.shipRepository = shipRepository;
        this.pirateService = pirateService;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🏴‍☠️ SPRING DATA JPA DEMO");
        System.out.println("=".repeat(60));

        // 1. Tworzenie danych
        setupData();

        // 2. Query Methods demo
        demoQueryMethods();

        // 3. Custom @Query demo
        demoCustomQueries();

        // 4. Service layer demo
        demoServiceLayer();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Demo zakończone!");
        System.out.println("=".repeat(60) + "\n");
    }

    private void setupData() {
        printSection("1. TWORZENIE DANYCH");

        // Tworzenie statków
        Ship blackPearl = shipRepository.save(new Ship("Black Pearl", "Galleon", 32));
        Ship flyingDutchman = shipRepository.save(new Ship("Flying Dutchman", "Galleon", 48));
        Ship interceptor = shipRepository.save(new Ship("Interceptor", "Brig", 16));

        System.out.println("✅ Utworzono statki: Black Pearl, Flying Dutchman, Interceptor");

        // Tworzenie piratów
        Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));
        jack.setNickname("Captain Jack");
        jack.setShip(blackPearl);
        pirateRepository.save(jack);

        Pirate barbossa = new Pirate("Hector Barbossa", "Captain", new BigDecimal("80000"));
        barbossa.setShip(blackPearl);
        pirateRepository.save(barbossa);

        Pirate davy = new Pirate("Davy Jones", "Captain", new BigDecimal("150000"));
        davy.setShip(flyingDutchman);
        pirateRepository.save(davy);

        Pirate will = new Pirate("Will Turner", "First Mate", new BigDecimal("50000"));
        will.setShip(interceptor);
        pirateRepository.save(will);

        Pirate gibbs = new Pirate("Joshamee Gibbs", "Quartermaster", new BigDecimal("30000"));
        gibbs.setShip(blackPearl);
        pirateRepository.save(gibbs);

        Pirate elizabeth = new Pirate("Elizabeth Swann", "Captain", new BigDecimal("60000"));
        pirateRepository.save(elizabeth);  // Bez statku

        System.out.println("✅ Utworzono 6 piratów");
        System.out.printf("   Łącznie w bazie: %d piratów, %d statków%n",
                pirateRepository.count(), shipRepository.count());
    }

    private void demoQueryMethods() {
        printSection("2. QUERY METHODS");

        // findByRank
        System.out.println("\n📋 findByRank('Captain'):");
        pirateRepository.findByRank("Captain")
                .forEach(p -> System.out.println("   - " + p.getName() + " (bounty: " + p.getBounty() + ")"));

        // findByBountyGreaterThan
        System.out.println("\n📋 findByBountyGreaterThanOrderByBountyDesc(70000):");
        pirateRepository.findByBountyGreaterThanOrderByBountyDesc(new BigDecimal("70000"))
                .forEach(p -> System.out.println("   - " + p.getName() + " (bounty: " + p.getBounty() + ")"));

        // findByNameContaining
        System.out.println("\n📋 findByNameContaining('Jack'):");
        pirateRepository.findByNameContaining("Jack")
                .forEach(p -> System.out.println("   - " + p.getName()));

        // countByRank
        System.out.println("\n📋 countByRank('Captain'): " + pirateRepository.countByRank("Captain"));

        // existsByName
        System.out.println("📋 existsByName('Jack Sparrow'): " + pirateRepository.existsByName("Jack Sparrow"));
        System.out.println("📋 existsByName('Blackbeard'): " + pirateRepository.existsByName("Blackbeard"));

        // findTop3
        System.out.println("\n📋 findTop3ByOrderByBountyDesc():");
        pirateRepository.findTop3ByOrderByBountyDesc()
                .forEach(p -> System.out.println("   - " + p.getName() + " (bounty: " + p.getBounty() + ")"));

        // findByShipIsNull
        System.out.println("\n📋 findByShipIsNull() - piraci bez statku:");
        pirateRepository.findByShipIsNull()
                .forEach(p -> System.out.println("   - " + p.getName()));
    }

    private void demoCustomQueries() {
        printSection("3. CUSTOM @QUERY");

        // findMostWanted
        System.out.println("\n📋 findMostWanted() - najbardziej poszukiwany:");
        pirateRepository.findMostWanted()
                .ifPresent(p -> System.out.println("   🎯 " + p.getName() + " (bounty: " + p.getBounty() + ")"));

        // sumAllBounties
        System.out.println("\n📋 sumAllBounties(): " + pirateRepository.sumAllBounties());

        // avgBountyByRank
        System.out.println("\n📋 avgBountyByRank():");
        pirateRepository.avgBountyByRank()
                .forEach(row -> System.out.printf("   - %s: avg %.2f%n", row[0], (Double) row[1]));

        // findByRankWithShip (JOIN FETCH)
        System.out.println("\n📋 findByRankWithShip('Captain') - z załadowanym statkiem:");
        pirateRepository.findByRankWithShip("Captain")
                .forEach(p -> {
                    String shipName = p.getShip() != null ? p.getShip().getName() : "brak statku";
                    System.out.printf("   - %s na %s%n", p.getName(), shipName);
                });
    }

    private void demoServiceLayer() {
        printSection("4. SERVICE LAYER");

        // Statystyki
        PirateService.FleetStats stats = pirateService.getFleetStats();
        System.out.println("\n📊 Statystyki floty:");
        System.out.printf("   - Piratów: %d%n", stats.totalPirates());
        System.out.printf("   - Statków: %d%n", stats.totalShips());
        System.out.printf("   - Kapitanów: %d%n", stats.captains());
        System.out.printf("   - Suma bounty: %s%n", stats.totalBounty());

        // Most wanted
        System.out.println("\n🎯 Most Wanted (top 3):");
        pirateService.getMostWanted()
                .forEach(p -> System.out.println("   - " + p.getName() + " (" + p.getBounty() + ")"));

        // Podniesienie bounty
        System.out.println("\n💰 Podnoszenie bounty kapitanów o 10%:");
        int updated = pirateService.raiseCaptainBounties(new BigDecimal("10"));

        // Sprawdzenie efektu
        System.out.println("\n📋 Kapitanowie po podwyżce:");
        pirateRepository.findByRank("Captain")
                .forEach(p -> System.out.println("   - " + p.getName() + ": " + p.getBounty()));
    }

    private void printSection(String title) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println(title);
        System.out.println("-".repeat(50));
    }
}
