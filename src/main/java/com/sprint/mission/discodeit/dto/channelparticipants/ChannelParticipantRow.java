package com.sprint.mission.discodeit.dto.channelparticipants;

import com.sprint.mission.discodeit.dto.user.UserDto;
import java.util.UUID;

public record ChannelParticipantRow(UUID channelId, UserDto user) {

}
