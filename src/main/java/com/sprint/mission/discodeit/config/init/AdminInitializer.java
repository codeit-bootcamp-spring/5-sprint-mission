package com.sprint.mission.discodeit.config.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements ApplicationRunner {

    private final InitAdminService initService;

    @Override
    public void run(ApplicationArguments args) {
        initService.initAdmin();
    }
}
