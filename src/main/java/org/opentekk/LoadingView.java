package org.opentekk;

/**
 * @author hadyn
 */
public interface LoadingView {

  // TODO(hadyn)
  void i();

  /**
   * Gets the percent of the screen that is loaded.
   *
   * @return the loaded percent.
   */
  int getLoadedPercent();

  void draw(GameStub stub, String text, int percent, boolean clear);

  void destroy();

  /**
   * Gets if the view is stale. If a screen is stale it is expected to be replaced.
   *
   * @param time the time.
   * @return if the screen is stale.
   */
  boolean isStale(long time);

  /**
   * Gets the amount of time in milliseconds it takes to transition from this view to another.
   *
   * @return the transition time.
   */
  int getTransitionTime();
}
