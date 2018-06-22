package com.hangxin.persistence.model;

import java.io.Serializable;

public class People extends BaseEntity implements Serializable{

    private static final long serialVersionUID = 1810690176131043901L;

    private String name;

	private Integer sex;

	private Integer age;

	private Double height;

	private Double weight;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

}
