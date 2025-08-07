package com.sprint.mission.discodeit.dto.request.message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.binaryContent.CreateBinaryContentRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CreateMessageRequest {
	@NotBlank(message = "작성자 ID는 필수")
	private UUID authorId;

	@NotBlank(message = "채널 ID는 필수")
	private UUID channelId;

	@NotBlank(message = "메시지 내용은 필수")
	private String text;

	@Builder.Default
	private List<CreateBinaryContentRequest> attachments = new ArrayList<>();
}