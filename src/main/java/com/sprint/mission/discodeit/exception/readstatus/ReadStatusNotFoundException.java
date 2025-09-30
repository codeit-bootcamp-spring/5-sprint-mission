package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Collections;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class ReadStatusNotFoundException extends ReadStatusException {

  public ReadStatusNotFoundException(UUID id) {
    super(ErrorCode.READ_STATUS_NOT_FOUND, Collections.singletonMap("id", id));
  }
}
