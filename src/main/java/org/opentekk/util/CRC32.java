package org.opentekk.util;

/**
 * @author hadyn
 */
public class CRC32 {

  private static int[] table = new int[256];

  private CRC32() {}

  public static int get(byte[] bytes) {
    return get(bytes, 0, bytes.length);
  }

  public static int get(byte[] bytes, int off, int len) {
    int value = -1;
    for (int i_5_ = off; len + off > i_5_; i_5_++) {
      value = (value >>> 8 ^ table[(bytes[i_5_] ^ value) & 0xff]);
    }
    value ^= 0xffffffff;
    return value;
  }

  static {
    for (int i = 0; i < 256; i++) {
      int j = i;
      for (int i_19_ = 0; i_19_ < 8; i_19_++) {
        if ((0x1 & j) == 1) {
          j = j >>> 1 ^ 0xedb88320;
        } else {
          j >>>= 1;
        }
      }
      table[i] = j;
    }
  }
}
