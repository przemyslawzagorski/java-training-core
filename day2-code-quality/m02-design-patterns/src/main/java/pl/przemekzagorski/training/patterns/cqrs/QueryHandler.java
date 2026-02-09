package pl.przemekzagorski.training.patterns.cqrs;

/**
 * Handler dla zapytań (Queries).
 * 
 * QueryHandler WYKONUJE zapytania - ODCZYTUJE dane (NIE ZMIENIA stanu!).
 * 
 * @param <Q> Typ zapytania
 * @param <R> Typ wyniku
 */
public interface QueryHandler<Q extends Query<R>, R> {
    /**
     * Wykonuje zapytanie i zwraca wynik.
     * 
     * @param query Zapytanie do wykonania
     * @return Wynik zapytania
     */
    R handle(Q query);
}

