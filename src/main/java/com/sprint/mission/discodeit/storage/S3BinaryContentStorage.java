package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.AWSConfig;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3", matchIfMissing = true)
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3;
  private final AWSConfig config;

  public S3BinaryContentStorage(AWSConfig config) {
    this.config = config;
    this.s3 = config.s3Client();
  }

  @Override
  public UUID put(UUID id, byte[] data) {
    String key = "binary/" + id;

    s3.putObject(b -> b.bucket(config.getBucket())
                       .key(key), software.amazon.awssdk.core.sync.RequestBody.fromBytes(data));

    return id;
  }

  @Override
  public InputStream get(UUID id) {
    String key = "binary/" + id;

    return s3.getObject(b -> b.bucket(config.getBucket())
                              .key(key));
  }

  @Override
  public ResponseEntity<?> download(UUID id) {
    String key = "binary/" + id;

    S3Presigner presigner = S3Presigner.builder()
                                       .region(Region.of(config.getRegion()))
                                       .credentialsProvider(StaticCredentialsProvider.create(
                                           AwsBasicCredentials.create(config.getAccessKey(),
                                               config.getSecretKey())))
                                       .build();

    GetObjectRequest request = GetObjectRequest.builder()
                                               .bucket(config.getBucket())
                                               .key(key)
                                               .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                                                                    .signatureDuration(
                                                                        Duration.ofSeconds(
                                                                            config.getPresignedUrlExpiration()))
                                                                    .getObjectRequest(request)
                                                                    .build();

    return ResponseEntity.status(HttpStatus.FOUND)
                         .location(URI.create(presigner.presignGetObject(presignRequest)
                                                       .url()
                                                       .toString()))
                         .build();
  }
}
