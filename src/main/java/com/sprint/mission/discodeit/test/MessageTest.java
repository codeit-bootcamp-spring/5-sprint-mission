package com.sprint.mission.discodeit.test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.dto.request.binaryContent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.JoinChannelRequest;
import com.sprint.mission.discodeit.dto.request.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.DeleteMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.GetMessagesByChannelIdRequest;
import com.sprint.mission.discodeit.dto.request.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.response.channel.CreateChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.JoinChannelResponse;
import com.sprint.mission.discodeit.dto.response.message.DeleteMessageResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.dto.response.user.CreateUserResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class MessageTest {

	public static void message(ConfigurableApplicationContext context) {
		System.out.println("시나리오 3: 첨부파일 & 메시지 관리");
		System.out.println();

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

		try {
			System.out.println("1. 회원가입 - 이동재");
			CreateUserRequest leedjRequest = CreateUserRequest.builder()
				.loginId("leedj")
				.password("1234")
				.defaultNickname("이동재")
				.email("leedj@lee.com")
				.build();

			CreateUserResponse leedjResponse = userService.createUser(leedjRequest);
			System.out.println("이동재 회원가입: " + (leedjResponse != null ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("2. 채널 생성");
			CreatePublicChannelRequest channelRequest = CreatePublicChannelRequest.builder()
				.channelName("임시")
				.build();

			CreateChannelResponse channelResponse = channelService.createPublicChannel(channelRequest);
			System.out.println("임시 채널 생성: " + (channelResponse != null ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("3. 이동재 채널 참여");
			JoinChannelRequest joinRequest = JoinChannelRequest.builder()
				.userId(leedjResponse.getId())
				.userDefaultNickname("이동재")
				.channelName("임시")
				.build();

			JoinChannelResponse joinResponse = channelService.joinChannel(joinRequest);
			System.out.println("이동재 채널 참여: " + (joinResponse != null && joinResponse.isSuccess() ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("4. 첨부파일 확인");
			String filePath1 = "src/main/java/com/sprint/mission/discodeit/mococo.png";
			String filePath2 = "src/main/java/com/sprint/mission/discodeit/mococoHello.png";

			boolean file1Exists = Files.exists(Paths.get(filePath1));
			boolean file2Exists = Files.exists(Paths.get(filePath2));

			System.out.println("mococo.png 파일: " + (file1Exists ? "존재" : "없음"));
			System.out.println("mococoHello.png 파일: " + (file2Exists ? "존재" : "없음"));

			if (!file1Exists || !file2Exists) {
				System.out.println("첨부파일이 없어서 테스트를 중단합니다.");
				return;
			}
			System.out.println("---------------------------------");

			System.out.println("5. 첨부파일 포함 메시지 작성");
			byte[] imageContent1 = Files.readAllBytes(Paths.get(filePath1));

			CreateBinaryContentRequest attachment1 = CreateBinaryContentRequest.builder()
				.filename("mococo.png")
				.contentType("image/png")
				.size((long)imageContent1.length)
				.content(imageContent1)
				.build();

			CreateMessageRequest messageRequest = CreateMessageRequest.builder()
				.authorId(leedjResponse.getId())
				.channelId(channelResponse.getId())
				.text("모코코 이미지 공유합니다")
				.attachments(Arrays.asList(attachment1))
				.build();

			MessageResponse messageResponse = messageService.createMessage(messageRequest);
			System.out.println("메시지 작성: " + (messageResponse != null ? "성공" : "실패"));
			if (messageResponse != null) {
				System.out.println("저장된 메시지 내용: \"" + messageResponse.getText() + "\" - (성공)");
				System.out.println("첨부파일 개수: " + messageResponse.getAttachmentIds().size() + "개");
			}
			System.out.println("messages.ser 파일: " + (new File("data/messages.ser").exists() ? "생성됨" : "없음"));
			System.out.println("binaryContent.ser 파일: " + (new File("data/binaryContent.ser").exists() ? "생성됨" : "없음"));
			System.out.println("---------------------------------");

			System.out.println("6. 메시지 수정 - 첨부파일 추가");
			byte[] imageContent2 = Files.readAllBytes(Paths.get(filePath2));

			CreateBinaryContentRequest attachment2 = CreateBinaryContentRequest.builder()
				.filename("mococoHello.png")
				.contentType("image/png")
				.size((long)imageContent2.length)
				.content(imageContent2)
				.build();

			UpdateMessageRequest updateRequest = UpdateMessageRequest.builder()
				.messageId(messageResponse.getId())
				.authorId(leedjResponse.getId())
				.text("모코코 이미지 2개 공유")
				.attachmentsToAdd(Arrays.asList(attachment2))
				.build();

			MessageResponse updatedMessage = messageService.updateMessage(updateRequest);
			System.out.println("메시지 수정: " + (updatedMessage != null ? "성공" : "실패"));
			if (updatedMessage != null) {
				System.out.println("수정된 메시지 내용: \"" + updatedMessage.getText() + "\" - (성공)");
				System.out.println("첨부파일 개수: " + updatedMessage.getAttachmentIds().size() + "개");
			}
			System.out.println("---------------------------------");

			System.out.println("7. 메시지 조회로 확인");
			GetMessagesByChannelIdRequest getMessagesRequest = GetMessagesByChannelIdRequest.builder()
				.channelId(channelResponse.getId())
				.build();

			List<MessageResponse> messages = messageService.getAllByChannelId(getMessagesRequest);

			System.out.println("임시 채널의 메시지 목록:");
			for (int i = 0; i < messages.size(); i++) {
				MessageResponse msg = messages.get(i);
				System.out.println("[" + (i+1) + "] 이동재: " + msg.getText());
				System.out.println("    첨부파일: " + msg.getAttachmentIds().size() + "개");
			}
			System.out.println("메시지 조회: " + (messages.size() == 1 ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("8. 메시지 삭제 - 첨부파일 연쇄 삭제 확인");

			boolean binaryFileBefore = new File("data/binaryContent.ser").exists();
			System.out.println("삭제 전 binaryContent.ser: " + (binaryFileBefore ? "존재" : "없음"));

			DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
				.messageId(messageResponse.getId())
				.authorId(leedjResponse.getId())
				.build();

			DeleteMessageResponse deleteResponse = messageService.deleteMessage(deleteRequest);
			System.out.println("메시지 삭제: " + (deleteResponse != null && deleteResponse.isSuccess() ? "성공" : "실패"));

			List<MessageResponse> messagesAfterDelete = messageService.getAllByChannelId(getMessagesRequest);
			System.out.println("삭제 후 메시지 개수: " + messagesAfterDelete.size() + "개");

			boolean binaryFileAfter = new File("data/binaryContent.ser").exists();
			System.out.println("삭제 후 binaryContent.ser: " + (binaryFileAfter ? "존재" : "없음"));

			boolean cascadeDeleteWorking = messagesAfterDelete.size() == 0;
			System.out.println("연쇄 삭제: " + (cascadeDeleteWorking ? "정상" : "오류"));
			System.out.println("---------------------------------");

			System.out.println("첨부파일 메시지 완료");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}