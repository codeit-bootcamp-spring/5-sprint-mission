package com.sprint.mission.discodeit.service;

import java.util.List;

import com.sprint.mission.discodeit.dto.request.channel.*;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelCreateResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDeleteResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelLeaveResponse;
import java.util.UUID;

public interface ChannelService {
	// 생성
	ChannelCreateResponse create(PublicChannelCreateRequest request);
	ChannelCreateResponse create(PrivateChannelCreateRequest request);

	// 참가, 퇴장
	ChannelLeaveResponse leaveChannel(ChannelLeaveRequest request);
	// 읽기
	List<ChannelResponse> findChannelsByUserId(UUID userId);
	ChannelResponse findByName(String channelName);
	ChannelResponse find(UUID channelId);

	// 수정
	ChannelResponse updateChannel(UUID channelId, ChannelUpdateRequest request);

	// 삭제
	ChannelDeleteResponse deleteChannel(UUID channelId);
}
