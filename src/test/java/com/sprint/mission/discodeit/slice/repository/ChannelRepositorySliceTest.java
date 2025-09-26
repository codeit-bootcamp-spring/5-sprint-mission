package com.sprint.mission.discodeit.slice.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ChannelRepositorySliceTest {

	@Autowired
	private ChannelRepository channelRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	private Channel createChannel(ChannelType channelType, String name, String description) {
		return new Channel(channelType, name, description);
	}

	@Test
	@DisplayName("ChannelRepository - findAllByTypeOrIdIn - 성공")
	public void findAllByTypeOrIdInSuccess() {
		Channel channel1 = channelRepository.save(createChannel(ChannelType.PUBLIC, "testName1", "testDescription1"));
		Channel channel2 = channelRepository.save(createChannel(ChannelType.PRIVATE, "testName2", "testDescription2"));
		Channel channel3 = channelRepository.save(createChannel(ChannelType.PRIVATE, "testName3", "testDescription3"));
		Channel channel4 = channelRepository.save(createChannel(ChannelType.PUBLIC, "testName4", "testDescription4"));
		Channel channel5 = channelRepository.save(createChannel(ChannelType.PUBLIC, "testName5", "testDescription5"));

		testEntityManager.flush();
		testEntityManager.clear();
		//
		List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC,
			List.of(channel2.getId()));
		//
		assertThat(result.size()).isEqualTo(4);
		int countPrivate = 0;
		for (Channel channel : result) {
			if(channel.getType().equals(ChannelType.PRIVATE)) {
				countPrivate++;
			}
		}
		assertThat(countPrivate).isEqualTo(1);
	}

	@Test
	@DisplayName("ChannelRepository - findAllByTypeOrIdIn - 실패 (결과 없음)")
	public void findAllByTypeOrIdInFail() {
		channelRepository.save(createChannel(ChannelType.PRIVATE, "testName1", "testDescription1"));
		channelRepository.save(createChannel(ChannelType.PRIVATE, "testName2", "testDescription2"));

		testEntityManager.flush();
		testEntityManager.clear();

		//
		List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, Collections.emptyList());

		//
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
	}
}
