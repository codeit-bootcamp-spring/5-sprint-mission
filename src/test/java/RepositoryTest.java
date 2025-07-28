import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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
		public UserRepository userRepository;
		public ChannelRepository channelRepository;
		public MessageRepository messageRepository;

		public User testUser;
		public Channel testChannel;
		public Message testMessage;

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
	@DisplayName("2. File Repository 테스트")
	class FileRepositoryTest {
		public UserRepository userRepository;
		public ChannelRepository channelRepository;
		public MessageRepository messageRepository;

		public User testUser;
		public Channel testChannel;
		public Message testMessage;

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

		@BeforeEach
		void setUp() {
			File dir = new File("data/");
			if (dir.exists()) {
				cleanDirectory(dir);
			}
			cleanDirectory(dir);

			userRepository = new FileUserRepository();
			channelRepository = new FileChannelRepository();
			messageRepository = new FileMessageRepository();

			testUser = new User("test10", "password10", "홍길동");
			testChannel = new Channel("일반");
			testMessage = new Message(testUser.getId(), testChannel.getId(), "안녕하세요");
		}

		@Nested
		@TestMethodOrder(MethodOrderer.DisplayName.class)
		@DisplayName("1. File UserRepository")
		class FileUserRepositoryTest {
			@Test
			@DisplayName("01. File save")
			void testFileUserSave() {
				// given

				// when
				userRepository.save(testUser);

				// then
				User user = userRepository.findById(testUser.getId());
				assertNotNull(user);
				assertEquals(testUser.getLoginId(), user.getLoginId());
				assertEquals(testUser.getDefaultNickname(), user.getDefaultNickname());

				// 파일 다시 읽기
				UserRepository reloadRepo = new FileUserRepository();
				User reloadUser = reloadRepo.findById(testUser.getId());
				assertNotNull(reloadUser);
				assertEquals(testUser.getLoginId(), reloadUser.getLoginId());
				assertEquals(testUser.getDefaultNickname(), reloadUser.getDefaultNickname());
			}

			@Test
			@DisplayName("02. File findById")
			void testFileUserFindById() {
				// given
				userRepository.save(testUser);

				// when
				User user = userRepository.findById(testUser.getId());

				// then
				assertNotNull(user);
				assertEquals(testUser.getId(), user.getId());
			}

			@Test
			@DisplayName("03. File findByLoginId")
			void testFileUserFindByLoginId() {
				// given
				userRepository.save(testUser);

				// when
				User user = userRepository.findByLoginId(testUser.getLoginId());

				// then
				assertNotNull(user);
				assertEquals(testUser.getLoginId(), user.getLoginId());
			}

			@Test
			@DisplayName("04. File findAll")
			void testFileFindAll(){
				// given
				User testUser2 = new User("test11", "password11", "박길동");
				User testUser3 = new User("test12", "password12", "김길동");
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

				UserRepository reloadRepo = new FileUserRepository();
				List<User> reloadUsers = reloadRepo.findAll();
				assertEquals(3, reloadUsers.size());
				assertThat(reloadUsers)
					.extracting(User::getDefaultNickname)
					.isEqualTo(List.of("김길동", "박길동", "홍길동"));
			}

			@Test
			@DisplayName("05. File existsByLoginId")
			void testFileExistsByLoginId() {
				// given
				userRepository.save(testUser);

				// when
				boolean exists = userRepository.existsByLoginId(testUser.getLoginId());

				// then
				assertTrue(exists);
			}

			@Test
			@DisplayName("06. File existsByLoginId 실패용")
			void testFileExistsByLoginId_fail() {
				// given
				userRepository.save(testUser);

				// when
				boolean exists = userRepository.existsByLoginId("fail");

				// then
				assertFalse(exists);
			}

			@Test
			@DisplayName("07. File existsByLoginId")
			void testFileExistsByLoginId_null() {
				// when
				boolean exists = userRepository.existsByLoginId(null);

				// then
				assertTrue(exists);
			}

			@Test
			@DisplayName("08. File deleteById")
			void testFileDeleteById() {
				// given
				userRepository.save(testUser);

				// when
				userRepository.deleteById(testUser.getId());

				// then
				assertNull(userRepository.findById(testUser.getId()));
				assertFalse(userRepository.existsByLoginId("test10"));

				UserRepository reloadRepo = new FileUserRepository();
				assertNull(reloadRepo.findById(testUser.getId()));
				assertFalse(reloadRepo.existsByLoginId("test10"));
			}

			@Test
			@DisplayName("09. File deleteByLoginId")
			void testFileDeleteByLoginId() {
				// given
				userRepository.save(testUser);
				assertTrue(userRepository.existsByLoginId("test10"));

				// when
				userRepository.deleteByLoginId("test10");

				// then
				assertNull(userRepository.findById(testUser.getId()));
				assertFalse(userRepository.existsByLoginId("test10"));
			}

			@Test
			@DisplayName("10. File Read Users")
			void testFileReadUsers() {
				// given
				userRepository.save(testUser);

				// when
				UserRepository reloadRepo = new FileUserRepository();

				// then
				User reloadUser = reloadRepo.findById(testUser.getId());
				assertThat(reloadUser).isNotNull();
				assertThat(reloadUser.getLoginId()).isEqualTo(testUser.getLoginId());
				assertThat(reloadUser.getDefaultNickname()).isEqualTo(testUser.getDefaultNickname());
				assertThat(reloadUser.getPassword()).isEqualTo(testUser.getPassword());
			}

			@Test
			@DisplayName("11. File Read loginIdMapping")
			void testFileReadLoginIdMapping() {
				// given
				userRepository.save(testUser);

				// when
				UserRepository reloadRepo = new FileUserRepository();

				// then
				User reloadUser = reloadRepo.findByLoginId(testUser.getLoginId());
				assertThat(reloadUser).isNotNull();
				assertThat(reloadUser.getId()).isEqualTo(testUser.getId());
				assertThat(reloadUser.getDefaultNickname()).isEqualTo(testUser.getDefaultNickname());
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.DisplayName.class)
		@DisplayName("2. File ChannelRepository")
		class FileChannelRepositoryTest {
			@Test
			@DisplayName("01. File ChannelSave")
			void testFileChannelSave() {
				// given

				// when
				channelRepository.save(testChannel);

				// then
				Channel ch = channelRepository.findById(testChannel.getId());
				assertThat(ch).isNotNull();
				assertThat(ch.getChannelName()).isEqualTo(testChannel.getChannelName());

				ChannelRepository reloadRepo = new FileChannelRepository();
				Channel reloadChannel = reloadRepo.findById(testChannel.getId());
				assertThat(reloadChannel).isNotNull();
				assertThat(reloadChannel.getChannelName()).isEqualTo(testChannel.getChannelName());
			}

			@Test
			@DisplayName("02. File Channel findById")
			void testFileChannelFindById() {
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
			@DisplayName("03. File Channel findByName")
			void testFileChannelFindByName() {
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
			@DisplayName("04. File Channel findByIdNotFound")
			void testFileChannelFindByIdNotFound() {
				// given

				// when
				Channel ch = channelRepository.findById(testChannel.getId());
				// then
				assertThat(ch).isNull();
			}

			@Test
			@DisplayName("05. File Channel findAll")
			void  testFileChannelFindAll() {
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

				ChannelRepository reloadRepo = new FileChannelRepository();
				List<Channel> reloadChannels = reloadRepo.findAll();
				assertThat(reloadChannels).hasSize(3);
				assertThat(reloadChannels)
					.extracting(Channel::getChannelName)
					.isEqualTo(List.of("일반", "취업", "코딩"));
			}

			@Test
			@DisplayName("06. File Channel findAll 정렬 실패")
			void  testFileChannelFindAll_sorted_fail() {
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
			@DisplayName("07. File Channel existsByName")
			void   testFileChannelExistsByName() {
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
			@DisplayName("08. File Channel deleteById")
			void  testFileChannelDeleteById() {
				// given
				channelRepository.save(testChannel);
				assertThat(channelRepository.existsByName("일반")).isTrue();

				// when
				channelRepository.deleteById(testChannel.getId());

				// then
				assertThat(channelRepository.findById(testChannel.getId())).isNull();
				assertThat(channelRepository.existsByName("일반")).isFalse();

				ChannelRepository reloadRepo = new FileChannelRepository();
				assertThat(reloadRepo.findById(testChannel.getId())).isNull();
				assertThat(reloadRepo.existsByName("일반")).isFalse();
			}

			@Test
			@DisplayName("09. File Channel deleteByName")
			void testFileChannelDeleteByName() {
				// given
				channelRepository.save(testChannel);
				assertThat(channelRepository.existsByName("일반")).isTrue();

				// when
				channelRepository.deleteByName("일반");

				// then
				assertThat(channelRepository.findByName("일반")).isNull();
				assertThat(channelRepository.existsByName("일반")).isFalse();
			}

			@Test
			@DisplayName("10. File Read channels")
			void testFileReadChannels() {
				// given
				channelRepository.save(testChannel);

				// when
				ChannelRepository reloadRepo = new FileChannelRepository();

				// then
				Channel reloadChannel = reloadRepo.findById(testChannel.getId());
				assertThat(reloadChannel).isNotNull();
				assertThat(reloadChannel.getChannelName()).isEqualTo(testChannel.getChannelName());
				assertThat(reloadChannel.getId()).isEqualTo(testChannel.getId());
			}

			@Test
			@DisplayName("11. File Read channelNameMapping")
			void testFileReadChannelNameMapping() {
				// given
				channelRepository.save(testChannel);

				// when
				ChannelRepository reloadRepo = new FileChannelRepository();

				// then
				Channel reloadChannel = reloadRepo.findByName(testChannel.getChannelName());
				assertThat(reloadChannel).isNotNull();
				assertThat(reloadChannel.getId()).isEqualTo(testChannel.getId());
				assertThat(reloadRepo.existsByName(testChannel.getChannelName())).isTrue();
			}

		}

		// 정렬을 OrderAnnotation로 해보기
		@Nested
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		@DisplayName("3. File MessageRepository")
		class FileMessageRepositoryTest {

			@Test
			@Order(1)
			@DisplayName("1. File MessageSave")
			void testFileMessageSave() {
				// given
				messageRepository.save(testMessage);
				// when
				Message msg = messageRepository.findById(testMessage.getId());
				assertThat(msg).isNotNull();
				assertThat(msg.getText()).isEqualTo(testMessage.getText());
				assertThat(msg.getAuthorUUID()).isEqualTo(testMessage.getAuthorUUID());
				assertThat(msg.getChannelUUID()).isEqualTo(testMessage.getChannelUUID());

				// 파일 다시 읽기
				MessageRepository reloadRepo = new FileMessageRepository();
				Message reloadMessage = reloadRepo.findById(testMessage.getId());
				assertThat(reloadMessage).isNotNull();
				assertThat(reloadMessage.getText()).isEqualTo(testMessage.getText());
				assertThat(reloadMessage.getAuthorUUID()).isEqualTo(testMessage.getAuthorUUID());
				assertThat(reloadMessage.getChannelUUID()).isEqualTo(testMessage.getChannelUUID());
			}

			@Test
			@Order(2)
			@DisplayName("2. File MessageFindById")
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
			@Order(3)
			@DisplayName("3. File MessageFindByIdNotFound")
			void testMessageFindByIdNotFound() {
				// when
				Message msg = messageRepository.findById(java.util.UUID.randomUUID());

				// then
				assertThat(msg).isNull();
			}

			@Test
			@Order(4)
			@DisplayName("4. File MessageFindAll")
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

				// 파일 다시 읽기
				MessageRepository reloadRepo = new FileMessageRepository();
				List<Message> reloadMessages = reloadRepo.findAll();
				assertThat(reloadMessages).hasSize(3);
				assertThat(reloadMessages)
					.extracting(Message::getText)
					.contains("안녕하세요", "첫번째", "두번째");
			}

			@Test
			@Order(5)
			@DisplayName("5. File MessageFindByChannelId")
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
			@Order(6)
			@DisplayName("6. File Message 빈 채널의 메시지 조회")
			void testMessageFindByChannelId_empty() {
				// given
				java.util.UUID emptyChannelId = java.util.UUID.randomUUID();

				// when
				java.util.List<Message> msgs = messageRepository.findByChannelId(emptyChannelId);

				// then
				assertThat(msgs).isEmpty();
			}

			@Test
			@Order(7)
			@DisplayName("7. File MessageDeleteById")
			void testMessageDeleteById() {
				// given
				messageRepository.save(testMessage);
				assertThat(messageRepository.findById(testMessage.getId())).isNotNull();

				// when
				messageRepository.deleteById(testMessage.getId());

				// then
				assertThat(messageRepository.findById(testMessage.getId())).isNull();

				// 파일 다시 읽기
				MessageRepository reloadRepo = new FileMessageRepository();
				assertThat(reloadRepo.findById(testMessage.getId())).isNull();
			}

			@Test
			@Order(8)
			@DisplayName("8. File Read message")
			void testFileReadMessage() {
				// given
				messageRepository.save(testMessage);

				// when
				MessageRepository reloadRepo = new FileMessageRepository();

				// then
				Message reloadMessage = reloadRepo.findById(testMessage.getId());
				assertThat(reloadMessage).isNotNull();
				assertThat(reloadMessage.getText()).isEqualTo(testMessage.getText());
				assertThat(reloadMessage.getAuthorUUID()).isEqualTo(testMessage.getAuthorUUID());
				assertThat(reloadMessage.getChannelUUID()).isEqualTo(testMessage.getChannelUUID());
				assertThat(reloadMessage.getCreatedAt()).isEqualTo(testMessage.getCreatedAt());
			}

			@Test
			@Order(9)
			@DisplayName("9. File Read ChannelMessages")
			void testChannelReadchannelMessages() {
				// given
				UUID otherChannelId = UUID.randomUUID();
				Message msg1 = new Message(testUser.getId(), testChannel.getId(), "일반채널메시지");
				Message msg2 = new Message(testUser.getId(), otherChannelId, "다른채널메시지");
				messageRepository.save(msg1);
				messageRepository.save(msg2);

				// when
				MessageRepository reloadRepo = new FileMessageRepository();

				// then
				List<Message> channelMessages = reloadRepo.findByChannelId(testChannel.getId());
				assertThat(channelMessages).hasSize(1);
				assertThat(channelMessages.get(0).getText()).isEqualTo("일반채널메시지");

				List<Message> otherChannelMessages = reloadRepo.findByChannelId(otherChannelId);
				assertThat(otherChannelMessages).hasSize(1);
				assertThat(otherChannelMessages.get(0).getText()).isEqualTo("다른채널메시지");
			}

			@Test
			@Order(10)
			@DisplayName("10. Order 작동하는지")
			void orderTest(){

			}
		}
	}
}
