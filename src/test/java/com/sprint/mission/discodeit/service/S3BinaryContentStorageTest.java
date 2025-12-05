package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.net.URL;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.sprint.mission.discodeit.configuration.property.AWSProperties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.storage.StorageWriteException;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3PresignService;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@ExtendWith(MockitoExtension.class)
public class S3BinaryContentStorageTest {

	@Mock
	S3Client s3Client;
	@Mock
	S3PresignService presignService;
	@Mock
	AWSProperties awsProps;

	@InjectMocks
	S3BinaryContentStorage storage;

	@BeforeEach
	void setUp() {
		given(awsProps.bucket()).willReturn("bucket");
	}

	@Test
	@DisplayName("put: 버킷/키가 올바르게 호출되고 같은 UUID 반환")
	void put_success() {
		UUID id = UUID.randomUUID();
		byte[] content = "content".getBytes();

		given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
			.willReturn(PutObjectResponse.builder().build());

		UUID returned = storage.put(id, content);

		assertThat(returned).isEqualTo(id);

		ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
		verify(s3Client).putObject(captor.capture(), any(RequestBody.class));

		PutObjectRequest request = captor.getValue();
		assertThat(request.bucket()).isEqualTo("bucket");
		assertThat(request.key()).isEqualTo("storage/" + id);
	}

	@Test
	@DisplayName("put: S3 업로드 실패 시 StorageWriteException 던짐(키 상세 포함)")
	void put_fail_exception() {
		UUID id = UUID.randomUUID();
		byte[] content = "boom!".getBytes();
		given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
			.willThrow(new RuntimeException("S3 down"));

		assertThatThrownBy(() -> storage.put(id, content))
			.isInstanceOf(StorageWriteException.class);
	}

	@Test
	@DisplayName("download: Presigned GET URL로 302 리다이렉트(바디 없음)")
	void download_success() throws Exception {
		UUID id = UUID.randomUUID();
		String key = "storage/" + id;
		String filename = "report.pdf";
		BinaryContentDto dto = new BinaryContentDto(
			id,
			filename,
			(long)"this is report".getBytes().length,
			"application/pdf",
			BinaryContentStatus.SUCCESS
		);

		URL presigned = new URL("https://example.com/presigned/get/url");
		given(presignService.createGetPresignedUrl(eq(key), any(String.class), eq(filename)))
			.willReturn(presigned);

		ResponseEntity<Resource> response = storage.download(dto);

		assertThat(response.getStatusCode().value()).isEqualTo(302);
		assertThat(response.getHeaders().getLocation()).hasToString(presigned.toString());
		assertThat(response.getBody()).isNull();

		verify(presignService).createGetPresignedUrl(eq(key), any(String.class), eq(filename));
	}

}
