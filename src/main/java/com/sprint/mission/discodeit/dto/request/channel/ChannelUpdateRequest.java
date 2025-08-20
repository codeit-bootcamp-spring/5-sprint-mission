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
	private String newName;
	private String newDescription;
}
