package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {

        // Spring Context
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        //Spring Context에서 Service Bean 꺼내오기(new 대체)
        UserService userService = context.getBean(UserService.class); //@Primary 붙은 FileUserSerivce 구현체가 주입됨
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        /*User Test*/

        // 1. Given: 유저 준비
        UserCreateRequest request = new UserCreateRequest("유저 테스트", "test@email.com", "1234");

        // 2. When: 등록/조회/수정
        userService.create(request);

        // 등록된 유저 단건 조회
        UserResponse foundUser = userService.findAll().stream()
                .filter(u -> u.getUserId().equals("유저 테스트"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 전체 유저 리스트 조회
        List<UserResponse> foundAll = userService.findAll();

        // 메모리 상에서 수정시간만 갱신
        foundUser.updateTime();

        //수정 요청 객체로 변환해서 저장소에 반영
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                foundUser.getId(),
                foundUser.getUserId(),
                foundUser.getEmail(),
                "updated-password", // 테스트용 새 비밀번호
                null // 프로필 이미지 없음
        );

        userService.update(updateRequest); // 저장소 map까지 수정

        // 3. Then: 등록/조회/수정 검증
        System.out.println("✅ 유저 등록: " + foundUser.getId());
        System.out.println("✅ 유저 아이디 조회: " + foundUser.getUserId());
        System.out.println("✅ 유저 단건 조회: " + userService.findById(foundUser.getId()));
        System.out.println("✅ 유저 전체 조회: " + userService.findAll());
        System.out.println("✅ 유저 수정 후 조회: " + userService.findById(foundUser.getId()));

        // 4. When: 삭제
        userService.delete(foundUser.getId());

        // 5. Then: 삭제 검증
        try {
            UserResponse afterDelete = userService.findById(foundUser.getId());
            System.out.println("✅ 삭제 후 유저 조회: " + afterDelete);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ 삭제된 유저는 더 이상 조회할 수 없습니다.");
        }

        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");


        /*Channel Test*/

        //1. Given: 채널 준비
        Channel channel = new Channel("채널 테스트", "채널 설명", ChannelType.VOICE);

        //2. When: 생성/조회/수정
        channelService.create(channel); // 생성
        Channel foundChannel = channelService.findById(channel.getId()); // 단건 조회
        List<Channel> foundAllChannel = channelService.findAll(); // 전체 조회
        foundChannel.updateTime();
        channelService.update(foundChannel);

        //3. Then: 생성/조회/수정 검증
        System.out.println("✅채널 등록: " + foundChannel.getId());
        System.out.println("✅채널 제목 조회: " + foundChannel.getTitle());
        System.out.println("✅채널 내용 조회: " + foundChannel.getDescription());
        System.out.println("✅채널 타입 조회: " + foundChannel.getChannelType());
        System.out.println("✅채널 단건 조회: " + channelService.findById(foundChannel.getId()));
        System.out.println("✅채널 전체 조회: " + channelService.findAll());
        System.out.println("✅채널 수정 후 조회: " + channelService.findById(foundChannel.getId()));

        //4. When: 삭제
        channelService.delete(foundChannel.getId());

        //5. Then: 삭제 검증
        try {
            channelService.findById(foundChannel.getId());
            System.out.println("❌ 삭제 된 채널이 조회됩니다. 오류!");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ 삭제 된 채널은 더이상 조회할 수 없습니다.");
        }
        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");


        /*Message Test*/

        // 1. Given: 메시지 Test를 위한 데이터 준비
        UserCreateRequest senderRequest = new UserCreateRequest("senderUser", "sender@email.com", "1234");
        userService.create(senderRequest);

        // sender ID 꺼내오기
        UUID senderId = userService.findAll().stream()
                .filter(u -> u.getUserId().equals("senderUser"))
                .map(UserResponse::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Channel messageChannel = new Channel("메시지 테스트 채널", "채널 설명", ChannelType.VOICE);
        channelService.create(messageChannel);

        // And Given: 메시지 준비
        Message message = new Message("테스트 내용", senderId, messageChannel.getId());

        // 2. When: 생성/조회/수정
        messageService.create(message);
        Message foundMessage = messageService.findById(message.getId());
        List<Message> foundAllMessage = messageService.findAll();

    }
}
