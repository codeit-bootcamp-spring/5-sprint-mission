package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.LocalBinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean(name = "localBinaryContentStorage")
    @ConditionalOnProperty(prefix = "discodeit.storage", name = "type",
            havingValue = "local", matchIfMissing = true)
    public BinaryContentStorage local(
            @Value("${discodeit.storage.local.root-path:.discodeit/storage}") String root) {
        return new LocalBinaryContentStorage(root);
    }

    @Bean(name = "s3BinaryContentStorage")
    @ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
    public BinaryContentStorage s3(
            @Value("${discodeit.storage.s3.access-key:}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key:}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration:600}") int expSec
    ) {
        return new S3BinaryContentStorage(accessKey, secretKey, region, bucket, expSec);
    }
}

