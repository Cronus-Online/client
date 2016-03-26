package org.opentekk.input;

import java.awt.Component;
import java.awt.event.*;

/**
 * @author hadyn
 */
public class ScrollableMouse extends AbstractMouse
  implements MouseListener, MouseMotionListener, MouseWheelListener {

  public ScrollableMouse(Component component) {
    component.addMouseListener(this);
    component.addMouseMotionListener(this);
    component.addMouseWheelListener(this);
  }

  @Override public void mouseClicked(MouseEvent e) {

  }

  @Override public void mousePressed(MouseEvent e) {

  }

  @Override public void mouseReleased(MouseEvent e) {

  }

  @Override public void mouseEntered(MouseEvent e) {

  }

  @Override public void mouseExited(MouseEvent e) {

  }

  @Override public void mouseDragged(MouseEvent e) {

  }

  @Override public void mouseMoved(MouseEvent e) {

  }

  @Override public void mouseWheelMoved(MouseWheelEvent e) {

  }
}
