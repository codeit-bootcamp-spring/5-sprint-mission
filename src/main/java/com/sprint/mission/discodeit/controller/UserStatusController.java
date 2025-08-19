package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-status")
public class UserStatusController {

    private final UserStatusService userStatusService;

    @Autowired
    public UserStatusController(UserStatusService userStatusService) {
        this.userStatusService = userStatusService;
    }

    // ✅ 상태 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> create(@RequestBody UserStatusCreateRequest request) {
        userStatusService.create(request);
        return ResponseEntity.ok().build();
    }

    // ✅ ID로 상태 조회
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserStatus> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userStatusService.findById(id));
    }

    // ✅ 전체 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserStatus>> findAll() {
        return ResponseEntity.ok(userStatusService.findAll());
    }

    // ✅ 상태 수정 (id 기반)
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Void> update(@RequestBody UserStatusUpdateRequest request) {
        userStatusService.update(request);
        return ResponseEntity.ok().build();
    }

    // ✅ 상태 수정 (userId 기반)
    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateByUserId(@RequestBody UserStatusUpdateRequest request) {
        userStatusService.updateByUserId(request);
        return ResponseEntity.ok().build();
    }

    // ✅ 상태 삭제
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ 유저 상태 조회
    @RequestMapping(value = "/online", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateOnlineStatus(@RequestBody UserStatusUpdateRequest request) {
        userStatusService.updateOnlineStatus(request);
        return ResponseEntity.ok().build();
    }


}
