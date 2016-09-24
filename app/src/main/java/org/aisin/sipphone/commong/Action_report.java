package org.aisin.sipphone.commong;

public class Action_report implements Comparable<Action_report> {

	private String agent_id;
	private String adid;
	private String picname;
	private int shownum;
	private int clicknum;

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getAdid() {
		return adid;
	}

	public void setAdid(String adid) {
		this.adid = adid;
	}

	public String getPicname() {
		return picname;
	}

	public void setPicname(String picname) {
		this.picname = picname;
	}

	public int getShownum() {
		return shownum;
	}

	public void setShownum(int shownum) {
		this.shownum = shownum;
	}

	public int getClicknum() {
		return clicknum;
	}

	public void setClicknum(int clicknum) {
		this.clicknum = clicknum;
	}

	@Override
	public int compareTo(Action_report another) {
		return this.adid.compareTo(another.adid);
	}

}
