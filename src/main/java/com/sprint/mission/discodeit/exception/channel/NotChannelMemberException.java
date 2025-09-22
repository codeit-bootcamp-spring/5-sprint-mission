package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class NotChannelMemberException extends ChannelException {
	public NotChannelMemberException() {
		super(ErrorCode.NOT_CHANNEL_MEMBER);
	}

    public static NotChannelMemberException withUserIdAndChannelId(UUID userId, UUID channelId) {
        NotChannelMemberException exception = new NotChannelMemberException();
        exception.addDetail("userId", userId);
        exception.addDetail("channelId", channelId);
        return exception;
    }
}
