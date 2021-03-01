package com.javaweb.bcjin.board.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

public interface BoardDAO {
	public List selectAllArticlesList() throws DataAccessException;
}
