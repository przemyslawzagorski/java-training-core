package pl.przemekzagorski.training.patterns.cqrs;

import java.util.List;

/**
 * Handler dla FindPiratesByRankQuery.
 * 
 * 🏴‍☠️ Znajduje piratów po randze (ODCZYT - nie zmienia stanu!).
 */
public class FindPiratesByRankQueryHandler implements QueryHandler<FindPiratesByRankQuery, List<Pirate>> {

    private final PirateDatabase database;

    public FindPiratesByRankQueryHandler(PirateDatabase database) {
        this.database = database;
    }

    @Override
    public List<Pirate> handle(FindPiratesByRankQuery query) {
        return database.findByRank(query.rank());
    }
}

