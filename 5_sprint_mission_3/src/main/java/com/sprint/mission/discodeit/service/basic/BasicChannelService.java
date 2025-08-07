package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicChannelService")
@Primary
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;


    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channel = channelRepository.save(channel);

        for (UUID userId : request.participantUserIds()) {
            if (!userRepository.existsById(userId)) {
                throw new NoSuchElementException("존재하지 않는 사용자 ID입니다: " + userId);
            }
            ReadStatus readStatus = new ReadStatus(channel.getId(), userId);
            readStatusRepository.save(readStatus);
        }
        return channel;
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channelId).orElse(null);

        List<UUID> participantIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findAllByChannelId(channelId)
                    .stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                participantIds
        );
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll();

        return allChannels.stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) return true;
                    return readStatusRepository.existsByUserIdAndChannelId(userId, channel.getId());
                })
                .map(channel -> {
                    Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel.getId())
                            .orElse(null);

                    List<UUID> participantIds = null;
                    if (channel.getType() == ChannelType.PRIVATE) {
                        participantIds = readStatusRepository.findAllByChannelId(channel.getId())
                                .stream()
                                .map(ReadStatus::getUserId)
                                .toList();
                    }

                    return new ChannelResponseDto(
                            channel.getId(),
                            channel.getType(),
                            channel.getName(),
                            channel.getDescription(),
                            lastMessageAt,
                            participantIds
                    );
                })
                .toList();
    }

    @Override
    public Channel update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.channelId() + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new UnsupportedOperationException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(request.newName(), request.newDescription());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id " + channelId));

        messageRepository.findAllByChannelId(channelId)
                .forEach(message -> messageRepository.deleteById(message.getId()));

        readStatusRepository.findAllByChannelId(channelId)
                .forEach(readStatus -> readStatusRepository.deleteById(readStatus.getId()));

        channelRepository.deleteById(channelId);
    }
}