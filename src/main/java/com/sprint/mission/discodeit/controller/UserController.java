package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final BinaryContentService binaryContentService;

    // 사용자 등록: POST /user
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<UserResponse.detail> create(@Valid @ModelAttribute UserRequest.create req) {
        return ResponseEntity.ok(userService.create(req));
    }

    // 사용자 정보 수정 // 이름, 이메일
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<User> update(@Valid @RequestBody UserRequest.update req) {
        return ResponseEntity.ok(userService.update(req));
    }

    // 비밀번호 수정
    @RequestMapping(value = "update/password", method = RequestMethod.PUT)
    public ResponseEntity<User> updatePassword(@Valid @RequestBody UserRequest.passwordReset req) {
        return ResponseEntity.ok(userService.updatePassword(req));
    }

    // 모든 사용자 조회: GET /user
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse.summary>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 이메일로 사용자 조회
    @RequestMapping(value = "findByEmail",method = RequestMethod.GET, params = "email")
    public ResponseEntity<User> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    // ID로 사용자 조회: GET /user/{id}
    @RequestMapping(value = "findById/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse.summary> findById(@PathVariable UUID id) {

        User user = userService.findById(id);

        UUID imageId = user.getProfileId();
        BinaryContent image = binaryContentService.findById(imageId);
        String imageUrl = (imageId != null) ? "/binary/" + imageId : null;

        return ResponseEntity.ok(
                UserResponse.summary.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build()
        );
    }

    // 삭제: DELETE /user/{id}
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        boolean deleted = userService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
