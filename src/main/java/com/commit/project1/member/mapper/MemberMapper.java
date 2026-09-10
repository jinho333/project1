package com.commit.project1.member.mapper;

import com.commit.project1.member.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

  //로그인 가능 여부 확인 쿼리
  MemberDTO memberLoginCheck(MemberDTO memberDTO);
}
