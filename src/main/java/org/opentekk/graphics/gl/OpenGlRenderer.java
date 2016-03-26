package org.opentekk.graphics.gl;

import org.opentekk.graphics.RendererInfo;
import org.opentekk.graphics.RendererToolkit;
import com.jogamp.nativewindow.NativeWindowFactory;
import com.jogamp.nativewindow.awt.AWTGraphicsConfiguration;
import com.jogamp.opengl.*;

import java.awt.Canvas;

/**
 * @author hadyn
 */
public class OpenGlRenderer extends RendererToolkit {
  private int version;
  private String hardwareVendor;
  private String hardwareRenderer;

  public OpenGlRenderer(Canvas canvas) {
    GLProfile glProfile = GLProfile.get(GLProfile.GL4);
    GLCapabilities capabilities = new GLCapabilities(glProfile);

    GLDrawable drawable = GLDrawableFactory.getDesktopFactory()
      .createGLDrawable(NativeWindowFactory.getNativeWindow(canvas,
        AWTGraphicsConfiguration.create(canvas.getGraphicsConfiguration(),
          capabilities, capabilities)));
    drawable.setRealized(true);

    GLContext context = drawable.createContext(null);
    context.makeCurrent();
    GL4 gl = (GL4) context.getGL();

    String version = gl.glGetString(GL.GL_VERSION);
    String[] array = version.replace('.', ' ').split(" ");
    int majorVersion = Integer.parseInt(array[0]);
    int minorVersion = Integer.parseInt(array[1]);
    this.version = majorVersion * 10 + minorVersion;

    hardwareVendor = gl.glGetString(GL.GL_VENDOR).toLowerCase();
    hardwareRenderer = gl.glGetString(GL.GL_RENDERER).toLowerCase();
  }

  @Override public RendererInfo getInfo() {
    // TODO(hadyn)
    int vendor = -1;
    return new RendererInfo("OpenGL", version, vendor, hardwareRenderer);
  }
}
