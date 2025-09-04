package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.Value;

/* Data에서 Value로 변경
 * 한번 값을 넣어서 보내는 용도이니
 * 나중에 수정할 필요가 없어서 불변으로 변경
 * 모든 필드 final로 됨 */

@Value
public class BinaryContentCreateRequest {

  private byte[] data;
  private String fileName;
  private long size;
  private String contentType;

}
