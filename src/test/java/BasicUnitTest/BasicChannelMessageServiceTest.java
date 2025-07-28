package BasicUnitTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.NotChannelMemberException;
import com.sprint.mission.discodeit.exception.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelMessageService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

@DisplayName("BasicChannelMessageService 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicChannelMessageServiceTest {

	private MessageRepository messageRepository;
	private UserRepository userRepository;
	private ChannelRepository channelRepository;
	private BasicUserService userService;
	private BasicChannelService channelService;
	private BasicChannelMessageService channelMessageService;

	private User testUser;
	private User otherUser;
	private Channel testChannel;

	@BeforeEach
	void setUp() {
		cleanDirectory(new File("data/"));

		userRepository = new FileUserRepository();
		channelRepository = new FileChannelRepository();
		messageRepository = new FileMessageRepository();

		userService = new BasicUserService(userRepository);
		channelService = new BasicChannelService(channelRepository);
		channelMessageService = new BasicChannelMessageService(messageRepository, userService, channelService);

		testUser = userService.createUser("test1", "password1", "홍길동");
		otherUser = userService.createUser("test2", "password2", "김길동");
		testChannel = channelService.createChannel("일반");
		channelService.joinChannel(testUser, "일반");
	}

	private void cleanDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						cleanDirectory(file);
					} else {
						file.delete();
					}
				}
			}
			directory.delete();
		}
	}

	@Test
	@Order(1)
	@DisplayName("1. Basic ChannelMessageCreate")
	void testCreateMessage() {
		// given

		// when
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "안녕하세요");

		// then
		List<Message> messages = messageRepository.findByChannelId(testChannel.getId());
		assertThat(messages).hasSize(1);
		assertThat(messages.get(0).getText()).isEqualTo("안녕하세요");
		assertThat(messages.get(0).getAuthorUUID()).isEqualTo(testUser.getId());
		assertThat(messages.get(0).getChannelUUID()).isEqualTo(testChannel.getId());

		Channel updatedChannel = channelRepository.findById(testChannel.getId());
		assertThat(updatedChannel.getChannelMessagesUUID()).contains(messages.get(0).getId());
	}

	@Test
	@Order(2)
	@DisplayName("2. Basic ChannelMessageCreate 사용자없음 예외")
	void testCreateMessage_UserNotFound() {
		// given
		UUID nonExistentUserId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.createMessage(nonExistentUserId, testChannel.getId(), "메시지"))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(3)
	@DisplayName("3. Basic ChannelMessageCreate 채널없음 예외")
	void testCreateMessage_ChannelNotFound() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.createMessage(testUser.getId(), nonExistentChannelId, "메시지"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(4)
	@DisplayName("4. Basic ChannelMessageCreate 채널멤버아님 예외")
	void testCreateMessage_NotChannelMember() {
		// given

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.createMessage(otherUser.getId(), testChannel.getId(), "메시지"))
			.isInstanceOf(NotChannelMemberException.class);
	}

	@Test
	@Order(5)
	@DisplayName("5. Basic ChannelMessageGetByAuthor")
	void testGetMessageByAuthor() {
		// given
		channelService.joinChannel(otherUser, "일반");
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "홍길동 메시지1");
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "홍길동 메시지2");
		channelMessageService.createMessage(otherUser.getId(), testChannel.getId(), "김길동 메시지1");

		// when
		List<Message> result = channelMessageService.getMessageByAuthor("홍길동", testChannel.getId());

		// then
		assertThat(result)
			.isNotNull()
			.hasSize(2);
		assertThat(result)
			.extracting(Message::getText)
			.containsExactlyInAnyOrder("홍길동 메시지1", "홍길동 메시지2");
		assertThat(result)
			.allMatch(message -> message.getAuthorUUID().equals(testUser.getId()));
	}

	@Test
	@Order(6)
	@DisplayName("6. Basic ChannelMessageGetByAuthor 채널없음 예외")
	void testGetMessageByAuthor_ChannelNotFound() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.getMessageByAuthor("홍길동", nonExistentChannelId))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(7)
	@DisplayName("7. Basic ChannelMessageGetByAuthor 존재하지않는작성자")
	void testGetMessageByAuthor_AuthorNotFound() {
		// given
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "테스트 메시지");

		// when
		List<Message> result = channelMessageService.getMessageByAuthor("존재하지않는닉네임", testChannel.getId());

		// then
		assertThat(result)
			.isNotNull()
			.isEmpty();
	}

	@Test
	@Order(8)
	@DisplayName("8. Basic ChannelMessageDelete")
	void testDeleteMessage() {
		// given
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "삭제될 메시지");
		List<Message> messages = messageRepository.findByChannelId(testChannel.getId());
		Message created = messages.get(0);

		// when
		channelMessageService.deleteMessage(created.getId(), testUser.getId());

		// then
		Message deletedMessage = messageRepository.findById(created.getId());
		assertThat(deletedMessage).isNull();

		Channel updatedChannel = channelRepository.findById(testChannel.getId());
		assertThat(updatedChannel.getChannelMessagesUUID()).doesNotContain(created.getId());
	}

	@Test
	@Order(9)
	@DisplayName("9. Basic ChannelMessageDelete 메시지없음 예외")
	void testDeleteMessage_MessageNotFound() {
		// given
		UUID nonExistentMessageId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.deleteMessage(nonExistentMessageId, testUser.getId()))
			.isInstanceOf(MessageNotFoundException.class);
	}

	@Test
	@Order(10)
	@DisplayName("10. Basic ChannelMessageDelete 권한없음 예외")
	void testDeleteMessage_UnauthorizedAccess() {
		// given
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "홍길동의 메시지");
		List<Message> messages = messageRepository.findByChannelId(testChannel.getId());
		Message created = messages.get(0);

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.deleteMessage(created.getId(), otherUser.getId()))
			.isInstanceOf(UnauthorizedMessageAccessException.class);
	}

	@Test
	@Order(11)
	@DisplayName("11. Basic ChannelMessageDeleteChannelWithMessages")
	void testDeleteChannelWithMessages() {
		// given
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "메시지1");
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "메시지2");
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "메시지3");

		// when
		channelMessageService.deleteChannelWithMessages(testChannel.getId());

		// then
		List<Message> remainingMessages = messageRepository.findByChannelId(testChannel.getId());
		assertThat(remainingMessages).isEmpty();

		assertThatThrownBy(() -> channelService.getChannelByUUID(testChannel.getId()))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(12)
	@DisplayName("12. Basic ChannelMessageDeleteChannelWithMessages 채널없음 예외")
	void testDeleteChannelWithMessages_ChannelNotFound() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.deleteChannelWithMessages(nonExistentChannelId))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(13)
	@DisplayName("13. Basic ChannelMessage 복수채널 메시지 작성자별 조회")
	void testGetMessageByAuthor_MultipleChannels() {
		// given
		Channel otherChannel = channelService.createChannel("개발팀");
		channelService.joinChannel(testUser, "개발팀");
		channelService.joinChannel(otherUser, "일반");
		channelService.joinChannel(otherUser, "개발팀");

		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "일반채널 홍길동 메시지");
		channelMessageService.createMessage(testUser.getId(), otherChannel.getId(), "개발팀채널 홍길동 메시지");
		channelMessageService.createMessage(otherUser.getId(), testChannel.getId(), "일반채널 김길동 메시지");

		// when
		List<Message> generalChannelMessages = channelMessageService.getMessageByAuthor("홍길동", testChannel.getId());
		List<Message> devChannelMessages = channelMessageService.getMessageByAuthor("홍길동", otherChannel.getId());

		// then
		assertThat(generalChannelMessages).hasSize(1);
		assertThat(generalChannelMessages.get(0).getText()).isEqualTo("일반채널 홍길동 메시지");

		assertThat(devChannelMessages).hasSize(1);
		assertThat(devChannelMessages.get(0).getText()).isEqualTo("개발팀채널 홍길동 메시지");
	}
}