package org.opentekk.util;

/**
 * A linked queue.
 *
 * @author hadyn
 */
public class Queue {
  private SubNode root = new SubNode();
  private SubNode iterator;

  public Queue() {
    root.prevSubNode = root;
    root.nextSubNode = root;
  }

  public void add(SubNode node) {
    if (node.prevNode != null) {
      node.unlinkNode();
    }
    node.nextSubNode = root;
    node.prevSubNode = root.prevSubNode;
    node.prevSubNode.nextSubNode = node;
    node.nextSubNode.prevSubNode = node;
  }

  public SubNode getFirst() {
    SubNode node = root.nextSubNode;
    if (node == root) {
      iterator = null;
      return null;
    }
    iterator = node.nextSubNode;
    return node;
  }

  public SubNode getNext() {
    SubNode node = iterator;
    if(root == node) {
      iterator = null;
      return null;
    }
    iterator = node.nextSubNode;
    return node;
  }

  public SubNode poll() {
    SubNode node = root.nextSubNode;
    if (node == root) {
      return null;
    }
    node.unlinkSubNode();
    return node;
  }
}
