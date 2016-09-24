package org.aisin.sipphone.dial;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.aisin.sipphone.commong.GimageInfo;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.GetAction_reports;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class GImagePagerAdapter extends PagerAdapter {
	private Context context;
	private ArrayList<GimageInfo> imagelist;
	private boolean Flag;// true强制拉伸填满,false自动填充
	private boolean advposition;// true广告位于屏幕上端，false广告位于下端
	private GPSONpause gsp;

	public GImagePagerAdapter(final Context context,
			ArrayList<GimageInfo> imagelist, boolean Flag, boolean advposition) {
		this.context = context;
		this.imagelist = imagelist;
		this.Flag = Flag;
		this.advposition = advposition;
	}

	@Override
	public int getCount() {
		return imagelist.size();
	}

	public void SetOnPauseLisen(GPSONpause gsp) {
		this.gsp = gsp;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		final GimageInfo gigi = imagelist.get(position);
		ImageView mViewPagerImageView = new ImageView(context);
		ViewGroup.LayoutParams viewPagerImageViewParams = null;
		if (Flag) {
			viewPagerImageViewParams = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			mViewPagerImageView.setScaleType(ScaleType.FIT_XY);
		} else {
			viewPagerImageViewParams = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		final int locaiton = position;
		mViewPagerImageView.setLayoutParams(viewPagerImageViewParams);
		mViewPagerImageView.setImageBitmap(gigi.getBitmap());
		mViewPagerImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 广告点击数加一
				GetAction_reports.ADDARSNUM(context, gigi.getAdid(),
						gigi.getUrlprefix() + gigi.getImagename(), 0, 1);
				if (gigi.getTourl() == null
						|| "".equals(gigi.getTourl().trim())) {
					return;
				}
				if ("1".equals(gigi.getTourl().trim())) {
					Intent intent = new Intent(context,
							org.aisin.sipphone.setts.SetRecharge.class);
					context.startActivity(intent);
				} else if ("2".equals(gigi.getTourl().trim())) {
					Intent intent = new Intent(context,
							org.aisin.sipphone.setts.SetInviteriends.class);
					context.startActivity(intent);
				} else if ("3".equals(gigi.getTourl().trim())) {
					Intent intent = new Intent(context,
							org.aisin.sipphone.setts.SetAboutaisin.class);
					context.startActivity(intent);
				} else {

					if (gigi.getTourl().endsWith(".mp4")) {
						Intent intent = new Intent(context,
								LoadVideoActivity.class);
						Bitmap bitmap = gigi.getBitmap();
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						// 将Bitmap压缩成PNG编码，质量为100%存储
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);// 除了PNG还有很多常见格式，如jpeg等。
						byte[] bytes = os.toByteArray();

						intent.putExtra("bitmap", bytes);
						intent.putExtra("url_view", gigi.getTourl());
						intent.putExtra("position", locaiton);
						intent.putExtra("advRedict", advposition); // 动画的方向

						intent.putExtra("width", bitmap.getWidth());
						intent.putExtra("height", bitmap.getHeight());
						context.startActivity(intent);

					} else {
						if (Check_network.isNetworkAvailable(context)) {
							Intent intent = new Intent(context,
									org.aisin.sipphone.setts.CheckWebView.class);
							intent.putExtra("url_view", gigi.getTourl());
							context.startActivity(intent);
						} else {
							new AisinBuildDialog(context, "提示",
									"网络连接不可用,请检查你的网络");
						}
					}
				}
			}
		});
		mViewPagerImageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (gsp != null) {
						gsp.setStatic(false);
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					if (gsp != null) {
						gsp.setStatic(true);
					}
				}
				return false;
			}
		});
		container.addView(mViewPagerImageView);
		return mViewPagerImageView;
	}

	public interface GPSONpause {
		void setStatic(boolean b);
	}

}
