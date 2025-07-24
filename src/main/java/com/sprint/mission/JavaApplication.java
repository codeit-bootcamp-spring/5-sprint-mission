package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) throws InterruptedException {
//        UserService userService = JCFUserService.getInstance();
//        ChannelService channelService = JCFChannelService.getInstance();
//        JCFMessageService messageService = JCFMessageService.getInstance();
//
//        //의존성 주입
//        messageService.setDependencies(userService, channelService);
//
//        // 테스트
//        userCRUDTest(userService);
//        channelCRUDTest(channelService, userService);
//        messageCRUDTest(messageService,userService,channelService);
//    }
//
//    private static void userCRUDTest(UserService userService) throws InterruptedException {
//        // 등록
//        System.out.println("====== User 등록 및 전체조회 ======");
//        final User user1 = new User("홍길동");
//        final User user2 = new User("김길동");
//        final User user3 = new User("이길동");
//
//        userService.create(user1);
//        userService.create(user2);
//        userService.create(user3);
//
//        UUID userId1 = user1.getId();
//        UUID userId2 = user2.getId();
//        UUID userId3 = user3.getId();
//
//        // 전체 조회
//        userService.findAll();
//
//        System.out.println("====== User 단건 조회");
//        // 단건 조회
//        userService.findById(userId1);
//        System.out.println("====== 이름 조회 ======");
//        userService.findByName(user2.getNickName());
//
//        // 전체 조회
//        //userService.findAll();
//
//        // 수정 및 조회
//        Thread.sleep(2000);
//        String newName = "박나나";
//        userService.update(userId1, newName);
//
//        User updatedUser = userService.findById(userId1);
//        System.out.println("수정 후 닉네임: " + updatedUser);
//
//        // 삭제 및 조회
//        userService.delete(userId2);
//        userService.findAll();
//    }
//
//    private static void channelCRUDTest(ChannelService channelService, UserService userService) {
//        // 등록
//        System.out.println("\n [CHANNEL CRUD 테스트]");
//
//        // 채널 생성
//        final Channel channel = new Channel("공지사항");
//        channelService.create(channel);
//        UUID channelId = channel.getId();
//
//        // 채널 조회
//        channelService.findById(channelId);
//        channelService.findAll();
//
//        // 채널 이름 수정
//        channelService.update(channelId, "일반채널");
//
//        // 유저 등록 후 채널 입장
//        final User user = new User("이순신");
//        userService.create(user);
//        channel.join(user.getId());
//        channelService.findAll();
//
//        // 유저 입장 확인
//        Channel updated = channelService.findById(channelId);
//        for (UUID uid : updated.getUserIds()) {
//            System.out.println("- 채널 유저: " + userService.findById(uid).getNickName());
//        }
//
//        // 삭제
//        channelService.delete(channelId);
//        System.out.println(channelService.findById(channelId) == null ? "채널 삭제 확인" : "삭제 실패");
//    }
//
//        private static void messageCRUDTest(JCFMessageService messageService, UserService userService, ChannelService channelService) {
//            System.out.println("메세지 테스트");
//
//            User user = new User ("김유신");
//            Channel channel = new Channel("스터디");
//
//            userService.create(user);
//            channelService.create(channel);
//            channel.join(user.getId());
//
//            // 메세지 생성
//            Message msg1 = new Message(user.getId(), channel.getId(), "안녕하세요.");
//            messageService.create(msg1);
//
//            // 메시지 전체 조회
//            List<Message> all = messageService.findAll();
//            for (Message msg : all) {
//                System.out.println(userService.findById(msg.getUserId()).getNickName() + ": " + msg.getContent());
//            }
//
//            // 메세지 수정
//            msg1.updateContent("안녕하세요. 반갑습니다.");
//            messageService.update(msg1.getId(), msg1.toString());
//
//            // 수정 확인
//            System.out.println("수정된 메세지: " + messageService.findById(msg1.getId()).getContent());
//
//            // 삭제
//            messageService.delete(msg1.getId());
//
//            // 삭제 확인
//            System.out.println(messageService.findById(msg1.getId()) == null ? "메세지 삭제됨" : "삭제오류");
//    }


}




