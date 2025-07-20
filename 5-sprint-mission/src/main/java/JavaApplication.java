import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.sql.SQLOutput;

public class JavaApplication {
    public static void userTest(UserService userService){
        // 생성 실험
        User user1 = userService.create("홍길동", "1234");
        User user2 = userService.create("김길동", "1234");

        User findId = userService.find(user1.getId());

        System.out.println(user1);
        System.out.println(findId);
        System.out.println(user1.equals(findId)); // true

        System.out.println(userService.findAll());
        System.out.println(userService.findAll().size() == 2); // true

        System.out.println(userService.create("www", "1234"));
    }


    public static void main(String[] args) {
        // 선언부
        UserService userService = new JCFUserService();

        // Test 시작
        userTest(userService);

    }
}
