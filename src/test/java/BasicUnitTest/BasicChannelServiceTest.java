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
	void 일반_이라는_채널_생성시_성공() {
		// given

		// when
		Channel result = channelService.createChannel("일반");

		// then
		assertThat(result).isNotNull();
		assertThat(result.getChannelName()).isEqualTo("일반");

		Optional<Channel> savedOpt = channelRepository.findById(result.getId());
		assertThat(savedOpt).isPresent();
		Channel saved = savedOpt.get();
		assertThat(saved.getChannelName()).isEqualTo("일반");
	}

	@Test
	@Order(2)
	void 일반_이라는_채널_생성후_재생성시_중복_실패() {
		// given
		channelService.createChannel("일반");

		// when

		// then
		assertThatThrownBy(() -> channelService.createChannel("일반"))
			.isInstanceOf(DuplicateChannelNameException.class);
	}

	@Test
	@Order(3)
	void 일반_채널_생성후_일반_채널_참가시_성공() {
		// given
		channelService.createChannel("일반");

		// when
		boolean result = channelService.joinChannel(testUser, "일반");

		// then
		assertThat(result).isTrue();

		Optional<Channel> channelOpt = channelRepository.findByName("일반");
		assertThat(channelOpt).isPresent();
		Channel channel = channelOpt.get();
		assertThat(channel.getChannelUsersUUID()).contains(testUser.getId());
		assertThat(channel.getUserNicknames()).containsKey(testUser.getId());
		assertThat(channel.getUserNickname(testUser.getId())).isEqualTo("홍길동");
	}

	@Test
	@Order(4)
	void 존재_하지_않는_채널_참가시_실패() {
		// given

		// when

		// then
		assertThatThrownBy(() -> channelService.joinChannel(testUser, "존재 하지 않는 채널"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(5)
	void 일반_채널_생성후_참가_이후_일반_채널_재참가시_실패() {
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
	void 일반_채널_생성후_channelName_일반으로_채널_조회시_성공() {
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
	void 존재_하지_않는_채널을_channelName로_채널_조회시_실패() {
		// given

		// when

		// then
		assertThatThrownBy(() -> channelService.getChannelByName("존재하지않는채널"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(8)
	void 일반_채널_생성후_UUID로_채널_조회시_성공() {
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
	void 일반_채널_생성후_잘못된_UUID로_채널_조회시_실패() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> channelService.getChannelByUUID(nonExistentId))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(10)
	void 일반_채널_생성후_일반_채널에_있는_모든_User_Nicknames_조회시_성공() {
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
	void 존재_하지_않는_채널에_channeName으로_User_Nicknames_조회시_실패() {
		// given

		// when

		// then
		assertThatThrownBy(() -> channelService.getMemberNicknames("존재하지않는채널"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(12)
	void 모든_채널_조회시_성공() {
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
	void 일반_채널_생성후_channelName을_공지로_수정시_성공() {
		// given
		Channel created = channelService.createChannel("일반");

		// when
		boolean result = channelService.updateChannelName(testUser, created.getId(), "공지");

		// then
		assertThat(result).isTrue();

		Optional<Channel> updatedOpt = channelRepository.findById(created.getId());
		assertThat(updatedOpt).isPresent();
		Channel updated = updatedOpt.get();
		assertThat(updated.getChannelName()).isEqualTo("공지");

		Optional<Channel> foundByNewNameOpt = channelRepository.findByName("공지");
		assertThat(foundByNewNameOpt).isPresent();
		Channel foundByNewName = foundByNewNameOpt.get();
		assertThat(foundByNewName.getId()).isEqualTo(created.getId());
	}

	@Test
	@Order(14)
	void 없는_채널을_새이름으로_수정시_실패() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when
		assertThatThrownBy(() -> channelService.updateChannelName(testUser, nonExistentId, "새이름"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(15)
	void 일반과_개발팀_채널을_생성후_일반_채널을_개발팀으로_변경시_중복으로_실패() {
		// given
		Channel channel1 = channelService.createChannel("일반");
		channelService.createChannel("개발팀");

		// when
		assertThatThrownBy(() -> channelService.updateChannelName(testUser, channel1.getId(), "개발팀"))
			.isInstanceOf(DuplicateChannelNameException.class);
	}

	@Test
	@Order(16)
	void 일반_채널_생성후_testUser_참가_채널별_Nickname으로_변경시_성공() {
		// given
		Channel created = channelService.createChannel("일반");
		channelService.joinChannel(testUser, "일반");

		// when
		boolean result = channelService.updateUserNickname(created.getId(), testUser.getId(), "새닉네임");

		// then
		assertThat(result).isTrue();

		Optional<Channel> updatedOpt = channelRepository.findById(created.getId());
		assertThat(updatedOpt).isPresent();
		Channel updated = updatedOpt.get();
		assertThat(updated.getUserNickname(testUser.getId())).isEqualTo("새닉네임");
	}

	@Test
	@Order(17)
	void 없는_채널에서_채널별_Nickname으로_변경시_실패() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when
		assertThatThrownBy(() -> channelService.updateUserNickname(nonExistentChannelId, testUser.getId(), "새닉네임"))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(18)
	void 참가_하지_않은_채널에서_채널별_닉네임_변경시_실패() {
		// given
		Channel created = channelService.createChannel("일반");

		// when
		assertThatThrownBy(() -> channelService.updateUserNickname(created.getId(), testUser.getId(), "새닉네임"))
			.isInstanceOf(NotChannelMemberException.class);
	}

	@Test
	@Order(19)
	void 일반_채널_에서_삭제후_성공() {
		// given
		Channel created = channelService.createChannel("일반");
		channelService.joinChannel(testUser, "일반");

		// when
		boolean result = channelService.leaveChannel(created.getId(), testUser.getId());

		// then
		assertThat(result).isTrue();

		Optional<Channel> updatedOpt = channelRepository.findById(created.getId());
		assertThat(updatedOpt).isPresent();
		Channel updated = updatedOpt.get();
		assertThat(updated.getChannelUsersUUID()).doesNotContain(testUser.getId());
		assertThat(updated.getUserNicknames()).doesNotContainKey(testUser.getId());
	}

	@Test
	@Order(20)
	void 존재_하지_않는_채널을_삭제시_실패() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when
		assertThatThrownBy(() -> channelService.leaveChannel(nonExistentChannelId, testUser.getId()))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(21)
	void 채널에_멤버가_아닌_User를_삭제시_실패() {
		// given
		Channel created = channelService.createChannel("일반");

		// when
		assertThatThrownBy(() -> channelService.leaveChannel(created.getId(), testUser.getId()))
			.isInstanceOf(NotChannelMemberException.class);
	}

	@Test
	@Order(22)
	void Channel_UUID로_삭제시_성공() {
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
	void 존재_하지_않는_Channel_UUID로_삭제시_실패() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		// when
		assertThatThrownBy(() -> channelService.deleteChannel(nonExistentChannelId))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@Order(24)
	void ChannelName으로_삭제시_성공() {
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
	void 존재_하지_않는_channelName으로_삭제시_실패() {
		// when
		assertThatThrownBy(() -> channelService.deleteChannel("존재하지않는채널"))
			.isInstanceOf(ChannelNotFoundException.class);
	}
}