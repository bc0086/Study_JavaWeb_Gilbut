package com.bookshop01.member.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.common.base.BaseController;
import com.bookshop01.member.service.MemberService;
import com.bookshop01.member.vo.MemberVO;

@Controller("memberController")
@RequestMapping(value = "/member")
public class MemberControllerImpl extends BaseController implements MemberController {
	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberVO memberVO;

	// 로그인
	@Override
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	public ModelAndView login(@RequestParam Map<String, String> loginMap, 
								HttpServletRequest request,
								HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		memberVO = memberService.login(loginMap);
		
		if (memberVO != null && memberVO.getMember_id() != null) {
			/* 조회한 회원정보를 가져와 isLogOn속성을 true로 설정하고 
			 * memberInfo속성으로 회원 정보를 저장함 */
			HttpSession session = request.getSession();
			session = request.getSession();
			session.setAttribute("isLogOn", true);
			session.setAttribute("memberInfo", memberVO);

			/* 상품 주문 과정에서 로그인 했으면 로그인 후 다시 주문 화면으로 진행하고
			 * 그 외에는 메인 페이지를 표시함 */
			String action = (String) session.getAttribute("action");
			if (action != null && action.equals("/order/orderEachGoods.do")) {
				mav.setViewName("forward:" + action);
			} else {
				mav.setViewName("redirect:/main/main.do");
			}
		} else {
			String message = "아이디나 비밀번호가 틀립니다. 다시 로그인 해주세요.";
			mav.addObject("message", message);
			mav.setViewName("/member/loginForm");
		}
		return mav;
	}

	// 로그아웃
	@Override
	@RequestMapping(value = "/logout.do", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		session.setAttribute("isLogOn", false);
		session.removeAttribute("memberInfo");
		mav.setViewName("redirect:/main/main.do");
		return mav;
	}

	// 회원 가입
	@Override
	@RequestMapping(value = "/addMember.do", method = RequestMethod.POST)
	public ResponseEntity addMember(@ModelAttribute("memberVO") MemberVO _memberVO, 
									HttpServletRequest request,
									HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("utf-8");
		String message = null;
		ResponseEntity resEntity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			memberService.addMember(_memberVO);
			message = "<script>";
			message += " alert('ȸ�� ������ ���ƽ��ϴ�.�α���â���� �̵��մϴ�.');";
			message += " location.href='" + request.getContextPath() + "/member/loginForm.do';";
			message += " </script>";
		} catch (Exception e) {
			message = "<script>";
			message += " alert('�۾� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���');";
			message += " location.href='" + request.getContextPath() + "/member/memberForm.do';";
			message += " </script>";
			e.printStackTrace();
		}
		resEntity = new ResponseEntity(message, responseHeaders, HttpStatus.OK);
		return resEntity;
	}

	// 아이디 중복검사
	@Override
	@RequestMapping(value = "/overlapped.do", method = RequestMethod.POST)
	public ResponseEntity overlapped(@RequestParam("id") String id, 
									HttpServletRequest request,
									HttpServletResponse response) throws Exception {
		ResponseEntity resEntity = null;
		// 아이디 중복검사를 함
		String result = memberService.overlapped(id);
		resEntity = new ResponseEntity(result, HttpStatus.OK);
		return resEntity;
	}
}
