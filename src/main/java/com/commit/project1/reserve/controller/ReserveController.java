package com.commit.project1.reserve.controller;

import com.commit.project1.member.dto.MemberDTO;
import com.commit.project1.reserve.dto.CategoryDTO;
import com.commit.project1.reserve.dto.ReserveDTO;
import com.commit.project1.reserve.dto.TimeSlotDTO;
import com.commit.project1.reserve.service.ReserveService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

  // 임시 폼 페이지 (테스트용)
  @GetMapping("/form-temp")
  public String reserveFormTemp() {
    return "pages/reserve/reserve_form_temp";
  }

  // 예약 시간 선택 페이지
  // 임시 처리 - 완성 전이라 URL 파라미터(product, symptom)로 값을 받아서
  // model에 담아 화면(reserve_time.html)의 hidden input으로 넘김
  // 값이 넘어오지 않으면 임시 테스트값을 사용

  @GetMapping("/time")
  public String reserveTime(
          @RequestParam(value = "product", required = false) String product,
          @RequestParam(value = "symptom", required = false) String symptom,
          Model model) {

    // 기본값: 에어컨(AC)
    // - product가 null이거나 "AC"인 경우 이 값이 그대로 사용됨
    int categoryNo = 1;
    String modelName = "삼성 무풍 에어컨";

    // 다른 제품일 때만 덮어쓰기
    if ("OUT".equals(product)) {
      categoryNo = 2;
      modelName = "삼성 실외기";
    } else if ("DEHUM".equals(product)) {
      categoryNo = 4;
      modelName = "삼성 제습기";
    }

    // 그 외(AC 또는 null)는 기본값 유지

    // 화면으로 넘길 값 세팅
    // → reserve_time.html의 <input type="hidden" th:value="${...}">에 자동 반영

    model.addAttribute("categoryNo", categoryNo);
    model.addAttribute("modelName", modelName);
    model.addAttribute("symptom",
            symptom != null ? symptom : "테스트 증상입니다. 찬바람이 안 나옵니다.");
    model.addAttribute("visitAddr", "울산광역시 남구 삼산로 123");
    model.addAttribute("visitAddrDetail", "101동 202호");
    return null;
  }

    @PostMapping("/form-submit")
    public String reserveTime (ReserveDTO reserveDTO){
      System.out.println(reserveDTO);
      return "pages/reserve/reserve_time";
    }

    // 시간 슬롯 조회 API (비동기)
    // 특정 날짜의 슬롯 목록 + 예약된 슬롯 번호를 JSON으로 반환
    // 호출 흐름
    // - reserve_time.js의 loadAvailableTimes()가 날짜 선택 시마다 호출
    // - 응답 형식: { slots: [...], reservedSlots: [1, 3] }
    // @param date 조회할 날짜 ("yyyy-MM-dd")
    // @return 슬롯 전체 목록(slots) + 이미 예약된 슬롯 번호 목록(reservedSlots)

    @GetMapping("/available-times")
    @ResponseBody
    public Map<String, Object> availableTimes (@RequestParam("date") String date){

      // 1) TIME_SLOT 테이블에서 전체 슬롯 목록 조회 (1~4번 슬롯)

      List<TimeSlotDTO> slots = reserveService.getAllTimeSlots();

      // 2) 해당 날짜에 이미 예약된 SLOT_NO 목록 조회 (취소 'C'는 제외됨)

      List<Long> reservedSlots = reserveService.getReservedSlotNosByDate(date);

      Map<String, Object> result = new HashMap<>();
      result.put("slots", slots);                 // 전체 슬롯 목록
      result.put("reservedSlots", reservedSlots); // 예약된 슬롯 번호 목록
      return result;
    }

    // 예약 확정 (저장)

    // 예약 확정 처리
    // 폼(reserve-time-form)에서 POST 전송됨
    // - 검증 통과 시 DB에 INSERT 후 완료 페이지로 redirect
    // 입력값 - ReserveDTO: modelName, symptom, categoryNo, visitAddr, visitAddrDetail,reserveDate, slotNo 등이 폼에서 자동 바인딩됨
    @PostMapping("/complete")
    public String completeReserve (ReserveDTO dto,
            HttpSession session,
            RedirectAttributes rttr){

      // 1) 슬롯 유효성 검증
      //    - 조작된 SLOT_NO(예: 9999)가 들어오는 것을 방지
      //    - TIME_SLOT 테이블에 존재하는 슬롯 번호인지 확인
      List<TimeSlotDTO> slots = reserveService.getAllTimeSlots();
      boolean validSlot = slots.stream()
              .anyMatch(s -> s.getSlotNo().equals(dto.getSlotNo()));
      if (!validSlot) {
        rttr.addFlashAttribute("error", "유효하지 않은 시간입니다.");
        return "redirect:/reserve/time";
      }

      // 2) 사전 중복 체크
      //    - 같은 날짜 + 같은 슬롯에 이미 예약이 있는지 확인
      //    - 취소('C')된 예약은 조회 결과에서 제외되므로 재예약 가능
      String dateStr = dto.getReserveDate().toString();  // LocalDate → "yyyy-MM-dd"
      List<Long> reserved = reserveService.getReservedSlotNosByDate(dateStr);
      if (reserved.contains(dto.getSlotNo())) {
        rttr.addFlashAttribute("error", "이미 예약된 시간입니다.");
        return "redirect:/reserve/time";
      }

      // 3) 로그인 회원 ID 결정
      //    - 팀원이 로그인 기능을 완성하면 세션의 loginInfo에서 꺼내 씀
      //    - 아직 없으면 임시값 "user1" 사용
      String memId = "user1";
      MemberDTO login = (MemberDTO) session.getAttribute("loginInfo");
      if (login != null) {
        memId = login.getMemId();
      }
      dto.setMemId(memId);

      // 4) 예약 상태 기본값 세팅
      //    - '0' = 예약접수 (기사 미배정)
      //    - 상태값은 서버에서 강제로 지정하여 조작 방지
      dto.setReserveStatus("0");

      // 5) DB 저장
      reserveService.saveReserve(dto);

      // 6) 저장 성공 시 완료 페이지로 이동
      return "redirect:/reserve/complete-page";
    }

    // 예약 완료 페이지
    @GetMapping("/complete-page")
    public String completePage () {
      return "pages/reserve/reserve_complete";
    }

    // 예약 조회 페이지

  @GetMapping("/list")
    public String reserveList (HttpServletRequest request, Model model){

      HttpSession session = request.getSession();
      MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
    String memId = "user1";
    MemberDTO login = (MemberDTO) session.getAttribute("loginInfo");
    if (login != null) {
      memId = login.getMemId();
    }
      if (loginInfo != null) {
        model.addAttribute("reserves", reserveService.getReservesByMemId(loginInfo.getMemId()));
      }

      // 2) 회원의 예약 목록 조회 (최신순)
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
