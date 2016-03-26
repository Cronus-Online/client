package org.opentekk;

/**
 * @author hadyn
 */
public class GameState {
  public static final int INITIAL_LOADING = 0;

  private GameState() {}

  public static boolean isLoading(int state) {
    return state == INITIAL_LOADING;
  }
}
