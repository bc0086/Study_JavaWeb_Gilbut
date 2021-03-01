package com.javaweb.bcjin.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.javaweb.bcjin.board.service.BoardService;
import com.javaweb.bcjin.board.vo.ArticleVO;

@Controller("boardController")
public class BoardControllerImpl implements BoardController {
	private static final String CURR_IMAGE_REPO_PATH = "c:\\03Workspace\\file_repo";
	
	@Autowired
	private BoardService boardService;
	
	@Autowired
	private ArticleVO articleVO;

	@Override
	@RequestMapping(value="/board/listArticles.do", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView listArticles(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		List articlesList = boardService.listArticles();
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("articlesList", articlesList);
		return mav;
	}

}
