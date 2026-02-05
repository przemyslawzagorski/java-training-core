package pl.przemekzagorski.training.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model pirata - zwykłe POJO (Plain Old Java Object).
 */
public class Pirate {

    private Long id;
    private String name;
    private String nickname;
    private String rank;
    private BigDecimal bounty;
    private Long shipId;
    private LocalDate joinedAt;

    public Pirate() {}

    public Pirate(String name, String rank, BigDecimal bounty) {
        this.name = name;
        this.rank = rank;
        this.bounty = bounty;
    }

    public Pirate(Long id, String name, String nickname, String rank,
                  BigDecimal bounty, Long shipId, LocalDate joinedAt) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.rank = rank;
        this.bounty = bounty;
        this.shipId = shipId;
        this.joinedAt = joinedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    public BigDecimal getBounty() { return bounty; }
    public void setBounty(BigDecimal bounty) { this.bounty = bounty; }
    public Long getShipId() { return shipId; }
    public void setShipId(Long shipId) { this.shipId = shipId; }
    public LocalDate getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDate joinedAt) { this.joinedAt = joinedAt; }

    @Override
    public String toString() {
        return String.format("Pirate{id=%d, name='%s', rank='%s', bounty=%s}", id, name, rank, bounty);
    }
}

