package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelPrivateCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelPublicCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelResponse createPrivateChannel(ChannelPrivateCreateRequest request) {
        Channel channel = Channel.builder()
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        if (channel.getId() == null) { // Generate ID if null
            channel.setId(UUID.randomUUID());
        }
        Channel savedChannel = channelRepository.save(channel);

        request.getUserIds().forEach(userId -> {
            ReadStatus readStatus = new ReadStatus(
                    UUID.randomUUID(),
                    Instant.now(),
                    Instant.now(),
                    userId,
                    savedChannel.getId(),
                    Instant.now()
            );
            readStatusRepository.save(readStatus);
        });

        return toChannelResponse(savedChannel);
    }

    @Override
    public ChannelResponse createPublicChannel(ChannelPublicCreateRequest request) {
        if (request.getChannelName() == null || request.getChannelName().isBlank()) {
            throw new IllegalArgumentException("Channel name cannot be null or blank.");
        }
        if (channelRepository.findByChannelName(request.getChannelName()).isPresent()) {
            throw new IllegalArgumentException("Channel with name '" + request.getChannelName() + "' already exists.");
        }

        Channel channel = Channel.builder()
                .channelName(request.getChannelName())
                .description(request.getDescription())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        if (channel.getId() == null) { // Generate ID if null
            channel.setId(UUID.randomUUID());
        }
        Channel savedChannel = channelRepository.save(channel);
        return toChannelResponse(savedChannel);
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        return toChannelResponse(channel);
    }

    @Override
    public List<ChannelResponse> findAll() {
        return channelRepository.findAll().stream()
                .map(this::toChannelResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> {
                    if (channel.getChannelName() == null) { // Private channel
                        return readStatusRepository.findAllByUserId(userId).stream()
                                .anyMatch(readStatus -> readStatus.getChannelId() != null && readStatus.getChannelId().equals(channel.getId()));
                    } else { // Public channel
                        return true;
                    }
                })
                .map(this::toChannelResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.getId() + " not found"));

        if (channel.getChannelName() == null) { // Private channel
            throw new IllegalArgumentException("Private channels cannot be updated.");
        }

        if (request.getChannelName() != null && !request.getChannelName().isBlank()) {
            channel.setChannelName(request.getChannelName());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            channel.setDescription(request.getDescription());
        }
        channel.setUpdatedAt(Instant.now());
        Channel updatedChannel = channelRepository.save(channel);
        return toChannelResponse(updatedChannel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        channelRepository.deleteById(channelId);
        messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .forEach(message -> messageRepository.deleteById(message.getId()));
        readStatusRepository.findAllByUserId(null).stream() // TODO: Need to filter by channelId
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .forEach(readStatus -> readStatusRepository.deleteById(readStatus.getId()));
    }

    @Override
    public void clear() {
        channelRepository.clear();
        readStatusRepository.clear();
        messageRepository.clear();
    }

    private ChannelResponse toChannelResponse(Channel channel) {
        Instant lastMessageAt = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channel.getId()))
                .map(message -> message.getCreatedAt())
                .max(Instant::compareTo)
                .orElse(null);

        List<UUID> participantIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .filter(readStatus -> readStatus.getChannelId() != null && readStatus.getChannelId().equals(channel.getId()))
                .map(ReadStatus::getUserId)
                .collect(Collectors.toList());

        return new ChannelResponse(
                channel.getId(),
                channel.getChannelName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                lastMessageAt,
                participantIds
        );
    }
}
