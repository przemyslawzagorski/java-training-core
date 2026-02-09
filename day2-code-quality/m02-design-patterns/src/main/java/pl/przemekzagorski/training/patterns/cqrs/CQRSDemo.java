package pl.przemekzagorski.training.patterns.cqrs;

import java.util.List;
import java.util.Optional;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║                    CQRS PATTERN DEMO                              ║
 * ╠═══════════════════════════════════════════════════════════════════╣
 * ║  CQRS = Command Query Responsibility Segregation                 ║
 * ║  Separacja Commands (zmiany) od Queries (odczyt)                 ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 *
 * 🏴‍☠️ Przykład: System zarządzania piratami
 *
 * KLUCZOWA ZASADA:
 * - Command = ZMIENIA stan (CREATE, UPDATE, DELETE) - void
 * - Query = ODCZYTUJE dane (READ) - zwraca wynik
 */
public class CQRSDemo {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║              🏴‍☠️ CQRS PATTERN DEMO 🏴‍☠️                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝\n");

        // ════════════════════════════════════════════════════════════════
        // SETUP: Inicjalizacja systemu CQRS
        // ════════════════════════════════════════════════════════════════

        PirateDatabase database = new PirateDatabase();
        CommandBus commandBus = new CommandBus();
        QueryBus queryBus = new QueryBus();

        // Rejestracja handlerów dla Commands
        commandBus.registerHandler(CreatePirateCommand.class, new CreatePirateCommandHandler(database));
        commandBus.registerHandler(UpdateBountyCommand.class, new UpdateBountyCommandHandler(database));

        // Rejestracja handlerów dla Queries
        queryBus.registerHandler(GetPirateByIdQuery.class, new GetPirateByIdQueryHandler(database));
        queryBus.registerHandler(FindPiratesByRankQuery.class, new FindPiratesByRankQueryHandler(database));

        System.out.println("✅ CQRS System initialized!\n");

        // ════════════════════════════════════════════════════════════════
        // DEMO 1: Commands - ZMIANY STANU
        // ════════════════════════════════════════════════════════════════

        System.out.println("═".repeat(70));
        System.out.println("DEMO 1: COMMANDS (Write Operations)");
        System.out.println("═".repeat(70) + "\n");

        // Command 1: Stwórz pirata
        System.out.println("📝 Executing: CreatePirateCommand");
        commandBus.execute(new CreatePirateCommand("Jack Sparrow", "Captain", 10000));

        // Command 2: Stwórz więcej piratów
        commandBus.execute(new CreatePirateCommand("Will Turner", "First Mate", 5000));
        commandBus.execute(new CreatePirateCommand("Hector Barbossa", "Captain", 15000));
        commandBus.execute(new CreatePirateCommand("Joshamee Gibbs", "Quartermaster", 3000));

        System.out.println();

        // ════════════════════════════════════════════════════════════════
        // DEMO 2: Queries - ODCZYT DANYCH
        // ════════════════════════════════════════════════════════════════

        System.out.println("═".repeat(70));
        System.out.println("DEMO 2: QUERIES (Read Operations)");
        System.out.println("═".repeat(70) + "\n");

        // Query 1: Znajdź pirata po ID
        System.out.println("🔍 Executing: GetPirateByIdQuery(1)");
        Optional<Pirate> pirate = queryBus.execute(new GetPirateByIdQuery(1L));
        pirate.ifPresent(p -> System.out.println("   Found: " + p));

        System.out.println();

        // Query 2: Znajdź wszystkich kapitanów
        System.out.println("🔍 Executing: FindPiratesByRankQuery('Captain')");
        List<Pirate> captains = queryBus.execute(new FindPiratesByRankQuery("Captain"));
        System.out.println("   Found " + captains.size() + " captains:");
        captains.forEach(c -> System.out.println("   - " + c));

        System.out.println();

        // ════════════════════════════════════════════════════════════════
        // DEMO 3: Command - UPDATE
        // ════════════════════════════════════════════════════════════════

        System.out.println("═".repeat(70));
        System.out.println("DEMO 3: UPDATE COMMAND");
        System.out.println("═".repeat(70) + "\n");

        // Command: Zaktualizuj bounty
        System.out.println("📝 Executing: UpdateBountyCommand(1, 50000)");
        commandBus.execute(new UpdateBountyCommand(1L, 50000));

        System.out.println();

        // Query: Sprawdź zmianę
        System.out.println("🔍 Verifying: GetPirateByIdQuery(1)");
        pirate = queryBus.execute(new GetPirateByIdQuery(1L));
        pirate.ifPresent(p -> System.out.println("   Updated: " + p));

        System.out.println();

        // ════════════════════════════════════════════════════════════════
        // PODSUMOWANIE
        // ════════════════════════════════════════════════════════════════

        System.out.println("═".repeat(70));
        System.out.println("✅ CQRS DEMO COMPLETED!");
        System.out.println("═".repeat(70));
        System.out.println("\n💡 KEY TAKEAWAYS:");
        System.out.println("   1. Commands CHANGE state (void) - CreatePirateCommand, UpdateBountyCommand");
        System.out.println("   2. Queries READ data (return result) - GetPirateByIdQuery, FindPiratesByRankQuery");
        System.out.println("   3. CommandBus and QueryBus separate concerns");
        System.out.println("   4. Easy to test, scale, and maintain!");
        System.out.println();
    }
}

