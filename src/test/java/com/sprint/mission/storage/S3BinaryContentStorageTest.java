package com.sprint.mission.storage;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ExtendWith(MockitoExtension.class)
public class S3BinaryContentStorageTest {

	private S3BinaryContentStorage s3BinaryContentStorage;
	private UUID uuid;
	byte[] bytes;
	MediaType contentType;

	@Mock
	private S3Client s3Client;
	@Mock
	private S3Presigner s3Presigner;

	@BeforeEach
	void setUp() {
		// 생성자 주입으로 직접 초기화
		s3BinaryContentStorage = new S3BinaryContentStorage(
		  "my-bucket",
		  "test-prefix",
		  s3Client,
		  s3Presigner
		);

		uuid = UUID.randomUUID();
		bytes = "testData".getBytes();
		contentType = MediaType.IMAGE_PNG;
	}

	@Test
	@DisplayName("s3 업로드 테스트")
	public void S3StoragePutTest() throws Exception {
		// Given
		PutObjectResponse mockResponse = PutObjectResponse.builder().build();
		when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
		  .thenReturn(mockResponse);

		ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
		ArgumentCaptor<RequestBody> requestBodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

		// When
		UUID result = s3BinaryContentStorage.put(uuid, bytes, contentType);

		// Then
		assertThat(result).isEqualTo(uuid);

		verify(s3Client, times(1))
		  .putObject(putObjectRequestCaptor.capture(), requestBodyCaptor.capture());

		PutObjectRequest capturedRequest = putObjectRequestCaptor.getValue();
		RequestBody capturedBody = requestBodyCaptor.getValue();
		assertEquals("my-bucket", capturedRequest.bucket());
		assertEquals(contentType.toString(), capturedRequest.contentType());
		assertTrue(capturedRequest.key().contains("test-prefix"));

		assertEquals(bytes.length, Optional.of(capturedBody.optionalContentLength()).get().orElse(null));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		capturedBody.contentStreamProvider().newStream().transferTo(baos);
		assertArrayEquals(bytes, baos.toByteArray());
	}

	@Test
	@DisplayName("s3 Object Get 테스트")
	public void S3StorageGetTest() throws IOException {
		//Given
		InputStream inputStream = new ByteArrayInputStream(bytes);
		GetObjectResponse getObjectResponse = GetObjectResponse.builder()
		  .contentLength((long)bytes.length)
		  .contentType("image/png")  // contentType 같은 메타데이터만 가능
		  .build();
		ResponseInputStream<GetObjectResponse> mockResponse =
		  new ResponseInputStream<>(getObjectResponse, inputStream);

		when(s3Client.getObject(any(GetObjectRequest.class)))
		  .thenReturn(mockResponse);

		ArgumentCaptor<GetObjectRequest> GetObjectRequestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);

		// When
		InputStream result = s3BinaryContentStorage.get(uuid);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.readAllBytes()).containsExactly(bytes);

		verify(s3Client, times(1)).getObject(GetObjectRequestCaptor.capture());
		GetObjectRequest capturedBody = GetObjectRequestCaptor.getValue();
		assertThat(capturedBody.bucket()).isEqualTo("my-bucket");
		assertThat(capturedBody.key().contains("test-prefix")).isTrue();
	}

	@Test
	@DisplayName("s3 Object download 테스트")
	public void S3StorageDownloadTest() throws IOException {
		// Given
		BinaryContentDto dto = BinaryContentDto.builder()
		  .id(uuid)
		  .fileName(uuid.toString())
		  .size((long)bytes.length)
		  .contentType(MediaType.IMAGE_PNG_VALUE)
		  .build();

		URL fakeUrl = new URL("https://fake-bucket.s3.amazonaws.com/dummy.png");
		ContentDisposition mockDisposition = ContentDisposition
		  .attachment()
		  .filename(dto.getFileName(), StandardCharsets.UTF_8)
		  .build();
		PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
		when(mockPresignedRequest.url()).thenReturn(fakeUrl);

		when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
		  .thenReturn(mockPresignedRequest);

		// when
		ResponseEntity<?> result = s3BinaryContentStorage.download(dto);

		// Then
		assertThat(result).isNotNull();

		// Status Code 확인
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);

		// Location Header 확인
		URI location = result.getHeaders().getLocation();
		assertThat(location).isNotNull();
		assertThat(location.toString()).isEqualTo(fakeUrl.toString());

	}

}
