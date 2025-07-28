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
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.AlreadyChannelMemberException;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.DuplicateChannelNameException;
import com.sprint.mission.discodeit.exception.NotChannelMemberException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;

@DisplayName("BasicChannelService 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicChannelServiceTest {

	private ChannelRepository channelRepository;
	private BasicChannelService channelService;
	private User testUser;
	private Channel testChannel;

	@BeforeEach
	void setUp() {
		cleanDirectory(new File("data/"));

		channelRepository = new FileChannelRepository();
		channelService = new BasicChannelService(channelRepository);
		testUser = new User("test1", "password1", "홍길동");
		testChannel = new Channel("일반");
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
	@DisplayName("1. Basic ChannelCreate")
	void testCreateChannel() {
		// given

		// when
		Channel result = channelService.createChannel("일반");

		// then
		assertThat(result).isNotNull();
		assertThat(result.getChannelName()).isEqualTo("일반");

		Channel saved = channelRepository.findById(result.getId());
		assertThat(saved).isNotNull();
		assertThat(saved.getChannelName()).isEqualTo("일반");
	}

	@Test
	@Order(2)
	@DisplayName("2. Basic ChannelCreate 중복채널명 예외")
	void testCreateChannel_DuplicateChannelName() {
		// given
		channelService.createChannel("일반");

		// when

		// then
		assertThatThrownBy(() -> channelService.createChannel("일반"))
			.isInstanceOf(DuplicateChannelNameException.class);
	}

	@Test
	@Order(3)
	@DisplayName("3. Basic ChannelJoin")
	void testJoinChannel() {
		// given
		channelService.createChannel("일반");

		// when
		boolean result = channelService.joinChannel(testUser, "일반");

		// then
		assertThat(result).isTrue();

		Channel channel = channelRepository.findByName("일반");
		assertThat(channel.getChannelUsersUUID()).contains(testUser.getId());
		assertThat(channel.getUserNicknames()).containsKey(testUser.getId());
		assertThat(channel.getUserNickname(testUser.getId())).isEqualTo("홍길동");
	}

	@Test
	@Order(4)
	@DisplayName("4. Basic ChannelJoin 존재하지않는채널 예외")
	void testJoinChannel_ChannelNotFound() {
		// given

		// when

		// then
		assertThatThrownBy(() -> channelService.joinChannel(testUser, "존재하지않는채널"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(5)
	@DisplayName("5. Basic ChannelJoin 이미참가한멤버 예외")
	void testJoinChannel_AlreadyMember() {
		// given
		channelService.createChannel("일반");
		channelService.joinChannel(testUser, "일반");

		// when

		// then
		assertThatThrownBy(() -> channelService.joinChannel(testUser, "일반"))
			.isInstanceOf(AlreadyChannelMemberException.class);
	}

	@Test
	@Order(6)
	@DisplayName("6. Basic ChannelGetByName")
	void testGetChannelByName() {
		// given
		Channel created = channelService.createChannel("일반");

		// when
		Channel result = channelService.getChannelByName("일반");

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(created.getId());
		assertThat(result.getChannelName()).isEqualTo("일반");
	}

	@Test
	@Order(7)
	@DisplayName("7. Basic ChannelGetByName 채널없음 예외")
	void testGetChannelByName_ChannelNotFound() {
		// given

		// when

		// then
		assertThatThrownBy(() -> channelService.getChannelByName("존재하지않는채널"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(8)
	@DisplayName("8. Basic ChannelGetByUUID")
	void testGetChannelByUUID() {
		// given
		Channel created = channelService.createChannel("일반");

		// when
		Channel result = channelService.getChannelByUUID(created.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(created.getId());
		assertThat(result.getChannelName()).isEqualTo("일반");
	}

	@Test
	@Order(9)
	@DisplayName("9. Basic ChannelGetByUUID 채널없음 예외")
	void testGetChannelByUUID_ChannelNotFound() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelService.getChannelByUUID(nonExistentId))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(10)
	@DisplayName("10. Basic ChannelGetMemberNicknames")
	void testGetMemberNicknames() {
		// given
		channelService.createChannel("일반");
		User user2 = new User("test2", "password2", "김길동");
		channelService.joinChannel(testUser, "일반");
		channelService.joinChannel(user2, "일반");

		// when
		List<String> result = channelService.getMemberNicknames("일반");

		// then
		assertThat(result)
			.isNotNull()
			.hasSize(2)
			.contains("홍길동", "김길동");
	}

	@Test
	@Order(11)
	@DisplayName("11. Basic ChannelGetMemberNicknames 채널없음 예외")
	void testGetMemberNicknames_ChannelNotFound() {
		// given

		// when

		// then
		assertThatThrownBy(() -> channelService.getMemberNicknames("존재하지않는채널"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(12)
	@DisplayName("12. Basic ChannelGetAll")
	void testGetAllChannels() {
		// given
		channelService.createChannel("일반");
		channelService.createChannel("개발팀");
		channelService.createChannel("취미");

		// when
		List<Channel> result = channelService.getAllChannels();

		// then
		assertThat(result)
			.isNotNull()
			.hasSize(3);
		assertThat(result)
			.extracting(Channel::getChannelName)
			.containsExactlyInAnyOrder("일반", "개발팀", "취미");
	}

	@Test
	@Order(13)
	@DisplayName("13. Basic ChannelUpdateChannelName")
	void testUpdateChannelName() {
		// given
		Channel created = channelService.createChannel("일반");

		// when
		boolean result = channelService.updateChannelName(testUser, created.getId(), "공지");

		// then
		assertThat(result).isTrue();

		Channel updated = channelRepository.findById(created.getId());
		assertThat(updated.getChannelName()).isEqualTo("공지");

		Channel foundByNewName = channelRepository.findByName("공지");
		assertThat(foundByNewName).isNotNull();
		assertThat(foundByNewName.getId()).isEqualTo(created.getId());
	}

	@Test
	@Order(14)
	@DisplayName("14. Basic ChannelUpdateChannelName 채널없음 예외")
	void testUpdateChannelName_ChannelNotFound() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when
		assertThatThrownBy(() -> channelService.updateChannelName(testUser, nonExistentId, "새이름"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(15)
	@DisplayName("15. Basic ChannelUpdateChannelName 중복채널명 예외")
	void testUpdateChannelName_DuplicateChannelName() {
		// given
		Channel channel1 = channelService.createChannel("일반");
		channelService.createChannel("개발팀");

		// when
		assertThatThrownBy(() -> channelService.updateChannelName(testUser, channel1.getId(), "개발팀"))
			.isInstanceOf(DuplicateChannelNameException.class);
	}

	@Test
	@Order(16)
	@DisplayName("16. Basic ChannelUpdateUserNickname")
	void testUpdateUserNickname() {
		// given
		Channel created = channelService.createChannel("일반");
		channelService.joinChannel(testUser, "일반");

		// when
		boolean result = channelService.updateUserNickname(created.getId(), testUser.getId(), "새닉네임");

		// then
		assertThat(result).isTrue();

		Channel updated = channelRepository.findById(created.getId());
		assertThat(updated.getUserNickname(testUser.getId())).isEqualTo("새닉네임");
	}

	@Test
	@Order(17)
	@DisplayName("17. Basic ChannelUpdateUserNickname 채널없음 예외")
	void testUpdateUserNickname_ChannelNotFound() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when
		assertThatThrownBy(() -> channelService.updateUserNickname(nonExistentChannelId, testUser.getId(), "새닉네임"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(18)
	@DisplayName("18. Basic ChannelUpdateUserNickname 멤버아님 예외")
	void testUpdateUserNickname_NotChannelMember() {
		// given
		Channel created = channelService.createChannel("일반");

		// when
		assertThatThrownBy(() -> channelService.updateUserNickname(created.getId(), testUser.getId(), "새닉네임"))
			.isInstanceOf(NotChannelMemberException.class);
	}

	@Test
	@Order(19)
	@DisplayName("19. Basic ChannelLeave")
	void testLeaveChannel() {
		// given
		Channel created = channelService.createChannel("일반");
		channelService.joinChannel(testUser, "일반");

		// when
		boolean result = channelService.leaveChannel(created.getId(), testUser.getId());

		// then
		assertThat(result).isTrue();

		Channel updated = channelRepository.findById(created.getId());
		assertThat(updated.getChannelUsersUUID()).doesNotContain(testUser.getId());
		assertThat(updated.getUserNicknames()).doesNotContainKey(testUser.getId());
	}

	@Test
	@Order(20)
	@DisplayName("20. Basic ChannelLeave 채널없음 예외")
	void testLeaveChannel_ChannelNotFound() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when
		assertThatThrownBy(() -> channelService.leaveChannel(nonExistentChannelId, testUser.getId()))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(21)
	@DisplayName("21. Basic ChannelLeave 멤버아님 예외")
	void testLeaveChannel_NotChannelMember() {
		// given
		Channel created = channelService.createChannel("일반");

		// when
		assertThatThrownBy(() -> channelService.leaveChannel(created.getId(), testUser.getId()))
			.isInstanceOf(NotChannelMemberException.class);
	}

	@Test
	@Order(22)
	@DisplayName("22. Basic ChannelDeleteByUUID")
	void testDeleteChannelByUUID() {
		// given
		Channel created = channelService.createChannel("일반");

		// when
		boolean result = channelService.deleteChannel(created.getId());

		// then
		assertThat(result).isTrue();

		assertThatThrownBy(() -> channelService.getChannelByUUID(created.getId()))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(23)
	@DisplayName("23. Basic ChannelDeleteByUUID 채널없음 예외")
	void testDeleteChannelByUUID_ChannelNotFound() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when
		assertThatThrownBy(() -> channelService.deleteChannel(nonExistentChannelId))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(24)
	@DisplayName("24. Basic ChannelDeleteByName")
	void testDeleteChannelByName() {
		// given
		channelService.createChannel("일반");

		// when
		boolean result = channelService.deleteChannel("일반");

		// then
		assertThat(result).isTrue();

		assertThatThrownBy(() -> channelService.getChannelByName("일반"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(25)
	@DisplayName("25. Basic ChannelDeleteByName 채널없을때 예외")
	void testDeleteChannelByName_ChannelNotFound() {
		// when
		assertThatThrownBy(() -> channelService.deleteChannel("존재하지않는채널"))
			.isInstanceOf(ChannelNotFoundException.class);
	}
}