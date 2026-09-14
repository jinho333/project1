package com.commit.project1.reserve.controller;

import com.commit.project1.member.dto.MemberDTO;
import com.commit.project1.reserve.dto.ReserveDTO;
import com.commit.project1.reserve.dto.TimeSlotDTO;
import com.commit.project1.reserve.service.ReserveService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reserve")
@RequiredArgsConstructor
public class ReserveController {

  private final ReserveService reserveService;
  @Value("${file.upload.dir}")
  private String uploadPath;  //첨부파일 업로드 경로 담을 문자열 변수

  // 폼 페이지
  @GetMapping("/form")
  public String reserveForm(HttpSession session, Model model){
    //로그인한 회원 주소
   MemberDTO member = (MemberDTO) session.getAttribute("loginInfo");
   if (member != null){
     String memId = member.getMemId();
     model.addAttribute("member", reserveService.selectMember(memId));
   }


    return "pages/reserve/reserve_form";
  }

  @GetMapping("/time")
  public String timePage(ReserveDTO reserveDTO ) {

    return "pages/reserve/reserve_time";
  }



  //  특정 날짜의 예약 가능 시간을 JSON으로 반환하는 API
  @PostMapping("/form-submit")
  public String reserveTime(ReserveDTO reserveDTO) {

    System.out.println(reserveDTO);

    return "pages/reserve/reserve_time";
  }


  // 시간 슬롯 조회 API (비동기)
  @GetMapping("/available-times")
  @ResponseBody
  public Map<String, Object> availableTimes(@RequestParam("date") String date) {

    // 1) 전체 슬롯 조회 (TIME_SLOT 테이블)
    //    화면에 4개 슬롯(10-12, 13-15, 15-17, 17-19)을 그리기 위함
    List<TimeSlotDTO> slots = reserveService.getAllTimeSlots();

    // 2) 이 날짜의 예약된 슬롯 번호 조회 (취소 'C' 제외)
    //    → 예약된 슬롯은 화면에서 '마감' 표시
    List<Long> reservedSlots = reserveService.getReservedSlotNosByDate(date);

    // 3) 두 리스트를 Map으로 묶어 리턴
    //    - 응답 필드 2개뿐이라 DTO 대신 Map 사용
    //    - 키 이름이 JSON 키가 되므로 JS(data.slots, data.reservedSlots)와 일치해야 함

    // 두 리스트(slots, reservedSlots)를 하나의 응답으로 묶기
    // Map은 "키-값" , JS의 오브젝트 ({key: value})와 비슷한 구조

    //   - new HashMap<>(): 빈 Map 생성
    //   - <String, Object>: 키는 String, 값은 아무 타입(Object) 가능
    Map<String, Object> result = new HashMap<>();

    // 키 "slots" 에 전체 슬롯 목록을 저장
    // → JSON 응답에서 "slots" 키로 나감
    result.put("slots", slots);

    // 키 "reservedSlots" 에 예약된 슬롯 번호 목록을 저장
    // → JSON 응답에서 "reservedSlots" 키로 나감
    result.put("reservedSlots", reservedSlots);

    // 이 Map을 리턴하면 @ResponseBody가 자동으로 JSON으로 변환해 응답
    return result;
  }

