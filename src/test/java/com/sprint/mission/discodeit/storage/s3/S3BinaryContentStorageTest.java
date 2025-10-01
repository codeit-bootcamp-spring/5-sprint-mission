package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.S3Config;
import com.sprint.mission.discodeit.domain.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.S3.AWSProperties;
import com.sprint.mission.discodeit.storage.S3.S3BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        S3BinaryContentStorage.class,
        AWSProperties.class,
        S3Config.class
})
@ActiveProfiles("test")
public class S3BinaryContentStorageTest {

    @Autowired
    private BinaryContentStorage storage;

    @Test
    @DisplayName("S3에 byte 데이터 업로드 및 다운로드")
    void uploadAndDownload_realS3() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        byte[] data = "Hello S3 World!".getBytes();

        // when - 업로드
        UUID savedId = storage.upload(id, data);
        assertThat(savedId).isEqualTo(id);

        // 2. 직접 가져오기
        InputStream is = storage.get(id);
        byte[] downloaded = is.readAllBytes();
        assertThat(downloaded).isEqualTo(data);

        // 3. Presigned URL 다운로드
        BinaryContentDto dto = BinaryContentDto.builder()
                .id(id)
                .fileName("test.txt")
                .size((long) data.length)
                .contentType("application/octet-stream")
                .build();

        ResponseEntity<?> response = storage.download(dto);
        assertThat(response.getStatusCode().is3xxRedirection()).isTrue();
        assertThat(response.getHeaders().getLocation().toString()).contains("https://");
    }

}
