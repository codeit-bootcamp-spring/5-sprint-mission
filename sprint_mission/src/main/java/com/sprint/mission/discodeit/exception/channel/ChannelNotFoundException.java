package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException {

    public ChannelNotFoundException() {
        super(ErrorCode.CHANNEL_NOT_FOUND);
    }

    public static ChannelNotFoundException withId(UUID channelId) {
        ChannelNotFoundException exception = new ChannelNotFoundException();
        exception.addDetail("channelId ", channelId);
        return exception;
    }
}
