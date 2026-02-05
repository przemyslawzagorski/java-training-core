package pl.przemekzagorski.training.patterns.builder;

/**
 * Statek piracki z wieloma opcjonalnymi parametrami.
 * Idealny kandydat do wzorca Builder!
 */
public class PirateShip {

    // Wymagane parametry
    private final String name;

    // Opcjonalne parametry
    private final String type;
    private final int cannons;
    private final int crewCapacity;
    private final boolean hasSails;
    private final boolean hasJollyRoger;  // Flaga piracka
    private final String captainName;
    private final int cargoCapacity;
    private final String homePort;

    /**
     * Prywatny konstruktor - tylko Builder może tworzyć obiekty!
     */
    private PirateShip(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.cannons = builder.cannons;
        this.crewCapacity = builder.crewCapacity;
        this.hasSails = builder.hasSails;
        this.hasJollyRoger = builder.hasJollyRoger;
        this.captainName = builder.captainName;
        this.cargoCapacity = builder.cargoCapacity;
        this.homePort = builder.homePort;
    }

    // Tylko gettery - obiekt jest IMMUTABLE (niezmienny)
    public String getName() { return name; }
    public String getType() { return type; }
    public int getCannons() { return cannons; }
    public int getCrewCapacity() { return crewCapacity; }
    public boolean hasSails() { return hasSails; }
    public boolean hasJollyRoger() { return hasJollyRoger; }
    public String getCaptainName() { return captainName; }
    public int getCargoCapacity() { return cargoCapacity; }
    public String getHomePort() { return homePort; }

    @Override
    public String toString() {
        return String.format("""
            🚢 PirateShip:
               Name: %s
               Type: %s
               Cannons: %d
               Crew capacity: %d
               Has sails: %s
               Has Jolly Roger: %s
               Captain: %s
               Cargo: %d tons
               Home port: %s
            """, name, type, cannons, crewCapacity, hasSails,
                hasJollyRoger, captainName, cargoCapacity, homePort);
    }

    /**
     * BUILDER - buduje obiekt krok po kroku.
     */
    public static class Builder {

        // Wymagane
        private final String name;

        // Opcjonalne z domyślnymi wartościami
        private String type = "Unknown";
        private int cannons = 0;
        private int crewCapacity = 10;
        private boolean hasSails = true;
        private boolean hasJollyRoger = false;
        private String captainName = "Unknown";
        private int cargoCapacity = 0;
        private String homePort = "Unknown";

        /**
         * Konstruktor z wymaganym parametrem.
         */
        public Builder(String name) {
            this.name = name;
        }

        public Builder type(String type) {
            this.type = type;
            return this;  // Zwracamy this dla fluent API
        }

        public Builder cannons(int cannons) {
            this.cannons = cannons;
            return this;
        }

        public Builder crewCapacity(int crewCapacity) {
            this.crewCapacity = crewCapacity;
            return this;
        }

        public Builder hasSails(boolean hasSails) {
            this.hasSails = hasSails;
            return this;
        }

        public Builder withJollyRoger() {
            this.hasJollyRoger = true;
            return this;
        }

        public Builder captain(String captainName) {
            this.captainName = captainName;
            return this;
        }

        public Builder cargoCapacity(int cargoCapacity) {
            this.cargoCapacity = cargoCapacity;
            return this;
        }

        public Builder homePort(String homePort) {
            this.homePort = homePort;
            return this;
        }

        /**
         * Buduje finalny obiekt.
         */
        public PirateShip build() {
            // Tutaj można dodać walidację!
            if (name == null || name.isBlank()) {
                throw new IllegalStateException("Statek musi mieć nazwę!");
            }
            return new PirateShip(this);
        }
    }
}

