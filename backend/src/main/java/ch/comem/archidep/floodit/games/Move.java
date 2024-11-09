package ch.comem.archidep.floodit.games;

import ch.comem.archidep.floodit.games.data.MoveDto;
import ch.comem.archidep.floodit.rules.Position;
import ch.comem.archidep.floodit.utils.ObjectUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = Move.TABLE_NAME)
public class Move implements Comparable<Move> {

  public static final String TABLE_NAME = "moves";
  public static final String FOREIGN_KEY_GAME = "game_id_fkey";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(foreignKey = @ForeignKey(name = Move.FOREIGN_KEY_GAME))
  private Game game;

  @Column(nullable = false)
  private int color;

  @CreationTimestamp
  @Column(nullable = false)
  private LocalDateTime createdAt;

  Move(Game game, int color) {
    this.game = Objects.requireNonNull(game, "Game is required");
    this.color = color;
  }

  Move() {
    // No-args constructor for Hibernate
  }

  public MoveDto toDto(Set<Position> flooded, GameState gameState) {
    return new MoveDto(this.id, this.color, flooded, gameState, this.createdAt);
  }

  @Override
  public int compareTo(Move other) {
    return this.createdAt.compareTo(other.createdAt);
  }

  @Override
  public boolean equals(Object obj) {
    return ObjectUtils.equals(
      this,
      obj,
      (builder, otherMove) -> builder.append(this.id, otherMove.id)
    );
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(29597, 717).append(this.id).toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("id", this.id)
      .append("gameId", Optional.of(this.game).map(Game::getId).orElse(null))
      .append("color", this.color)
      .append("createdAt", this.createdAt)
      .build();
  }

  public Long getId() {
    return id;
  }

  public Game getGame() {
    return game;
  }

  public int getColor() {
    return color;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
