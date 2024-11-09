package ch.comem.archidep.floodit.games.data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGameDto(
  @NotBlank String playerName,
  @NotNull @Min(2) @Max(100) Integer boardWidth,
  @NotNull @Min(2) @Max(100) Integer boardHeight,
  @NotNull @Min(2) @Max(10) Integer numberOfColors,
  @NotNull @Min(1) Integer maxMoves
) {}
