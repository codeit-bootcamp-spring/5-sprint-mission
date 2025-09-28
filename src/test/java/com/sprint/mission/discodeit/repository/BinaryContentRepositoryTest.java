// src/test/java/com/sprint/mission/discodeit/repository/BinaryContentRepositoryTest.java
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

class BinaryContentRepositoryTest extends RepositorySliceTestBase {

  @Autowired BinaryContentRepository binaryContentRepository;

  @Test
  void save_and_findById() {
    var b = binaryContentRepository.save(new BinaryContent("a.txt", 5L, "text/plain"));

    var found = binaryContentRepository.findById(b.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getFileName()).isEqualTo("a.txt");
  }
}
