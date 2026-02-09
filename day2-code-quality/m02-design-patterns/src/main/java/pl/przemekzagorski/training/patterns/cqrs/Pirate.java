package pl.przemekzagorski.training.patterns.cqrs;

/**
 * Model pirata (prosta klasa danych).
 * 
 * 🏴‍☠️ W prawdziwej aplikacji to byłaby encja JPA lub DTO.
 */
public class Pirate {
    private Long id;
    private String name;
    private String rank;
    private int bounty;

    public Pirate(Long id, String name, String rank, int bounty) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.bounty = bounty;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getRank() { return rank; }
    public int getBounty() { return bounty; }

    // Setter dla bounty (używany w UpdateBountyCommand)
    public void setBounty(int bounty) { this.bounty = bounty; }

    @Override
    public String toString() {
        return String.format("Pirate{id=%d, name='%s', rank='%s', bounty=%d}",
            id, name, rank, bounty);
    }
}

