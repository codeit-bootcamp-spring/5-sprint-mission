package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) throws IOException {

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
        System.out.println("----------------------------------------");
        System.out.println("-------- Channel 고도화 단독 테스트 --------");
        System.out.println("----------------------------------------");

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


            // -------- ReadStatus 단독 테스트 --------
            System.out.println("----------------------------------------");
            System.out.println("-------- ReadStatus 단독 테스트 --------");
            System.out.println("----------------------------------------");

            // 1. Given: 유저, 채널 생성 (ReadStatus 저장에 필요)
            UserCreateRequest readUserReq = new UserCreateRequest("read-user", "readuser@email.com", "pass");
            userService.create(readUserReq);

            UserResponse readUser = userService.findAll().stream()
                    .filter(u -> u.getUserId().equals("read-user"))
                    .findFirst()
                    .orElseThrow();

            Channel readChannel = new Channel("읽음채널", "읽음 테스트용 채널", ChannelType.TEXT);
            channelService.create(readChannel);

            // 2. When: ReadStatus 생성
            ReadStatusCreateRequest readStatusCreate = new ReadStatusCreateRequest();
            readStatusCreate.setUserId(readUser.getId());
            readStatusCreate.setChannelId(readChannel.getId());

            ReadStatusRepository readStatusRepositoryBean = context.getBean(ReadStatusRepository.class);
            readStatusRepositoryBean.save(new ReadStatus(
                    UUID.randomUUID(),
                    readStatusCreate.getChannelId(),
                    readStatusCreate.getUserId(),
                    Instant.now()
            ));

            // 3. Then: ReadStatus 단건 조회
            List<ReadStatus> allReadStatuses = readStatusRepositoryBean.findAll();
            ReadStatus one = allReadStatuses.get(allReadStatuses.size() - 1);

            System.out.println("✅ ReadStatus 생성: ID = " + one.getId());
            System.out.println("✅ ReadStatus 유저 = " + one.getUserId());
            System.out.println("✅ ReadStatus 채널 = " + one.getChannelId());

            // 4. When: ReadStatus 수정
            ReadStatusUpdateRequest updateRequested = new ReadStatusUpdateRequest();
            updateRequested.setId(one.getId());
            updateRequested.setLastReadAt(Instant.now());

            ReadStatus updatedd = new ReadStatus(
                    one.getId(),
                    one.getChannelId(),
                    one.getUserId(),
                    updateRequested.getLastReadAt()
            );

            readStatusRepositoryBean.save(updatedd);

            // 5. Then: 수정 확인
            ReadStatus afterUpdate = readStatusRepositoryBean.findById(one.getId());
            System.out.println("✅ 수정된 ReadStatus lastReadAt = " + afterUpdate.getLastReadAt());

            // 6. When: 삭제
            readStatusRepositoryBean.deleteById(one.getId());

            // 7. Then: 삭제 확인
            ReadStatus deleted = readStatusRepositoryBean.findById(one.getId());
            if (deleted == null) {
                System.out.println("✅ ReadStatus 삭제 성공");
            } else {
                System.out.println("❌ 삭제 실패: 아직 존재함");
            }

            // -------- UserStatusService 단독 테스트 --------
            System.out.println("----------------------------------------");
            System.out.println("-------- UserStatusService 테스트 --------");
            System.out.println("----------------------------------------");

// 생성된 유저의 ID 찾기
            UserResponse createdUser = userService.findAll().stream()
                    .filter(u -> u.getUserId().equals("read-user"))
                    .findFirst()
                    .orElseThrow();

            UserStatusService userStatusService = context.getBean(UserStatusService.class);

// 생성된 상태 객체 찾기
            List<UserStatus> allStatuses = userStatusService.findAll();
            UserStatus newStatus = allStatuses.stream()
                    .filter(status -> status.getUserId().equals(createdUser.getId()))
                    .findFirst()
                    .orElseThrow();

            System.out.println("✅ UserStatus 생성됨: ID = " + newStatus.getId());

// 4. When: findById 로 조회
            UserStatus found = userStatusService.findById(newStatus.getId());
            System.out.println("✅ UserStatus 단건 조회 성공: userId = " + found.getUserId());

// 5. When: update 수행
            UserStatusUpdateRequest statusUpdateRequest = new UserStatusUpdateRequest();
            statusUpdateRequest.setId(newStatus.getId());
            statusUpdateRequest.setLastOnline(Instant.now().plusSeconds(10));
            userStatusService.update(statusUpdateRequest);

// Then: 수정 확인
            UserStatus afterUpdated = userStatusService.findById(newStatus.getId());
            System.out.println("✅ UserStatus 수정 완료: lastOnline = " + afterUpdated.getLastOnline());

// 6. When: 삭제
            userStatusService.delete(newStatus.getId());

// Then: 삭제 확인
            try {
                userStatusService.findById(newStatus.getId());
                System.out.println("❌ 삭제된 UserStatus가 조회됩니다. 오류!");
            } catch (IllegalArgumentException e1) {
                System.out.println("✅ UserStatus 삭제 성공");
            }
            System.out.println("----------------------------------------");


            // -------- BinaryContent + User 단독 테스트 --------
            System.out.println("----------------------------------------");
            System.out.println("-------- BinaryContent + User 테스트 --------");
            System.out.println("----------------------------------------");

            // 1. Given: 파일 없이 유저 등록
            UserCreateRequest withImage = new UserCreateRequest(
                    "binary-user",
                    "binary@email.com",
                    "pass"
            );

            // BinaryContent 생성자용 데이터 준비
            UUID binaryId = UUID.randomUUID();
            Instant now = Instant.now();
            UUID ownerIdd = UUID.randomUUID(); // 가짜 유저 ID
            String fileName = "test.png";
            String contentType = "image/png";
            long size = 1234L;
            byte[] data = "fake image data".getBytes();

            // BinaryContent 직접 저장
            BinaryContent binary = new BinaryContent(
                    binaryId,
                    now,
                    ownerIdd,
                    fileName,
                    contentType,
                    size,
                    data
            );
            BinaryContentRepository binaryContentRepository = context.getBean(BinaryContentRepository.class);
            binaryContentRepository.save(binary);
            System.out.println("✅ BinaryContent 저장 완료: " + binary.getId());

            // 2. When: 유저 생성
            userService.create(withImage);

            // 3. Then: 유저 저장 확인
            UserResponse createdUserWithImage = userService.findAll().stream()
                    .filter(u -> u.getUserId().equals("binary-user"))
                    .findFirst()
                    .orElseThrow();

            System.out.println("✅ 유저 생성 완료: " + createdUserWithImage.getId());


        }
    }
}