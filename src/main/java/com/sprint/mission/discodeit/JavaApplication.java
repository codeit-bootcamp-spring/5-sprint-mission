package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    private static JCFUserService userService;
    private static JCFChannelService channelService;
    private static JCFMessageService messageService;

    public static void main(String[] args) {
        userService = new JCFUserService();
        channelService = new JCFChannelService();
        messageService = new JCFMessageService();

        userCRUDTest();
        System.out.println("\n======================================================================");
        channelCRUDTest();
        System.out.println("\n======================================================================");
        messageCRUDTest();
    }

    private static void userCRUDTest() {
        System.out.println("\n<<<<User Test Start>>>>");
        // 사용자 등록
        System.out.println("====사용자 등록====");
        User user1 = userService.create("홍길동", "hong1234@gmail.com", "1234");
        User user2 = userService.create("박길동", "park1234@gmail.com", "1234");
        User user3 = userService.create("김길동", "kim1234@gmail.com", "1234");
        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);

        // 사용자 조회
        System.out.println("\n====사용자 조회====");
        System.out.println("----단건 조회----");
        System.out.println(userService.find(user1.getId()));

        System.out.println("----전체 조회----");
        System.out.println(userService.findAll());

        // 사용자 수정
        System.out.println("\n====사용자 수정====");
        System.out.println("수정 전 | " + userService.find(user3.getId()));
        userService.update(user3.getId(), "이길동", "lee1234@gmail.com", user3.getPassword());
        System.out.println("수정 후 | " + userService.find(user3.getId()));

        // 사용자 삭제 & 조회
        System.out.println("\n====사용자 삭제====");
        System.out.println("삭제 전 | " + userService.find(user2.getId()));
        System.out.println("삭제 결과 | " + userService.delete(user2.getId()));
        System.out.println("삭제 후 | " + userService.find(user2.getId()) + '\n' + userService.findAll());
        System.out.println("<<<<User Test End>>>>");
    }

    private static void channelCRUDTest() {
        User user1 = userService.findAll().get(0);
        User user2 = userService.findAll().get(1);

        System.out.println("\n<<<<Channel Test Start>>>>");
        // 채널 등록
        System.out.println("====채널 등록====");
        Channel channel1 = channelService.create("채널A", user1.getId());
        Channel channel2 = channelService.create("채널b", user2.getId());
        Channel channel3 = channelService.create("채널C", user2.getId());
        System.out.println(channel1);
        System.out.println(channel2);
        System.out.println(channel3);

        // 채널 조회
        System.out.println("\n====채널 조회====");
        System.out.println("----단건 조회----");
        System.out.println(channelService.find(channel1.getId()));
        System.out.println("----전체 조회----");
        System.out.println(channelService.findAll());

        // 채널 수정
        System.out.println("\n====채널 수정====");
        System.out.println("수정 전 | " + channelService.find(channel2.getId()));
        channelService.update(channel2.getId(), "채널B", user1.getId());
        System.out.println("수정 후 | " + channelService.find(channel2.getId()));

        // 채널 삭제 & 조회
        System.out.println("\n====채널 삭제====");
        System.out.println("삭제 전 | " + channelService.find(channel3.getId()));
        System.out.println("삭제 결과 | " + channelService.delete(channel3.getId()));
        System.out.println("삭제 후 | " + channelService.find(channel3.getId()) + '\n' + channelService.findAll());
        System.out.println("<<<<Channel Test End>>>>");
    }

    private static void messageCRUDTest() {
        User user1 = userService.findAll().get(0);
        User user2 = userService.findAll().get(1);
        Channel channel1 = channelService.findAll().get(0);
        Channel channel2 = channelService.findAll().get(1);

        System.out.println("\n<<<<Message Test Start>>>>");
        // 메시지 등록
        System.out.println("====메시지 등록====");
        Message message1 = messageService.create("메시지A", user1.getId(), channel1.getId());
        Message message2 = messageService.create("메시지B", user2.getId(), channel2.getId());
        Message message3 = messageService.create("메시지C", user2.getId(), channel2.getId());
        System.out.println(message1);
        System.out.println(message2);
        System.out.println(message3);

        // 메시지 조회
        System.out.println("\n====메시지 조회====");
        System.out.println("----단건 조회----");
        System.out.println(messageService.find(message1.getId()));
        System.out.println("----전체 조회----");
        System.out.println(messageService.findAll());

        // 메시지 수정
        System.out.println("\n====메시지 수정====");
        System.out.println("수정 전 | " + messageService.find(message2.getId()));
        messageService.update(message2.getId(), "호로로롤롤ㄹ");
        System.out.println("수정 후 | " + messageService.find(message2.getId()));

        // 메시지 삭제 & 조회
        System.out.println("\n====메시지 삭제====");
        System.out.println("삭제 전 | " + messageService.find(message3.getId()));
        System.out.println("삭제 결과 | " + messageService.delete(message3.getId()));
        System.out.println("삭제 후 | " + messageService.find(message3.getId()) + '\n' + messageService.findAll());
        System.out.println("<<<<Message Test End>>>>");
    }
}
