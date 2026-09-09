package com.commit.project1.reserve.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReserveDTO {

  private Long reserveNo;              // 예약 번호 (PK, 자동 증가)
  private String modelName;            // 제품명
  private String memId;                // 회원 ID (MEMBER 테이블 참조)
  private Long technicianId;           // 기사 ID (추후 확장, null 허용)
  private Long categoryNo;             // 카테고리 번호 (CATEGORY 테이블 참조)
  private LocalDateTime reserveDate;   // 희망 방문 일시 (LocalDateTime)
  private LocalDateTime registDate;    // 등록 일시 (DB 자동 입력, 조회용)
  private String symptom;              // 고장 증상
  private String reserveStatus;        // 예약 상태 ('0','1','2','3','C')
  private String visitAddr;            // 방문 주소
  private String visitAddrDetail;      // 상세 방문 주소
}
