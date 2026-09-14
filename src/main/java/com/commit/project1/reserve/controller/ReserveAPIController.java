package com.commit.project1.reserve.controller;

import com.commit.project1.reserve.dto.CategoryDTO;
import com.commit.project1.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/reserve-api")
@RestController
public class ReserveAPIController {
  private final ReserveService reserveService;

  @GetMapping("/productType")
  public List<CategoryDTO> selectCategory(@RequestParam("productType") String productType){
    return reserveService.selectCategory(productType);
  }
}
