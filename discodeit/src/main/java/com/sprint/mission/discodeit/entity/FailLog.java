package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;



// 이걸로 보내는게 아니고 , notificaion을 통해서 내용에 Id 등 넣어서 알림을 보냄
// titile에 RequestId 가 들어가고 나머지 내용은 전부 content에 넣어서 보내면 해결.
@Entity
@Table(name = "fail_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class FailLog extends BaseEntity {
    String requstId;
    String binaryContentId;
    String error;

    public FailLog(String requestId, String BinaryContentId, String Error) {
        this.requstId = requestId;
        this.binaryContentId = BinaryContentId;
        this.error = Error;
    }

    public FailLog (String requestId, UUID  BinaryContentId, String Error) {
        this.requstId = requestId;
        this.binaryContentId = BinaryContentId.toString();
        this.error = Error;
    }



}
