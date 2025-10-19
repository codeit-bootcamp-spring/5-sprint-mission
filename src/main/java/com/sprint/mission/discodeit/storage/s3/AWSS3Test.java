package com.sprint.mission.discodeit.storage.s3;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

/**
 * AWS S3에 이미지 파일을 업로드/다운로드/Presigned URL 생성하는 간단 테스트 클래스.
 * (src/main/java 아래 위치)
 */
public class AWSS3Test {

    private static final Properties props = new Properties();
    private static S3Client s3;
    private static S3Presigner presigner;

    public static void main(String[] args) throws IOException {
        loadEnv();
        initS3Client();

        // 테스트용 로컬 이미지 경로 (프로젝트 내 images/sample.png 라고 가정)
        Path imagePath = Path.of("C:/Users/user/Desktop/files/IMG_8132.JPEG");

        UUID imageId = UUID.randomUUID();
        String key = "discodeit/images/" + imageId + ".png";

        uploadImage(imagePath, key);      // 🟩 업로드
        downloadImage(key, "downloads/test-download.png"); // 🟨 다운로드
        generatePresignedUrl(key);        // 🟦 Presigned URL 생성

        s3.close();
        presigner.close();
    }

    /** .env 로드 */
    private static void loadEnv() throws IOException {
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
        }
    }

    /** S3Client, S3Presigner 초기화 */
    private static void initS3Client() {
        AwsBasicCredentials creds = AwsBasicCredentials.create(
                props.getProperty("AWS_S3_ACCESS_KEY"),
                props.getProperty("AWS_S3_SECRET_KEY")
        );

        Region region = Region.of(props.getProperty("AWS_REGION"));

        s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();

        presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();
    }

    // =============================================================
    // 1️⃣ 이미지 업로드
    // =============================================================
    private static void uploadImage(Path imagePath, String key) throws IOException {
        String bucket = props.getProperty("AWS_S3_BUCKET");

        // 이미지 파일을 바이트 배열로 읽기
        byte[] bytes = Files.readAllBytes(imagePath);

        // 이미지 MIME 타입 자동 추정 (간단 버전)
        String contentType = Files.probeContentType(imagePath);
        if (contentType == null) contentType = "application/octet-stream";

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3.putObject(putReq, RequestBody.fromBytes(bytes));

        System.out.println("✅ 이미지 업로드 완료: s3://" + bucket + "/" + key);
    }

    // =============================================================
    // 2️⃣ 이미지 다운로드
    // =============================================================
    private static void downloadImage(String key, String saveAsPath) throws IOException {
        String bucket = props.getProperty("AWS_S3_BUCKET");

        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        Path savePath = Path.of(saveAsPath);                 // 예: "downloads/test-download.png"
        Path parent = savePath.getParent();                  // "downloads"
        if (parent != null) {
            Files.createDirectories(parent);                 // 폴더 없으면 생성
        }

        try (ResponseInputStream<GetObjectResponse> in = s3.getObject(getReq);
             OutputStream out = Files.newOutputStream(savePath)) {
            in.transferTo(out);
            System.out.println("📥 이미지 다운로드 완료 → " + saveAsPath);
        }
    }

    // =============================================================
    // 3️⃣ Presigned URL 생성
    // =============================================================
    private static void generatePresignedUrl(String key) {
        String bucket = props.getProperty("AWS_S3_BUCKET");

        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getReq)
                .build();

        PresignedGetObjectRequest presigned = presigner.presignGetObject(presignReq);
        System.out.println("🔗 10분 유효한 다운로드 URL:");
        System.out.println(presigned.url());
    }
}
