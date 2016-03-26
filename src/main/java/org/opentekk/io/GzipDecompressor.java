package org.opentekk.io;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * @author hadyn
 */
public class GzipDecompressor {
  private Inflater inflater;

  public GzipDecompressor() {
  }

  /**
   * Decompresses a stream of bytes.
   *
   * @param src  the source buffer.
   * @param dest the destination buffer.
   */
  public void decompress(ByteBuffer src, byte[] dest) {
    if (src.getByte(src.getPosition()) != 31 || src.getByte(src.getPosition() + 1) != -117) {
      throw new RuntimeException("Invalid GZIP header.");
    }

    if (inflater == null) {
      inflater = new Inflater(true);
    }

    try {
      inflater.setInput(src.getBytes(), src.getPosition() + 10,
        src.getCapacity() - src.getPosition() - 10 - 8);
      inflater.inflate(dest);
    } catch (DataFormatException ex) {
      throw new RuntimeException("Invalid GZIP compressed data.");
    } finally {
      inflater.reset();
    }
  }
}
