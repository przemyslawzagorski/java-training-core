package pl.przemekzagorski.training.jpa.dto;

import java.math.BigDecimal;

/**
 * DTO dla projekcji pirata - tylko potrzebne pola.
 *
 * Używane z SELECT NEW w JPQL:
 * SELECT NEW pl.przemekzagorski.training.jpa.dto.PirateDTO(p.name, p.rank, p.bounty)
 * FROM Pirate p
 *
 * Zalety DTO:
 * - Mniej danych przesyłanych z bazy
 * - Type-safe (nie Object[])
 * - Immutable (record)
 * - Łatwe do serializacji (JSON)
 */
public record PirateDTO(
    String name,
    String rank,
    BigDecimal bounty
) {
    /**
     * Konstruktor wymagany przez JPQL SELECT NEW.
     * Musi być publiczny i pasować do argumentów w zapytaniu.
     */
    public PirateDTO {
        // record automatycznie generuje konstruktor
    }

    @Override
    public String toString() {
        return String.format("PirateDTO{name='%s', rank='%s', bounty=%s}", name, rank, bounty);
    }
}
