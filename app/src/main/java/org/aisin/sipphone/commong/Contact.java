package org.aisin.sipphone.commong;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeSet;

import org.aisin.sipphone.tools.AvatarID;

import android.graphics.Bitmap;

public class Contact implements Comparable<Contact>, Serializable {
	private static final long serialVersionUID = -6919461967497580385L;
	private Long contractID;
	private String remark;// 通讯录的名称
	private String F_PY;
	private String T_PY;
	private String spy;
	private String matchstr;
	private String matchstr_mailist;
	private Bitmap tx;
	private int avatarid;
	private TreeSet<String> searchlist4name;
	private ArrayList<String> phonesList;
	private boolean showflag = false;// 展示以什么为主false标志以通讯录风格展示，true表示以环宇好友风格展示
	private boolean isfreand = false;// 是否环宇用户
	private Bitmap tx_fread;// 环宇头像
	private String ver;// 好友资料版本号
	private String picture;// 好友头像URL
	private String picmd5;// 好友头像md5值
	private String signature;// 好友给自己设置的签名
	private String friendname;// 好友给自己设置的姓名昵称
	private String friendphone;// 环宇好友的手机号码
	private String mobileNumber;// 好友绑定的手机号码
	private String profession;// 好友的职业
	private String company;// 好友的公司
	private String province;// 好友的省份
	private String city;// 好友的城市
	private String school;// 好友的学校城市
	private String uid;// 好友的UID
	private String sex;// 环宇好友性别
	private String bthday;// 环宇好友生日
	private int tx_imagedownflag;// 是否需要更新好友自定义头像 // 0不需要更新头像，1需要更新头像
	private boolean isMyfriend = true;// 是否是我的环宇好友

	public boolean isMyfriend() {
		return isMyfriend;
	}

	public void setMyfriend(boolean isMyfriend) {
		this.isMyfriend = isMyfriend;
	}

	public int getAvatarid() {
		return avatarid;
	}

	public boolean isShowflag() {
		return showflag;
	}

	public void setShowflag(boolean showflag) {
		this.showflag = showflag;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public int getTx_imagedownflag() {
		return tx_imagedownflag;
	}

	public void setTx_imagedownflag(int tx_imagedownflag) {
		this.tx_imagedownflag = tx_imagedownflag;
	}

	public String getFriendphone() {
		return friendphone;
	}

	public void setFriendphone(String friendphone) {
		this.friendphone = friendphone;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBthday() {
		return bthday;
	}

	public void setBthday(String bthday) {
		this.bthday = bthday;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPicmd5() {
		return picmd5;
	}

	public void setPicmd5(String picmd5) {
		this.picmd5 = picmd5;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getFriendname() {
		return friendname;
	}

	public void setFriendname(String friendname) {
		this.friendname = friendname;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Bitmap getTx_fread() {
		return tx_fread;
	}

	public void setTx_fread(Bitmap tx_fread) {
		this.tx_fread = tx_fread;
	}

	public boolean isIsfreand() {
		return isfreand;
	}

	public void setIsfreand(boolean isfreand) {
		this.isfreand = isfreand;
	}

	public Contact() {
		remark = "";
		F_PY = "~";
		T_PY = "~";
		spy = "";
		tx = null;
		searchlist4name = new TreeSet<String>();
		phonesList = new ArrayList<String>();
		avatarid = AvatarID.getAvatarID();
	}

	public String getMatchstr_mailist() {
		return matchstr_mailist;
	}

	public void setMatchstr_mailist(String matchstr_mailist) {
		this.matchstr_mailist = matchstr_mailist;
	}

	public String getMatchstr() {
		return matchstr;
	}

	public void setMatchstr(String matchstr) {
		this.matchstr = matchstr;
	}

	public String getSpy() {
		return spy;
	}

	public void setSpy(String spy) {
		this.spy = spy;
	}

	public Long getContractID() {
		return contractID;
	}

	public void setContractID(Long contractID) {
		this.contractID = contractID;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public ArrayList<String> getPhonesList() {
		return phonesList;
	}

	public String getF_PY() {
		return F_PY;
	}

	public void setF_PY(String f_PY) {
		F_PY = f_PY;
	}

	public TreeSet<String> getSearchlist4name() {
		return searchlist4name;
	}

	public Bitmap getTx() {
		return tx;
	}

	public void setTx(Bitmap tx) {
		this.tx = tx;
	}

	public String getT_PY() {
		return T_PY;
	}

	public void setT_PY(String t_PY) {
		T_PY = t_PY;
	}

	@Override
	public int compareTo(Contact another) {
		if (this.F_PY.equals(another.F_PY)) {
			if (this.T_PY.equals(another.T_PY)) {
				if (this.remark.equals(another.remark)) {
					return this.phonesList.get(0).compareTo(
							another.phonesList.get(0));
				} else {
					return this.remark.compareTo(another.remark);
				}
				 
			}
			return this.T_PY.compareTo(another.T_PY);
//			if (this.remark.equals(another.remark)) {
//				return this.phonesList.get(0).compareTo(
//						another.phonesList.get(0));
//			} else {
//				return this.remark.compareTo(another.remark);
//			}
		} else {
			return this.F_PY.compareTo(another.F_PY);
		}
	}

	public void receveheadimage() {
		if (tx != null) {
			tx.recycle();
			tx = null;
		}
		if (tx_fread != null) {
			tx_fread.recycle();
			tx_fread = null;
		}
	}

}
