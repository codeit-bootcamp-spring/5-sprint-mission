package com.sprint.mission.discodeit.storage;


import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final int presignedUrlExpirationSec;

  public S3BinaryContentStorage(String accessKey, String secretKey, String region,
      String bucket, int presignedUrlExpirationSec) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = Objects.requireNonNullElse(region, "ap-northeast-2");
    this.bucket = bucket;
    this.presignedUrlExpirationSec = presignedUrlExpirationSec <= 0 ? 600 : presignedUrlExpirationSec;
  }

  // ===== BinaryContentStorage =====

  @Override
  public UUID put(UUID id, byte[] bytes) {
    ensureBucket();
    String key = keyOf(id);

    PutObjectRequest req = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentLength((long) bytes.length)
        .build();

    s3().putObject(req, RequestBody.fromBytes(bytes));
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    String key = keyOf(id);
    ResponseBytes<GetObjectResponse> resp = s3().getObjectAsBytes(GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build());
    return new ByteArrayInputStream(resp.asByteArray());
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto dto) {
    String key = keyOf(dto.id());
    String url = generatePresignedUrl(key, dto.contentType(), dto.fileName());

    return ResponseEntity.status(302)
        .header(HttpHeaders.LOCATION, url)
        .build();
  }

  //헬퍼

  private String keyOf(UUID id) {
    // 필요 시 Prefix 전략 적용 가능
    return "binary/" + id;
  }

  private void ensureBucket() {
    // 존재/권한 확인 (없으면 404/403 발생)
    s3().headBucket(HeadBucketRequest.builder().bucket(bucket).build());
  }

  //다운로드용 GET PresignedUrl
  public String generatePresignedUrl(String key, String contentType, String downloadFileName) {
    GetObjectRequest get = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(contentType != null ? contentType : "application/octet-stream")
        .responseContentDisposition(contentDisposition(downloadFileName))
        .build();

    GetObjectPresignRequest preq = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(presignedUrlExpirationSec))
        .getObjectRequest(get)
        .build();

    PresignedGetObjectRequest presigned = presigner().presignGetObject(preq);
    return presigned.url().toString();
  }

  private String contentDisposition(String fileName) {
    if (fileName == null || fileName.isBlank()) return "attachment";
    String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    return "attachment; filename*=UTF-8''" + encoded;
  }

  private S3Client s3() {
    AwsCredentialsProvider cp = hasKeys()
        ? StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
        : DefaultCredentialsProvider.create();

    return S3Client.builder()
        .region(Region.of(region))
        .httpClientBuilder(UrlConnectionHttpClient.builder())
        .credentialsProvider(cp)
        .build();
  }

  private S3Presigner presigner() {
    S3Presigner.Builder b = S3Presigner.builder()
        .region(Region.of(region));
    if (hasKeys()) {
      b.credentialsProvider(StaticCredentialsProvider.create(
          AwsBasicCredentials.create(accessKey, secretKey)));
    } else {
      b.credentialsProvider(DefaultCredentialsProvider.create());
    }
    return b.build();
  }

  private boolean hasKeys() {
    return accessKey != null && !accessKey.isBlank()
        && secretKey != null && !secretKey.isBlank();
  }
}
