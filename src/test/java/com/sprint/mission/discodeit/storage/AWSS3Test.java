package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.storage.s3.S3Properties;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@SpringBootTest
public class AWSS3Test {

  @Autowired
  private S3Properties s3Properties;

  @Test
  void testUpload() {
    S3Client s3 = s3Properties.s3Client();
    String key = "test/" + UUID.randomUUID() + ".txt";
    s3.putObject(b -> b.bucket(s3Properties.getBucket())
            .key(key),
        software.amazon.awssdk.core.sync.RequestBody.fromString("Hello S3!"));
    System.out.println("업로드 성공: " + key);
  }


  @Test
  void testDonwload() throws Exception {
    S3Client s3 = s3Properties.s3Client();

    String key = "test/test.txt";

    InputStream is = s3.getObject(b -> b.bucket(s3Properties.getBucket())
        .key(key));

    System.out.println(is.readAllBytes()
        .toString());
    System.out.println("다운로드 성공: " + key);
  }

  @Test
  void presignedUrl() {
    S3Presigner presigner = s3Properties.presigner();

    var get = GetObjectRequest.builder().bucket(s3Properties.getBucket()).key("test/test.txt")
        .build();
    var pre = GetObjectPresignRequest.builder().getObjectRequest(get)
        .signatureDuration(Duration.ofMinutes(10)).build();
    URL url = presigner.presignGetObject(pre).url();
    System.out.println("PRESIGNED_GET: " + url);
  }
}
