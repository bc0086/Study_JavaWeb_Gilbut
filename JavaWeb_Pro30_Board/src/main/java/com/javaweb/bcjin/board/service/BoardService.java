package com.javaweb.bcjin.board.service;

import java.util.List;

import com.javaweb.bcjin.board.vo.ArticleVO;

public interface BoardService {
	public List<ArticleVO> listArticles() throws Exception;
}
