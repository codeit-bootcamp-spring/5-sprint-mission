package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record MessageCreateMultipartForm(
    @Schema(implementation = MessageCreateRequest.class)
    MessageCreateRequest messageCreateRequest,

    @Schema(description = "Message 첨부 파일들")
    List<MultipartFile> profile
) {
}
