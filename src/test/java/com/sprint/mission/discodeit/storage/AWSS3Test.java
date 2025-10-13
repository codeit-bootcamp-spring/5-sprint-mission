package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.AWSConfig;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
@ActiveProfiles("test")
class AWSS3Test {

  @Autowired
  private AWSConfig config;

  @Test
  void testUpload() {
    S3Client s3 = config.s3Client();
    String key = "test/" + UUID.randomUUID() + ".txt";
    s3.putObject(b -> b.bucket(config.getBucket())
                       .key(key),
        software.amazon.awssdk.core.sync.RequestBody.fromString("Hello S3!"));
    System.out.println("✅ 업로드 성공: " + key);
  }

  @Test
  void testDonwload() throws Exception {
    S3Client s3 = config.s3Client();

    String key = "test/test.txt";

    InputStream is = s3.getObject(b -> b.bucket(config.getBucket())
                                        .key(key));

    System.out.println(is.readAllBytes()
                         .toString());
    System.out.println("✅ 다운로드 성공: " + key);
  }
}