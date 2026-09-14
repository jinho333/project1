package com.commit.project1.review.service;

import com.commit.project1.review.dto.ReviewDTO;
import com.commit.project1.review.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {
  private final ReviewMapper reviewMapper;

  public void reviewReg(ReviewDTO reviewDTO){
    reviewMapper.reviewReg(reviewDTO);
  }

  public List<ReviewDTO>selectReviews(){
    return reviewMapper.selectReviews();
  }

  public Map<String, Object>selectStats(){
    return reviewMapper.selectStats();
  }

}
