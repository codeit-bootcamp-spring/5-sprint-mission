package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessagesGetByAuthorRequest {
    @NotBlank(message = "작성자 닉네임은 필수")
    private String author;

    @NotNull(message = "채널 ID는 필수")
    private UUID channelId;
}