package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TestEntityManager em;

    private Channel createChannel(PublicChannelCreateRequest request) {

        return Channel.builder()
                .type(ChannelType.PUBLIC)
                .description("hi")
                .name("bus")
                .build();
    }

    @Test
    @DisplayName("findAllByTypeOrIdInTest")
    void findAllByTypeOrIdInTest() {
        Channel channel = channelRepository.save(createChannel(new PublicChannelCreateRequest("hi","bus")));
        em.flush();
        em.clear();
        List<Channel> channels = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC,new ArrayList<>(Collections.singleton(channel.getId())) );
        assertThat(channels).isNotEmpty();


    }



}
