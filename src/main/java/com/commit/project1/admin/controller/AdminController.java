package com.commit.project1.admin.controller;

import com.commit.project1.reserve.service.ReserveService;
import com.commit.project1.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
  private final ReserveService reserveService;
  private final ReviewService reviewService;
  @GetMapping("")
  public String adminMain(Model model){
    model.addAttribute("reserves", reserveService.selectReserves());
    model.addAttribute("reviews", reviewService.selectReviews());
    model.addAttribute("reviewStats", reviewService.selectStats());
    return "pages/admin/dash_board";
  }

}
