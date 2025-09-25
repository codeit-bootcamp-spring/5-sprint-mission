package com.sprint.mission.discodeit.domain.message.exception;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class MessageAttachmentFailed extends MessageException {
    public MessageAttachmentFailed(Long messageId) {
        super(ErrorCode.MESSAGE_ATTACHMENT_UPLOAD_FAILED, Map.of("messageId", messageId));
    }
}