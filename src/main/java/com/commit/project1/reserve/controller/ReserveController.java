package com.commit.project1.reserve.controller;

import com.commit.project1.member.dto.MemberDTO;
import com.commit.project1.reserve.dto.CategoryDTO;
import com.commit.project1.reserve.dto.ReserveDTO;
import com.commit.project1.reserve.service.ReserveService;
import jakarta.servlet.http.HttpServletRequest;
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

  //예약 정보 입력 관련 컨트롤러
  @GetMapping("/form")
  public String reserveForm(HttpSession session, Model model){

    //로그인한 회원 주소 (세션에서 아이디를 찾자!)
   MemberDTO member = (MemberDTO) session.getAttribute("loginInfo");
   if (member != null){
     String memId = member.getMemId();
     model.addAttribute("member", reserveService.selectMember(memId));
   }

    return "pages/reserve/reserve_form";
  }

  // 예약 시간 선택 페이지
  @PostMapping("/form-submit")
  public String reserveTime(ReserveDTO reserveDTO){
    System.out.println(reserveDTO);
    return "pages/reserve/reserve_time";
  }



  //  특정 날짜의 예약 가능 시간을 JSON으로 반환하는 API
  @GetMapping("/available-times")
  @ResponseBody
  public Map<String, Object> availableTimes(@RequestParam("date") String date) {

    // 전체 시간 슬롯 (09:00 ~ 16:30, 30분 단위) 테이블이 없으므로 임의로 제작
    List<String> allTimes = List.of(
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30"
    );

    // DB에서 해당 날짜에 이미 예약된 시간 목록 조회
    List<String> reservedTimes = reserveService.getReservedTimesByDate(date);

    // 결과를 Map으로 묶어 JSON으로 반환 임시로 map 사용 차후 dto를 따로 생성
    Map<String, Object> result = new HashMap<>();
    result.put("allTimes", allTimes);             // 전체 시간 목록
    result.put("reservedTimes", reservedTimes);  // 마감된 시간 목록

    return result;
  }

  // [POST] 예약 확정 처리
  // - reserve_time.html 폼에서 전송
  // - 저장 성공 시 완료 페이지로 redirect
  @PostMapping("/complete")
  public String completeReserve(ReserveDTO dto, HttpSession session) {
    // 1. 로그인한 사용자 ID 가져오기
    //    TODO: 로그인 기능 완성 후 세션에서 정확한 ID를 꺼내도록 수정
    //    현재는 로그인 기능이 없어서 임시로 "user1" 사용
    String memId = (String) session.getAttribute("loginId");
    if (memId == null) {
      memId = "user1";  // 임시
    }
    dto.setMemId(memId);

    // 2. 예약 상태 기본값: '0' = 예약접수
    dto.setReserveStatus("0");

    // 3. Service 호출 → DB 저장
    int result = reserveService.saveReserve(dto);

    // 4. 결과에 따라 페이지 이동
    if (result > 0) {
      // 저장 성공 → 예약 완료 페이지
      return "redirect:/reserve/complete-page";
    } else {
      // 저장 실패 → 다시 시간 선택 페이지
      return "redirect:/reserve/time";
    }
  }

  //예약 완료
  @GetMapping("/complete-page")
  public String completePage() {
    return "pages/reserve/reserve_complete";
  }

  //예약 조회
  @GetMapping("/list")
  public String reserveList(HttpServletRequest request, Model model){
    HttpSession session = request.getSession();
    MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
    if( loginInfo != null ) {
      model.addAttribute("reserves", reserveService.getReservesByMemId(loginInfo.getMemId()));
    }

    return "pages/reserve/reserve_list";
  }


}
