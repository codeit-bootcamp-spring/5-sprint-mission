package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

/* DataJpaTest 활용
 * 데이터소스는 H2 인메모리 데이터 베이스를 사용하고, PostgreSQL 호환 모드로 설정
 * JPA Audit 기능을 활성화 하기 위해 테스트 클래스에 @EnableJpaAuditing을 추가
 * [ ] 커스텀 쿼리 메소드
 * [ ] 페이징 및 정렬 메소드
 */
@DataJpaTest
@ActiveProfiles("test") // application-test.yaml 적용
@EnableJpaAuditing
public class ChannelRepositoryTest {

  @Autowired
  ChannelRepository channelRepository;

  @Test
    /*채널 저장 및 단건 조회 성공
     */
  void saveAndFindById_success() {
    // given
    Channel channel = new Channel("test채널", "설명", ChannelType.PUBLIC);
    Channel saved = channelRepository.save(channel);

    // when
    Optional<Channel> found = channelRepository.findById(saved.getId());

    // then
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("test채널");
  }

  /*없는 채널 ID 조회시 실패
   */
  @Test
  void findById_fail() {
    // when
    Optional<Channel> found = channelRepository.findById(java.util.UUID.randomUUID());

    // then
    assertThat(found).isEmpty();
  }

  /*
  채널 여러개 저장 후 전체 조회
  */
  @Test
  void findAll_success() {
    // given
    Channel ch1 = new Channel("채널1", "설명1", ChannelType.PUBLIC);
    Channel ch2 = new Channel("채널2", "설명2", ChannelType.PRIVATE);

    channelRepository.save(ch1);
    channelRepository.save(ch2);

    // when
    var channels = channelRepository.findAll();

    // then
    assertThat(channels.size()).isGreaterThanOrEqualTo(2);
  }

  /*채널 삭제 성공
   */
  @Test
  void deleteById_success() {
    // given
    Channel channel = new Channel("del채널", "del설명", ChannelType.PUBLIC);
    Channel saved = channelRepository.save(channel);

    // when
    channelRepository.deleteById(saved.getId());

    // then
    Optional<Channel> found = channelRepository.findById(saved.getId());
    assertThat(found).isEmpty();
  }
}



