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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReserveService {

  private final ReserveMapper reserveMapper;

  // 예약 등록
  public void saveReserve(ReserveDTO dto) {
    reserveMapper.insertReserve(dto);
  }

  // 예약 상태 변경 (취소/배정/완료 등)

  public int updateReserveStatus(ReserveDTO dto) {
    return reserveMapper.updateReserveStatus(dto);
  }

  // 예약 번호로 상세 조회
  public ReserveDTO getReserveByNo(Long reserveNo) {
    return reserveMapper.selectReserveByNo(reserveNo);
  }

  // 특정 날짜의 예약된 SLOT_NO 목록 조회 (취소 'C' 제외)
  public List<Long> getReservedSlotNosByDate(String date) {
    return reserveMapper.selectReservedSlotNosByDate(date);
  }

  // 회원 ID로 예약 목록 조회 (최신순)
  public List<ReserveDTO> getReservesByMemId(String memId) {
    return reserveMapper.selectReserveByMemId(memId);
  }

  //로그인한 회원 주소 조회
  public MemberDTO selectMember(String memId){
    return reserveMapper.selectMember(memId);
  }

  //모든 예약 정보 조회
  public List<ReserveDTO> selectReserves(){
    return reserveMapper.selectReserves();
  }


  //예약페이지 카테고리 조회
   public List<CategoryDTO> selectCategory(String productType){
    return reserveMapper.selectCategory(productType);
   }

  // 전체 시간 슬롯 조회
  public List<TimeSlotDTO> getAllTimeSlots() {
    return reserveMapper.selectAllTimeSlots();
  }


  // 예약 검증
  // 반환 규칙 - null → 검증 통과 (에러 없음)
  //          - 문자열 → 검증 실패 (그 문자열이 사용자에게 보여줄 에러 메시지)

  public String validateReserve(ReserveDTO dto) {

    // 1) 슬롯 번호가 실제로 존재 여부
    //   조작된 SLOT_NO(예: 9999)로 요청이 들어오는 것을 방지
    List<TimeSlotDTO> allSlots = reserveMapper.selectAllTimeSlots();

    // 슬롯을 찾았는지 저장할 변수. 처음엔 false(못 찾음)
    boolean slotExists = false;
    for (TimeSlotDTO slot : allSlots) {
      // 요청한 slotNo와 DB의 slotNo가 같으면 → 찾음
      if (slot.getSlotNo().equals(dto.getSlotNo())) {
        slotExists = true;
        break;   // 찾았으니 반복문 종료 (더 볼 필요 없음)
      }
    }

    // 반복문을 다 돌았는데도 못 찾았다면 → 유효하지 않은 슬롯
    if (!slotExists) {
      return "유효하지 않은 시간입니다.";
    }

    // 2) 같은 날짜 + 같은 슬롯에 예약 여부 why) 한 기사가 같은 시간에 두 집을 갈 수는 없으므로 같은 날짜+슬롯 조합은 1건만
    //   취소('C')된 예약은 제외되므로 재예약 가능

    // 날짜를 "yyyy-MM-dd" 문자열로 변환 (Mapper가 문자열로 받기 때문)
    String date = dto.getReserveDate().toString();

    // 그 날짜에 이미 잡힌 슬롯 번호들 (취소된 건 제외됨)
    List<Long> reservedSlots = reserveMapper.selectReservedSlotNosByDate(date);

    for (Long reservedSlotNo : reservedSlots) {
      // 이미 예약된 슬롯 번호와 같다면 → 중복 예약
      if (reservedSlotNo.equals(dto.getSlotNo())) {
        return "이미 예약된 시간입니다.";
      }
    }

    // 모든 검증을 통과 → null 반환 , 호출한 쪽에서 null을 받으면 성공 으로 해석
    return null;
  }

  public List<Map<String, Object>>countReserveByCate(){
    return reserveMapper.countReserveByCate();
  }

}