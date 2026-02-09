package pl.przemekzagorski.training.springdata.advanced.projection;

import java.math.BigDecimal;

/**
 * 🎯 Projekcja - podsumowanie pirata (imię + bounty).
 *
 * ZASTOSOWANIE:
 * - Listy rankingowe (top piraci)
 * - Raporty finansowe
 * - Wszędzie gdzie nie potrzebujemy pełnej encji
 *
 * KORZYŚCI:
 * - Mniej danych z bazy (nie pobieramy nickname, rank, ship, version)
 * - Szybsze zapytania
 * - Mniejsze zużycie pamięci
 * - Można łączyć z paginacją: Page<PirateSummary>
 *
 * PRZYKŁAD SQL:
 * SELECT p.name, p.bounty FROM pirates p WHERE p.bounty > :amount
 * (zamiast SELECT * FROM pirates...)
 */
public interface PirateSummary {
    
    /**
     * Nazwa pirata.
     */
    String getName();
    
    /**
     * Nagroda za głowę pirata.
     */
    BigDecimal getBounty();
}

