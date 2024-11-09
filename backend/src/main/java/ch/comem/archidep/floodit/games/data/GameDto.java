package ch.comem.archidep.floodit.games.data;

import ch.comem.archidep.floodit.games.GameState;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameDto {

  private final long id;
  private final GameState state;
  private final String playerName;
  private final int boardWidth;
  private final int boardHeight;
  private final int numberOfColors;
  private final int maxMoves;
  private final List<MoveDto> moves;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public GameDto(
    long id,
    GameState state,
    String playerName,
    int boardWidth,
    int boardHeight,
    int numberOfColors,
    int maxMoves,
    List<MoveDto> moves,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
  ) {
    this.id = id;
    this.state = Objects.requireNonNull(state, "State is required");
    this.playerName = Objects.requireNonNull(
      playerName,
      "Player name is required"
    );
    this.boardWidth = boardWidth;
    this.boardHeight = boardHeight;
    this.numberOfColors = numberOfColors;
    this.maxMoves = maxMoves;
    this.moves = Collections.unmodifiableList(
      Objects.requireNonNull(moves, "Moves are required")
    );
    this.createdAt = Objects.requireNonNull(
      createdAt,
      "Creation date is required"
    );
    this.updatedAt = Objects.requireNonNull(
      updatedAt,
      "Last modification date is required"
    );
  }

  public long getId() {
    return id;
  }

  public GameState getState() {
    return state;
  }

  public String getPlayerName() {
    return playerName;
  }

  public int getBoardWidth() {
    return boardWidth;
  }

  public int getBoardHeight() {
    return boardHeight;
  }

  public int getNumberOfColors() {
    return numberOfColors;
  }

  public int getMaxMoves() {
    return maxMoves;
  }

  public List<MoveDto> getMoves() {
    return moves;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
