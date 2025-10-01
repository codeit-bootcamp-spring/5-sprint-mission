package com.sprint.mission.discodeit.storage.S3;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

public class S3PresignerFactory {
    public static S3Presigner create(AWSProperties props) {
        return S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        props.getAccessKey(),
                                        props.getSecretKey()
                                )
                        )
                )
                .build();
    }
}
