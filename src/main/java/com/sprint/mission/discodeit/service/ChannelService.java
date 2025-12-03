package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.data.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.data.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.channel.ChannelDeletedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateChannelException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.channel.UsersNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    private final CacheManager cacheManager;
    private final ChannelMapper channelMapper;

    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public ChannelDto create(PublicChannelCreateRequest request) {
        log.debug("공개 채널 생성 요청: name={}, description={}",
            request.name(), request.description());

        Channel savedChannel = channelRepository.save(
            new Channel(
                ChannelType.PUBLIC,
                request.name().strip(),
                request.description() != null ? request.description().strip() : null
            )
        );

        log.info("공개 채널 생성 완료: channelId={}, name={}",
            savedChannel.getId(), savedChannel.getName());

        return channelMapper.toDto(
            savedChannel,
            List.of(),
            null
        );
    }

    @Transactional
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.debug("비공개 채널 생성 요청: participantIds={}", request.participantIds());

        List<User> participants = validateAndFetchParticipants(request.participantIds());
        checkDuplicateTwoPersonChannel(participants);

        Channel savedChannel = channelRepository.save(
            new Channel(
                ChannelType.PRIVATE,
                null,
                null
            )
        );

        initializeReadStatuses(savedChannel, participants, savedChannel.getCreatedAt());

        log.info("비공개 채널 생성 완료: channelId={}", savedChannel.getId());

        return channelMapper.toDto(
            savedChannel,
            participants,
            null
        );
    }

    private List<User> validateAndFetchParticipants(Set<UUID> participantIds) {
        List<User> users = userRepository.findAllByIdIn(participantIds);

        if (users.size() != participantIds.size()) {
            Set<UUID> foundUserIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

            Set<UUID> missingUserIds = participantIds.stream()
                .filter(id -> !foundUserIds.contains(id))
                .collect(Collectors.toSet());

            throw new UsersNotFoundException(missingUserIds);
        }

        return users;
    }

    private void checkDuplicateTwoPersonChannel(List<User> participants) {
        if (participants.size() == 2) {
            UUID userId1 = participants.get(0).getId();
            UUID userId2 = participants.get(1).getId();
            if (channelRepository.existsBetweenUsers(userId1, userId2)) {
                throw new DuplicateChannelException(userId1, userId2);
            }
        }
    }

    private void initializeReadStatuses(Channel channel, List<User> participants, Instant timestamp) {
        List<ReadStatus> readStatuses = participants.stream()
            .map(user -> new ReadStatus(user, channel, timestamp, true))
            .toList();
        readStatusRepository.saveAll(readStatuses);
        evictReadStatusesCacheForUsers(participants);
    }

    private void evictReadStatusesCacheForUsers(List<User> users) {
        Cache cache = cacheManager.getCache("readStatuses");
        if (cache != null) {
            users.forEach(user -> cache.evict(user.getId()));
        }
    }

    @Transactional(readOnly = true)
    public List<ChannelDto> findAll(UUID userId) {
        log.debug("채널 목록 조회: userId={}", userId);

        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(Channel::getId)
            .toList();

        List<Channel> channels = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds);
        if (channels.isEmpty()) {
            return List.of();
        }

        Map<UUID, List<User>> participantsByChannel = buildParticipantsByChannel(channels);
        Map<UUID, Instant> lastMessageAtByChannel = buildLastMessageAtByChannel(channels);

        return channels.stream()
            .map(channel -> channelMapper.toDto(
                channel,
                participantsByChannel.getOrDefault(channel.getId(), List.of()),
                lastMessageAtByChannel.get(channel.getId())
            ))
            .toList();
    }

    private List<ReadStatus> findAllReadStatusesByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    private Map<UUID, List<User>> buildParticipantsByChannel(List<Channel> channels) {
        return readStatusRepository.findAllByChannelIn(channels).stream()
            .collect(Collectors.groupingBy(
                rs -> rs.getChannel().getId(),
                Collectors.mapping(ReadStatus::getUser, Collectors.toList())
            ));
    }

    private Map<UUID, Instant> buildLastMessageAtByChannel(List<Channel> channels) {
        return messageRepository.findLastMessageAtByChannels(channels).stream()
            .collect(Collectors.toMap(
                ChannelLastMessageAtDto::channelId,
                ChannelLastMessageAtDto::lastMessageAt
            ));
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        log.debug("채널 수정 요청: channelId={}", channelId);

        Channel channel = getChannelOrThrow(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new PrivateChannelUpdateException();
        }

        Instant lastMessageAt = Optional.ofNullable(
                messageRepository.findFirstByChannelOrderByCreatedAtDesc(channel))
            .map(Message::getCreatedAt)
            .orElse(null);

        channelRepository.save(updateChannel(channel, request));

        log.info("채널 수정 완료: channelId={}", channelId);

        return channelMapper.toDto(channel, List.of(), lastMessageAt);
    }

    private Channel updateChannel(Channel channel, PublicChannelUpdateRequest request) {
        String newName = null;
        if (hasText(request.newName())) {
            newName = request.newName().strip();
        }

        String newDescription = null;
        if (request.newDescription() != null) {
            newDescription = request.newDescription().strip();
        }

        return channel.update(newName, newDescription);
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public void deleteById(UUID channelId) {
        log.debug("채널 삭제 요청: channelId={}", channelId);

        getChannelOrThrow(channelId);
        channelRepository.deleteById(channelId);

        log.info("채널 삭제 완료: channelId={}", channelId);

        eventPublisher.publishEvent(new ChannelDeletedEvent(channelId));
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }
}
