package org.aisin.sipphone.setts;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.GetMusicName4PY;
import org.aisin.sipphone.tools.KeyBoardSoundPools;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetKeyboardMusicAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<String> musicnames;
	private String checkmusicname;
	private int colorh = Color.parseColor("#BDBEBD");
	private int colorw = Color.parseColor("#ffffff");
	private TreeMap<String, String> musicnames4py;
	private boolean colsem = true;
	private boolean isplay = false;
	private String[] MP;// 谱
	private int MSTOP;// 当前音频已经播放到的位置位置
	private boolean[] arms;

	public void setisplayover() {
		for (int i = 0; i < arms.length; i++) {
			arms[i] = false;
		}
		colsem = false;
		isplay = false;
	}

	public SetKeyboardMusicAdapter(Context context, ArrayList<String> musicnames) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.musicnames = musicnames;
		this.musicnames4py = GetMusicName4PY.getMusicNames();
		this.checkmusicname = SharedPreferencesTools
				.getSharedPreferences_4KEYMUSIC(context).getString(
						SharedPreferencesTools.KEYMUSIC_KEY, "无");
		this.arms = new boolean[musicnames.size()];
		for (int i = 0; i < this.arms.length; i++) {
			this.arms[i] = false;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (colsem) {
					try {
						Thread.sleep(200);
						if (!isplay) {
							continue;
						}
						if (MP == null
								|| KeyBoardSoundPools
										.getSoundPools(SetKeyboardMusicAdapter.this.context) == null) {
							continue;
						}
						for (String stt : MP[MSTOP].split("m")) {
							Integer soundsidtemp = KeyBoardSoundPools
									.getSoundsIDs().get(stt);
							if (soundsidtemp != null) {
								KeyBoardSoundPools.getSoundPools(
										SetKeyboardMusicAdapter.this.context)
										.play(soundsidtemp, 1, 1, 1, 0, 1.0f);
							}
						}
						MSTOP += 1;
						if (MSTOP >= MP.length) {
							isplay = false;
							for (int i = 0; i < arms.length; i++) {
								arms[i] = false;
							}
						}
					} catch (Exception e) {
					}
				}
			}
		}).start();
	}

	@Override
	public void notifyDataSetChanged() {
		checkmusicname = SharedPreferencesTools.getSharedPreferences_4KEYMUSIC(
				context).getString(SharedPreferencesTools.KEYMUSIC_KEY, "无");
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return musicnames.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolderMusic holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.setkeyboardmusicadapter,
					parent, false);
			holder = new ViewHolderMusic();
			holder.musicname = (TextView) convertView
					.findViewById(R.id.musicname);
			holder.checked = (TextView) convertView.findViewById(R.id.checked);
			holder.playmusic = (ImageView) convertView
					.findViewById(R.id.playmusic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderMusic) convertView.getTag();
		}
		final String musicname = musicnames.get(position);
		String musicname_cn = musicnames4py.get(musicname);
		if (musicname_cn != null) {
			holder.musicname.setText(musicname_cn);
		} else {
			holder.musicname.setText(musicname);
		}
		if ("无".equals(musicname)) {
			holder.playmusic.setVisibility(View.INVISIBLE);
		} else {
			holder.playmusic.setVisibility(View.VISIBLE);
		}
		if (checkmusicname.equals(musicname)) {
			holder.checked.setText("已启用");
			holder.checked.setTextColor(colorh);
			holder.checked.setBackgroundResource(R.drawable.checkmusicy);
		} else {
			holder.checked.setText("启用");
			holder.checked.setTextColor(colorw);
			holder.checked.setBackgroundResource(R.drawable.checkmusicno);
		}
		final ViewHolderMusic holderf = holder;
		final int positionf = position;
		final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == 1) {
					holderf.playmusic.setImageResource(R.drawable.music_icon);
				} else if (msg.what == 2) {
					holderf.playmusic.setImageResource(R.drawable.animation1);
					AnimationDrawable animationDrawable = (AnimationDrawable) holderf.playmusic
							.getDrawable();
					animationDrawable.start();
				}
			}
		};
		holder.playmusic.setOnClickListener(new OnClickListener() {
			boolean b = true;

			@Override
			public void onClick(View v) {
				if (KeyBoardSoundPools.getSoundPools(context) == null) {
					return;
				}
				if (b) {
					b = false;
					isplay = false;
					for (int i = 0; i < arms.length; i++) {
						arms[i] = false;
					}
					arms[positionf] = true;
					try {
						MP = null;
						MSTOP = 0;
						if ("无".equals(musicname)) {
							return;
						}
						AssetManager asmg = context.getAssets();
						InputStream inputs = asmg.open(Constants.musicp + "/"
								+ musicname);
						StringBuffer out = new StringBuffer();
						byte[] bytes = new byte[1024];
						for (int n; (n = inputs.read(bytes)) != -1;) {
							out.append(new String(bytes, 0, n));
						}
						inputs.close();
						MP = out.toString().split("y");
						isplay = true;
						mHandler.sendEmptyMessage(2);
						new Thread(new Runnable() {
							@Override
							public void run() {
								while (true) {
									try {
										Thread.sleep(200);
										if (!arms[positionf]) {
											b = true;
											mHandler.sendEmptyMessage(1);
											return;
										}
									} catch (Exception e) {
									}
								}
							}
						}).start();
					} catch (Exception e) {
					}
				} else {
					b = true;
					isplay = false;
					mHandler.sendEmptyMessage(1);
				}
			}
		});
		return convertView;
	}
	class ViewHolderMusic {
		TextView musicname;
		TextView checked;
		ImageView playmusic;
	}
}


