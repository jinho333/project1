package com.commit.project1.member.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberDTO {
  private String memId;
  private String memPw;
  private String memName;
  private String memRole;
  private String tel;
  private String addr;
  private String addrDetail;
  private String memStatus;
  private LocalDate registDate;
}
