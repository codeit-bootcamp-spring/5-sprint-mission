package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

final class S3TestFixtures {

    static final String TEST_CONTENT = "테스트 데이터";
    static final String TEST_CONTENT_ENGLISH = "test content";
    static final String ORIGINAL_CONTENT = "original content";
    static final String NEW_CONTENT = "new content";
    static final int LARGE_FILE_SIZE_MB = 1;
    static final int BYTES_PER_MB = 1024 * 1024;

    private S3TestFixtures() {
    }

    static byte[] createTestContent(String content) {
        return content.getBytes();
    }

    static byte[] createLargeContent() {
        byte[] content = new byte[LARGE_FILE_SIZE_MB * BYTES_PER_MB];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        return content;
    }

    static BinaryContentDto createBinaryContentDto(UUID id, String fileName, long size, String contentType) {
        return new BinaryContentDto(id, fileName, size, contentType);
    }

    static void assertStreamContentEquals(InputStream stream, byte[] expected) throws IOException {
        try (stream) {
            byte[] actual = stream.readAllBytes();
            assertThat(actual).isEqualTo(expected);
        }
    }
}
