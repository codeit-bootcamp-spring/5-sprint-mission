package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.AWSConfig;
import com.sprint.mission.discodeit.event.BinaryContentFailEvent;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3;
  private final AWSConfig config;
  private static final String PATH = "binary/";

  private final ApplicationEventPublisher publisher;

  public S3BinaryContentStorage(AWSConfig config, ApplicationEventPublisher publisher) {
    this.config = config;
    this.s3 = config.s3Client();
    this.publisher = publisher;
  }

  @Retryable(value = {S3Exception.class,
      SdkClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000L))
  @Override
  public UUID put(UUID id, byte[] data) {
    String key = PATH + id;

    s3.putObject(b -> b.bucket(config.getBucket())
                       .key(key), software.amazon.awssdk.core.sync.RequestBody.fromBytes(data));

    return id;
  }

  @Override
  public InputStream get(UUID id) {
    String key = PATH + id;

    return s3.getObject(b -> b.bucket(config.getBucket())
                              .key(key));
  }

  @Override
  public ResponseEntity<?> download(UUID id) {
    String key = PATH + id;

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

  @Recover
  public void recover(S3Exception e, UUID key, InputStream input) {
    log.error("[S3 업로드 실패] key: {}, error: {}", key, e.getMessage());
    publisher.publishEvent(BinaryContentFailEvent.builder()
                                                 .binaryContentId(key)
                                                 .exception(e)
                                                 .build());
  }
}
