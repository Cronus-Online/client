package org.opentekk.io;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A file on disk.
 *
 * @author hadyn
 */
public class FileOnDisk {

  private File file;
  private long maximumSize;
  private RandomAccessFile randomAccessFile;
  private long position;

  public FileOnDisk(File file, String access, long maximumSize) throws IOException {
    if(maximumSize == 0L) {
      maximumSize = Long.MAX_VALUE;
    }

    if(maximumSize < file.length()) {
      file.delete();
    }

    this.file = file;
    this.maximumSize = maximumSize;
    randomAccessFile = new RandomAccessFile(file, access);
    position = 0L;

    int read = randomAccessFile.read();
    if (read != -1 && !access.equals("r")) {
      randomAccessFile.seek(0L);
      randomAccessFile.write(read);
    }
    randomAccessFile.seek(0L);
  }

  public void seek(long position) throws IOException {
    randomAccessFile.seek(position);
    this.position = position;
  }

  public int read(byte[] dest, int off, int len) throws IOException {
    int read = randomAccessFile.read(dest, off, len);
    if(read > 0) {
      position += (long) read;
    }
    return read;
  }

  public void write(byte[] src, int off, int len) throws IOException {
    if(maximumSize < position + len) {
      randomAccessFile.seek(len + 1L);
      randomAccessFile.write(1);
      throw new EOFException();
    }
    randomAccessFile.write(src, off, len);
    position += (long) len;
  }

  public long length() throws IOException {
    return randomAccessFile.length();
  }

  public File getFile() {
    return file;
  }

  public void close() throws IOException {
    if (randomAccessFile != null) {
      randomAccessFile.close();
      randomAccessFile = null;
    }
  }
}
