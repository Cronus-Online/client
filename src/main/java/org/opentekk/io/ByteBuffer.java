package org.opentekk.io;

/**
 * A byte buffer.
 *
 * @author hadyn
 */
public class ByteBuffer {
  byte[] bytes;
  int position;

  public ByteBuffer() {}

  public int getPosition() {
    return position;
  }

  public byte getByte(int position) {
    return bytes[position];
  }

  public byte[] getBytes() {
    return bytes;
  }

  public int getCapacity() {
    return bytes.length;
  }
}
