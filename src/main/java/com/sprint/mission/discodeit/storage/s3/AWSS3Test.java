package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.configuration.AWSProperties;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

public class AWSS3Test {

  private final AWSProperties props = new AWSProperties();
  private final AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
      AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey()));
  private final Region region = Region.of(props.getRegion());
  private final String bucket = props.getBucket();

  private final S3Client s3Client = S3Client.builder()
      .region(region)
      .credentialsProvider(credentialsProvider)
      .build();

  private final S3Presigner s3Presigner = S3Presigner.builder()
      .region(region)
      .credentialsProvider(credentialsProvider)
      .build();

  public void testUpload() {
    try {
      String key = "tests/" + UUID.randomUUID() + "/hello.txt";
      byte[] data = "hello-upload".getBytes(StandardCharsets.UTF_8);

      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .contentType("text/plain")
          .build();

      s3Client.putObject(request, RequestBody.fromBytes(data));
      System.out.println("[UPLOAD OK] s3://" + bucket + "/" + key);
    } catch (Exception e) {
      System.err.println("[UPLOAD FAIL]" + e.getMessage());
      e.printStackTrace();
    }
  }

  public void testDownload() {
    try {
      String key = "tests/" + UUID.randomUUID() + "/download.txt";
      byte[] data = "hello-download".getBytes(StandardCharsets.UTF_8);

      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(bucket)
              .key(key)
              .contentType("text/plain")
              .build(),
          RequestBody.fromBytes(data)
      );

      byte[] downloaded = s3Client.getObjectAsBytes(
          GetObjectRequest.builder().bucket(bucket).key(key).build()
      ).asByteArray();

      String text = new String(downloaded, StandardCharsets.UTF_8);
      System.out.println("[DOWNLOAD OK] content=" + text);
    } catch (Exception e) {
      System.err.println("[DOWNLOAD FAIL]" + e.getMessage());
      e.printStackTrace();
    }
  }

  public void testPresignedUrl() {
    try {
      String putKey = "tests/" + UUID.randomUUID() + "/presigned-put.txt";
      PutObjectPresignRequest putPresign = PutObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(5))
          .putObjectRequest(PutObjectRequest.builder()
              .bucket(bucket)
              .key(putKey)
              .build())
          .build();
      PresignedPutObjectRequest putRequest = s3Presigner.presignPutObject(putPresign);
      String putUrl = putRequest.url().toString();
      System.out.println("[PRESENT URL] " + putUrl);

      byte[] data = "via-presigned-put".getBytes(StandardCharsets.UTF_8);
      HttpURLConnection putConn = (HttpURLConnection) new URL(putUrl).openConnection();
      putConn.setDoOutput(true);
      putConn.setRequestMethod("PUT");
      putConn.getOutputStream().write(data);
      int putCode = putConn.getResponseCode();
      if (putCode / 100 != 2) {
        System.out.println("[PUT FAIL] code=" + putCode + "\n body" + readBody(putConn));
        throw new IllegalStateException("PUT FAIL=" + putCode);
      }
      System.out.println("[PRESIGNED PUT OK] " + putUrl);

      GetObjectPresignRequest getPresign = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(5))
          .getObjectRequest(GetObjectRequest.builder()
              .bucket(bucket)
              .key(putKey)
              .build())
          .build();
      PresignedGetObjectRequest getRequest = s3Presigner.presignGetObject(getPresign);
      String getUrl = getRequest.url().toString();

      HttpURLConnection getConn = (HttpURLConnection) new URL(getUrl).openConnection();
      getConn.setRequestMethod("GET");
      int getCode = getConn.getResponseCode();
      if (getCode / 100 != 2) {
        System.out.println("[GET] code=" + getCode + "\n body" + readBody(getConn));
        throw new IllegalStateException("GET FAIL=" + getCode);
      }
      byte[] downloaded = getConn.getInputStream().readAllBytes();
      System.out.println("[DOWNLOAD OK] " + getUrl);
      System.out.println("[PRESIGNED GET OK] content=" +
          new String(downloaded, StandardCharsets.UTF_8));


    } catch (Exception e) {
      System.err.println("[DOWNLOAD FAIL]" + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    AWSS3Test test = new AWSS3Test();
    test.testUpload();
    test.testDownload();
    test.testPresignedUrl();
  }

  private static String readBody(HttpURLConnection conn) {
    try (var is = (conn.getErrorStream() != null) ? conn.getErrorStream() : conn.getInputStream()) {
      return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
    } catch (Exception e) {
      return "(no body)";
    }
  }

}
