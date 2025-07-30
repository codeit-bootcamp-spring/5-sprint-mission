package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        userTest(userService);
        channelTest(channelService);
        messageTest(messageService);
    }

    static void userTest(UserService userService) {
        User user1 = userService.create("test1", "1234", "홍길동");
        User user2 = userService.create("test2", "1234", "김길동");
        System.out.println("유저1 생성 : " + user1);
        System.out.println("유저2 생성 : " + user2);

        User getUser = userService.get(user1.getId());
        System.out.println("유저 찾기 : " + getUser);

        userService.delete(user1.getId());

        List<User> users = userService.getAll();
        users.forEach(System.out::println);
    }

    static void channelTest(ChannelService channelService) {
        Channel channel1 = channelService.create("일반", "일반채널입니다.");
        Channel channel2 = channelService.create("공지", "공지채널입니다.");
        System.out.println("일반 채널 생성 확인 : " + channel1);
        System.out.println("공지 채널 생성 확인 : " + channel2);

        Channel getChannel = channelService.get(channel1.getId());
        System.out.println("일반 채널 조회 : " + getChannel);

        channelService.delete(channel1.getId());

        System.out.println("일반 채널 삭제후 전체 조회");
        channelService.getAll().forEach(System.out::println);
    }

    static void messageTest(MessageService messageService) {
        User user1 = new User("test1", "1234", "홍길동");
        User user2 = new User("test2", "1234", "김길동");

        Channel channel1 = new Channel("일반", "일반채널입니다.");
        Channel channel2 = new Channel("공지", "공지채널입니다.");


        Message message1 = messageService.create("안녕하세요", user1.getUserId(), channel1.getId());
        Message message2 = messageService.create("반갑습니다", user2.getUserId(), channel2.getId());
        System.out.println("일반 메세지 생성 확인 : " + message1);
        System.out.println("공지 메세지 생성 확인 : " + message2);

        messageService.update(message1.getId(), "삭제 예정");
        System.out.println("메세지 수정 확인 : " + message1);

        messageService.delete(message1.getId());
        System.out.println("메세지 삭제 후 전체 메세지 조회");
        messageService.getMessages().forEach(System.out::println);

    }
}
