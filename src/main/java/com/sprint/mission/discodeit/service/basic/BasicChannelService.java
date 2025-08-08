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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service("BasicChannelService")
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(ChannelRepository channelRepository,
                               ReadStatusRepository readStatusRepository,
                               MessageRepository messageRepository) {
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

        for (UUID userId : Optional.ofNullable(request.getParticipantIds())
                .orElseGet(Collections::emptyList)) {
            Instant now = Instant.now();
            ReadStatus rs = new ReadStatus(
                    UUID.randomUUID(),
                    channel.getId(),
                    userId,
                    now,
                    now,
                    now
            );
            readStatusRepository.save(rs);
        }
        return channel.getId();
    }

    private Instant resolveLastMessageAt(Channel ch) {
        Instant recent = messageRepository.findRecentMessageTimeByChannelId(ch.getId()); // A안: Instant 그대로
        if (recent != null) return recent;
        return (ch.getUpdatedAt() != null) ? ch.getUpdatedAt() : ch.getCreatedAt();
    }

    @Override
    public Optional<ChannelResponse> find(UUID channelId) {
        return channelRepository.findById(channelId).map(channel -> {
            Instant recentMessageTime = resolveLastMessageAt(channel);
            List<UUID> participantIds = (channel.getType() == ChannelType.PRIVATE)
                    ? readStatusRepository.findAllByChannelId(channel.getId())
                    .stream().map(ReadStatus::getUserId).toList()
                    : List.of();

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
        return channelRepository.findAll().stream()
                .filter(ch -> ch.getType() == ChannelType.PUBLIC ||
                        readStatusRepository.findAllByUserId(userId).stream()
                                .anyMatch(rs -> rs.getChannelId().equals(ch.getId())))
                .map(ch -> {
                    Instant recentMessageTime = resolveLastMessageAt(ch);
                    List<UUID> participantIds = (ch.getType() == ChannelType.PRIVATE)
                            ? readStatusRepository.findAllByChannelId(ch.getId())
                            .stream().map(ReadStatus::getUserId).toList()
                            : List.of();

                    return new ChannelResponse(
                            ch.getId(),
                            ch.getCreatedAt(),
                            ch.getUpdatedAt(),
                            ch.getName(),
                            ch.getDescription(),
                            ch.getType().name(),
                            recentMessageTime,
                            participantIds
                    );
                })
                .toList();
    }

    @Override
    public boolean update(ChannelUpdateRequest request) {
        Channel ch = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        if (ch.getType() == ChannelType.PRIVATE) return false;

        ch.update(request.getName(), request.getDescription());
        channelRepository.save(ch);
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

