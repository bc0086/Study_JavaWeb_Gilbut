package com.spring.bcjin.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.bcjin.member.vo.MemberVO;

public interface MemberController {
	public ModelAndView listMembers(HttpServletRequest request, 
									HttpServletResponse response) throws Exception;

	ModelAndView addMember(@ModelAttribute("info") MemberVO memberVO, 
							HttpServletRequest request, 
							HttpServletResponse response) throws Exception;

	ModelAndView removeMember(@RequestParam("id") String id, 
								HttpServletRequest request, 
								HttpServletResponse response) throws Exception;

	ModelAndView login(MemberVO member, 
						RedirectAttributes rAttr, 
						HttpServletRequest request,
						HttpServletResponse response) throws Exception;

	ModelAndView logout(HttpServletRequest request,
						HttpServletResponse response) throws Exception;
}
