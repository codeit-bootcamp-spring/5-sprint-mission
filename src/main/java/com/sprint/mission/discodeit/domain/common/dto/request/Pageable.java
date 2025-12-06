package com.sprint.mission.discodeit.domain.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

@Schema(
    example = """
        {
          "page": 0,
          "size": 50,
          "sort": ["createdAt", "desc"]
        }
        """
)
public record Pageable(
    @Min(0) Integer page,
    @Min(1) Integer size,
    List<String> sort
) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 50;
    public static final List<String> DEFAULT_SORT = List.of("createdAt", "desc");

    public Pageable {
        if (page == null) {
            page = DEFAULT_PAGE;
        }
        if (size == null) {
            size = DEFAULT_SIZE;
        }
        if (sort == null || sort.size() < 2) {
            sort = DEFAULT_SORT;
        }
    }

    public PageRequest toPageRequest() {
        String property = sort.get(0);
        Direction direction = Direction.fromOptionalString(sort.get(1))
            .orElse(Direction.DESC);

        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}
