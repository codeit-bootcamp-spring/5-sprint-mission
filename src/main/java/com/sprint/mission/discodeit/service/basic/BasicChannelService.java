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
import java.util.stream.Collectors;

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

        // 참여자 없을 수도 있으니 NPE 방지
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

    // 메시지의 최신 시각을 안전하게 계산 (밀리초 기준)
    private Instant resolveLastMessageAt(Channel ch) {
        Long lastMsgMillis = messageRepository.findRecentMessageTimeByChannelId(ch.getId());
        if (lastMsgMillis != null) {
            // 저장소가 millisecond를 반환한다고 가정. 만약 second라면 ofEpochSecond로 바꾸세요.
            return Instant.ofEpochMilli(lastMsgMillis);
        }
        // 메시지가 없으면 채널의 updatedAt -> createdAt 순서로 대체
        return ch.getUpdatedAt() != null ? ch.getUpdatedAt() : ch.getCreatedAt();
    }

    @Override
    public Optional<ChannelResponse> find(UUID channelId) {
        return channelRepository.findById(channelId).map(ch -> {
            Instant recentMessageTime = resolveLastMessageAt(ch);

            List<UUID> participantIds = Collections.emptyList();
            if (ch.getType() == ChannelType.PRIVATE) {
                participantIds = readStatusRepository.findAllByChannelId(ch.getId())
                        .stream()
                        .map(ReadStatus::getUserId)
                        .collect(Collectors.toList());
            }

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
        });
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        // 사용자가 속한 PRIVATE 채널 id를 먼저 모아두기
        Set<UUID> myPrivateChannelIds = readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        return channelRepository.findAll().stream()
                .filter(ch -> ch.getType() == ChannelType.PUBLIC || myPrivateChannelIds.contains(ch.getId()))
                .map(ch -> {
                    Instant recentMessageTime = resolveLastMessageAt(ch);

                    List<UUID> participantIds = Collections.emptyList();
                    if (ch.getType() == ChannelType.PRIVATE) {
                        participantIds = readStatusRepository.findAllByChannelId(ch.getId())
                                .stream()
                                .map(ReadStatus::getUserId)
                                .collect(Collectors.toList());
                    }

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
                .collect(Collectors.toList());
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
