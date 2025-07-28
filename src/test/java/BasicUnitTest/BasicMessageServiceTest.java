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
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelMessageService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

@DisplayName("BasicMessageService 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicMessageServiceTest {

	private MessageRepository messageRepository;
	private UserRepository userRepository;
	private ChannelRepository channelRepository;
	private BasicUserService userService;
	private BasicChannelService channelService;
	private BasicChannelMessageService channelMessageService;
	private BasicMessageService messageService;

	private User testUser;
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
		messageService = new BasicMessageService(messageRepository, channelMessageService);

		testUser = userService.createUser("test1", "password1", "홍길동");
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
	@DisplayName("1. Basic MessageCreate")
	void testCreateMessage() {
		// given

		// when
		boolean result = messageService.createMessage(testUser.getId(), testChannel.getId(), "안녕하세요");

		// then
		assertThat(result).isTrue();

		List<Message> messages = messageRepository.findByChannelId(testChannel.getId());
		assertThat(messages).hasSize(1);
		assertThat(messages.get(0).getText()).isEqualTo("안녕하세요");
		assertThat(messages.get(0).getAuthorUUID()).isEqualTo(testUser.getId());
	}

	@Test
	@Order(2)
	@DisplayName("2. Basic MessageGetById")
	void testGetMessage() {
		// given
		messageService.createMessage(testUser.getId(), testChannel.getId(), "테스트 메시지");
		List<Message> messages = messageRepository.findAll();
		Message created = messages.get(0);

		// when
		Message result = messageService.getMessage(created.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(created.getId());
		assertThat(result.getText()).isEqualTo("테스트 메시지");
		assertThat(result.getAuthorUUID()).isEqualTo(testUser.getId());
	}

	@Test
	@Order(3)
	@DisplayName("3. Basic MessageGetById 메시지없음 예외")
	void testGetMessage_MessageNotFound() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> messageService.getMessage(nonExistentId))
			.isInstanceOf(MessageNotFoundException.class);
	}

	@Test
	@Order(4)
	@DisplayName("4. Basic MessageGetAll")
	void testGetAllMessages() {
		// given
		messageService.createMessage(testUser.getId(), testChannel.getId(), "메시지1");
		messageService.createMessage(testUser.getId(), testChannel.getId(), "메시지2");
		messageService.createMessage(testUser.getId(), testChannel.getId(), "메시지3");

		// when
		List<Message> result = messageService.getAllMessages();

		// then
		assertThat(result)
			.isNotNull()
			.hasSize(3);
		assertThat(result)
			.extracting(Message::getText)
			.containsExactlyInAnyOrder("메시지1", "메시지2", "메시지3");
	}

	@Test
	@Order(5)
	@DisplayName("5. Basic MessageGetByAuthor")
	void testGetMessageByAuthor() {
		// given
		User otherUser = userService.createUser("test2", "password2", "김길동");
		channelService.joinChannel(otherUser, "일반");

		messageService.createMessage(testUser.getId(), testChannel.getId(), "홍길동 메시지1");
		messageService.createMessage(testUser.getId(), testChannel.getId(), "홍길동 메시지2");
		messageService.createMessage(otherUser.getId(), testChannel.getId(), "김길동 메시지1");

		// when
		List<Message> result = messageService.getMessageByAuthor("홍길동", testChannel.getId());

		// then
		assertThat(result)
			.isNotNull()
			.hasSize(2);
		assertThat(result)
			.extracting(Message::getText)
			.containsExactlyInAnyOrder("홍길동 메시지1", "홍길동 메시지2");
	}

	@Test
	@Order(6)
	@DisplayName("6. Basic MessageGetByChannel")
	void testGetMessageByChannel() {
		// given
		Channel otherChannel = channelService.createChannel("개발팀");
		channelService.joinChannel(testUser, "개발팀");

		messageService.createMessage(testUser.getId(), testChannel.getId(), "일반 메시지1");
		messageService.createMessage(testUser.getId(), testChannel.getId(), "일반 메시지2");
		messageService.createMessage(testUser.getId(), otherChannel.getId(), "개발팀 메시지1");

		// when
		List<Message> result = messageService.getMessageByChannel(testChannel.getId());

		// then
		assertThat(result)
			.isNotNull()
			.hasSize(2);
		assertThat(result)
			.extracting(Message::getText)
			.containsExactlyInAnyOrder("일반 메시지1", "일반 메시지2");
	}

	@Test
	@Order(7)
	@DisplayName("7. Basic MessageUpdate")
	void testUpdateMessage() throws InterruptedException {
		// given
		messageService.createMessage(testUser.getId(), testChannel.getId(), "원본 메시지");
		List<Message> messages = messageRepository.findAll();
		Message created = messages.get(0);

		Thread.sleep(1000);

		// when

		boolean result = messageService.updateMessage(created.getId(), testUser.getId(), "수정된 메시지");

		// then
		assertThat(result).isTrue();

		Message updated = messageRepository.findById(created.getId());
		assertThat(updated.getText()).isEqualTo("수정된 메시지");
		assertThat(updated.getUpdatedAt()).isNotEqualTo(updated.getCreatedAt());
	}

	@Test
	@Order(8)
	@DisplayName("8. Basic MessageUpdate 메시지없음 예외")
	void testUpdateMessage_MessageNotFound() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> messageService.updateMessage(nonExistentId, testUser.getId(), "수정된 메시지"))
			.isInstanceOf(MessageNotFoundException.class);
	}

	@Test
	@Order(9)
	@DisplayName("9. Basic MessageUpdate 권한없음 예외")
	void testUpdateMessage_UnauthorizedAccess() {
		// given
		User otherUser = userService.createUser("test2", "password2", "김길동");
		messageService.createMessage(testUser.getId(), testChannel.getId(), "홍길동의 메시지");
		List<Message> messages = messageRepository.findAll();
		Message created = messages.get(0);

		// when

		// then
		assertThatThrownBy(() -> messageService.updateMessage(created.getId(), otherUser.getId(), "다른사람이 수정"))
			.isInstanceOf(UnauthorizedMessageAccessException.class);
	}

	@Test
	@Order(10)
	@DisplayName("10. Basic MessageDelete")
	void testDeleteMessage() {
		// given
		messageService.createMessage(testUser.getId(), testChannel.getId(), "삭제될 메시지");
		List<Message> messages = messageRepository.findAll();
		Message created = messages.get(0);

		// when
		messageService.deleteMessage(created.getId(), testUser.getId());

		// then
		assertThatThrownBy(() -> messageService.getMessage(created.getId()))
			.isInstanceOf(MessageNotFoundException.class);

		Channel updatedChannel = channelRepository.findById(testChannel.getId());
		assertThat(updatedChannel.getChannelMessagesUUID()).doesNotContain(created.getId());
	}

	@Test
	@Order(11)
	@DisplayName("11. Basic MessageDelete 메시지없음 예외")
	void testDeleteMessage_MessageNotFound() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> messageService.deleteMessage(nonExistentId, testUser.getId()))
			.isInstanceOf(MessageNotFoundException.class);
	}

	@Test
	@Order(12)
	@DisplayName("12. Basic MessageDelete 권한없음 예외")
	void testDeleteMessage_UnauthorizedAccess() {
		// given
		User otherUser = userService.createUser("test2", "password2", "김길동");
		messageService.createMessage(testUser.getId(), testChannel.getId(), "홍길동의 메시지");
		List<Message> messages = messageRepository.findAll();
		Message created = messages.get(0);

		// when

		// then
		assertThatThrownBy(() -> messageService.deleteMessage(created.getId(), otherUser.getId()))
			.isInstanceOf(UnauthorizedMessageAccessException.class);
	}
}
