package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.dto.channel.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    /** 인메모리 인덱스: channelId -> 참여자 userIds (교육용/재기동 시 초기화됨) */
    private final Map<UUID, Set<UUID>> channelParticipants = new ConcurrentHashMap<>();


    @Override
    public ChannelView createPublic(CreatePublicChannelRequest request) {
        Objects.requireNonNull(request, "request");
        if (request.name == null || request.name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, request.name, request.description));
        return toView(channel);
    }

    @Override
    public ChannelView createPrivate(CreatePrivateChannelRequest request) {
        Objects.requireNonNull(request, "request");
        List<UUID> participants = Optional.ofNullable(request.participantUserIds).orElseGet(List::of);
        Channel channel = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

        // ReadStatus 생성 + 인덱스 등록
        Instant now = Instant.now();
        Set<UUID> set = new HashSet<>();
        for (UUID uid : participants) {
            if (uid == null) continue;
            readStatusRepository.save(ReadStatus.create(uid, channel.getId(), now));
            set.add(uid);
        }
        if (!set.isEmpty()) {
            channelParticipants.put(channel.getId(), set);
        }

        return toView(channel);
    }


    @Override
    public ChannelView find(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId");
        Channel ch = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));
        return toView(ch);
    }

    @Override
    public List<ChannelView> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId");

        // PUBLIC: 전체
        List<Channel> all = channelRepository.findAll();
        List<Channel> publics = all.stream()
                .filter(ch -> ch.getType() == ChannelType.PUBLIC)
                .collect(Collectors.toList());

        // PRIVATE: 사용자가 속한 채널만 (ReadStatus 레포 제공 메서드 활용)
        Set<UUID> myPrivateChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        List<Channel> privates = all.stream()
                .filter(ch -> ch.getType() == ChannelType.PRIVATE && myPrivateChannelIds.contains(ch.getId()))
                .collect(Collectors.toList());

        return concat(publics, privates).stream().map(this::toView).collect(Collectors.toList());
    }



    @Override
    public ChannelView update(UpdateChannelRequest request) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(request.channelId, "channelId");

        Channel ch = channelRepository.findById(request.channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + request.channelId));

        if (ch.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        ch.update(request.newName, request.newDescription);
        ch = channelRepository.save(ch);
        return toView(ch);
    }


    @Override
    public void delete(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId");
        Channel ch = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

        // 1) 메시지 삭제
        messageRepository.findAll().stream()
                .filter(m -> Objects.equals(m.getChannelId(), channelId))
                .map(Message::getId)
                .forEach(messageRepository::deleteById);

        // 2) ReadStatus 삭제 (인메모리 인덱스 활용; 재기동 후엔 누락될 수 있음)
        Set<UUID> participants = channelParticipants.getOrDefault(channelId, Set.of());
        for (UUID uid : participants) {
            readStatusRepository.findByUserIdAndChannelId(uid, channelId)
                    .ifPresent(rs -> readStatusRepository.deleteById(rs.getId()));
        }
        channelParticipants.remove(channelId);

        // 3) 채널 삭제
        channelRepository.deleteById(channelId);
    }

    // ============== 내부 유틸 ==============

    private ChannelView toView(Channel ch) {
        // 최신 메시지 시각
        Instant latest = messageRepository.findAll().stream()
                .filter(m -> Objects.equals(m.getChannelId(), ch.getId()))
                .map(Message::getCreatedAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        // PRIVATE 참여자 목록 (인메모리 인덱스 사용; 없으면 빈 리스트)
        List<UUID> participants = (ch.getType() == ChannelType.PRIVATE)
                ? new ArrayList<>(channelParticipants.getOrDefault(ch.getId(), Set.of()))
                : List.of();

        return new ChannelView(
                ch.getId(),
                ch.getType(),
                ch.getName(),
                ch.getDescription(),
                ch.getCreatedAt(),
                ch.getUpdatedAt(),
                latest,
                participants
        );
    }

    private static <T> List<T> concat(List<T> a, List<T> b) {
        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;
        List<T> r = new ArrayList<>(a.size() + b.size());
        r.addAll(a);
        r.addAll(b);
        return r;
    }
}
