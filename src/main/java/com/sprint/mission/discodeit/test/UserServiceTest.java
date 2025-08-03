package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

public class UserServiceTest {
    private final UserService userService;

    public UserServiceTest(UserService userService) {
        this.userService = userService;
    }

    public void runAllTest() {
        save();
        afterEach();

        findOne();
        afterEach();

        findAll();
        afterEach();

        update();
        afterEach();

        delete();
        afterEach();
    }

    public void afterEach() {
        userService.deleteAll();
    }

    public void save() {
        printLine("save");

        User user = new User("홍길동", "길동2", "1234");

        User savedUser = userService.save(user);

        boolean isSameId = user.getId().equals(savedUser.getId());
        boolean isSameName = user.getName().equals(savedUser.getName());
        boolean isSameNick = user.getNickname().equals(savedUser.getNickname());
        boolean isSamePassword = user.getPassword().equals(savedUser.getPassword());

        printResult("save", isSameId && isSameName && isSameNick && isSamePassword);
    }

    public void findOne() {
        printLine("findOne");

        User user = new User("홍길동", "길동2", "1234");

        userService.save(user);
        User findUser = userService.findById(user.getId());

        printResult("findOne", user.equals(findUser));
    }

    public void findAll() {
        printLine("findAll");

        User user1 = new User("김철수", "자비에", "1234");
        User user2 = new User("유재석", "석재", "1212");
        User user3 = new User("박명수", "명수는여덟살", "1234");

        userService.save(user1);
        userService.save(user2);
        userService.save(user3);

        List<User> allUsers = userService.findAll();

        printResult("findAll", allUsers.size() == 3);
    }

    public void update() {
        printLine("update");

        User user = new User("홍길동", "hong", "1111");
        userService.save(user);

        User updateUser = userService.update(user.getId(), new User("홍남동", "동에번쩍", "4321"));

        boolean isSuccess = "홍남동".equals(updateUser.getName())
                && "동에번쩍".equals(updateUser.getNickname())
                && "4321".equals(updateUser.getPassword());

        printResult("update", isSuccess);
    }

    public void delete() {
        printLine("delete");

        User user = new User("홍길동", "hong", "1111");
        userService.save(user);
        userService.delete(user.getId());

        try {
            userService.findById(user.getId());
        } catch (NoSuchElementException e) {
            printResult("delete", true);
            return;
        }

        printResult("delete", false);
    }

    public void printLine(String methodName) {
        System.out.println("======= User " + methodName + " test =======");
    }

    public void printResult(String testName, boolean success) {
        if (success) {
            System.out.println("[" + testName + "] passed\n");
        } else {
            System.err.println("[" + testName + "] failed\n");
        }
    }
}
