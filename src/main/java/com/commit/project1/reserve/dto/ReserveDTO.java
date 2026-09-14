package com.commit.project1.reserve.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReserveDTO {

  private Long reserveNo;              // 예약 번호 (PK, 자동 증가)
  private String modelName;            // 제품명
  private String memId;                // 회원 ID (MEMBER 테이블 참조)
  private Long technicianId;           // 기사 ID 현재 TECHNICIAN(기사) 테이블이 없어서 임의의 숫자를 저장 중
  private Long categoryNo;             // 카테고리 번호 (CATEGORY 테이블 참조)
  private String symptom;              // 고장 증상
  private String reserveStatus;        // 예약 상태 ('0','1','2','C')
  private String visitAddr;            // 방문 주소
  private String visitAddrDetail;      // 상세 방문 주소

  private LocalDate reserveDate;    // 방문 일시
  private LocalDateTime registDate; // 등록 일시 (DB 자동 입력)

  // 타임 슬롯
  private Long slotNo;         // 슬롯 번호
  private String slotStart;    // 시작 시간
  private String slotEnd;      // 종료 시간
  private String slotLabel;    // 화면 표시용 라벨

  //
  private String categoryName;   // 카테고리명 (예: '에어컨')
  private String productType;    // 제품 유형 ('냉방' / '난방')


}
