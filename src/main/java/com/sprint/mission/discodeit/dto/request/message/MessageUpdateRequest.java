package com.sprint.mission.discodeit.dto.request.message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageUpdateRequest {
	@NotNull(message = "작성자 ID는 필수")
	private UUID authorId;

	@NotBlank(message = "메시지 내용은 필수")
	private String content;

	@Builder.Default
	private List<BinaryContentCreateRequest> attachmentsToAdd = new ArrayList<>();

	@Builder.Default
	private List<UUID> attachmentIdsToRemove = new ArrayList<>();
}