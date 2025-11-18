package com.sprint.mission.discodeit.storage.s3;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.configuration.AWSProperties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.storage.StorageWriteException;
import com.sprint.mission.discodeit.log.LogUtils;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@RequiredArgsConstructor
@Slf4j
public class S3BinaryContentStorage implements BinaryContentStorage {

	private static final String PREFIX = "storage/";

	private final S3PresignService s3PresignService;
	private final S3Client s3Client;
	private final AWSProperties awsProperties;

	private String keyOf(UUID id) {
		return PREFIX + id;
	}

	@Override
	public UUID put(UUID id, byte[] bytes) {
		String key = keyOf(id);
		log.debug("[S3BinaryContentStorage#put] try with key: {}", key);
		try {
			PutObjectRequest request = PutObjectRequest.builder()
				.bucket(awsProperties.getBucket())
				.key(key)
				.build();

			s3Client.putObject(request, RequestBody.fromBytes(bytes));
			log.info("[S3BinaryContentStorage#put] uploaded: s3://{}/{}", awsProperties.getBucket(), key);
			return id;
		} catch (Exception e) {
			throw new StorageWriteException(e).addDetail("S3key", key);
		}
	}

	@Override
	public InputStream get(UUID id) {
		String key = keyOf(id);
		log.debug("[S3BinaryContentStorage#get] try with key: {}", key);
		GetObjectRequest request = GetObjectRequest.builder()
			.bucket(awsProperties.getBucket())
			.key(key)
			.build();

		return s3Client.getObject(request);
	}

	@Override
	public ResponseEntity<Resource> download(BinaryContentDto dto) {
		String key = keyOf(dto.id());
		log.debug("[S3BinaryContentStorage#download] try with key: {}, dto: {}",
			key, LogUtils.summarizeAttachment(dto));

		URL url = s3PresignService.createGetPresignedUrl(
			key, awsProperties.getBucket(), dto.fileName()
		);
		return ResponseEntity.status(302)
			.location(URI.create(url.toString()))
			.build();
	}
}
