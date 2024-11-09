package ch.comem.archidep.floodit.rules;

import ch.comem.archidep.floodit.utils.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JsonFormat(shape = Shape.ARRAY)
@JsonPropertyOrder({ "column", "row" })
public class Position {

  public static Position at(int column, int row) {
    return new Position(column, row);
  }

  private final int column;
  private final int row;

  Position(int column, int row) {
    if (column < 0) {
      throw new IllegalArgumentException(
        "Column must be an integer greater than or equal to zero"
      );
    } else if (row < 0) {
      throw new IllegalArgumentException(
        "Row must be an integer greater than or equal to zero"
      );
    }

    this.column = column;
    this.row = row;
  }

  public int getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }

  @JsonIgnore
  public Set<Position> getAdjacentPositions() {
    var adjacentPositions = new HashSet<Position>();
    adjacentPositions.add(Position.at(this.column + 1, this.row));
    adjacentPositions.add(Position.at(this.column, this.row + 1));

    if (this.column > 0) {
      adjacentPositions.add(Position.at(this.column - 1, this.row));
    }

    if (this.row > 0) {
      adjacentPositions.add(Position.at(this.column, this.row - 1));
    }

    return adjacentPositions;
  }

  @Override
  public boolean equals(Object obj) {
    return ObjectUtils.equals(this, obj, (builder, otherPosition) ->
      builder
        .append(this.column, otherPosition.column)
        .append(this.row, otherPosition.row)
    );
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17931, 311)
      .append(this.column)
      .append(this.row)
      .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("column", this.column)
      .append("row", this.row)
      .build();
  }
}