  // 예약 확정 처리
  @PostMapping("/complete")
  @ResponseBody
  public Map<String, Object> completeReserve(ReserveDTO dto, HttpSession session) {

    Map<String, Object> result = new HashMap<>();

    // 1) 로그인 체크
    // 로그인 안 된 상태로 URL 직접 접근 시 로그인 페이지로 보냄
    // (JS가 redirect 값을 받아 location.href 로 이동)
    MemberDTO login = (MemberDTO) session.getAttribute("loginInfo");
    if (login == null) {
      result.put("success", false);
      result.put("redirect", "/member/login-form");
      return result;
    }

    // 2) 필수값 검증
    // JS가 이미 검증하지만 개발자도구 조작 대비 서버도 검증
    if (dto.getReserveDate() == null || dto.getSlotNo() == null) {
      result.put("success", false);
      result.put("error", "날짜와 시간을 선택해주세요.");
      return result;
    }

    // 3) 비즈니스 검증 (서비스에 위임)
    // validateReserve 반환 규칙:
    //   null    → 통과 , 문자열   → 실패 (문자열이 에러 메시지)
    String error = reserveService.validateReserve(dto);
    if (error != null) {
      result.put("success", false);
      result.put("error", error);
      return result;
    }

    // 4) 저장
    // memId / reserveStatus 는 서버가 강제 (사용자 조작 방지)
    dto.setMemId(login.getMemId());
    dto.setReserveStatus("0");       // '0' = 예약접수
    reserveService.saveReserve(dto);

    // 5) 성공 응답
    // JS가 이 redirect 값을 받아 완료 페이지로 이동.
    result.put("success", true);
    result.put("redirect", "/reserve/complete-page");
    return result;
  }

  // completeReserve 가 성공하면 JS가 이 URL로 이동
  // GET이 없으면 완료 화면 이동 시 404가 뜸
  // (redirect 값이 "/reserve/complete-page")
  @GetMapping("/complete-page")
  public String completePage() {
    return "pages/reserve/reserve_complete";
  }

  // [API] 예약 취소 (AJAX)
  //   성공        : { "success": true,  "message": "예약이 취소되었습니다." }
  //   로그인 실패 : { "success": false, "redirect": "/member/login-form" }
  //   검증 실패   : { "success": false, "error": "취소할 수 없는 예약입니다." }
  @PostMapping("/cancel")
  @ResponseBody
  public Map<String, Object> cancelReserve(@RequestParam("reserveNo") Long reserveNo,
                                           HttpSession session) {

    Map<String, Object> result = new HashMap<>();

    // 1) 로그인 체크
    MemberDTO login = (MemberDTO) session.getAttribute("loginInfo");
    if (login == null) {
      result.put("success", false);
      result.put("redirect", "/member/login-form");
      return result;
    }

    // 2) 예약 조회 (존재 여부 + 본인 확인용)
    ReserveDTO reserve = reserveService.getReserveByNo(reserveNo);
    if (reserve == null) {
      result.put("success", false);
      result.put("error", "예약을 찾을 수 없습니다.");
      return result;
    }

    // 3) 본인 예약인지 확인
    if (!login.getMemId().equals(reserve.getMemId())) {
      result.put("success", false);
      result.put("error", "본인의 예약만 취소할 수 있습니다.");
      return result;
    }

    // 4) 취소 가능한 상태인지 확인 ('0'/'1'만 취소 가능)
    String status = reserve.getReserveStatus();
    if (!"0".equals(status) && !"1".equals(status)) {
      result.put("success", false);
      result.put("error", "현재 상태에서는 취소할 수 없습니다.");
      return result;
    }

    // 5) 상태를 'C'(취소)로 변경
    ReserveDTO dto = new ReserveDTO();
    dto.setReserveNo(reserveNo);
    dto.setReserveStatus("C");
    reserveService.updateReserveStatus(dto);

    // 6) 성공
    result.put("success", true);
    result.put("message", "예약이 취소되었습니다.");
    return result;
  }





  @GetMapping("/list")
  public String reserveList(HttpSession session, Model model) {

    // 1) 로그인 체크
    MemberDTO login = (MemberDTO) session.getAttribute("loginInfo");
    if (login == null) {
      return "redirect:/member/login-form";
    }

    // 2) 본인 예약만 조회 (최신순 정렬은 Mapper의 ORDER BY)
    String memId = login.getMemId();
    List<ReserveDTO> reserves = reserveService.getReservesByMemId(memId);

    // 3) 화면에 전달 → reserve_list.html의 ${reserves}
    model.addAttribute("reserves", reserves);

    return "pages/reserve/reserve_list";
  }


}

