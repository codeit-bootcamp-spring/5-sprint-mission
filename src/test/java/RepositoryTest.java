import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

@DisplayName("Repository 테스트")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class RepositoryTest {

	@Nested
	@TestMethodOrder(MethodOrderer.DisplayName.class)
	@DisplayName("JCF Repository 테스트")
	class JCFRepositoryTest {
		protected UserRepository userRepository;
		protected ChannelRepository channelRepository;
		protected MessageRepository messageRepository;

		protected User testUser;
		protected Channel testChannel;
		protected Message testMessage;

		@BeforeEach
		void setUp() {
			// Repository들 생성
			userRepository = new JCFUserRepository();
			channelRepository = new JCFChannelRepository();
			messageRepository = new JCFMessageRepository();

			// 테스트 데이터 생성
			testUser = new User("test1", "password1", "홍길동");
			testChannel = new Channel("일반");
			testMessage = new Message(testUser.getId(), testChannel.getId(), "안녕하세요");
		}

		@Nested
		@TestMethodOrder(MethodOrderer.DisplayName.class)
		@DisplayName("1. JCF UserRepository")
		class JCFUserRepositoryTest {

			@Test
			@DisplayName("1. save 테스트")
			void testUserSave() {
				// given


				// when
				userRepository.save(testUser);


				// then
				User user = userRepository.findById(testUser.getId());
				assertNotNull(user);
				assertEquals(testUser.getLoginId(), user.getLoginId());
				assertEquals(testUser.getDefaultNickname(), user.getDefaultNickname());
			}

			@Test
			@DisplayName("2. findById 테스트")
			void testUserFindById() {
				// given
				userRepository.save(testUser);

				// when
				User user = userRepository.findById(testUser.getId());

				// then
				assertNotNull(user);
				assertEquals(testUser.getId(), user.getId());
			}

			@Test
			@DisplayName("3. findByLoginId 테스트")
			void testUserFindByLoginId() {
				// given
				userRepository.save(testUser);

				// when
				User user = userRepository.findByLoginId(testUser.getLoginId());

				// then
				assertNotNull(user);
				assertEquals(testUser.getLoginId(), user.getLoginId());
			}

			@Test
			@DisplayName("4. findAll 테스트")
			void testFindAll(){
				// given
				User testUser2 = new User("test2", "password2", "박길동");
				User testUser3 = new User("test3", "password3", "김길동");
				userRepository.save(testUser);
				userRepository.save(testUser2);
				userRepository.save(testUser3);

				// when
				List<User> users =  userRepository.findAll();

				// then
				assertEquals(3, users.size());
				assertThat(users)
					.extracting(User::getDefaultNickname)
					.isEqualTo(List.of("김길동", "박길동", "홍길동"));

			}

			@Test
			@DisplayName("5. existsByLoginId")
			void testExistsByLoginId() {
				// given
				userRepository.save(testUser);

				// when
				boolean exists = userRepository.existsByLoginId(testUser.getLoginId());

				// then
				assertTrue(exists);
			}

			@Test
			@DisplayName("6. existsByLoginId 실패용")
			void testExistsByLoginId_fail() {
				// given
				userRepository.save(testUser);

				// when
				boolean exists = userRepository.existsByLoginId("fail");

				// then
				assertFalse(exists);
			}

			@Test
			@DisplayName("7. deleteById 테스트")
			void testDeleteById() {
				// given
				userRepository.save(testUser);

				// when
				userRepository.deleteById(testUser.getId());

				// then
				assertNull(userRepository.findById(testUser.getId()));
				assertFalse(userRepository.existsByLoginId("test1"));
			}

			@Test
			@DisplayName("8. deleteByLoginId")
			void testUserDeleteByLoginId() {
				// given
				userRepository.save(testUser);
				assertTrue(userRepository.existsByLoginId("test1"));

				// when
				userRepository.deleteByLoginId("test1");

				// then
				assertNull(userRepository.findById(testUser.getId()));
				assertFalse(userRepository.existsByLoginId("test1"));
			}
		}

		// assertj 써보기
		@Nested
		@TestMethodOrder(MethodOrderer.DisplayName.class)
		@DisplayName("2. JCF ChannelRepository")
		class JCFChannelRepositoryTest {

			@Test
			@DisplayName("1. save")
			void testChannelSave() {
				// given

				// when
				channelRepository.save(testChannel);

				// then
				Channel ch = channelRepository.findById(testChannel.getId());
				assertThat(ch).isNotNull();
				assertThat(ch.getChannelName()).isEqualTo(testChannel.getChannelName());
			}

			@Test
			@DisplayName("2. ChannelFindById")
			void testChannelFindById() {
				// given
				channelRepository.save(testChannel);

				// when
				Channel ch = channelRepository.findById(testChannel.getId());

				// then
				assertThat(ch).isNotNull();
				assertThat(ch.getId()).isEqualTo(testChannel.getId());
				assertThat(ch.getChannelName()).isEqualTo(testChannel.getChannelName());
			}

			@Test
			@DisplayName("3. ChannelFindByName")
			void testChannelFindByName() {
				// given
				channelRepository.save(testChannel);

				// when
				Channel ch = channelRepository.findByName("일반");

				// then
				assertThat(ch).isNotNull();
				assertThat(ch.getId()).isEqualTo(testChannel.getId());
				assertThat(ch.getChannelName()).isEqualTo(testChannel.getChannelName());
			}

			@Test
			@DisplayName("4. ChannelFindByIdNotFound")
			void testChannelFindByIdNotFound() {
				// given

				// when
				Channel ch = channelRepository.findById(testChannel.getId());
				// then
				assertThat(ch).isNull();
			}

			@Test
			@DisplayName("5. ChannelFindAll")
			void  testChannelFindAll() {
				// given
				Channel channel1 = new Channel("코딩");
				Channel channel2 = new Channel("취업");
				channelRepository.save(testChannel);
				channelRepository.save(channel1);
				channelRepository.save(channel2);

				// when
				List<Channel> channels = channelRepository.findAll();
				// then
				assertThat(channels).hasSize(3);
				assertThat(channels)
					.extracting(Channel::getChannelName)
					.isEqualTo(List.of("일반", "취업", "코딩"));
			}

			@Test
			@DisplayName("6. ChannelFindAll 정렬 실패 테스트")
			void  testChannelFindAll_sorted_fail() {
				// given
				Channel channel1 = new Channel("코딩");
				Channel channel2 = new Channel("취업");
				channelRepository.save(testChannel);
				channelRepository.save(channel1);
				channelRepository.save(channel2);

				// when
				List<Channel> channels = channelRepository.findAll();
				// then
				assertThat(channels).hasSize(3);
				assertThat(channels)
					.extracting(Channel::getChannelName)
					.doesNotContainSequence("코딩", "일반", "취업");
			}

			@Test
			@DisplayName("7. ChannelExistsByName")
			void   testChannelExistsByName() {
				// given
				channelRepository.save(testChannel);
				// when
				boolean t = channelRepository.existsByName("일반");
				boolean f = channelRepository.existsByName("존재하지않는채널");
				// then
				assertThat(t).isTrue();
				assertThat(f).isFalse();

			}

			@Test
			@DisplayName("8. ChannelDeleteById")
			void  testChannelDeleteById() {
				// given
				channelRepository.save(testChannel);
				assertThat(channelRepository.existsByName("일반")).isTrue();

				// when
				channelRepository.deleteById(testChannel.getId());

				// then
				assertThat(channelRepository.findById(testChannel.getId())).isNull();
				assertThat(channelRepository.existsByName("일반")).isFalse();
			}

			@Test
			@DisplayName("9. ChannelDeleteByName")
			void testChannelDeleteByName() {
				// given
				channelRepository.save(testChannel);
				assertThat(channelRepository.existsByName("일반")).isTrue();

				// when
				channelRepository.deleteByName("일반");

				// then
				assertThat(channelRepository.findByName("일반")).isNull();
				assertThat(channelRepository.existsByName("일반")).isFalse();
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.DisplayName.class)
		@DisplayName("3. JCF MessageRepository")
		class JCFMessageRepositoryTest {
			@Test
			@DisplayName("1. MessageSave")
			void testMessageSave() {
				// when
				messageRepository.save(testMessage);

				// then
				Message msg = messageRepository.findById(testMessage.getId());
				assertThat(msg).isNotNull();
				assertThat(msg.getText()).isEqualTo(testMessage.getText());
				assertThat(msg.getAuthorUUID()).isEqualTo(testMessage.getAuthorUUID());
				assertThat(msg.getChannelUUID()).isEqualTo(testMessage.getChannelUUID());
			}

			@Test
			@DisplayName("2. MessageFindById")
			void testMessageFindById() {
				// given
				messageRepository.save(testMessage);

				// when
				Message msg = messageRepository.findById(testMessage.getId());

				// then
				assertThat(msg).isNotNull();
				assertThat(msg.getId()).isEqualTo(testMessage.getId());
				assertThat(msg.getText()).isEqualTo(testMessage.getText());
			}

			@Test
			@DisplayName("3. MessageFindByIdNotFound")
			void testMessageFindByIdNotFound() {
				// when
				Message msg = messageRepository.findById(java.util.UUID.randomUUID());

				// then
				assertThat(msg).isNull();
			}

			@Test
			@DisplayName("4. MessageFindAll")
			void testMessageFindAll() {
				// given
				Message msg1 = new Message(testUser.getId(), testChannel.getId(), "첫번째");
				Message msg2 = new Message(testUser.getId(), testChannel.getId(), "두번째");
				messageRepository.save(testMessage);  // 안녕하세요
				messageRepository.save(msg1);     // 첫 번째 메시지
				messageRepository.save(msg2);     // 두 번째 메시지

				// when
				java.util.List<Message> msgs = messageRepository.findAll();

				// then
				assertThat(msgs).hasSize(3);
				assertThat(msgs)
					.extracting(Message::getText)
					.contains("안녕하세요", "첫번째", "두번째");
			}

			@Test
			@DisplayName("5. MessageFindByChannelId")
			void testMessageFindByChannelId() {
				// given
				java.util.UUID otherChannelId = java.util.UUID.randomUUID();
				Message msg1 = new Message(testUser.getId(), testChannel.getId(), "일반 채널 메시지");
				Message msg2 = new Message(testUser.getId(), otherChannelId, "코딩 채널 메시지");
				messageRepository.save(msg1);
				messageRepository.save(msg2);

				// when
				java.util.List<Message> channelMessages = messageRepository.findByChannelId(testChannel.getId());

				// then
				assertThat(channelMessages).hasSize(1);
				assertThat(channelMessages.get(0).getText()).isEqualTo("일반 채널 메시지");
				assertThat(channelMessages.get(0).getChannelUUID()).isEqualTo(testChannel.getId());
			}

			@Test
			@DisplayName("6. 빈 채널의 메시지 조회")
			void testMessageFindByChannelId_empty() {
				// given
				java.util.UUID emptyChannelId = java.util.UUID.randomUUID();

				// when
				java.util.List<Message> msgs = messageRepository.findByChannelId(emptyChannelId);

				// then
				assertThat(msgs).isEmpty();
			}

			@Test
			@DisplayName("7. MessageDeleteById")
			void testMessageDeleteById() {
				// given
				messageRepository.save(testMessage);
				assertThat(messageRepository.findById(testMessage.getId())).isNotNull();

				// when
				messageRepository.deleteById(testMessage.getId());

				// then
				assertThat(messageRepository.findById(testMessage.getId())).isNull();
			}

			@Test
			@DisplayName("8. MessageFindByChannelId_multiple_channels")
			void testMessageFindByChannelId_multiple_channels() {
				// given
				Channel channel2 = new Channel("코딩");
				java.util.UUID channel2Id = channel2.getId();

				Message msg1 = new Message(testUser.getId(), testChannel.getId(), "일반채널 메시지1");
				Message msg2 = new Message(testUser.getId(), testChannel.getId(), "일반채널 메시지2");
				Message msg3 = new Message(testUser.getId(), channel2Id, "코딩채널 메시지1");

				messageRepository.save(msg1);
				messageRepository.save(msg2);
				messageRepository.save(msg3);

				// when
				java.util.List<Message> generalMessages = messageRepository.findByChannelId(testChannel.getId());
				java.util.List<Message> codingMessages = messageRepository.findByChannelId(channel2Id);

				// then
				assertThat(generalMessages).hasSize(2);
				assertThat(codingMessages).hasSize(1);

				assertThat(generalMessages)
					.extracting(Message::getText)
					.containsExactlyInAnyOrder("일반채널 메시지1", "일반채널 메시지2");

				assertThat(codingMessages.get(0).getText()).isEqualTo("코딩채널 메시지1");
			}

		}
	}

	@Nested
	@TestMethodOrder(MethodOrderer.DisplayName.class)
	@DisplayName("File Repository 테스트")
	class FileRepositoryTest {
		protected UserRepository userRepository;
		protected ChannelRepository channelRepository;
		protected MessageRepository messageRepository;

		protected User testUser;
		protected Channel testChannel;
		protected Message testMessage;

		@BeforeEach
		void setUp() {
			// 파일 정리 추가해야함
			// cleanDirectory();

			userRepository = new FileUserRepository();
			channelRepository = new FileChannelRepository();
			messageRepository = new FileMessageRepository();

			testUser = new User("test10", "password10", "홍길동");
			testChannel = new Channel("일반");
			testMessage = new Message(testUser.getId(), testChannel.getId(), "안녕하세요");
		}

		@Nested
		@DisplayName("File UserRepository")
		class FileUserRepositoryTest {

		}

		@Nested
		@DisplayName("File ChannelRepository")
		class FileChannelRepositoryTest {

		}

		@Nested
		@DisplayName("File MessageRepository")
		class FileMessageRepositoryTest {

		}
	}
}
