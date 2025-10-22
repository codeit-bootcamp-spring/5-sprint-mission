package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.storage.s3.test.EnvLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class S3BinaryContentStorageTest {

  @Test
  @DisplayName("S3 파일 업로드 및 다운로드 테스트")
  void put_and_get_file() throws Exception {

    //1. given: 테스트에 필요한 파일, 환경설정, 스토리지 객체 준비(리소스 폴더에서 샘플파일 읽기)
    UUID id = UUID.randomUUID();
    byte[] content = Files.readAllBytes(Paths.get("src/test/resources/sample1004.txt"));

    //2. .env 파일에서 S3 연결정보 읽기
    Properties props = EnvLoader.load();
    String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    String region = props.getProperty("AWS_S3_REGION");
    String bucket = props.getProperty("AWS_S3_BUCKET");

    //3. S3BinaryContentStorage 인스턴스 생성
    S3BinaryContentStorage storage = new S3BinaryContentStorage(
        accessKey, secretKey, region, bucket
    );

    //4. when: 파일 업로드 후 다운로드 수행
    storage.put(id, content);

    //5. get()으로 다운로드해서 내용 비교
    var inputStream = storage.get(id);
    byte[] downloaded = inputStream.readAllBytes();

    //6.then: 업로드된 파일과 다운로드된 파일이 동일한지 검증
    assertThat(downloaded).isEqualTo(content);
  }


  /* presigned URL 접근 시 key 이름을 정확히 맞춰야 함.
   * 테스트에선 랜덤 UUID로 저장되어 링크 접속시 안나옴
   * 하지만 AWS S3 버킷에서는 나옴
   * */
  @Test
  @DisplayName("파일 URL 접근 테스트")
  void generate_presigned_url() throws Exception {
    // 1. given: S3 연결정보와 스토리지 객체 준비
    Properties props = EnvLoader.load();
    String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    String region = props.getProperty("AWS_S3_REGION");
    String bucket = props.getProperty("AWS_S3_BUCKET");

    // 2. 인스턴스 생성
    S3BinaryContentStorage storage = new S3BinaryContentStorage(
        accessKey, secretKey, region, bucket
    );

    // 3. 미리 업로드된 파일 key 사용 (예: "sample1004.txt" 또는 실제 S3에 있는 key)
    String key = "sample1004.txt"; // 실제 있는 파일 이름/UUID

    // 4. when: presigned URL 생성 요청
    String url = storage.generatePresignedUrl(key);

    // 5. then: 생성된 URL 확인 (콘솔 출력)
    System.out.println("Presigned URL: " + url);

  }
}
