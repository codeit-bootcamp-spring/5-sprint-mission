package com.sprint.mission.discodeit.controller.readstatus;

import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.service.readstatus.ReadStatusService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/read-statuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @GetMapping({"", "/"})
  @ResponseStatus(HttpStatus.OK)
  public List<ReadStatusResponse> findAllByUserId(
      @RequestParam("userId") UUID userId) {
    return readStatusService.findAllByUserId(userId);
  }

  @PostMapping
  public ResponseEntity<ReadStatusResponse> create(
      @Valid @RequestBody ReadStatusCreateRequest body) {
    ReadStatusResponse res = readStatusService.create(body);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}").buildAndExpand(res.id()).toUri();
    return ResponseEntity.created(location).body(res);
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<Void> update(@PathVariable("id") UUID id,
      @Valid @RequestBody ReadStatusUpdateRequest body) {
    readStatusService.update(id, body);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(path = "/by")
  public ResponseEntity<ReadStatusResponse> findByUserAndChannel(
      @RequestParam("userId") UUID userId,
      @RequestParam("channelId") UUID channelId) {
    return ResponseEntity.ok(readStatusService.findByUserAndChannel(userId, channelId));
  }
}
