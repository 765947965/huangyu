package org.aisin.sipphone.find;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FindActivity extends Activity implements OnClickListener {
	private LinearLayout removelayout;
	private LinearLayout find_load = null;
	private Context mContext;

	private List<Map<String, String>> items;
	private int position;
	private LinearLayout lin;
	private List<String> toUrl = null;
	private ImageView img_back;
	private TextView tv_title;
	private ProgressDialog dialog;
	private String version;
	private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if (items.size() != 0) {
					for (int i = 0; i < items.size(); i++) {
						Map<String, String> item = items.get(i);
						String to = item.get("to");
						toUrl.add(to);
						final String name = item.get("name");
						if (i % 3 == 0) {
							lin = getLinearLayout(mContext);
							find_load.addView(lin);
						}
						Bitmap bitmap = null;
						try {
							bitmap = BitmapFactory.decodeStream(mContext
									.openFileInput("img_sub_" + version
											+ position + i + ".png"));
							if (bitmap != null) {
								bitmaps.add(bitmap);
							}
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						final int positon = i;
						LinearLayout linsub = getLinerLayOutSub(mContext,
								bitmap, name);
						View view = getPaddingView(mContext);
						linsub.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(
										FindActivity.this,
										org.aisin.sipphone.setts.CheckWebView.class);
								intent.putExtra("flag_name", name);
								intent.putExtra("url_view", toUrl.get(position)
										.toString());
								startActivity(intent);

							}
						});
						lin.addView(linsub);
						lin.addView(view);

					}

					switch (items.size() % 3) {
					case 0:

						break;
					case 1:
						LinearLayout lins1 = getLinerLayOutSub(mContext, null,
								"");
						View viewpadding = getPaddingView(mContext);
						LinearLayout linsub11 = getLinerLayOutSub(mContext,
								null, "");

						lin.addView(lins1);
						lin.addView(viewpadding);
						lin.addView(linsub11);

						break;
					case 2:
						LinearLayout lins3 = getLinerLayOutSub(mContext, null,
								"");

						lin.addView(lins3);

						break;

					default:
						break;
					}

				}

			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_activity);
		removelayout = (LinearLayout) findViewById(R.id.removelayout);
		find_load = (LinearLayout) findViewById(R.id.find_load);
		mContext = this;
		position = getIntent().getIntExtra("position", 0);
		String title = getIntent().getStringExtra("flag_name");
		img_back = (ImageView) findViewById(R.id.find_img_back);
		tv_title = (TextView) findViewById(R.id.txt_title);
		img_back.setOnClickListener(this);
		tv_title.setText(title);
		String str = SharedPreferencesTools.getSharedPreferences_ServicePage(
				mContext).getString(SharedPreferencesTools.SERVICE_Data, "");
		items = paseSubPage(str, position);
		toUrl = new ArrayList<String>();

	}

	private List<Map<String, String>> paseSubPage(String result,
			final int position) {
		final List<Map<String, String>> subItems = new ArrayList<Map<String, String>>();
		try {
			JSONObject json = new JSONObject(result);
			JSONArray servicePages = json.getJSONArray("items");
			version = json.optString(SharedPreferencesTools.SERVICE_PAGE_VER);
			JSONObject servicePage = servicePages.getJSONObject(position);
			final JSONArray subPages = servicePage.getJSONArray("items");
			dialog = new ProgressDialog(this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage("数据加载中...");
			dialog.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < subPages.length(); i++) {
						Map<String, String> subItem = new HashMap<String, String>();
						JSONObject subservicePage;
						try {
							subservicePage = subPages.getJSONObject(i);
							String pic = subservicePage
									.optString(SharedPreferencesTools.SERVICE_PAGE_PIC);
							String name = subservicePage
									.optString(SharedPreferencesTools.SERVICE_PAGE_NAME);
							String to = subservicePage
									.optString(SharedPreferencesTools.SERVICE_PAGE_TO);
							subItem.put("name", name);
							subItem.put("to", to);
							subItem.put("pic", pic);
							subItems.add(subItem);
							File file = mContext.getFileStreamPath("img_sub_"
									+ version + position + i + ".png");
							if (!file.exists())
								HttpUtils.downloadImage(mContext, pic,
										"img_sub_" + version + position + i
												+ ".png");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					mHandler.sendEmptyMessage(1);

				}
			}).start();

		} catch (Exception e) {

		}

		return subItems;
	}

	private LinearLayout getLinerLayOutSub(Context context, Bitmap bitmap,
			String text) {
		LinearLayout view = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.find_subs, null);
		view.setLayoutParams(new LinearLayout.LayoutParams(0,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
		ImageView img = (ImageView) view.findViewById(R.id.imagesub);
		TextView tv_view = (TextView) view.findViewById(R.id.txt_sub);
		tv_view.setText(text);
		img.setImageBitmap(bitmap);

		return view;

	}

	private LinearLayout getLinearLayout(Context context) {

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
		params.setMargins(10, 10, 10, 10);
		linearLayout.setLayoutParams(params);

		return linearLayout;

	}

	private View getPaddingView(Context context) {
		View view = new View(context);
		view.setLayoutParams(new LinearLayout.LayoutParams(2,
				LinearLayout.LayoutParams.MATCH_PARENT));
		return view;

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.find_img_back)
			this.finish();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容器
		}
		for (Bitmap bt : bitmaps) {
			if (bt != null) {
				bt.recycle();
			}
		}
		bitmaps.clear();
		bitmaps = null;
		System.gc();
	}
}
