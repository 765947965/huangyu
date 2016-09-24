package org.aisin.sipphone.commong;

public class UserInfo {
	private String bding_phone;
	private String phone;
	private String pwd;
	private String uid;
	private String agent_id = "1";
	private String v = "";
	private String pv = "android";

	public UserInfo() {

	}

	public String getBding_phone() {
		return bding_phone;
	}

	public void setBding_phone(String bding_phone) {
		this.bding_phone = bding_phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public String getPv() {
		return pv;
	}

}
