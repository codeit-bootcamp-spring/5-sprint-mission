package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public final class S3TestFixtures {

    public static final String TEST_CONTENT = "테스트 데이터";
    public static final String TEST_CONTENT_ENGLISH = "test content";
    public static final String ORIGINAL_CONTENT = "original content";
    public static final String NEW_CONTENT = "new content";

    private static final int LARGE_FILE_SIZE_MB = 1;
    private static final int BYTES_PER_MB = 1024 * 1024;

    private S3TestFixtures() {
        throw new AssertionError("Utility class");
    }

    public static byte[] createTestContent(String content) {
        return content.getBytes();
    }

    public static byte[] createLargeContent() {
        byte[] content = new byte[LARGE_FILE_SIZE_MB * BYTES_PER_MB];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        return content;
    }

    public static BinaryContentDto createBinaryContentDto(
        UUID binaryContentId,
        String fileName,
        long size,
        String contentType
    ) {
        return new BinaryContentDto(binaryContentId, fileName, size, contentType);
    }

    public static void assertStreamContentEquals(
        InputStream inputStream,
        byte[] expectedBytes
    ) throws IOException {
        try (inputStream) {
            byte[] actual = inputStream.readAllBytes();
            assertThat(actual).isEqualTo(expectedBytes);
        }
    }
}
