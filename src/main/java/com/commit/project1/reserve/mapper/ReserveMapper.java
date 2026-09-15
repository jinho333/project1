package com.commit.project1.reserve.mapper;

import com.commit.project1.member.dto.MemberDTO;
import com.commit.project1.reserve.dto.CategoryDTO;
import com.commit.project1.reserve.dto.ReserveDTO;
import com.commit.project1.reserve.dto.TimeSlotDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReserveMapper {

  // 전체 슬롯 목록 조회 (TIME_SLOT 테이블)
  List<TimeSlotDTO> selectAllTimeSlots();

  // 예약번호로 상세조회
  ReserveDTO selectReserveByNo(Long reserveNo);

  // 특정 날짜에 이미 예약된 SLOT_NO 목록 조회
  List<Long> selectReservedSlotNosByDate(String date);

  // 회원 ID로 예약 목록 조회
  List<ReserveDTO> selectReserveByMemId(String memId);

  // 예약 저장
  void insertReserve(ReserveDTO dto);

  //로그인한 회원의 주소를 조회
  MemberDTO selectMember(String memId);

  //모든 예약 정보 조회
  List<ReserveDTO> selectReserves();


  //예약 페이지 카테고리 조회
  List<CategoryDTO> selectCategory(String productType);

  // 카테고리 번호로 카테고리 단건 조회 (예약 저장 시 카테고리명을 모델명 자리에 채우기 위함)
  CategoryDTO selectCategoryByNo(Long categoryNo);

  // 예약 상태 변경 (취소/배정/완료 등)
  int updateReserveStatus(ReserveDTO dto);





}
