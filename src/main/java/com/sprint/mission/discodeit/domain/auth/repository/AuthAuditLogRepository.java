package com.sprint.mission.discodeit.domain.auth.repository;

import com.sprint.mission.discodeit.domain.auth.entity.AuthAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, UUID> {

}
