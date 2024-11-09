package ch.comem.archidep.floodit.games;

import ch.comem.archidep.floodit.FloodItRoutes;
import ch.comem.archidep.floodit.games.data.MoveDto;
import ch.comem.archidep.floodit.games.data.PlayDto;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(FloodItRoutes.MOVES)
@Validated
public class MoveController {

  @Autowired
  private GameService gameService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MoveDto playMove(
    @RequestHeader(
      HttpHeaders.AUTHORIZATION
    ) Optional<String> authorizationHeader,
    @Valid @RequestBody PlayDto requestBody
  ) {
    return this.gameService.play(
        authorizationHeader
          .map(header -> header.replaceFirst("Bearer ", ""))
          .orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED)
          ),
        requestBody
      );
  }
}
