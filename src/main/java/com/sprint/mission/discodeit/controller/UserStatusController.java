package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-status")
public class UserStatusController {

    private final UserStatusService userStatusService;

    public UserStatusController(UserStatusService userStatusService) {
        this.userStatusService = userStatusService;
    }

    // ✅ 상태 생성
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody UserStatusCreateRequest request) {
        userStatusService.create(request);
        return ResponseEntity.ok().build();
    }

    // ✅ ID로 상태 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserStatus> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userStatusService.findById(id));
    }

    // ✅ 전체 조회
    @GetMapping
    public ResponseEntity<List<UserStatus>> findAll() {
        return ResponseEntity.ok(userStatusService.findAll());
    }

    // ✅ 상태 수정 (id 기반)
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody UserStatusUpdateRequest request) {
        userStatusService.update(request);
        return ResponseEntity.ok().build();
    }

    // ✅ 상태 수정 (userId 기반)
    @PatchMapping
    public ResponseEntity<Void> updateByUserId(@RequestBody UserStatusUpdateByUserIdRequest request) {
        userStatusService.updateByUserId(request);
        return ResponseEntity.ok().build();
    }

    // ✅ 상태 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
