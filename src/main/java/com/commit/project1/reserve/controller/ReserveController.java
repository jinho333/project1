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
  public String reserveForm(HttpSession session, Model model) {

    //로그인한 회원 주소 (세션에서 아이디를 찾자!)
    MemberDTO member = (MemberDTO) session.getAttribute("loginInfo");
    if (member != null) {
      String memId = member.getMemId();
      model.addAttribute("member", reserveService.selectMember(memId));
    }

    return "pages/reserve/reserve_form";
  }

  @GetMapping("/time")
  public String reserveTime(ReserveDTO reserveDTO, Model model) {

    return "pages/reserve/reserve_time";
  }

  @PostMapping("/form-submit")
  public String reserveTime(ReserveDTO reserveDTO) {

    System.out.println(reserveDTO);

    return "pages/reserve/reserve_time";
  }

  // 시간 슬롯 조회 API (비동기)
  @GetMapping("/available-times")
  @ResponseBody
  public Map<String, Object> availableTimes(@RequestParam("date") String date) {

    // 1) TIME_SLOT 테이블에서 전체 슬롯 목록 조회 (1~4번 슬롯)
    List<TimeSlotDTO> slots = reserveService.getAllTimeSlots();

    // 2) 해당 날짜에 이미 예약된 SLOT_NO 목록 조회 (취소 'C'는 제외됨)
    List<Long> reservedSlots = reserveService.getReservedSlotNosByDate(date);

    Map<String, Object> result = new HashMap<>();
    result.put("slots", slots);                 // 전체 슬롯 목록
    result.put("reservedSlots", reservedSlots); // 예약된 슬롯 번호 목록
    return result;
  }

  // 예약 확정 처리
  @PostMapping("/complete")
  public String completeReserve(ReserveDTO dto,
                                HttpSession session,
                                Model model) {

    // 로그인 체크
    MemberDTO login = (MemberDTO) session.getAttribute("loginInfo");
    if (login == null) {
      return "redirect:/member/login-form";
    }

    // 날짜/슬롯 필수값 검증
    if (dto.getReserveDate() == null || dto.getSlotNo() == null) {
      model.addAttribute("error", "날짜와 시간을 선택해주세요.");
      // 사용자가 1단계에서 입력한 값 유지
      model.addAttribute("modelName", dto.getModelName());
      model.addAttribute("symptom", dto.getSymptom());
      model.addAttribute("categoryNo", dto.getCategoryNo());
      model.addAttribute("visitAddr", dto.getVisitAddr());
      model.addAttribute("visitAddrDetail", dto.getVisitAddrDetail());
      return "pages/reserve/reserve_time";
    }

    // 슬롯 유효성 검증
    List<TimeSlotDTO> slots = reserveService.getAllTimeSlots();
    boolean validSlot = slots.stream()
            .anyMatch(s -> s.getSlotNo().equals(dto.getSlotNo()));
    if (!validSlot) {
      model.addAttribute("error", "유효하지 않은 시간입니다.");
      model.addAttribute("modelName", dto.getModelName());
      model.addAttribute("symptom", dto.getSymptom());
      model.addAttribute("categoryNo", dto.getCategoryNo());
      model.addAttribute("visitAddr", dto.getVisitAddr());
      model.addAttribute("visitAddrDetail", dto.getVisitAddrDetail());
      return "pages/reserve/reserve_time";
    }

    // 사전 중복 체크
    String dateStr = dto.getReserveDate().toString();
    List<Long> reserved = reserveService.getReservedSlotNosByDate(dateStr);
    if (reserved.contains(dto.getSlotNo())) {
      model.addAttribute("error", "이미 예약된 시간입니다.");
      model.addAttribute("modelName", dto.getModelName());
      model.addAttribute("symptom", dto.getSymptom());
      model.addAttribute("categoryNo", dto.getCategoryNo());
      model.addAttribute("visitAddr", dto.getVisitAddr());
      model.addAttribute("visitAddrDetail", dto.getVisitAddrDetail());
      return "pages/reserve/reserve_time";
    }

    // 회원 ID 세팅 (세션에서)
    dto.setMemId(login.getMemId());

    // 상태값 강제 세팅 ('0' = 예약접수)
    dto.setReserveStatus("0");

    // DB 저장
    reserveService.saveReserve(dto);

    // 성공 → 완료 페이지로 redirect
    return "redirect:/reserve/complete-page";
  }

  // 예약 완료 페이지
  @GetMapping("/complete-page")
  public String completePage() {
    return "pages/reserve/reserve_complete";
  }


  // 예약 조회 페이지
  @GetMapping("/list")
  public String reserveList(HttpSession session, Model model) {

    // 1) 로그인 체크
    MemberDTO login = (MemberDTO) session.getAttribute("loginInfo");
    if (login == null) {
      return "redirect:/member/login-form";
    }

    // 2) 회원의 예약 목록 조회 (최신순)
    String memId = login.getMemId();
    List<ReserveDTO> reserves = reserveService.getReservesByMemId(memId);

    // 3) 탭별 카운트 계산
    long activeCount = reserves.stream()
            .filter(r -> "0".equals(r.getReserveStatus())
                    || "1".equals(r.getReserveStatus())
                    || "2".equals(r.getReserveStatus()))
            .count();
    long doneCount = reserves.stream()
            .filter(r -> "9".equals(r.getReserveStatus()))
            .count();
    long cancelCount = reserves.stream()
            .filter(r -> "C".equals(r.getReserveStatus()))
            .count();

    // 4) 화면에 전달
    model.addAttribute("reserves", reserves);
    model.addAttribute("activeCount", activeCount);
    model.addAttribute("doneCount", doneCount);
    model.addAttribute("cancelCount", cancelCount);

    return "pages/reserve/reserve_list";
  }
}
