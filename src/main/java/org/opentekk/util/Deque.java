package org.opentekk.util;

/**
 * A double-ended linked queue.
 *
 * @author hadyn
 */
public class Deque {
  private Node root = new Node();
  private Node iterator;

  public Deque() {
    root.prevNode = root;
    root.nextNode = root;
  }

  public void addLast(Node node) {
    if (node.prevNode != null) {
      node.unlinkNode();
    }
    node.nextNode = root;
    node.prevNode = root.prevNode;
    node.prevNode.nextNode = node;
    node.nextNode.prevNode = node;
  }

  public Node getFirst() {
    Node node = root.nextNode;
    if (node == root) {
      iterator = null;
      return null;
    }
    iterator = node.nextNode;
    return node;
  }

  public Node getNext() {
    Node node = iterator;
    if(root == node) {
      iterator = null;
      return null;
    }
    iterator = node.nextNode;
    return node;
  }
}
