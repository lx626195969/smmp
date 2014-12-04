package com.sioo.cmppgw.entity.test;

import java.io.Serializable;

/**
 * @author leeson 2014年12月4日 下午5:25:06 li_mr_ceo@163.com <br>
 * 
 */
public class Student implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private int age;

	public Student(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
