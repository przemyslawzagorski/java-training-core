package pl.przemekzagorski.training.jdbc.dao;

import pl.przemekzagorski.training.jdbc.model.Pirate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacja PirateDao używająca JDBC.
 */
public class JdbcPirateDao implements PirateDao {

    private final Connection connection;

    public JdbcPirateDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Pirate save(Pirate pirate) {
        String sql = "INSERT INTO pirates (name, nickname, rank, bounty, ship_id, joined_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, pirate.getName());
            stmt.setString(2, pirate.getNickname());
            stmt.setString(3, pirate.getRank());
            stmt.setBigDecimal(4, pirate.getBounty());
            if (pirate.getShipId() != null) {
                stmt.setLong(5, pirate.getShipId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            if (pirate.getJoinedAt() != null) {
                stmt.setDate(6, Date.valueOf(pirate.getJoinedAt()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    pirate.setId(keys.getLong(1));
                }
            }
            return pirate;
        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy zapisie pirata", e);
        }
    }

    @Override
    public Optional<Pirate> findById(Long id) {
        String sql = "SELECT * FROM pirates WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPirate(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy szukaniu pirata", e);
        }
    }

    @Override
    public List<Pirate> findAll() {
        String sql = "SELECT * FROM pirates ORDER BY id";
        List<Pirate> pirates = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pirates.add(mapRowToPirate(rs));
            }
            return pirates;
        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy pobieraniu piratów", e);
        }
    }

    @Override
    public List<Pirate> findByRank(String rank) {
        String sql = "SELECT * FROM pirates WHERE rank = ? ORDER BY name";
        List<Pirate> pirates = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rank);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pirates.add(mapRowToPirate(rs));
                }
            }
            return pirates;
        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy szukaniu piratów", e);
        }
    }

    @Override
    public void update(Pirate pirate) {
        String sql = "UPDATE pirates SET name = ?, nickname = ?, rank = ?, bounty = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pirate.getName());
            stmt.setString(2, pirate.getNickname());
            stmt.setString(3, pirate.getRank());
            stmt.setBigDecimal(4, pirate.getBounty());
            stmt.setLong(5, pirate.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy aktualizacji pirata", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM pirates WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy usuwaniu pirata", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM pirates";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Błąd przy zliczaniu piratów", e);
        }
    }

    private Pirate mapRowToPirate(ResultSet rs) throws SQLException {
        Pirate pirate = new Pirate();
        pirate.setId(rs.getLong("id"));
        pirate.setName(rs.getString("name"));
        pirate.setNickname(rs.getString("nickname"));
        pirate.setRank(rs.getString("rank"));
        pirate.setBounty(rs.getBigDecimal("bounty"));
        long shipId = rs.getLong("ship_id");
        pirate.setShipId(rs.wasNull() ? null : shipId);
        Date joinedAt = rs.getDate("joined_at");
        pirate.setJoinedAt(joinedAt != null ? joinedAt.toLocalDate() : null);
        return pirate;
    }
}

