package org.aisin.sipphone.setts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.commong.UserXXInfo;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.myview.AisinBuildDialog.onMyItemClickListener;
import org.aisin.sipphone.sqlitedb.User_data_Ts;
import org.aisin.sipphone.tools.BitMapcreatPath2smallSize;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.FileManager;
import org.aisin.sipphone.tools.GetPathFromUri4kitkat;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelfPersonalData extends Activity {

	private LinearLayout sfpd_datalinelayout;
	private ImageView rush_selfdata;
	private ImageView spd_headimageshow;// 头像
	private TextView spd_nametext;// 姓名
	private TextView spd_qianmingtext;// 签名
	private TextView sdp_phonenum;// 手机号
	private TextView sdp_uidnum;// 环宇号
	private TextView spd_sextext;// 性别
	private TextView spd_bdatetext;// 生日

	private UserXXInfo uxxi;
	private File headiamge;// 头像图片文件
	private Bitmap bitmap_headimage;// 头像图片

	private String picurl_prefix;// 图片下载地址头
	private String picture;// 图片下载地址
	private String picmd5;// 图片MD5

	private String name;
	private String sextext;
	private String bdate;
	private String signature;// 签名

	private String province;// 省份
	private String city;// 城市
	private String company;// 公司
	private String profession;// 职位
	private String school;// 学校

	private boolean downupimageflag;

	private UserInfo userinfo = null;

	private TreeMap<String, String> upmap = new TreeMap<String, String>();
	private sfpdreBroadcast spdrbt;

	private Animation anim;

	private ProgressDialog prd;

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());

	private File cutfile;

	private int upimageflag;// 上传头像返回值

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
			} else if (msg.what == 2) {
				if (bitmap_headimage != null) {
					bitmap_headimage.recycle();
					bitmap_headimage = null;
				}
				bitmap_headimage = BitMapcreatPath2smallSize
						.GetBitmap4Path2samll(headiamge.getAbsolutePath());
				if (bitmap_headimage != null) {
					spd_headimageshow.setImageBitmap(bitmap_headimage);
				} else {
					spd_headimageshow
							.setImageResource(R.drawable.defaultuserimage);
				}
			} else if (msg.what == 3) {
				if (Check_network.isNetworkAvailable(SelfPersonalData.this)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							// 下载头像图片
							HttpUtils.downloadImage(SelfPersonalData.this,
									picurl_prefix + picture, uxxi.getUid()
											+ "headimage.jpg");
							mHandler.sendEmptyMessage(2);
						}
					}).start();
				}

			} else if (msg.what == 4) {
				// 修改姓名
				spd_nametext.setText(name);
			} else if (msg.what == 5) {
				// 修改性别
				spd_sextext.setText(sextext);
			} else if (msg.what == 6) {
				// 修改生日
				spd_bdatetext.setText(bdate);
			} else if (msg.what == 7) {
				SelfPersonalData.this.finish();
			} else if (msg.what == 8) {
				rush_selfdata.clearAnimation();
				invit();
			} else if (msg.what == 9) {
				if (prd != null) {
					prd.dismiss();
				}
				if (bitmap_headimage != null) {
					spd_headimageshow.setImageBitmap(bitmap_headimage);
				} else {
					spd_headimageshow
							.setImageResource(R.drawable.defaultuserimage);
				}
			} else if (msg.what == 10) {
				if (prd != null) {
					prd.dismiss();
				}
				switch (upimageflag) {
				case 5:
					new AisinBuildDialog(SelfPersonalData.this, "提示", "参数不能为空!");
					break;
				case 61:
					new AisinBuildDialog(SelfPersonalData.this, "提示",
							"个人资料头像图片太大!");
					break;
				case 45:
					new AisinBuildDialog(SelfPersonalData.this, "提示", "服务器异常!");
					break;
				default:
					new AisinBuildDialog(SelfPersonalData.this, "提示",
							"网络异常,请检查网络连接!");
					break;
				}
			} else if (msg.what == 11) {
				spd_qianmingtext.setText(signature);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selfpersonaldata);
		sfpd_datalinelayout = (LinearLayout) this
				.findViewById(R.id.sfpd_datalinelayout);
		rush_selfdata = (ImageView) this.findViewById(R.id.rush_selfdata);
		spd_headimageshow = (ImageView) this
				.findViewById(R.id.spd_headimageshow);
		spd_nametext = (TextView) this.findViewById(R.id.spd_nametext);
		spd_qianmingtext = (TextView) this.findViewById(R.id.spd_qianmingtext);
		sdp_phonenum = (TextView) this.findViewById(R.id.sdp_phonenum);
		sdp_uidnum = (TextView) this.findViewById(R.id.sdp_uidnum);
		spd_sextext = (TextView) this.findViewById(R.id.spd_sextext);
		spd_bdatetext = (TextView) this.findViewById(R.id.spd_bdatetext);
		anim = AnimationUtils.loadAnimation(this, R.anim.upanim);
		// 初始化
		invit();

		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("");

		// 注册监听信息改变
		spdrbt = new sfpdreBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BrandName + ".spdrbt.name");
		filter.addAction(Constants.BrandName + ".spdrbt.qianming");
		filter.addAction(Constants.BrandName + ".spdrbt.personalcard");
		registerReceiver(spdrbt, filter);

	}

	private void invit() {
		uxxi = User_data_Ts.getUXXInfo4DB_self(this,
				UserInfo_db.getUserInfo(this).getUid());
		userinfo = UserInfo_db.getUserInfo(this);
		sdp_phonenum.setText(userinfo.getPhone());
		sdp_uidnum.setText(userinfo.getUid());
		headiamge = SelfPersonalData.this.getFileStreamPath(userinfo.getUid()
				+ "headimage.jpg");
		if (uxxi != null) {
			name = uxxi.getName();
			sextext = uxxi.getSex();
			bdate = uxxi.getBirthday();
			signature = uxxi.getSignature();
			province = uxxi.getProvince();
			city = uxxi.getCity();
			company = uxxi.getCompany();
			profession = uxxi.getProfession();
			school = uxxi.getSchool();
			if (name != null && !"".equals(name) && !"null".equals(name)) {
				spd_nametext.setText(name);
			}
			if (signature != null && !"".equals(signature)
					&& !"null".equals(signature)) {
				spd_qianmingtext.setText(signature);
			}
			if (sextext != null && !"".equals(sextext)
					&& !"null".equals(sextext)) {
				spd_sextext.setText(sextext);
			}
			if (bdate != null && !"".equals(bdate) && !"null".equals(bdate)) {
				spd_bdatetext.setText(bdate);
			}
			// 设置头像
			picture = uxxi.getPicture();
			picmd5 = uxxi.getPicmd5();
			picurl_prefix = uxxi.getPicurl_prefix();
			if (picture != null && !"".equals(picture) && picmd5 != null
					&& !"".equals(picmd5) && picurl_prefix != null
					&& !"".equals(picurl_prefix)) {

				// 如果头像文件不存在 或者MD5不匹配 通知下载
				if (!headiamge.exists() || downupimageflag) {
					mHandler.sendEmptyMessage(3);
				} else {
					mHandler.sendEmptyMessage(2);
				}
			}
		}
	}

	public void OnRelativeLayoutClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.shouwimageaction:// 显示头像
			if (headiamge.exists()) {
				Intent intent = new Intent(SelfPersonalData.this,
						org.aisin.sipphone.setts.ShowImage.class);
				intent.putExtra("imagepath", headiamge.getAbsolutePath());
				SelfPersonalData.this.startActivity(intent);
				SelfPersonalData.this.overridePendingTransition(R.anim.anim_6,
						R.anim.anim_3);
			}
			break;
		case R.id.setts_selfpd_back:// 返回rush_selfdata
			savedata();
			break;
		case R.id.rush_selfdata:// 刷新
			upmap.clear();
			rush_selfdata.startAnimation(anim);
			new HttpTask_getselfdata().execute("");
			break;
		case R.id.spd_headimagerl:// 头像
			showheadimagechange();
			break;
		case R.id.spd_namerl:// 姓名
			Intent intent = new Intent(SelfPersonalData.this,
					org.aisin.sipphone.setts.SPD_changeName.class);
			if (name != null && !"".equals(name) && !"null".equals(name)) {
				intent.putExtra("name", name);
			}
			SelfPersonalData.this.startActivity(intent);
			break;
		case R.id.spd_qianmingrl:// 个性签名
			Intent intent_qm = new Intent(SelfPersonalData.this,
					org.aisin.sipphone.setts.SPD_qianming.class);
			if (signature != null && !"".equals(signature)
					&& !"null".equals(signature)) {
				intent_qm.putExtra("signature", signature);
			}
			SelfPersonalData.this.startActivity(intent_qm);
			break;
		case R.id.qrcode_relayout:// 二维码
			Intent itent_qrcode = new Intent(SelfPersonalData.this,
					org.aisin.sipphone.setts.MyQrcode.class);
			SelfPersonalData.this.startActivity(itent_qrcode);
			break;
		case R.id.spd_selfmprl:// 个人名片
			Intent intent_mp = new Intent(SelfPersonalData.this,
					org.aisin.sipphone.setts.PersonalCard.class);
			if (province != null && !"".equals(province)
					&& !"null".equals(province) && city != null
					&& !"".equals(city) && !"null".equals(city)) {
				intent_mp.putExtra("province", province);
				intent_mp.putExtra("city", city);
			}
			if (company != null && !"".equals(company)
					&& !"null".equals(company)) {
				intent_mp.putExtra("company", company);
			}
			if (profession != null && !"".equals(profession)
					&& !"null".equals(profession)) {
				intent_mp.putExtra("profession", profession);
			}
			if (school != null && !"".equals(school) && !"null".equals(school)) {
				intent_mp.putExtra("school", school);
			}
			SelfPersonalData.this.startActivity(intent_mp);
			break;
		case R.id.spd_sexrl:// 性别
			showsex();
			break;
		case R.id.spd_bdaterl:// 生日
			showbdate();
			break;
		}
	}

	private void showheadimagechange() {
		AisinBuildDialog mybuild = new AisinBuildDialog(SelfPersonalData.this);
		mybuild.setTitle("请选择照片");
		String[] items = { "拍照", "从相册选择" };
		mybuild.setListViewItem(items, new onMyItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					Intent cameraintent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					// 指定调用相机拍照后照片的储存路径
					cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(tempFile));
					startActivityForResult(cameraintent,
							PHOTO_REQUEST_TAKEPHOTO);
				} else if (position == 1) {
					Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
					getAlbum.setType("image/*");
					startActivityForResult(getAlbum, PHOTO_REQUEST_GALLERY);
				}
			}
		});
		mybuild.setOnDialogCancelListener("取消", null);
		mybuild.dialogShow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 针对按返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			savedata();
			return true;
		} else {
			return false;
		}
	}

	private void savedata() {
		if (upmap.size() > 0) {
			// 修改了数据
			// 上传数据
			new HttpTask_upuserdata().execute("up");
		} else {
			mHandler.sendEmptyMessage(7);
		}
	}

	private void showbdate() {
		Calendar c = Calendar.getInstance();
		DatePickerDialog dpdlog = new DatePickerDialog(SelfPersonalData.this,
				new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						String temp_bdate = year + "-" + (monthOfYear + 1)
								+ "-" + dayOfMonth;
						if (!temp_bdate.equals(bdate)) {
							bdate = temp_bdate;
							upmap.put("birthday", bdate);
							mHandler.sendEmptyMessage(6);
						}
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));
		dpdlog.show();
	}

	private void showsex() {
		// 弹出性别选择框

		AisinBuildDialog mybuild = new AisinBuildDialog(SelfPersonalData.this);
		mybuild.setTitle("请选择性别");
		final String[] items = { "男", "女" };
		mybuild.setListViewItem(items, new onMyItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (sextext == null || !items[position].equals(sextext)) {
					sextext = items[position];
					upmap.put("sex", sextext);
					mHandler.sendEmptyMessage(5);
				}
			}
		});
		mybuild.setOnDialogCancelListener("取消", null);
		mybuild.dialogShow();

	}

	// 接收更新事物
	private class sfpdreBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.BrandName + ".spdrbt.name")) {
				String temp_name = intent.getStringExtra("name");
				if (temp_name != null && !temp_name.equals(name)) {
					name = temp_name;
					upmap.put("name", name);
					mHandler.sendEmptyMessage(4);
				}
			} else if (intent.getAction().equals(
					Constants.BrandName + ".spdrbt.qianming")) {
				String temp_signature = intent.getStringExtra("signature");
				if (temp_signature != null && !temp_signature.equals(signature)) {
					signature = temp_signature;
					upmap.put("signature", signature);
					mHandler.sendEmptyMessage(11);
				}
			} else if (intent.getAction().equals(
					Constants.BrandName + ".spdrbt.personalcard")) {
				String temp_province = intent.getStringExtra("province");
				String temp_city = intent.getStringExtra("city");
				String temp_company = intent.getStringExtra("company");
				String temp_profession = intent.getStringExtra("profession");
				String temp_school = intent.getStringExtra("school");
				if (temp_province != null && !temp_province.equals(province)) {
					province = temp_province;
					upmap.put("province", province);
				}
				if (temp_city != null && !temp_city.equals(city)) {
					city = temp_city;
					upmap.put("city", city);
				}
				if (temp_company != null && !temp_company.equals(company)) {
					company = temp_company;
					upmap.put("company", company);
				}
				if (temp_profession != null
						&& !temp_profession.equals(profession)) {
					profession = temp_profession;
					upmap.put("profession", profession);
				}
				if (temp_school != null && !temp_school.equals(school)) {
					school = temp_school;
					upmap.put("school", school);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (spdrbt != null) {
			unregisterReceiver(spdrbt);
		}
		if (bitmap_headimage != null) {
			bitmap_headimage.recycle();
			bitmap_headimage = null;
		}
		RecoveryTools.unbindDrawables(sfpd_datalinelayout);// 回收容器
	}

	// 上传更新数据
	private class HttpTask_upuserdata extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... paramArrayOfParams) {

			try {
				if (userinfo == null) {
					userinfo = UserInfo_db.getUserInfo(SelfPersonalData.this);
				}
				UserXXInfo uxxi = User_data_Ts.getUXXInfo4DB_self(
						SelfPersonalData.this, userinfo.getUid());
				String ver = "1.0";
				if (uxxi != null && uxxi.getVer() != null
						&& !"".equals(uxxi.getVer())
						&& !"null".equals(uxxi.getVer())) {
					ver = uxxi.getVer();
				}
				HttpClient httpclient = new DefaultHttpClient();
				// 请求超时
				httpclient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				// 读取超时
				httpclient.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 5000);
				HttpPost post = new HttpPost(HttpUtils.UPUSERDATAURL + "?uid="
						+ userinfo.getUid() + "&ver=" + ver);
				JSONObject js1 = new JSONObject();
				for (Entry<String, String> ent : upmap.entrySet()) {
					js1.put(ent.getKey(), ent.getValue());
				}
				JSONObject js2 = new JSONObject();
				js2.put("userprofile", js1);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("userprofile", js2.toString()));
				UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(
						list, HTTP.UTF_8);
				post.setEntity(formEntiry);
				HttpResponse response = httpclient.execute(post);
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					return EntityUtils.toString(response.getEntity(), "UTF-8");
				} else {
					return null;
				}
			} catch (Exception e) {
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject json = null;
			int doresult = -104;
			String ver = null;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer.parseInt(json
							.optString("result", "-104"));
					ver = json.optString("ver");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			AisinBuildDialog mybuild = new AisinBuildDialog(
					SelfPersonalData.this);

			switch (doresult) {
			case 0:
				ContentValues cv = new ContentValues();
				cv.put("uid", userinfo.getUid());
				cv.put("ver", ver);
				for (Entry<String, String> ent : upmap.entrySet()) {
					cv.put(ent.getKey(), ent.getValue());
				}
				User_data_Ts.add2upUXXI(SelfPersonalData.this,
						userinfo.getUid(), cv);
				mHandler.sendEmptyMessage(7);
				return;
			case 5:
				mybuild.setMessage("资料保存失败！\n参数不能为空!是否放弃更改?");
				break;
			case 45:
				mybuild.setMessage("资料保存失败！\n服务器异常!是否放弃更改?");
				break;
			default:
				mybuild.setMessage("资料保存失败！\n网络不可用!是否放弃更改?");
				break;
			}
			mybuild.setTitle("提示");
			mybuild.setOnDialogCancelListener("取消", null);
			mybuild.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							mHandler.sendEmptyMessage(7);
						}
					});
			mybuild.dialogShow();
		}
	}

	// 获取个人资料
	private class HttpTask_getselfdata extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			UserInfo usi = UserInfo_db.getUserInfo(SelfPersonalData.this);
			String ver = "1.0";
			String reg_url = URLTools.GetHttpURL_4UserXXinfo_url(
					SelfPersonalData.this, usi.getUid(), ver);
			String result = HttpUtils.result_url_get(reg_url,
					"{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// 处理数据
			JSONObject json = null;
			int doresult = -104;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer.parseInt(json
							.optString("result", "-104"));
				}
			} catch (JSONException e) {
			}
			switch (doresult) {
			case 0:
				// 存储数据
				try {
					int uid = json.getInt("uid");
					String province = json.optString("province");
					String picture = json.optString("picture");
					String picmd5 = json.optString("picmd5");
					String ver = json.optString("ver");
					String picurl_prefix = json.optString("picurl_prefix");
					String city = json.optString("city");
					String company = json.optString("company");
					String profession = json.optString("profession");
					String school = json.optString("school");
					String sex = json.optString("sex");
					String birthday = json.optString("birthday");
					String location = json.optString("location");
					String signature = json.optString("signature");
					String from = json.optString("from_self");
					String mobileNumber = json.optString("mobileNumber");
					String email = json.optString("email");
					String name = json.optString("name");
					ContentValues cv = new ContentValues();
					cv.put("uid", uid);
					if (province != null && !"".equals(province)
							&& !"null".equals(province)) {
						cv.put("province", province);
					}
					if (picture != null && !"".equals(picture)
							&& !"null".equals(picture)) {
						cv.put("picture", picture);
					}
					if (ver != null && !"".equals(ver) && !"null".equals(ver)) {
						cv.put("ver", ver);
					}
					if (picmd5 != null && !"".equals(picmd5)
							&& !"null".equals(picmd5)) {
						cv.put("picmd5", picmd5);
					}
					if (picurl_prefix != null && !"".equals(picurl_prefix)
							&& !"null".equals(picurl_prefix)) {
						cv.put("picurl_prefix", picurl_prefix);
					}
					if (city != null && !"".equals(city)
							&& !"null".equals(city)) {
						cv.put("city", city);
					}
					if (company != null && !"".equals(company)
							&& !"null".equals(company)) {
						cv.put("company", company);
					}
					if (profession != null && !"".equals(profession)
							&& !"null".equals(profession)) {
						cv.put("profession", profession);
					}
					if (school != null && !"".equals(school)
							&& !"null".equals(school)) {
						cv.put("school", school);
					}
					if (sex != null && !"".equals(sex) && !"null".equals(sex)) {
						cv.put("sex", sex);
					}
					if (location != null && !"".equals(location)
							&& !"null".equals(location)) {
						cv.put("location", location);
					}
					if (birthday != null && !"".equals(birthday)
							&& !"null".equals(birthday)) {
						cv.put("birthday", birthday);
					}
					if (signature != null && !"".equals(signature)
							&& !"null".equals(signature)) {
						cv.put("signature", signature);
					}
					if (from != null && !"".equals(from)
							&& !"null".equals(from)) {
						cv.put("from_self", from);
					}
					if (mobileNumber != null && !"".equals(mobileNumber)
							&& !"null".equals(mobileNumber)) {
						cv.put("mobileNumber", mobileNumber);
					}
					if (email != null && !"".equals(email)
							&& !"null".equals(email)) {
						cv.put("email", email);
					}
					if (name != null && !"".equals(name)
							&& !"null".equals(name)) {
						cv.put("name", name);
					}
					User_data_Ts
							.add2upUXXI(SelfPersonalData.this, uid + "", cv);
					if (uxxi == null) {
						downupimageflag = true;
					} else {
						downupimageflag = !(picmd5.equals(uxxi.getPicmd5()));
					}

				} catch (Exception e) {
				}
				break;
			}
			mHandler.sendEmptyMessage(8);
		}
	}

	// 使用系统当前日期加以调整作为照片的名称
	@SuppressLint("SimpleDateFormat")
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:// 当选择拍照时调用
			startPhotoZoom(Uri.fromFile(tempFile));
			break;
		case PHOTO_REQUEST_GALLERY:// 当选择从本地获取图片时
			// 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
			if (data != null) {
				String path = GetPathFromUri4kitkat.getPath(
						SelfPersonalData.this, data.getData());
				startPhotoZoom(Uri.parse("file:///" + path));
			} else {
			}
			break;
		case PHOTO_REQUEST_CUT:// 返回的结果
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					String path = GetPathFromUri4kitkat.getPath(
							SelfPersonalData.this, data.getData());
					// 保存图像 并提交数据
					prd.setMessage("上传照片...");
					prd.show();
					saveimage(Uri.parse("file:///" + path).getPath());
				} else {
					if (cutfile != null && cutfile.exists()) {
						// 保存图像 并提交数据
						prd.setMessage("上传照片...");
						prd.show();
						saveimage(cutfile.getAbsolutePath());
					} else {
						new AisinBuildDialog(SelfPersonalData.this, "提示",
								"该截图工具与软件不兼容,请重新选择截图工具!");
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void saveimage(final String filepath) {
		if (filepath != null) {
			// 上传头像数据
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if (userinfo == null) {
							userinfo = UserInfo_db
									.getUserInfo(SelfPersonalData.this);
						}
						HttpClient httpclient = new DefaultHttpClient();
						// 请求超时
						httpclient.getParams().setParameter(
								CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
						// 读取超时
						httpclient.getParams().setParameter(
								CoreConnectionPNames.SO_TIMEOUT, 5000);
						HttpPost post = new HttpPost(HttpUtils.UPUserHeadURL
								+ "?uid=" + userinfo.getUid());
						MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
						ContentBody cbFile = new FileBody(new File(filepath));
						mpEntity.addPart("msg", cbFile);
						post.setEntity(mpEntity);
						HttpResponse response = httpclient.execute(post);
						int code = response.getStatusLine().getStatusCode();
						if (code == 200) {
							String result = EntityUtils.toString(
									response.getEntity(), "UTF-8");
							JSONObject json = null;
							int doresult = -104;
							try {
								if (result != null) {
									json = new JSONObject(result);
									doresult = Integer.parseInt(json.optString(
											"result", "-104"));
								}
							} catch (JSONException e) {
							}
							switch (doresult) {
							case 0:
								// 保存数据
								picurl_prefix = json.optString("picurl_prefix");
								picture = json.optString("picture");
								picmd5 = json.optString("picmd5");
								String ver = json.optString("ver");
								ContentValues cv = new ContentValues();
								cv.put("uid", userinfo.getUid());
								cv.put("picurl_prefix", picurl_prefix);
								cv.put("picture", picture);
								cv.put("picmd5", picmd5);
								cv.put("ver", ver);
								User_data_Ts.add2upUXXI(SelfPersonalData.this,
										userinfo.getUid(), cv);
								// 保存图像
								FileInputStream finput = new FileInputStream(
										filepath);
								FileOutputStream outf = SelfPersonalData.this
										.openFileOutput(userinfo.getUid()
												+ "headimage.jpg",
												Context.MODE_PRIVATE);
								byte[] bts = new byte[1024];
								int num = 0;
								while ((num = finput.read(bts)) != -1) {
									outf.write(bts, 0, num);
								}
								outf.flush();
								outf.close();
								finput.close();
								new File(filepath).delete();
								// 重置图像
								if (bitmap_headimage != null) {
									bitmap_headimage.recycle();
									bitmap_headimage = null;
								}
								bitmap_headimage = BitMapcreatPath2smallSize
										.GetBitmap4Path2samll(headiamge
												.getAbsolutePath());
								mHandler.sendEmptyMessage(9);
								return;
							default:
								upimageflag = doresult;
								mHandler.sendEmptyMessage(10);
								break;
							}
						} else {
							// 上传失败
							upimageflag = -104;
							mHandler.sendEmptyMessage(10);
						}
					} catch (Exception e) {
						upimageflag = -104;
						mHandler.sendEmptyMessage(10);
					}
				}
			}).start();
		}
	}

	public byte[] getBitmapByte(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// 图片输出路径 不需要有 只要设置return-data为false会自动输出目录
		File file = new File(FileManager.getFileDir() + "/"
				+ Constants.BrandName);
		if (!file.exists()) {
			file.mkdirs();
		}
		cutfile = new File(FileManager.getFileDir() + "/" + Constants.BrandName
				+ "/" + System.currentTimeMillis() + ".jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cutfile));// 图片输出
		intent.putExtra("scale", true);
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", 500);
		intent.putExtra("outputY", 500);
		intent.putExtra("return-data", false);// 是否返回图像数据与图片输出只能有一个存在
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("scaleUpIfNeeded", true);// 图片不够大的话 就自动拉伸
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}
}
