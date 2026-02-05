package pl.przemekzagorski.training.jdbc;

import pl.przemekzagorski.training.jdbc.dao.JdbcPirateDao;
import pl.przemekzagorski.training.jdbc.dao.PirateDao;
import pl.przemekzagorski.training.jdbc.model.Pirate;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Demonstracja operacji CRUD przez JDBC.
 */
public class CrudDemo {

    private static final String JDBC_URL = "jdbc:h2:mem:crud_demo";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) throws SQLException {
        System.out.println("🏴‍☠️ JDBC CRUD Demo");
        System.out.println("==================\n");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            setupDatabase(conn);
            PirateDao pirateDao = new JdbcPirateDao(conn);

            // CREATE
            System.out.println("1️⃣ CREATE - Dodawanie piratów:");
            Pirate jack = new Pirate("Jack Sparrow", "Captain", new BigDecimal("10000.00"));
            jack.setNickname("Captain Jack");
            jack.setJoinedAt(LocalDate.of(1720, 1, 15));
            pirateDao.save(jack);
            System.out.println("   Zapisano: " + jack);

            Pirate barbossa = new Pirate("Hector Barbossa", "Captain", new BigDecimal("8000.00"));
            pirateDao.save(barbossa);
            System.out.println("   Zapisano: " + barbossa);

            // READ
            System.out.println("\n2️⃣ READ - Pobieranie piratów:");
            List<Pirate> allPirates = pirateDao.findAll();
            System.out.println("   Wszyscy piraci (" + allPirates.size() + "):");
            allPirates.forEach(p -> System.out.println("   • " + p));

            pirateDao.findById(jack.getId())
                .ifPresent(p -> System.out.println("\n   Szukanie po ID: " + p));

            // UPDATE
            System.out.println("\n3️⃣ UPDATE - Aktualizacja:");
            jack.setBounty(new BigDecimal("25000.00"));
            pirateDao.update(jack);
            pirateDao.findById(jack.getId())
                .ifPresent(p -> System.out.println("   Po aktualizacji: " + p));

            // DELETE
            System.out.println("\n4️⃣ DELETE - Usuwanie:");
            System.out.println("   Przed usunięciem: " + pirateDao.count() + " piratów");
            pirateDao.delete(barbossa.getId());
            System.out.println("   Po usunięciu: " + pirateDao.count() + " piratów");
        }
    }

    private static void setupDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE pirates (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    nickname VARCHAR(100),
                    rank VARCHAR(50),
                    bounty DECIMAL(15,2) DEFAULT 0,
                    ship_id BIGINT,
                    joined_at DATE
                )
            """);
        }
    }
}

