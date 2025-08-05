package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserFindRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public User create(UserCreateRequest request) {

        User user = userRepository.findById(request.id()).orElseThrow(()->
                new IllegalArgumentException("User id not found"));

        // username, email 중복 체크
        if(user.getUsername().equals(request.username())){
            throw new IllegalArgumentException("Username is already in use");
        }
        if(user.getEmail().equals(request.email())){
            throw new IllegalArgumentException("Email is already in use");
        }

        // 유저 생성
        User newUser = new User(request.username(), request.email(), request.password());
        userRepository.save(newUser);

        // UserStatus 생성
        UserStatus status = new UserStatus(newUser.getId());
        userStatusRepository.save(status);

        return user;
    }

    // DTO를 활용하여:
    // 사용자의 온라인 상태 정보를 같이 포함하세요.
    // 패스워드 정보는 제외하세요.
    @Override
    public UserFindResponse find(UserFindRequest request) {

        User user = userRepository.findById(request.id()).orElseThrow(()->
                new IllegalArgumentException("User id not found"));

        UserStatus status = userStatusRepository.findById(request.id()).orElseThrow(()->
                new IllegalArgumentException("User status not found"));

        UserFindResponse response = new UserFindResponse(user.getId(),
                user.getUsername(),
                user.getEmail(),
                status.isOnline());
        return response;
    }


    @Override
    public List<UserFindResponse> findAll() {
        List<User> users = userRepository.findAll();
        List<UserFindResponse> responses = new ArrayList<>();

        for (User user : users) {
            UserStatus status = userStatusRepository.findById(user.getId()).orElseThrow(()->
                    new IllegalArgumentException("User status not found"));
            responses.add(new UserFindResponse(user.getId(), user.getUsername(), user.getEmail(), status.isOnline()));
        }
        return responses;
    }

    @Override
    public User update(UserUpdateRequest request) {
        User user = userRepository.findById(request.id()).orElseThrow(()->
                new IllegalArgumentException("User with id "+ request.id() +" not found"));
        user.update(request.username(),
                request.email(),
                request.password(),
                request.binaryContent());
        userRepository.save(user);

        // 프로필 이미지 업데이트 선택사항....
        if(request.binaryContent() != null){
            BinaryContent binaryContent = request.binaryContent();
            binaryContentRepository.save(binaryContent);
        }
        return user;
    }

    // 관련 도메인 모두 삭제 - BinaryContent(프로필), UserStatus
    @Override
    public void delete(UUID userId) {
        userRepository.deleteById(userId);
        userStatusRepository.delete(userId);
        binaryContentRepository.deleteById(userId);
    }
}
