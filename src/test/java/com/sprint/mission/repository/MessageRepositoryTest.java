package com.sprint.mission.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class MessageRepositoryTest {

	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private BinaryContentRepository binaryContentRepository;

	@Autowired
	private EntityManager em;

	String username;
	String email;
	String password;

	String channelName;
	String channelDescription;
	ChannelType channelType;

	String messageContent = "messageContent";

	String fileName;
	String contentType;
	long size;

	User user;
	Channel channel;
	Message message;
	BinaryContent attachment;

	Statistics stats;

	@BeforeEach
	public void setup() {
		username = "username";
		email = "username";
		password = "password";

		user = User.builder()
		  .username(username)
		  .email(email)
		  .password(password)
		  .profileImage(null)
		  .build();
		userRepository.save(user);

		channelName = "channelName";
		channelDescription = "channelDescription";
		channelType = ChannelType.PUBLIC;
		channel = Channel.builder()
		  .type(ChannelType.PUBLIC)
		  .name(channelName)
		  .description(channelDescription)
		  .build();
		channelRepository.save(channel);

		fileName = "attachmentFileName";
		contentType = "contentType";
		size = 123L;
		attachment = BinaryContent.builder()
		  .fileName(fileName)
		  .contentType(contentType)
		  .size(size)
		  .build();

		em.flush();  // 실제 SQL 실행
		// Hibernate Statistics 활성화 및 초기화
		stats = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
		stats.setStatisticsEnabled(true);

	}

	@Test
	public void findAllDetailsByChannelIdTest() {

		// Given
		Message message1 = new Message("content1", user, channel, List.of(attachment));
		Message message2 = new Message("content2", user, channel, List.of(attachment));
		binaryContentRepository.save(attachment);
		messageRepository.save(message1);
		messageRepository.save(message2);

		em.flush();  // 실제 SQL 실행
		stats.clear();  // 이전 테스트 쿼리 카운트 초기화

		// When
		Page<Message> result = messageRepository
		  .findAllDetailsByChannelId(channel.getId(), PageRequest.of(0, 10));

		// 프록시 초기화 유도
		result.getContent().forEach(m -> {
			m.getUser().getUsername();
			m.getUser().getProfileImage();
			m.getChannel().getName();
			m.getAttachments().size();
		});

		// Then
		assertAll(
		  () -> assertThat(result.getContent().size()).isEqualTo(2),
		  () -> assertThat(result.getContent()).containsExactly(message1, message2),
		  () -> assertThat(stats.getPrepareStatementCount()).isEqualTo(1L) // N+1 검증
		);
	}

	@Test
	public void findAllDetailsByChannelIdAndCursorTest() throws InterruptedException {
		// Given
		Message message1 = new Message("content1", user, channel, List.of(attachment));
		Thread.sleep(50); // 밀리초 단위로 타임스탬프 차이 강제
		Message message2 = new Message("content2", user, channel, List.of(attachment));
		binaryContentRepository.save(attachment);

		messageRepository.save(message1);
		messageRepository.save(message2);

		em.flush();  // 실제 SQL 실행
		stats.clear();  // 이전 테스트 쿼리 카운트 초기화
		Instant cursor = message2.getCreatedAt();

		// When
		Page<Message> result = messageRepository
		  .findAllDetailsByChannelIdAndCursor(channel.getId(), cursor, PageRequest.of(0, 10));

		// Then
		assertAll(
		  () -> assertThat(result.getContent().size()).isEqualTo(1),
		  () -> assertThat(result.getContent()).containsExactly(message1),
		  () -> assertThat(stats.getPrepareStatementCount()).isEqualTo(1L) // N+1 검증
		);
	}

	@Test
	public void findMessageDetailsByIdTest() {
		// Given
		Message message1 = new Message("content1", user, channel, List.of(attachment));
		binaryContentRepository.save(attachment);

		messageRepository.save(message1);

		em.flush();  // 실제 SQL 실행
		stats.clear();  // 이전 테스트 쿼리 카운트 초기화

		// When
		Optional<Message> result = messageRepository.findMessageDetailsById(message1.getId());

		Message item = result.orElseThrow();
		item.getUser().getProfileImage();
		// Then
		assertAll(
		  () -> assertThat(item.equals(message1)).isTrue(),
		  () -> assertThat(stats.getPrepareStatementCount()).isEqualTo(1L) // lazy join 검증
		);

	}

}
