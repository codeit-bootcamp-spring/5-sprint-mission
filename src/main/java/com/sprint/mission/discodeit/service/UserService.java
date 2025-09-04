package com.sprint.mission.discodeit.service;

//인터페이스
//기능의 약속을 정의하며, 다중구현이 가능
//CRUD(생성,읽기,모두읽기,수정,삭제) 기능 구현하기


import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  User create(UserCreateRequest request, MultipartFile profile);

  UserDto findById(UUID id);

  List<UserDto> findAll(); //전체 조회후 응답용 DTO로 반환

  User update(UUID id, UserUpdateRequest request, MultipartFile profileImage)
      throws IOException;

  void delete(UUID id); //삭제

  boolean existsByUsername(String username); // username 중복 확인

  boolean existsByEmail(String email);       // email 중복 확인

  Optional<User> findEntityById(UUID id);
}
