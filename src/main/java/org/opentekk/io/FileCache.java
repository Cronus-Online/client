package org.opentekk.io;

import java.io.IOException;

/**
 * A file cache. A file cache is a custom file format used by Jagex to store files in a manner
 * where the expected size of the file is to grow over time.
 * </p>
 * This class is <b>not</b> thread safe.
 *
 * @author hadyn
 */
public class FileCache {

  public static final int CHUNK_SIZE = 520;

  /**
   * Byte buffer used for writing and reading files from the cache.
   */
  private static final byte[] buffer = new byte[CHUNK_SIZE];

  private int id;
  private BufferedFile mainFile;
  private BufferedFile indexFile;
  private int maximumSize;

  public FileCache(int id, BufferedFile mainFile, BufferedFile indexFile, int maximumSize) {
    this.id = id;
    this.mainFile = mainFile;
    this.indexFile = indexFile;
    this.maximumSize = maximumSize;
  }

  /**
   * Gets a file from the cache.
   *
   * @param file the id of the file.
   * @return the bytes of the file or {@code null} if there was an issue while trying to fetch
   * the file.
   */
  public byte[] get(int file) {
    try {
      if (mainFile.length() < (long) (6 * file + 6)) {
        return null;
      }
      indexFile.seek((long) (6 * file));
      indexFile.read(buffer, 0, 6);
      int size = ((buffer[0] & 0xff) << 16) | ((buffer[1] & 0xff) << 8) | (buffer[2] & 0xff);
      int chunk = ((buffer[3] & 0xff) << 16) | ((buffer[4] & 0xff) << 8) | (buffer[5] & 0xff);
      if (size < 0 || size > maximumSize) {
        return null;
      }
      if (chunk <= 0 || (long) chunk > mainFile.length() / 520L) {
        return null;
      }
      byte[] is = new byte[size];
      int off = 0;
      int part = 0;
      while (off < size) {

        // Chunk `0` is reserved for end of file.
        if (chunk == 0) {
          return null;
        }

        mainFile.seek((long) (chunk * 520));

        int read = size - off;
        if (read > 512) {
          read = 512;
        }

        mainFile.read(buffer, 0, read + 8);

        int fileCheck = ((buffer[0] & 0xff) << 8) | (buffer[1] & 0xff);
        int partCheck = ((buffer[2] & 0xff) << 8) | (buffer[3] & 0xff);
        int nextChunk = ((buffer[4] & 0xff) << 16) | ((buffer[5] & 0xff) << 8) | (buffer[6] & 0xff);
        int idCheck = buffer[7] & 0xff;
        if (file != fileCheck || part != partCheck || id != idCheck) {
          return null;
        }

        if (nextChunk < 0 || ((long) nextChunk > mainFile.length() / 520L)) {
          return null;
        }

        chunk = nextChunk;
        part++;

        for (int i = 0; i < read; i++) {
          is[off++] = buffer[i + 8];
        }
      }
      return is;
    } catch (IOException ex) {
      return null;
    }
  }
}
