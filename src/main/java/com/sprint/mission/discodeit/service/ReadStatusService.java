// package com.sprint.mission.discodeit.service.readstatus;
//
// import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
// import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
// import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
// import com.sprint.mission.discodeit.entity.ReadStatus;
// import com.sprint.mission.discodeit.exception.DuplicateResourceException;
// import com.sprint.mission.discodeit.repository.ChannelRepository;
// import com.sprint.mission.discodeit.repository.ReadStatusRepository;
// import com.sprint.mission.discodeit.repository.UserRepository;
// import java.util.List;
// import java.util.UUID;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// @Service
// @RequiredArgsConstructor
// @Transactional(readOnly = true)
// public class ReadStatusService {
//
//   private final UserRepository userRepository;
//   private final ReadStatusRepository readStatusRepository;
//   private final ChannelRepository channelRepository;
//
//   public List<ReadStatusDto> findAllByUserId(UUID userId) {
//     userRepository.getOrThrow(userId);
//     return readStatusRepository.findAllByUserId(userId).stream()
//         .map(ReadStatusDto::from)
//         .toList();
//   }
//
//   @Transactional
//   public ReadStatusDto create(ReadStatusCreateRequest req) {
//     userRepository.getOrThrow(req.userId());
//     channelRepository.getOrThrow(req.channelId());
//
//     readStatusRepository.findByUserIdAndChannelId(req.userId(), req.channelId())
//         .ifPresent(rs -> {
//           throw new DuplicateResourceException(
//               "ReadStatus with userId %s and channelId %s already exists."
//                   .formatted(req.userId(), req.channelId()));
//         });
//
//     return ReadStatusDto.from(
//         readStatusRepository.save(new ReadStatus(req.userId(), req.channelId(), req.lastReadAt()))
//     );
//   }
//
//   @Transactional
//   public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest req) {
//     ReadStatus rs = readStatusRepository.getOrThrow(readStatusId);
//
//     return ReadStatusDto.from(
//         readStatusRepository.save(rs.update(req.newLastReadAt()))
//     );
//   }
// }
