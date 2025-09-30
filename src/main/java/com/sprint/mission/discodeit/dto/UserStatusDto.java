package com.sprint.mission.discodeit.dto;


import java.time.Instant;
import lombok.Data;

@Data
public class UserStatusDto {

  private Instant newLastActiveAt; //마지막 접속 시간

}
