package org.opentekk.io;

import org.opentekk.util.SubNode;

/**
 * @author hadyn
 */
public abstract class AbstractFileRequest extends SubNode {
  private boolean completed;

  protected AbstractFileRequest() {}

  public abstract byte[] getBytes();

  public void setComplete(boolean completed) {
    synchronized (this) {
      this.completed = completed;
    }
  }

  public boolean isCompleted() {
    synchronized (this) {
      return completed;
    }
  }
}
