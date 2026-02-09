package pl.przemekzagorski.training.patterns.cqrs;

/**
 * Marker interface dla wszystkich zapytań (Queries).
 * 
 * Query = operacja ODCZYTUJĄCA dane (READ) - NIE ZMIENIA stanu!
 * 
 * 🏴‍☠️ Przykłady:
 * - GetPirateByIdQuery
 * - FindPiratesByRankQuery
 * - CountPiratesQuery
 * 
 * @param <R> Typ wyniku zapytania
 */
public interface Query<R> {
    // Marker interface - nie ma metod
}

