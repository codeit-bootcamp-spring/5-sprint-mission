package com.sprint.mission.discodeit.config.init;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

  private final UserService userService;

  @Override
  public void run(String... args) throws Exception {

    // TODO 나중에 환경변수로 분리하면 좋을듯
    userService.create(UserDto.CreateCommand.builder()
                                            .username("admin")
                                            .password("admin1234")
                                            .email("admin@example.com")
                                            .role(UserRole.ADMIN.name())
                                            .build());
  }
}