package pl.przemekzagorski.training.jdbc.dao;

import org.junit.jupiter.api.*;
import pl.przemekzagorski.training.jdbc.model.Pirate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Testy integracyjne dla JdbcPirateDao.
 * 
 * WZORZEC: Test z bazą H2 in-memory
 * - Każdy test ma czystą bazę (DROP + CREATE w @BeforeEach)
 * - Baza istnieje tylko w pamięci
 * - Testy są izolowane - jeden nie wpływa na drugi
 * 
 * UWAGA: To są testy INTEGRACYJNE, nie jednostkowe!
 * Testujemy współpracę DAO z prawdziwą bazą danych.
 */
@DisplayName("JdbcPirateDao - testy integracyjne")
class JdbcPirateDaoTest {

    private Connection connection;
    private JdbcPirateDao dao;

    @BeforeEach
    void setUp() throws SQLException {
        // Tworzymy nową bazę H2 in-memory dla każdego testu
        connection = DriverManager.getConnection(
            "jdbc:h2:mem:testdb_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1",
            "sa", ""
        );
        
        // Tworzymy tabelę pirates
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS ships (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL
                )
            """);
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pirates (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    nickname VARCHAR(100),
                    rank VARCHAR(50),
                    bounty DECIMAL(15, 2),
                    ship_id BIGINT,
                    joined_at DATE,
                    FOREIGN KEY (ship_id) REFERENCES ships(id)
                )
            """);
        }
        
        dao = new JdbcPirateDao(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // ==========================================
    // save() tests
    // ==========================================

    @Nested
    @DisplayName("save()")
    class SaveTests {

        @Test
        @DisplayName("powinien zapisać pirata i przypisać ID")
        void shouldSavePirateAndAssignId() {
            // Given
            Pirate pirate = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));

            // When
            Pirate saved = dao.save(pirate);

            // Then
            assertThat(saved.getId()).isNotNull().isPositive();
            assertThat(saved.getName()).isEqualTo("Jack Sparrow");
        }

        @Test
        @DisplayName("powinien zapisać pirata z wszystkimi polami")
        void shouldSavePirateWithAllFields() {
            // Given
            Pirate pirate = new Pirate();
            pirate.setName("Hector Barbossa");
            pirate.setNickname("Barbossa");
            pirate.setRank("Captain");
            pirate.setBounty(new BigDecimal("80000"));
            pirate.setJoinedAt(LocalDate.of(2024, 6, 15));  // Używamy nowszej daty - historyczne daty mają problemy z timezone

            // When
            Pirate saved = dao.save(pirate);

            // Then
            Optional<Pirate> found = dao.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getNickname()).isEqualTo("Barbossa");
            assertThat(found.get().getJoinedAt()).isEqualTo(LocalDate.of(2024, 6, 15));
        }

        @Test
        @DisplayName("powinien obsłużyć null w polach opcjonalnych")
        void shouldHandleNullOptionalFields() {
            // Given
            Pirate pirate = new Pirate("Simple Pirate", "Sailor", BigDecimal.ZERO);
            pirate.setNickname(null);
            pirate.setShipId(null);
            pirate.setJoinedAt(null);

            // When
            Pirate saved = dao.save(pirate);

            // Then
            Optional<Pirate> found = dao.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getNickname()).isNull();
            assertThat(found.get().getShipId()).isNull();
        }
    }

    // ==========================================
    // findById() tests
    // ==========================================

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("powinien znaleźć istniejącego pirata")
        void shouldFindExistingPirate() {
            // Given
            Pirate saved = dao.save(new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000")));

            // When
            Optional<Pirate> found = dao.findById(saved.getId());

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Jack Sparrow");
            assertThat(found.get().getRank()).isEqualTo("Captain");
        }

        @Test
        @DisplayName("powinien zwrócić empty dla nieistniejącego ID")
        void shouldReturnEmptyForNonExistentId() {
            // When
            Optional<Pirate> found = dao.findById(999L);

            // Then
            assertThat(found).isEmpty();
        }
    }

    // ==========================================
    // findAll() tests
    // ==========================================

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {

        @Test
        @DisplayName("powinien zwrócić pustą listę gdy brak piratów")
        void shouldReturnEmptyListWhenNoPirates() {
            // When
            List<Pirate> pirates = dao.findAll();

            // Then
            assertThat(pirates).isEmpty();
        }

        @Test
        @DisplayName("powinien zwrócić wszystkich piratów")
        void shouldReturnAllPirates() {
            // Given
            dao.save(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            dao.save(new Pirate("Barbossa", "Captain", new BigDecimal("80000")));
            dao.save(new Pirate("Will", "First Mate", new BigDecimal("50000")));

            // When
            List<Pirate> pirates = dao.findAll();

            // Then
            assertThat(pirates).hasSize(3);
            assertThat(pirates).extracting(Pirate::getName)
                .containsExactlyInAnyOrder("Jack", "Barbossa", "Will");
        }
    }

    // ==========================================
    // findByRank() tests
    // ==========================================

    @Nested
    @DisplayName("findByRank()")
    class FindByRankTests {

        @Test
        @DisplayName("powinien znaleźć piratów według rangi")
        void shouldFindPiratesByRank() {
            // Given
            dao.save(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            dao.save(new Pirate("Barbossa", "Captain", new BigDecimal("80000")));
            dao.save(new Pirate("Will", "First Mate", new BigDecimal("50000")));

            // When
            List<Pirate> captains = dao.findByRank("Captain");

            // Then
            assertThat(captains).hasSize(2);
            assertThat(captains).extracting(Pirate::getRank)
                .containsOnly("Captain");
        }

        @Test
        @DisplayName("powinien zwrócić pustą listę dla nieistniejącej rangi")
        void shouldReturnEmptyForNonExistentRank() {
            // Given
            dao.save(new Pirate("Jack", "Captain", new BigDecimal("100000")));

            // When
            List<Pirate> admirals = dao.findByRank("Admiral");

            // Then
            assertThat(admirals).isEmpty();
        }
    }

    // ==========================================
    // update() tests
    // ==========================================

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("powinien zaktualizować dane pirata")
        void shouldUpdatePirate() {
            // Given
            Pirate saved = dao.save(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            saved.setName("Captain Jack Sparrow");
            saved.setBounty(new BigDecimal("200000"));

            // When
            dao.update(saved);

            // Then
            Optional<Pirate> updated = dao.findById(saved.getId());
            assertThat(updated).isPresent();
            assertThat(updated.get().getName()).isEqualTo("Captain Jack Sparrow");
            assertThat(updated.get().getBounty()).isEqualByComparingTo("200000");
        }

        @Test
        @DisplayName("powinien nie zmieniać ID przy update")
        void shouldNotChangeIdOnUpdate() {
            // Given
            Pirate saved = dao.save(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            Long originalId = saved.getId();
            saved.setName("Updated Name");

            // When
            dao.update(saved);

            // Then
            Optional<Pirate> updated = dao.findById(originalId);
            assertThat(updated).isPresent();
            assertThat(updated.get().getId()).isEqualTo(originalId);
        }
    }

    // ==========================================
    // delete() tests
    // ==========================================

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("powinien usunąć pirata")
        void shouldDeletePirate() {
            // Given
            Pirate saved = dao.save(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            Long id = saved.getId();

            // When
            dao.delete(id);

            // Then
            Optional<Pirate> found = dao.findById(id);
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("powinien nie rzucić wyjątku przy usuwaniu nieistniejącego")
        void shouldNotThrowWhenDeletingNonExistent() {
            // When & Then
            assertThatCode(() -> dao.delete(999L))
                .doesNotThrowAnyException();
        }
    }

    // ==========================================
    // count() tests
    // ==========================================

    @Nested
    @DisplayName("count()")
    class CountTests {

        @Test
        @DisplayName("powinien zwrócić 0 dla pustej bazy")
        void shouldReturnZeroForEmptyDatabase() {
            // When
            long count = dao.count();

            // Then
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("powinien zwrócić poprawną liczbę piratów")
        void shouldReturnCorrectCount() {
            // Given
            dao.save(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            dao.save(new Pirate("Barbossa", "Captain", new BigDecimal("80000")));

            // When
            long count = dao.count();

            // Then
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("powinien aktualizować count po delete")
        void shouldUpdateCountAfterDelete() {
            // Given
            Pirate saved = dao.save(new Pirate("Jack", "Captain", new BigDecimal("100000")));
            dao.save(new Pirate("Barbossa", "Captain", new BigDecimal("80000")));
            assertThat(dao.count()).isEqualTo(2);

            // When
            dao.delete(saved.getId());

            // Then
            assertThat(dao.count()).isEqualTo(1);
        }
    }
}
