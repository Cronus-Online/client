package org.opentekk;

import org.opentekk.util.ThreadUtils;
import org.opentekk.util.TimeUtils;

/**
 * Game loading screen.
 *
 * @author hadyn
 */
public class LoadingScreen implements Runnable {
  private GameStub stub;
  private LoadingView view = new SimpleLoadingView();
  private LoadingView previousView;
  private boolean shutdown;
  private int cycle;
  private boolean clear;

  private LoadingStage stage;
  private String message;
  private int progress;

  public LoadingScreen(GameStub stub) {
    this.stub = stub;
  }

  public synchronized void update(LoadingStage stage, int progress, String message) {
    this.stage = stage;
    this.progress = progress;
    this.message = message;
  }

  @Override public void run() {
    while (!shutdown) {
      long start = TimeUtils.getCurrentTimeMillis();
      synchronized (this) {
        cycle++;

        // TODO(hadyn): Transitions between screens when the renderer is initialized.
        if(false) {

        } else {
          if(previousView != null) {
            previousView.destroy();
            previousView = null;
          }

          if(clear) {
            stub.fillPadding();
            // TODO(hadyn)
          }

          view.draw(stub, message, progress, clear);
        }

        // TODO(hadyn): Swap buffers when the renderer is initialized.
      }
      long end = TimeUtils.getCurrentTimeMillis();
      int sleepTime = (int) (start - end + 20L);    // 50 frames per second.
      if(sleepTime > 0) {
        ThreadUtils.sleep(sleepTime);
      }
    }
  }
}
