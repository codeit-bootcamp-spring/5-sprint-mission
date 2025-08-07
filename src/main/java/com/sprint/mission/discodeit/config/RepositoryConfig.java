package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository("users.dat");
    }

    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository("channels.dat");
    }

    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository("messages.dat");
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new FileBinaryContentRepository("binary.dat");
    }
}
