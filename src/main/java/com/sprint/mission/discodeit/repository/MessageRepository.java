package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    long count();

    Message delete(UUID id);

    boolean existsById(UUID id);

    // 더필요한 내용은 아래 query 메서드 이름 형식을 참고하여 더 작성할것
    // https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html

}
