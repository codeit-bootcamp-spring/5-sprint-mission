package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.controller.api.BinaryContentApi;
import com.codeit.mission.discodeit.dto.data.BinaryContentDto;
import com.codeit.mission.discodeit.service.BinaryContentService;
import com.codeit.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping(path = "{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable("binaryContentId") UUID binaryContentId) {
        log.debug("파일 정보 조회 API 호출 - binaryContentId: {}", binaryContentId);

        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
        log.debug("파일 정보 조회 API 성공 - binaryContentId: {}, fileName: {}, size: {} bytes",
                binaryContentId, binaryContent.fileName(), binaryContent.size());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContent);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        log.debug("파일 정보 일괄 조회 API 호출 - 파일 수: {}", binaryContentIds.size());
        log.debug("조회 대상 파일 ID 목록 - binaryContentIds: {}", binaryContentIds);

        List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(
                binaryContentIds);
        log.info("파일 정보 일괄 조회 API 성공 - 요청 파일 수: {}, 조회된 파일 수: {}",
                binaryContentIds.size(), binaryContents.size());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }

    @GetMapping(path = "{binaryContentId}/download")
    public ResponseEntity<?> download(
            @PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
        return binaryContentStorage.download(binaryContentDto);
    }
}
