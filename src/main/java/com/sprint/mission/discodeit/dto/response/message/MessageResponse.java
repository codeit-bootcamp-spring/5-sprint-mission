package com.sprint.mission.discodeit.dto.response.message;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageResponse {
	private UUID id;
	private Instant createdAt;
	private Instant updatedAt;
	private UUID authorId;
	private UUID channelId;
	private String content;
	private List<BinaryContentDTO> attachments;

    private UserResponse author;

	private MessageResponse(Message message) {
		this.id = message.getId();
		this.createdAt = message.getCreatedAt();
		this.updatedAt = message.getUpdatedAt();
		this.authorId = message.getAuthor().getId();
		this.channelId = message.getChannel().getId();
		this.content = message.getContent();
        this.attachments = message.getAttachments().stream()
                .map(attachment -> BinaryContentDTO.builder()
                        .id(attachment.getId())
                        .fileName(attachment.getFileName())
                        .contentType(attachment.getContentType())
                        .size(attachment.getSize())
                        .build())
                .toList();
        this.author = UserResponse.success(message.getAuthor());
	}

	public static MessageResponse success(Message message) {
		return new MessageResponse(message);
	}
}
