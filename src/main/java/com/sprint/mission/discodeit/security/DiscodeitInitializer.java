package com.sprint.mission.discodeit.security;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.service.InitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscodeitInitializer implements ApplicationRunner {
	private final InitService initService;

	@Override
	public void run(ApplicationArguments args) {
		initService.initAdmin();
		initService.initUser();
	}
}
