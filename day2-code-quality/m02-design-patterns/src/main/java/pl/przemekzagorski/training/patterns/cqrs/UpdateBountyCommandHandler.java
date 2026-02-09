package pl.przemekzagorski.training.patterns.cqrs;

/**
 * Handler dla UpdateBountyCommand.
 * 
 * 🏴‍☠️ Aktualizuje nagrodę za głowę pirata.
 */
public class UpdateBountyCommandHandler implements CommandHandler<UpdateBountyCommand> {

    private final PirateDatabase database;

    public UpdateBountyCommandHandler(PirateDatabase database) {
        this.database = database;
    }

    @Override
    public void handle(UpdateBountyCommand command) {
        Pirate pirate = database.findById(command.pirateId())
            .orElseThrow(() -> new IllegalArgumentException("Pirate not found: " + command.pirateId()));

        int oldBounty = pirate.getBounty();
        pirate.setBounty(command.newBounty());

        System.out.println("✅ Updated bounty for " + pirate.getName() + 
            ": " + oldBounty + " → " + command.newBounty());
    }
}

