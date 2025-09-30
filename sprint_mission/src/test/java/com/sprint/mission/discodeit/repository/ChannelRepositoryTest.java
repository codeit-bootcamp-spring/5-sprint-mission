package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
public class ChannelRepositoryTest {

    @Autowired
    ChannelRepository channelRepository;

    public Channel channel1;
    public Channel channel2;
    public Channel channel3;

    @BeforeEach
    void setUp() {
        channel1 = channelRepository.save(new Channel(ChannelType.PUBLIC, "채널1","채널1입니다"));
        channel2 = channelRepository.save(new Channel(ChannelType.PRIVATE, "채널2","채널2입니다"));
        channel3 = channelRepository.save(new Channel(ChannelType.PUBLIC, "채널3","채널3입니다"));
    }


//    List<Channel> findAllByTypeOrIdIn(ChannelType type, List<UUID> ids);
    @Test
    void findAllByTypeOrIdInTest_Public(){
        List<UUID> ids = List.of(channel1.getId(), channel3.getId()); // 조회할 채널 ID 목록
        ChannelType type = ChannelType.PUBLIC;            // 조회할 타입

        List<Channel> result = channelRepository.findAllByTypeOrIdIn(type, ids);

        assertEquals(2, result.size()); // general(type=PUBLIC), dev(type=PUBLIC), id=1,3 포함

    }
    @Test
    void findAllByTypeOrIdInTest_Private(){
        List<UUID> ids = List.of(channel2.getId()); // 조회할 채널 ID 목록
        ChannelType type = ChannelType.PRIVATE;            // 조회할 타입

        List<Channel> result = channelRepository.findAllByTypeOrIdIn(type, ids);

        assertEquals(1, result.size()); // general(type=PUBLIC), dev(type=PUBLIC), id=1,3 포함

    }


}
