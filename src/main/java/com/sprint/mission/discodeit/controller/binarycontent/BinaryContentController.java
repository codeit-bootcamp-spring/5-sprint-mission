package com.sprint.mission.discodeit.controller.binarycontent;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.service.binarycontent.BinaryContentService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/binaryContents")
@Validated
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<BinaryContentDto> findAllByIn(

      @RequestParam("binaryContentIds")
      @Size(min = 1)
      List<@NotNull UUID> binaryContentIds
  ) {

    return binaryContentService.findAllByIn(binaryContentIds);
  }

  @GetMapping(path = "/{binaryContentId}")
  @ResponseStatus(HttpStatus.OK)
  public BinaryContentDto find(

      @PathVariable("binaryContentId")
      UUID binaryContentId
  ) {

    return binaryContentService.find(binaryContentId);
  }
}