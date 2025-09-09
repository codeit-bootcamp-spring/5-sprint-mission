package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.docs.ReadStatusControllerDocs;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusControllerDocs {

    private final ReadStatusRepository readStatusRepository;

    private final ReadStatusService readStatusService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReadStatusDto> findAllByUserId(@RequestParam("userId") UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadStatusDto create(@RequestBody @Valid ReadStatusCreateRequest req) {
        return readStatusService.create(req);
    }

    @PatchMapping("/{readStatusId}")
    @ResponseStatus(HttpStatus.OK)
    public ReadStatusDto update(
        @PathVariable UUID readStatusId,
        @RequestBody @Valid ReadStatusUpdateRequest req
    ) {
        return readStatusService.update(readStatusId, req);
    }
}
