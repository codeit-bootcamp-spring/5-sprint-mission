package com.sprint.mission.discodeit.service.channel;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.dto.request.chnanel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.chnanel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;

  @Transactional
  public ChannelResponse create(PublicChannelCreateRequest req) {
    return ChannelResponse.from(channelRepository.save(new Channel(req.name(), req.description())));
  }

  @Transactional
  public ChannelResponse create(PrivateChannelCreateRequest req) {
    for (UUID id : req.participantIds()) {
      userRepository.getOrThrow(id);
    }

    return ChannelResponse.from(channelRepository.save(new Channel(req.participantIds())));
  }
}
