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

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.storage", name = "type",
            havingValue = "local", matchIfMissing = true)
    public BinaryContentStorage localBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path:.discodeit/storage}") String root) {
        return new LocalBinaryContentStorage(root);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
    public BinaryContentStorage s3BinaryContentStorage(S3Properties s3) {
        return new S3BinaryContentStorage(
                s3.getAccessKey(),
                s3.getSecretKey(),
                s3.getRegion(),
                s3.getBucket(),
                s3.getPresignedUrlExpiration()
        );
    }
}

