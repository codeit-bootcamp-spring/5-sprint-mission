package com.sprint.mission.discodeit.event;


import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChannelDeletedEvent {
    private final ChannelResponse channelResponse;
}