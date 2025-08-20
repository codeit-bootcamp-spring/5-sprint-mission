package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChannelUpdateRequest {
	@NotBlank(message = "채널 명은 필수")
	private String newName;
	private String newDescription;
}
