package org.opentekk.graphics;

/**
 * @author hadyn
 */
public abstract class RendererToolkit {

  /**
   * Gets the information about this renderer which includes its name, vendor, version.
   *
   * @return the info.
   */
  public abstract RendererInfo getInfo();
}
