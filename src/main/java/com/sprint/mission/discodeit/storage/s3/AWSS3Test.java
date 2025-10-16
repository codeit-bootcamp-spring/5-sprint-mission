package com.sprint.mission.discodeit.storage.s3;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

public class AWSS3Test {

  /* .env 파일에서 AWS S3에 접속하는 데 필요한 정보(키/시크릿/리전/버킷명)를 읽어서
   자바 S3 SDK로 직접 S3에 연결한 뒤
   아래 세 가지를 직접 코드로 테스트하는 용도

   (1) 파일 업로드
   (2) 파일 다운로드
   (3) 파일 URL
*/

  private static final Properties props = EnvLoader.load(); //환경변수 불러옴
  private static final S3Client s3 = S3Client.builder() //AWS SDK 자체에서 제공하는 빌더 클래스
      .region(Region.of(props.getProperty("AWS_S3_REGION")))
      .credentialsProvider(StaticCredentialsProvider.create(
          AwsBasicCredentials.create(
              props.getProperty("AWS_S3_ACCESS_KEY"),
              props.getProperty("AWS_S3_SECRET_KEY")
          )
      ))
      .build();


  //업로드 메서드
  public static void uploadFile() {
    String bucket = props.getProperty("AWS_S3_BUCKET");
    String key = "sample.txt";
    Path filePath = Paths.get("src/test/resources/sample.txt");

    //어떤 버킷에 어떤 키 이름으로 업로드할지
    s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(),
        RequestBody.fromFile(filePath)); // 실제 업로드할 파일 내용
    System.out.println("✅파일 업로드 완료! + key");
  }


  //파일 다운로드
  public static void downloadFile() throws IOException {
    String bucket = props.getProperty("AWS_S3_BUCKET");
    String key = "sample.txt";
    Path dest = Paths.get("C:/dev_source/5-sprint-mission/downloaded_sample.txt");

    s3.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build(),
        ResponseTransformer.toFile(dest));

    System.out.println("✅ 파일 다운로드 완료: " + dest.toAbsolutePath());
  }


  //업로드한 S3파일에 접근할수 있는 URL 생성
  public static void generatePresignedUrl() {
    String bucket = props.getProperty("AWS_S3_BUCKET");
    String key = "sample.txt";

    S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(props.getProperty("AWS_S3_REGION")))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                props.getProperty("AWS_S3_ACCESS_KEY"),
                props.getProperty("AWS_S3_SECRET_KEY")
            )
        ))
        .build();

    // Presigned URL은 1분(60초) 유효하도록 설정
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .getObjectRequest(GetObjectRequest.builder().bucket(bucket).key(key).build())
        .signatureDuration(Duration.ofMinutes(1))
        .build();

    PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
    System.out.println("✅ Presigned URL은?: " + presignedRequest.url());

    presigner.close();
  }


  // 테스트 실행용 메인 메서드 (필요 시 실행)
  public static void main(String[] args) throws IOException {
    uploadFile();
    downloadFile();
    generatePresignedUrl();
  }
}