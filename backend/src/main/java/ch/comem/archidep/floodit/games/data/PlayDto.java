package ch.comem.archidep.floodit.games.data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlayDto(
  @NotNull @ExistingGame Long gameId,
  @NotNull @Min(0) Integer color
) {}
