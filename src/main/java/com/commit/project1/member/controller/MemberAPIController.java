package com.commit.project1.member.controller;


import com.commit.project1.member.dto.MemberDTO;
import com.commit.project1.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member-api")
public class MemberAPIController {
  private final MemberService memberService;

  //로그인 버튼 클릭시 로그인 성공 여부 확인!(비동기 방식) -> 중복 확인 결과 !null면 로그인 성공!
  @GetMapping("/login")
  public MemberDTO memberLogin(MemberDTO memberDTO, HttpServletRequest request){
    MemberDTO loginInfo = memberService.memberLoginCheck(memberDTO);
    if (loginInfo != null){
      HttpSession session = request.getSession();
      session.setAttribute("loginInfo", loginInfo);
    }
    return loginInfo;
  }

}
