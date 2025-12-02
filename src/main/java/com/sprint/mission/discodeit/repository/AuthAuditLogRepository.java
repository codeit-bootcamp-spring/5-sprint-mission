package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.AuthAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, UUID> {

}
