package com.sprint.mission.discodeit.domain.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, UUID> {

}
