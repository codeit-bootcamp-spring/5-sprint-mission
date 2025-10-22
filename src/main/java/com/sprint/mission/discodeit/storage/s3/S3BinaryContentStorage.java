package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final long presignedExpireSeconds;

  private S3Client newS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  private S3Presigner newPresigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  @Override
  public UUID put(UUID key, byte[] bytes) {
    if (key == null) key = UUID.randomUUID();
    try (S3Client s3 = newS3Client()) {
      PutObjectRequest req = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key.toString())
          .contentLength((long) bytes.length)
          .build();
      s3.putObject(req, RequestBody.fromBytes(bytes));
    }
    return key;
  }

  @Override
  public InputStream get(UUID key) {
    S3Client s3 = newS3Client();
    GetObjectRequest req = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key.toString())
        .build();
    ResponseInputStream<?> res = s3.getObject(req);
    // 호출 측에서 close 하도록 그대로 반환
    return res;
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto dto) {
    // DTO에 contentType이 있으면 활용
    String contentType = dto.getContentType() != null ? dto.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

    try (S3Presigner presigner = newPresigner()) {
      GetObjectRequest get = GetObjectRequest.builder()
          .bucket(bucket)
          .key(dto.getId().toString())
          .responseContentType(contentType)
          .responseContentDisposition("attachment; filename=\"" + dto.getFilename() + "\"")
          .build();

      GetObjectPresignRequest presign = GetObjectPresignRequest.builder()
          .getObjectRequest(get)
          .signatureDuration(Duration.ofSeconds(presignedExpireSeconds))
          .build();

      PresignedGetObjectRequest pre = presigner.presignGetObject(presign);

      return ResponseEntity.ok()
          .header(HttpHeaders.LOCATION, pre.url().toExternalForm())
          .build();
    }
  }
}



