package com.sprint.mission.discodeit.dto.response;

import jakarta.validation.constraints.Min;
import java.util.List;

public record Pageable(
    @Min(0) int page,
    @Min(1) int size,
    List<String> sort
) {

}
