package org.opentekk.io;

/**
 * @author hadyn
 */
public class FileContainer {
  private static GzipDecompressor gzipDecompressor = new GzipDecompressor();

  private FileContainer() {}

  public static byte[] unpack(byte[] src) {
    ByteBuffer buffer = new ByteBuffer();
    return null;
  }
}
