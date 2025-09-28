package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
){
=======
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "File name is mandatory") @Size(max = 200, message = "File name must be at most 200 characters") String fileName,
    @NotBlank(message = "Content type is mandatory") @Size(max = 200, message = "Content type must be at most 200 characters") String contentType,

    @NotNull(message = "Content is mandatory") @Size(min = 1, message = "Content must be at least 1 byte") byte[] bytes) {

>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
