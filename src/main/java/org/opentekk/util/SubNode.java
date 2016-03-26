package org.opentekk.util;

/**
 * A node that can belong to a {@link Queue}.
 *
 * @author hadyn
 */
public class SubNode extends Node {
  SubNode prevSubNode;
  SubNode nextSubNode;
  long subNodeKey;
  
  public void setSubNodeKey(long subNodeKey) {
    this.subNodeKey = subNodeKey;
  }

  public long getSubNodeKey() {
    return subNodeKey;
  }

  public void unlinkSubNode() {
    if(prevSubNode == null) {
      return;
    }
    prevSubNode.nextNode = nextSubNode;
    nextSubNode.prevNode = prevSubNode;
    prevSubNode = null;
    nextSubNode = null;
  }
}
