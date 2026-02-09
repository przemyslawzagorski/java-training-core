package pl.przemekzagorski.training.patterns.cqrs;

/**
 * Komenda: Zaktualizuj nagrodę za głowę pirata.
 * 
 * 🏴‍☠️ Command = ZMIENIA stan (modyfikuje bounty).
 */
public record UpdateBountyCommand(
    Long pirateId,
    int newBounty
) implements Command {
}

