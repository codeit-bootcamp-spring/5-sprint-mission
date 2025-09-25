package com.sprint.mission.discodeit.domain.message.exception;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class MessageAttachmentFailed extends MessageException {
    public MessageAttachmentFailed(String filename) {
        super(ErrorCode.MESSAGE_ATTACHMENT_UPLOAD_FAILED, Map.of("filename", filename));
    }
}