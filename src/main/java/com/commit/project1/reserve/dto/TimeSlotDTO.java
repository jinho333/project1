package com.commit.project1.reserve.dto;

import lombok.Data;

@Data
public class TimeSlotDTO {
  private Long slotNo;         // 슬롯 번호
  private String slotStart;    // 시작 시간
  private String slotEnd;      // 종료 시간
  private String slotLabel;    // 화면 표시용 라벨
}
