package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository; // 프로필 이미지 저장을 위해 추가

  /* 회원가입
   * DTO를 받아서 처리하고, DTO를 반환
   */
  @Override
  @Transactional
  public UserDto create(UserDto dto, MultipartFile profile) throws IOException {
    if (existsByUsername(dto.getUsername())) {
      log.warn("회원가입 실패(중복 username): {}", dto.getUsername());
      throw new IllegalArgumentException("사용자 이름이 이미 존재합니다.");
    }
    if (existsByEmail(dto.getEmail())) {
      log.warn("회원가입 실패(중복 email): {}", dto.getEmail());
      throw new IllegalArgumentException("이메일이 이미 존재합니다.");
    }

    // DTO -> Entity 변환
    User user = userMapper.toEntityForCreate(dto);

    // 프로필 이미지 처리
    if (profile != null && !profile.isEmpty()) {
      BinaryContent profileImage = new BinaryContent(
          profile.getOriginalFilename(),
          profile.getContentType(),
          profile.getSize(),
          profile.getBytes()
      );
      user.setProfile(binaryContentRepository.save(profileImage));
    }

    // DB 저장 후 Entity -> DTO 변환하여 반환
    User createdUser = userRepository.save(user);
    log.info("회원가입 완료: userId={}", createdUser.getId());
    return userMapper.toDto(createdUser);
  }

  /* 단일 조회
   * User -> UserDto 변환
   */
  @Override
  @Transactional
  public UserDto findById(UUID id) {
    log.info("유저 단건조회 시도: userId={}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("유저 단건조회 실패(없음): userId={}", id);
          return new IllegalArgumentException("해당 유저 없음");
        });
    log.info("유저 단건조회 성공: userId={}", id);
    return userMapper.toDto(user);
  }

  /* 전체 유저 조회
   * User -> UserDto 변환
   */
  @Override
  @Transactional
  public List<UserDto> findAll() {
    log.info("전체 유저 목록 조회");
    List<User> users = userRepository.findAll();
    return userMapper.toDtoList(users);
  }

  /* 정보 수정
   * DTO를 받아서 처리하고, DTO를 반환
   */
  @Override
  @Transactional
  public UserDto update(UUID id, UserDto dto, MultipartFile profileImage) throws IOException {
    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("회원정보 수정 실패(없는 유저): userId={}", id);
          return new IllegalArgumentException("해당 유저 없음");
        });

    // Mapper를 통해 엔티티 값 업데이트
    userMapper.updateEntityFromDto(user, dto);

    // 프로필 이미지 수정 로직 (새 이미지가 있다면 기존 이미지 삭제 후 새 이미지 저장)
    if (profileImage != null && !profileImage.isEmpty()) {
      // 기존 프로필 이미지 삭제
      if (user.getProfile() != null) {
        binaryContentRepository.delete(user.getProfile());
      }
      // 새 프로필 이미지 저장
      BinaryContent newProfile = new BinaryContent(
          profileImage.getOriginalFilename(),
          profileImage.getContentType(),
          profileImage.getSize(),
          profileImage.getBytes()
      );
      user.setProfile(binaryContentRepository.save(newProfile));
    }

    // 변경된 엔티티를 DTO로 변환하여 반환 (Dirty Checking으로 자동 업데이트)
    log.info("회원정보 수정 완료: userId={}", id);
    return userMapper.toDto(user);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));
    userRepository.delete(user);
    log.info("회원 탈퇴 완료: userId={}", id);
  }

  @Override
  @Transactional
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  @Transactional
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}