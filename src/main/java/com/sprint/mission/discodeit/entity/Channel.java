package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {
    private String channelName;
    private String channelDescription;
    private ChannelAccessibility accessibility;
    private List<UUID> userIdList;
    private List<Message> messageList;

    public static Channel createPublic(String channelName, String channelDescription, List<UUID> userIdList) {
        return new Channel(UUID.randomUUID(), channelName, channelDescription, ChannelAccessibility.PUBLIC, userIdList, Instant.now());
    }

    public static Channel createPrivate(List<UUID> userIdList) {
        return new Channel(UUID.randomUUID(), null, null, ChannelAccessibility.PRIVATE, userIdList, Instant.now());
    }

    private Channel(UUID id, String channelName, String channelDescription, ChannelAccessibility accessibility, List<UUID> userIdList, Instant createAt) {
        super(id, createAt);
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.accessibility = accessibility;
        this.userIdList = userIdList;
        messageList = new ArrayList<>();
    }

    public void update(UpdateChannelRequest request) {
        if (request.name() != null) this.channelName = request.name();
        if (request.description() != null) this.channelDescription = request.description();
        updateTimeStamp();
    }
}
