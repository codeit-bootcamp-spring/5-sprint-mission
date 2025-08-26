package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @Operation(summary = "Message 읽음 상태 생성")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = ReadStatusDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found"))
            ),
            @ApiResponse(
                    responseCode = "400", description = "이미 읽음 상태가 존재함",
                    content = @Content(examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists"))
            )
    })
    public ResponseEntity<ReadStatusDto> readStatusCreate(@RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatusDto readStatus = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }


    @Operation(summary = "Message 읽음 상태 수정")
    @PatchMapping(value = "/{readStatusId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = ReadStatusDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found"))
            )
    })
    public ResponseEntity<ReadStatusDto> Update(@PathVariable("readStatusId") UUID readStatusId,
                                                          @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatusDto readStatus = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(readStatus);
    }


    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
                    content = @Content(array = @ArraySchema (schema = @Schema(implementation = ReadStatusDto.class)))
            )
    })
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
    }
}
