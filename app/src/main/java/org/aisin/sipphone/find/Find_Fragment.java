package org.aisin.sipphone.find;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.setts.HelpNow;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.GimageInfo;
import org.aisin.sipphone.dial.GImagePagerAdapter;
import org.aisin.sipphone.dial.GImagePagerAdapter.GPSONpause;
import org.aisin.sipphone.find.Http_task_servicepage.ServicePageListener;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.DisplayUtil;
import org.aisin.sipphone.tools.GetAction_reports;
import org.aisin.sipphone.tools.GetGImageMap;
import org.aisin.sipphone.tools.JumpMap;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

public class Find_Fragment extends Fragment implements OnPageChangeListener,
		OnClickListener, GPSONpause {
	private ViewPager find_ggtp;
	private boolean ggFlag = true;// 广告自动切换开关
	private GImagePagerAdapter gpa;
	private Thread mythread;
	private LinearLayout find_ggtp_title;
	private ImageView[] images;
	private TextView tv_title;
	private int width;
	private LinearLayout linear_find_aisin;
	private LinearLayout linearLayout;
	private AisinImageView aisin_mall_relayout;// 环宇商城
	private AisinImageView phone_recharge_relayout;// 手机充值
	private AisinImageView express_query_relayout;// 快递查询
	private AisinImageView attractions_tickets_relayout;// 景点门票
	private ArrayList<GimageInfo> ginfos = new ArrayList<GimageInfo>();
	private List<String> toUrl = new ArrayList<String>();
	private View view;
	private ArrayList<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
	int ncolorid = 0;
	private ArrayList<View> views = new ArrayList<View>();
	private String path, version;
	AisinImageView[] imageviews = new AisinImageView[8];
	private BitmapUtils bitmapUtils;
	private LinearLayout layout1, layout2, layout3, layout4;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 调整广告展示viewpager的展示页
				if (find_ggtp != null && gpa != null
						&& GetGImageMap.imagelist.size() > 1) {
					// 回滚到下一个页面
					find_ggtp.setCurrentItem(find_ggtp.getCurrentItem() + 1);
				}
			} else if (msg.what == 2) {// 更新广告
				if (GetGImageMap.imagelist == null
						|| GetGImageMap.imagelist.size() == 0) {
					return;
				}
				for (GimageInfo ginfo : GetGImageMap.imagelist) {
					if (ginfo.getBitmap() == null) {
						return;
					}
				}
				ginfos.clear();
				ginfos.addAll(GetGImageMap.imagelist);
				ginfos.add(0, ginfos.get(ginfos.size() - 1));
				ginfos.add(ginfos.get(1));
				if (gpa == null) {
					gpa = new GImagePagerAdapter(AisinActivity.context, ginfos,
							true, true);
					gpa.SetOnPauseLisen(Find_Fragment.this);
					find_ggtp.setAdapter(gpa);
					find_ggtp.setCurrentItem(1);
				} else {
					gpa.notifyDataSetChanged();
				}
				find_ggtp_title.removeAllViews();
				images = new ImageView[ginfos.size() - 2];
				ViewGroup.LayoutParams viewPagerImageViewParams = new ViewGroup.LayoutParams(
						30, 20);
				for (int i = 0; i < ginfos.size() - 2; i++) {
					ImageView mViewPagerImageView = new ImageView(
							AisinActivity.context);
					mViewPagerImageView
							.setLayoutParams(viewPagerImageViewParams);
					if (i == 0) {
						mViewPagerImageView
								.setImageResource(R.drawable.bootpagerchanged);
					} else {
						mViewPagerImageView
								.setImageResource(R.drawable.bootpagerunchanged);
					}
					images[i] = mViewPagerImageView;
					find_ggtp_title.addView(mViewPagerImageView);
				}
				if (mythread == null) {
					mythread = new Thread(new Runnable() {
						@Override
						public void run() {
							while (true) {
								try {
									Thread.sleep(3000);
									if (ggFlag) {
										mHandler.sendEmptyMessage(1);
									}
								} catch (Exception e) {
								}
							}
						}
					});
					mythread.start();
				}

			} else if (msg.what == 5) {
				SharedPreferences shared = SharedPreferencesTools
						.getSharedPreferences_ServicePage(AisinActivity.context);
				tv_title.setText(shared.getString(
						SharedPreferencesTools.SERVICE_PAGE_SERVICEPAGE_NAME,
						"发现"));
				version = shared.getString(
						SharedPreferencesTools.SERVICE_PAGE_VER, "1.0");
				String result = shared.getString(
						SharedPreferencesTools.SERVICE_Data, "");
				List<Map<String, String>> items = parseString(result);
				if (items == null) {
					return;
				}
				listItems.addAll(items);
				aisin_mall_relayout = (AisinImageView) view
						.findViewById(R.id.aisin_mall_relayout);
				phone_recharge_relayout = (AisinImageView) view
						.findViewById(R.id.phone_recharge_relayout);
				express_query_relayout = (AisinImageView) view
						.findViewById(R.id.express_query_relayout);
				attractions_tickets_relayout = (AisinImageView) view
						.findViewById(R.id.attractions_tickets_relayout);
				for (int i = 0; i < items.size(); i++) {
					Map<String, String> item = items.get(i);
					String name = item.get("name");
					String pic = item.get("pic");
					String to = item.get("to");
					toUrl.add(to);
					if (i >= 4) {
						if (i % 4 == 0) {
							linearLayout = getLinearLayout(AisinActivity.context);
							View view = getPaddingViewHor(AisinActivity.context);
							linear_find_aisin.addView(view);
							linear_find_aisin.addView(linearLayout);
							views.add(view);
							views.add(linearLayout);
						}
						AisinImageView aisinview = getAisinImageView(
								AisinActivity.context,
								width
										/ 4
										- DisplayUtil.dip2px(
												AisinActivity.context, 0.75f),
								width / 4);
						setAisinImagviewAttr(aisinview, name, pic, i, to, 0.5f);
						View viewc = getPaddingViewVer(AisinActivity.context);
						linearLayout.addView(aisinview);
						linearLayout.addView(viewc);
						continue;
					}
					if (i == 0) {
						setAisinImagviewAttr(aisin_mall_relayout, name, pic, i,
								to, 1f);
					} else if (i == 1) {
						setAisinImagviewAttr(phone_recharge_relayout, name,
								pic, i, to, 1f);

					} else if (i == 2) {
						setAisinImagviewAttr(express_query_relayout, name, pic,
								i, to, 0.5f);

					} else if (i == 3) {
						setAisinImagviewAttr(attractions_tickets_relayout,
								name, pic, i, to, 0.5f);

					}

				}

				if (items.size() % 4 != 0) {
					for (int i = 0; i < (4 - items.size() % 4); i++) {

						AisinImageView aisinview = getAisinImageView(
								AisinActivity.context,
								width
										/ 4
										- DisplayUtil.dip2px(
												AisinActivity.context, 0.75f),
								width / 4);
						linearLayout.addView(aisinview);
					}

				}
			} else if (msg.what == 6) {
				toUrl.clear();
				listItems.clear();
				for (View v : views) {
					linear_find_aisin.removeView(v);
				}
				views.clear();
				Http_task_servicepage servicePage = new Http_task_servicepage(
						AisinActivity.context);
				// 图片下载完成监听器
				servicePage.setCompleteListener(new ServicePageListener() {

					@Override
					public void compelete() {
						// 更新服务页
						mHandler.sendEmptyMessage(5);
					}
				});
				servicePage.execute("");
			} else if (msg.what == 7) {
				int scitem = msg.getData().getInt("scitem");
				find_ggtp.setCurrentItem(scitem, false);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			String FRAGMENTS_TAG = "android:support:fragments";
			// remove掉保存的Fragment
			savedInstanceState.remove(FRAGMENTS_TAG);
		}
		view = inflater.inflate(R.layout.find_fragmentlayout, container, false);
		bitmapUtils = new BitmapUtils(AisinActivity.context);
		path = AisinActivity.context.getFilesDir().getPath();
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager m = (WindowManager) AisinActivity.context
				.getSystemService(Context.WINDOW_SERVICE);
		m.getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		tv_title = (TextView) view.findViewById(R.id.find_title);
		find_ggtp = (ViewPager) view.findViewById(R.id.find_ggtp);
		find_ggtp_title = (LinearLayout) view
				.findViewById(R.id.find_ggtp_title);
		linear_find_aisin = (LinearLayout) view
				.findViewById(R.id.linear_find_aisin);
		layout1 = (LinearLayout) view.findViewById(R.id.layout1);
		layout2 = (LinearLayout) view.findViewById(R.id.layout2);
		layout3 = (LinearLayout) view.findViewById(R.id.layout3);
		layout4 = (LinearLayout) view.findViewById(R.id.layout4);
		layout1.setOnClickListener(this);
		layout2.setOnClickListener(this);
		layout3.setOnClickListener(this);
		layout4.setOnClickListener(this);
		find_ggtp.setOnPageChangeListener(this);
		mHandler.sendEmptyMessage(2);// 开启广告

		mHandler.sendEmptyMessage(6);
		return view;
	}

	public List<Map<String, String>> parseString(String result) {
		List<Map<String, String>> items = null;
		try {
			JSONObject json = new JSONObject(result);
			JSONArray servicePages = json.getJSONArray("items");
			items = new ArrayList<Map<String, String>>();
			for (int i = 0; i < servicePages.length(); i++) {
				Map<String, String> item = new HashMap<String, String>();
				JSONObject servicePage = servicePages.getJSONObject(i);
				String name = servicePage
						.optString(SharedPreferencesTools.SERVICE_PAGE_NAME);
				String to = servicePage
						.optString(SharedPreferencesTools.SERVICE_PAGE_TO);
				String pic = servicePage
						.optString(SharedPreferencesTools.SERVICE_PAGE_PIC);
				String has_sect = servicePage
						.optString(SharedPreferencesTools.SERVICE_PAGE_HAS_SUB_SECT);
				item.put("name", name);
				item.put("to", to);
				item.put("hasselect", has_sect);
				item.put("pic", pic);
				items.add(item);
			}
			return items;
		} catch (JSONException e) {
			return null;
		}

	}

	private void setAisinImagviewAttr(AisinImageView aisinImageview,
			final String name, String pic, final int location, String to,
			float bmatrix) {

		aisinImageview.setText(name);
		File file = new File(path + "/img_" + version + location + ".png");

		if (file.exists() && file.length() > 0) {
			Bitmap bttemp = BitmapFactory.decodeFile(path + "/img_" + version
					+ location + ".png");
			if (bttemp != null) {
				if (bmatrix > 0) {
					float sflv = (float) (bmatrix)
							* (width / 2f / (float) bttemp.getWidth());
					Matrix matrix = new Matrix();
					matrix.setScale(sflv, sflv);
					try {
						Bitmap bitmap_1 = Bitmap.createBitmap(bttemp, 0, 0,
								bttemp.getWidth(), bttemp.getHeight(), matrix,
								true);
						bttemp.recycle();
						bttemp = null;
						aisinImageview.setImageResource(bitmap_1);
					} catch (Error e) {
					}
				} else {
					aisinImageview.setImageResource(bttemp);
				}
			}
		} else
			bitmapUtils.display(aisinImageview.getImg_pic(), pic);

		aisinImageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String strurl = toUrl.get(location);

				Goto(strurl, location, name);

			}
		});

	}

	private Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		} else {
			return null;
		}
	}

	private void Goto(String startpager_to, int location, String name) {

		if (startpager_to != null && !"".equals(startpager_to)) {
			if (startpager_to.startsWith("http")) {
				// 跳转到URL
				if (startpager_to.endsWith(".mp4")) {
					Intent intent = new Intent(AisinActivity.context,
							org.aisin.sipphone.dial.LoadVideoActivity.class);
					intent.putExtra("url_view", startpager_to);
					AisinActivity.context.startActivity(intent);
				} else {

					String hasselect = listItems.get(location).get("hasselect");
					if (TextUtils.isEmpty(startpager_to)
							&& !TextUtils.isEmpty(hasselect)) {
						Intent intent = new Intent(AisinActivity.context,
								FindSubActivity.class);
						intent.putExtra("flag_name", name);
						intent.putExtra("position", location);
						startActivity(intent);

					} else if (TextUtils.isEmpty(startpager_to)
							&& TextUtils.isEmpty(hasselect)) {
						Intent intent = new Intent(AisinActivity.context,
								FindActivity.class);
						intent.putExtra("flag_name", name);
						intent.putExtra("position", location);
						startActivity(intent);
					}

					else {
						if (Check_network
								.isNetworkAvailable(AisinActivity.context)) {
							Intent intent = new Intent(AisinActivity.context,
									org.aisin.sipphone.setts.CheckWebView.class);
							intent.putExtra("flag_name", name);
							intent.putExtra("url_view", startpager_to);
							startActivity(intent);
						} else {
							new AisinBuildDialog(AisinActivity.context, "提示",
									"请检查你的网络!");
						}
					}

				}
			} else if (Check_format.Check_num(startpager_to.trim())) {
				// 匹配要跳转到的Activity
				if (startpager_to.trim().length() == 1) {
					int temp = Integer.parseInt(startpager_to.trim());
					if (temp >= 1 && temp <= 5) {
						Intent intent = new Intent(Constants.BrandName
								+ ".setTabSelection");
						intent.putExtra("position", temp);

						AisinActivity.context.sendBroadcast(intent);

					}
				} else {
					Intent intenttemp = JumpMap.getIntent(
							AisinActivity.context, startpager_to.trim());
					if (intenttemp != null) {
						this.startActivity(intenttemp);
					}
				}
			}
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// Viewpager滚动监听

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// Viewpager滚动监听
		if (arg1 == 0.0f) {
			ggFlag = true;
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 0) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() { //
					Message message = new Message();
					message.what = 7;
					Bundle bundle = new Bundle();
					bundle.putInt("scitem", ginfos.size() - 2);
					message.setData(bundle);
					mHandler.sendMessage(message);
				}
			}, 150);
			return;
		} else if (arg0 == ginfos.size() - 1) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() { //
					Message message = new Message();
					message.what = 7;
					Bundle bundle = new Bundle();
					bundle.putInt("scitem", 1);
					message.setData(bundle);
					mHandler.sendMessage(message);
				}
			}, 150);
			return;
		}
		try {
			GimageInfo ginfo = ginfos.get(arg0);
			GetAction_reports.ADDARSNUM(AisinActivity.context, ginfo.getAdid(),
					ginfo.getUrlprefix() + ginfo.getImagename(), 1, 0);
			if (arg0 == 0) {
				arg0 = ginfos.size() - 2;
			} else if (arg0 == ginfos.size() - 1) {
				arg0 = 0;
			} else {
				arg0 = arg0 - 1;
			}
			for (int i = 0; i < images.length; i++) {
				if (i == arg0) {
					images[arg0].setImageResource(R.drawable.bootpagerchanged);
				} else {
					images[i].setImageResource(R.drawable.bootpagerunchanged);
				}

			}

		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = new Intent(AisinActivity.context,
				org.aisin.sipphone.setts.CheckWebView.class);
		switch (id) {
		case R.id.layout1:
			String url = SharedPreferencesTools
					.getSharedPreferences_msglist_date_share(
							AisinActivity.context)
					.getString(
							SharedPreferencesTools.SPF_msglist_date_servicepage_company_url,
							"");
			intent.putExtra("flag_name", "环宇官网");
			intent.putExtra("url_view", url);
			startActivity(intent);
			break;
		case R.id.layout2:
			String ur2 = SharedPreferencesTools
					.getSharedPreferences_msglist_date_share(
							AisinActivity.context)
					.getString(
							SharedPreferencesTools.SPF_msglist_date_servicepage_recentevents_url,
							"");
			intent.putExtra("flag_name", "最新活动");
			intent.putExtra("url_view", ur2);
			startActivity(intent);
			break;
		case R.id.layout3:
			String ur3 = SharedPreferencesTools
					.getSharedPreferences_msglist_date_share(
							AisinActivity.context)
					.getString(
							SharedPreferencesTools.SPF_msglist_date_servicepage_noticeboard_url,
							"");

			intent.putExtra("flag_name", "公告栏");
			intent.putExtra("url_view", ur3);
			startActivity(intent);
			break;
		case R.id.layout4:
			String ur4 = SharedPreferencesTools
					.getSharedPreferences_msglist_date_share(
							AisinActivity.context)
					.getString(
							SharedPreferencesTools.SPF_msglist_date_servicepage_instructions_url,
							"");

			Intent intent2 = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.CheckWebView.class);
			intent2.putExtra("flag_name", "使用说明");
			intent2.putExtra("url_view", ur4);
			startActivity(intent2);
			break;
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ggFlag = false;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ggFlag = true;
	}

	public void ReSetViewPager() {// 重载ViewPager
		mHandler.sendEmptyMessage(2);
	}

	private AisinImageView getAisinImageView(Context context, int width,
			int height) {
		AisinImageView aisinImageView = new AisinImageView(context);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		aisinImageView.setLayoutParams(lp);

		return aisinImageView;

	}

	private LinearLayout getLinearLayout(Context context) {

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1.0f));
		return linearLayout;

	}

	public void upServerdata() {
		mHandler.sendEmptyMessage(6);
	}

	private View getPaddingViewVer(Context context) {
		View view = new View(context);
		view.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2px(
				AisinActivity.context, 1f),
				LinearLayout.LayoutParams.MATCH_PARENT));
		view.setBackgroundColor(Color.parseColor("#FFFFFF"));
		return view;
	}

	private View getPaddingViewHor(Context context) {
		View view = new View(context);
		view.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(
						AisinActivity.context, 1f)));
		view.setBackgroundColor(Color.parseColor("#FFFFFF"));
		return view;
	}

	@Override
	public void setStatic(boolean b) {
		// TODO Auto-generated method stub
		ggFlag = b;
	}

}
