package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ReadStatus", description = "메세지 읽음표시 API")
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  /* 메세지별 읽음표시
   * 내가 채팅방 안 메세지를 어디까지 읽었는지, 13시 10분 읽음 등
   * */

  //A채널 읽었을때 읽었음!이라는 새로운 읽음상태 생성
  @Operation(summary = "읽음상태 생성")
  @PostMapping
  public ResponseEntity<ReadStatusDto> create(@RequestBody ReadStatusDto dto) {
    ReadStatusDto created = readStatusService.create(dto); //서비스가 DTO를 반환
    return ResponseEntity.status(201).body(created);
  }

  //읽음상태 업데이트
  @Operation(summary = "읽음상태 업데이트")
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusDto dto
  ) {
    ReadStatusDto updated = readStatusService.update(readStatusId, dto);
    return ResponseEntity.ok(updated);
  }

  //내가 속한 모든 채팅방에서, 내가 어디까지 읽었는지
  //안읽은 메세지 3개, 마지막으로 읽은 위치 등과 같은
  @Operation(summary = "내 모든 채팅방 읽음상태 조회")
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }

  //채널 삭제시, 그 채널의 모든 읽음기록 지우기
  @Operation(summary = "채널의 모든 읽음상태 삭제")
  @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteByChannelId(@PathVariable UUID channelId) {
    readStatusService.deleteByChannelId(channelId);
    return ResponseEntity.noContent().build();
  }
}
