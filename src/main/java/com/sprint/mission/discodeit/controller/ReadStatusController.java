package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

  private final BasicReadStatusService readStatusService;

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public ResponseEntity<ReadStatus> create(
      @RequestPart ReadStatusCreateRequest readStatusCreateRequest) {
    ReadStatus readStatus = readStatusService.create(readStatusCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
  }

  @RequestMapping(value = {"/update"}, method = RequestMethod.POST)
  public ResponseEntity<ReadStatus> update(
      @RequestPart ReadStatusUpdateRequest readStatusUpdateRequest) {
    ReadStatus readStatus = readStatusService.update(readStatusUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(readStatus);
  }

  @RequestMapping(value = {"/find/{userId}",
      "findAllByUserId/{userId}"}, method = RequestMethod.GET)
  public ResponseEntity<List<ReadStatus>> findByUser(@PathVariable UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
  }
}
