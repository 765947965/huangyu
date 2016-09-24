package org.aisin.sipphone.tools;

import java.util.TreeMap;

public class GetMusicName4PY {

	private static TreeMap<String, String> musicnames;

	public static TreeMap<String, String> getMusicNames() {
		if (musicnames != null) {
			return musicnames;
		}
		musicnames = new TreeMap<String, String>();
		musicnames.put("huanlesong", "欢乐颂");
		musicnames.put("mengzhongdehunli", "梦中的婚礼");
		musicnames.put("jiannan", "江南");
		musicnames.put("shinian", "十年");
		musicnames.put("tiankongzhichen", "天空之城");
		musicnames.put("molihua", "茉莉花");
		return musicnames;
	}
}
