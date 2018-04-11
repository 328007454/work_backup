package com.cnksi.bdzinspection.model;

import java.util.ArrayList;

public class TestPerson {

	public String name;

	public String position;
	
	public String phoneNumber;

	public boolean isChecked = false;

	public TestPerson(String name, String position) {
		this.name = name;
		this.position = position;
		this.phoneNumber = "13981308155";
		this.isChecked = false;
	}

	public static ArrayList<TestPerson> getPersonList() {
		ArrayList<TestPerson> list = new ArrayList<TestPerson>();

		list.add(new TestPerson("杨莉", "专责"));
		list.add(new TestPerson("肖珂", "班长"));
		list.add(new TestPerson("钟斌", "运维检修部副主任"));
		list.add(new TestPerson("贺俊", "班组长"));

		return list;
	}

}
