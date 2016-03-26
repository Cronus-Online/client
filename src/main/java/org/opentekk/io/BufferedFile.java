package org.opentekk.io;

import org.opentekk.util.ArrayUtils;

import java.io.EOFException;
import java.io.IOException;

/**
 * @author hadyn
 */
public class BufferedFile {
  private FileOnDisk file;
  private long readLength;
  private long writeLength;
  private long currentPosition;
  private long readPosition = -1L;
  private long writePosition = -1L;
  private int readBufferLen;
  private int writeBufferLen;
  private long position;
  private byte[] writeBuffer;
  private byte[] readBuffer;

  public BufferedFile(FileOnDisk file, int writeBuffLen, int readBufferLen) throws IOException {
    this.file = file;
    readLength = writeLength = file.length();
    position = 0L;
    writeBuffer = new byte[writeBuffLen];
    readBuffer = new byte[readBufferLen];
  }

  public void seek(long position) throws IOException {
    if (position < 0L) {
      throw new IOException("Invalid seek to " + position + " in file " + file.getFile());
    }
    this.position = position;
  }

  public void write(byte[] src, int off, int len) throws IOException {
    try {
      // Update the readable file length.
      if (position + (long) len > writeLength) {
        readLength = position + (long) len;
      }

      // If we are over writing a write buffered section of the file then flush the buffer before
      // we continue.
      if (writePosition != -1L && (position < writePosition
        || position > (long) writeBufferLen + writePosition)) {
        flushWriteBuffer();
      }

      if (writePosition != -1L
        && (long) writeBuffer.length + writePosition < position + (long) len) {
        int i = (int) (writePosition + (long) writeBuffer.length - position);
        len -= i;
        ArrayUtils.copy(src, off, writeBuffer, (int) (position - writePosition), i);
        position += (long) i;
        writeBufferLen = writeBuffer.length;
        off += i;
        flushWriteBuffer();
      }

      // If there isn't enough space to buffer the write directory write the data to the file.
      if (writeBuffer.length < len) {
        if (position != currentPosition) {
          file.seek(position);
          currentPosition = position;
        }
        file.write(src, off, len);

        // Attempt to buffer the write.
        long lowerPos = -1L;
        if (position < readPosition || position >= readPosition + (long) readBufferLen) {
          if (readPosition >= position && readPosition < (long) len + position) {
            lowerPos = readPosition;
          }
        } else {
          lowerPos = position;
        }

        currentPosition += (long) len;
        if (writeLength < currentPosition) {
          writeLength = currentPosition;
        }

        long upperPos = -1L;
        if (readPosition > (long) len + position
          && (long) len + position <= (long) readBufferLen + readPosition) {
          upperPos = (long) len + position;
        } else if (position < readPosition + (long) readBufferLen
          && readPosition + (long) readBufferLen <= (long) len + position) {
          upperPos = readPosition + (long) readBufferLen;
        }

        if (lowerPos > -1L && upperPos > lowerPos) {
          int i = (int) (upperPos - lowerPos);
          ArrayUtils.copy(src, (int) (((long) off + lowerPos) - position), readBuffer,
            (int) (lowerPos + -readPosition), i);
        }
        position += (long) len;
      } else if (len > 0) {
        if (writePosition == -1L) {
          writePosition = position;
        }
        ArrayUtils.copy(src, off, writeBuffer, (int) (position - writePosition), len);
        position += (long) len;
        if (position - writePosition > (long) writeBufferLen) {
          writeBufferLen = (int) (position - writePosition);
        }
      }
    } catch (IOException ex) {
      currentPosition = -1L;
      throw ex;
    }
  }

