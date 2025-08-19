package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new FileBinaryContentRepository(); // 파일 기반 저장소라 스프링이 자동으로 못찾아서 수동 등록
    }
}
