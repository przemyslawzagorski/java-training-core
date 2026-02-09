package pl.przemekzagorski.training.patterns.cqrs;

/**
 * Handler dla komend (Commands).
 * 
 * CommandHandler WYKONUJE komendy - ZMIENIA stan systemu.
 * 
 * @param <C> Typ komendy
 */
public interface CommandHandler<C extends Command> {
    /**
     * Wykonuje komendę.
     * 
     * @param command Komenda do wykonania
     */
    void handle(C command);
}

