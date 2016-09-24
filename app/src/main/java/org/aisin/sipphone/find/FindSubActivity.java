package org.aisin.sipphone.find;

import java.io.File;
import java.io.FileInputStream;
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
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FindSubActivity extends Activity implements OnClickListener {

	private LinearLayout removelayout;
	private LinearLayout find_load = null;
	private Context mContext;
	private List<Map<String, String>> items;
	private int position;
	private LinearLayout lin;
	private List<String> toUrl = null;
	private ImageView img_back;
	private TextView tv_title;
	private List<List<Map<String, String>>> itemsubs;
	private ProgressDialog dialog;
	private String version;
	private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if (items.size() != 0) {
					for (int i = 0; i < items.size(); i++) {
						Map<String, String> item = items.get(i);
						String name = "";
						Bitmap bitmap = null;
						try {
							bitmap = BitmapFactory.decodeStream(mContext
									.openFileInput("img_sub1_" + version
											+ position + i + ".png"));
							if (bitmap != null) {
								bitmaps.add(bitmap);
							}
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						LinearLayout linparent = getLinearLayout(mContext);

						LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
						lps.setMargins(0, 20, 0, 0);
						linparent.setLayoutParams(lps);
						find_load.addView(linparent);
						LinearLayout lin1 = getLinerLayOutSub(mContext, bitmap,
								name);
						linparent.addView(lin1);

						List<Map<String, String>> itemsub1 = itemsubs.get(i);
						for (int j = 0; j < itemsub1.size(); j++) {
							Map<String, String> itemsub2 = itemsub1.get(j);
							final String tosub = itemsub2.get("to");

							final String namesub = itemsub2.get("name");
							if (j % 4 == 0) {
								lin = getLinearLayout(mContext);
								find_load.addView(lin);
							}
							Bitmap bitmap1 = null;
							try {
								bitmap1 = BitmapFactory.decodeStream(mContext
										.openFileInput("img_sub11_" + version
												+ position + i + j + ".png"));
								if (bitmap1 != null) {
									bitmaps.add(bitmap1);
								}
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							LinearLayout linsub = getLinerLayOutSub(mContext,
									bitmap1, namesub);
							View view = getPaddingView(mContext);
							linsub.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(
											FindSubActivity.this,
											org.aisin.sipphone.setts.CheckWebView.class);
									intent.putExtra("flag_name", namesub);
									intent.putExtra("url_view", tosub);
									startActivity(intent);

								}
							});
							lin.addView(linsub);
							lin.addView(view);

						}

					}

				}

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_sub);
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
		itemsubs = new ArrayList<List<Map<String, String>>>();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.find_img_back)
			this.finish();

	}

	private List<Map<String, String>> paseSubPage(String result,
			final int position) {
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("数据加载中...");
		dialog.show();
		final List<Map<String, String>> subItems = new ArrayList<Map<String, String>>();
		try {
			JSONObject json = new JSONObject(result);
			version = json.optString(SharedPreferencesTools.SERVICE_PAGE_VER);
			JSONArray servicePages = json.getJSONArray("items");

			JSONObject servicePage = servicePages.getJSONObject(position);
			final JSONArray subPages = servicePage.getJSONArray("items");

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
							subItem.put("name", name);
							List<Map<String, String>> itemsub = new ArrayList<Map<String, String>>();
							JSONArray subsubs = subservicePage
									.getJSONArray("items");
							for (int j = 0; j < subsubs.length(); j++) {
								JSONObject subsubPage = null;
								subsubPage = subsubs.getJSONObject(j);
								Map<String, String> subsubItems = new HashMap<String, String>();
								String picsub = subsubPage
										.optString(SharedPreferencesTools.SERVICE_PAGE_PIC);
								String namesub = subsubPage
										.optString(SharedPreferencesTools.SERVICE_PAGE_NAME);
								String tosub = subsubPage
										.optString(SharedPreferencesTools.SERVICE_PAGE_TO);
								subsubItems.put("name", namesub);

								subsubItems.put("to", tosub);
								itemsub.add(subsubItems);
								File file = mContext
										.getFileStreamPath("img_sub11_"
												+ version + position + i + j
												+ ".png");
								if (!file.exists())
									HttpUtils.downloadImage(mContext, picsub,
											"img_sub11_" + version + position
													+ i + j + ".png");
							}

							itemsubs.add(itemsub);

							subItems.add(subItem);
							File file = mContext.getFileStreamPath("img_sub1_"
									+ version + position + i + ".png");
							if (!file.exists())
								HttpUtils.downloadImage(mContext, pic,
										"img_sub1_" + version + position + i
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

	/*
	 * private List<Map<String,String>> paseSubPage(String result,int position){
	 * final List<Map<String,String>>subItems=new
	 * ArrayList<Map<String,String>>(); try { JSONObject json = new
	 * JSONObject(result); JSONArray servicePages = json.getJSONArray("items");
	 * 
	 * JSONObject servicePage = servicePages.getJSONObject(position); final
	 * JSONArray subPages = servicePage.getJSONArray("items");
	 * Toast.makeText(mContext, "pages="+subPages.length(), 3000).show(); new
	 * Thread(new Runnable() {
	 * 
	 * @Override public void run() { for (int i = 0; i < subPages.length(); i++)
	 * { Map<String, String>subItem=new HashMap<String, String>(); JSONObject
	 * subservicePage; try { subservicePage = subPages.getJSONObject(i); String
	 * pic=subservicePage.optString(SharedPreferencesTools.SERVICE_PAGE_PIC);
	 * String
	 * name=subservicePage.optString(SharedPreferencesTools.SERVICE_PAGE_NAME);
	 * String
	 * to=subservicePage.optString(SharedPreferencesTools.SERVICE_PAGE_TO);
	 * subItem.put("name", name);
	 * 
	 * subItems.add(subItem); HttpUtils.downloadImage(mContext, pic, "img_sub1"
	 * + i + ".png"); } catch (JSONException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); }
	 * 
	 * } mHandler.sendEmptyMessage(1);
	 * 
	 * } }).start();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * } catch (Exception e) {
	 * 
	 * } Toast.makeText(mContext, "pagesSize="+subItems.size(), 3000).show();
	 * return subItems; }
	 */
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
		LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
		lps.setMargins(0, 0, 0, 20);
		linearLayout.setLayoutParams(lps);

		return linearLayout;

	}

	private View getPaddingView(Context context) {
		View view = new View(context);
		view.setLayoutParams(new LinearLayout.LayoutParams(2,
				LinearLayout.LayoutParams.MATCH_PARENT));
		return view;

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
