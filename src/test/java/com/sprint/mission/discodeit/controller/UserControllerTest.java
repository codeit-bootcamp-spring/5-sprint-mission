package com.sprint.mission.discodeit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/* @WebMvcTest를 활용해 테스트를 구현
 * 서비스레이어를 모의(mock)하여 컨트롤러 로직만 테스트
 * JSON 응답을 검증하는 테스트를 포함
 */
@WebMvcTest(UserController.class) //UserController만 슬라이스로 올림
public class UserControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper; // Jackson, multipart json 변환에 필요

  @MockBean //진짜 테스트할 객체
  UserService userService;

  @Test
    /* 유저 전체 조회 성공
     * GET /api/users
     */
  void findAll_success() throws Exception {

    // given
    UserDto dto = new UserDto();
    dto.setId(UUID.randomUUID());
    dto.setUsername("testuser");
    dto.setEmail("test@email.com");

    BDDMockito.given(userService.findAll()).willReturn(List.of(dto));

    // when & then
    mockMvc.perform(get("/api/users") // 요청 보냄
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) //응답 결과 200 OK인지
        .andExpect(jsonPath("$[0].username").value("testuser")); // 첫번째 유저 검증
  }

  @Test
    /* 유저 전체 조회 실패 - 데이터 없음
     * GET /api/users
     */
  void findAll_fail_empty() throws Exception {
    // given
    BDDMockito.given(userService.findAll()).willReturn(Collections.emptyList()); //유저 없음 상황 세팅

    // when & then
    mockMvc.perform(get("/api/users") //요청 보냄
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) //응답 200 OK인지
        .andExpect(jsonPath("$").isArray()) // JSON 배열인지
        .andExpect(jsonPath("$.length()").value(0)); // 아무 유저도 없는지 검증
  }

}
