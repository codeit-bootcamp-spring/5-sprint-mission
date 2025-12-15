package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.FailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FailLogRepository extends JpaRepository<FailLog, UUID> {

}
