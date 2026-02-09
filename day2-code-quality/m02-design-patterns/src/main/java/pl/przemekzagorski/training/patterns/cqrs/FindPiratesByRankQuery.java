package pl.przemekzagorski.training.patterns.cqrs;

import java.util.List;

/**
 * Zapytanie: Znajdź wszystkich piratów o danej randze.
 * 
 * 🏴‍☠️ Query = ODCZYTUJE dane (NIE ZMIENIA stanu!).
 */
public record FindPiratesByRankQuery(
    String rank
) implements Query<List<Pirate>> {
}

