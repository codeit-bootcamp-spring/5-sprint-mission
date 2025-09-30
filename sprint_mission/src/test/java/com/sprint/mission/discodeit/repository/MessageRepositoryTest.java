package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.repository.query.Param;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
public class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private TestEntityManager em;

    private Channel channel;
    private User author;

    @BeforeEach
    void setUp() {
        channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "채널1","채널1입니다"));

        author = userRepository.save(new User("test1", "test1@test.com","1234",null));

        UserStatus status = new UserStatus(author, Instant.now());
        userStatusRepository.save(status);


        // 메시지 30개 생성
//        messageRepository.save(new Message("Hello 1", channel, author,null));
//        messageRepository.save(new Message("Hello 2", channel, author, null));
//        messageRepository.save(new Message("Hello 3", channel, author, null));
        for (int i = 0; i < 30; i++) {
            messageRepository.save(new Message("Hello " + i, channel, author, null));
        }

        em.flush(); // 메시지 저장 후 flush
        em.clear(); // 영속성 컨텍스트 초기화
    }


//    Slice<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId,
    @Test
    void findAllByChannelIdWithAuthor(){
        // given
        Instant now = Instant.now();
        Pageable pageable = PageRequest.of(0, 10);


        // when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
                channel.getId(),
                now.plusSeconds(60),
                pageable
        );
        System.out.println("안녕 "+now.minusSeconds(20)+" "+Instant.now());
        System.out.println("안녕2 "+result);

        // then
        assertEquals(10, result.getContent().size());
        assertTrue(result.getContent().stream()
                .allMatch(m -> m.getAuthor() != null && m.getAuthor().getStatus() != null));
        assertEquals(10, result.getContent().size());
        assertTrue(result.hasNext());  // 30개 중 첫 10개라 다음 페이지 존재


    }





}
