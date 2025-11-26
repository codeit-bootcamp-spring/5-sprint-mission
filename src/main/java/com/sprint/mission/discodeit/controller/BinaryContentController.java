package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.docs.BinaryContentControllerDocs;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.FileDownloadResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.util.StringUtil.sanitizeFilename;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentControllerDocs {

    private final BinaryContentService binaryContentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BinaryContentDto> getAllBinaryContents(@RequestParam Collection<UUID> binaryContentIds) {
        return binaryContentService.findAllByIdIn(binaryContentIds);
    }

    @GetMapping("/{binaryContentId}")
    @ResponseStatus(HttpStatus.OK)
    public BinaryContentDto getBinaryContent(@PathVariable UUID binaryContentId) {
        return binaryContentService.getBinaryContent(binaryContentId);
    }

    @GetMapping(
        path = "/{binaryContentId}/download",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<Resource> download(@PathVariable UUID binaryContentId) {
        FileDownloadResponse fileDownloadResponse = binaryContentService.download(binaryContentId);

        MediaType mediaType;
        try {
            mediaType = (fileDownloadResponse.contentType() != null && !fileDownloadResponse.contentType().isBlank())
                ? MediaType.parseMediaType(fileDownloadResponse.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        } catch (InvalidMediaTypeException e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        ContentDisposition contentDisposition = ContentDisposition.attachment()
            .filename(sanitizeFilename(fileDownloadResponse.fileName()), StandardCharsets.UTF_8)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(mediaType)
            .contentLength(fileDownloadResponse.size())
            .body(fileDownloadResponse.resource());
    }
}
