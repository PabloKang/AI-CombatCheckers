package checkers;

import java.awt.Point;

public class CheckersMove {
  public final Point source;
  public final Point destination;

  CheckersMove (Point source, Point destination) {
    this.source = source;
    this.destination = destination;
  }

  CheckersMove (int sourceX, int sourceY, int destX, int destY) {
    this.source = new Point(sourceX, sourceY);
    this.destination = new Point(destX, destY);
  }
}
