package ch.comem.archidep.floodit.games;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.comem.archidep.floodit.FloodItRoutes;
import ch.comem.archidep.floodit.games.data.PlayDto;
import ch.comem.archidep.floodit.utils.AbstractControllerTests;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(MoveController.class)
class MoveControllerTests extends AbstractControllerTests {

  @MockBean
  private GameRepository gameRepository;

  @MockBean
  private GameService gameService;

  @AfterEach
  void teardown() {
    verifyNoMoreInteractions(this.gameService);
  }

  @Test
  void play_a_move() throws Exception {
    var requestBody = GameFixtures.playDto();
    var game = mockGameFound(requestBody.gameId());

    var result = GameFixtures.moveDto();
    when(this.gameService.play(anyString(), any(PlayDto.class))).thenReturn(
      result
    );

    perform(
      playMoveRequest(requestBody).header(
        HttpHeaders.AUTHORIZATION,
        String.format("Bearer %s", game.getSecret())
      )
    )
      .andExpect(status().isCreated())
      .andExpect(content().json(serialize(result)));

    verify(this.gameService, times(1)).play(
      eq(game.getSecret()),
      any(PlayDto.class)
    );
  }

  @Test
  void cannot_play_a_move_without_authorization() throws Exception {
    var requestBody = GameFixtures.playDto();
    mockGameFound(requestBody.gameId());

    perform(playMoveRequest(requestBody))
      .andExpect(status().isUnauthorized())
      .andExpect(content().string(""));
  }

  private Game mockGameFound(Long gameId) {
    var game = GameFixtures.newGame(builder -> builder.withId(gameId));
    when(this.gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    return game;
  }

  private MockHttpServletRequestBuilder playMoveRequest(PlayDto requestBody)
    throws Exception {
    return post(FloodItRoutes.MOVES).content(serialize(requestBody));
  }
}
