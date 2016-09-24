package org.aisin.sipphone.commong;

import java.io.Serializable;

import org.aisin.sipphone.tools.CursorTools;

public class RedObject implements Comparable<RedObject>, Serializable {

	private static final long serialVersionUID = -6919461967497580485L;
	private String splitsnumber;
	private String shake_ratio;
	private String received_money;
	private String status;
	private String returned_money;
	private String sub_type;
	private String command;
	private String from;
	private String open_time;
	private String gift_id;
	private String money;
	private int has_open;
	private String direct;
	private String create_time;
	private String from_phone;
	private String fromnickname;
	private String money_type;// 拼手气
	private String tips;
	private String exp_time;
	private String type;
	private String sender_gift_id;
	private String name;

	public String getSplitsnumber() {
		return splitsnumber;
	}

	public void setSplitsnumber(String splitsnumber) {
		this.splitsnumber = splitsnumber;
	}

	public String getShake_ratio() {
		return shake_ratio;
	}

	public void setShake_ratio(String shake_ratio) {
		this.shake_ratio = shake_ratio;
	}

	public String getReceived_money() {
		return received_money;
	}

	public void setReceived_money(String received_money) {
		this.received_money = received_money;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReturned_money() {
		return returned_money;
	}

	public void setReturned_money(String returned_money) {
		this.returned_money = returned_money;
	}

	public String getSub_type() {
		return sub_type;
	}

	public void setSub_type(String sub_type) {
		this.sub_type = sub_type;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getOpen_time() {
		return open_time;
	}

	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}

	public String getGift_id() {
		return gift_id;
	}

	public void setGift_id(String gift_id) {
		this.gift_id = gift_id;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public int getHas_open() {
		return has_open;
	}

	public void setHas_open(int has_open) {
		this.has_open = has_open;
	}

	public String getDirect() {
		return direct;
	}

	public void setDirect(String direct) {
		this.direct = direct;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getFrom_phone() {
		return from_phone;
	}

	public void setFrom_phone(String from_phone) {
		this.from_phone = from_phone;
	}

	public String getFromnickname() {
		return fromnickname;
	}

	public void setFromnickname(String fromnickname) {
		this.fromnickname = fromnickname;
	}

	public String getMoney_type() {
		return money_type;
	}

	public void setMoney_type(String money_type) {
		this.money_type = money_type;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public String getExp_time() {
		return exp_time;
	}

	public void setExp_time(String exp_time) {
		this.exp_time = exp_time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSender_gift_id() {
		return sender_gift_id;
	}

	public void setSender_gift_id(String sender_gift_id) {
		this.sender_gift_id = sender_gift_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(RedObject another) {
		if (another.direct.equals("sended") && this.direct.equals("sended")) {
			if (another.create_time.equals(this.create_time)) {
				return another.gift_id.compareTo(this.gift_id);
			} else {
				return another.create_time.compareTo(this.create_time);
			}
		} else if (another.direct.equals("sended")
				&& this.direct.equals("received")) {
			return -1;
		} else if (another.direct.equals("received")
				&& this.direct.equals("sended")) {
			return 1;
		} else if (another.direct.equals("received")
				&& this.direct.equals("received")) {
			if ((this.has_open == 0 && (this.exp_time
					.compareTo(CursorTools.date_3) < 0))
					&& (another.has_open == 0 && (another.exp_time
							.compareTo(CursorTools.date_3) < 0))) {
				if (another.create_time.equals(this.create_time)) {
					return another.gift_id.compareTo(this.gift_id);
				} else {
					return another.create_time.compareTo(this.create_time);
				}
			} else if ((this.has_open == 0 && (this.exp_time
					.compareTo(CursorTools.date_3) < 0))
					&& (another.has_open == 0 && (another.exp_time
							.compareTo(CursorTools.date_3) >= 0))) {
				return 1;
			} else if ((this.has_open == 0 && (this.exp_time
					.compareTo(CursorTools.date_3) < 0))
					&& another.has_open == 1) {
				return 1;
			} else if ((this.has_open == 0 && (this.exp_time
					.compareTo(CursorTools.date_3) >= 0))
					&& (another.has_open == 0 && (another.exp_time
							.compareTo(CursorTools.date_3) < 0))) {
				return -1;
			} else if (this.has_open == 1
					&& (another.has_open == 0 && (another.exp_time
							.compareTo(CursorTools.date_3) < 0))) {
				return -1;
			} else if (another.has_open == 0 && this.has_open == 1) {
				return 1;
			} else if (another.has_open == 1 && this.has_open == 0) {
				return -1;
			} else if (another.has_open == 0 && this.has_open == 0) {
				if (another.create_time.equals(this.create_time)) {
					return another.gift_id.compareTo(this.gift_id);
				} else {
					return another.create_time.compareTo(this.create_time);
				}
			} else if (another.has_open == 1 && this.has_open == 1) {
				if (another.open_time.equals(this.open_time)) {
					return another.gift_id.compareTo(this.gift_id);
				} else {
					return another.open_time.compareTo(this.open_time);
				}
			}
		}
		return 0;
	}
}
