package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.channel.*;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.CreateChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.DeleteChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.JoinChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.LeaveChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public interface ChannelService {
	// 생성
	CreateChannelResponse createPublicChannel(CreatePublicChannelRequest request);
	CreateChannelResponse createPrivateChannel(CreatePrivateChannelRequest request);

	// 참가, 퇴장
	JoinChannelResponse joinChannel(JoinChannelRequest request);
	LeaveChannelResponse leaveChannel(LeaveChannelRequest request);
	// 읽기
	List<ChannelResponse> getChannelsByUserId(GetChannelsByUserRequest request);
	ChannelResponse getChannelByName(GetChannelBychannelName request);
	ChannelResponse getChannelByUUID(GetChannelByChannelIdRequest request);
	List<String> getMemberNicknames(String channelName);

	// 수정
	boolean updateUserNickname(UpdateUserNicknameRequest request);

	// 삭제
	DeleteChannelResponse deleteChannel(DeleteChannelRequest request);

	@Deprecated
	Channel createChannel(String channelName);
	@Deprecated
	boolean joinChannel(User user, String channelName);
	@Deprecated
	Channel getChannelByName(String channelName);
	@Deprecated
	Channel getChannelByUUID(UUID channelUUID);
	@Deprecated
	List<Channel> getAllChannels();
	@Deprecated
	boolean updateChannelName(UUID channelUUID, String channelNewName);
	@Deprecated
	boolean updateUserNickname(UUID channelUUID, UUID userUUID, String newNickname);
	@Deprecated
	boolean leaveChannel(UUID channelUUID, UUID userUUID);
	@Deprecated
	boolean deleteChannel(UUID channelUUID);
	@Deprecated
	boolean deleteChannel(String channelName);
}
