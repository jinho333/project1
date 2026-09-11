package com.commit.project1.reserve.mapper;

import com.commit.project1.reserve.dto.ReserveDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReserveMapper {

  // 회원 ID로 예약 목록 조회 (최신순)
  List<ReserveDTO> selectReserveByMemId(String memId);

  // 예약번호로 상세조회
  ReserveDTO selectReserveByNo(Long reserveNo);

  // 예약 정보 저장
  int insertReserve(ReserveDTO dto);

  // 예약 상태 변경 ( 취소 / 완료 )
  int updateReserveStatus(ReserveDTO dto);

  // 특정 날짜에 예약된 시간 목록 조회
  List<String> selectReservedTimesByDate(String date);

  //모든 예약 정보 조회
  List<ReserveDTO> selectReserves();




}
