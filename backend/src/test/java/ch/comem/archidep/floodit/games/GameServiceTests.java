package ch.comem.archidep.floodit.games;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.comem.archidep.floodit.games.data.CreateGameDto;
import ch.comem.archidep.floodit.games.data.GameDto;
import ch.comem.archidep.floodit.games.exceptions.GameForbidden;
import ch.comem.archidep.floodit.rules.Position;
import ch.comem.archidep.floodit.utils.AbstractServiceTests;
import ch.comem.archidep.floodit.utils.TestUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class GameServiceTests extends AbstractServiceTests {

  @Autowired
  private GameFixtures gameFixtures;

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private GameService gameService;

  @Test
  void create_a_game() {
    var dto = new CreateGameDto("John Doe", 30, 30, 6, 25);

    var now = LocalDateTime.now();
    var result = this.gameService.createGame(dto);

    assertThat(result.getId(), is(greaterThanOrEqualTo(1L)));
    assertThat(
      result.getSecret(),
      allOf(hasLength(255), not(blankOrNullString()))
    );
    assertThat(result.getState(), is(GameState.ONGOING));
    assertThat(result.getPlayerName(), is(equalTo("John Doe")));
    assertThat(result.getBoardWidth(), is(equalTo(30)));
    assertThat(result.getBoardHeight(), is(equalTo(30)));
    assertThat(result.getNumberOfColors(), is(equalTo(6)));
    assertThat(result.getMaxMoves(), is(equalTo(25)));
    assertThat(result.getMoves(), is(empty()));
    assertThat(
      result.getCreatedAt(),
      is(allOf(greaterThanOrEqualTo(now), lessThan(now.plusSeconds(3))))
    );
    assertThat(
      result.getUpdatedAt(),
      is(allOf(greaterThanOrEqualTo(now), lessThan(now.plusSeconds(3))))
    );
    assertThat(
      TestUtils.getFieldNames(result),
      containsInAnyOrder(
        "id",
        "secret",
        "state",
        "playerName",
        "boardWidth",
        "boardHeight",
        "numberOfColors",
        "maxMoves",
        "moves",
        "createdAt",
        "updatedAt"
      )
    );

    transactionWithoutResult(() -> {
      var createdGame = this.getGame(result.getId());
      assertThat(createdGame.getSecret(), is(equalTo(result.getSecret())));
      assertThat(createdGame.getState(), is(equalTo(result.getState())));
      assertThat(
        createdGame.getPlayerName(),
        is(equalTo(result.getPlayerName()))
      );
      assertThat(
        createdGame.getBoardWidth(),
        is(equalTo(result.getBoardWidth()))
      );
      assertThat(
        createdGame.getBoardHeight(),
        is(equalTo(result.getBoardHeight()))
      );
      assertThat(
        createdGame.getNumberOfColors(),
        is(equalTo(result.getNumberOfColors()))
      );
      assertThat(createdGame.getMaxMoves(), is(equalTo(result.getMaxMoves())));
      assertThat(
        createdGame.getSeed(),
        is(
          allOf(
            greaterThanOrEqualTo(Long.MIN_VALUE),
            lessThanOrEqualTo(Long.MAX_VALUE)
          )
        )
      );
      assertThat(createdGame.getMoves(), is(empty()));
      assertThat(
        createdGame.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
        is(equalTo(result.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)))
      );
      assertThat(
        createdGame.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS),
        is(equalTo(result.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)))
      );
      assertThat(createdGame.getVersion(), is(equalTo(0L)));
      assertThat(
        TestUtils.getFieldNames(createdGame),
        containsInAnyOrder(
          "id",
          "secret",
          "state",
          "playerName",
          "boardWidth",
          "boardHeight",
          "numberOfColors",
          "maxMoves",
          "seed",
          "moves",
          "createdAt",
          "updatedAt",
          "version"
        )
      );
    });
  }

  @Test
  void list_games() {
    var gamePlayedTwoDaysAgo =
      this.gameFixtures.game(builder ->
          builder.withCreatedAt(LocalDateTime.now().minusDays(2))
        );
    var gamePlayedOneWeekAgo =
      this.gameFixtures.game(builder ->
          builder.withCreatedAt(LocalDateTime.now().minusWeeks(1))
        );
    var gamePlayedJustNow = this.gameFixtures.game();

    var result = this.gameService.listRecentGames();

    assertThat(
      result.stream().map(GameDto::getId).toList(),
      contains(
        gamePlayedJustNow.getId(),
        gamePlayedTwoDaysAgo.getId(),
        gamePlayedOneWeekAgo.getId()
      )
    );
  }

  @Test
  void get_a_game() {
    var game = this.gameFixtures.game();

    var result = this.gameService.getGame(game.getId());

    assertThat(result.getId(), is(equalTo(game.getId())));
  }

  @Test
  void cannot_get_a_non_existent_game() {
    var game = this.gameFixtures.game();
    var nonExistentId = game.getId() + 1;

    var exception = assertThrows(ResponseStatusException.class, () -> {
      this.gameService.getGame(nonExistentId);
    });

    assertThat(exception.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  void get_the_board_of_a_game() {
    var game =
      this.gameFixtures.game(builder ->
          builder
            .withBoardWidth(3)
            .withBoardHeight(3)
            .withNumberOfColors(3)
            .withSeed(42)
        );

    var result = this.gameService.getGameBoard(game.getId());

    // 2 2 2
    // 0 0 2
    // 0 1 1
    assertThat(
      result,
      is(equalTo(List.of(List.of(2, 2, 2), List.of(0, 0, 2), List.of(0, 1, 1))))
    );
  }

  @Test
  void cannot_get_the_board_of_a_non_existent_game() {
    var game = this.gameFixtures.game();
    var nonExistentId = game.getId() + 1;

    var exception = assertThrows(ResponseStatusException.class, () -> {
      this.gameService.getGameBoard(nonExistentId);
    });

    assertThat(exception.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  void play_the_first_move_in_a_game() {
    var game = this.gameFixtures.game(builder -> builder.withMaxMoves(3));
    var dto = GameFixtures.playDto(game);

    var now = LocalDateTime.now();
    var result = this.gameService.play(game.getSecret(), dto);

    assertThat(result.id(), is(greaterThanOrEqualTo(1L)));
    assertThat(result.color(), is(equalTo(dto.color())));
    assertThat(result.flooded(), is(not(empty())));
    assertThat(result.gameState(), is(GameState.ONGOING));
    assertThat(
      result.createdAt(),
      is(allOf(greaterThanOrEqualTo(now), lessThan(now.plusSeconds(3))))
    );
    assertThat(
      TestUtils.getFieldNames(result),
      containsInAnyOrder("id", "color", "flooded", "gameState", "createdAt")
    );

    transactionWithoutResult(() -> {
      var updatedGame = this.getGame(game);

      assertThat(
        updatedGame.getUpdatedAt(),
        is(allOf(greaterThanOrEqualTo(now), lessThan(now.plusSeconds(3))))
      );

      var moves = updatedGame.getMoves();
      assertThat(moves, hasSize(1));

      var createdMove = updatedGame.getMoves().get(0);
      assertThat(createdMove, isA(Move.class));
      assertThat(createdMove.getId(), is(greaterThanOrEqualTo(1L)));
      assertThat(
        Optional.ofNullable(createdMove.getGame())
          .map(Game::getId)
          .orElse(null),
        is(equalTo(game.getId()))
      );
      assertThat(createdMove.getColor(), is(equalTo(dto.color())));
      assertThat(
        createdMove.getCreatedAt(),
        is(allOf(greaterThanOrEqualTo(now), lessThan(now.plusSeconds(3))))
      );
      assertThat(
        TestUtils.getFieldNames(createdMove),
        containsInAnyOrder("id", "game", "color", "createdAt")
      );
    });
  }

  @Test
  void win_a_3x3_game_in_2_moves() {
    // 2 2 2
    // 0 0 2
    // 0 1 1
    var game =
      this.gameFixtures.game(builder ->
          builder
            .withBoardWidth(3)
            .withBoardHeight(3)
            .withNumberOfColors(3)
            .withMaxMoves(3)
            .withSeed(42)
        );

    // 0 0 0
    // 0 0 0
    // 0 1 1
    var firstMoveDto = GameFixtures.playDto(builder ->
      builder.withGame(game).withColor(0)
    );
    var firstMove = this.gameService.play(game.getSecret(), firstMoveDto);
    assertThat(
      firstMove.flooded(),
      containsInAnyOrder(
        Position.at(0, 0),
        Position.at(1, 0),
        Position.at(2, 0),
        Position.at(2, 1)
      )
    );
    assertThat(firstMove.gameState(), is(GameState.ONGOING));

    // 1 1 1
    // 1 1 1
    // 1 1 1
    var secondMoveDto = GameFixtures.playDto(builder ->
      builder.withGame(game).withColor(1)
    );
    var secondMove = this.gameService.play(game.getSecret(), secondMoveDto);
    assertThat(
      secondMove.flooded(),
      containsInAnyOrder(
        Position.at(0, 1),
        Position.at(0, 2),
        Position.at(1, 1)
      )
    );
    assertThat(secondMove.gameState(), is(GameState.WIN));

    transactionWithoutResult(() -> {
      var updatedGame = this.getGame(game);
      assertThat(updatedGame.getState(), is(GameState.WIN));
    });
  }

  @Test
  void lose_a_3x3_game_in_3_moves() {
    // 2 2 2
    // 0 0 2
    // 0 1 1
    var game =
      this.gameFixtures.game(builder ->
          builder
            .withBoardWidth(3)
            .withBoardHeight(3)
            .withNumberOfColors(3)
            .withMaxMoves(3)
            .withSeed(42)
        );

    // 1 1 1
    // 0 0 1
    // 0 1 1
    var firstMoveDto = GameFixtures.playDto(builder ->
      builder.withGame(game).withColor(1)
    );
    var firstMove = this.gameService.play(game.getSecret(), firstMoveDto);
    assertThat(
      firstMove.flooded(),
      containsInAnyOrder(
        Position.at(0, 0),
        Position.at(1, 0),
        Position.at(2, 0),
        Position.at(2, 1)
      )
    );
    assertThat(firstMove.gameState(), is(GameState.ONGOING));

    // 2 2 2
    // 0 0 2
    // 0 2 2
    var secondMoveDto = GameFixtures.playDto(builder ->
      builder.withGame(game).withColor(2)
    );
    var secondMove = this.gameService.play(game.getSecret(), secondMoveDto);
    assertThat(
      secondMove.flooded(),
      containsInAnyOrder(Position.at(1, 2), Position.at(2, 2))
    );
    assertThat(secondMove.gameState(), is(GameState.ONGOING));

    // 1 1 1
    // 0 0 1
    // 0 1 1
    var thirdMoveDto = GameFixtures.playDto(builder ->
      builder.withGame(game).withColor(1)
    );
    var thirdMove = this.gameService.play(game.getSecret(), thirdMoveDto);
    assertThat(thirdMove.flooded(), is(empty()));
    assertThat(thirdMove.gameState(), is(GameState.LOSS));

    transactionWithoutResult(() -> {
      var updatedGame = this.getGame(game);
      assertThat(updatedGame.getState(), is(GameState.LOSS));
    });
  }

  @Test
  void cannot_play_without_the_correct_game_secret() {
    var game = this.gameFixtures.game();
    var dto = GameFixtures.playDto(game);

    var wrongSecret = game.getSecret() + "foo";

    assertThrows(GameForbidden.class, () ->
      this.gameService.play(wrongSecret, dto)
    );
  }

  private Game getGame(Game game) {
    return this.getGame(game.getId());
  }

  private Game getGame(Long gameId) {
    return this.gameRepository.findById(gameId).orElseThrow(() ->
        new IllegalStateException(
          String.format(
            "Could not find game with ID %s in the database",
            gameId
          )
        )
      );
  }
}
