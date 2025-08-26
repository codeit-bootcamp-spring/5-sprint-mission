package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@EnableConfigurationProperties(DiscodeitRepositoryProperties.class)
@RequiredArgsConstructor
public class AppConfig {
    private final DiscodeitRepositoryProperties properties;

    @Bean
    public UserRepository userRepository() {
        return switch (properties.getType()) {
            case "file" -> new FileUserRepository(properties.getFileDirectory());
            default -> new JCFUserRepository();
        };
    }

    @Bean
    public ChannelRepository channelRepository() {
        return switch (properties.getType()) {
            case "file" -> new FileChannelRepository(properties.getFileDirectory());
            default -> new JCFChannelRepository();
        };
    }

    @Bean
    public MessageRepository messageRepository() {
        return switch (properties.getType()) {
            case "file" -> new FileMessageRepository(properties.getFileDirectory());
            default -> new JCFMessageRepository();
        };
    }

    @Bean
    public ChannelParticipantRepository channelParticipantRepository() {
        return switch (properties.getType()) {
            case "file" -> new FileChannelParticipantRepository(properties.getFileDirectory());
            default -> new JCFChannelParticipantRepository();
        };
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return switch (properties.getType()) {
            case "file" -> new FileBinaryContentRepository(properties.getFileDirectory());
            default -> new JCFBinaryContentRepository();
        };
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        return switch (properties.getType()) {
            case "file" -> new FileReadStatusRepository(properties.getFileDirectory());
            default -> new JCFReadStatusRepository(new HashMap<>());
        };
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        return switch (properties.getType()) {
            case "file" -> new FileUserStatusRepository(properties.getFileDirectory());
            default -> new JCFUserStatusRepository();
        };
    }

    @Bean
    public UserService userService() {
        return new BasicUserService(userRepository(), binaryContentRepository(), userStatusRepository());
    }

    @Bean
    public ChannelService channelService(ChannelParticipantRepository channelParticipantRepository) {
        return new BasicChannelService(
                channelRepository(),
                readStatusRepository(),
                userRepository(),
                messageRepository(),
                channelParticipantRepository()
        );
    }


    @Bean
    public MessageService messageService() {
        return new BasicMessageService(
                messageRepository(),
                channelRepository(),
                userRepository(),
                binaryContentRepository()
        );
    }

    @Bean
    public UserStatusService userStatusService() {
        return new BasicUserStatusService(userStatusRepository());
    }

    @Bean
    public ReadStatusService readStatusService() {
        return new BasicReadStatusService(readStatusRepository());
    }
}
