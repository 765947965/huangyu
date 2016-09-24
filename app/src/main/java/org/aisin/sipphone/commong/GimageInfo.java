package org.aisin.sipphone.commong;

import android.graphics.Bitmap;

public class GimageInfo {
	private String imagename;
	private String tourl;
	private Bitmap bitmap;
	private String adid;
	private String urlprefix;

	public GimageInfo(String imagename, String tourl, Bitmap bitmap,String adid,String urlprefix) {
		super();
		this.imagename = imagename;
		this.tourl = tourl;
		this.bitmap = bitmap;
		this.adid = adid;
		this.urlprefix = urlprefix;
	}

	public String getImagename() {
		return imagename;
	}

	public String getTourl() {
		return tourl;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public String getAdid() {
		return adid;
	}

	public String getUrlprefix() {
		return urlprefix;
	}

	public int getbmpW() {
		if (bitmap != null) {
			return bitmap.getWidth();
		}
		return 0;
	}

	public int getbmpH() {
		if (bitmap != null) {
			return bitmap.getHeight();
		}
		return 0;
	}
}
