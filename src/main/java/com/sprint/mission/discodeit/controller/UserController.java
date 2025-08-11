package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
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
    private final BinaryContentService binaryContentService;

    // 회원 등록: POST /user
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<CreateUserResponse> create(@Valid @ModelAttribute CreateUserRequest req) {
        return ResponseEntity.ok(userService.create(req));
    }

    // 모든 회원 조회: GET /user
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserListResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 이메일로 회원 조회
    @RequestMapping(value = "findByEmail",method = RequestMethod.GET, params = "email")
    public ResponseEntity<User> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    // ID로 회원 조회: GET /user/{id}
    @RequestMapping(value = "findById/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {

        User user = userService.findById(id);

        UUID imageId = user.getProfileId();
        BinaryContent image = binaryContentService.findById(imageId).orElse(null);
        String imageUrl = (imageId != null) ? "/binary/" + imageId : null;

        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                imageId,
                imageUrl,
                (image != null ? image.getContent().length : null),
                (image != null ? image.getContentType() : null)

        ));
    }

    // 회원 정보 수정 // 이름, 이메일
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<User> update(@Valid @RequestBody UpdateUserRequest dto) {
        return ResponseEntity.ok(userService.update(dto));
    }

    // 비밀번호 수정
    @RequestMapping(value = "update/password", method = RequestMethod.PUT)
    public ResponseEntity<User> updatePassword(@Valid @RequestBody PasswordRequest req) {
        return ResponseEntity.ok(userService.updatePassword(req));
    }

    // 회원 삭제: DELETE /user/{id}
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        boolean deleted = userService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
