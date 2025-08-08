package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.CreateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Channel.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("BasicChannelService")
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(ChannelRepository channelRepository, ReadStatusRepository readStatusRepository, MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.readStatusRepository = readStatusRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public UUID createPublicChannel(CreateChannelRequest request) {
        Channel channel = new Channel(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                request.getName(),
                request.getDescription(),
                ChannelType.PUBLIC
        );
        channelRepository.save(channel);
        return channel.getId();
    }

    @Override
    public Optional<ChannelResponse> findById(UUID channelId) {
        return find(channelId);
    }

    @Override
    public UUID createPrivateChannel(PrivateChannelRequest request) {
        Channel channel = new Channel(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                null,
                null,
                ChannelType.PRIVATE
        );
        channelRepository.save(channel);

        for (UUID userId : request.getParticipantIds()) {
            Instant now = Instant.now();
            ReadStatus readStatus = new ReadStatus(
                    UUID.randomUUID(),
                    channel.getId(),
                    userId,
                    now,
                    now,
                    now
            );
            readStatusRepository.save(readStatus);
        }


        return channel.getId();
    }

    @Override
    public Optional<ChannelResponse> find(UUID channelId) {
        Optional<Channel> optionalChannel = channelRepository.findById(channelId);

        return optionalChannel.map(channel -> {
            Instant recentMessageTime = Instant.ofEpochSecond(messageRepository.findRecentMessageTimeByChannelId(channelId));
            List<UUID> participantIds = new ArrayList<>();

            if (channel.getType() == ChannelType.PRIVATE) {
                participantIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                        .map(ReadStatus::getUserId)
                        .collect(Collectors.toList());
            }

            return new ChannelResponse(
                    channel.getId(),
                    channel.getCreatedAt(),
                    channel.getUpdatedAt(),
                    channel.getName(),
                    channel.getDescription(),
                    channel.getType().name(),
                    recentMessageTime,
                    participantIds
            );
        });
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll();

        return allChannels.stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC ||
                        readStatusRepository.findAllByUserId(userId).stream()
                                .anyMatch(rs -> rs.getChannelId().equals(channel.getId())))
                .map(channel -> {
                    Instant recentMessageTime = Instant.ofEpochSecond(messageRepository.findRecentMessageTimeByChannelId(channel.getId()));
                    List<UUID> participantIds = new ArrayList<>();

                    if (channel.getType() == ChannelType.PRIVATE) {
                        participantIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                                .map(ReadStatus::getUserId)
                                .collect(Collectors.toList());
                    }

                    return new ChannelResponse(
                            channel.getId(),
                            channel.getCreatedAt(),
                            channel.getUpdatedAt(),
                            channel.getName(),
                            channel.getDescription(),
                            channel.getType().name(),
                            recentMessageTime,
                            participantIds
                    );

                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        if (channel.getType() == ChannelType.PRIVATE) return false;

        channel.update(request.getName(), request.getDescription());
        channelRepository.save(channel);
        return true;
    }

    @Override
    public boolean delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) return false;

        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
        return true;
    }
}

