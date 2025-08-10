package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {


    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelResponse create(ChannelCreateRequest request) {
        return request.getType() == ChannelType.PUBLIC
                ? createPublic(request)
                : createPrivate(new PrivateChannelCreateRequest(request.getParticipantIds()));
    }

    @Override
    public ChannelResponse createPublic(ChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.getName(), request.getDescription());
        channelRepository.save(channel);

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                null,
                null
        );
    }

    @Override
    public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE,null, null);
        channelRepository.save(channel);

        for (UUID userId : request.getParticipantIds()) {
            if (!userRepository.existsById(userId)) {
                throw new RuntimeException("존재하지 않는 사용자입니다: " + userId);
            }
            ReadStatus status = new ReadStatus(userId, channel.getId(), Instant.now());
            readStatusRepository.save(status);
        }

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                null,
                null,
                null,
                request.getParticipantIds()
        );
    }

    @Override
    public ChannelResponse findById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        List<UUID> participantIds = channel.getType() == ChannelType.PRIVATE
                ? readStatusRepository.findAll().stream()
                .filter(r -> r.getChannelId().equals(channelId))
                .map(ReadStatus::getUserId)
                .toList()
                : null;

        Instant latestMessageTime = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channel.getId()))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                latestMessageTime,
                participantIds
        );
    }

    @Override
    public List<ChannelResponse> findAll() {
        return channelRepository.findAll().stream()
                .map(channel -> {
                    List<UUID> participantIds = channel.getType() == ChannelType.PRIVATE
                            ? readStatusRepository.findAll().stream()
                            .filter(r -> r.getChannelId().equals(channel.getId()))
                            .map(ReadStatus::getUserId)
                            .toList()
                            : null;

                    Instant latestMessageTime = messageRepository.findAll().stream()
                            .filter(m -> m.getChannelId().equals(channel.getId()))
                            .map(Message::getCreatedAt)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    return new ChannelResponse(
                            channel.getId(),
                            channel.getType(),
                            channel.getName(),
                            channel.getDescription(),
                            latestMessageTime,
                            participantIds
                    );
                })
                .toList();
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<UUID> privateChannelIds = readStatusRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC || privateChannelIds.contains(channel.getId()))
                .map(channel -> {
                    List<UUID> participantIds = channel.getType() == ChannelType.PRIVATE
                            ? readStatusRepository.findAll().stream()
                            .filter(r -> r.getChannelId().equals(channel.getId()))
                            .map(ReadStatus::getUserId)
                            .toList()
                            : null;

                    Instant latestMessageTime = messageRepository.findAll().stream()
                            .filter(m -> m.getChannelId().equals(channel.getId()))
                            .map(Message::getCreatedAt)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    return new ChannelResponse(
                            channel.getId(),
                            channel.getType(),
                            channel.getName(),
                            channel.getDescription(),
                            latestMessageTime,
                            participantIds
                    );
                })
                .toList();
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.getId() + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new RuntimeException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(request.getName(), request.getDescription());
        channelRepository.save(channel);

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                null,
                null
        );
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            readStatusRepository.findAll().stream()
                    .filter(r -> r.getChannelId().equals(channelId))
                    .forEach(r -> readStatusRepository.deleteById(r.getId()));
        }

        channelRepository.deleteById(channelId);
    }
}
