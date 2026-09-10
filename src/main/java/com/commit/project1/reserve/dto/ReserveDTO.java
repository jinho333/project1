package com.commit.project1.reserve.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ReserveDTO {

  private Long reserveNo;              // 예약 번호 (PK, 자동 증가)
  private String modelName;            // 제품명
  private String memId;                // 회원 ID (MEMBER 테이블 참조)
  private Long technicianId;           // 기사 ID 현재 TECHNICIAN(기사) 테이블이 없어서 임의의 숫자를 저장 중
  private Long categoryNo;             // 카테고리 번호 (CATEGORY 테이블 참조)
  private String symptom;              // 고장 증상
  private String reserveStatus;        // 예약 상태 ('0','1','2','3','C')
  private String visitAddr;            // 방문 주소
  private String visitAddrDetail;      // 상세 방문 주소


  // @DateTimeFormat: 프론트(JS)에서 넘어오는 문자열을 LocalDateTime으로 변환
  // 형식: "yyyy-MM-dd HH:mm:ss" (예: 2026-09-10 10:00:00)
  // JS가 보내는 형식과 반드시 일치해야 함 (불일치 시 에러 발생)
  // 관련 파일: reserve_time.js, reserve_time.html
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime reserveDate;   // 희망 방문 일시 (LocalDateTime)
  private LocalDateTime registDate;    // 등록 일시 (DB 자동 입력, 조회용)



}
