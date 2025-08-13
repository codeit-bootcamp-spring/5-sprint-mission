package com.codeit.mission.discodeit.config;

import com.codeit.mission.discodeit.repository.*;
import com.codeit.mission.discodeit.repository.file.*;
import com.codeit.mission.discodeit.repository.jcf.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RepositoryConfiguration {

    @ConfigurationProperties(prefix = "discodeit.repository")
    public static class RepositoryProperties {
        private String type = "file";
        private String fileDirectory = ".file-data-map";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFileDirectory() {
            return fileDirectory;
        }

        public void setFileDirectory(String fileDirectory) {
            this.fileDirectory = fileDirectory;
        }

        public boolean isJcf() {
            return "jcf".equalsIgnoreCase(type);
        }
    }

    public static class RepositoryFactory {
        private final RepositoryProperties properties;

        public RepositoryFactory(RepositoryProperties properties) {
            this.properties = properties;
        }

        public UserRepository createUserRepository() {
            return properties.isJcf() ? new JCFUserRepository() : new FileUserRepository(properties.getFileDirectory());
        }

        public UserStatusRepository createUserStatusRepository() {
            return properties.isJcf() ? new JCFUserStatusRepository() : new FileUserStatusRepository(properties.getFileDirectory());
        }

        public BinaryContentRepository createBinaryContentRepository() {
            return properties.isJcf() ? new JCFBinaryContentRepository() : new FileBinaryContentRepository(properties.getFileDirectory());
        }

        public ChannelRepository createChannelRepository() {
            return properties.isJcf() ? new JCFChannelRepository() : new FileChannelRepository(properties.getFileDirectory());
        }

        public MessageRepository createMessageRepository() {
            return properties.isJcf() ? new JCFMessageRepository() : new FileMessageRepository(properties.getFileDirectory());
        }

        public ReadStatusRepository createReadStatusRepository() {
            return properties.isJcf() ? new JCFReadStatusRepository() : new FileReadStatusRepository(properties.getFileDirectory());
        }
    }

    @Bean
    @ConfigurationProperties(prefix = "discodeit.repository")
    public RepositoryProperties repositoryProperties() {
        return new RepositoryProperties();
    }

    @Bean
    public RepositoryFactory repositoryFactory(RepositoryProperties properties) {
        return new RepositoryFactory(properties);
    }

    @Bean("userRepository")
    @Primary
    public UserRepository userRepository(RepositoryFactory factory) {
        return factory.createUserRepository();
    }

    @Bean("userStatusRepository")
    @Primary
    public UserStatusRepository userStatusRepository(RepositoryFactory factory) {
        return factory.createUserStatusRepository();
    }

    @Bean("binaryContentRepository")
    @Primary
    public BinaryContentRepository binaryContentRepository(RepositoryFactory factory) {
        return factory.createBinaryContentRepository();
    }

    @Bean("channelRepository")
    @Primary
    public ChannelRepository channelRepository(RepositoryFactory factory) {
        return factory.createChannelRepository();
    }

    @Bean("messageRepository")
    @Primary
    public MessageRepository messageRepository(RepositoryFactory factory) {
        return factory.createMessageRepository();
    }

    @Bean("readStatusRepository")
    @Primary
    public ReadStatusRepository readStatusRepository(RepositoryFactory factory) {
        return factory.createReadStatusRepository();
    }
}