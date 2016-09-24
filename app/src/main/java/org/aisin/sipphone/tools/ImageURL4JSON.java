package org.aisin.sipphone.tools;

import java.util.ArrayList;
import java.util.List;

import org.aisin.sipphone.commong.ImageInfo;
import org.json.JSONArray;
import org.json.JSONObject;

public class ImageURL4JSON {
	public static List<ImageInfo> getImageUrlList(String json) {
		List<ImageInfo> list = new ArrayList<ImageInfo>();
		try {
			JSONObject dataJson = new JSONObject(json.trim());
			String urlprefix = dataJson.getString("urlprefix");
			JSONArray pn = dataJson.getJSONArray("pn");
			for (int i = 0; i < pn.length(); i++) {
				JSONObject tp = pn.getJSONObject(i);
				ImageInfo imif = new ImageInfo();
				imif.setDLURL(urlprefix.trim() + tp.getString("n").trim());
				imif.setTo(tp.getString("to").trim());
				imif.setName(tp.getString("n").trim());
				list.add(imif);
			}
			return list;
		} catch (Exception e) {
			return null;
		}
	}
}
