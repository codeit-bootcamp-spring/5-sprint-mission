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
        System.out.println("생성: " + user);

        // 단건 조회
        User found = userService.read(user.getId());
        System.out.println("조회: " + found);

        // 전체 조회
        System.out.println("전체 유저 목록:");
        userService.readAll().forEach(System.out::println);

        // 수정
        userService.update(user.getId(), "buzz");
        System.out.println("수정 후 조회: " + userService.read(user.getId()));

        // 삭제
        userService.delete(user.getId());
        System.out.println("삭제 후 조회: " + userService.read(user.getId())); // null 기대
    }

    public static void main(String[] args) {
        UserRepository userRepository = new FileUserRepository();
        UserService userService = new FileUserService(userRepository);

        userCRUDTest(userService);
    }
    // 초기화
    // ((FileUserRepository) userRepository2).clearFile();
}
