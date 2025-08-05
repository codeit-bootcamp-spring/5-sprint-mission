package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RepositoryProperties.class)
public class RepositoryConfig {

    @Bean
    public UserRepository userRepository(RepositoryProperties props) {
        if ("file".equals(props.getType())) return new FileUserRepository(props.getFilePath());
        else return new JCFUserRepository();
    }

    @Bean
    public UserStatusRepository userStatusRepository(RepositoryProperties props) {
        return new JCFUserStatusRepository();
    }

    @Bean
    public MessageRepository messageRepository(RepositoryProperties props) {
        if ("file".equals(props.getType())) return new FileMessageRepository(props.getFilePath());
        else return new JCFMessageRepository();
    }

    @Bean
    public ChannelRepository channelRepository(RepositoryProperties props) {
        if ("file".equals(props.getType())) return new FileChannelRepository(props.getFilePath());
        else return new JCFChannelRepository();
    }

    @Bean
    public ReadStatusRepository readStatusRepository(RepositoryProperties props) {
        return new JCFReadStatusRepository();
    }

    @Bean
    public BinaryContentRepository binaryContentRepository(RepositoryProperties props) {
        return new JCFBinaryContentRepository();
    }
}
