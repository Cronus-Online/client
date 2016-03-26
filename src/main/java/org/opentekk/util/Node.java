package org.opentekk.util;

/**
 * A node that can belong to {@link Deque}.
 *
 * @author hadyn
 */
public class Node {
  Node prevNode;
  Node nextNode;
  long nodeKey;

  public void unlinkNode() {
    if(prevNode == null) {
      return;
    }
    prevNode.nextNode = nextNode;
    nextNode.prevNode = prevNode;
    prevNode = null;
    nextNode = null;
  }
}
