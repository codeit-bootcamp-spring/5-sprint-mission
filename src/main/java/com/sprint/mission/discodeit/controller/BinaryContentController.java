package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage storage;

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> download(@PathVariable("id") UUID id) {
    var meta = binaryContentRepository.findById(id)
        .orElseThrow(() ->
            new ResponseStatusException(NOT_FOUND, "BinaryContent with id " + id + " not found"));

    try {
      return storage.download(binaryContentMapper.toDto(meta));
    } catch (IOException e) {
      throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to read binary content", e);
    }
  }
}
