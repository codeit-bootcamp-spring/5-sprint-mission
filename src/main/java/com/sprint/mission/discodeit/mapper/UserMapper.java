package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  // Create 요청용
  public User toEntityForCreate(UserDto dto) {
    return new User(dto.getUsername(), dto.getPassword(), dto.getEmail());
  }

  // Entity → DTO 응답용
  public UserDto toDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setProfileId(user.getProfile() != null ? user.getProfile().getId() : null);
    dto.setOnline(user.getStatus() != null && user.getStatus().isOnline());
    return dto;
  }

  // Entity List → DTO List (전체 조회용)
  public List<UserDto> toDtoList(List<User> users) {
    return users.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  // DTO → Entity 값 업데이트
  public void updateEntityFromDto(User user, UserDto dto) {
    if (dto.getNewUsername() != null) {
      user.setUsername(dto.getNewUsername());
    }
    if (dto.getNewEmail() != null) {
      user.setEmail(dto.getNewEmail());
    }
    if (dto.getNewPassword() != null) {
      user.setPassword(dto.getNewPassword());
    }
  }
}