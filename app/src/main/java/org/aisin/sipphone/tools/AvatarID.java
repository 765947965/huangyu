package org.aisin.sipphone.tools;

import java.util.Random;

import org.aisin.sipphone.tianyu.R;

public class AvatarID {

	private static int[] ids = { R.drawable.avatar1, R.drawable.avatar2,
			R.drawable.avatar3, R.drawable.avatar4 };
	private static Random random = new Random();

	public static int getAvatarID() {
		return ids[random.nextInt(ids.length)];
	}

}
