package com.sprint.mission;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) throws InterruptedException {
        JCFUserService userService = JCFUserService.getInstance();

        // 유저 생성
        User user1 = new User("홍길동");
        User user2 = new User("박철수");

        // 저장
        userService.create(user1);
        userService.create(user2);

        // 확인 출력
        System.out.println("저장된 사용자 목록");
        userService.findAll();
        System.out.println("아이디로 찾기");
        userService.findById(user1.getId());
        userService.findById(user2.getId());
        System.out.println("이름 바꾸기");
        Thread.sleep(2000);
        userService.update(user1.getId(), "박하늘");
        userService.findAll();
        System.out.println("아이디를 받고 데이터 삭제");
        userService.delete(user1.getNickName());
        userService.findAll();




    }
}
