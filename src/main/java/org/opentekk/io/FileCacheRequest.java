package org.opentekk.io;

/**
 * A request for a {@link FileCacheWorker}.
 *
 * @author hadyn
 */
public class FileCacheRequest extends AbstractFileRequest {
  public static final int TYPE_READ_IMMEDIATELY = 1;
  public static final int TYPE_WRITE = 2;
  public static final int TYPE_READ = 3;

  private int type;
  private FileCache fileCache;
  private byte[] bytes;

  public FileCacheRequest() {}

  void setType(int type) {
    this.type = type;
  }

  int getType() {
    return type;
  }

  public void setFileCache(FileCache fileCache) {
    this.fileCache = fileCache;
  }

  public FileCache getFileCache() {
    return fileCache;
  }

  void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  @Override public byte[] getBytes() {
    return bytes;
  }
}
