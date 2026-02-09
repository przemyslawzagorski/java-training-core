package pl.przemekzagorski.training.patterns.cqrs;

import java.util.Optional;

/**
 * Zapytanie: Znajdź pirata po ID.
 * 
 * 🏴‍☠️ Query = ODCZYTUJE dane (NIE ZMIENIA stanu!).
 */
public record GetPirateByIdQuery(
    Long pirateId
) implements Query<Optional<Pirate>> {
}

