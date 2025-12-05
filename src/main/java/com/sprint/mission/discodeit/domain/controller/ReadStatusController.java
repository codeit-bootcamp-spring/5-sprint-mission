package com.sprint.mission.discodeit.domain.controller;

import com.sprint.mission.discodeit.common.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.domain.controller.docs.ReadStatusControllerDocs;
import com.sprint.mission.discodeit.domain.dto.readstatus.data.ReadStatusDto;
import com.sprint.mission.discodeit.domain.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.domain.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.domain.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusControllerDocs {

    private final ReadStatusService readStatusService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadStatusDto create(
        @AuthenticationPrincipal DiscodeitUserDetails userDetails,
        @RequestBody @Valid ReadStatusCreateRequest request
    ) {
        return readStatusService.create(userDetails.getUserDetailsDto().id(), request);
    }

    @GetMapping
    public List<ReadStatusDto> findAllByUserId(
        @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        return readStatusService.findAllByUserId(userDetails.getUserDetailsDto().id());
    }

    @PatchMapping("/{readStatusId}")
    public ReadStatusDto update(
        @AuthenticationPrincipal DiscodeitUserDetails userDetails,
        @PathVariable UUID readStatusId,
        @RequestBody @Valid ReadStatusUpdateRequest request
    ) {
        return readStatusService.update(readStatusId, userDetails.getUserDetailsDto().id(), request);
    }
}
