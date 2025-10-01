package com.sprint.mission.discodeit.storage.S3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AWSProperties {

    private final Properties properties = new Properties();

    public AWSProperties() {
        try (FileInputStream fis = new FileInputStream(".env")) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load AWS .env file", e);
        }
    }

    public String getAccessKey() {
        return properties.getProperty("AWS_S3_ACCESS_KEY");
    }

    public String getSecretKey() {
        return properties.getProperty("AWS_S3_SECRET_KEY");
    }

    public String getRegion() {
        return properties.getProperty("AWS_S3_REGION");
    }

    public String getBucket() {
        return properties.getProperty("AWS_S3_BUCKET");
    }
}
