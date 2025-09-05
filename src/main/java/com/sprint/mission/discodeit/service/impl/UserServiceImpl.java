package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  /*회원가입
   * username, email, password
   */
  @Override
  public User create(UserCreateRequest request, MultipartFile profile) {
    User user = new User(request.getUsername(), request.getPassword(), request.getEmail());
    return userRepository.save(user);
  }

  /*단일 조회
   * User -> UserDto 변환
   * 영속성 컨텍스트에 엔티티 올림
   */
  @Override
  @Transactional
  public UserDto findById(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));
    return UserDto.fromEntity(user);
  }

  /* 전체 유저 조회
   *  User -> UserDto 변환
   * 영속성 컨텍스트에 엔티티 올림
   * */
  @Override
  @Transactional
  public List<UserDto> findAll() {
    return userRepository.findAll().stream() //1. DB에서 모든 UserEntity 꺼내옴
        .map(UserDto::fromEntity) //2. 각각의 User 엔티티를 UserDto로 변환
        .collect(Collectors.toList()); //3. 변환된 UserDto들을 리스트로 모음
  }


  /* 정보 수정
   * findById로 영속성 컨텍스트에 올린후
   * set~메서드로 필드값만 수정
   * save 호출X,
   * 트랜잭션 끝나면 자동 DB update
   */
  @Override
  @Transactional
  public User update(UUID id, UserUpdateRequest request, MultipartFile profileImage)
      throws IOException {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));

    //값만 바꿔주면 트랜잭션 끝날때 자동 update
    if (request.getNewUsername() != null) {
      user.setUsername(request.getNewUsername());
    }
    if (request.getNewEmail() != null) {
      user.setEmail(request.getNewEmail());
    }
    if (request.getNewPassword() != null) {
      user.setPassword(request.getNewPassword());
    }

    //트랜잭션 끝, JPA가 알아서 update 쿼리 실행
    // DB에 최신 내용 제출
    return user; // Dirty checking
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));
    userRepository.delete(user);
  }


  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}
