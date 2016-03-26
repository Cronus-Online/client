package org.opentekk;

import org.opentekk.sign.SignLink;
import org.opentekk.util.ThreadUtils;

import java.awt.*;

/**
 * @author hadyn
 */
// TODO(hadyn): View is a questionable name, need to determine if it fits.
public abstract class GameStub implements Runnable {
  private SignLink signLink;
  private int maximumMemory = 64;
  private int availableProcessors = 1;

  private Frame window;
  private int windowWidth;
  private int windowHeight;

  private Canvas canvas;
  private int viewPositionX;
  private int viewPositionY;
  private int viewWidth;
  private int viewHeight;
  private Image canvasImage;

  private Font loadingBarFont;

  /**
   * Initializes the game. This function is only ever called once, on startup.
   */
  public abstract void initialize();

  public abstract void update();

  public abstract void draw();

  @Override public void run() {
    Runtime runtime = Runtime.getRuntime();
    maximumMemory = (int) (runtime.maxMemory() / 1048576L);
    availableProcessors = runtime.availableProcessors();

    initializeCanvas();
    initialize();

    while (true) {
      update();
      draw();
      ThreadUtils.sleep(50L);
    }
  }

  // TODO(hadyn): Come up with a better formal definition for this.
  public void initializeCanvas() {
    if (canvas != null) {
      canvas.getParent().setBackground(Color.black);
      canvas.getParent().remove(canvas);
    }

    // TODO(hadyn): Implement future support if we ever want to have a web client (use applet).
    Container container = window;
    container.setLayout(null);

    canvas = new Canvas();
    container.add(canvas);
    canvas.setSize(viewWidth, viewHeight);
    canvas.setVisible(true);

    if (container == window) {
      Insets insets = container.getInsets();
      canvas.setLocation(insets.left + viewPositionX, insets.top + viewPositionY);
    } else {
      canvas.setLocation(viewPositionX, viewPositionY);
    }
    canvas.requestFocus();
  }

  /**
   * Fills the space between the edges of the window and the view.
   */
  public void fillPadding() {
    int x = viewPositionX;
    int y = viewPositionY;
    int paddingWidth = (windowWidth - viewWidth) - x;
    int paddingHeight = (windowHeight - viewHeight) - y;

    if (x > 0 || paddingWidth > 0 || y > 0 || paddingHeight > 0) {
      Graphics graphics = window.getGraphics();
      graphics.setColor(Color.black);

      Insets insets = window.getInsets();
      if (x > 0) {
        graphics.fillRect(insets.left, insets.top, x, windowHeight);
      }

      if (y > 0) {
        graphics.fillRect(insets.left, insets.top, windowWidth, y);
      }

      if (paddingWidth > 0) {
        graphics.fillRect(insets.left + windowWidth - paddingWidth, insets.top, paddingWidth,
          windowHeight);
      }

      if (paddingHeight > 0) {
        graphics.fillRect(insets.left, insets.top + windowHeight - paddingHeight, windowWidth,
          paddingHeight);
      }
    }
  }

  /**
   * Draws the loading bar using the default outline, bar, and text colors. The outline and
   * bar colors are both (140, 17, 17). The text color is white (255, 255,2 55).
   *
   * @param percent      the loaded percent.
   * @param overlayText  the text to draw over the bar.
   * @param overheadText the text to draw above the bar.
   */
  public void drawLoadingBar(int percent, String overlayText, String overheadText) {
    drawLoadingBar(new Color(140, 17, 17), new Color(140, 17, 17), new Color(255, 255, 255),
      percent, overlayText, overheadText);
  }

  public void drawLoadingBar(Color outlineColor, Color loadingBarColor, Color textColor,
    int percent, String overlayText, String overheadText) {
    if (loadingBarFont == null) {
      loadingBarFont = new Font("Helvetica", 1, 13);
    }
    Graphics canvasGraphics = canvas.getGraphics();
    try {
      if (canvasImage == null) {
        canvasImage = canvas.createImage(viewWidth, viewHeight);
      }
      Graphics imageGraphics = canvasImage.getGraphics();

      imageGraphics.setColor(Color.black);
      imageGraphics.fillRect(0, 0, viewWidth, viewHeight);

      int x = viewWidth / 2 - 152;
      int y = viewHeight / 2 - 18;

      imageGraphics.setColor(outlineColor);
      imageGraphics.drawRect(x, y, 303, 33);

      imageGraphics.setColor(loadingBarColor);
      imageGraphics.fillRect(2 + x, 2 + y, percent * 3, 30);

      imageGraphics.setColor(Color.black);
      imageGraphics.drawRect(1 + x, 1 + y, 301, 31);
      imageGraphics.fillRect(3 * percent + 2 + x, 2 + y, 300 - (percent * 3), 30);

      imageGraphics.setFont(loadingBarFont);
      imageGraphics.setColor(textColor);

      if (overheadText != null) {
        imageGraphics.drawString(overheadText, (viewWidth / 2) - overheadText.length() * 6 / 2,
          viewHeight / 2 - 26);
      }

      imageGraphics.drawString(overlayText, x + (304 - overlayText.length() * 6) / 2, 22 + y);

      canvasGraphics.drawImage(canvasImage, 0, 0, null);
    } catch (Exception ex) {

      canvasGraphics.setColor(Color.black);
      canvasGraphics.fillRect(0, 0, viewWidth, viewHeight);

      int x = viewWidth / 2 - 152;
      int y = viewHeight / 2 - 18;

      canvasGraphics.setColor(outlineColor);
      canvasGraphics.drawRect(x, y, 303, 33);

      canvasGraphics.setColor(loadingBarColor);
      canvasGraphics.fillRect(2 + x, 2 + y, percent * 3, 30);

      canvasGraphics.setColor(Color.black);
      canvasGraphics.drawRect(1 + x, 1 + y, 301, 31);
      canvasGraphics.fillRect(3 * percent + 2 + x, 2 + y, 300 - (percent * 3), 30);

      canvasGraphics.setFont(loadingBarFont);
      canvasGraphics.setColor(textColor);

      if (overheadText != null) {
        canvasGraphics.drawString(overheadText, (viewWidth / 2) - (overheadText.length() * 6 / 2),
          viewHeight / 2 - 26);
      }

      canvasGraphics.drawString(overlayText, x + ((304 - (overlayText.length() * 6)) / 2), 22 + y);
    }
  }

  public SignLink getSignLink() {
    return signLink;
  }

  public Canvas getCanvas() {
    return canvas;
  }

  public void start(int width, int height) {
    viewPositionX = 0;
    viewPositionY = 0;
    windowWidth = viewWidth = width;
    windowHeight = viewHeight = height;

    window = new Frame("OpenTekk");
    window.setResizable(true);
    window.setVisible(true);
    window.toFront();

    Insets insets = window.getInsets();
    window.setSize(insets.right + insets.left + width, insets.bottom +
      height + insets.top);

    signLink = new SignLink();

    SignLink.Request request = signLink.createThread(this, 1);
    while (request.getStatus() == SignLink.Request.STATUS_OK) {
      ThreadUtils.sleep(10L);
    }
  }
}
