package pl.przemekzagorski.training.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

/**
 * 🏴‍☠️ Connection Pool Demo - HikariCP
 *
 * ═══════════════════════════════════════════════════════════════════
 * DLACZEGO CONNECTION POOLING?
 * ═══════════════════════════════════════════════════════════════════
 *
 * Problem z DriverManager.getConnection():
 * - Każde wywołanie = nowe połączenie TCP z bazą danych
 * - Połączenie TCP to ~50-100ms opóźnienia
 * - Baza danych ma limit połączeń (np. 100)
 * - W aplikacji webowej = 1000 requestów = 1000 połączeń = KATASTROFA!
 *
 * Rozwiązanie - Connection Pool:
 * - Pula gotowych, otwartych połączeń
 * - getConnection() = pobranie z puli (~0.1ms)
 * - close() = zwrot do puli (nie zamyka fizycznie!)
 * - Automatyczne zarządzanie limitem połączeń
 *
 * ═══════════════════════════════════════════════════════════════════
 * HIKARICP - STANDARD PRODUKCYJNY
 * ═══════════════════════════════════════════════════════════════════
 *
 * - Najszybszy connection pool w Java
 * - Domyślny w Spring Boot
 * - Zero konfiguracji dla podstawowego użycia
 * - Automatyczne health checking połączeń
 *
 * ═══════════════════════════════════════════════════════════════════
 */
public class ConnectionPoolDemo {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Connection Pool Demo - HikariCP");
        System.out.println("════════════════════════════════════════\n");

        // Demo 1: Problem z DriverManager
        System.out.println("1️⃣ PROBLEM: DriverManager.getConnection()");
        System.out.println("─".repeat(50));
        demoDriverManagerProblem();

        System.out.println("\n");

        // Demo 2: Rozwiązanie z HikariCP
        System.out.println("2️⃣ ROZWIĄZANIE: HikariCP Connection Pool");
        System.out.println("─".repeat(50));
        demoHikariCPSolution();

        System.out.println("\n");

        // Demo 3: Porównanie wydajności
        System.out.println("3️⃣ PORÓWNANIE WYDAJNOŚCI");
        System.out.println("─".repeat(50));
        comparePerformance();
    }

    /**
     * Demonstracja problemu z DriverManager - każde połączenie to koszt!
     */
    private static void demoDriverManagerProblem() {
        String url = "jdbc:h2:mem:pooltest1;DB_CLOSE_DELAY=-1";

        System.out.println("   Tworzenie 10 połączeń przez DriverManager...");
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            try (Connection conn = DriverManager.getConnection(url, "sa", "")) {
                // Symulacja prostej operacji
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SELECT 1");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("   ⏱️ Czas: " + duration + "ms");
        System.out.println("   ⚠️ Każde wywołanie = nowe połączenie TCP!");
        System.out.println("   ⚠️ W produkcji przy 1000 req/s = KATASTROFA!");
    }

    /**
     * Demonstracja rozwiązania z HikariCP - pula połączeń.
     */
    private static void demoHikariCPSolution() {
        // Konfiguracja HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:pooltest2;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");

        // Konfiguracja puli
        config.setMaximumPoolSize(10);          // Max 10 połączeń w puli
        config.setMinimumIdle(2);               // Min 2 połączenia gotowe
        config.setConnectionTimeout(30000);     // Timeout 30s na pobranie z puli
        config.setIdleTimeout(600000);          // 10 min idle przed zamknięciem
        config.setPoolName("PirateHikariPool"); // Nazwa dla logów

        System.out.println("   📦 Konfiguracja HikariCP:");
        System.out.println("      - Max pool size: " + config.getMaximumPoolSize());
        System.out.println("      - Min idle: " + config.getMinimumIdle());
        System.out.println("      - Pool name: " + config.getPoolName());

        // Tworzymy pulę (zazwyczaj raz w aplikacji!)
        try (HikariDataSource dataSource = new HikariDataSource(config)) {

            System.out.println("\n   Tworzenie 10 połączeń przez HikariCP...");
            long start = System.currentTimeMillis();

            for (int i = 0; i < 10; i++) {
                // getConnection() zwraca połączenie z puli (bardzo szybkie!)
                try (Connection conn = dataSource.getConnection()) {
                    // close() zwraca do puli, NIE zamyka fizycznie!
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("SELECT 1");
                    }
                }
            }

            long duration = System.currentTimeMillis() - start;
            System.out.println("   ⏱️ Czas: " + duration + "ms");
            System.out.println("   ✅ Połączenia pobierane z puli - błyskawicznie!");
            System.out.println("   ✅ close() zwraca do puli, nie zamyka połączenia!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Porównanie wydajności: DriverManager vs HikariCP
     */
    private static void comparePerformance() {
        int iterations = 100;

        // DriverManager
        String url1 = "jdbc:h2:mem:perf1;DB_CLOSE_DELAY=-1";
        long driverManagerTime = measureDriverManager(url1, iterations);

        // HikariCP
        long hikariTime = measureHikariCP(iterations);

        System.out.println("\n   📊 WYNIKI (" + iterations + " iteracji):");
        System.out.println("   ─".repeat(40));
        System.out.printf("   DriverManager:  %5d ms%n", driverManagerTime);
        System.out.printf("   HikariCP:       %5d ms%n", hikariTime);
        System.out.println("   ─".repeat(40));

        if (driverManagerTime > 0 && hikariTime > 0) {
            double speedup = (double) driverManagerTime / hikariTime;
            System.out.printf("   🚀 HikariCP jest %.1fx szybszy!%n", speedup);
        }

        System.out.println("\n   💡 WNIOSEK:");
        System.out.println("   W produkcji ZAWSZE używaj Connection Pool!");
        System.out.println("   HikariCP to standard w Spring Boot.");
    }

    private static long measureDriverManager(String url, int iterations) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            try (Connection conn = DriverManager.getConnection(url, "sa", "");
                 Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT 1");
            } catch (SQLException e) {
                // ignore
            }
        }
        return System.currentTimeMillis() - start;
    }

    private static long measureHikariCP(int iterations) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:perf2;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(10);

        try (HikariDataSource ds = new HikariDataSource(config)) {
            // Warm-up
            try (Connection conn = ds.getConnection()) {
                conn.createStatement().execute("SELECT 1");
            }

            long start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                try (Connection conn = ds.getConnection();
                     Statement stmt = conn.createStatement()) {
                    stmt.execute("SELECT 1");
                }
            }
            return System.currentTimeMillis() - start;

        } catch (SQLException e) {
            return -1;
        }
    }
}
