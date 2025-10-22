package com.sprint.mission.discodeit.stoarge.s3;

import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AWSS3Test {

  private static S3Client s3;
  private static S3Presigner presigner;
  private static String bucket;
  private static String key;

  @BeforeAll
  static void setUp() {
    Properties env = Env.loadDotEnv();
    Env.require(env, "AWS_S3_ACCESS_KEY", "AWS_S3_SECRET_KEY", "AWS_S3_REGION", "AWS_S3_BUCKET");

    AwsBasicCredentials creds = AwsBasicCredentials.create(
        env.getProperty("AWS_S3_ACCESS_KEY"),
        env.getProperty("AWS_S3_SECRET_KEY")
    );
    Region region = Region.of(env.getProperty("AWS_S3_REGION"));
    bucket = env.getProperty("AWS_S3_BUCKET");

    s3 = S3Client.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(creds))
        .serviceConfiguration(S3Configuration.builder()
            .checksumValidationEnabled(false) // 환경에 따라 유용
            .build())
        .build();

    presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(creds))
        .build();

    key = "test/discodeit/" + UUID.randomUUID() + ".txt";
  }

  @AfterAll
  static void tearDown() {
    if (s3 != null) {
      try { s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build()); }
      catch (S3Exception ignored) {}
      s3.close();
    }
    if (presigner != null) presigner.close();
  }

  @Test
  @Order(1)
  @DisplayName("업로드")
  void upload() {
    String content = "hello, discodeit s3 test! 업로드 OK";
    PutObjectRequest req = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType("text/plain; charset=utf-8")
        .build();

    PutObjectResponse res = s3.putObject(req, RequestBody.fromString(content, StandardCharsets.UTF_8));
    SdkHttpResponse http = res.sdkHttpResponse();
    assertTrue(http.isSuccessful(), "업로드 실패: " + http.statusCode());
  }

  @Test
  @Order(2)
  @DisplayName("다운로드")
  void download() throws Exception {
    try (ResponseInputStream<GetObjectResponse> in =
        s3.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build())) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      in.transferTo(bos);
      String body = bos.toString(StandardCharsets.UTF_8);
      assertTrue(body.contains("discodeit s3 test"), "다운로드 내용 불일치");
    }
  }

  @Test
  @Order(3)
  @DisplayName("Presigned URL 생성")
  void presignedUrl() {
    GetObjectRequest getReq = GetObjectRequest.builder()
        .bucket(bucket).key(key).build();

    GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
        .getObjectRequest(getReq)
        .signatureDuration(Duration.ofMinutes(10))
        .build();

    PresignedGetObjectRequest pre = presigner.presignGetObject(preReq);
    String url = pre.url().toString();

    assertNotNull(url);
    assertTrue(url.contains("X-Amz-"), "Presigned URL 형식 아님: " + url);

    // 객체 존재 여부 간접 확인(HEAD)
    HeadObjectResponse head = s3.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
    assertTrue(head.sdkHttpResponse().isSuccessful(), "HEAD 실패");
    System.out.println("Presigned URL: " + url);
  }
}
