package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "Message 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Message가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = MessageDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId| not found"))
            )
    })
    public ResponseEntity<MessageDto> create(@RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
                                             @RequestPart(required = false, value = "attachments") List<MultipartFile> attachments) {
        List<BinaryContentCreateRequest> binaryContent = List.of();

        if (attachments != null && !attachments.isEmpty()) {
            binaryContent = attachments.stream()
                    .map(file -> {
                        try {
                            return new BinaryContentCreateRequest(
                                    file.getOriginalFilename(), file.getContentType(), file.getBytes()
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }

        MessageDto message = messageService.create(messageCreateRequest, binaryContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }


    @Operation(summary = "Message 내용 수정")
    @PatchMapping(value = "/{messageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Message가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = MessageDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
            )
    })
    public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
                                             @RequestBody MessageUpdateRequest messageUpdateRequest) {
        MessageDto message = messageService.update(messageId, messageUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @Operation(summary = "Message 삭제")
    @DeleteMapping(value = "/{messageId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Message가 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
            )
    })
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Channel의 Message 목록 조회 성공")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Message 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MessageDto.class)))
            )
    })
    public ResponseEntity<List<MessageDto>> findByChannelId(@RequestParam("channelId") UUID id) {
        List<MessageDto> messages = messageService.findAllByChannelId(id);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
