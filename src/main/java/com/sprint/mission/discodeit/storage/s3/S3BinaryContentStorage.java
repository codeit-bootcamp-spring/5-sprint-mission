package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

  /* S3에 업로드/다운로드/PresignedUrl 생성
   * 구현 담당 클래스
   */

  /* put() → 파일을 AWS S3 버킷에 업로드
     get() → S3에서 파일을 다운로드해서 InputStream으로 반환
     download() → S3 파일에 접근할 수 있는 Presigned URL 생성해서 리다이렉트 응답
   */

  private final S3StorageProperties props; //설정값

  //네 개 값 받는 생성자
  public S3BinaryContentStorage(String accessKey, String secretKey, String region, String bucket) {
    // presignedUrlExpiration 기본값 600L(10분)로 세팅!
    S3StorageProperties props = new S3StorageProperties();
    props.setAccessKey(accessKey);
    props.setSecretKey(secretKey);
    props.setRegion(region);
    props.setBucket(bucket);
    props.setPresignedUrlExpiration(600L); // 기본 10분

    this.props = props;
  }

  /* Client 생성 메서드
   * AWS SDK에서 제공하는 builder
   * props에서 값 읽어서 인증 처리
   */
  private S3Client s3() {
    return S3Client.builder()
        .region(Region.of(props.getRegion()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    props.getAccessKey(), //액세스 키
                    props.getSecretKey() //시크릿 키
                )
            )
        ).build();
  }

  /* 파일업로드 메서드
   * @param id - UUID (파일 식별자)
   * @param bytes - 업로드할 파일 데이터
   * @return 업로드 완료된 파일의 UUID
   *  */
  @Override
  public UUID put(UUID id, byte[] bytes) {
    s3().putObject(
        PutObjectRequest.builder()
            .bucket(props.getBucket()) //저장될 버킷 이름
            .key(id.toString()) //파일명
            .build(),
        RequestBody.fromBytes(bytes)
    );
    return id;
  }

  /* 파일 다운로드 메서드
   * @param id - S3에서 가져올 파일의 UUID
   * @return 파일 내용을 담은 InputStream
   * */
  @Override
  public InputStream get(UUID id) {
    var resp = s3().getObject(
        GetObjectRequest.builder()
            .bucket(props.getBucket()) //조회할 버킷
            .key(id.toString()) // 조회할 파일 key
            .build()
    );
    return resp;
  }

  /* Presigned URL방식 다운로드 권장 메서드
   * 클라이언트가 S3에서 직접 다운로드하게끔 링크만 제공
   * */
  @Override
  public ResponseEntity<?> download(BinaryContentDto dto) {
    String presignedUrl = generatePresignedUrl(dto.getKey());
    return ResponseEntity.status(302)
        .header(HttpHeaders.LOCATION, presignedUrl)
        .build();
  }

  /* Presigned URL 생성 메서드
   *  @param key - S3 파일의 key (UUID 또는 파일명)
   *  @return presigned URL (일시적 접근 가능 링크)
   * */
  public String generatePresignedUrl(String key) {
    try (S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(props.getRegion()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    props.getAccessKey(),
                    props.getSecretKey()
                )
            )
        ).build()) {

      //S3에서 가져올 파일 정보 지정
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(props.getBucket())
          .key(key)
          .build();

      //접근 시간 설정
      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .getObjectRequest(getObjectRequest)
          .signatureDuration(java.time.Duration.ofSeconds(props.getPresignedUrlExpiration()))
          .build();

      //URL 생성
      PresignedGetObjectRequest presignedGetObjectRequest =
          presigner.presignGetObject(presignRequest);

      //S3 접근 가능한 URL 문자열 리턴
      return presignedGetObjectRequest.url().toString();
    }
  }

}