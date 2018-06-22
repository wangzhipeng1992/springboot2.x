package com.hangxin.persistence.mapper;

import java.util.Map;

import com.hangxin.persistence.model.People;
import com.hangxin.util.MyMapper;

public interface PeopleMapper extends MyMapper<People> {

	int peopleInsertXml(Map<String, Object> peopleInfo);
	
}
