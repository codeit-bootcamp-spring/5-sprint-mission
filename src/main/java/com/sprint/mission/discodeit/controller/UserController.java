package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/register", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<User>> userRegister(@RequestPart("user") UserCreateRequest userCreateRequest,
                                                        @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        BinaryContentCreateRequest contentCreateRequest = null;

        if (profile != null && !profile.isEmpty()) {
            contentCreateRequest = new BinaryContentCreateRequest(
                    profile.getName(),
                    profile.getContentType(),
                    profile.getBytes());
        }
        User user = userService.create(userCreateRequest, Optional.ofNullable(contentCreateRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(user, "사용자가 생성되었습니다"));
    }
}
