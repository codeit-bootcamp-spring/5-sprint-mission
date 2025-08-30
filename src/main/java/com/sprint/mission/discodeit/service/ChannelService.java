package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.ChannelType;
import com.sprint.mission.discodeit.exception.AccessDeniedException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserService userService;
    private final EntityManager em;

    public List<ChannelDto> findAll(UUID userId) {
        List<Channel> channels = channelRepository.findAllByUserId(userId);
        if (channels.isEmpty()) {
            return List.of();
        }

        List<Channel> privateChannels = channels.stream()
            .filter(c -> c.getType() == ChannelType.PRIVATE)
            .toList();

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIn(privateChannels);

        Map<Channel, List<User>> participantsMap =
            readStatuses.stream().collect(Collectors.groupingBy(
                ReadStatus::getChannel,
                Collectors.mapping(ReadStatus::getUser, Collectors.toList())
            ));

        return channels.stream()
            .map(c -> new ChannelDto(
                c.getId(),
                c.getType(),
                c.getName(),
                c.getDescription(),
                null,
                null
            ))
            .toList();
    }

    @Transactional
    public ChannelDto create(PublicChannelCreateRequest req) {
        String name = req.name().strip();
        String description = req.description() != null ? req.description().strip() : null;
        Channel c = channelRepository.save(new Channel(ChannelType.PUBLIC, name, description));
        return ChannelDto.from(c, List.of(), null);
    }

    // delete user와 경합성 문제를 fk 제약 없이 해결하기 어려움
    @Transactional
    public ChannelDto create(PrivateChannelCreateRequest req) {
        List<UserDto> users = userRepository.findAllByIdIn(req.participantIds(),
            Instant.now().minus(Duration.ofMinutes(5)));

        if (users.size() == 2) {
            UUID userId1 = users.get(0).id();
            UUID userId2 = users.get(1).id();
            if (channelRepository.existsBetweenUsers(userId1, userId2)) {
                throw new DataIntegrityViolationException(
                    "[Key (userId1, userId2)=(%s, %s) already exists.]".formatted(userId1,
                        userId2));
            }
        }

        Instant now = Instant.now();

        Channel c = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));
        List<ReadStatus> readStatuses = users.stream()
            .map(u -> new ReadStatus(em.getReference(User.class, u.id()), c, now))
            .toList();

        readStatusRepository.saveAll(readStatuses);

        return ChannelDto.from(c, users, null);
    }

    @Transactional
    public void delete(UUID channelId) {
        Channel c = channelRepository.getOrThrowForUpdate(channelId);

        // messageAttachmentRepository.deleteAllByChannel(c);

        messageRepository.deleteAllByChannel(c);

        readStatusRepository.deleteAllByChannel(c);

        channelRepository.deleteById(channelId);
    }

    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest req) {
        Channel c = channelRepository.getOrThrow(channelId);

        if (c.getType() == ChannelType.PRIVATE) {
            throw new AccessDeniedException("Private channel cannot be updated");
        }

        String newName = req.newName() != null ? req.newName().strip() : null;
        String newDescription = req.newDescription() != null ? req.newDescription().strip() : null;

        if (newName != null) {
            c.setName(newName);
        }

        if (newDescription != null) {
            c.setDescription(newDescription);
        }

        return ChannelDto.from(c, List.of(), null);
    }
}
