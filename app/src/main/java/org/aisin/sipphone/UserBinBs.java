package org.aisin.sipphone;

import cn.bmob.v3.BmobObject;

public class UserBinBs extends BmobObject {

	private String username;
	private String password;
	private String balance;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

}
