package com.javaweb.bcjin.board.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.javaweb.bcjin.board.vo.ArticleVO;

public interface BoardDAO {
	public List selectAllArticlesList() throws DataAccessException;

	public int insertNewArticle(Map articleMap);

	public ArticleVO selectArticle(int articleNO) throws DataAccessException;

	public void updateArticle(Map<String, Object> articleMap);

	public void deleteArticle(int articleNO);

}
