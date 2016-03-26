package org.opentekk.util;

/**
 * @author hadyn
 */
public class ThreadUtils {

  public static void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ex) {}
  }
}
