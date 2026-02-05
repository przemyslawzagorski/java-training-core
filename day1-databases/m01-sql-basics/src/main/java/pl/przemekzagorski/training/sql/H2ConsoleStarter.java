package pl.przemekzagorski.training.sql;

import org.h2.tools.Server;
import java.sql.*;

/**
 * Uruchamia konsolę H2 do ćwiczeń SQL.
 * Po uruchomieniu otwórz przeglądarkę: http://localhost:8082
 */
public class H2ConsoleStarter {

    private static final String JDBC_URL = "jdbc:h2:mem:pirates;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) throws Exception {
        System.out.println("🏴‍☠️ Uruchamianie bazy danych piratów...");

        initializeDatabase();

        Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
        webServer.start();

        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  🌐 Konsola H2 uruchomiona!                                ║");
        System.out.println("║  Otwórz przeglądarkę: http://localhost:8082                ║");
        System.out.println("║  JDBC URL: jdbc:h2:mem:pirates                             ║");
        System.out.println("║  User: sa  |  Password: (puste)                            ║");
        System.out.println("║  Naciśnij ENTER aby zatrzymać...                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        System.in.read();
        webServer.stop();
        System.out.println("👋 Serwer zatrzymany.");
    }

    private static void initializeDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String schema = new String(H2ConsoleStarter.class.getResourceAsStream("/schema.sql").readAllBytes());
            String data = new String(H2ConsoleStarter.class.getResourceAsStream("/data.sql").readAllBytes());

            try (Statement stmt = conn.createStatement()) {
                for (String command : schema.split(";")) {
                    String trimmed = command.replaceAll("--.*", "").trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
                for (String command : data.split(";")) {
                    String trimmed = command.replaceAll("--.*", "").trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
            System.out.println("✅ Baza danych zainicjalizowana");
        }
    }
}

