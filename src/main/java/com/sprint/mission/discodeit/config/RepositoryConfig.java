package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RepositoryProperties.class)
public class RepositoryConfig {

    @Bean
    public UserRepository userRepository(RepositoryProperties props) {
        if ("file".equalsIgnoreCase(props.getType())) {
            return new FileUserRepository(props.getFilePath());
        } else {
            return new JCFUserRepository();
        }
    }

    @Bean
    public ChannelRepository channelRepository(RepositoryProperties props) {
        if ("file".equalsIgnoreCase(props.getType())) {
            return new FileChannelRepository(props.getFilePath());
        } else {
            return new JCFChannelRepository();
        }
    }

    @Bean
    public MessageRepository messageRepository(RepositoryProperties props) {
        if ("file".equalsIgnoreCase(props.getType())) {
            return new FileMessageRepository(props.getFilePath());
        } else {
            return new JCFMessageRepository();
        }
    }

    @Bean
    public UserStatusRepository userStatusRepository(RepositoryProperties props) {
        if ("file".equalsIgnoreCase(props.getType())) {
            return new FileUserStatusRepository(props.getFilePath());
        } else {
            return new JCFUserStatusRepository();
        }
    }

    @Bean
    public ReadStatusRepository readStatusRepository(RepositoryProperties props) {
        if ("file".equalsIgnoreCase(props.getType())) {
            return new FileReadStatusRepository(props.getFilePath());
        } else {
            return new JCFReadStatusRepository();
        }
    }

    @Bean
    public BinaryContentRepository binaryContentRepository(RepositoryProperties props) {
        if ("file".equalsIgnoreCase(props.getType())) {
            return new FileBinaryContentRepository(props.getFilePath());
        } else {
            return new JCFBinaryContentRepository();
        }
    }

}