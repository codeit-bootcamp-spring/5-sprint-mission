package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.jcf.JCFChannelService;
import com.sprint.mission.discodeit.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void userTest(UserService userService){
        System.out.println("==========유저 테스트==========");
        // 사용자 등록
        User user1 = userService.create("차규환", "1234");
        User user2 = userService.create("차기쁨", "1234");
        // 사용자 조회 (단건, 다건)
        User findId = userService.find(user1.getId());
        System.out.println(user1);
        System.out.println(findId);
        System.out.println(user1.equals(findId)); // true

        System.out.println(userService.findAll());
        System.out.println(userService.findAll().size() == 2); // true

        // 사용자 수정 및 수정된 데이터 조회
        userService.update(user1.getId(), "치규환","4321");
        userService.update(user2.getId(), "치기쁨","4321");
        System.out.println(userService.findAll());

        System.out.println(user1.getUsername() + "님이 삭제 완료됐습니다!");
        userService.delete(user1.getId());
        System.out.println(user2.getUsername() + "님이 삭제 완료됐습니다!");
        userService.delete(user2.getId());
        System.out.println(userService.findAll());


    }
    public static void channelTest(ChannelService channelService) {
        System.out.println("==========채널 테스트==========");
        // 채널 등록 및 등록된 채널 조회
        Channel ch1 = channelService.createChannel("test server1", "테스트용 서버입니다.");
        Channel ch2 = channelService.createChannel("test server2", "테스트용 2번째 서버입니다.");
        System.out.println(channelService.findAll());

        // 채널 수정 및 수정된 채널 조회
        System.out.println("==========채널 수정 테스트==========");
        channelService.updateChannel(ch1.getId(),"테스트 서버1", "한글 패치 완료!");
        channelService.updateChannel(ch2.getId(),"테스트 서버2", "한글 패치 완료!");
        System.out.println(channelService.findAll());

        // 채널 삭제 및 삭제된 데이터 조회
        System.out.println("==========채널 삭제 테스트==========");
        System.out.println(ch1.getChannelName() + " 가 삭제되었습니다 !");
        channelService.deleteChannel(ch1.getId());
        System.out.println(ch2.getChannelName() + " 가 삭제되었습니다 !");
        channelService.deleteChannel(ch2.getId());
        System.out.println(channelService.findAll());
    }
    public static void messageTest(MessageService messageService) {
        System.out.println("==========메세지 테스트==========");
        // 메세지 생성
        Message ms1 = messageService.createMessage("Test Server1", "차규환", "안녕하세요 첫 번째 테스트 메세지입니다.");
        Message ms2 = messageService.createMessage("Test Server2", "차기쁨", "안녕하세요 두 번째 테스트 메세지입니다.");
        System.out.println(messageService.findAll());
        System.out.println("==========메세지 수정 테스트==========");
        messageService.updateMessage(ms1.getId(),"1채널 메세지 변경");
        messageService.updateMessage(ms2.getId(),"2채널 메세지 변경");
        System.out.println(messageService.findAll());
        System.out.println("==========메세지 삭제 테스트==========");
        System.out.println(ms1.getChannelName() + "에 사용자의 메세지가 삭제 되었습니다.");
        messageService.deleteMessage(ms1.getId());
        System.out.println(ms2.getChannelName() + "에 사용자의 메세지가 삭제 되었습니다.");
        messageService.deleteMessage(ms2.getId());
        System.out.println(messageService.findAll());


    }

    public static void main(String[] args) {
        // 선언부
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();


        // Test 시작
        userTest(userService);
        channelTest(channelService);
        messageTest(messageService);

    }
}
