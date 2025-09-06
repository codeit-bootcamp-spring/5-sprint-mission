package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;


/* 하나의 UserDto로 통합 후
 * Mapper 클래스가 Entity <-> DTO 변환 책임 가짐
 */

@Component
public class UserMapper {

  //Create 요청용
  public User toEntityForCreate(UserDto dto) {
    return new User(dto.getUsername(), dto.getPassword(), dto.getEmail());
  }

  //Entity -> Dto 응답용
  public UserDto toDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setEmail(user.getEmail());
    dto.setProfileId(user.getProfile() != null ? user.getProfile().getId() : null);
    dto.setOnline(user.getStatus() != null && user.getStatus().isOnline());
    return dto;
  }


}
