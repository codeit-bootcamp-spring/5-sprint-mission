package com.sprint.mission.discodeit.config.init;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.exception.user.UserDuplicateException;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class AdminInitializer implements CommandLineRunner {

  private final UserService userService;

  @Override
  public void run(String... args) {

    // TODO 나중에 환경변수로 분리하면 좋을듯
    try {

      userService.create(UserDto.CreateCommand.builder()
                                              .username("admin")
                                              .password("admin1234")
                                              .email("admin@example.com")
                                              .role(UserRole.ADMIN.name())
                                              .build());
    } catch (UserDuplicateException e) {
      log.info("Admin already exists");
    }
  }
}