package com.sprint.mission.discodeit.service;

import java.util.List;

import com.sprint.mission.discodeit.dto.request.channel.*;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.CreateChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.DeleteChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.JoinChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.LeaveChannelResponse;

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
	ChannelResponse updateUserNickname(UpdateUserNicknameRequest request);
	ChannelResponse updateChannel(UpdateChannelRequest request);

	// 삭제
	DeleteChannelResponse deleteChannel(DeleteChannelRequest request);
}
