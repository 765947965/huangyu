package org.aisin.sipphone;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.myview.CircleImageView;
import org.aisin.sipphone.sqlitedb.RedData_DBHelp;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.BitMapcreatPath2smallSize;
import org.aisin.sipphone.tools.CharTools;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.CursorTools;
import org.aisin.sipphone.tools.DisplayUtil;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RedDialog extends Activity implements OnClickListener {
	private TextView sendfromname_text;// 发红包者
	private TextView sendfromname_messages_text;// 红包名称
	private AlwaysMarqueeTextView red_tips_text;// 红包详情tips
	private CircleImageView sendfromname_image;// 发红包这头像
	private ImageView red_anim_image;// 拆红包动画
	private ImageView red_closedbt;// 关闭按钮
	private LinearLayout reddialog_linlayout;
	private LinearLayout reddial_imagell;// 红包部分
	private TextView red_errortext;
	private View reddiloag_top;
	private View reddiloag_botton;
	private String aoutup;// 是否需要主动联网刷新红包数据
	// 红包数据相关字段
	private String from;
	private String fromnickname;
	private String gift_id;
	private String name;
	private String tips;
	private String gift_type;
	private String anim;
	private byte[] bitmapByte;
	private Bitmap bitmap;

	private boolean credflag = false;
	private AnimationDrawable animationDrawable;
	private SimpleDateFormat sdformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Animation anim = null;
				if (Constants.showbaidumap) {
					anim = AnimationUtils.loadAnimation(RedDialog.this,
							R.anim.anim_9);
				} else {
					anim = AnimationUtils.loadAnimation(RedDialog.this,
							R.anim.anim_1);
				}
				reddialog_linlayout.startAnimation(anim);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(3);
					}
				}, 610);
			} else if (msg.what == 2) {
				Animation anim = AnimationUtils.loadAnimation(RedDialog.this,
						R.anim.anim_2);
				reddialog_linlayout.startAnimation(anim);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(3);
					}
				}, 160);
			} else if (msg.what == 3) {
				RedDialog.this.finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reddialoglayout);
		aoutup = getIntent().getStringExtra("aoutup");

		from = getIntent().getStringExtra("from");
		fromnickname = getIntent().getStringExtra("fromnickname");
		gift_id = getIntent().getStringExtra("gift_id");
		name = getIntent().getStringExtra("name");
		tips = getIntent().getStringExtra("tips");
		gift_type = getIntent().getStringExtra("gift_type");
		anim = getIntent().getStringExtra("anim");
		bitmapByte = this.getIntent().getByteArrayExtra("bitmapByte");
		if (gift_id == null || "".equals(gift_id)) {
			finish();
			return;
		}
		reddiloag_top = this.findViewById(R.id.reddiloag_top);
		reddiloag_botton = this.findViewById(R.id.reddiloag_botton);
		reddial_imagell = (LinearLayout) this
				.findViewById(R.id.reddial_imagell);
		reddialog_linlayout = (LinearLayout) this
				.findViewById(R.id.reddialog_linlayout);
		sendfromname_text = (TextView) this
				.findViewById(R.id.sendfromname_text);
		sendfromname_messages_text = (TextView) this
				.findViewById(R.id.sendfromname_messages_text);
		red_tips_text = (AlwaysMarqueeTextView) this
				.findViewById(R.id.red_anim_text);
		red_tips_text.setOnClickListener(this);
		red_errortext = (TextView) this.findViewById(R.id.red_errortext);
		sendfromname_image = (CircleImageView) this
				.findViewById(R.id.sendfromname_image);
		red_anim_image = (ImageView) this.findViewById(R.id.red_anim_image);
		red_closedbt = (ImageView) this.findViewById(R.id.red_closedbt);
		red_closedbt.setOnClickListener(this);
		reddiloag_top.setOnClickListener(this);
		reddiloag_botton.setOnClickListener(this);
		if (bitmapByte != null) {
			bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0,
					bitmapByte.length);
			sendfromname_image.setImageBitmap(bitmap);
		} else {
			File file = this.getFileStreamPath(from + "headimage.jpg");
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				if (bitmap != null) {
					sendfromname_image.setImageBitmap(bitmap);
				}
			}
		}
		// 设置发红包方
		if (fromnickname != null && !"".equals(fromnickname.trim())) {
			sendfromname_text.setText(fromnickname.trim());
		} else {
			if (from != null && !"".equals(from.trim())) {
				if ("system".equals(from.trim())) {
					sendfromname_text.setText("环宇");
				} else {
					// 匹配通讯录或者UID
					boolean tempf = true;
					// 匹配通讯录
					for (Long lg : CursorTools.cttmap.keySet()) {
						Contact ctt = CursorTools.cttmap.get(lg);
						if (ctt == null) {
							continue;
						}
						for (String strp : ctt.getPhonesList()) {
							if (from.trim().equals(strp.trim())) {
								if (ctt.getRemark() != null
										&& !"".equals(ctt.getRemark())) {
									sendfromname_text.setText(ctt.getRemark());
									tempf = false;// 成功匹配到 置为假
									break;
								}
							}
						}
					}
					// 匹配UID
					// 都没有匹配成功直接设置号码
					if (tempf) {
						// 没有成功匹配到
						sendfromname_text.setText(from.trim());
					}
				}
			}
		}
		if (!name.isEmpty()) {
			sendfromname_messages_text.setText("给您发来" + name);
		}
		red_tips_text.setText(tips);

		int t_sp = (int) (CharTools.getStringLength(tips) * 18);
		int t_px = DisplayUtil.sp2px(this, t_sp);
		if (t_px >= PhoneInfo.width * 0.8) {
			// 改变颜色
			// red_tips_text.setTextColor(Color.parseColor("#00BFFF"));
		}

		red_anim_image.setImageResource(R.drawable.rpopen);
		red_anim_image.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.reddiloag_top:
			animfinish();
			break;
		case R.id.reddiloag_botton:
			animfinish();
			break;
		case R.id.red_closedbt:
			animfinish();
			break;
		case R.id.red_anim_text:
			Intent intent = new Intent(RedDialog.this,
					org.aisin.sipphone.setts.RedDlTextShow.class);
			intent.putExtra("tips", tips);
			RedDialog.this.startActivity(intent);
			break;
		case R.id.red_anim_image:
			red_anim_image.setEnabled(false);
			red_errortext.setText("");
			// 开启动画
			try {
				red_anim_image.setImageResource(R.anim.redcheckoutanim);
				animationDrawable = (AnimationDrawable) red_anim_image
						.getDrawable();
				animationDrawable.start();
			} catch (Exception e) {
				red_anim_image.setImageResource(R.drawable.rpopen);
			} catch (Error e) {
				red_anim_image.setImageResource(R.drawable.rpopen);
			}
			// 拆红包
			new HttpTask_Red_checkout().execute("checkout");
			break;
		default:
			break;
		}
	}

	private void animfinish() {
		if (anim != null && "diaoluo".equals(anim)) {
			mHandler.sendEmptyMessage(1);
		} else {
			mHandler.sendEmptyMessage(2);
		}
	}

	private class HttpTask_Red_checkout extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
			}
			// 获取拆红包的URl
			String url = URLTools.GetHttpURL_4RedDaily_CheckOUT_Url(
					RedDialog.this, gift_id, "open");
			String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject json = null;
			int doresult = -14;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer
							.parseInt(json.optString("result", "-14"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			switch (doresult) {
			case 0:
				String award_money = json.optString("award_money", "");
				String fee_rate = json.optString("fee_rate", "");

				if (aoutup != null && "aoutup".equals(aoutup)) {
					// 存储更改了的红包信息 因为这是从启动界面进来的
					RedObject red = RedData_DBHelp.GetRedData(RedDialog.this,
							gift_id);
					if (red != null) {
						red.setHas_open(1);
						red.setMoney(award_money);
						red.setOpen_time(sdformat.format(new Date()));
						RedData_DBHelp.addRedDatas(RedDialog.this, red);
					}
					SharedPreferencesTools
							.getSharedPreferences_4RED_COUT(RedDialog.this)
							.edit()
							.putBoolean(SharedPreferencesTools.REDCOUT_key,
									false)
							.putInt(SharedPreferencesTools.REhasnum_key, 0)
							.commit();
					RedDialog.this.sendBroadcast(new Intent(Constants.BrandName
							+ ".redcedbd.upreddate.cannotcheck"));
				}

				// 发送广播 通知红包列表更新 如果红包列表存在的话
				Intent intentbroad = new Intent(Constants.BrandName
						+ ".redlisttoup");
				intentbroad.putExtra("gift_id", gift_id);
				intentbroad.putExtra("money", award_money);
				intentbroad.putExtra("open_time", sdformat.format(new Date()));
				RedDialog.this.sendBroadcast(intentbroad);
				credflag = true;

				// 启动拆红包成功展示Activity
				Intent intent = new Intent(RedDialog.this,
						org.aisin.sipphone.RedDetailsActivity.class);
				intent.putExtra("fromnickname", fromnickname);
				intent.putExtra("from", from);
				intent.putExtra("name", name);
				intent.putExtra("tips", tips);
				intent.putExtra("gift_type", gift_type);
				intent.putExtra("award_money", award_money);
				intent.putExtra("fee_rate", fee_rate);
				if (bitmapByte != null) {
					intent.putExtra("bitmapByte", bitmapByte);
				}
				RedDialog.this.startActivity(intent);
				RedDialog.this.finish();
				return;
			case 6:
				red_errortext.setText("sign错误");
				break;
			case 51:
				red_errortext.setText("红包id不存在，无法拆红包!");
				break;
			case 52:
				red_errortext.setText("该红包已经拆过了!");
				break;
			case 53:
				red_errortext.setText("红包已过有效期!");
				break;
			case 45:
				red_errortext.setText("服务器异常!");
				break;
			default:
				red_errortext.setText("联网失败,请检查网络连接!");
				break;
			}
			red_anim_image.setImageResource(R.drawable.rpopen);
			red_anim_image.setEnabled(true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (animationDrawable != null) {
			animationDrawable.stop();
			animationDrawable = null;
		}

		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		if (bitmapByte != null) {
			bitmapByte = null;
		}
		RecoveryTools.unbindDrawables(reddialog_linlayout);// 回收容器
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			animfinish();
			return true;
		}

		return false;
	}
}
