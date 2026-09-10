package com.commit.project1.member.service;

import com.commit.project1.member.dto.MemberDTO;
import com.commit.project1.member.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberMapper memberMapper;

  //로그인 가능여부 판단 쿼리
  public MemberDTO memberLoginCheck(MemberDTO memberDTO){
    return memberMapper.memberLoginCheck(memberDTO);
  }




}
