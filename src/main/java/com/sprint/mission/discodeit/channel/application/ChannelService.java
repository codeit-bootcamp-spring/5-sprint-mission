package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelDeletedEvent;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.PrivateChannelCreatedEvent;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.channel.domain.exception.DuplicateChannelException;
import com.sprint.mission.discodeit.channel.domain.exception.ParticipantsNotFoundException;
import com.sprint.mission.discodeit.channel.domain.exception.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.channel.presentation.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.presentation.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.global.cache.CacheHelper;
import com.sprint.mission.discodeit.global.cache.CacheName;
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

    private final CacheHelper cacheHelper;

    private final ChannelMapper channelMapper;
    private final ChannelInfoService channelInfoService;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    @CacheEvict(value = CacheName.PUBLIC_CHANNELS, allEntries = true)
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

    private void checkDuplicateTwoPersonChannel(List<User> participants) {
        if (participants.size() != 2) {
            return;
        }
        UUID userId1 = participants.get(0).getId();
        UUID userId2 = participants.get(1).getId();
        if (channelRepository.existsBetweenUsers(userId1, userId2)) {
            throw new DuplicateChannelException(userId1, userId2);
        }
    }

    private void initializeReadStatuses(Channel channel, List<User> participants, Instant timestamp) {
        List<ReadStatus> readStatuses = participants.stream()
            .map(user -> new ReadStatus(user, channel, timestamp, true))
            .toList();
        readStatusRepository.saveAll(readStatuses);
        eventPublisher.publishEvent(new PrivateChannelCreatedEvent(
            participants.stream()
                .map(User::getId)
                .collect(Collectors.toSet())));
    }

    @Transactional(readOnly = true)
    public List<ChannelDto> findAll(UUID userId) {
        log.debug("채널 목록 조회: userId={}", userId);

        List<ChannelInfoDto> subscribedChannels = channelInfoService.findSubscribedChannels(userId);
        List<ChannelInfoDto> publicChannels = findPublicChannels();

        List<ChannelInfoDto> allChannels = mergeChannels(publicChannels, subscribedChannels);
        if (allChannels.isEmpty()) {
            return List.of();
        }

        return toChannelDtos(allChannels);
    }

    private List<ChannelInfoDto> findPublicChannels() {
        return channelRepository.findAllByType(ChannelType.PUBLIC).stream()
            .map(channelMapper::toChannelInfo)
            .toList();
    }

    private List<ChannelInfoDto> mergeChannels(List<ChannelInfoDto> publicChannels, List<ChannelInfoDto> subscribedChannels) {
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
                Message::getCreatedAt
            ));
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        log.debug("채널 수정 요청: channelId={}", channelId);

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new PrivateChannelUpdateException();
        }

        String newName = hasText(request.newName()) ? request.newName().strip() : null;
        String newDescription = request.newDescription() != null
            ? request.newDescription().strip() : null;
        channel.update(newName, newDescription);

        Instant lastMessageAt = Optional.ofNullable(
                messageRepository.findFirstByChannelOrderByCreatedAtDesc(channel))
            .map(Message::getCreatedAt)
            .orElse(null);

        log.info("채널 수정 완료: channelId={}", channelId);

        return channelMapper.toDto(channel, List.of(), lastMessageAt);
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public void deleteById(UUID channelId) {
        log.debug("채널 삭제 요청: channelId={}", channelId);

        if (!channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(channelId);
        }
        channelRepository.deleteById(channelId);

        log.info("채널 삭제 완료: channelId={}", channelId);

        eventPublisher.publishEvent(new ChannelDeletedEvent(channelId));
    }
}
