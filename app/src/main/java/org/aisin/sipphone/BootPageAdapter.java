package org.aisin.sipphone;

import java.util.ArrayList;

import org.aisin.sipphone.tools.ReleaseImageViewBitmap;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class BootPageAdapter extends PagerAdapter {

	private Context context;
	private ArrayList<Integer> ids;
	private Handler handler;

	BootPageAdapter(Context context, ArrayList<Integer> ids, Handler handler) {
		this.context = context;
		this.ids = ids;
		this.handler = handler;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		ReleaseImageViewBitmap.releaseImageViewResouce((ImageView) object);
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		ImageView mViewPagerImageView = new ImageView(context);
		ViewGroup.LayoutParams viewPagerImageViewParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		mViewPagerImageView.setScaleType(ScaleType.FIT_XY);
		mViewPagerImageView.setLayoutParams(viewPagerImageViewParams);
		mViewPagerImageView.setImageBitmap(BitmapFactory.decodeResource(
				context.getResources(), ids.get(position)));
		container.addView(mViewPagerImageView);
		if (position == ids.size() - 1) {
			mViewPagerImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 记录这个版本的引导页已经展示过了
					PackageManager manager = context.getPackageManager();
					PackageInfo info;
					String version = "1.0";
					try {
						info = manager.getPackageInfo(context.getPackageName(),
								0);
						version = info.versionName;
					} catch (NameNotFoundException e) {
					}
					SharedPreferencesTools
							.getSharedPreferences_4bootpager(context).edit()
							.putBoolean(version, false).commit();

					handler.sendEmptyMessage(1);
				}
			});
		}
		return mViewPagerImageView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ids.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

}
