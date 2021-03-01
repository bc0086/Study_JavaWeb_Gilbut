package com.javaweb.bcjin.member.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.javaweb.bcjin.member.vo.MemberVO;


public interface MemberService {
	public List listMembers() throws DataAccessException;

	public int addMember(MemberVO membeVO) throws DataAccessException;
	
	public int removeMember(String id) throws DataAccessException;

	public MemberVO findMember(String id) throws DataAccessException;

	public MemberVO login(MemberVO member);

}
