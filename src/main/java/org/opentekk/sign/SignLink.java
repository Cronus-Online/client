package org.opentekk.sign;

/**
 * @author hadyn
 */
public class SignLink implements Runnable {

  private Thread thread;
  private String javaVendor;
  private String javaVersion;

  private Request head;
  private Request tail;

  public SignLink() {
    javaVendor = "Unknown";
    javaVersion = "1.1";

    try {
      javaVendor = System.getProperty("java.vendor");
      javaVersion = System.getProperty("java.version");
    } catch (Exception ex) {}

    thread = new Thread(this);
    thread.setPriority(10);
    thread.setDaemon(true);
    thread.start();
  }

  @Override public void run() {
    for(;;) {
      Request request;
      synchronized (this) {
        for (;;) {
          if (false) {
            return;
          }

          if (head != null) {
            request = head;
            head = head.next;
            if (head == null) {
              tail = null;
            }
            break;
          }

          try {
            wait();
          } catch (InterruptedException e) {
          }
        }
      }

      if(request.type == Request.CREATE_THREAD) {
        Runnable runnable = (Runnable) request.objArgument;
        int priority = request.intArgument;
        Thread thread = new Thread(runnable);
        // TODO(hadyn): This normally is false.
        thread.setDaemon(false);
        thread.start();
        thread.setPriority(priority);
        request.result = thread;
      } else {
        throw new RuntimeException("Unhandled request type: " + request.type + ".");
      }

      request.status = Request.STATUS_OK;
      synchronized (request) {
        request.notify();
      }
    }
  }

  public Request createThread(Runnable runnable, int priority) {
    return submit(Request.CREATE_THREAD, runnable, priority);
  }

  public Request submit(int requestType, Object objArgument, int intArgument) {
    Request request = new Request();
    request.type = requestType;
    request.objArgument = objArgument;
    request.intArgument = intArgument;

    synchronized (this) {
      if(tail == null) {
        head = tail = request;
      } else {
        tail.next = request;
        tail = request;
      }
      notify();
    }
    return request;
  }

  public String getJavaVendor() {
    return javaVendor;
  }

  public String getJavaVersion() {
    return javaVersion;
  }

  public static class Request {
    public static final int CREATE_THREAD = 0;

    public static final int STATUS_WAITING = 0;
    public static final int STATUS_OK = 1;

    Request next;
    int type;
    Object objArgument;
    int intArgument;
    int status = STATUS_WAITING;
    Object result;

    Request() {}

    public int getStatus() {
      return status;
    }

    public Object getResult() {
      return result;
    }
  }
}
