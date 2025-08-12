package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RepositoryConfig {

    @Bean
    @Primary
    public UserRepository fileUserRepository() {
        return new FileUserRepository("users");
    }

    @Bean
    @Primary
    public ChannelRepository fileChannelRepository() {
        return new FileChannelRepository("channels");
    }

    @Bean
    @Primary
    public MessageRepository fileMessageRepository() {
        return new FileMessageRepository("messages");
    }

    @Bean
    @Primary
    public ReadStatusRepository fileReadStatusRepository() {
        return new FileReadStatusRepository("read-status");
    }

    @Bean
    @Primary
    public UserStatusRepository fileUserStatusRepository() {
        return new FileUserStatusRepository("user-status");
    }

    @Bean
    @Primary
    public BinaryContentRepository fileBinaryContentRepository() {
        return new FileBinaryContentRepository("binary");
    }

}



