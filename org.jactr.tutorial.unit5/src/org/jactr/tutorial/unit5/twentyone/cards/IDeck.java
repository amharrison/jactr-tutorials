package org.jactr.tutorial.unit5.twentyone.cards;

import java.util.stream.Stream;

public interface IDeck {
  public Stream<ICard> cards();
}
