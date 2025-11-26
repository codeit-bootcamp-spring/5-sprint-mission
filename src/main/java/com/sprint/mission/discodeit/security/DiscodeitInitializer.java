package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.service.InitService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DiscodeitInitializer implements ApplicationRunner {

  private final InitService initService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    initService.initAdmin();
    initService.initDefaultUser();
  }
}
