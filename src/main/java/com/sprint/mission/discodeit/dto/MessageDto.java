package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.lang.Nullable;

public class MessageDto {

    public record Create(
            User user,
            Channel channel,
            String content
    ) {}
}
