package pl.przemekzagorski.training.springdata.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.przemekzagorski.training.springdata.entity.Pirate;
import pl.przemekzagorski.training.springdata.entity.Ship;
import pl.przemekzagorski.training.springdata.repository.PirateRepository;
import pl.przemekzagorski.training.springdata.repository.ShipRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serwis piratów - warstwa logiki biznesowej.
 *
 * WAŻNE:
 * - @Transactional na poziomie klasy = każda publiczna metoda to osobna transakcja
 * - Wyjątek RuntimeException = automatyczny rollback
 * - Dirty checking działa - nie trzeba wołać save() dla managed entities
 */
@Service
@Transactional
public class PirateService {

    private final PirateRepository pirateRepository;
    private final ShipRepository shipRepository;

    public PirateService(PirateRepository pirateRepository, ShipRepository shipRepository) {
        this.pirateRepository = pirateRepository;
        this.shipRepository = shipRepository;
    }

    /**
     * Rekrutuje pirata do załogi statku.
     */
    public void recruitPirate(Long pirateId, Long shipId) {
        Pirate pirate = pirateRepository.findById(pirateId)
                .orElseThrow(() -> new IllegalArgumentException("Pirat nie istnieje: " + pirateId));

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new IllegalArgumentException("Statek nie istnieje: " + shipId));

        // Dirty checking - zmiana zostanie zapisana automatycznie
        pirate.setShip(ship);

        System.out.printf("✅ Pirat %s dołączył do załogi %s%n", pirate.getName(), ship.getName());
    }

    /**
     * Transferuje pirata między statkami.
     * Transakcja obejmuje obie operacje - albo obie się udają, albo żadna.
     */
    public void transferPirate(Long pirateId, Long fromShipId, Long toShipId) {
        Pirate pirate = pirateRepository.findWithShipById(pirateId)
                .orElseThrow(() -> new IllegalArgumentException("Pirat nie istnieje: " + pirateId));

        if (pirate.getShip() == null || !pirate.getShip().getId().equals(fromShipId)) {
            throw new IllegalStateException("Pirat nie jest na statku " + fromShipId);
        }

        Ship toShip = shipRepository.findById(toShipId)
                .orElseThrow(() -> new IllegalArgumentException("Statek docelowy nie istnieje: " + toShipId));

        String fromShipName = pirate.getShip().getName();
        pirate.setShip(toShip);

        System.out.printf("✅ Pirat %s przetransferowany z %s na %s%n",
                pirate.getName(), fromShipName, toShip.getName());
    }

    /**
     * Podnosi bounty kapitanów o określony procent.
     * Używa bulk update dla wydajności.
     */
    public int raiseCaptainBounties(BigDecimal percent) {
        int updated = pirateRepository.increaseBountyForRank("Captain", percent);
        System.out.printf("✅ Podniesiono bounty %d kapitanów o %.0f%%%n", updated, percent);
        return updated;
    }

    /**
     * Znajduje piratów "most wanted" (top 3 bounty).
     */
    @Transactional(readOnly = true)  // Optymalizacja dla read-only
    public List<Pirate> getMostWanted() {
        return pirateRepository.findTop3ByOrderByBountyDesc();
    }

    /**
     * Pobiera statystyki floty.
     */
    @Transactional(readOnly = true)
    public FleetStats getFleetStats() {
        long totalPirates = pirateRepository.count();
        long totalShips = shipRepository.count();
        BigDecimal totalBounty = pirateRepository.sumAllBounties();
        long captains = pirateRepository.countByRank("Captain");

        return new FleetStats(totalPirates, totalShips, totalBounty, captains);
    }

    /**
     * DTO ze statystykami floty.
     */
    public record FleetStats(
            long totalPirates,
            long totalShips,
            BigDecimal totalBounty,
            long captains
    ) {}
}
