package pl.przemekzagorski.training.patterns.cqrs;

/**
 * Komenda: Stwórz nowego pirata.
 * 
 * 🏴‍☠️ Command = ZMIENIA stan (dodaje pirata do systemu).
 */
public record CreatePirateCommand(
    String name,
    String rank,
    int bounty
) implements Command {
    // Record automatycznie generuje: constructor, getters, equals, hashCode, toString
}

