package com.sprint.mission.discodeit.service;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ROLE;

public interface ChannelService {
	// 생성
	boolean createChannel(String channelName);
	boolean createChannelForAdmin(String channelName, ROLE[] accessPermission);

	// 읽기
	Channel getChannel(String channelName);
	Channel[] getChannelAll();

	// 수정
	boolean updateChannelForName(UUID id, String channelNewName);
	boolean updateChannelForRole(UUID id, ROLE role);

	// 삭제
	boolean deleteChannelById(UUID id);
	boolean deleteChannelByName(String channelName);
}
