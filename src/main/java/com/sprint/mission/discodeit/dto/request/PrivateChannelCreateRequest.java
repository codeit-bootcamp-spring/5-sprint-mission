package com.sprint.mission.discodeit.dto.request;

// PRIVATE 채널을 생성할 때:
// 채널에 참여하는 User의 정보를 받아 User 별 ReadStatus 정보를 생성합니다.
// name과 description 속성은 생략합니다.

import com.sprint.mission.discodeit.entity.User;
import java.util.*;

public record PrivateChannelCreateRequest(
       List<User> user
) {
}
