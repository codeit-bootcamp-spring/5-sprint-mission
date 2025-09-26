package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

/* DataJpaTest 활용
 * 데이터소스는 H2 인메모리 데이터 베이스를 사용하고, PostgreSQL 호환 모드로 설정
 * JPA Audit 기능을 활성화 하기 위해 테스트 클래스에 @EnableJpaAuditing을 추가
 * [ ] 커스텀 쿼리 메소드 -> 구현 예정
 * [ ] 페이징 및 정렬 메소드 -> 구현 예정
 */

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
public class MessageRepositoryTest {

  @Autowired
  MessageRepository messageRepository;
  @Autowired
  ChannelRepository channelRepository;
  @Autowired
  UserRepository userRepository;

  @Test
    /*메시지 저장/조회 성공
     */
  void saveAndFindById_success() {

    // given
    User user = userRepository.save(new User("testUser", "pw", "email@test.com"));
    Channel channel = channelRepository.save(
        new Channel("testChannel", "desc", ChannelType.PUBLIC));
    Message msg = new Message("hello", user, channel);

    Message saved = messageRepository.save(msg);

    // when
    Optional<Message> found = messageRepository.findById(saved.getId());

    // then
    assertThat(found).isPresent();
    assertThat(found.get().getContent()).isEqualTo("hello");
  }

  @Test
    /*없는 메시지 ID 조회시 실패
     * given 필요 없음
     */
  void findById_fail() {
    //when
    Optional<Message> found = messageRepository.findById(UUID.randomUUID());

    //then
    assertThat(found).isEmpty();
  }

  @Test
    /*채널별 메시지 리스트 조회 성공
     */
  void findAllByChannelId_success() {

    // given
    User user = userRepository.save(new User("u1", "p1", "u1@email.com"));
    Channel channel = channelRepository.save(new Channel("ch", "desc", ChannelType.PUBLIC));
    Message msg1 = new Message("content1", user, channel);
    Message msg2 = new Message("content2", user, channel);
    messageRepository.save(msg1);
    messageRepository.save(msg2);

    // when
    List<Message> messages = messageRepository.findAllByChannel_Id(channel.getId());

    // then
    assertThat(messages).hasSize(2);
    assertThat(messages.get(0).getChannel().getId()).isEqualTo(channel.getId());
  }

  /*채널에 속한 메시지 없으면 빈 리스트 반환
   */
  @Test
  void findAllByChannelId_fail_noMessages() {

    //given
    Channel channel = channelRepository.save(new Channel("emptyCh", "desc", ChannelType.PUBLIC));

    //when
    List<Message> messages = messageRepository.findAllByChannel_Id(channel.getId());

    //then
    assertThat(messages).isEmpty();
  }
}


