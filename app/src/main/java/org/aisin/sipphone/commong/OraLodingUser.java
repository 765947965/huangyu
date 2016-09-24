package org.aisin.sipphone.commong;

public class OraLodingUser implements Comparable<OraLodingUser> {
	private String phonenum;
	private long time;

	public OraLodingUser(String phonenum, long time) {
		super();
		this.phonenum = phonenum;
		this.time = time;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public long getTime() {
		return time;
	}

	@Override
	public int compareTo(OraLodingUser another) {
		return (int) (another.time - this.time);
	}

}
