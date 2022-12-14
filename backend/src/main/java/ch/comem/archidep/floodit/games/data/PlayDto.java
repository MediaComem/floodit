package ch.comem.archidep.floodit.games.data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record PlayDto(
  @NotNull @ExistingGame Long gameId,
  @NotNull @Min(0) Integer color
) {}
