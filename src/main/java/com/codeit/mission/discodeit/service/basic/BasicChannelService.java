package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.data.ChannelDto;
import com.codeit.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.ChannelType;
import com.codeit.mission.discodeit.entity.Message;
import com.codeit.mission.discodeit.entity.ReadStatus;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.repository.ChannelRepository;
import com.codeit.mission.discodeit.repository.MessageRepository;
import com.codeit.mission.discodeit.repository.ReadStatusRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public Channel create(PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);

        return channelRepository.save(channel);
    }

    @Override
    public Channel create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        request.participantIds().forEach(userId -> {
            User user = userRepository.findById(userId)
                .orElseThrow(
                    () -> new NoSuchElementException("User with id " + userId + " not found"));
            ReadStatus readStatus = new ReadStatus(user, createdChannel,
                createdChannel.getCreatedAt());
            readStatusRepository.save(readStatus);
        });

        return createdChannel;
    }

    @Override
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
            .map(this::toDto)
            .orElseThrow(
                () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatus -> readStatus.getChannel().getId())
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
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channels cannot be updated");
        }

        channel.update(newName, newDescription);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.deleteById(channelId);
    }

    private ChannelDto toDto(Channel channel) {
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
                .map(readStatus -> readStatus.getUser().getId())
                .forEach(participantIds::add);
        }

        return new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            participantIds,
            lastMessageAt
        );
    }
}
