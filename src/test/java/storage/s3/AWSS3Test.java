package storage.s3;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

class AWSS3Test {
  String accessKey = System.getenv("AWS_S3_ACCESS_KEY");
  String secretKey = System.getenv("AWS_S3_SECRET_KEY");
  String region    = System.getenv("AWS_S3_REGION");
  String bucket    = System.getenv("AWS_S3_BUCKET");

  private StaticCredentialsProvider creds() {
    return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
  }
  private S3Client s3() { return S3Client.builder().credentialsProvider(creds()).region(Region.of(region)).build(); }
  private S3Presigner presigner() { return S3Presigner.builder().credentialsProvider(creds()).region(Region.of(region)).build(); }

  @Test void upload() {
    s3().putObject(PutObjectRequest.builder().bucket(bucket).key("test/hello.txt").contentType("text/plain").build(),
        RequestBody.fromString("hello s3"));
  }
  @Test void download() throws Exception {
    Path tmp = Files.createTempFile("s3-", ".txt");
    s3().getObject(GetObjectRequest.builder().bucket(bucket).key("test/hello.txt").build(),
        software.amazon.awssdk.core.sync.ResponseTransformer.toFile(tmp));
    System.out.println("downloaded: " + tmp);
  }
  @Test void presignedUrl() {
    var get = GetObjectRequest.builder().bucket(bucket).key("test/hello.txt").build();
    var pre = GetObjectPresignRequest.builder().getObjectRequest(get).signatureDuration(Duration.ofMinutes(10)).build();
    URL url = presigner().presignGetObject(pre).url();
    System.out.println("PRESIGNED_GET: " + url);
  }
}
