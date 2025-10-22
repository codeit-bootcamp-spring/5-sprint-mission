package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto; // DTO 경로 프로젝트에 맞게
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI; import java.net.URL;
import java.time.Duration; import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Props props;

  private StaticCredentialsProvider creds() {
    return StaticCredentialsProvider.create(
        AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
    );
  }

  private S3Client s3() {
    return S3Client.builder().credentialsProvider(creds())
        .region(Region.of(props.getRegion())).build();
  }

  private S3Presigner presigner() {
    return S3Presigner.builder().credentialsProvider(creds())
        .region(Region.of(props.getRegion())).build();
  }

  private String keyFrom(UUID id) {
    return id.toString();
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    s3().putObject(
        PutObjectRequest.builder()
            .bucket(props.getBucket())
            .key(keyFrom(id))
            .contentType("application/octet-stream")
            .build(),
        RequestBody.fromBytes(bytes)
    );
    return id;
  }

  @Override
  public java.io.InputStream get(UUID id) {
    throw new UnsupportedOperationException("Use download() with presigned URL.");
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto meta) {
    var getReq = GetObjectRequest.builder()
        .bucket(props.getBucket())
        .key(keyFrom(meta.id()))
        .responseContentDisposition(buildContentDisposition(guessFileName(meta))) // ← 수정
        .build();

    var preReq = GetObjectPresignRequest.builder()
        .getObjectRequest(getReq)
        .signatureDuration(Duration.ofSeconds(props.getPresignedUrlExpiration()))
        .build();

    URL url = presigner().presignGetObject(preReq).url();
    URI location = UriComponentsBuilder.fromUriString(url.toString()).build(true).toUri();
    return ResponseEntity.status(302).location(location).build();
  }

  //  DTO에 파일명 메서드가 없으면 기본 파일명 반환
  private static String guessFileName(Object meta) {
    String[] candidates = {
        "originalFilename", "getOriginalFilename",
        "filename", "getFilename",
        "fileName", "getFileName",
        "name", "getName"
    };
    for (String m : candidates) {
      try {
        var method = meta.getClass().getMethod(m);
        Object v = method.invoke(meta);
        if (v != null && !v.toString().isBlank()) {
          return v.toString();
        }
      } catch (NoSuchMethodException ignored) {
      } catch (Exception e) {
        // 예외
      }
    }
    try {
      var idVal = meta.getClass().getMethod("id").invoke(meta);
      return (idVal != null ? idVal.toString() : "download.bin");
    } catch (Exception e) {
      return "download.bin";
    }
  }

  // Content-Disposition 안전하게 생성 (한글/공백 대응)
  private static String buildContentDisposition(String filename) {
    if (filename == null || filename.isBlank())
      return "attachment";
    String safe = filename.replace("\"", "");
    String encoded = java.net.URLEncoder.encode(safe, java.nio.charset.StandardCharsets.UTF_8);
    return "attachment; filename=\"" + safe + "\"; filename*=UTF-8''" + encoded;
  }
}