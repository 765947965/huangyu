package org.aisin.sipphone.dial;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

public class XXContext extends BmobObject{

	private String name;
	private ArrayList<String> phones;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getPhones() {
		return phones;
	}
	public void setPhones(ArrayList<String> phones) {
		this.phones = phones;
	}
	
}
