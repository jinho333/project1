package com.commit.project1.reserve.service;

import com.commit.project1.member.dto.MemberDTO;
import com.commit.project1.member.mapper.MemberMapper;
import com.commit.project1.reserve.dto.CategoryDTO;
import com.commit.project1.reserve.dto.ReserveDTO;
import com.commit.project1.reserve.dto.TimeSlotDTO;
import com.commit.project1.reserve.mapper.ReserveMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReserveService {

  private final ReserveMapper reserveMapper;

  // @Transactional: 여러 DB 작업을 하나의 트랜잭션으로 묶음
  // 모든 작업이 성공하면 COMMIT, 하나라도 실패하면 ROLLBACK
  // INSERT/UPDATE/DELETE 같은 데이터를 변경하는 메서드에 사용

  // 예약 등록
  @Transactional
  public void saveReserve(ReserveDTO dto) {
    reserveMapper.insertReserve(dto);
  }

  // 회원 ID로 예약 목록 조회 (최신순)
  public List<ReserveDTO> getReservesByMemId(String memId) {
    return reserveMapper.selectReserveByMemId(memId);
  }

  // 예약 번호로 상세 조회
  public ReserveDTO getReserveByNo(Long reserveNo) {
    return reserveMapper.selectReserveByNo(reserveNo);
  }

  // 예약 상태 변경 (취소/완료)
  @Transactional
  public int updateReserveStatus(ReserveDTO dto) {
    return reserveMapper.updateReserveStatus(dto);
  }
  public List<TimeSlotDTO> getAllTimeSlots() {
    return reserveMapper.selectAllTimeSlots();
  }

  public List<Long> getReservedSlotNosByDate(String date) {
    return reserveMapper.selectReservedSlotNosByDate(date);
  }

  //로그인한 회원 주소 조회
  public MemberDTO selectMember(String memId){
    return reserveMapper.selectMember(memId);
  }
  //모든 예약 정보 조회
  public List<ReserveDTO> selectReserves(){
    return reserveMapper.selectReserves();
  }



}