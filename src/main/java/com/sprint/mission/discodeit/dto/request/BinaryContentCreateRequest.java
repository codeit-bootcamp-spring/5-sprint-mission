package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BinaryContentCreateRequest", description = "업로드할 바이너리 컨텐츠 정보")
public record BinaryContentCreateRequest(
        @Schema(description = "파일명")
        String fileName,
        @Schema(description = "콘텐츠 타입(MIME)")
        String contentType,
        @Schema(description = "파일 바이트", type = "string", format = "byte")
        byte[] bytes
) {
}
