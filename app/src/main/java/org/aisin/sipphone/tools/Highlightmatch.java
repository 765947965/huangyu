package org.aisin.sipphone.tools;

import org.aisin.sipphone.commong.Contact;

import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

public class Highlightmatch {

	public static void phonenumMatch(Contact contact, String phonenum,
			TextView textphone, String mysherchtext) {
		int phoneks = phonenum.indexOf(mysherchtext);
		if (phoneks > -1) {// 号码有匹配到 高亮显示
			int phonejs = phoneks + mysherchtext.length();
			Spanned spnd = Html.fromHtml("<font color=#808080>"
					+ phonenum.substring(0, phoneks)
					+ "</font><font color=#1160FD>"
					+ phonenum.substring(phoneks, phonejs)
					+ "</font><font color=#808080>"
					+ phonenum.substring(phonejs) + "</font>");
			textphone.setText(spnd);
		} else {
			textphone.setText(phonenum);
		}
	}

	public static void nameMatch(Contact contact, String name,
			TextView textname, String mysherchtext) {
		int numks = name.indexOf(mysherchtext);
		if (name.indexOf(mysherchtext) > -1) {// 跟汉字匹配了
			Spanned spnd = Html
					.fromHtml("<font color=#000000>" + name.substring(0, numks)
							+ "</font><font color=#1160FD>" + mysherchtext
							+ "</font><font color=#000000>"
							+ name.substring(numks + mysherchtext.length())
							+ "</font>");// 匹配到的汉字
											// 高亮显示
			textname.setText(spnd);
		} else if (contact.getSpy().indexOf(mysherchtext) > -1
				|| contact.getSpy().indexOf(mysherchtext.substring(0, 1)) > -1) {// 跟首字母拼音匹配了
			int numpyks = contact.getSpy().indexOf(mysherchtext);
			int numpyjs = 0;
			if (numpyks > 0) {// 如果是跟首字母组合匹配
				numpyjs = numpyks + mysherchtext.length();
			} else {// 跟拼音匹配的
				// 取搜索的首字母
				String ss_szm = mysherchtext.substring(0, 1);
				numpyks = contact.getSpy().indexOf(ss_szm);// 汉字高亮开始位置
				numpyjs = numpyks + 1;// 高亮结束位置
				char[] cars = contact.getSpy().toCharArray();
				for (int i = numpyks + 1; i < cars.length; i++) {// 看看首字母是否依次都在搜索字符里出现过，没出现了
																	// 跳出
					if (mysherchtext.indexOf(cars[i] + "") > -1) {
						numpyjs += 1;
					} else {
						break;
					}
				}
			}
			Spanned spnd = Html.fromHtml("<font color=#000000>"
					+ name.substring(0, numpyks)
					+ "</font><font color=#1160FD>"
					+ name.substring(numpyks, numpyjs)
					+ "</font><font color=#000000>" + name.substring(numpyjs)
					+ "</font>");// 匹配到的汉字
									// 高亮显示
			textname.setText(spnd);
		} else {// 没有匹配到汉字
			textname.setText(name);
		}

	}
}
