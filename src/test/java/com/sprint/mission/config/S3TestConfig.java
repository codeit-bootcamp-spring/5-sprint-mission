package com.sprint.mission.config;

import static org.mockito.Mockito.*;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@TestConfiguration
@Profile("test")
public class S3TestConfig {
	@Bean
	public S3Client s3Client() {
		S3Client mockS3 = mock(S3Client.class);

		when(mockS3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
		  .thenReturn(PutObjectResponse.builder().eTag("mock-etag").build());

		when(mockS3.deleteObject(any(DeleteObjectRequest.class)))
		  .thenReturn(DeleteObjectResponse.builder().build());

		ResponseInputStream<GetObjectResponse> mockStream = mock(ResponseInputStream.class);
		when(mockS3.getObject(any(GetObjectRequest.class)))
		  .thenReturn(mockStream);
		return mockS3;
	}

	@Bean
	public S3Presigner s3Presigner() {
		S3Presigner mockS3Presigner = mock(S3Presigner.class);

		when(mockS3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
		  .thenReturn(PresignedGetObjectRequest.builder().build());

		doNothing().when(mockS3Presigner).close();

		return mockS3Presigner;
	}
}
