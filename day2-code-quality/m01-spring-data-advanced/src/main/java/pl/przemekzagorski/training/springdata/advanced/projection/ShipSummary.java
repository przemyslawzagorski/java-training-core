package pl.przemekzagorski.training.springdata.advanced.projection;

/**
 * 🎯 Projekcja - podsumowanie statku (nazwa + typ).
 *
 * ZASTOSOWANIE:
 * - Listy statków (bez szczegółów)
 * - Dropdown lists w UI
 * - Raporty
 *
 * KORZYŚCI:
 * - Nie pobieramy cannons, version, crew
 * - Szybsze zapytanie
 * - Mniejsze zużycie pamięci
 *
 * PRZYKŁAD SQL:
 * SELECT s.name, s.type FROM ships s
 * (zamiast SELECT * FROM ships...)
 */
public interface ShipSummary {
    
    /**
     * Nazwa statku.
     */
    String getName();
    
    /**
     * Typ statku (Galleon, Frigate, Brig, Sloop).
     */
    String getType();
}

