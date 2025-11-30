package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.data.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.data.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {

    private static final Duration ONLINE_STATUS_THRESHOLD = Duration.ofMinutes(5);

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @CacheEvict(value = "userChannels", allEntries = true)
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

    @CacheEvict(value = "userChannels", allEntries = true)
    @Transactional
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.debug("비공개 채널 생성 요청: participantIds={}", request.participantIds());

        List<User> participants = validateAndFetchParticipants(request.participantIds());
        checkDuplicateTwoPersonChannel(participants);

        Instant now = Instant.now();

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

    @Cacheable(value = "userChannels", key = "#userId")
    @Transactional(readOnly = true)
    public List<ChannelDto> findAll(UUID userId) {
        log.debug("사용자 채널 목록 캐시 미스: userId={}", userId);
        List<Channel> channels = channelRepository.findAllByUserId(userId);
        if (channels.isEmpty()) {
            return List.of();
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIn(channels);

        Map<UUID, List<UUID>> channelToUserIds = readStatuses.stream()
            .collect(Collectors.groupingBy(
                readStatus -> readStatus.getChannel().getId(),
                Collectors.mapping(rs -> rs.getUser().getId(), Collectors.toList())
            ));

        Set<UUID> allUserIds = channelToUserIds.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet());
        Map<UUID, User> userMap = allUserIds.isEmpty()
            ? Map.of()
            : userRepository.findAllByIdIn(allUserIds).stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

        Map<UUID, Instant> channelToLastMessageAt =
            messageRepository.findLastMessageAtByChannels(channels).stream()
                .collect(Collectors.toMap(
                    ChannelLastMessageAtDto::channelId,
                    ChannelLastMessageAtDto::lastMessageAt
                ));

        return channels.stream()
            .map(channel -> {
                List<User> participants = channelToUserIds
                    .getOrDefault(channel.getId(), List.of()).stream()
                    .map(userMap::get)
                    .filter(Objects::nonNull)
                    .toList();

                return channelMapper.toDto(
                    channel,
                    participants,
                    channelToLastMessageAt.get(channel.getId())
                );
            })
            .toList();
    }

    @CacheEvict(value = "userChannels", allEntries = true)
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public ChannelDto update(
        UUID channelId,
        PublicChannelUpdateRequest request
    ) {
        log.debug("채널 수정 요청: channelId={}", channelId);

        Channel channel = getChannelOrThrow(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new PrivateChannelUpdateException();
        }

        updateChannel(channel, request);

        log.info("채널 수정 완료: channelId={}", channelId);

        Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel.getId());

        return channelMapper.toDto(channel, new ArrayList<>(), lastMessageAt);
    }

    @CacheEvict(value = "userChannels", allEntries = true)
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public void delete(UUID channelId) {
        log.debug("채널 삭제 요청: channelId={}", channelId);

        Channel channel = getChannelOrThrow(channelId);

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.delete(channel);

        log.info("채널 삭제 완료: channelId={}", channelId);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    private void updateChannel(
        Channel channel,
        PublicChannelUpdateRequest request
    ) {
        String newName = null;
        if (hasText(request.newName())) {
            newName = request.newName().strip();
        }

        String newDescription = null;
        if (request.newDescription() != null) {
            newDescription = request.newDescription().strip();
        }

        channel.update(
            newName,
            newDescription
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
    }
}
