package com.sprint.mission.discodeit.controller.restController;

import com.sprint.mission.discodeit.dto.request.AddBinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @RequestMapping(method = RequestMethod.POST)
  public BinaryContent addBinaryContent(
      @RequestBody AddBinaryContentDto binaryContent
  ) {
    return binaryContentService.addBinaryContent(binaryContent);
  }


  @RequestMapping(path = "find/{binaryContentId}", method = RequestMethod.GET)
  public ResponseEntity<BinaryContent> getBinaryContentById(
      @PathVariable UUID binaryContentId
  ) {
    return ResponseEntity.ok(binaryContentService.getBinaryContentById(binaryContentId));
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<BinaryContent> getBinaryContent() {
    return binaryContentService.getAllBinaryContent();
  }
}
