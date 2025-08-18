package com.sprint.mission.discodeit.controller.binarycontent;

import static org.springframework.http.ContentDisposition.parse;

import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.binarycontent.BinaryContentService;
import com.sprint.mission.discodeit.support.FileNames;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/binary-contents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  private static final List<String> ACCEPTS = List.of(
      MediaType.APPLICATION_OCTET_STREAM_VALUE,
      MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE
  );

  @PostMapping(path = {"", "/"}, consumes = {
      MediaType.APPLICATION_OCTET_STREAM_VALUE,
      MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE})
  @ResponseStatus(HttpStatus.CREATED)
  public BinaryContentResponse upload(
      @RequestHeader(value = "Content-Type", required = false) String contentType,
      @RequestHeader(value = "Content-Disposition", required = false) String contentDisposition,
      @RequestBody byte[] body
  ) {
    if (body == null || body.length == 0) {
      throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
    }

    String ct = normalizeContentType(contentType);
    String original = parseFilename(contentDisposition);

    String fileName;
    if (original == null) {
      fileName = FileNames.randomWithExtension(null, ct);
    } else {
      String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

      String ext = original.contains(".")
          ? original.substring(original.lastIndexOf("."))
          : "";

      String nameWithoutExt = original.contains(".")
          ? original.substring(0, original.lastIndexOf("."))
          : original;

      fileName = nameWithoutExt + "_" + random + ext;
    }

    return binaryContentService.create(
        new BinaryContentCreateRequest(fileName, ct, body)
    );
  }

  @GetMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  public BinaryContentResponse find(@PathVariable("id") UUID id) {
    return binaryContentService.findById(id);
  }

  @GetMapping({"", "/"})
  @ResponseStatus(HttpStatus.OK)
  public List<BinaryContentResponse> findAll() {
    return binaryContentService.findAll();
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") UUID id) {
    if (!binaryContentService.delete(id)) {
      throw new NotFoundException("바이너리 컨텐츠를 찾을 수 없습니다: " + id);
    }
  }

  private static String normalizeContentType(String ct) {
    String value = (ct == null || ct.isBlank()) ? MediaType.APPLICATION_OCTET_STREAM_VALUE : ct;
    return ACCEPTS.contains(value) ? value : MediaType.APPLICATION_OCTET_STREAM_VALUE;
  }

  private static String parseFilename(String contentDisposition) {
    if (contentDisposition == null || contentDisposition.isBlank()) {
      return null;
    }
    try {
      return parse(contentDisposition).getFilename();
    } catch (Exception ignore) {
      return null;
    }
  }
}