package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;
import java.util.Optional;

public class JavaApplication {
    private static final UserService userService = new BasicUserService(new JCFUserRepository());
    private static final ChannelService channelService = new BasicChannelService(new JCFChannelRepository());
    private static final MessageService messageService = new BasicMessageService(new FileMessageRepository());

    private static boolean setupUser(UserService userService) {
        if (!userService.register("aaa@aaa.aaa", "just_do_it", "홍길동", "1234", "010-1234-5678")) return false;
        if (!userService.register("bbb@bbb.bbb", "what_can_i_do", "홍길동", "1234", "010-1234-5678")) return false;
        if (!userService.register("ccc@ccc.ccc", "anything", "이길동", "1234", "010-1234-5678")) return false;
        if (!userService.register("ddd@ddd.ddd", "omg", "이길동", "1234", "010-1234-5678")) return false;
        return userService.register("eee@eee.eee", "this_code", "김길동", "1234", "010-1234-5678");
    }

    private static void setupChannel(ChannelService channelService) {
        channelService.createChannel("소개합니다", "개성 가득한 소개를 남겨주세요.");
        channelService.createChannel("소통할까요", "다양한 주제로 자유롭게 소통하는 채널이에요.");
        channelService.createChannel("공지", "이 채널에 공유되는 소식은 꼭 확인해주세요.");
        System.out.println("채널 셋업 완료...");

        System.out.print("------ 모든 채널 확인 -------\n");
        for (Channel channel : channelService.getAll()) {
            System.out.print(channel);
            System.out.print("--------------\n");
        }
    }

    private static void setupMessage(MessageService messageService) {
        userService.register("test1@test.com", "send_user", "주건희", "test", "010-1111-2222");
        userService.register("test2@test.com", "receive_user", "받건희", "test", "010-2222-3333");

        Channel testChannel1 = channelService.createChannel("메시지 테스트 채널1", "테스트용 채널입니다.");
        Channel testChannel2 = channelService.createChannel("메시지 테스트 채널2", "테스트용 채널입니다.");

        User sendUser = userService.getByUserName("send_user");

        messageService.createMessage(sendUser, testChannel1, "심심하다");
        messageService.createMessage(sendUser, testChannel1, "나랑 같이 떠들 사람");
        messageService.createMessage(userService.getByUserName("receive_user"), testChannel2, "들썩 들썩 떠들썩");
        System.out.println("메시지 셋업 완료...");
    }

    public static void main(String[] args) {
        userCRUDTest();
        channelCRUDTest();
        messageCRUDTest();
    }

    private static void userCRUDTest() {
        System.out.print("============User CRUD Test============\n");

        if (setupUser(userService)) System.out.println("유저 생성 테스트 :: 성공");
        else System.out.println("유저 생성 테스트 :: 실패");

        User findByEmail = userService.getByEmail("aaa@aaa.aaa");
        if (findByEmail == null) System.out.println("유저 검색 테스트(이메일) :: 성공");
        else System.out.println("유저 검색 테스트(이메일) :: 실패");


        User findByUserName = userService.getByUserName("길동");
        if (findByUserName == null) System.out.println("유저 검색 테스트(이름) :: 성공");
        else System.out.println("유저 검색 테스트 :: 실패");


        List<User> findByNickname = userService.searchByNickname("대소동");
        if (findByNickname == null || findByNickname.isEmpty()) System.out.println("유저 검색 테스트(닉네임) :: 실패");
        else System.out.println("유저 검색 테스트(닉네임) :: 성공");

        String email = "ddd@ddd.ddd";
        if (userService.updateByEmail(email, "holymoly", "동동동", "4321", "010-0000-1111")) {
            System.out.println("유저 수정 테스트 :: 성공");
        } else System.out.println("유저 수정 테스트 :: 실패\n");


        if (userService.removeByEmail(email)) System.out.println("유저 삭제 테스트 :: 성공");
        else System.out.println("유저 삭제 테스트 :: 실패");
    }

    private static void channelCRUDTest() {
        System.out.print("============Channel CRUD Test============\n");

        setupChannel(channelService);

        Channel testChannel = channelService.createChannel("테스트 채널", "테스트 전용 채널입니다");

        List<Channel> findChannels = channelService.getByChannelName("테스트");
        if (findChannels == null || findChannels.isEmpty()) System.out.print("채널 검색 테스트 :: 실패");
        else System.out.println("채널 검색 테스트 :: 성공");

        String channelName = "TEST CHANNEL";
        channelService.updateById(testChannel.getId(), channelName, "테스트 전용 채널입니다.");
        Optional<Channel> optionalChannel = channelService.getByChannelName(channelName).stream()
                .filter(channel -> channel.getChannelName().equals(channelName))
                .findFirst();
        if (optionalChannel.isPresent()) System.out.println("채널 수정 테스트 :: 성공");
        else System.out.println("채널 수정 테스트 :: 실패");

        if (channelService.removeById(testChannel.getId())) System.out.println("채널 삭제 테스트 :: 성공");
        else System.out.println("유저 삭제 테스트 :: 실패");
    }

    private static void messageCRUDTest() {
        System.out.print("============Message CRUD Test============\n");

        setupMessage(messageService);

        User testUser = userService.getByUserName("send_user");
        List<Message> messageList = messageService.getByUser(testUser);
        if (!messageList.isEmpty()) System.out.println("메시지 검색 테스트 :: 성공");
        else System.out.println("메시지 검색 테스트 :: 실패");

        Channel testChannel = channelService.getByChannelName("메시지 테스트 채널").get(0);
        Message testMessage = messageService.createMessage(testUser, testChannel, "테스트를 위한 메시지");

        String updateMessage = "아아 메시지 테스트";
        if (messageService.updateById(testMessage.getId(), testUser, testChannel, testMessage.getMessage(), updateMessage)) {
            System.out.println("메시지 수정 테스트 :: 성공");
        } else System.out.println("메시지 수정 테스트 :: 실패");

        if (messageService.removeById(testMessage.getId(), testUser, testChannel)) {
            System.out.println("메시지 삭제 테스트 :: 성공");
        } else System.out.println("메시지 삭제 테스트 :: 실패");
    }
}
