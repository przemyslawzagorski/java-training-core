package pl.przemekzagorski.training.patterns.cqrs;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Handler dla CreatePirateCommand.
 * 
 * 🏴‍☠️ Tworzy nowego pirata i dodaje go do "bazy danych" (w tym przypadku PirateDatabase).
 */
public class CreatePirateCommandHandler implements CommandHandler<CreatePirateCommand> {

    private final PirateDatabase database;
    private final AtomicLong idGenerator;

    public CreatePirateCommandHandler(PirateDatabase database) {
        this.database = database;
        this.idGenerator = new AtomicLong(1);
    }

    @Override
    public void handle(CreatePirateCommand command) {
        // Walidacja
        if (command.name() == null || command.name().isBlank()) {
            throw new IllegalArgumentException("Pirate name cannot be empty");
        }

        // Tworzenie nowego pirata
        Long newId = idGenerator.getAndIncrement();
        Pirate pirate = new Pirate(newId, command.name(), command.rank(), command.bounty());

        // Zapis do "bazy danych"
        database.save(pirate);

        System.out.println("✅ Created pirate: " + pirate);
    }
}

