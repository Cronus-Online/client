package org.opentekk.util;

/**
 * @author hadyn
 */
public class TimeUtils {
  private static long previousTime;
  private static long delta;

  private TimeUtils() {}

  public static long getCurrentTimeMillis() {
    long time = System.currentTimeMillis();
    if (time < previousTime) {
      delta += previousTime - time;
    }
    previousTime = time;
    return time + delta;
  }
}
