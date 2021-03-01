package com.javaweb.bcjin.member.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.javaweb.bcjin.member.service.MemberService;
import com.javaweb.bcjin.member.vo.MemberVO;


@Controller("memberController")
	/* @Controller를 이용해 MemberControllerImpl 클래스에 대해 
	 * id가 memberController인 빈을 자동 생성함 */

public class MemberControllerImpl implements MemberController {
	private static final Logger logger = LoggerFactory.getLogger(MemberControllerImpl.class);
		// LoggerFactory클래스를 이용해 Logger클래스 객체를 가져옴
	
	@Autowired
	private MemberService memberService; 
		// id가 memberService인 빈을 자동 주입
	
	@Autowired
	private MemberVO memberVO; 
		// id가 memberVO인 빈을 자동 주입
	
	// main.jsp를 표시해주는 main()메서드 추가
	@RequestMapping(value= "/main.do", method=RequestMethod.GET)
	private ModelAndView main(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		return mav;
	}
	
	
	@Override
	@RequestMapping(value="/member/listMembers.do", method=RequestMethod.GET)
	public ModelAndView listMembers(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
			// 인터셉터에서 바인딩 된 뷰임을 가져옴.
		logger.info("info레벨 : viewName =" + viewName); // 로그메세지 레벨을 info로 설정
		logger.debug("debug레벨 : viewName =" + viewName); // 로그메세지 레벨을 debug로 설정
		
		List membersList = memberService.listMembers();
		ModelAndView mav = new ModelAndView(viewName);
			// viewName이 definition 태그에 설정한 뷰이름과 일치함
		
		mav.addObject("membersList", membersList); 
			// 조회한 회원 정보를 ModelAndView의 addObject() 메서드를 이용해 바인딩함. 
		
		return mav;
			// ModelAndView 객체에 설정한 뷰 이름을 타일즈 뷰리졸버로 반환함
	}
	


	@Override
	@RequestMapping(value="/member/addMember.do", method=RequestMethod.POST)
	public ModelAndView addMember(@ModelAttribute("member") MemberVO member, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		int result = 0;
		result = memberService.addMember(member);
		ModelAndView mav = new ModelAndView("redirect:/member/listMembers.do");
			/* 회원정보 추가 후 ModelAndView클래스의 redirect 속성을 이용해 
			 * /member.listMembers.do로 리다이렉트함 */

		return mav;
	}
	
	@Override
	@RequestMapping(value="/member/removeMember.do", method=RequestMethod.GET)
	public ModelAndView removeMember(@RequestParam("id") String id, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		memberService.removeMember(id);
		ModelAndView mav = new ModelAndView("redirect:/member/listMembers.do");

		return mav;
	}
	

	
	// 로그인
	@Override
	@RequestMapping(value="/member/login.do", method=RequestMethod.POST)
	public ModelAndView login (@ModelAttribute("member") MemberVO member, 
								RedirectAttributes rAttr,
								HttpServletRequest request,
								HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		memberVO = memberService.login(member); // login()메서드를 호출하면서 로그인정보 전달
		
		if(memberVO != null) {
			HttpSession session = request.getSession();
			session.setAttribute("member", memberVO);
			session.setAttribute("isLogOn", true); // 세션에 로그인 상태를 true로 설정
			
			// 로그인 성공 시 세션에 저장된 action값을 가져옴
			String action = (String) session.getAttribute("action");
			
			// action값이 null이 아니면 action값을 뷰이름으로 지정해 글쓰기창으로 이동함
			if(action != null) {
				mav.setViewName("redirect:" + action);
			} else {
				mav.setViewName("redirect:/member/listMembers.do");
			}
		}
		else {
			rAttr.addAttribute("result", "loginFailed");
				// 로그인 실패시 실패 메세지를 로그인창으로 전달
			mav.setViewName("redirect:/member/loginForm.do");
				// 로그인 실채 시 다시 로그인창으로 리다이렉트
		}
		return mav;
	}
	
	// 로그아웃
	@Override
	@RequestMapping(value="/member/logout.do", method=RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request,
								HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		// 로그아웃 요청 시 세션에 저장된 로그인 정보와 회원정보를 삭제
		session.removeAttribute("member");
		session.removeAttribute("isLogOn");
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/member/listMembers.do");
		return mav;
	}
	
	// 로그인 후 수행할 글쓰기 요청명을 action값에 저장함. 로그인 성공 후 바로 글쓰기 창으로 이동
	@RequestMapping(value="/member/*Form.do", method=RequestMethod.GET)
	private ModelAndView form(@RequestParam(value="result", required = false) String result,
								@RequestParam(value="action", required = false) String action,
								HttpServletRequest request,
								HttpServletResponse response) throws Exception {
//		String viewName = getViewName(request);
		String viewName = (String) request.getAttribute("viewName");
			// 인터셉터에서 바인딩 된 뷰이름을 가져옴.
		
		HttpSession session = request.getSession();
		session.setAttribute("action", action);
			// 글쓰기 창 요청명을 action 속성으로 세션에 저장함.
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("result", result);
		mav.setViewName(viewName);
		return mav;
	}
	
	
}