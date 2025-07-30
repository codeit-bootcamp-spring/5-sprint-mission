package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User; //User 도메인 모델 import

import java.util.List; // List 인터페이스 import
import java.util.UUID; // UUID 클래스 import
import java.util.Optional; // Optional 클래스 import

//사용자(User) 도메인 모델에 대한 CRUD(생성, 읽기, 수정, 삭제) 작업을 정의하는 인터페이스
public interface UserService {

    //새로운 User를 생성하고 저장한다.
    //@param user 생성할 User 객체
    //@return 생성된 User 객체
    User create(User user);

    //주어진 ID에 해당하는 User를 조회한다.
    //param id 조회할 User의 UUID
    //@return 해당 ID의 User 객체 (존재하지 않으면 Optional.empty())
    Optional<User> findById(UUID userId);

    //모든 User 객체를 조회
    // @return 모든 User 객체의 리스트
    List<User> findAll();

    //주어진 ID에 해당하는 User를 업데이트
    //@param id 업데이트할 User의 UUID
    //@param updatedUser 업데이트할 내용을 담은 User 객체 (id, createdAt 제외)
    //@return 업데이트된 User 객체 (존재하지 않으면 Optional.empty())
    Optional<User> updateId(UUID userId, User updateUser);

    //주어진 ID에 해당하는 User를 삭제
    //@param id 삭제할 User의 UUID
    //@return 삭제 성공 여부 (true: 성공, false: 실패)
    boolean deleteById(UUID userId);

}
