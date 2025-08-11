package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Value("${discodeit.repository.type:jcf}") // 기본값 jcf
    private String repositoryType;

    @Value("${discodeit.repository.file-directory:.discodeit}")
    private String fileDirectory;

    @Bean
    public UserRepository userRepository() {
        if ("file".equalsIgnoreCase(repositoryType)) {
            return new FileUserRepository(fileDirectory); // 파일저장소
        } else {
            return new JCFUserRepository(); //메모리저장소
        }
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        if ("file".equalsIgnoreCase(repositoryType)) {
            return new FileUserStatusRepository(fileDirectory); // 파일저장소
        } else {
            return new JCFUserStatusRepository(); //메모리저장소
        }
    }

    @Bean
    public MessageRepository messageRepository() {
        if ("file".equalsIgnoreCase(repositoryType)) {
            return new FileMessageRepository(fileDirectory);
        } else {
            return new JCFMessageRepository();
        }
    }

    @Bean
    public ChannelRepository channelRepository() {
        if ("file".equalsIgnoreCase(repositoryType)) {
            return new FileChannelRepository(fileDirectory);
        } else {
            return new JCFChannelRepository();
        }
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        if ("file".equalsIgnoreCase(repositoryType)) {
            return new FileReadStatusRepository(fileDirectory);
        } else {
            return new JCFReadStatusRepository();
        }
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        if ("file".equalsIgnoreCase(repositoryType)) {
            return new FileBinaryContentRepository(fileDirectory);
        } else {
            return new JCFBinaryContentRepository();
        }
    }

}
