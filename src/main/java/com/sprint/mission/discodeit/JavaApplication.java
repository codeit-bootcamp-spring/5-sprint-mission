package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.UUID;

public class JavaApplication {

    static void userCRUDTest(UserService userService) {
        // 생성
        User user = new User("김유민");
        userService.create(user);
        System.out.println("[생성] " + user);

        // 단건 조회 - 성공
        User found = userService.read(user.getId());
        System.out.println("[조회 - 성공] " + found);

        // 단건 조회 - 실패 (없는 ID)
        UUID fakeId = UUID.randomUUID();
        User notFound = userService.read(fakeId);
        System.out.println("[조회 - 실패] ID: " + fakeId + " → " + notFound); // null 기대

        // 전체 조회
        System.out.println("[전체 조회]");
        userService.readAll().forEach(System.out::println);

        // 수정 - 성공
        boolean updated = userService.update(user.getId(), "buzz");
        System.out.println("[수정 - 성공] 변경됨: " + updated);
        System.out.println("[수정 결과] " + userService.read(user.getId()));

        // 수정 - 실패
        boolean updateFail = userService.update(fakeId, "ghost");
        System.out.println("[수정 - 실패] 존재하지 않는 ID: " + fakeId + " → 변경됨: " + updateFail);

        // 삭제 - 성공
        userService.delete(user.getId());
        System.out.println("[삭제 - 성공] " + user.getId());
        System.out.println("[삭제 후 조회] " + userService.read(user.getId())); // null 기대

        // 삭제 - 실패
        userService.delete(fakeId); // 없어도 예외 발생 안 하면 무시됨
        System.out.println("[삭제 - 실패] (이미 없는 ID) " + fakeId);
    }

    public static void main(String[] args) {
        UserRepository userRepository = new FileUserRepository();
        UserService userService = new FileUserService(userRepository);

        userCRUDTest(userService);

        // Optional: 테스트 후 파일 초기화
        // ((FileUserRepository) userRepository).clearFile();
    }
}
