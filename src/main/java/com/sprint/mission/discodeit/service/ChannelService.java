package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.ChannelType;
import com.sprint.mission.discodeit.exception.AccessDeniedException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    private final ChannelMapper channelMapper;

    private static void updateChannel(Channel channel, PublicChannelUpdateRequest request) {
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

    @Transactional
    public ChannelDto create(PublicChannelCreateRequest request) {
        log.info("Creating public channel. Name: {}, Description: {}", request.name(), request.description());

        String name = request.name().strip();
        String description = request.description() != null ? request.description().strip() : null;

        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, name, description)
        );

        log.info("Public channel created successfully. ChannelId: {}", channel.getId());

        return channelMapper.toDto(
            channel,
            List.of(),
            null,
            null
        );
    }

    @Transactional
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.info("Creating private channel. ParticipantIds: {}", request.participantIds());

        List<User> users = userRepository.findAllByIdIn(request.participantIds());
        if (users.size() != request.participantIds().size()) {
            Set<UUID> missingIds = request.participantIds().stream()
                .filter(id -> users.stream().noneMatch(user -> user.getId().equals(id)))
                .collect(Collectors.toSet());

            throw new NotFoundException("Users not found: " + missingIds);
        }

        if (users.size() == 2) {
            UUID userId1 = users.get(0).getId();
            UUID userId2 = users.get(1).getId();
            if (channelRepository.existsBetweenUsers(userId1, userId2)) {
                throw new DataIntegrityViolationException("[Key (userId1, userId2)=(%s, %s) already exists.]"
                    .formatted(userId1, userId2));
            }
        }

        Instant now = Instant.now();

        Channel channel = channelRepository.save(
            new Channel(
                ChannelType.PRIVATE,
                null,
                null
            )
        );

        List<ReadStatus> readStatuses = users.stream()
            .map(user -> new ReadStatus(user, channel, now))
            .toList();
        readStatusRepository.saveAll(readStatuses);

        return channelMapper.toDto(
            channel,
            users,
            null,
            now.minus(Duration.ofMinutes(5))
        );
    }

    public List<ChannelDto> findAll(UUID userId) {
        log.debug("Finding all channels for user, UserId: {}", userId);
        List<Channel> channels = channelRepository.findAllByUserId(userId);
        if (channels.isEmpty()) {
            return List.of();
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIn(channels);

        Map<UUID, List<UUID>> channelToUserIds = readStatuses.stream()
            .collect(Collectors.groupingBy(
                rs -> rs.getChannel().getId(),
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

        Instant onlineSince = Instant.now().minus(Duration.ofMinutes(5));

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
                    channelToLastMessageAt.get(channel.getId()),
                    onlineSince
                );
            })
            .toList();
    }

    @Transactional
    public ChannelDto update(
        UUID channelId,
        PublicChannelUpdateRequest request
    ) {
        log.info("Updating channel. ChannelId: {}", channelId);
        Channel channel = channelRepository.getOrThrow(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new AccessDeniedException("Private channel cannot be updated");
        }

        updateChannel(channel, request);

        Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel.getId());
        Instant onlineSince = Instant.now().minus(Duration.ofMinutes(5));

        return channelMapper.toDto(channel, new ArrayList<>(), lastMessageAt, onlineSince);
    }

    @Transactional
    public void delete(UUID channelId) {
        Channel c = channelRepository.getOrThrow(channelId);

        messageRepository.deleteAllByChannelId(c.getId());
        readStatusRepository.deleteAllByChannelId(c.getId());

        channelRepository.delete(c);
    }
}
