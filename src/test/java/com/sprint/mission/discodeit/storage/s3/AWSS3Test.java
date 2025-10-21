package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.S3.AWSProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class AWSS3Test {

    @Autowired
    private AWSProperties props;

    @Autowired
    private S3Client s3;

    @Test
    void uploadTest() {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key("test/hello.txt")
                .contentType("text/plain; charset=UTF-8")
                .build();

        s3.putObject(putRequest, new File("hello.txt").toPath());
        assertThat(new File("hello.txt").exists()).isTrue();
    }

    @Test
    void downloadTest() throws IOException {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key("test/hello.txt")
                .build();

        s3.getObject(getRequest, ResponseTransformer.toFile(Paths.get("download_hello.txt")));
        assertThat(new File("download_hello.txt").exists()).isTrue();
    }

    @Test
    void presignedUrlTest() {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
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

            assertThat(url).contains("https://");
        }
    }

}
