package com.commit.project1.reserve.controller;

import com.commit.project1.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reserve")
@RequiredArgsConstructor
public class ReserveController {
  private final ReserveService reserveService;

  //예약 정보 입력 관련 컨트롤러
  @GetMapping("/form")
  public String reserveForm(){
    return "pages/reserve/reserve_form";
  }

  //예약일정 관련 컨트롤러
  @GetMapping("/time")
  public String reserveTime(){
    return "pages/reserve/reserve_time";
  }


}
