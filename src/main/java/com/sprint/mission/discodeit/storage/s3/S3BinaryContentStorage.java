package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Properties s3Properties;
  private final S3Client s3Client;
  private final S3Presigner presigner;

  public S3BinaryContentStorage(S3Properties s3Properties) {
    this.s3Properties = s3Properties;
    this.s3Client = s3Properties.s3Client();
    this.presigner = s3Properties.presigner();
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(s3Properties.getBucket())
            .key(binaryContentId.toString())
            .build(),
        RequestBody.fromBytes(bytes)
    );
    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(binaryContentId.toString())
        .build();
    return s3Client.getObject(request);
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto metaData) {
    String key = metaData.id().toString();
    String contentType = metaData.contentType();
    String presignedUrl = generatePresignedUrl(key, contentType);
    return ResponseEntity.status(302)
        .header(HttpHeaders.LOCATION, presignedUrl)
        .build();
  }

  S3Client getS3Client() {
    return s3Client;
  }

  String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(key)
        .responseContentType(contentType)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .getObjectRequest(request)
        .signatureDuration(Duration.ofSeconds(s3Properties.getExpiration()))
        .build();
    PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(
        presignRequest);
    return presignedGetObjectRequest.url().toString();
  }
}
