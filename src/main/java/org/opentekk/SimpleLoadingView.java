package org.opentekk;

/**
 * @author hadyn
 */
public class SimpleLoadingView implements LoadingView {
  @Override public void i() {

  }

  @Override public int getLoadedPercent() {
    return 100;  // That's my secret Cam, I'm always loaded.
  }

  @Override public void draw(GameStub stub, String text, int percent, boolean clear) {
    // TODO(hadyn): Find a better place to put the overhead message.
    // TODO(hadyn): See if we can track down the old images for the loading screen.
    stub.drawLoadingBar(percent, text, "Loading please wait...");
  }

  @Override public void destroy() {

  }

  @Override public boolean isStale(long time) {
    return true;
  }

  @Override public int getTransitionTime() {
    return 0;
  }
}
