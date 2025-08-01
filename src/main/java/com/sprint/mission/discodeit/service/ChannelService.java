package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public interface ChannelService {
	// 생성
	Channel createChannel(String channelName);

	// 참가
	boolean joinChannel(User user, String channelName);

	// 읽기
	Channel getChannelByName(String channelName);
	Channel getChannelByUUID(UUID channelUUID);

	List<String> getMemberNicknames(String channelName);
	List<Channel> getAllChannels();

	// 수정
	boolean updateChannelName(User user, UUID channelUUID, String channelNewName);
	boolean updateUserNickname(UUID channelUUID, UUID userUUID, String newNickname);

	// 삭제
	boolean leaveChannel(UUID channelUUID, UUID userUUID);
	boolean deleteChannel(UUID channelUUID);
	boolean deleteChannel(String channelName);
}
