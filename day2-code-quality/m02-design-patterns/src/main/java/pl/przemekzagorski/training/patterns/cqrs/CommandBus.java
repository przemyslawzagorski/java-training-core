package pl.przemekzagorski.training.patterns.cqrs;

import java.util.HashMap;
import java.util.Map;

/**
 * CommandBus - dispatcher dla komend.
 * 
 * 🏴‍☠️ Przekazuje komendy do odpowiednich handlerów.
 * 
 * CQRS: Separacja Commands (zmiany stanu) od Queries (odczyt).
 */
public class CommandBus {

    private final Map<Class<? extends Command>, CommandHandler<? extends Command>> handlers = new HashMap<>();

    /**
     * Rejestruje handler dla danego typu komendy.
     */
    public <C extends Command> void registerHandler(Class<C> commandClass, CommandHandler<C> handler) {
        handlers.put(commandClass, handler);
    }

    /**
     * Wykonuje komendę - znajduje odpowiedni handler i deleguje do niego.
     */
    @SuppressWarnings("unchecked")
    public <C extends Command> void execute(C command) {
        CommandHandler<C> handler = (CommandHandler<C>) handlers.get(command.getClass());
        
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for command: " + command.getClass().getName());
        }

        handler.handle(command);
    }
}

