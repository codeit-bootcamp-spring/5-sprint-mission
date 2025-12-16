package com.sprint.mission.discodeit.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sprint.mission.discodeit.configuration.property.AWSProperties;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class AWSS3Config {

	private S3Client s3Client;
	private S3Presigner s3Presigner;

	@Bean
	public AwsCredentialsProvider awsCredentialsProvider(AWSProperties props) {
		String accessKey = req(props.accessKey(), "AWS_S3_ACCESS_KEY");
		String secretKey = req(props.secretKey(), "AWS_S3_SECRET_KEY");
		AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		return StaticCredentialsProvider.create(awsCredentials);
	}

	@Bean
	public Region awsRegion(AWSProperties props) {
		String region = req(props.region(), "AWS_S3_REGION");
		return Region.of(region);
	}

	@Bean
	public S3Client s3Client(AwsCredentialsProvider credentialsProvider, Region region) {
		this.s3Client = S3Client.builder()
			.credentialsProvider(credentialsProvider)
			.region(region)
			.build();
		return this.s3Client;
	}

	@Bean
	public S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider, Region region) {
		this.s3Presigner = S3Presigner.builder()
			.credentialsProvider(credentialsProvider)
			.region(region)
			.build();
		return this.s3Presigner;
	}

	@PreDestroy
	public void shutdown() {
		if (this.s3Client != null) {
			this.s3Client.close();
		}
		if (this.s3Presigner != null) {
			this.s3Presigner.close();
		}
	}

	private static String req(String value, String name) {
		if (value == null || value.isBlank()) {
			throw new IllegalStateException("missing required property: " + name); // TODO: 커스텀 예외
		}
		return value;
	}
}
