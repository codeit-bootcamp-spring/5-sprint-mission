package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.application.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.channel.domain.exception.DuplicateChannelException;
import com.sprint.mission.discodeit.channel.domain.exception.ParticipantsNotFoundException;
import com.sprint.mission.discodeit.channel.domain.exception.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.channel.presentation.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.presentation.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import com.sprint.mission.discodeit.message.domain.Message;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelInfoService channelInfoService;
    private final ChannelMapper channelMapper;

    private final CacheService cacheService;
    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    @CacheEvict(value = CacheName.PUBLIC_CHANNELS, allEntries = true)
    public ChannelDto create(PublicChannelCreateRequest request) {
        String name = request.name().strip();
        String description = request.description() != null
            ? request.description().strip() : null;

        log.debug("Creating public channel: [name={}, description={}]", name, description);

        Channel savedChannel = channelRepository.save(
            new Channel(
                ChannelType.PUBLIC,
                name,
                description
            )
        );

        ChannelDto result = channelMapper.toDto(
            savedChannel,
            List.of(),
            null
        );

        log.info("Public channel created: [channelId={}, name={}, description={}]",
            savedChannel.getId(), savedChannel.getName(), savedChannel.getDescription());

        return result;
    }

    @Transactional
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.info("Creating private channel: [participantIds={}]", request.participantIds());

        List<User> participants = validateAndFetchParticipants(request.participantIds());
        checkDuplicateTwoPersonChannel(participants);

        Channel savedChannel = channelRepository.save(
            new Channel(
                ChannelType.PRIVATE,
                null,
                null
            )
        );

        List<ReadStatus> readStatuses = participants.stream()
            .map(user -> new ReadStatus(
                user,
                savedChannel,
                savedChannel.getCreatedAt(),
                true
            ))
            .toList();

        readStatusRepository.saveAll(readStatuses);

        ChannelDto result = channelMapper.toDto(
            savedChannel,
            participants,
            null
        );

        cacheService.evictAll(CacheName.READ_STATUSES, request.participantIds());
        cacheService.evictAll(CacheName.SUBSCRIBED_CHANNELS, request.participantIds());

        log.info("Private channel created: [channelId={}, participantIds={}]",
            savedChannel.getId(), request.participantIds());

        return result;
    }

    private List<User> validateAndFetchParticipants(Set<UUID> participantIds) {
        List<User> users = userRepository.findAllWithProfileByIdIn(participantIds);

        if (users.size() != participantIds.size()) {
            Set<UUID> foundUserIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

            Set<UUID> missingUserIds = participantIds.stream()
                .filter(id -> !foundUserIds.contains(id))
                .collect(Collectors.toSet());

            throw new ParticipantsNotFoundException(missingUserIds);
        }

        return users;
    }

    // 분산 락(Redisson) 필요
    private void checkDuplicateTwoPersonChannel(List<User> participants) {
        if (participants.size() != 2) {
            return;
        }

        List<UUID> sortedIds = participants.stream()
            .map(User::getId)
            .sorted()
            .toList();

        UUID userId1 = sortedIds.get(0);
        UUID userId2 = sortedIds.get(1);

        if (channelRepository.existsBetweenUsers(userId1, userId2)) {
            throw new DuplicateChannelException(userId1, userId2);
        }
    }

    @Transactional(readOnly = true)
    public List<ChannelDto> findAll(UUID userId) {
        log.debug("Finding channels: [userId={}]", userId);

        List<ChannelInfoDto> subscribedChannels = channelInfoService.findSubscribedChannels(userId);
        List<ChannelInfoDto> publicChannels = channelInfoService.findPublicChannels();

        List<ChannelInfoDto> allChannels = mergeChannels(publicChannels, subscribedChannels);
        if (allChannels.isEmpty()) {
            log.info("No channels found: [userId={}]", userId);

            return List.of();
        }

        List<ChannelDto> result = toChannelDtos(allChannels);

        log.info("Channels found: [userId={}, channelCount={}]", userId, result.size());

        return result;
    }


    private List<ChannelInfoDto> mergeChannels(
        List<ChannelInfoDto> publicChannels,
        List<ChannelInfoDto> subscribedChannels
    ) {
        Set<UUID> publicChannelIds = publicChannels.stream()
            .map(ChannelInfoDto::id)
            .collect(Collectors.toSet());

        List<ChannelInfoDto> privateSubscribed = subscribedChannels.stream()
            .filter(channel -> !publicChannelIds.contains(channel.id()))
            .toList();

        List<ChannelInfoDto> result = new ArrayList<>(publicChannels);
        result.addAll(privateSubscribed);
        return result;
    }

    private List<ChannelDto> toChannelDtos(List<ChannelInfoDto> channels) {
        List<UUID> channelIds = channels.stream().map(ChannelInfoDto::id).toList();

        Map<UUID, List<User>> participantsByChannel = buildParticipantsByChannelIdIn(channelIds);
        Map<UUID, Instant> lastMessageAtByChannel = buildLastMessageAtByChannelIdIn(channelIds);

        return channels.stream()
            .map(channel -> channelMapper.toDtoByInfo(
                channel,
                participantsByChannel.getOrDefault(channel.id(), List.of()),
                lastMessageAtByChannel.get(channel.id())
            ))
            .sorted(Comparator.comparing(
                ChannelDto::lastMessageAt,
                Comparator.nullsLast(Comparator.reverseOrder()))
            )
            .toList();
    }

    private Map<UUID, List<User>> buildParticipantsByChannelIdIn(List<UUID> channelIds) {
        return readStatusRepository.findAllWithUserProfileByChannelIdIn(channelIds).stream()
            .collect(Collectors.groupingBy(
                rs -> rs.getChannel().getId(),
                Collectors.mapping(ReadStatus::getUser, Collectors.toList())
            ));
    }

    private Map<UUID, Instant> buildLastMessageAtByChannelIdIn(List<UUID> channelIds) {
        return messageRepository.findLastMessageByChannelIdIn(channelIds).stream()
            .collect(Collectors.toMap(
                message -> message.getChannel().getId(),
                Message::getCreatedAt,
                (existing, replacement) -> existing
            ));
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    @CacheEvict(value = CacheName.PUBLIC_CHANNELS, allEntries = true)
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = hasText(request.newName()) ? request.newName().strip() : null;
        String newDescription = request.newDescription() != null
            ? request.newDescription().strip() : null;

        log.debug("Updating channel: [channelId={}, newName={}, newDescription={}]",
            channelId, newName, newDescription);

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new PrivateChannelUpdateException();
        }

        channel.update(newName, newDescription);

        Instant lastMessageAt = messageRepository.findLastCreatedAtByChannelId(channelId)
            .orElse(null);

        ChannelDto result = channelMapper.toDto(
            channel,
            List.of(),
            lastMessageAt
        );

        log.info("Channel updated: channelId={}, newName={}, newDescription={}",
            channelId, newName, newDescription);

        return result;
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public void deleteById(UUID channelId) {
        log.debug("Deleting channel: channelId={}", channelId);

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));
        ChannelType channelType = channel.getType();

        channelRepository.delete(channel);

        eventPublisher.publishEvent(
            new ChannelDeletedEvent(
                channelId,
                channelType
            )
        );

        log.info("Channel deleted: [channelType={}, channelId={}]", channelType, channelId);
    }
}
