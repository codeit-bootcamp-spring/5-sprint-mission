package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCreateRequest {
	private String username;
	private String password;
	private String defaultNickname;
	private String email;
	@Nullable
	private UserProfileImageRequest profileImage;

	public User toUser() {
		return toUserWithProfile(null);
	}

    public User toUserWithProfile(BinaryContent profile) {
        String nickname = (defaultNickname != null) ? defaultNickname : username;
        return new User(username, password, nickname, email, profile);
    }

}
