package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    private Message createMessage(MessageCreateRequest messageCreateRequest,
                                  List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        Optional<Channel> channel = channelRepository.findById(messageCreateRequest.channelId());
        Optional<User> user = userRepository.findById(messageCreateRequest.authorId());

        return Message.builder()
                .content(messageCreateRequest.content())
                .channel(channel.get())
                .author(user.get())
                .build();
    }

    private User createUser(UserCreateRequest userCreateRequest,
                            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest){


        return User.builder()
                .username(userCreateRequest.username())
                .email(userCreateRequest.email())
                .password(userCreateRequest.password())
                .build();
    }

    private Channel createChannel(PublicChannelCreateRequest request) {

        return Channel.builder()
                .type(ChannelType.PUBLIC)
                .description("hi")
                .name("bus")
                .build();
    }

    @Test
    @DisplayName("findLastMessageAtByChannelId")
    void findLastMessageAtByChannelId() {
        List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();

        Channel channel = channelRepository.save(createChannel(new PublicChannelCreateRequest("hi","bus")));
        User user = userRepository.save(createUser(new UserCreateRequest("bus","bus@sdf.com","bus12"),null));
        UserStatus status = new UserStatus(user , Instant.now());
        messageRepository.save(
                createMessage(new MessageCreateRequest("hi", channel.getId(),user.getId()),binaryContentCreateRequests)
        );

        em.flush();
        em.clear();
        Slice<Message> found = messageRepository.findAllByChannelIdWithAuthor(channel.getId(),null,null);
        assertThat(found).isNotNull();


    }
}
