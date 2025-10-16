package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.storage.s3.test.EnvLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class S3BinaryContentStorageTest {

  @Test
  void put_and_get_file() throws Exception {
    // 1. 테스트용 UUID와 바이트 준비 (리소스 폴더에서 샘플파일 읽기)
    UUID id = UUID.randomUUID();
    byte[] content = Files.readAllBytes(Paths.get("src/test/resources/sample1004.txt"));

    // 2. .env 파일에서 S3 연결정보 읽기
    Properties props = EnvLoader.load();
    String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    String region = props.getProperty("AWS_S3_REGION");
    String bucket = props.getProperty("AWS_S3_BUCKET");

    // 3. S3BinaryContentStorage 인스턴스 생성
    S3BinaryContentStorage storage = new S3BinaryContentStorage(
        accessKey, secretKey, region, bucket
    );

    // 4. put()으로 업로드
    storage.put(id, content);

    // 5. get()으로 다운로드해서 내용 비교
    var inputStream = storage.get(id);
    byte[] downloaded = inputStream.readAllBytes();

    assertThat(downloaded).isEqualTo(content);
  }
}
