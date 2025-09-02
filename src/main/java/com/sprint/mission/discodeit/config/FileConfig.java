package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.file.common.FileUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class FileConfig {
    @Value("${discodeit.repository.file-directory:data}")
    private String fileDirectory;

    @PostConstruct
    public void init() {
        FileUtils.setBasePath(Paths.get(System.getProperty("user.dir"), fileDirectory));
    }
}
