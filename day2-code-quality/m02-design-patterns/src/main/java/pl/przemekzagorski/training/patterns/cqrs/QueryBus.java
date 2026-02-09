package pl.przemekzagorski.training.patterns.cqrs;

import java.util.HashMap;
import java.util.Map;

/**
 * QueryBus - dispatcher dla zapytań.
 * 
 * 🏴‍☠️ Przekazuje zapytania do odpowiednich handlerów.
 * 
 * CQRS: Separacja Commands (zmiany stanu) od Queries (odczyt).
 */
public class QueryBus {

    private final Map<Class<? extends Query<?>>, QueryHandler<? extends Query<?>, ?>> handlers = new HashMap<>();

    /**
     * Rejestruje handler dla danego typu zapytania.
     */
    public <Q extends Query<R>, R> void registerHandler(Class<Q> queryClass, QueryHandler<Q, R> handler) {
        handlers.put(queryClass, handler);
    }

    /**
     * Wykonuje zapytanie - znajduje odpowiedni handler i deleguje do niego.
     */
    @SuppressWarnings("unchecked")
    public <Q extends Query<R>, R> R execute(Q query) {
        QueryHandler<Q, R> handler = (QueryHandler<Q, R>) handlers.get(query.getClass());
        
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for query: " + query.getClass().getName());
        }

        return handler.handle(query);
    }
}

