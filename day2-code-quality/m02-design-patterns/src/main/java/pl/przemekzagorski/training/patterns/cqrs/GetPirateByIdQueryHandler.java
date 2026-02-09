package pl.przemekzagorski.training.patterns.cqrs;

import java.util.Optional;

/**
 * Handler dla GetPirateByIdQuery.
 * 
 * 🏴‍☠️ Znajduje pirata po ID (ODCZYT - nie zmienia stanu!).
 */
public class GetPirateByIdQueryHandler implements QueryHandler<GetPirateByIdQuery, Optional<Pirate>> {

    private final PirateDatabase database;

    public GetPirateByIdQueryHandler(PirateDatabase database) {
        this.database = database;
    }

    @Override
    public Optional<Pirate> handle(GetPirateByIdQuery query) {
        return database.findById(query.pirateId());
    }
}

