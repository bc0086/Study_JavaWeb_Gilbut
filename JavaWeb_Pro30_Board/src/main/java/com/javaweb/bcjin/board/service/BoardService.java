package com.javaweb.bcjin.board.service;

import java.util.List;
import java.util.Map;

import com.javaweb.bcjin.board.vo.ArticleVO;

public interface BoardService {
	public List<ArticleVO> listArticles() throws Exception;

	int addNewArticle(Map articleMap) throws Exception;

//	public ArticleVO viewArticle(int articleNO) throws Exception;
	public Map viewArticle(int articleNO) throws Exception;

	public void modArticle(Map<String, Object> articleMap);

	public void removeArticle(int articleNO);
}
