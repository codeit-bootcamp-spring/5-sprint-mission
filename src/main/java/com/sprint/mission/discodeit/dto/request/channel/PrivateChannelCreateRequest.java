package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PrivateChannelCreateRequest {
    @NotEmpty(message = "참여자 목록은 필수입니다")
    @Size(min = 2, message = "PRIVATE 채널은 최소 2명 이상이어야 합니다")
    private List<UUID> participantIds;
}
