package com.sprint.mission.discodeit.slice.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

@DataJpaTest
public class MessageRepositorySliceTest {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private ChannelRepository channelRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BinaryContentRepository binaryContentRepository;

	@Autowired
	private UserStatusRepository userStatusRepository;

	@Autowired
	private ReadStatusRepository readStatusRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	private Channel createChannel(ChannelType channelType, String channelName, String channelDesc) {
		Channel channel = new Channel(channelType, channelName, channelDesc);
		return channel;
	}

	private User creataUser(String username, String password, String email, BinaryContent binaryContent) {
		return new User(username, email, password, binaryContent);
	}

	private ReadStatus createReadStatus(User user, Channel channel, Instant lastReadAt) {
		return new ReadStatus(user, channel, lastReadAt);
	}

	private BinaryContent createBinaryContent(String filename, Long size, String contentType) {
		return new BinaryContent(filename, size, contentType);
	}

	private Message createMessage(String content, Channel channel, User author) {
		return new Message(content, channel, author, null);
	}

	@Test
	@DisplayName("MessageRepository - findAllByChannelIdWithAuthor - 성공")
	public void findAllByChannelIdWithAuthorSuccess() {
		BinaryContent profile1 = binaryContentRepository.save(createBinaryContent("fileName1", 20L, "contentType1"));
		BinaryContent profile2 = binaryContentRepository.save(createBinaryContent("fileName2", 20L, "contentType2"));
		User user1 = userRepository.save(creataUser("username1", "password1", "email1", profile1));
		User user2 = userRepository.save(creataUser("username2", "password2", "email2", profile2));
		Channel testChannel1 = channelRepository.save(
			createChannel(ChannelType.PUBLIC, "testChannel1", "testChannel1 입니다."));
		Channel testChannel2 = channelRepository.save(
			createChannel(ChannelType.PUBLIC, "testChannel2", "testChannel2 입니다."));

		userStatusRepository.save(new UserStatus(user1, Instant.now()));
		userStatusRepository.save(new UserStatus(user2, Instant.now()));

		messageRepository.save(createMessage("message1", testChannel1, user1));
		messageRepository.save(createMessage("message2", testChannel1, user1));
		messageRepository.save(createMessage("message3", testChannel1, user1));
		messageRepository.save(createMessage("message4", testChannel1, user1));
		messageRepository.save(createMessage("message5", testChannel2, user1));
		messageRepository.save(createMessage("message6", testChannel2, user1));

		testEntityManager.flush();
		testEntityManager.clear();

		Pageable pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "createdAt");
		//
		Slice<Message> allByChannelIdWithAuthor = messageRepository.findAllByChannelIdWithAuthor(testChannel1.getId(),
			Instant.now().plus(1, ChronoUnit.HOURS), pageable);
		//
		assertThat(allByChannelIdWithAuthor.getContent()).hasSize(2);
		assertThat(allByChannelIdWithAuthor.hasNext()).isTrue();
		assertThat(allByChannelIdWithAuthor.getContent())
			.extracting(Message::getContent)
			.containsExactly("message4", "message3");
	}

	@Test
	@DisplayName("MessageRepository - findAllByChannelIdWithAuthor - 실패: UserStatus 없음")
	public void findAllByChannelIdWithAuthor_fail_noStatus() {
		//
		BinaryContent profile1 = binaryContentRepository.save(createBinaryContent("fileName1", 20L, "contentType1"));
		BinaryContent profile2 = binaryContentRepository.save(createBinaryContent("fileName2", 20L, "contentType2"));
		User user1 = userRepository.save(creataUser("username1", "password1", "email1", profile1));
		User user2 = userRepository.save(creataUser("username2", "password2", "email2", profile2));
		Channel ch1 = channelRepository.save(createChannel(ChannelType.PUBLIC, "testChannel1", "desc"));

		messageRepository.save(createMessage("message1", ch1, user1));
		messageRepository.save(createMessage("message2", ch1, user1));

		testEntityManager.flush();
		testEntityManager.clear();

		Pageable pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "createdAt");

		//
		Slice<Message> slice = messageRepository.findAllByChannelIdWithAuthor(
			ch1.getId(),
			Instant.now().plus(1, ChronoUnit.HOURS),
			pageable
		);

		//
		assertThat(slice.getContent()).isEmpty();
		assertThat(slice.hasNext()).isFalse();
	}

	@Test
	@DisplayName("MessageRepository - findLastMessageAtByChannelId - 성공")
	public void findLastMessageAtByChannelIdSuccess() {
		BinaryContent profile1 = binaryContentRepository.save(createBinaryContent("fileName1", 20L, "contentType1"));
		BinaryContent profile2 = binaryContentRepository.save(createBinaryContent("fileName2", 20L, "contentType2"));
		User user1 = userRepository.save(creataUser("username1", "password1", "email1", profile1));
		User user2 = userRepository.save(creataUser("username2", "password2", "email2", profile2));
		Channel testChannel1 = channelRepository.save(
			createChannel(ChannelType.PUBLIC, "testChannel1", "testChannel1 입니다."));
		Channel testChannel2 = channelRepository.save(
			createChannel(ChannelType.PUBLIC, "testChannel2", "testChannel2 입니다."));

		userStatusRepository.save(new UserStatus(user1, Instant.now()));
		userStatusRepository.save(new UserStatus(user2, Instant.now()));

		messageRepository.save(createMessage("message1", testChannel1, user1));
		messageRepository.save(createMessage("message2", testChannel1, user1));
		messageRepository.save(createMessage("message3", testChannel1, user1));
		Message message4 = messageRepository.save(createMessage("message4", testChannel1, user1));
		messageRepository.save(createMessage("message5", testChannel2, user1));
		messageRepository.save(createMessage("message6", testChannel2, user1));

		testEntityManager.flush();
		testEntityManager.clear();
		//
		Optional<Instant> lastOpt = messageRepository.findLastMessageAtByChannelId(testChannel1.getId());

		//
		assertThat(lastOpt).isPresent();
		// 초 단위만
		Instant last = lastOpt.get().truncatedTo(ChronoUnit.SECONDS);
		assertThat(last).isEqualTo(message4.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
	}

}