  public void read(byte[] dest, int off, int len)
    throws IOException, ArrayIndexOutOfBoundsException {

    try {
      if (off + len > dest.length) {
        throw new ArrayIndexOutOfBoundsException(off + len - dest.length);
      }

      // Check to see if we already have the bytes write buffered so we can just copy them over.
      if (writePosition != -1L && position >= writePosition
        && (long) writeBufferLen + writePosition >= position + (long) len) {
        ArrayUtils.copy(writeBuffer, (int) (position - writePosition), dest, off, len);
        position += (long) len;
        return;
      }

      long pos = position;
      int readOff = off;
      int readLen = len;

      // Check to see if we already have bytes read buffered so we can just copy them over.
      if (position >= readPosition && position < readPosition + (long) readBufferLen) {
        int read = (int) ((long) readBufferLen + readPosition - position);
        if (read > len) {
          read = len;
        }
        ArrayUtils.copy(readBuffer, (int) (position - readPosition), dest, off, read);
        len -= read;
        position += (long) read;
        off += read;
      }

      // If we room in the read buffer fill it and copy data from it,
      // if we don't just read directly from the file until the end
      // of the file
      if (readBuffer.length >= len) {
        if (len > 0) {
          fillReadBuffer();
          int read = len;
          if (read > readBufferLen) {
            read = readBufferLen;
          }
          ArrayUtils.copy(readBuffer, 0, dest, off, read);
          off += read;
          position += (long) read;
          len -= read;
        }
      } else {
        file.seek(position);
        currentPosition = position;
        while (len > 0) {
          int read = file.read(dest, off, len);
          if (read == -1) {
            break;
          }
          currentPosition += (long) read;
          len -= read;
          position += (long) read;
          off += read;
        }
      }


      if (writePosition != -1L) {

        // Fill the buffer with zeros to the ending offset where the last
        // write occurred, this is under the assumption we reached the
        // end of the file.
        if (position < writePosition && len > 0) {
          int fillOff = off + (int) (writePosition - position);
          if (fillOff > off + len) {
            fillOff = len + off;
          }
          while (fillOff > off) {
            len--;
            dest[off++] = (byte) 0;
            position++;
          }
        }

        // Copy the chunk of the write buffer that corresponds to the read region.
        long upperPos = -1L;
        if (pos < (long) writeBufferLen + writePosition
          && writePosition + (long) writeBufferLen <= (long) readLen + pos) {
          upperPos = writePosition + (long) writeBufferLen;
        } else if (((pos + (long) readLen) > (writePosition))
          && pos + (long) readLen >= (long) writeBufferLen + writePosition) {
          upperPos = pos + (long) readLen;
        }

        long lowerPos = -1L;
        if (writePosition >= pos && writePosition < pos + (long) readLen) {
          lowerPos = writePosition;
        } else if (pos >= writePosition && pos < writePosition + (long) writeBufferLen) {
          lowerPos = pos;
        }

        if (lowerPos > -1L && upperPos > lowerPos) {
          int diff = (int) (upperPos - lowerPos);
          ArrayUtils.copy(writeBuffer, (int) (lowerPos - writePosition), dest,
            (int) (lowerPos - pos) + readOff, diff);
          if (position < upperPos) {
            len -= upperPos - position;
            position = upperPos;
          }
        }
      }
    } catch (IOException ex) {
      currentPosition = -1L;
      throw ex;
    }

    if (len > 0) {
      throw new EOFException();
    }
  }

  public long length() {
    return readLength;
  }

  private void flushWriteBuffer() throws IOException {
    if (writePosition != -1L) {
      if (writePosition != currentPosition) {
        file.seek(writePosition);
        currentPosition = writePosition;
      }
      file.write(writeBuffer, 0, writeBufferLen);
      currentPosition += (long) writeBufferLen;
      if (writeLength < currentPosition) {
        writeLength = currentPosition;
      }

      long upperPos = -1L;
      if (writePosition >= readPosition && writePosition < readPosition + (long) readBufferLen) {
        upperPos = writePosition;
      } else if (readPosition >= writePosition
        && writePosition + (long) writeBufferLen > readPosition) {
        upperPos = readPosition;
      }

      long lowerPos = -1L;
      if (writePosition + (long) writeBufferLen <= readPosition
        || readPosition + (long) readBufferLen < (long) writeBufferLen + writePosition) {
        if (writePosition < (long) readBufferLen + readPosition
          && (long) readBufferLen + readPosition <= (long) writeBufferLen + writePosition) {
          lowerPos = (long) readBufferLen + readPosition;
        }
      } else {
        lowerPos = writePosition + (long) writeBufferLen;
      }

      if (upperPos > -1L && upperPos < lowerPos) {
        int len = (int) (lowerPos - upperPos);
        ArrayUtils.copy(writeBuffer, (int) (upperPos - writePosition), readBuffer,
          (int) (upperPos - readPosition), len);
      }
      writePosition = -1L;
      writeBufferLen = 0;
    }
  }

  private void fillReadBuffer() throws IOException {
    readBufferLen = 0;
    if (currentPosition != position) {
      file.seek(position);
      currentPosition = position;
    }
    readPosition = position;
    while (readBufferLen < readBuffer.length) {
      int len = readBuffer.length - readBufferLen;
      if (len > 200000000) {
        len = 200000000;
      }
      int read = file.read(readBuffer, readBufferLen, len);
      if (read == -1) {
        break;
      }
      readBufferLen += read;
      currentPosition += (long) read;
    }
  }
}

