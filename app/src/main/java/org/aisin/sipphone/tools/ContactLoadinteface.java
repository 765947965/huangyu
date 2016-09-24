package org.aisin.sipphone.tools;

import java.util.TreeMap;
import java.util.TreeSet;

import org.aisin.sipphone.commong.Contact;

public interface ContactLoadinteface {
	void DoadDown_map(TreeMap<Long, Contact> cttmap);

	void headimagedown(boolean upflag);

	void upfrendsdwon(boolean upflag);

	void showfriends(boolean upflag);
}
