package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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


        /* Channel 고도화 테스트 (PRIVATE + ReadStatus) */

// 1. Given: PRIVATE 채널에 참여할 유저들 생성
        String userId1 = "channelUser-" + UUID.randomUUID();
        String userId2 = "channelUser-" + UUID.randomUUID();

        userService.create(new UserCreateRequest(userId1, userId1 + "@email.com", "1234"));
        userService.create(new UserCreateRequest(userId2, userId2 + "@email.com", "1234"));

        UUID ownerId = userService.findAll().stream()
                .filter(u -> u.getUserId().equals(userId1))
                .map(UserResponse::getId)
                .findFirst()
                .orElseThrow();

        UUID memberId = userService.findAll().stream()
                .filter(u -> u.getUserId().equals(userId2))
                .map(UserResponse::getId)
                .findFirst()
                .orElseThrow();

// 2. When: PrivateChannelCreateRequest 만들고 채널 생성
        PrivateChannelCreateRequest requested = new PrivateChannelCreateRequest();
        requested.setOwnerId(ownerId.toString());
        requested.setMembersId(List.of(ownerId.toString(), memberId.toString()));

        channelService.createPrivateChannel(requested);

// 3. Then: 채널 조회 및 검증
        Channel created = channelService.findAll().get(channelService.findAll().size() - 1);
        System.out.println("✅ PRIVATE 채널 ID: " + created.getId());
        System.out.println("✅ 채널 타입: " + created.getChannelType());
        System.out.println("✅ 채널 소유자: " + created.getOwnerId());
        System.out.println("✅ 채널 제목: '" + created.getTitle() + "' (비어 있음이 정상)");

// 4. Then: ReadStatus 조회 및 검증
        ReadStatusRepository readStatusRepository = context.getBean(ReadStatusRepository.class);
        List<ReadStatus> readStatuses = readStatusRepository.findByChannelId(created.getId());

        System.out.println("✅ ReadStatus 총 개수: " + readStatuses.size());
        for (ReadStatus rs : readStatuses) {
            System.out.println("   📌 ReadStatus - userId: " + rs.getUserId() + ", 채널 ID: " + rs.getChannelId());
        }
        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");



        /* Message Test */

        //1. Given: 유저, 채널, 메시지 생성 준비
        UUID senderId = userService.findAll().get(0).getId(); // 첫 번째 유저
        UUID channelId = channelService.findAll().get(0).getId(); // 첫 번째 채널

        MessageCreateRequest createRequest = new MessageCreateRequest();
        createRequest.setContent("메시지 테스트");
        createRequest.setChannelId(channelId);
        createRequest.setSender(senderId);
        createRequest.setAttachmentIds(null); // 첨부파일 없을 수도 있음

        //2. When: 메시지 생성
        messageService.create(createRequest);

        //3. Then: 생성된 메시지 확인
        List<Message> allMessages = messageService.findAllByChannelId(channelId);
        Message create = allMessages.get(allMessages.size() - 1); // 마지막 메시지

        System.out.println("✅ 메시지 생성됨: " + create.getId());
        System.out.println("✅ 메시지 내용: " + create.getContent());
        System.out.println("✅ 메시지 보낸 사람: " + create.getSender());
        System.out.println("✅ 메시지 채널: " + create.getChannelId());

        //4. When: 메시지 수정
        MessageUpdateRequest updatedRequest = new MessageUpdateRequest();
        updatedRequest.setId(create.getId());
        updatedRequest.setContent("수정된 메시지 ✨");

        messageService.update(updatedRequest);

        //5. Then: 수정된 메시지 확인
        Message updated = messageService.findById(create.getId());
        System.out.println("✅ 메시지 수정 완료: " + updated.getContent());

        //6. When: 메시지 삭제
        messageService.delete(updated);

        //7. Then: 삭제된 메시지 확인
        try {
            messageService.findById(updated.getId());
            System.out.println("❌ 삭제된 메시지가 조회됩니다. 오류!");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ 삭제된 메시지는 더 이상 조회되지 않습니다.");
        }

    }
}