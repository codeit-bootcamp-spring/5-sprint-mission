package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.ChannelParticipant;
import com.sprint.mission.discodeit.repository.ChannelParticipantRepository;
import com.sprint.mission.discodeit.service.ChannelParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicChannelParticipantService")
@RequiredArgsConstructor
public class BasicChannelParticipantService implements ChannelParticipantService {

    private final ChannelParticipantRepository channelParticipantRepository;

    @Override
    public ChannelParticipant addParticipant(UUID channelId, UUID userId) {
        if (channelParticipantRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new IllegalStateException("이미 참가한 사용자입니다.");
        }
        ChannelParticipant participant = new ChannelParticipant(channelId, userId);
        return channelParticipantRepository.save(participant);
    }

    @Override
    public void removeParticipant(UUID channelId, UUID userId) {
        if (!channelParticipantRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new NoSuchElementException("참가자가 존재하지 않습니다.");
        }
        channelParticipantRepository.deleteByChannelIdAndUserId(channelId, userId);
    }

    @Override
    public List<ChannelParticipant> findParticipantsByChannelId(UUID channelId) {
        return channelParticipantRepository.findAllByChannelId(channelId);
    }

    @Override
    public List<ChannelParticipant> findChannelsByUserId(UUID userId) {
        return channelParticipantRepository.findAllByUserId(userId);
    }
}
