package com.sprint.mission.discodeit.it;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserApiIntegrationTest extends BaseIntegrationTest {

  @Autowired MockMvc mvc;

  @Test
  void create_success_withoutProfile_and_list() throws Exception {
    var req = new UserCreateRequest("neo","neo@matrix.io","secret123");
    var json = om.writeValueAsString(req);
    var userPart = new MockMultipartFile(
        "userCreateRequest","user.json", MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8)
    );

    mvc.perform(multipart("/api/users").file(userPart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("neo"))
        .andExpect(jsonPath("$.email").value("neo@matrix.io"));

    mvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("neo"));
  }

  @Test
  void create_conflict_when_sameEmail() throws Exception {
    var req = new UserCreateRequest("neo","dup@mail.io","secret123");
    var json = om.writeValueAsString(req);
    var part = new MockMultipartFile("userCreateRequest","user.json", MediaType.APPLICATION_JSON_VALUE, json.getBytes());

    mvc.perform(multipart("/api/users").file(part))
        .andExpect(status().isCreated());

    mvc.perform(multipart("/api/users").file(part))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"));
  }

  @Test
  void update_success_withProfile_and_delete() throws Exception {
    var create = new UserCreateRequest("trinity","tri@matrix.io","pw");
    var cjson = om.writeValueAsString(create);
    var cpart = new MockMultipartFile("userCreateRequest","user.json", MediaType.APPLICATION_JSON_VALUE, cjson.getBytes());
    var created = mvc.perform(multipart("/api/users").file(cpart))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    var id = om.readTree(created).get("id").asText();

    var upd = new UserUpdateRequest("trinity2","tri2@matrix.io","pw2");
    var ujson = om.writeValueAsString(upd);
    var upart = new MockMultipartFile("userUpdateRequest","update.json", MediaType.APPLICATION_JSON_VALUE, ujson.getBytes());
    var profile = new MockMultipartFile("profile","p.png","image/png","IMG".getBytes());

    mvc.perform(multipart("/api/users/{id}", UUID.fromString(id))
            .file(upart).file(profile).with(r -> { r.setMethod("PATCH"); return r; }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("trinity2"));

    mvc.perform(delete("/api/users/{id}", UUID.fromString(id)))
        .andExpect(status().isNoContent());
  }
}
