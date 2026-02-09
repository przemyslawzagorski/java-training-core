package pl.przemekzagorski.training.springdata.advanced.projection;

/**
 * 🎯 Projekcja - tylko imię i ranga pirata.
 *
 * KONCEPCJA:
 * - Spring Data Projections pozwalają pobrać tylko wybrane pola z encji
 * - Zamiast SELECT * FROM pirates, wykonuje SELECT name, rank FROM pirates
 * - Mniej danych = szybsze zapytanie, mniej pamięci
 *
 * JAK TO DZIAŁA:
 * - Spring Data widzi interfejs z getterami
 * - Automatycznie generuje implementację proxy
 * - Mapuje wyniki zapytania na interfejs
 *
 * UŻYCIE:
 * List<PirateNameOnly> pirates = repository.findProjectedByRank("Captain");
 * pirates.forEach(p -> System.out.println(p.getName() + " - " + p.getRank()));
 */
public interface PirateNameOnly {
    
    /**
     * Nazwa pirata.
     * Spring Data automatycznie mapuje pole 'name' z encji Pirate.
     */
    String getName();
    
    /**
     * Ranga pirata.
     * Spring Data automatycznie mapuje pole 'rank' z encji Pirate.
     */
    String getRank();
}

