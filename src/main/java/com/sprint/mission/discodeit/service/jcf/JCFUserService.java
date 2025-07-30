package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User; // User 도메인 모델 import
import com.sprint.mission.discodeit.service.UserService; // UserService 인터페이스 import
import java.util.ArrayList; // ArrayList 클래스 import
import java.util.List; // List 인터페이스 import
import java.util.Map; // Map 인터페이스 import
import java.util.Optional; // Optional 클래스 import
import java.util.UUID; // UUID 클래스 import
import java.util.concurrent.ConcurrentHashMap; // ConcurrentHashMap 클래스 import (멀티스레드 환경 고려)

//Java Collections Framework (JCF)를 활용하여 User 데이터를 관리하는 UserService 구현체.
//데이터는 메모리(Map)에 저장된다.

public class JCFUserService implements UserService {
    // User 데이터를 저장할 Map (UUID를 키로, User 객체를 값으로 사용)
    // final로 선언하여 생성자에서만 초기화되도록 합니다.
    private final Map<UUID, User> data; //필드명 data로 통일해서 사용

    //JCFUserService의 생성자
    //데이터 저장을 위해 Map을 초기화한다.
    public JCFUserService() {
        this.data = new ConcurrentHashMap<>(); //ConcurrentHashMap을 사용하여 스레드 안전성 확보
    }

    //새로운 User를 생성하고 Map에 저장한다.
    //@param user 생성할 User 객체
    //@return 생성된 User 객체
    @Override // 인터페이스 메서드를 오버라이드함을 명시
    public User create(User user) {
        if (user == null || data.containsKey(user.getUserId())) {
            //유효하지 않는 user 객체이거나 이미 존재하는 유저id의 경우 예외 처리 또는 null로 반환
            System.err.println("오류: User 생성에 실패했습니다. 사용자가 null이거나 User ID가 이미 존재합니다:"+ (user != null ? user.getUserId() : "null"));
            return null;
        }
        data.put(user.getUserId(), user); //Map에 User 객체 저장
        System.out.println("유저 생성 : "+ user);
        return user;
    }

    //주어진 userId에 해당하는 User를 Map에서 조회합니다.
    // @param userId 조회할 User의 UUID
    // @return 해당 userId의 User 객체 (존재하지 않으면 Optional.empty())
    @Override // 인터페이스 메서드를 오버라이드함을 명시
    public Optional<User> findById(UUID userId) {
        if (userId == null) {
            System.err.println("오류: findById에 실패했습니다. User ID가 null입니다.");
            return Optional.empty();
        }
        return Optional.ofNullable(data.get(userId)); // Map에서 ID에 해당하는 User를 찾아 Optional로 반환
    }


    //Map에 저장된 모든 User 객체를 조회합니다.
    //@return 모든 User 객체의 리스트
    @Override // 인터페이스 메서드를 오버라이드함을 명시
    public List<User> findAll() {
        return new ArrayList<>(data.values()); // Map의 모든 값(User 객체)들을 ArrayList로 변환하여 반환
    }

    //주어진 ID에 해당하는 User를 Map에서 업데이트합니다.
    //User 객체의 update 메서드를 호출하여 필드를 수정하고, updatedAt을 업데이트합니다.
    //@param userId 업데이트할 User의 UUID
    //@param updatedUser 업데이트할 내용을 담은 User 객체 (userId, createdAt 제외)
    //@return 업데이트된 User 객체 (존재하지 않으면 Optional.empty())
    @Override // 인터페이스 메서드를 오버라이드함을 명시
    public Optional<User> updateId(UUID userId, User updatedUser) {
        if (userId == null || updatedUser == null) {
            System.err.println("오류: User 업데이트에 실패했습니다. User ID 또는 updateUser가 null입니다.");
            return Optional.empty();
        }
        return Optional.ofNullable(data.get(userId))
                .map(existingUser -> {
                    // 기존 User 객체의 update 메서드를 호출하여 필드 업데이트
                    existingUser.updateUser(updatedUser.getUserName(), updatedUser.getEmail());
                    System.out.println("사용자 업데이트: " + existingUser);
                    return existingUser;
                });

    }

   //주어진 ID에 해당하는 User를 Map에서 삭제합니다.
   //@param id 삭제할 User의 UUID
   //@return 삭제 성공 여부 (true: 성공, false: 실패)
   @Override // 인터페이스 메서드를 오버라이드함을 명시
   public boolean deleteById(UUID userId) {
        if (userId == null) {
            System.err.println("오류: User 삭제에 실패했습니다. User ID가 null입니다.");
            return false;
        }
        User removedUser = data.remove(userId); // Map에서 ID에 해당하는 User 삭제
        if (removedUser != null) {
            System.out.println("User 삭제: " + removedUser);
            return true; // 삭제 성공
        }
        System.out.println("User ID: " + userId + "인 User가 존재하지 않아 삭제 실패하였습니다.");
        return false; // 해당 ID의 User가 존재하지 않아 삭제 실패
   }
}
