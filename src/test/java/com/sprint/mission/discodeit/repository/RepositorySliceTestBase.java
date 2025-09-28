package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
public abstract class RepositorySliceTestBase {}
