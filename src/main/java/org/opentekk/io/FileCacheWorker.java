package org.opentekk.io;

import org.opentekk.sign.SignLink;
import org.opentekk.util.Queue;
import org.opentekk.util.ThreadUtils;

/**
 * Writes, and reads files asynchronously to and from file caches.
 *
 * @author hadyn
 */
public class FileCacheWorker implements Runnable {
  private Queue requests = new Queue();
  private Thread thread;
  private boolean shutdown;
  private int requestCount;

  public FileCacheWorker(SignLink signLink) {
    SignLink.Request request = signLink.createThread(this, Thread.NORM_PRIORITY);
    while (request.getStatus() == SignLink.Request.STATUS_WAITING) {
      ThreadUtils.sleep(10L);
    }
    thread = (Thread) request.getResult();
  }

  /**
   * Reads a file immediately from a cache. If there is a request to write a file to a cache then
   * that data is used in place of the data written to the cache.
   *
   * @param fileCache the file cache to read the file from.
   * @param id        the id of the file.
   * @return the completed request.
   */
  public FileCacheRequest readImmediately(FileCache fileCache, int id) {
    FileCacheRequest request = new FileCacheRequest();
    request.setType(FileCacheRequest.TYPE_READ_IMMEDIATELY);
    synchronized (requests) {
      for (FileCacheRequest compare = ((FileCacheRequest) requests.getFirst());
           compare != null; compare = ((FileCacheRequest) requests.getNext())) {
        if (compare.getSubNodeKey() == (long) id && compare.getFileCache() == fileCache
          && compare.getType() == FileCacheRequest.TYPE_WRITE) {
          request.setBytes(compare.getBytes());
          request.setComplete(true);
          return request;
        }
      }
    }
    request.setBytes(fileCache.get(id));
    request.setComplete(true);
    return request;
  }

  public FileCacheRequest read(FileCache fileCache, int id) {
    FileCacheRequest request = new FileCacheRequest();
    request.setType(FileCacheRequest.TYPE_READ);
    request.setFileCache(fileCache);
    request.setSubNodeKey((long) id);
    append(request);
    return request;
  }

  @Override public void run() {
    while (!shutdown) {
      FileCacheRequest request;
      synchronized (requests) {
        request = ((FileCacheRequest) requests.poll());
        if (request != null) {
          requestCount--;
        } else {
          try {
            requests.wait();
          } catch (InterruptedException interruptedexception) {
          }
          continue;
        }
      }

      if (request.getType() == FileCacheRequest.TYPE_READ) {
        request.setBytes(request.getFileCache().get((int) request.getSubNodeKey()));
      }

      // TODO(hadyn)
      if(request.getType() == FileCacheRequest.TYPE_WRITE) {

      }

      request.setComplete(true);
    }
  }

  private void append(FileCacheRequest request) {
    synchronized (requests) {
      requests.add(request);
      requestCount++;
      requests.notifyAll();
    }
  }
}
