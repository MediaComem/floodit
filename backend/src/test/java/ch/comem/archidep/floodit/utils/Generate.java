package ch.comem.archidep.floodit.utils;

import com.github.javafaker.Faker;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generate random data and combinations of elements.
 *
 * @since 1.1.0
 */
public final class Generate {

  private static final Faker FAKER = new Faker();

  /**
   * Generates a random list of elements.
   *
   * @param <T> the type of element
   * @param generator the supplier of elements
   * @param count the number of elements to generate
   * @return a list of generated elements
   */
  public static <T> List<T> randomListOf(Supplier<T> generator, int count) {
    return randomStreamOf(generator, count, count).collect(Collectors.toList());
  }

  /**
   * Generates a random list of elements.
   *
   * @param <T> the type of element
   * @param generator the supplier of elements
   * @param min the minimum number of elements to generate
   * @param max the maximum number of elements to generate
   * @return a list of generated elements
   */
  public static <T> List<T> randomListOf(
    Supplier<T> generator,
    int min,
    int max
  ) {
    return randomStreamOf(generator, min, max).collect(Collectors.toList());
  }

  /**
   * Generates a random stream of elements.
   *
   * @param <T> the type of element
   * @param generator the supplier of elements
   * @param min the minimum number of elements to generate
   * @param max the maximum number of elements to generate
   * @return a stream of generated elements
   */
  private static <T> Stream<T> randomStreamOf(
    Supplier<T> generator,
    int min,
    int max
  ) {
    if (min < 0) {
      throw new IllegalArgumentException(
        "Minimum must be greater than or equal to zero"
      );
    } else if (min > max) {
      throw new IllegalArgumentException(
        String.format("Minimum %d is greater than maximum %d", min, max)
      );
    }

    return Stream.generate(generator).limit(
      FAKER.number().numberBetween(min, max)
    );
  }

  private Generate() {}
}
