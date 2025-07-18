package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.IUserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserServiceImpl;

import java.util.List;

public class Run {
    public static void main(String[] args) {
//        IUserService userService = new JCFUserServiceImpl();
        JCFUserServiceImpl jcfUserService = new JCFUserServiceImpl();
//        userTest(userService);
        userTest(jcfUserService);
    }

//[ ] 등록
//[ ] 조회(단건, 다건)
//[ ] 수정
//[ ] 수정된 데이터 조회
//[ ] 삭제
//[ ] 조회를 통해 삭제되었는지 확인
    public static void userTest(JCFUserServiceImpl userService) {
        // [] 등록
        userService.createUser("홍길동", "111", "email1");
        userService.createUser("김길동", "111", "email2");
        userService.createUser("3길동", "222", "email2");
        userService.createUser("1길동", "333", "email2");
        User user1 = userService.getUserData().get(0);

        System.out.println("userService.getUserData().size() : "  + userService.getUserData().size());
        System.out.println("userData => " + userService.getUserData() );
        System.out.println("---------------");

        //[] 단건 조회
        User findUser = userService.findById(user1.getId());
        System.out.println("[단건 조회] findId : " + findUser);
        System.out.println("-----------------------------");

        //[] 다건 조회
        List<User> findAllUser = userService.findAll("김길동");
        System.out.println("[다건 조회]findAll() => " + findAllUser);
        System.out.println("----------------");

        //[] 데이터 수정
        userService.update(user1.getId(),"username","홍길동","장길동");
        System.out.println("username 수정 : " + userService.getUserData());
        System.out.println("--------------");
        userService.update(user1.getId(), "password", "111","444");
        System.out.println("password 수정 : " + userService.getUserData());
        System.out.println("--------------");

        System.out.println("user 데이터 삭제(전) : " + userService.getUserData().size());
        System.out.println("------------------");
        userService.delete(user1.getId());
        System.out.println("user 데이터 삭제(후) : " +  userService.getUserData().size());
        System.out.println("------------------");
        System.out.println("조회를 통해 삭제되었는지 확인");
        System.out.println(userService.getUserData());

    }

}
