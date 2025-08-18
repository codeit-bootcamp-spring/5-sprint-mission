package com.sprint.mission.discodeit.controller.user;

import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateEmailRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdatePhoneNumberRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateProfileSettingsRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateUsernameRequest;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.user.UserCreateResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.service.binarycontent.BinaryContentService;
import com.sprint.mission.discodeit.service.user.UserService;
import com.sprint.mission.discodeit.support.FileNames;
import com.sprint.mission.discodeit.support.StringUtil;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final BinaryContentService binaryContentService;

  private static final List<String> SUPPORTED = List.of(
      MediaType.APPLICATION_OCTET_STREAM_VALUE,
      MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE,
      "image/webp"
  );

  @GetMapping({"", "/"})
  @ResponseStatus(HttpStatus.OK)
  public List<UserResponse> findAll(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String email) {
    String u = StringUtil.stripToLowerCase(username);
    String e = StringUtil.stripToLowerCase(email);
    if (u != null && e != null) {
      throw new IllegalArgumentException("username과 email은 동시에 포함될 수 없습니다.");
    }
    if (u != null && u.length() < 2) {
      throw new IllegalArgumentException("username은 2자 이상이어야 합니다.");
    }
    if (e != null && e.length() < 6) {
      throw new IllegalArgumentException("email은 6자 이상이어야 합니다.");
    }
    if (u != null) {
      return userService.findByUsername(u);
    }
    if (e != null) {
      return userService.findByEmail(e);
    }
    return userService.findAll();
  }

  @PostMapping(path = {"", "/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public UserCreateResponse create(
      @RequestPart("userCreateRequest") @Valid UserCreateRequest req,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws HttpMediaTypeNotSupportedException, IOException {

    UUID profileId = null;
    if (profile != null && !profile.isEmpty()) {
      String ct = FileNames.normalizeContentType(profile.getContentType());
      if (!SUPPORTED.contains(ct)) {
        throw new HttpMediaTypeNotSupportedException(
            "지원되지 않는 이미지 타입입니다: " + ct,
            SUPPORTED.stream().map(MediaType::valueOf).toList()
        );
      }

      String original = profile.getOriginalFilename();
      String fileName = FileNames.buildStoredName(original, ct);

      BinaryContentResponse saved = binaryContentService.create(
          new BinaryContentCreateRequest(fileName, ct, profile.getBytes())
      );
      profileId = saved.id();
    }

    return userService.create(req, profileId);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse find(@PathVariable("id") UUID id) {
    return userService.find(id);
  }


  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") UUID id) {
    userService.deleteAccount(id);
  }

  @PatchMapping(path = "/{id}/profile-settings")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateProfileSettings(@PathVariable("id") UUID id,
      @Valid @RequestBody UserUpdateProfileSettingsRequest body) {
    userService.updateProfileSettings(id, body);
  }

  @PutMapping(path = "/{id}/profile-image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateProfileImage(@PathVariable("id") UUID id,
      @Valid @RequestBody UserUpdateProfileImageRequest body) {
    userService.updateProfileImage(id, body);
  }

  @DeleteMapping(path = "/{id}/profile-image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void clearProfileImage(@PathVariable("id") UUID id) {
    userService.clearProfileImage(id);
  }

  @PutMapping(path = "/{id}/email")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateEmail(@PathVariable("id") UUID id,
      @Valid @RequestBody UserUpdateEmailRequest body) {
    userService.updateEmail(id, body);
  }

  @PutMapping(path = "/{id}/username")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUsername(@PathVariable("id") UUID id,
      @Valid @RequestBody UserUpdateUsernameRequest body) {
    userService.updateUsername(id, body);
  }

  @PutMapping(path = "/{id}/password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updatePassword(@PathVariable("id") UUID id,
      @Valid @RequestBody UserUpdatePasswordRequest body) {
    userService.updatePassword(id, body);
  }

  @PutMapping(path = "/{id}/phone-number")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updatePhoneNumber(@PathVariable("id") UUID id,
      @Valid @RequestBody UserUpdatePhoneNumberRequest body) {
    userService.updatePhoneNumber(id, body);
  }

  @DeleteMapping(path = "/{id}/phone-number")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void clearPhoneNumber(@PathVariable("id") UUID id) {
    userService.clearPhoneNumber(id);
  }

  @PostMapping(path = "/{id}/deactivation")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deactivateAccount(@PathVariable("id") UUID id) {
    userService.deactivateAccount(id);
  }

  @GetMapping(path = "/{id}/friends")
  @ResponseStatus(HttpStatus.OK)
  public List<UserResponse> getFriends(@PathVariable("id") UUID id) {
    return userService.getFriends(id);
  }

  @DeleteMapping(path = "/{id}/friends/{friendId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeFriend(@PathVariable("id") UUID id,
      @PathVariable("friendId") UUID friendId) {
    userService.removeFriend(id, friendId);
  }
}
