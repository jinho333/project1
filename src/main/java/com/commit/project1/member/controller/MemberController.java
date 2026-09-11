package com.commit.project1.member.controller;

import com.commit.project1.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/member")
@RequiredArgsConstructor
@Controller
public class MemberController {
  private final MemberService memberService;

  //로그인 페이지 이동
  @GetMapping("/login-form")
  public String loginForm(){
    return "pages/member/login";
  }
}
