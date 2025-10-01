package com.sprint.mission.discodeit.storage.S3;

import com.sprint.mission.discodeit.domain.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final AWSProperties props = new AWSProperties();
    private final S3Presigner s3Presigner = S3PresignerFactory.create(props);
    private final S3Client s3Client = S3ClientFactory.create(props);


    @Override
    public UUID upload(UUID binaryContentId, byte[] bytes) {
        String key = "binary/" + binaryContentId;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .contentType("application/octet-stream")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));
        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        String key = "binary/" + binaryContentId;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build();

        // S3에서 직접 다운로드해서 InputStream 반환
        byte[] bytes = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        String key = "binary/" + metaData.id();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(getObjectRequest)
                .build();

        URI presignedUrl = s3Presigner.presignGetObject(presignRequest).httpRequest().getUri();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(presignedUrl)
                .build();
    }
}
