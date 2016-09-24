package org.aisin.sipphone.tools;

import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class KeyBoardSoundPools {

	private static boolean MusicIsOk = false;

	private static SoundPool soundpool;
	private static TreeMap<String, Integer> soundids;

	public static SoundPool getSoundPools(Context context) {// 音乐准备好了 返回的非空 没准备好
															// 返回null;
		if (!MusicIsOk) {
			if (soundpool == null) {
				SetSoundPools(context);
			}
			return null;
		} else {
			return soundpool;
		}
	}

	public static TreeMap<String, Integer> getSoundsIDs() {
		return soundids;
	}

	private static void SetSoundPools(Context context) {
		try {
			if (soundpool != null) {
				soundpool.release();
				soundids.clear();
				soundpool = null;
				soundids = null;
			}
			final long ll1 = System.currentTimeMillis();
			Log.i("环宇", "ll1::" + ll1);
			AssetManager asmg = context.getAssets();
			if (android.os.Build.VERSION.SDK_INT < 21) {
				soundpool = new SoundPool(88, AudioManager.STREAM_MUSIC, 0);
			} else {
				soundpool = createSoundPoolWithBuilder();
			}
			soundids = new TreeMap<String, Integer>();
			for (int i = 1; i <= 88; i++) {
				soundids.put(
						i + "",
						soundpool.load(
								asmg.openFd(Constants.musicogg + "/p_" + i
										+ ".ogg"), 1));
			}
			soundpool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId,
						int status) {
					if (sampleId >= 88) {
						MusicIsOk = true;
						long ll2 = System.currentTimeMillis();
						Log.i("环宇", "ll2::" + ll2);
						Log.i("环宇", "LODINGOK::" + (ll2 - ll1));
					}
				}
			});
		} catch (Exception e) {
		}
	}

	@SuppressLint("NewApi")
	protected static SoundPool createSoundPoolWithBuilder() {
		AudioAttributes attributes = new AudioAttributes.Builder()
				.setUsage(AudioAttributes.USAGE_GAME)
				.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
				.build();
		return new SoundPool.Builder().setAudioAttributes(attributes)
				.setMaxStreams(88).build();
	}

}
