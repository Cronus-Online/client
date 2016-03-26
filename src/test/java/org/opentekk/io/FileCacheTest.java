package org.opentekk.io;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * {@link FileCache} unit tests.
 *
 * @author hadyn
 */
@RunWith(MockitoJUnitRunner.class)
public class FileCacheTest {

  @Mock private BufferedFile mainFile;

  @Mock private BufferedFile indexFile;

  private ByteBuffer mainFileBuffer;
  private ByteBuffer indexFileBuffer;

  @Before
  public void setUp() throws IOException {
    mainFileBuffer = ByteBuffer.allocate(1_024);
    indexFileBuffer = ByteBuffer.allocate(1_024);
    mockFile(mainFile, mainFileBuffer);
    mockFile(indexFile, indexFileBuffer);
  }

  @Test public void testGet() throws IOException {
    byte[] fileData = new byte[] {
      3, 59, 69, 31, 42, 89, 90, 23
    };

    byte[] indexData = new byte[] {
      0, 0, (byte) fileData.length, // Size
      0, 0, 1,                      // Chunk
    };

    byte[] chunkHeader = new byte[] {
      0, 0,                         // File
      0, 0,                         // Part
      0, 0, 0,                      // Next chunk
      1                             // Index id
    };

    byte[] chunkData = new byte[chunkHeader.length + fileData.length];
    System.arraycopy(chunkHeader, 0, chunkData, 0, chunkHeader.length);
    System.arraycopy(fileData, 0, chunkData, 8, fileData.length);

    indexFileBuffer.put(indexData);
    indexFileBuffer.flip();

    mainFileBuffer.position(FileCache.CHUNK_SIZE);
    mainFileBuffer.put(chunkData);
    mainFileBuffer.flip();

    FileCache fileCache = new FileCache(1, mainFile, indexFile, 1_024);
    assertThat(fileCache.get(0)).isEqualTo(fileData);
  }

  private void mockFile(BufferedFile file, ByteBuffer buffer) throws IOException {
    doAnswer(invocation -> {
      Long position = invocation.getArgumentAt(0, Long.class);
      buffer.position(position.intValue());
      return null;
    }).when(file).seek(anyLong());

    doAnswer(invocation -> {
      byte[] dest = invocation.getArgumentAt(0, byte[].class);
      Integer offset = invocation.getArgumentAt(1, Integer.class);
      Integer length = invocation.getArgumentAt(2, Integer.class);
      buffer.get(dest, offset, length);
      return null;
    }).when(file).read(any(byte[].class), anyInt(), anyInt());

    when(file.length()).thenReturn((long) buffer.capacity());
  }
}
