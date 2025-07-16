package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import java.util.ArrayList;
import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();
        System.out.println("================== 생성 ======================");

        System.out.println(userService.create("test1" , "홍길동", "1234"));
        System.out.println(userService.create("test2", "김길동", "1234"));

        Channel ch1 = channelService.create("김김김");
        Channel ch2 = channelService.create("박박박");
        System.out.println("ch1 = " + ch1);
        System.out.println("ch2 = " + ch2);

        Message msg1 = messageService.create("안녕하세요", "test1", ch1.getId());
        Message msg2 = messageService.create("반갑습니다", "test2", ch2.getId());
        System.out.println("msg1 = " + msg1);
        System.out.println("msg2 = " + msg2);

        System.out.println("=============== 조회 ======================");

        User user1 = userService.get("test1");
        User user2 = userService.get("test2");
        System.out.println("user1 = " + user1);
        System.out.println("user2 = " + user2);

        System.out.println(channelService.get("김김김"));
        System.out.println(channelService.get("박박박"));

        List<User> userList = userService.getAll();
        List<Channel> channelList = channelService.getAll();
        List<Message> messageList = messageService.getMessages();

        System.out.println("=============== 전체 조회 =================");

        System.out.println(userList);
        System.out.println(channelList);
        System.out.println(messageList);

        System.out.println("============= 수정 =====================");
        System.out.println("유저 이름 수정 = " + userService.updateUserName("test1", "김철수") + " 수정 후 test1 이름 : " + userService.get("test1"));
        System.out.println("채널 이름 수정 = " + channelService.update(ch1.getId(), "대한민국") + " 수정 후 ch1 이름 : " + channelService.get("대한민국"));
        System.out.println("메세지 수정 = " + messageService.update(msg1.getId(), "가나다라마바사") + " 수정후 msg1 메세지내용 : " + msg1.getContent());

        System.out.println("============= 삭제 ======================");
        System.out.println("test2 삭제 : " + userService.delete("test2"));
        System.out.println("박박박 채널 삭제 : " + channelService.delete(ch2.getId()));
        System.out.println("msg2 메세지 삭제 : " +  messageService.delete(msg2.getId()));

        System.out.println("유저 삭제 후 조회 : " + userService.get("test2"));
        System.out.println("채널 삭제 후 조회 : " + channelService.get("박박박")) ;
        System.out.println("메세지 삭제 후 조회 " + messageService.getMessageById(msg2.getId()));

        System.out.println("=============== 테스트 종료 =====================");













    }
}
