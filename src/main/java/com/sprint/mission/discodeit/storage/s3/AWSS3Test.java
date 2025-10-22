package com.sprint.mission.discodeit.storage.s3;

import java.time.Duration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.nio.file.Paths;

public class AWSS3Test {
  private static final String BUCKET_NAME = System.getenv("AWS_S3_BUCKET");
  private static final String ACCESS_KEY = System.getenv("AWS_S3_ACCESS_KEY");
  private static final String SECRET_KEY = System.getenv("AWS_S3_SECRET_KEY");
  private static final Region REGION = Region.of(System.getenv("AWS_S3_REGION"));

  private static S3Client s3Client;
  private static S3Presigner s3Presigner;

  static {
    StaticCredentialsProvider credentialsProvider =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY));

    s3Client = S3Client.builder()
        .region(REGION)
        .credentialsProvider(credentialsProvider)
        .build();

    s3Presigner = S3Presigner.builder()
        .region(REGION)
        .credentialsProvider(credentialsProvider)
        .build();
  }

  public void uploadFile(String key, String filePath) {
    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(key)
          .build();
      s3Client.putObject(putObjectRequest, Paths.get(filePath));
      System.out.println("File uploaded successfully!");
    } catch (Exception e) {
      System.err.println("Upload failed: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void downloadFile(String key, String downloadPath) {
    try {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(key)
          .build();
      s3Client.getObject(getObjectRequest, Paths.get(downloadPath));
      System.out.println("File downloaded successfully!");
    } catch (Exception e) {
      System.err.println("Download failed: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public String generatePresignedUrl(String key) {
    try {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(key)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .getObjectRequest(getObjectRequest)
          .signatureDuration(Duration.ofMinutes(10))
          .build();

      PresignedGetObjectRequest presignedObjectRequest = s3Presigner.presignGetObject(presignRequest);

      return presignedObjectRequest.url().toString();
    } catch (Exception e) {
      System.err.println("Presigned URL generation failed: " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }

  public static void main(String[] args) {
    if (BUCKET_NAME == null || ACCESS_KEY == null || SECRET_KEY == null || REGION == null) {
      System.err.println("FATAL: AWS S3 environment variables (AWS_S3_BUCKET, AWS_S3_ACCESS_KEY, AWS_S3_SECRET_KEY, AWS_S3_REGION) must be set.");
      return;
    }

    AWSS3Test s3Test = new AWSS3Test();

    String url = s3Test.generatePresignedUrl("test.txt");
    if (url != null) {
      System.out.println("Presigned URL: " + url);
    }
  }
}
