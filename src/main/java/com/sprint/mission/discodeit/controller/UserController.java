// package com.sprint.mission.discodeit.controller.user;
//
// import static com.sprint.mission.discodeit.support.Constants.MAX_EMAIL_LENGTH;
// import static com.sprint.mission.discodeit.support.Constants.MAX_USERNAME_LENGTH;
// import static com.sprint.mission.discodeit.support.Constants.MIN_EMAIL_LENGTH;
// import static com.sprint.mission.discodeit.support.Constants.MIN_USERNAME_LENGTH;
// import static com.sprint.mission.discodeit.support.Constants.SUPPORTED_IMAGE_TYPE;
//
// import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
// import com.sprint.mission.discodeit.dto.user.UserDto;
// import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
// import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
// import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
// import com.sprint.mission.discodeit.exception.ParameterNumberNotValidException;
// import com.sprint.mission.discodeit.service.user.UserService;
// import com.sprint.mission.discodeit.support.FileNames;
// import jakarta.validation.Valid;
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.Size;
// import java.io.IOException;
// import java.util.List;
// import java.util.UUID;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.HttpMediaTypeNotSupportedException;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RequestPart;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
// @Validated
// public class UserController {
//
//   private final UserService userService;
//
//   @GetMapping
//   public List<UserDto> findAll(
//
//       @RequestParam(required = false)
//       @Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH)
//       String username,
//
//       @RequestParam(required = false)
//       @Size(min = MIN_EMAIL_LENGTH, max = MAX_EMAIL_LENGTH)
//       @Email
//       String email
//   ) {
//
//     if (username != null && email != null) {
//       throw new ParameterNumberNotValidException(List.of("username", "email"));
//     }
//     if (username != null) {
//       return userService.findByUsername(username);
//     }
//     if (email != null) {
//       return userService.findByEmail(email);
//     }
//     return userService.findAll();
//   }
//
//   @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//   @ResponseStatus(HttpStatus.CREATED)
//   public UserDto create(
//
//       @RequestPart("userCreateRequest")
//       @Valid
//       UserCreateRequest req,
//
//       @RequestPart(value = "profile", required = false)
//       MultipartFile profile
//   ) throws HttpMediaTypeNotSupportedException, IOException {
//
//     if (profile != null && !profile.isEmpty()) {
//       String ct = FileNames.normalizeContentType(profile.getContentType());
//       if (!SUPPORTED_IMAGE_TYPE.contains(ct)) {
//         throw new HttpMediaTypeNotSupportedException(
//             "Content-Type '%s' not supported".formatted(ct),
//             SUPPORTED_IMAGE_TYPE.stream().map(MediaType::valueOf).toList());
//       }
//     }
//
//     return userService.create(req, profile);
//   }
//
//   @GetMapping(path = "/{userId}")
//   public UserDto find(
//
//       @PathVariable("userId")
//       UUID userId
//   ) {
//     return userService.find(userId);
//   }
//
//   @DeleteMapping(path = "/{userId}")
//   @ResponseStatus(HttpStatus.NO_CONTENT)
//   public void delete(
//
//       @PathVariable("userId")
//       UUID userId
//   ) {
//     userService.delete(userId);
//   }
//
//   @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//   public UserDto update(
//
//       @PathVariable("userId")
//       UUID userId,
//
//       @RequestPart(value = "userUpdateRequest", required = false)
//       @Valid
//       UserUpdateRequest req,
//
//       @RequestPart(value = "profile", required = false)
//       MultipartFile profile
//   ) throws HttpMediaTypeNotSupportedException, IOException {
//
//     if (profile != null && !profile.isEmpty()) {
//       String ct = FileNames.normalizeContentType(profile.getContentType());
//       if (!SUPPORTED_IMAGE_TYPE.contains(ct)) {
//         throw new HttpMediaTypeNotSupportedException(
//             "Content-Type '%s' not supported".formatted(ct),
//             SUPPORTED_IMAGE_TYPE.stream().map(MediaType::valueOf).toList());
//       }
//     }
//
//     return userService.update(userId, req, profile);
//   }
//
//   @PatchMapping(path = "/{userId}/userStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
//   @ResponseStatus(HttpStatus.OK)
//   public UserStatusDto updateUserStatusByUserId(
//
//       @PathVariable("userId")
//       UUID userId,
//
//       @RequestBody
//       UserStatusUpdateRequest req
//   ) {
//
//     return userService.updateUserStatusByUserId(userId, req);
//   }
//
//   @PostMapping(path = "/{userId}/heartbeat")
//   @ResponseStatus(HttpStatus.NO_CONTENT)
//   public void heartbeat(
//
//       @PathVariable("userId")
//       UUID userId
//   ) {
//     userService.heartbeat(userId);
//   }
// }
