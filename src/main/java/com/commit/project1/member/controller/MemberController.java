package com.commit.project1.member.controller;

import com.commit.project1.member.service.MemberService;
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

  //로그인 버튼 클릭시
  // 비동기로 중복을 확인 후 !=m

  //작업 내용 commit / dev로 이동/ dev내용 내려받기
  //@PostMapping("/login")

}
