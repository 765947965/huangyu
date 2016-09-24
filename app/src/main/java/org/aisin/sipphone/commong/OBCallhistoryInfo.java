package org.aisin.sipphone.commong;

import java.io.Serializable;
import java.util.TreeSet;

import android.graphics.Bitmap;

public class OBCallhistoryInfo implements Comparable<OBCallhistoryInfo>,
		Serializable {
	private static final long serialVersionUID = -6919461967497580385L;
	private String phone;
	private TreeSet<CallhistoryInfo> set = new TreeSet<CallhistoryInfo>();
	private Bitmap headbitmap;

	@Override
	public int compareTo(OBCallhistoryInfo another) {
		// TODO Auto-generated method stub
		if (set.size() > 0 && another.set.size() > 0) {
			CallhistoryInfo thisclh = (CallhistoryInfo) this.set.first();
			CallhistoryInfo thisclh_an = (CallhistoryInfo) another.set.first();
			return -thisclh
					.getCall_time()
					.replaceAll("-", "")
					.replaceAll(" ", "")
					.replaceAll(":", "")
					.compareTo(
							thisclh_an.getCall_time().replaceAll("-", "")
									.replaceAll(" ", "").replaceAll(":", ""));
		}
		return -1;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public TreeSet<CallhistoryInfo> getSet() {
		return set;
	}

	public Bitmap getHeadbitmap() {
		return headbitmap;
	}

	public void setHeadbitmap(Bitmap headbitmap) {
		this.headbitmap = headbitmap;
	}

	public void receveheadImage() {
		if (headbitmap != null) {
			headbitmap.recycle();
			headbitmap = null;
		}
	}
}
