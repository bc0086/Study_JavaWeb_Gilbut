package com.spring.bcjin.member.service.copy;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.spring.bcjin.member.vo.MemberVO;

public interface MemberService {
	public List listMembers() throws DataAccessException;

	public int addMember(MemberVO membeVO) throws DataAccessException;
	
	public int removeMember(String id) throws DataAccessException;

	public MemberVO findMember(String id) throws DataAccessException;

	public MemberVO login(MemberVO member);

}
