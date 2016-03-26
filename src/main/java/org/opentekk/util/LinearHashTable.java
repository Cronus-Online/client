package org.opentekk.util;

/**
 * @author hadyn
 */
public class LinearHashTable {

  private int[] buckets;

  public LinearHashTable(int[] keys) {

    // Calculate the length of the table.
    int length;
    for (length = 1; (keys.length >> 1) + keys.length >= length; length <<= 1) {}

    // Initialize each of the buckets.
    buckets = new int[length + length];
    for (int i = 0; i < length + length; i++) {
      buckets[i] = -1;
    }

    // Map each of the key value pairs out where the values are the indexes
    // in the key array, if a bucket is already filled with a key value pair
    // then just move to the next bucket until we find an empty bucket. This
    // starts from the center and moves forward.
    for (int value = 0; value < keys.length; value++) {
      int index;
      for (
        index = keys[value] & length - 1;
        buckets[index + index + 1] != -1; index = length - 1 & index + 1) {
      }
      buckets[index + index] = keys[value];
      buckets[index + index + 1] = value;
    }
  }

  public int get(int key) {
    int split = (buckets.length >> 1) - 1;
    int index = key & split;
    for (; ; ) {
      int value = buckets[index + index + 1];
      if (value == -1) {
        return -1;
      }
      if (buckets[index + index] == key) {
        return value;
      }
      index = split & index + 1;
    }
  }
}
