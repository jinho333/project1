package com.commit.project1.review.mapper;

import com.commit.project1.review.dto.ReviewDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewMapper {
  void reviewReg(ReviewDTO reviewDTO);
  List<ReviewDTO>selectReviews();
  Map<String, Object>selectStats();
}
