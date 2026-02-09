package pl.przemekzagorski.training.patterns.cqrs;

/**
 * Marker interface dla wszystkich komend (Commands).
 * 
 * Command = operacja ZMIENIAJĄCA stan (CREATE, UPDATE, DELETE).
 * 
 * 🏴‍☠️ Przykłady:
 * - CreatePirateCommand
 * - UpdateBountyCommand
 * - DeletePirateCommand
 */
public interface Command {
    // Marker interface - nie ma metod
}

