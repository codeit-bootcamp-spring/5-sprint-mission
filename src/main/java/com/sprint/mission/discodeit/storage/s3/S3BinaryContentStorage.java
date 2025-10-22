package com.sprint.mission.discodeit.storage.s3;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

	private static final Logger log = LoggerFactory.getLogger(S3BinaryContentStorage.class);
	private final String bucket;
	private final String prefix;
	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	public S3BinaryContentStorage(
	  @Value("${discodeit.storage.s3.bucket}") String bucket,
	  @Value("${discodeit.storage.s3.prefix}") String prefix,
	  S3Client s3Client,
	  S3Presigner s3Presigner
	) {
		this.bucket = bucket;
		this.prefix = prefix;
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
	}

	@Override
	public UUID put(UUID id, byte[] bytes, MediaType contentType) {

		// File 객체 생성
		String s3Key = buildS3Key(id.toString());
		if (bytes == null) { // 삭제 조건
			s3Client.deleteObject(DeleteObjectRequest.builder()
			  .bucket(bucket)
			  .key(s3Key)
			  .build());
			return id;
		}
		// 파일 경로 지정
		S3Client s3Client = getS3Client();

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
		  .bucket(bucket)
		  .key(s3Key)
		  .contentType(contentType.toString())
		  .build();

		try {
			s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return id;
	}

	@Override
	public InputStream get(UUID id) {
		S3Client s3Client = getS3Client();

		try {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
			  .bucket(bucket)
			  .key(buildS3Key(id.toString()))
			  .build();

			ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
			return s3Object;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ResponseEntity<?> download(BinaryContentDto dto) {
		try {
			// S3 key 생성
			String s3Key = buildS3Key(dto.getId().toString());

			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
			  .bucket(bucket)
			  .key(s3Key)
			  .build();

			PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
			  GetObjectPresignRequest.builder()
				.getObjectRequest(getObjectRequest)
				.signatureDuration(Duration.ofMinutes(10))
				.build()
			);

			String presignedUrl = presignedRequest.url().toString();
			s3Presigner.close();

			// UrlResource로 반환
			Resource resource = new UrlResource(presignedUrl);

			ContentDisposition contentDisposition = ContentDisposition
			  .attachment()
			  .filename(dto.getFileName(), StandardCharsets.UTF_8)
			  .build();

			return ResponseEntity.status(HttpStatus.FOUND)
			  .location(URI.create(presignedUrl))
			  .build();

		} catch (MalformedURLException e) {
			log.error("Failed to create UrlResource for BinaryContent with ID={}, fileName={}",
			  dto.getId(), dto.getFileName(), e);
			throw new RuntimeException(e);
		} catch (Exception e) {
			log.error("Failed to generate presigned URL for BinaryContent with ID={}, fileName={}",
			  dto.getId(), dto.getFileName(), e);
			throw new RuntimeException(e);
		}
	}

	private String buildS3Key(String fileName) {
		return (prefix == null || prefix.isBlank()) ?
		  fileName :
		  ((prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix) + "/" + fileName);
	}

	private S3Client getS3Client() {
		return s3Client;
	}
}
