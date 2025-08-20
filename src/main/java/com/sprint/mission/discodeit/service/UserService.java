package com.sprint.mission.discodeit.service;

//인터페이스
//기능의 약속을 정의하며, 다중구현이 가능
//CRUD(생성,읽기,모두읽기,수정,삭제) 기능 구현하기


import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    //약속
    //다중 구현 가능

    User create(UserCreateRequest request, MultipartFile profileImage);

    UserResponse findById(UUID id); //조회

    List<User> findAll(); //리스트에 넣기

    User update(UserUpdateRequest request, MultipartFile profileImage) throws IOException;

    void delete(UUID id); //삭제

    boolean existsByUsername(String userId); // username 중복 확인

    boolean existsByEmail(String email);       // email 중복 확인

    Optional<User> findEntityById(UUID id);
}
