package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import javax.swing.*;
import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {

        /*
         * Map을 이용한 JCF(java collection framework)의 서비스 구현체
         * Channel, Message, User 객체 세 가지를
         * 생성 -> 조회 -> 수정 -> 삭제 테스트하는 파일
         * */

        /* given - when - then 테스트
         * ~인 상황에서, ~를 했을때, ~의 결과가 나와야 한다.
         */


        //User Test
        //1. Given: 구현체 및 객체 생성
        UserService userService = new JCFUserService();
        User user = new User("test1", "1234");

        //2. When: 등록/조회/수정
        userService.create(user); // 유저 등록
        User founduser = userService.findById(user.getId()); // 단건 조회
        List<User> allUsers = userService.findAll(); // 전체 조회
        founduser.updateTime(); // 메모리에서만 수정 시간 갱신
        userService.update(founduser); // 저장소 Map까지 수정

        //3. Then: 등록/조회/수정 검증
        System.out.println("✅유저 등록: " + founduser.getId());
        System.out.println("✅유저 아이디 조회: " + founduser.getUserId());
        System.out.println("✅유저  비밀번호 조회: " + founduser.getPassword());
        System.out.println("✅유저 단건 조회:" + userService.findById(user.getId()));
        System.out.println("유저 전체 조회" + userService.findAll());
        System.out.println("✅유저 수정 후 조회: " + userService.findById(user.getId()));

        //4. When: 삭제
        userService.delete(user.getId());

        //5. Then: 삭제 검증
        try {
            User afterDelete = userService.findById(user.getId());
            System.out.println("✅유저 삭제 후 조회: " + afterDelete);
        } catch (IllegalArgumentException e) {
            System.out.println("✅❗삭제된 유저는 더이상 조회할 수 없습니다.");
        }
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");

        //Channel Test
        //1. Given: 구현체 및 객체 생성
        ChannelService channelService = new JCFChannelService();
        Channel channel = new Channel("테스트제목", "설명테스트", "voice");

        //2. When: 등록/조회/수정
        channelService.create(channel); // 채널 등록
        Channel foundChannel = channelService.findById(channel.getId());//단건 조회
        List<Channel> allChannel = channelService.findAll();//전체 조회
        foundChannel.updateTime(); // 메모리안에서만 수정시간 갱신
        channelService.update(foundChannel);// 저장소 Map 값까지 수정

        //3. Then: 등록/조회/수정 검증
        System.out.println("✅채널 등록: " + channel.getId());
        System.out.println("✅채널 제목 조회: " + channel.getTitle());
        System.out.println("✅채널 설명 조회: " + channel.getDescription());
        System.out.println("✅채널 타입 조회: " + channel.getChanneltype());
        System.out.println("✅채널 단건 조회: " + channelService.findById(channel.getId()));
        System.out.println("✅채널 전체 조회: " + channelService.findAll());
        System.out.println("✅채널 수정 후 조회: " + channelService.findById(channel.getId()));

        //4. When: 삭제
        channelService.delete(foundChannel.getId());

        //5. Then: 삭제 검증
        try {
            Channel afterDelete = channelService.findById(channel.getId());
            System.out.println("✅채널 삭제 후 조회: " + afterDelete);
        } catch (IllegalArgumentException e) {
            System.out.println("✅❗삭제된 채널은 더이상 조회할 수 없습니다.");
        }
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");


        //Message Test
        //테스트위해 sender & channel 다시 등록
        User sender = new User("senderUser", "pw1234");
        userService.create(sender);
        Channel messageChannel = new Channel("메세지 테스트 채널", "채팅용", "text");
        channelService.create(messageChannel);

        //1. Given: 구현체 및 객체 생성
        MessageService messageService = new JCFMessageService(userService); //유저 유효성 검사위해 userService 전달
        Message message = new Message("테스트내용", sender.getId(), messageChannel.getId());


        //2. When: 등록/조회/수정
        messageService.create(message); // 등록
        Message foundMessage = messageService.findById(message.getId()); //단건 조회
        List<Message> allMessage = messageService.findAll(); // 전체 조회
        foundMessage.updateTime(); // 메모리 안에서만 수정 시간 갱신
        messageService.update(foundMessage); // 저장소 Map까지 수정

        //3. Then: 등록/조회/수정 검증
        System.out.println("✅메세지 등록: " + message.getId());
        System.out.println("✅메세지 내용 조회: " + message.getContent());
        System.out.println("✅메세지 보낸 사람 조회: " + message.getSender());
        System.out.println("✅메세지 채널ID 조회: " + message.getChannelId());
        System.out.println("✅메세지 단건 조회: " + messageService.findById(message.getId()));
        System.out.println("✅메세지 전체 조회: " + messageService.findAll());
        System.out.println("✅수정 메세지 조회: " + messageService.findById(message.getId()));

        //4. When: 삭제
        messageService.delete(foundMessage);

        //5. Then: 삭제 검증
        try {
            Message afterDelete = messageService.findById(foundMessage.getId());
            System.out.println("✅삭제 후 메세지 조회: " + afterDelete);
        } catch (IllegalArgumentException e) {
            System.out.println("✅❗삭제된 메세지는 더이상 조회할 수 없습니다.");
        }
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");


    }


}
