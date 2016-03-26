package org.opentekk.input;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author hadyn
 */
public class Keyboard extends AbstractKeyboard implements KeyListener {

  private Component component;

  public Keyboard(Component component) {
    this.component = component;
    component.addKeyListener(this);
  }

  @Override public void keyTyped(KeyEvent e) {
    System.out.println(e);
  }

  @Override public void keyPressed(KeyEvent e) {
    System.out.println(e);
  }

  @Override public void keyReleased(KeyEvent e) {
    System.out.println(e);
  }
}
