package ch.comem.archidep.floodit.games;

import ch.comem.archidep.floodit.games.data.CreateGameDto;
import ch.comem.archidep.floodit.games.data.CreatedGameDto;
import ch.comem.archidep.floodit.games.data.GameDto;
import ch.comem.archidep.floodit.games.data.MoveDto;
import ch.comem.archidep.floodit.games.data.PlayDto;
import ch.comem.archidep.floodit.games.exceptions.GameForbidden;
import ch.comem.archidep.floodit.rules.Board;
import ch.comem.archidep.floodit.rules.Position;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class GameService {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    GameService.class
  );

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private MoveRepository moveRepository;

  public CreatedGameDto createGame(CreateGameDto dto) {
    var game = new Game(dto);

    var createdGame = this.gameRepository.saveAndFlush(game);

    LOGGER.info("Created game {}", createdGame);

    return createdGame.toCreatedDto();
  }

  public List<GameDto> listRecentGames() {
    return this.gameRepository.findTop10ByOrderByCreatedAtDesc()
      .stream()
      .map(Game::toDto)
      .toList();
  }

  public GameDto getGame(long id) {
    return this.gameRepository.findById(id)
      .map(Game::toDto)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  public List<List<Integer>> getGameBoard(long id) {
    return this.gameRepository.findById(id)
      .map(game -> {
        var board = game.buildCurrentBoard();
        return this.generateBoard(board);
      })
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  public MoveDto play(String authorization, PlayDto dto) {
    var game = this.loadGame(dto.gameId());
    if (!game.getSecret().equals(authorization)) {
      throw new GameForbidden(game);
    }

    var board = game.buildCurrentBoard();

    var newMove = new Move(game, dto.color());
    game.getMoves().add(newMove);

    var createdMove = this.moveRepository.saveAndFlush(newMove);
    var createdMoveDto = play(
      board,
      createdMove,
      game.getMoves().size(),
      game.getMaxMoves()
    );

    game.setState(createdMoveDto.gameState());
    game.touch();
    this.gameRepository.saveAndFlush(game);

    LOGGER.info("Created move {} for game {}", createdMove, game);

    return createdMoveDto;
  }

  public static MoveDto play(Board board, Move move, int n, int maxMoves) {
    var flooded = board.flood(move.getColor());

    var newState = GameState.ONGOING;
    if (board.isOneColor()) {
      newState = GameState.WIN;
    } else if (n == maxMoves) {
      newState = GameState.LOSS;
    }

    return move.toDto(flooded, newState);
  }

  private List<List<Integer>> generateBoard(Board board) {
    return IntStream.range(0, board.getHeight())
      .mapToObj(row -> this.generateRow(board, row))
      .toList();
  }

  private List<Integer> generateRow(Board board, int row) {
    return IntStream.range(0, board.getWidth())
      .mapToObj(column -> board.getColor(Position.at(column, row)))
      .collect(Collectors.toList());
  }

  private Game loadGame(Long gameId) {
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
