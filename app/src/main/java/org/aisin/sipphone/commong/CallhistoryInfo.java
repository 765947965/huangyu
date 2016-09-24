package org.aisin.sipphone.commong;

import java.io.Serializable;

public class CallhistoryInfo implements Comparable<CallhistoryInfo>,
		Serializable {
	private static final long serialVersionUID = -6919461967497580385L;
	private String _id;
	private String name;
	private String phone;
	private int call_type; // 1呼入 2呼出 3未接 0挂断
	private String call_type_name;// 呼叫状态名称
	private int call_type_imageid;// 呼叫状态图标ID
	private String call_time;
	private String chainal_call_time;
	private String duration;
	private boolean isself;

	public CallhistoryInfo(String _id, String name, String phone,
			int call_type, String call_type_name, int call_type_imageid,
			String call_time, String chainal_call_time, String duration,
			boolean isself) {
		this._id = _id;
		this.name = name;
		this.phone = phone;
		this.call_type = call_type;
		this.call_type_name = call_type_name;
		this.call_type_imageid = call_type_imageid;
		this.call_time = call_time;
		this.chainal_call_time = chainal_call_time;
		this.duration = duration;
		this.isself = isself;
	}

	public boolean isIsself() {
		return isself;
	}

	public void setChainal_call_time(String chainal_call_time) {
		this.chainal_call_time = chainal_call_time;
	}

	public String get_id() {
		return _id;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public int getCall_type() {
		return call_type;
	}

	public String getCall_time() {
		return call_time;
	}

	public String getDuration() {
		return duration;
	}

	public String getCall_type_name() {
		return call_type_name;
	}

	public int getCall_type_imageid() {
		return call_type_imageid;
	}

	public String getChainal_call_time() {
		return chainal_call_time;
	}

	@Override
	public int compareTo(CallhistoryInfo another) {
		return -this.call_time.compareTo(another.call_time);
	}
}
