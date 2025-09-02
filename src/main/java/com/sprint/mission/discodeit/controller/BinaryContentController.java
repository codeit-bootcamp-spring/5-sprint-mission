package com.sprint.mission.discodeit.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.response.binaryContent.Base64BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @RequestMapping(path = "/find", method = RequestMethod.GET)
  public ResponseEntity<Base64BinaryContentResponse> findBinaryContentForList(
      @RequestParam UUID binaryContentId) throws IOException {
    BinaryContentResponse response = binaryContentService.getById(binaryContentId);
    Base64BinaryContentResponse base64Response = Base64BinaryContentResponse.fromResponse(response);
    return ResponseEntity.ok(base64Response);
  }

  @RequestMapping(path = "/findContents", method = RequestMethod.GET)
  public ResponseEntity<List<Base64BinaryContentResponse>> findBinaryContentsForList(
      @RequestParam List<UUID> binaryContentIds) throws IOException {
    List<BinaryContentResponse> responses = binaryContentService.getAllByIdIn(binaryContentIds);

    List<Base64BinaryContentResponse> base64Responses = responses.stream()
        .map(Base64BinaryContentResponse::fromResponse)
        .toList();

    return ResponseEntity.ok(base64Responses);
  }

  @RequestMapping(path = "/{binaryContentId}", method = RequestMethod.GET)
  public ResponseEntity<Base64BinaryContentResponse> findBinaryContent(
      @PathVariable UUID binaryContentId) throws IOException {
    BinaryContentResponse response = binaryContentService.getById(binaryContentId);
    Base64BinaryContentResponse base64Response = Base64BinaryContentResponse.fromResponse(response);
    return ResponseEntity.ok(base64Response);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Base64BinaryContentResponse>> findBinaryContents(
      @RequestParam List<UUID> binaryContentIds) throws IOException {
    List<BinaryContentResponse> responses = binaryContentService.getAllByIdIn(binaryContentIds);

    List<Base64BinaryContentResponse> base64Responses = responses.stream()
        .map(Base64BinaryContentResponse::fromResponse)
        .toList();

    return ResponseEntity.ok(base64Responses);
  }

    @RequestMapping(path = "/{binaryContentId}/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@PathVariable UUID binaryContentId) throws IOException {
        BinaryContentDTO dto = binaryContentService.download(binaryContentId);
        return binaryContentStorage.download(dto);
    }
}