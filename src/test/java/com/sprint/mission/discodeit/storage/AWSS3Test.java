package com.sprint.mission.discodeit.storage;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.AWSConfig;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Disabled
@SpringBootTest
@ActiveProfiles("test")
class AWSS3Test {

  @Autowired
  private AWSConfig config;

  @Test
  @EnabledIf(expression = "#{environment['discodeit.storage.type'] == 's3'}", reason = "Only run when storage type is S3")
  void testUpload() {
    S3Client s3 = config.s3Client();
    String key = "test/" + UUID.randomUUID() + ".txt";
    PutObjectResponse response = s3.putObject(b -> b.bucket(config.getBucket())
                                                    .key(key),
        software.amazon.awssdk.core.sync.RequestBody.fromString("Hello S3!"));

    assertThat(response.eTag()).isNotEmpty();
  }

  @Test
  @EnabledIf(expression = "#{environment['discodeit.storage.type'] == 's3'}", reason = "Only run when storage type is S3")
  void testDonwload() throws Exception {
    S3Client s3 = config.s3Client();

    String key = "test/test.txt";

    InputStream is = s3.getObject(b -> b.bucket(config.getBucket())
                                        .key(key));

    String downloadText = new String(is.readAllBytes());

    assertThat(downloadText).isEqualTo("Hello S3!");
  }
}