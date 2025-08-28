// package com.sprint.mission.discodeit.dto.user;
//
// import com.fasterxml.jackson.annotation.JsonInclude;
// import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
// import com.sprint.mission.discodeit.entity.User;
// import java.util.UUID;
//
// public record UserDto(
//
//     UUID id,
//     String username,
//     String email,
//
//     @JsonInclude(JsonInclude.Include.ALWAYS)
//     BinaryContentDto profile,
//
//     boolean online
// ) {
//
//   public static UserDto from(User user, UserStatusType status, BinaryContentDto profile) {
//     return new UserDto(
//         user.getId(),
//         user.getUsername(),
//         user.getEmail(),
//         profile,
//         !UserStatusType.OFFLINE.equals(status)
//     );
//   }
// }
