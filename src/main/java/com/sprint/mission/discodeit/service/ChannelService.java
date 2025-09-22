package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.channel.ChannelLeaveRequest;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDeleteResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelLeaveResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 생성
    ChannelResponse create(PublicChannelCreateRequest request);

    ChannelResponse create(PrivateChannelCreateRequest request);

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
