package pl.przemekzagorski.training.jpa.dto;

/**
 * DTO dla podsumowania statku - agregacja.
 *
 * Używane do raportów:
 * SELECT NEW ShipSummaryDTO(s.name, s.type, COUNT(p), SUM(p.bounty))
 * FROM Ship s LEFT JOIN s.crew p
 * GROUP BY s.id, s.name, s.type
 */
public record ShipSummaryDTO(
    String shipName,
    String shipType,
    Long crewCount,
    java.math.BigDecimal totalBounty
) {
    @Override
    public String toString() {
        return String.format("ShipSummary{ship='%s', type='%s', crew=%d, totalBounty=%s}",
            shipName, shipType, crewCount, totalBounty);
    }
}
