// src/main/java/com/sprint/mission/discodeit/storage/s3/S3BinaryContentStorage.java
package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.S3Config.S3Props;              // ✅ 버킷/만료 전달용
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

// ✅ s3.model 패키지
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

// ✅ presigner.model 패키지
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * S3 기반 구현체.
 * - discodeit.storage.type = s3 일 때만 빈으로 등록됨.
 * - put/get은 S3 API 직접 호출
 * - download는 Presigned URL을 만들어 302 Redirect로 응답
 */
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

    // ✅ 설정/클라이언트들은 구성(설정)에서 빈으로 주입받는다.
    private final S3Client s3;             // 업/다운로드
    private final S3Presigner presigner;   // Presigned URL 생성기
    private final S3Props props;           // 버킷/만료(초)

    public S3BinaryContentStorage(S3Client s3, S3Presigner presigner, S3Props props) {
        this.s3 = s3;
        this.presigner = presigner;
        this.props = props;
    }

    /** UUID를 키로 사용해 S3에 업로드. */
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        String key = binaryContentId.toString();
        if (exists(key)) {
            throw new IllegalArgumentException("File with key " + key + " already exists");
        }

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(props.bucket())                          // ✅ 버킷은 props에서
                .key(key)
                .contentType("application/octet-stream")         // 필요시 확장
                .build();

        s3.putObject(req, RequestBody.fromBytes(bytes));
        return binaryContentId;
    }

    /** UUID로 객체를 읽어 InputStream 반환. (호출자가 닫아야 함) */
    @Override
    public InputStream get(UUID binaryContentId) {
        String key = binaryContentId.toString();
        if (!exists(key)) {
            throw new NoSuchElementException("File with key " + key + " does not exist");
        }

        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .build();

        return s3.getObject(req); // ResponseInputStream is InputStream
    }

    /** Presigned URL을 생성해 302 Redirect 응답. */
    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        String key = metaData.id().toString();
        if (!exists(key)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found: " + key);
        }

        String presignedUrl = generatePresignedUrl(key, metaData.contentType());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, presignedUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302
    }

    /** Presigned GET URL 생성 */
    public String generatePresignedUrl(String key, String contentType) {
        GetObjectRequest.Builder get = GetObjectRequest.builder()
                .bucket(props.bucket())
                .key(key);

        if (StringUtils.hasText(contentType)) {
            get.responseContentType(contentType);
        }

        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(props.presignedExpirationSeconds()))
                .getObjectRequest(get.build())
                .build();

        PresignedGetObjectRequest presigned = presigner.presignGetObject(presignReq);
        return presigned.url().toString();
    }

    /** S3에 키 존재 여부 확인 */
    private boolean exists(String key) {
        try {
            s3.headObject(HeadObjectRequest.builder()
                    .bucket(props.bucket())
                    .key(key)
                    .build());
            return true;
        } catch (S3Exception ex) {
            return false; // 404 등
        }
    }

    /** (테스트 편의) 클라이언트 확인용 */
    public S3Client getS3Client() {
        return this.s3;
    }
}
