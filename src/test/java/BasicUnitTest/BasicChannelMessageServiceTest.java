package BasicUnitTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.util.List;
import java.util.Optional;
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
	void setUp에_정의된_더미값으로_메시지_생성후_저장시_성공() {
		// given

		// when
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "안녕하세요");

		// then
		List<Message> messages = messageRepository.findByChannelId(testChannel.getId());
		assertThat(messages).hasSize(1);
		assertThat(messages.get(0).getText()).isEqualTo("안녕하세요");
		assertThat(messages.get(0).getAuthorUUID()).isEqualTo(testUser.getId());
		assertThat(messages.get(0).getChannelUUID()).isEqualTo(testChannel.getId());

		Optional<Channel> updatedChannelOpt = channelRepository.findById(testChannel.getId());
		assertThat(updatedChannelOpt).isPresent();
		Channel updatedChannel = updatedChannelOpt.get();
		assertThat(updatedChannel.getChannelMessagesUUID()).contains(messages.get(0).getId());
	}

	@Test
	@Order(2)
	void 존재_하지_않는_User로_메시지_생성시_실패() {
		// given
		UUID nonExistentUserId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.createMessage(nonExistentUserId, testChannel.getId(), "메시지"))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(3)
	void 존재_하지_않는_채널에_메시지_생성시_실패() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.createMessage(testUser.getId(), nonExistentChannelId, "메시지"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(4)
	void 멤버가_아닌_채널에_메시지_작성시_실패() {
		// given

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.createMessage(otherUser.getId(), testChannel.getId(), "메시지"))
			.isInstanceOf(NotChannelMemberException.class);
	}

	@Test
	@Order(5)
	void 채널에_작성된_메시지중_작성자로_조회시_모든_메시지_반환_후_성공() {
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
	void 존재_하지_않는_채널에_작성된_메시지중_작성자로_조회시_실패() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.getMessageByAuthor("홍길동", nonExistentChannelId))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(7)
	void 존재_하지_않는_작성자명으로_조회시_실패() {
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
	void message_id로_message_삭제시_성공() {
		// given
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "삭제될 메시지");
		List<Message> messages = messageRepository.findByChannelId(testChannel.getId());
		Message created = messages.get(0);

		// when
		channelMessageService.deleteMessage(created.getId(), testUser.getId());

		// then
		Optional<Message> deletedMessageOpt = messageRepository.findById(created.getId());
		assertThat(deletedMessageOpt).isEmpty();

		Optional<Channel> updatedChannelOpt = channelRepository.findById(testChannel.getId());
		assertThat(updatedChannelOpt).isPresent();
		Channel updatedChannel = updatedChannelOpt.get();
		assertThat(updatedChannel.getChannelMessagesUUID()).doesNotContain(created.getId());
	}

	@Test
	@Order(9)
	void 존재_하지_않는_message_UUID로_삭제시_실패() {
		// given
		UUID nonExistentMessageId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.deleteMessage(nonExistentMessageId, testUser.getId()))
			.isInstanceOf(MessageNotFoundException.class);
	}

	@Test
	@Order(10)
	void 다른_User가_메시지를_삭제_시도_시_AuthorUUID로_판별하여_실패() {
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
	void 채널_삭제시_메시지도_같이_삭제되었으면_성공() {
		// given
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "메시지1");
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "메시지2");
		channelMessageService.createMessage(testUser.getId(), testChannel.getId(), "메시지3");

		// when
		channelMessageService.deleteChannelWithMessages(testChannel.getId());

		// then
		List<Message> remainingMessages = messageRepository.findByChannelId(testChannel.getId());
		assertThat(remainingMessages).isEmpty();

		Optional<Channel> deletedChannelOpt = channelRepository.findById(testChannel.getId());
		assertThat(deletedChannelOpt).isEmpty();
	}

	@Test
	@Order(12)
	void 존재_하지_않는_채널을_삭제시_실패() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelMessageService.deleteChannelWithMessages(nonExistentChannelId))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(13)
	void 채널이_여러개일때_개별_조회시_개별적으로_조회된다면_성공() {
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