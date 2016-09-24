package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.DisplayUtil;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class SetKeybackAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private String[] path;
	private BitmapUtils bitmapUtils;
	private String morenbk;
	private Handler mHandler;
	private boolean zdyflag;

	public SetKeybackAdapter(Context context, Handler mHandler,
			BitmapUtils bitmapUtils) {
		this.context = context;
		this.mHandler = mHandler;
		this.inflater = LayoutInflater.from(context);
		this.bitmapUtils = bitmapUtils;
		SharedPreferences sp = SharedPreferencesTools
				.getSharedPreferences_4keybackground(context);
		morenbk = sp.getString(SharedPreferencesTools.KBG_KEY,
				Constants.KEYBACK_MR);
		zdyflag = sp.getBoolean(SharedPreferencesTools.KBG_ZDY, false);
		try {
			path = context.getAssets().list(Constants.KEYBACK);
		} catch (Exception e) {
		}
	}

	@Override
	public void notifyDataSetChanged() {
		SharedPreferences sp = SharedPreferencesTools
				.getSharedPreferences_4keybackground(context);
		morenbk = sp.getString(SharedPreferencesTools.KBG_KEY,
				Constants.KEYBACK_WRIGHT);
		zdyflag = sp.getBoolean(SharedPreferencesTools.KBG_ZDY, false);
		try {
			path = context.getAssets().list(Constants.KEYBACK);
		} catch (Exception e) {
		}
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (path != null) {
			if (path.length % 2 != 0) {
				return path.length / 2 + 1;
			} else {
				return path.length / 2;
			}
		} else {
			return 0;
		}
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
		ViewHolder_setkey holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.setkeybackadapter, parent,
					false);
			holder = new ViewHolder_setkey();
			holder.bcimage_0 = (ImageView) convertView
					.findViewById(R.id.bcimage_0);
			holder.bcimagecheck_0 = (ImageView) convertView
					.findViewById(R.id.bcimagecheck_0);
			holder.bcimage_1 = (ImageView) convertView
					.findViewById(R.id.bcimage_1);
			holder.bcimagecheck_1 = (ImageView) convertView
					.findViewById(R.id.bcimagecheck_1);
			if (PhoneInfo.width != 0) {
				int hieght = (int) ((float) ((PhoneInfo.width / 2 - DisplayUtil
						.dip2px(context, 22f)) * 367) / 480f);
				LayoutParams lp = holder.bcimage_0.getLayoutParams();
				lp.height = hieght;
				holder.bcimage_0.setLayoutParams(lp);
				holder.bcimage_1.setLayoutParams(lp);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder_setkey) convertView.getTag();
		}
		final String filename0 = path[position * 2];
		bitmapUtils.display(holder.bcimage_0, "assets/" + Constants.KEYBACK
				+ "/" + filename0);
		holder.bcimage_0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Constants.KEYBACK_ZDY.equals(filename0)) {
					// 自定义
					mHandler.sendEmptyMessage(2);
					return;
				}
				if (morenbk.equals(filename0)) {
					return;
				}
				Intent intent = new Intent(context,
						org.aisin.sipphone.setts.ShowSetKeyback.class);
				intent.putExtra("filename", filename0);
				context.startActivity(intent);
			}
		});
		if (!morenbk.equals(filename0)) {
			holder.bcimagecheck_0.setVisibility(View.INVISIBLE);
		} else {
			holder.bcimagecheck_0.setVisibility(View.VISIBLE);
		}
		if (zdyflag && Constants.KEYBACK_ZDY.equals(filename0)) {
			holder.bcimagecheck_0.setVisibility(View.VISIBLE);
		}
		if (path.length > position * 2 + 1) {
			final String filename1 = path[position * 2 + 1];
			bitmapUtils.display(holder.bcimage_1, "assets/" + Constants.KEYBACK
					+ "/" + filename1);
			holder.bcimage_1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (Constants.KEYBACK_ZDY.equals(filename1)) {
						// 自定义
						mHandler.sendEmptyMessage(2);
						return;
					}
					if (morenbk.equals(filename1)) {
						return;
					}
					Intent intent = new Intent(context,
							org.aisin.sipphone.setts.ShowSetKeyback.class);
					intent.putExtra("filename", filename1);
					context.startActivity(intent);
				}
			});
			if (!morenbk.equals(filename1)) {
				holder.bcimagecheck_1.setVisibility(View.INVISIBLE);
			} else {
				holder.bcimagecheck_1.setVisibility(View.VISIBLE);
			}
			if (zdyflag && Constants.KEYBACK_ZDY.equals(filename1)) {
				holder.bcimagecheck_1.setVisibility(View.VISIBLE);
			}
		} else {
			holder.bcimage_1.setVisibility(View.INVISIBLE);
			holder.bcimagecheck_1.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

}

class ViewHolder_setkey {
	ImageView bcimage_0;
	ImageView bcimagecheck_0;
	ImageView bcimage_1;
	ImageView bcimagecheck_1;
}
