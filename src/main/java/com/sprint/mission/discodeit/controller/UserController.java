package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDeleteResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> getUserAll() {
        List<UserResponse> userResponses = userService.findAll();

        return ResponseEntity.ok(userResponses);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        try {
            UserProfileImageRequest imageRequest = convertFile(profile);
            if (imageRequest != null) {
                request.setProfileImage(imageRequest);
            }

            UserResponse response = userService.create(request);

            URI location = URI.create("/api/users/" + response.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .location(location)
                    .body(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {
        try {
            UserProfileImageRequest imageRequest = convertFile(profile);
            UserResponse userResponse = userService.update(userId, request, imageRequest);
            return ResponseEntity.ok(userResponse);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<UserDeleteResponse> deleteUserById(@PathVariable UUID userId) {
        UserDeleteResponse userDeleteResponse = userService.delete(userId);
        return ResponseEntity.ok(userDeleteResponse);
    }

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public ModelAndView userListPage() {
        return new ModelAndView("redirect:/user-list.html");
    }

    private UserProfileImageRequest convertFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        return UserProfileImageRequest.builder()
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .bytes(file.getBytes())
                .build();
    }
}
