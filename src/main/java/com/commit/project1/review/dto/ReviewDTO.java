package com.commit.project1.review.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReviewDTO {
  private Long reviewNo;
  private Long rating;
  private String content;
  private Long reserveNo;
  private LocalDate reviewDate;
}
