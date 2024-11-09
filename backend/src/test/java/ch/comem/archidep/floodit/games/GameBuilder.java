package ch.comem.archidep.floodit.games;

import ch.comem.archidep.floodit.games.data.CreateGameDtoBuilder;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.commons.lang3.builder.Builder;
import org.springframework.test.util.ReflectionTestUtils;

public class GameBuilder implements Builder<Game> {

  private final CreateGameDtoBuilder dtoBuilder;
  private Long id;
  private Long seed;
  private LocalDateTime createdAt;

  public GameBuilder(CreateGameDtoBuilder dtoBuilder, long seed) {
    this.dtoBuilder = Objects.requireNonNull(
      dtoBuilder,
      "DTO builder is required"
    );
    this.seed = seed;
  }

  public GameBuilder withId(long id) {
    this.id = id;
    return this;
  }

  public GameBuilder withPlayerName(String playerName) {
    this.dtoBuilder.withPlayerName(playerName);
    return this;
  }

  public GameBuilder withBoardWidth(Integer boardWidth) {
    this.dtoBuilder.withBoardWidth(boardWidth);
    return this;
  }

  public GameBuilder withBoardHeight(Integer boardHeight) {
    this.dtoBuilder.withBoardHeight(boardHeight);
    return this;
  }

  public GameBuilder withNumberOfColors(Integer numberOfColors) {
    this.dtoBuilder.withNumberOfColors(numberOfColors);
    return this;
  }

  public GameBuilder withMaxMoves(Integer maxMoves) {
    this.dtoBuilder.withMaxMoves(maxMoves);
    return this;
  }

  public GameBuilder withSeed(long seed) {
    this.seed = seed;
    return this;
  }

  public GameBuilder withCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  @Override
  public Game build() {
    var game = new Game(this.dtoBuilder.build());

    ReflectionTestUtils.setField(game, "id", this.id);
    ReflectionTestUtils.setField(game, "seed", this.seed);
    ReflectionTestUtils.setField(game, "createdAt", this.createdAt);

    return game;
  }
}
