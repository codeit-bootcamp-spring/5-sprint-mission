package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.configuration.AWSProperties;
import java.net.URL;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3PresignService {

  private final S3Presigner presigner;
  private final AWSProperties awsProperties;
  private final long expiration = awsProperties.getExpiration();

  public URL createGetPresignedUrl(String key, String bucket, @Nullable String filename) {
    GetObjectRequest.Builder get = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key);

    if (filename != null && !filename.isBlank()) {
      get = get.responseContentDisposition(
          "attachment; filename=\"" + filename + "\""
      );
    }

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(expiration)) // ex) Duration.ofMinutes(10)
        .getObjectRequest(get.build())
        .build();

    PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);

    return presigned.url();
  }

  public URL createPutPresignedUrl(String bucket, String key, String contentType, Duration ttl) {
    PutObjectRequest put = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType(contentType)
        .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(expiration))
        .putObjectRequest(put)
        .build();

    PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);
    return presigned.url();
  }


  private String extractFileName(String key) {
    int index = key.lastIndexOf('/');
    return (index >= 0) ? key.substring(index + 1) : key;
  }
}
