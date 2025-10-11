package com.sprint.mission.discodeit.stoarge.s3;

import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSS3Test {

  // ------- env/properties -------
  static Properties env;
  static String region;
  static String bucket;
  static String accessKey;
  static String secretKey;
  static String endpoint; // optional (e.g., Localstack)

  // ------- sdk clients -------
  static S3Client s3;
  static S3Presigner presigner;

  // 공유 키(테스트 간 재사용)
  static String testKey;

  @BeforeAll
  static void setup() throws Exception {
    env = loadDotEnv();

    region    = get("AWS_REGION");
    bucket    = get("S3_BUCKET");
    accessKey = get("AWS_ACCESS_KEY_ID");
    secretKey = get("AWS_SECRET_ACCESS_KEY");
    endpoint  = env.getProperty("S3_ENDPOINT", "").trim();

    // 필수값 없으면 전체 테스트 스킵
    assumeTrue(notBlank(region) && notBlank(bucket) && notBlank(accessKey) && notBlank(secretKey),
        "Set AWS_REGION, S3_BUCKET, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY in .env or environment");

    var reg   = Region.of(region);
    var creds = StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKey, secretKey)
    );

    // --- Builder 타입을 변수로 잡지 않고 체이닝으로 생성 (Builder 인식 문제 회피) ---
    if (endpoint.isEmpty()) {
      s3 = S3Client.builder()
          .region(reg)
          .credentialsProvider(creds)
          .build();

      presigner = S3Presigner.builder()
          .region(reg)
          .credentialsProvider(creds)
          .build();
    } else {
      var uri = URI.create(endpoint);
      s3 = S3Client.builder()
          .region(reg)
          .credentialsProvider(creds)
          .endpointOverride(uri)
          .build();

      presigner = S3Presigner.builder()
          .region(reg)
          .credentialsProvider(creds)
          .endpointOverride(uri)
          .build();
    }

    // 버킷 접근 가능 여부(존재/권한) 확인
    s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());

    testKey = "mission-test/" + UUID.randomUUID() + ".txt";
  }

  @AfterAll
  static void teardown() {
    if (presigner != null) presigner.close();
    if (s3 != null) s3.close();
  }

  @Test
  @Order(1)
  void upload() {
    var content = "hello, S3! key=" + testKey;
    var put = PutObjectRequest.builder()
        .bucket(bucket)
        .key(testKey)
        .contentType("text/plain; charset=utf-8")
        .contentLength((long) content.getBytes(StandardCharsets.UTF_8).length)
        .build();

    s3.putObject(put, RequestBody.fromString(content, StandardCharsets.UTF_8));

    HeadObjectResponse head = s3.headObject(b -> b.bucket(bucket).key(testKey));
    assertNotNull(head);
    assertTrue(head.contentLength() > 0);
    System.out.println("[UPLOAD OK] key=" + testKey + ", size=" + head.contentLength());
  }

  @Test
  @Order(2)
  void download() {
    ResponseBytes<GetObjectResponse> bytes =
        s3.getObjectAsBytes(b -> b.bucket(bucket).key(testKey));

    String body = bytes.asString(StandardCharsets.UTF_8);
    assertTrue(body.contains("hello, S3!"));
    System.out.println("[DOWNLOAD OK] length=" + bytes.asByteArray().length);
  }

  @Test
  @Order(3)
  void presignedUrl() throws Exception {
    var get = GetObjectRequest.builder()
        .bucket(bucket)
        .key(testKey)
        .build();

    var preq = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(get)
        .build();

    PresignedGetObjectRequest presigned = presigner.presignGetObject(preq);

    assertNotNull(presigned);
    String url = presigned.url().toString();
    assertTrue(url.contains(bucket));
    System.out.println("[PRESIGNED OK] " + url);

    // (선택) URL을 파일로 저장해 수동 확인
    Path out = Paths.get("build/presigned-url.txt");
    Files.createDirectories(out.getParent());
    Files.writeString(out, url, StandardCharsets.UTF_8);
  }

  // -------- helpers --------

  /** 프로젝트 루트의 .env + OS 환경변수 병합 로더 */
  private static Properties loadDotEnv() throws Exception {
    Properties p = new Properties();

    // 1) OS 환경변수(AWS_/S3_ 접두) 우선 주입
    System.getenv().forEach((k, v) -> {
      if (k.startsWith("AWS_") || k.startsWith("S3_")) {
        p.setProperty(k, v);
      }
    });

    // 2) 프로젝트 루트 .env 병합(있으면)
    Path envPath = Paths.get(System.getProperty("user.dir"), ".env");
    if (Files.exists(envPath)) {
      try (var fis = new FileInputStream(envPath.toFile())) {
        // key=value 포맷은 Properties로 곧장 로드 가능
        p.load(fis);
      }
    }
    return p;
  }

  private static String get(String key) {
    String v = env.getProperty(key);
    if (v == null || v.trim().isEmpty()) {
      v = System.getenv(key);
    }
    return v == null ? "" : v.trim();
  }

  private static boolean notBlank(String s) {
    return s != null && !s.trim().isEmpty();
  }
}