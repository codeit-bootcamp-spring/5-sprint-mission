// package com.sprint.mission.discodeit.dto.userstatus;
//
// import com.sprint.mission.discodeit.entity.UserStatus;
// import java.time.Instant;
// import java.util.UUID;
//
// public record UserStatusDto(
//
//     UUID id,
//     Instant createdAt,
//     Instant updatedAt,
//     UUID userId,
//     Instant lastActiveAt,
//     boolean online,
//     UserStatusType userStatusType
// ) {
//
//   public static UserStatusDto from(UserStatus us) {
//     return new UserStatusDto(
//         us.getId(),
//         us.getCreatedAt(),
//         us.getUpdatedAt(),
//         us.getUserId(),
//         us.getLastActiveAt(),
//         !UserStatusType.OFFLINE.equals(us.getType()),
//         us.getType()
//     );
//   }
// }
