package org.aisin.sipphone.tools;

import java.util.Map.Entry;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;

public class JumpMap {
	private static TreeMap<String, Intent> map;

	public static Intent getIntent(Context context, String to) {
		Intent intent = null;

		int times = to.length() - 2;
		for (int i = 0; i <= times; i++) {
			String tempstr = to.substring(0, 2 + i);
			for (Entry<String, Intent> ent : getMap(context).entrySet()) {
				if (tempstr.equals(ent.getKey())) {
					intent = ent.getValue();
				}
			}
		}
		return intent;
	}

	private static TreeMap<String, Intent> getMap(Context context) {
		if (map != null) {
			return map;
		} else {
			map = new TreeMap<String, Intent>();
			map.put("51", new Intent(context,
					org.aisin.sipphone.setts.SetWallet.class));
			map.put("52", new Intent(context,
					org.aisin.sipphone.setts.SetRecharge.class));
			map.put("53", new Intent(context,
					org.aisin.sipphone.setts.MyRedEnvelope.class));
			map.put("54", new Intent(context,
					org.aisin.sipphone.setts.SetAccount.class));
			map.put("55", new Intent(context,
					org.aisin.sipphone.setts.CallOutSet.class));
			map.put("56", new Intent(context,
					org.aisin.sipphone.setts.SetInviteriends.class));
			map.put("57", new Intent(context,
					org.aisin.sipphone.setts.SetAboutaisin.class));
			return map;
		}
	}
}
