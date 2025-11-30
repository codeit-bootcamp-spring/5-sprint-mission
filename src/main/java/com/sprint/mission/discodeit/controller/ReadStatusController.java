package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.docs.ReadStatusControllerDocs;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusControllerDocs {

    private final ReadStatusService readStatusService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadStatusDto create(@RequestBody @Valid ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReadStatusDto> findAllByUserId(@RequestParam("userId") UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }

    @PatchMapping("/{readStatusId}")
    @ResponseStatus(HttpStatus.OK)
    public ReadStatusDto update(
        @PathVariable
        UUID readStatusId,
        @RequestBody
        @Valid
        ReadStatusUpdateRequest request
    ) {
        return readStatusService.update(readStatusId, request);
    }
}
