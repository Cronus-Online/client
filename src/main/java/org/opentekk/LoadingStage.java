package org.opentekk;

/**
 * @author hadyn
 */
public class LoadingStage {
  public static final LoadingStage ALLOCATE_MEMORY =
    new LoadingStage(0, 0, 100, "Allocating " + "memory - ", "Allocated memory - ", true, true);

  private int id;
  private int incompleteProgress;
  private int completeProgress;
  private String incompleteText;
  private String completeText;
  private boolean displayProgress;
  private boolean updateProgress;

  private LoadingStage(int id, int incompleteProgress, int completeProgress, String incompleteText,
    String completeText) {
    this(id, incompleteProgress, completeProgress, incompleteText, completeText, true, false);
  }

  private LoadingStage(int id, int incompleteProgress, int completeProgress, String incompleteText,
    String completeText, boolean displayProgress, boolean updateProgress) {
    this.id = id;
    this.incompleteProgress = incompleteProgress;
    this.completeProgress = completeProgress;
    this.incompleteText = incompleteText;
    this.completeText = completeText;
    this.displayProgress = displayProgress;
    this.updateProgress = updateProgress;
  }

  public int getId() {
    return id;
  }

  public int getIncompleteProgress() {
    return incompleteProgress;
  }

  public int getCompleteProgress() {
    return completeProgress;
  }

  public String getIncompleteText() {
    return incompleteText;
  }

  public String getCompleteText() {
    return completeText;
  }

  public boolean getAppendProgress() {
    return displayProgress;
  }

  public boolean getUpdateProgress() {
    return updateProgress;
  }

  public static LoadingStage[] getLoadingStages() {
    return new LoadingStage[] {ALLOCATE_MEMORY};
  }
}
