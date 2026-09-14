package com.commit.project1.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/review")
public class ReviewController {

  @GetMapping("/write")
  public String reviewWrite() {
    return "pages/review/review_form";
  }
}
