package com.hangxin.common.db;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wang
 * @since 2018-05-29 10:11
 */
public class BaseSqlSession {

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	public SqlSession getDefaultSqlSession() {
		SqlSession sqlSession = new SqlSessionTemplate(sqlSessionFactory);
		return sqlSession;

	}

}