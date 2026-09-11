package com.commit.project1;

import com.commit.project1.member.dto.MemberDTO;
import com.commit.project1.reserve.service.ReserveService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {
  private final ReserveService reserveService;

  @GetMapping("/")
  public String main(Model model, HttpServletRequest request){

    HttpSession session = request.getSession();
    MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
    if( loginInfo != null ) {
      model.addAttribute("reserves", reserveService.getReservesByMemId(loginInfo.getMemId()));
    }

    return "pages/reserve/main";
  }
}
