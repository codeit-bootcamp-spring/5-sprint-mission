package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;

  // online 값을 알고 있을 때
  public @Nullable UserDto toDto(@Nullable User user, @Nullable Boolean online) {
    if (user == null) return null; // 필요시 IllegalArgumentException으로 바꿔도 OK
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        binaryContentMapper.toDto(user.getProfile()), // profile == null이어도 안전(앞서 null-safe로 수정)
        online
    );
  }

  // online 값을 아직 모르면 null
  public @Nullable UserDto toDto(@Nullable User user) {
    return toDto(user, null);
  }

  //컬렉션 변환 유틸(빈/NULL 안전)
  public List<UserDto> toDtoList(@Nullable Collection<User> users) {
    if (users == null || users.isEmpty()) return List.of();
    return users.stream()
        .map(this::toDto)
        .filter(Objects::nonNull)
        .toList();
  }
}
