import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.sprint.mission.discodeit.service.factory.ServiceFactory;

public class JavaApplication {

    private static UserService userService = ServiceFactory.createUserService();
    private static ChannelService channelService = ServiceFactory.createChannelService();
    private static MessageService messageService = ServiceFactory.createMessageService();


    public static void main(String[] args) {
        System.out.println("\n========== Discord like 간단한 CRUD TEST ==========");

        // 각 테스트 실행 전에 서비스 데이터를 초기화하여 테스트 간의 독립성을 보장합니다.
        userService.clear();
        channelService.clear();
        messageService.clear();
        runTest(JavaApplication::testUserCrud, "User Service Test");

        userService.clear();
        channelService.clear();
        messageService.clear();
        runTest(JavaApplication::testChannelCrud, "Channel Service Test");

        userService.clear();
        channelService.clear();
        messageService.clear();
        runTest(JavaApplication::testMessageCrud, "Message Service Test");

        System.out.println("\n========== Test End ==========");
    }

    private static void runTest(Runnable testMethod, String testName) {
        System.out.println("\n========== " + testName + " ==========");
        try {
            testMethod.run();
            System.out.println("\n-> " + testName + " PASSED");
        } catch (Exception e) {
            System.err.println("\n-> " + testName + " FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testUserCrud() {
        System.out.println("--- User 등록---");

        User userAdmin = userService.create("AdminBot", "admin_pass");
        User userDev = userService.create("DevMaster", "dev_pass");
        User userGuest = userService.create("GuestUser", "guest_pass");
        User userToBeDeleted = userService.create("OldUser", "old_pass");

        System.out.println("  - 생성된 사용자: " + userAdmin.getUsername() + ", " + userDev.getUsername() + ", " + userGuest.getUsername() + ", " + userToBeDeleted.getUsername());

        System.out.println(userAdmin);
        System.out.println(userDev);
        System.out.println(userGuest);
        System.out.println(userToBeDeleted);

        System.out.println("\n--- User 조회(단건, 다건) ---");

        System.out.println("  - 'DevMaster' 사용자 이름으로 단건 조회: ");
        System.out.println(userService.findByUsername("DevMaster"));
        System.out.println("  - 'AdminBot' ID로 단건 조회: ");
        System.out.println(userService.find(userAdmin.getId()));

        List<User> allUsers = userService.findAll();
        System.out.println("  - 모든 사용자 조회(다건 조회): ");
        System.out.println(allUsers);

        System.out.println("\n --- User 수정 ---");
        System.out.println("  - 'AdminBot' 수정 전 조회: ");
        System.out.println(userService.find(userAdmin.getId()));

        System.out.println("\n --- User 수정된 데이터 조회 ---");
        userService.update(userAdmin.getId(), "SuperAdmin", null);
        System.out.println("  - 'AdminBot' -> 'SuperAdmin'으로 수정 후 조회: ");
        System.out.println(userService.find(userAdmin.getId()));

        userService.update(userAdmin.getId(), null, "new_pass");
        System.out.println(" 이어서 admin_pass -> new_pass로 비밀번호 변경 후 조회: ");
        System.out.println(userService.find(userAdmin.getId()));

        userService.update(userAdmin.getId(), "NewAdmin", "new_admin_password");
        System.out.println(" 이어서 이름: 'NewAdmin', 비밀번호'new_admin_password'로 변경 후 조회 ");
        System.out.println(userService.find(userAdmin.getId()));

        System.out.println("\n --- User 삭제 ---");


        userService.delete(userToBeDeleted.getId());

        System.out.println("\n --- User 조회를 통해 삭제 되었는지 확인 ---");
        System.out.println("  - 'OldUser' 삭제 후 모든 사용자 조회: " + userService.findAll());

        System.out.println("\n--- User Exception Handling ---");

        try {
           userService.delete(userToBeDeleted.getId());
        } catch (NoSuchElementException e) {
            System.out.println("  - 'OldUser' 재삭제 시도 (예외 발생): " + e.getMessage());
        }

        try {
            System.out.println(userService.findByUsername("NonExistentUser"));
        } catch (NoSuchElementException e) {
            System.out.println("  - 'NonExistentUser' 조회 시도 (예외 발생): " + e.getMessage());
        }

        System.out.println("\n--- User Validation Handling ---");
        try {
            System.out.println(userService.create("DevMaster", "duplicate_pass"));
        } catch (IllegalArgumentException e) {
            System.out.println("  - 중복 사용자 이름 생성 시도 (예외 발생): " + e.getMessage());
        }

        try {
            System.out.println(userService.create(null, "some_pass"));
        } catch (IllegalArgumentException e) {
            System.out.println("  - null 사용자 이름 생성 시도 (예외 발생): " + e.getMessage());
        }

        try {
            System.out.println(userService.create("", "some_pass"));
        } catch (IllegalArgumentException e) {
            System.out.println("  - 빈 사용자 이름 생성 시도 (예외 발생): " + e.getMessage());
        }

        try {
            System.out.println(userService.create("NewUser", null));
        } catch (IllegalArgumentException e) {
            System.out.println("  - null 비밀번호 생성 시도 (예외 발생): " + e.getMessage());
        }

    }


    private static void testChannelCrud() {
        // 테스트에 필요한 사용자 생성
        User userAdmin = userService.create("AdminBot", "admin_pass");
        User userDev = userService.create("DevMaster", "dev_pass");
        User userGuest = userService.create("GuestUser", "guest_pass");
        List<User> chAllUsers = userService.findAll();
        System.out.println("  - 채널에 들어갈 사용자들: ");
        System.out.println(chAllUsers);

        System.out.println("\n--- Channel 등록 ---");
        Channel channelAnnounce = channelService.create("#announcements", "서버의 중요한 소식이나 업데이트를 공지하는 채널입니다. 모두 확인 필수!");
        Channel channelGeneral = channelService.create("#general-chat", "자유롭게 대화하는 공간입니다. 규칙을 지켜주세요.");
        Channel channelDev = channelService.create("#dev-talk", "개발 관련 질문과 토론을 위한 채널입니다.");
        Channel channelToBeDeleted = channelService.create("#old-channel", "더 이상 사용하지 않는 채널입니다.");

        System.out.println("\n  - 생성된 채널: " + channelAnnounce.getChannelName() + ", " + channelGeneral.getChannelName() + ", " + channelDev.getChannelName() + ", " + channelToBeDeleted.getChannelName());

        System.out.println("\n--- Channel에 (User) 추가 ---");
        channelAnnounce.addUser(userAdmin);
        System.out.println("  - #announcements 채널에 사용자 AdminBot 추가: ");
        System.out.println(channelService.find(channelAnnounce.getId()));

        channelGeneral.addUser(userAdmin);
        channelGeneral.addUser(userGuest);
        System.out.println("  - #general-chat 채널에 사용자 AdminBot, GuestUser 추가: " );
        System.out.println(channelService.find(channelGeneral.getId()));

        channelDev.addUser(userDev);
        System.out.println("  - #dev-talk 채널에 사용자 DevMaster 추가: ");
        System.out.println(channelService.find(channelDev.getId()));

        System.out.println("\n--- Channel 조회(단건, 다건) ---");

        System.out.println("  - '#dev-talk' 채널명으로 단건 조회: ");
        System.out.println(channelService.find(channelDev.getId()));
        System.out.println("  - ID로 단건 조회: ");
        System.out.println(channelService.find(channelToBeDeleted.getId()));

        List<Channel> allChannels = channelService.findAll();
        System.out.println("  - 모든 채널 조회(다건 조회): ");
        System.out.println((allChannels));

        System.out.println("\n--- Channel 수정 ---");
        System.out.println("  - '#general-chat' 수정 전 조회: ");
        System.out.println(channelService.find(channelGeneral.getId()));
        channelService.update(channelGeneral.getId(), "#free-talk", null);

        System.out.println("\n--- Channel 수정된 데이터 조회 ---");

        System.out.println("  - '#general-chat' -> '#free-talk'으로 수정 후 조회: ");
        System.out.println(channelService.find(channelGeneral.getId()));

        channelService.update(channelGeneral.getId(), null, "Free한 느낌의 채널입니다. (업데이트)"); // 이름은 그대로
        System.out.println(" 이어서 description만 수정 후 조회: ");
        System.out.println(channelService.find(channelGeneral.getId()));

        channelService.update(channelGeneral.getId(), "#자유로운 채널", "자유, free한 채널 (업데이트)"); // 이름은 그대로
        System.out.println(" 이어서 채널명, description 둘 다 수정 후 조회");
        System.out.println(channelService.find(channelGeneral.getId()));

        System.out.println("\n--- Channel 삭제 ---");
        channelService.delete(channelToBeDeleted.getId());

        System.out.println("\n--- Channel 조회를 통해 삭제 되었는지 확인 ---");
        System.out.println("  - '#old-channel' 삭제 후 모든 채널 조회: ");
        System.out.println((channelService.findAll()));

        System.out.println("\n--- Channel Exception Handling ---");

        try {
            channelService.delete(channelToBeDeleted.getId());
        } catch (NoSuchElementException e) {
            System.out.println("  - '#old-channel' 재삭제 시도 (예외 발생): " + e.getMessage());
        }
        try {
            System.out.println(channelService.find(UUID.randomUUID()));
        } catch (NoSuchElementException e) {
            System.out.println("  - 존재하지 않는 채널 ID로 조회 시도 (예외 발생): " + e.getMessage());
        }

        try {
            System.out.println(channelService.create("#announcements", "duplicate description"));
        } catch (IllegalArgumentException e) {
            System.out.println("  - 중복 채널 이름 생성 시도 (예외 발생): " + e.getMessage());
        }

        try {
            System.out.println(channelService.create(null, "some description"));
        } catch (IllegalArgumentException e) {
            System.out.println("  - null 채널 이름 생성 시도 (예외 발생): " + e.getMessage());
        }

        try {
            System.out.println( channelService.create("", "some description"));
        } catch (IllegalArgumentException e) {
            System.out.println("  - 빈 채널 이름 생성 시도 (예외 발생): " + e.getMessage());
        }

    }

    private static void testMessageCrud() {

        // 테스트에 필요한 사용자 생성
        User userAdmin = userService.create("AdminBot", "admin_pass");
        User userDev = userService.create("DevMaster", "dev_pass");
        User userGuest = userService.create("GuestUser", "guest_pass");
        List<User> chAllUsers = userService.findAll();
        System.out.println(" - 채널에 들어갈 사용자들: ");
        System.out.println(chAllUsers);

        Channel channelAnnounce = channelService.create("#announcements", "서버의 중요한 소식이나 업데이트를 공지하는 채널입니다. 모두 확인 필수!");
        Channel channelGeneral = channelService.create("#general-chat", "자유롭게 대화하는 공간입니다. 규칙을 지켜주세요.");
        Channel channelDev = channelService.create("#dev-talk", "개발 관련 질문과 토론을 위한 채널입니다.");

        System.out.println(" - 메시지 테스트할 채널들: ");
        System.out.println(channelService.findAll());

        System.out.println("\n--- Message 등록 ---");

        Message msg1 = messageService.create(channelAnnounce.getId(), userAdmin.getId(), "[공지] 서버 점검이 1시간 연장됩니다. 양해 부탁드립니다.");
        Message msg2 = messageService.create(channelGeneral.getId(), userGuest.getId(), "안녕하세요! 새로 왔습니다. 잘 부탁드려요~");
        Message msg3 = messageService.create(channelDev.getId(), userDev.getId(), "새로운 기능 구현에 대한 아이디어 있으신 분?");
        Message msg4 = messageService.create(channelGeneral.getId(), userDev.getId(), "이 메시지는 곧 삭제될 예정입니다.");


        System.out.println("  - 생성된 메시지: " + msg1.getContent() + ", " + msg2.getContent() + ", " + msg3.getContent() + "," +msg4.getContent());

        System.out.println("\n--- Message 조회(단건, 다건) ---");

        System.out.println("  - '새로운 기능~' 메시지 단건 조회: " );
        System.out.println(messageService.find(msg3.getId()));
        System.out.println("  - '삭제될 예정~' 메시지 단건 조회: " );
        System.out.println(messageService.find(msg4.getId()));

        List<Message> allMessages = messageService.findAll();
        System.out.println("  - 모든 메시지 조회(다건 조회): ");
        System.out.println(allMessages);

        System.out.println("\n--- Message 수정 ---");

        System.out.println(" - 공지 메시지 수정 전 조회: " );
        System.out.println(messageService.find(msg1.getId()));
        System.out.println(" - 일반 메시지 수정 전 조회: " );
        System.out.println(messageService.find(msg2.getId()));

        System.out.println("\n--- Message 수정된 데이터 조회 ---");

        messageService.update(msg1.getId(), "[긴급 공지] 서버 점검이 완료되었습니다. 접속 가능합니다!");
        System.out.println(" - 공지 메시지 수정 후 조회: ");
        System.out.println( messageService.find(msg1.getId()));
        messageService.update(msg2.getId(), "안녕하세요! 잘 부탁드립니다. (수정됨)");
        System.out.println(" - 일반 메시지 수정 후 조회: ");
        System.out.println(messageService.find(msg2.getId()));

        System.out.println("\n--- Message 삭제 ---");

        messageService.delete(msg4.getId());
        System.out.println("  - '삭제될 예정~' 메시지 삭제 후 모든 메시지 조회: ");
        System.out.println(messageService.findAll());

        System.out.println("\n--- Message Exception Handling ---");
        try {
            messageService.delete(msg4.getId());
        } catch (NoSuchElementException e) {
            System.out.println("  - '삭제될 예정~' 메시지 재삭제 시도 (예외 발생): " + e.getMessage());
        }

        try {
            System.out.println(messageService.create(channelGeneral.getId(), userGuest.getId(), null));
        } catch (IllegalArgumentException e) {
            System.out.println("  - null 메시지 내용 생성 시도 (예외 발생): " + e.getMessage());
        }

        try {
            System.out.println( messageService.create(channelGeneral.getId(), userGuest.getId(), ""));
        } catch (IllegalArgumentException e) {
            System.out.println("  - 빈 메시지 내용 생성 시도 (예외 발생): " + e.getMessage());
        }

        System.out.println("\n--- 심화 요구 사항: 연관된 도메인 모델 데이터 테스트 ---");
        try {
            System.out.println( messageService.create(channelGeneral.getId(), UUID.randomUUID(), "유효하지 않은 사용자의 메시지"));
        } catch (IllegalArgumentException e) {
            System.out.println("  - 유효하지 않은 사용자 메시지 생성 시도 (예외 발생): " + e.getMessage());
        }
        try {
            System.out.println(messageService.create(UUID.randomUUID(), userDev.getId(), "유효하지 않은 채널 메시지"));
        } catch (IllegalArgumentException e) {
            System.out.println("  - 유효하지 않은 채널 메시지 생성 시도 (예외 발생): " + e.getMessage());
        }
    }
}