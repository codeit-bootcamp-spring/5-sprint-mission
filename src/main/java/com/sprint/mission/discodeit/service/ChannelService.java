// package com.sprint.mission.discodeit.service.channel;
//
// import static com.sprint.mission.discodeit.support.StringUtil.nullOrStrip;
//
// import com.sprint.mission.discodeit.domain.enums.ChannelType;
// import com.sprint.mission.discodeit.dto.channel.ChannelDto;
// import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
// import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
// import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
// import com.sprint.mission.discodeit.entity.Channel;
// import com.sprint.mission.discodeit.entity.User;
// import com.sprint.mission.discodeit.exception.AccessDeniedException;
// import com.sprint.mission.discodeit.exception.DuplicateResourceException;
// import com.sprint.mission.discodeit.exception.NotFoundException;
// import com.sprint.mission.discodeit.repository.ChannelRepository;
// import com.sprint.mission.discodeit.repository.MessageRepository;
// import com.sprint.mission.discodeit.repository.UserRepository;
// import java.time.Instant;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.Set;
// import java.util.UUID;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// @Service
// @RequiredArgsConstructor
// @Transactional(readOnly = true)
// public class ChannelService {
//
//   private final ChannelRepository channelRepository;
//   private final UserRepository userRepository;
//   private final MessageRepository messageRepository;
//
//   public List<ChannelDto> findAll(UUID userId) {
//     User u = userRepository.getOrThrow(userId);
//
//     Set<UUID> channelIds = u.getChannelIds();
//
//     List<Channel> myChannels = channelRepository.findAllByIdIn(u.getChannelIds());
//     List<Channel> publicChannels = channelRepository.findAllPublic();
//
//     List<Channel> channels = Stream.concat(myChannels.stream(), publicChannels.stream())
//         .toList();
//
//     if (channels.isEmpty()) {
//       return List.of();
//     }
//
//     Set<UUID> lastMessageIds = channels.stream()
//         .map(Channel::getLastMessageId)
//         .flatMap(Optional::stream)
//         .collect(Collectors.toSet());
//
//     Map<UUID, Instant> lastMessageAtMap = messageRepository.findAllCreatedAtById(lastMessageIds);
//
//     return channels.stream()
//         .map(c -> ChannelDto.from(
//             c,
//             List.of(),
//             c.getLastMessageId()
//                 .map(lastMessageAtMap::get)
//                 .orElse(null))
//         )
//         .toList();
//   }
//
//   @Transactional
//   public ChannelDto create(PublicChannelCreateRequest req) {
//     String name = nullOrStrip(req.name());
//     String description = nullOrStrip(req.description());
//     return ChannelDto.from(
//         channelRepository.save(new Channel(name, description)),
//         List.of(),
//         null
//     );
//   }
//
//   @Transactional
//   public ChannelDto create(PrivateChannelCreateRequest req) {
//     Set<UUID> ids = req.participantIds();
//     List<User> users = userRepository.findAllByIdIn(ids);
//
//     if (users.size() != ids.size()) {
//       Set<UUID> found = users.stream().map(User::getId).collect(Collectors.toSet());
//       UUID missing = ids.stream().filter(id -> !found.contains(id)).findFirst().orElse(null);
//       throw new NotFoundException("User with id %s not found".formatted(missing));
//     }
//
//     if (users.size() == 2) {
//       UUID userId1 = users.get(0).getId();
//       UUID userId2 = users.get(1).getId();
//       if (channelRepository.existsBetween(userId1, userId2)) {
//         throw new DuplicateResourceException(
//             "Private channel between %s, %s already exists".formatted(userId1, userId2));
//       }
//     }
//
//     Channel c = channelRepository.save(new Channel(ids));
//
//     users.forEach(u -> u.joinChannel(c.getId()));
//     userRepository.saveAll(users);
//
//     return ChannelDto.from(
//         c,
//         List.of(),
//         null
//     );
//   }
//
//   @Transactional
//   public void delete(UUID channelId) {
//     if (!channelRepository.delete(channelId)) {
//       throw new NotFoundException("Channel with id %s not found".formatted(channelId));
//     }
//   }
//
//   @Transactional
//   public ChannelDto update(UUID channelId, PublicChannelUpdateRequest req) {
//     Channel c = channelRepository.getOrThrow(channelId);
//
//     if (c.getType() == ChannelType.PRIVATE) {
//       throw new AccessDeniedException("Private channel cannot be updated");
//     }
//
//     String name = nullOrStrip(req.newName());
//     String description = nullOrStrip(req.newDescription());
//
//     if (name == null && description == null) {
//       return ChannelDto.from(
//           c,
//           List.of(),
//           null
//       );
//     }
//
//     return ChannelDto.from(
//         channelRepository.save(c.update(name, description)),
//         List.of(),
//         null
//     );
//   }
// }
