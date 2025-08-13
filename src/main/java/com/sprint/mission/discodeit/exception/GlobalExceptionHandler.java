package com.sprint.mission.discodeit.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.channel.AlreadyExistsChannelMemberException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateChannelNameException;
import com.sprint.mission.discodeit.exception.channel.NotChannelMemberException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.message.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.exception.readstatus.AlreadyExistsReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateLoginIdException;
import com.sprint.mission.discodeit.exception.user.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.AlreadyExistsUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;

// 에러처리는 status 별로?
// 도메인 별로 모아두기?
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
		return ResponseEntity.status(404).body(e.getMessage());
	}

	@ExceptionHandler(ChannelNotFoundException.class)
	public ResponseEntity<String> handleChannelNotFound(ChannelNotFoundException e) {
		return ResponseEntity.status(404).body(e.getMessage());
	}

	@ExceptionHandler(MessageNotFoundException.class)
	public ResponseEntity<String> handleMessageNotFound(MessageNotFoundException e) {
		return ResponseEntity.status(404).body(e.getMessage());
	}

	@ExceptionHandler(BinaryContentNotFoundException.class)
	public ResponseEntity<String> handleBinaryContentNotFound(BinaryContentNotFoundException e) {
		return ResponseEntity.status(404).body(e.getMessage());
	}

	@ExceptionHandler(UserStatusNotFoundException.class)
	public ResponseEntity<String> handleUserStatusNotFound(UserStatusNotFoundException e) {
		return ResponseEntity.status(404).body(e.getMessage());
	}

	@ExceptionHandler(ReadStatusNotFoundException.class)
	public ResponseEntity<String> handleReadStatusNotFound(ReadStatusNotFoundException e) {
		return ResponseEntity.status(404).body(e.getMessage());
	}

	@ExceptionHandler(DuplicateLoginIdException.class)
	public ResponseEntity<String> handleDuplicateLoginId(DuplicateLoginIdException e) {
		return ResponseEntity.status(409).body(e.getMessage());
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<String> handleDuplicateEmail(DuplicateEmailException e) {
		return ResponseEntity.status(409).body(e.getMessage());
	}

	@ExceptionHandler(DuplicateChannelNameException.class)
	public ResponseEntity<String> handleDuplicateChannelName(DuplicateChannelNameException e) {
		return ResponseEntity.status(409).body(e.getMessage());
	}

	@ExceptionHandler(AlreadyExistsChannelMemberException.class)
	public ResponseEntity<String> handleAlreadyExistsChannelMember(AlreadyExistsChannelMemberException e) {
		return ResponseEntity.status(409).body(e.getMessage());
	}

	@ExceptionHandler(AlreadyExistsUserStatusException.class)
	public ResponseEntity<String> handleAlreadyExistsUserStatus(AlreadyExistsUserStatusException e) {
		return ResponseEntity.status(409).body(e.getMessage());
	}

	@ExceptionHandler(AlreadyExistsReadStatusException.class)
	public ResponseEntity<String> handleAlreadyExistsReadStatus(AlreadyExistsReadStatusException e) {
		return ResponseEntity.status(409).body(e.getMessage());
	}

	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<String> handleInvalidPassword(InvalidPasswordException e) {
		return ResponseEntity.status(401).body(e.getMessage());
	}

	@ExceptionHandler(UnauthorizedMessageAccessException.class)
	public ResponseEntity<String> handleUnauthorizedMessageAccess(UnauthorizedMessageAccessException e) {
		return ResponseEntity.status(401).body(e.getMessage());
	}

	@ExceptionHandler(NotChannelMemberException.class)
	public ResponseEntity<String> handleNotChannelMember(NotChannelMemberException e) {
		return ResponseEntity.status(403).body(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception e) {
		return ResponseEntity.status(500).body("서버 내부 오류");
	}
}
