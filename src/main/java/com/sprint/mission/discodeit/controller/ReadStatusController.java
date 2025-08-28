// package com.sprint.mission.discodeit.controller.readstatus;
//
// import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
// import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
// import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
// import com.sprint.mission.discodeit.service.readstatus.ReadStatusService;
// import jakarta.validation.Valid;
// import java.util.List;
// import java.util.UUID;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.bind.annotation.RestController;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping(path = "/api/readStatuses")
// public class ReadStatusController {
//
//   private final ReadStatusService readStatusService;
//
//   @GetMapping
//   @ResponseStatus(HttpStatus.OK)
//   public List<ReadStatusDto> findAllByUserId(
//
//       @RequestParam("userId")
//       UUID userId
//   ) {
//
//     return readStatusService.findAllByUserId(userId);
//   }
//
//   @PostMapping
//   @ResponseStatus(HttpStatus.CREATED)
//   public ReadStatusDto create(
//
//       @RequestBody
//       @Valid
//       ReadStatusCreateRequest body
//   ) {
//
//     return readStatusService.create(body);
//   }
//
//   @PatchMapping(path = "/{readStatusId}")
//   @ResponseStatus(HttpStatus.OK)
//   public ReadStatusDto update(
//
//       @PathVariable("readStatusId")
//       UUID id,
//
//       @RequestBody
//       @Valid
//       ReadStatusUpdateRequest body
//   ) {
//
//     return readStatusService.update(id, body);
//   }
// }
