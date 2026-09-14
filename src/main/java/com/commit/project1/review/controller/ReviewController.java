package com.commit.project1.review.controller;

import com.commit.project1.review.dto.ReviewDTO;
import com.commit.project1.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
  private final ReviewService reviewService;


  @GetMapping("/form/{reserveNo}")
  public String reviewForm(@PathVariable("reserveNo")Long reserveNo, Model model){
    model.addAttribute("reserveNo", reserveNo);
    return "pages/review/review_form";
  }

  @PostMapping("/reg/{reserveNo}")
  public String reviewReg(ReviewDTO reviewDTO){
    System.out.println(reviewDTO);
    reviewService.reviewReg(reviewDTO);
    return "redirect:/reserve/list";
  }

}
