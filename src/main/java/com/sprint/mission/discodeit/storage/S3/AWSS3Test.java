package com.sprint.mission.discodeit.storage.S3;

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;

public class AWSS3Test {

    private static final AWSProperties props = new AWSProperties();
    private static final S3Client s3 = S3ClientFactory.create(props);

    public static void main(String[] args) throws Exception {
        uploadTest();
        downloadTest();
        presignedUrlTest();
    }

    // 업로드
    public static void uploadTest() {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key("test/hello.txt")
                .contentType("text/plain; charset=UTF-8")
                .build();

        s3.putObject(putRequest, new File("hello.txt").toPath());
        System.out.println("업로드 성공!");
    }

    // 다운로드
    public static void downloadTest() throws IOException {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key("test/hello.txt")
                .build();

        s3.getObject(getRequest, ResponseTransformer.toFile(Paths.get("download_hello.txt")));
        System.out.println("다운로드 성공!");
    }

    // Presigned URL 생성
    public static void presignedUrlTest() {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(software.amazon.awssdk.regions.Region.of(props.getRegion()))
                .credentialsProvider(() -> software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(
                        props.getAccessKey(), props.getSecretKey()
                ))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key("test/hello.txt")
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            String url = presigner.presignGetObject(presignRequest).url().toString();
            System.out.println("Presigned URL: " + url);
        }
    }
}
