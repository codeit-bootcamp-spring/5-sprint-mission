package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelResponseDto create(Object request) {
        if (request instanceof ChannelCreateRequest) {
            return createPublicChannel((ChannelCreateRequest) request);
        } else if (request instanceof PrivateChannelCreateRequest) {
            return createPrivateChannel((PrivateChannelCreateRequest) request);
        }
        throw new IllegalArgumentException("잘못된 채널 타입입니다.");
    }

    private ChannelResponseDto createPublicChannel(ChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();

        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        Channel saved = channelRepository.save(channel);

        return toDto(saved);
    }

    private ChannelResponseDto createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        // 참여자들에 대한 읽음 상태 저장
        request.participantIds().stream()
                .map(userId -> new ReadStatus(userId, createdChannel.getId(), Instant.MIN))
                .forEach(readStatusRepository::save);

        return toDto(createdChannel);
    }


    @Override
    public ChannelResponseDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("id가" + channelId + "인 채널을 찾을 수 없습니다."));
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannelIds.contains(channel.getId())
                )
                .map(this::toDto)
                .toList();
    }

    @Override
    public ChannelResponseDto update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(request.name(), request.description());
        Channel savedChannel = channelRepository.save(channel);
        return this.toDto(savedChannel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.deleteById(channelId);
    }

    private ChannelResponseDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);

        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .forEach(participantIds::add);
        }

        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }
}
