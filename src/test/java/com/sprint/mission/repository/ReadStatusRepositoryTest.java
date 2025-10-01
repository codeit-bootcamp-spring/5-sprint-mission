package com.sprint.mission.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class ReadStatusRepositoryTest {

	@Autowired
	private ReadStatusRepository readStatusRepository;
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

	String fileName;
	String contentType;
	long size;

	User user1;
	User user2;
	Channel channel1;
	Channel channel2;
	BinaryContent userProfile;

	Statistics stats;

	@BeforeEach
	public void setup() {
		fileName = "userProfileFileName";
		contentType = "contentType";
		size = 123L;
		userProfile = BinaryContent.builder()
		  .fileName(fileName)
		  .contentType(contentType)
		  .size(size)
		  .build();
		binaryContentRepository.save(userProfile);

		username = "username";
		email = "username";
		password = "password";
		user1 = User.builder()
		  .username(username)
		  .email(email)
		  .password(password)
		  .profileImage(userProfile)
		  .build();
		userRepository.save(user1);
		user2 = User.builder()
		  .username(username + "2")
		  .email(email + "2")
		  .password(password)
		  .profileImage(null)
		  .build();
		userRepository.saveAll(List.of(user1, user2));

		channelName = "channelName";
		channelDescription = "channelDescription";
		channelType = ChannelType.PUBLIC;
		channel1 = Channel.builder()
		  .type(ChannelType.PUBLIC)
		  .name(channelName)
		  .description(channelDescription)
		  .build();
		channel2 = Channel.builder()
		  .type(ChannelType.PUBLIC)
		  .name(channelName)
		  .description(channelDescription)
		  .build();
		channelRepository.saveAll(List.of(channel1, channel2));

		em.flush();  // 실제 SQL 실행
		// Hibernate Statistics 활성화 및 초기화
		stats = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
		stats.setStatisticsEnabled(true);

	}

	@Test
	public void findAllByUserIdTest() {
		// Given
		ReadStatus readStatus1 = new ReadStatus(user1, channel1);
		ReadStatus readStatus2 = new ReadStatus(user1, channel2);
		readStatusRepository.saveAll(List.of(readStatus1, readStatus2));

		em.flush();
		stats.clear();  // 이전 테스트 쿼리 카운트 초기화

		// When
		List<ReadStatus> result = readStatusRepository.findAllByUserId(user1.getId());
		result.forEach(rs -> {
			rs.getChannel().getName();
			rs.getUser().getProfileImage();
		});
		// Then
		assertThat(result).hasSize(2);
		assertThat(result).containsAll(List.of(readStatus1, readStatus2));
		assertThat(stats.getPrepareStatementCount()).isEqualTo(1L);

	}

	@Test
	public void findReadStatusDetailAllByChannelIdsTest() {
		// Given
		ReadStatus readStatus1 = new ReadStatus(user1, channel1);
		ReadStatus readStatus2 = new ReadStatus(user2, channel2);
		readStatusRepository.saveAll(List.of(readStatus1, readStatus2));

		em.flush();
		stats.clear();  // 이전 테스트 쿼리 카운트 초기화

		// When
		List<ReadStatus> result = readStatusRepository.findReadStatusDetailAllByChannelIds
		  (List.of(channel1.getId(), channel2.getId()));
		result.forEach(rs -> {
			rs.getChannel().getName();
			rs.getUser().getProfileImage();
		});

		// Then
		assertThat(result).hasSize(2);
		assertThat(result).containsAll(List.of(readStatus1, readStatus2));
		assertThat(stats.getPrepareStatementCount()).isEqualTo(1L);
	}

	@Test
	public void findReadStatusDetailsByIdTest() {
		// Given
		ReadStatus readStatus = new ReadStatus(user1, channel1);
		readStatusRepository.save(readStatus);

		// When
		Optional<ReadStatus> result = readStatusRepository.findReadStatusDetailsById(readStatus.getId());
		ReadStatus rs = result.orElseThrow();

		rs.getChannel().getName();
		rs.getUser().getProfileImage();

		// Then
		assertThat(rs.equals(readStatus)).isTrue();

	}

}
