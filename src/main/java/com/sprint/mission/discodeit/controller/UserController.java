package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreateFile;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> create(
            @RequestPart("user") CreateUserRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        UserResponse created = userService.register(request, toCreateFile(profileImage));
        if (created == null) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(value = "/findAll", method = GET)
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> update(
            @RequestPart("user") UpdateUserRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        UserResponse updated = userService.update(request, toCreateFile(profileImage));
        if (updated == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/status/{id}", method = PATCH)
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable UUID id) {
        userStatusService.updateByUserId(id);
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.remove(id);
        return ResponseEntity.noContent().build();
    }

    private CreateFile toCreateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        return new CreateFile(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes(),
                file.getSize()
        );
    }
}
