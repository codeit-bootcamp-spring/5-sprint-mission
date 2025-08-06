package com.sprint.mission.discodeit.entity;



//사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
//        [ ] 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요.
//          마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

//@Data
@Getter
@Setter
@ToString
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;

    private Instant loginCheck;
    private boolean login;

    public UserStatus(){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.loginCheck = Instant.now();
        this.login = true;
    }

    public UserStatus(UUID userId){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.loginCheck = Instant.now();
        this.login = true;
    }
    public UserStatus(User user){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = user.getId();
        this.loginCheck = Instant.now();
        this.login = true;
    }


    public Instant loginCheck(Instant now){
        Duration duration = Duration.between(loginCheck, now);
        if (duration.compareTo(Duration.ofMinutes(5)) < 0) {
            Instant time=this.loginCheck;
            this.loginCheck=Instant.now();
            System.out.println("현재 접속 중입니다.");
            this.login=true;
            return time;
        } else {
            this.login=false;
            System.out.println("현재 접속 중이 아닙니다.");
            return this.loginCheck;
        }
    }




}
