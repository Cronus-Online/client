package org.opentekk;

import org.opentekk.input.AbstractKeyboard;
import org.opentekk.input.AbstractMouse;
import org.opentekk.input.Keyboard;
import org.opentekk.input.ScrollableMouse;
import org.opentekk.io.FileCacheWorker;

/**
 * @author hadyn
 */
public class GameClient extends GameStub {
  private int state = GameState.INITIAL_LOADING;

  private AbstractKeyboard keyboard;
  private AbstractMouse mouse;

  private FileCacheWorker fileCacheWorker;

  private LoadingStage[] loadingStages;
  private LoadingScreen loadingScreen;
  private LoadingStage loadingStage;
  private Thread loadingScreenThread;
  private int completedProgress;

  @Override public void initialize() {
    fileCacheWorker = new FileCacheWorker(getSignLink());
    keyboard = new Keyboard(getCanvas());
    mouse = new ScrollableMouse(getCanvas());
  }

  @Override public void update() {
    if (GameState.isLoading(state)) {
      updateLoading();
    }
  }

  @Override public void draw() {
    // If the client is still loading then just update the loading screen.
    if (GameState.isLoading(state)) {

    }
  }

  public void setState(int state) {
    if (this.state != state) {
      this.state = state;
    }
  }

  private void updateLoading() {
    if (loadingStages == null) {
      loadingStages = LoadingStage.getLoadingStages();
      loadingStage = loadingStages[0];
    }

    if (loadingScreen == null) {
      loadingScreen = new LoadingScreen(this);
      loadingScreenThread = new Thread(loadingScreen, "Loading Screen");
      loadingScreenThread.start();
      loadingScreen.update(loadingStage, loadingStage.getIncompleteProgress(),
        loadingStage.getIncompleteText());
    }

    LoadingStage stage = loadingStage;
    int stageProgress = load();

    String text;
    if (stage != loadingStage) {
      text = stage.getCompleteText();
      if (loadingStage.getAppendProgress()) {
        text += stage.getCompleteProgress() + "%";
      }
      completedProgress = stage.getCompleteProgress();
    } else {
      text = stage.getIncompleteText();

      if (stage.getUpdateProgress()) {
        completedProgress = stage.getIncompleteProgress()
          + stageProgress * (stage.getCompleteProgress() - stage.getIncompleteProgress()) / 100;
      }

      if (stage.getAppendProgress()) {
        text += completedProgress + "%";
      }
    }

    loadingScreen.update(loadingStage, completedProgress, text);
  }

  /**
   * Handles the loading for the game for the current {@link LoadingStage} the game is set to.
   *
   * @return the progress of how complete the current {@link LoadingStage} is as a percent.
   */
  private int load() {
    if(loadingStage == LoadingStage.ALLOCATE_MEMORY) {

    }

    // Update the loading stage if the stage is completed.
    if (loadingStage.getId() + 1 < loadingStages.length) {
      loadingStage = loadingStages[loadingStage.getId() + 1];
    }
    return 100;
  }

  public static void main(String... args) {
    GameClient client = new GameClient();
    client.start(1024, 768);                // 4:3 aspect ratio
  }

}
