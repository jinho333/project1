package com.commit.project1.reserve.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 예약(RESERVE) DTO
// TimeSlotDTO, CategoryDTO를 필드로 중첩하지 않고,
// 화면에 필요한 값을 전부 이 클래스 하나에 담음.
// MyBatis resultMap은 JOIN 결과를 <result property="필드" column="컬럼"/> 식으로 1:1로 이어붙이는 게 제일 단순하고,
// 중첩 객체로 받으려면 <association> 같은 더 복잡한 매핑 문법이 필요해서 지금 규모(테이블 3~4개 조인)엔 과하다고 판단.
// 단점: "예약" DTO인데 조회 전용 표시값(카테고리명, 슬롯 라벨)까지 같이 들고 있어서 필드 책임이 다소 섞여 있음
// (예: INSERT 때는 slotLabel/categoryName이 안 쓰이는데도 같은 클래스에 계속 붙어 있음).

@Data
public class ReserveDTO {

  private Long reserveNo;              // 예약 번호 (PK, 자동 증가)
  private String modelName;            // 제품명
  private String memId;                // 회원 ID (MEMBER 테이블 참조)
  private Long technicianId;           // 기사 ID 현재 TECHNICIAN(기사) 테이블이 없어서 임의의 숫자를 저장 중
  private Long categoryNo;             // 카테고리 번호 (CATEGORY 테이블 참조)
  private String symptom;              // 고장 증상
  // 예약 상태 - '0' 접수, '1' 배정, '9' 완료, 'C' 취소
  // 완료를 '2'가 아니라 '9'로 둔 이유: 접수/배정 사이에 나중에 중간 단계(기사 출발, 현장 도착 등)가
  // 늘어날 걸 대비해 진행 상태는 앞에서부터 채우고, 완료는 끝 번호(9)에 고정해서 안 밀리게 함.
  // 취소는 숫자 진행 흐름과 다른 별개의 결과라 문자(C)로 구분함.
  private String reserveStatus;
  private String visitAddr;            // 방문 주소
  private String visitAddrDetail;      // 상세 방문 주소

  private LocalDate reserveDate;    // 방문 일시
  private LocalDateTime registDate; // 등록 일시 (DB 자동 입력)

  // 타임 슬롯
  private Long slotNo;         // 슬롯 번호
  private String slotStart;    // 시작 시간
  private String slotEnd;      // 종료 시간
  private String slotLabel;    // 화면 표시용 라벨

  // 카테고리
  private String categoryName;   // 카테고리명 (예: '에어컨')
  private String productType;    // 제품 유형 ('냉방' / '난방')


}
