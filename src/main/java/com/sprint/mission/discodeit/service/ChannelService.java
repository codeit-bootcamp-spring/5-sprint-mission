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
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final EntityManager em;

    private final ChannelMapper channelMapper;

    public List<ChannelDto> findAll(UUID userId) {
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
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<UUID, Instant> channelToLastMessageAt =
            messageRepository.findLastMessageAtByChannels(channels).stream()
                .collect(Collectors.toMap(
                    ChannelLastMessageAtDto::channelId,
                    ChannelLastMessageAtDto::lastMessageAt
                ));

        Instant onlineSince = Instant.now().minus(Duration.ofMinutes(5));

        return channels.stream()
            .map(c -> {
                List<User> participants = channelToUserIds
                    .getOrDefault(c.getId(), List.of()).stream()
                    .map(userMap::get)
                    .filter(Objects::nonNull)
                    .toList();

                return channelMapper.toDto(
                    c,
                    participants,
                    channelToLastMessageAt.get(c.getId()),
                    onlineSince
                );
            })
            .toList();
    }

    @Transactional
    public ChannelDto create(PublicChannelCreateRequest req) {
        String name = req.name().strip();
        String description = req.description() != null ? req.description().strip() : null;
        Channel c = channelRepository.save(new Channel(ChannelType.PUBLIC, name, description));
        return channelMapper.toDto(c, List.of(), null, null);
    }

    @Transactional
    public ChannelDto create(PrivateChannelCreateRequest req) {
        List<User> users = userRepository.findAllByIdIn(req.participantIds());

        if (users.size() == 2) {
            UUID userId1 = users.get(0).getId();
            UUID userId2 = users.get(1).getId();
            if (channelRepository.existsBetweenUsers(userId1, userId2)) {
                throw new DataIntegrityViolationException(
                    "[Key (userId1, userId2)=(%s, %s) already exists.]".formatted(userId1,
                        userId2));
            }
        }

        Instant now = Instant.now();

        Channel c = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

        List<ReadStatus> readStatuses = users.stream()
            .map(u -> new ReadStatus(u, c, now))
            .toList();
        readStatusRepository.saveAll(readStatuses);

        return channelMapper.toDto(c, users, null, now.minus(Duration.ofMinutes(5)));
    }

    @Transactional
    public void delete(UUID channelId) {
        channelRepository.deleteById(channelId);
    }

    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest req) {
        Channel c = channelRepository.getOrThrowForUpdate(channelId);

        if (c.getType() == ChannelType.PRIVATE) {
            throw new AccessDeniedException("Private channel cannot be updated");
        }

        String newName = req.newName() != null ? req.newName().strip() : null;
        if (newName != null && !newName.isBlank()) {
            c.setName(newName);
        }

        if (req.newDescription() != null) {
            c.setDescription(req.newDescription().strip());
        }

        List<User> participants = readStatusRepository.findUsersByChannel(c);
        Instant lastMessageAt = messageRepository.findLastMessageAtByChannel(c);
        Instant onlineSince = Instant.now().minus(Duration.ofMinutes(5));

        return channelMapper.toDto(c, participants, lastMessageAt, onlineSince);
    }
}
