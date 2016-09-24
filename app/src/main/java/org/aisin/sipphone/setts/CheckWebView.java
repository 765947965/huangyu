package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.RecoveryTools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class CheckWebView extends Activity implements OnClickListener,
		OnTouchListener {
	private LinearLayout checkwebview_linlayout;
	private LinearLayout tabbarlayout;
	private ImageView topbarimage;
	private View barlineview;
	private RelativeLayout jindutiaolayout;
	private TextView jindutiao_h;
	private TextView jindutiao_q;
	private TextView banckiamge;
	private TextView advance;
	private TextView upwebimage;
	private String title_name;
	private String url_view;
	private ImageView setting_chechweb_back;
	private TextView setting_chechweb_text;
	private WebView check_webvew;
	private boolean uporstop = true;// 刷新或者停止加载 true//刷新 false 停止加载
	private int nNewProgress;// 进度
	private PopupWindow popupWindow;
	// 手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
	private float x1 = 0;
	private float x2 = 0;
	private float y1 = 0;
	private float y2 = 0;
	private boolean updordown = true;// bar隐藏还是显示

	private String js0 = "(function() { var btn = document.getElementById(\"bn-closebtn\");btn.click();}()) ",
			js1 = "(function() { var taobaoLogo = document.getElementsByClassName(\"new-header new-header-append\");var len = taobaoLogo.length;for(var i = 0; i < len; i++) {taobaoLogo[i].parentNode.removeChild(taobaoLogo[i]);}}()) ",
			js2 = "(function() { var taobaoLogo = document.getElementById(\"down_app_header\"); if(taobaoLogo)taobaoLogo.parentNode.removeChild(taobaoLogo);}()) ",
			js3 = "(function() { var taobaoLogo = document.getElementById(\"down_app\"); if(taobaoLogo)taobaoLogo.parentNode.removeChild(taobaoLogo);}()) ",
			js4 = "(function() { var taobaoLogo = document.getElementById(\"down_app_header\"); if(taobaoLogo)taobaoLogo.parentNode.removeChild(taobaoLogo);}()) ",
			js5 = "(function() { var taobaoLogo = document.getElementById(\"m_common_tip\"); if(taobaoLogo)taobaoLogo.parentNode.removeChild(taobaoLogo);}()) ",
			js6 = "(function() { var taobaoLogo = document.getElementById(\"m_common_header\"); if(taobaoLogo)taobaoLogo.parentNode.removeChild(taobaoLogo);}()) ",
			js7 = "(function() { var taobaoLogo = document.getElementsByClassName(\"new-header\");var len = taobaoLogo.length;for(var i = 0; i < len; i++) {taobaoLogo[i].parentNode.removeChild(taobaoLogo[i]);}}()) ",
			js8 = "(function() { var qunar = document.getElementsByClassName(\"QNT_download_touch\");var len = qunar.length;for(var i = 0; i < len; i++) {qunar[i].parentNode.removeChild(qunar[i]);}}()) ",
			js9 = "(function() { var qunar = document.getElementsByClassName(\"qn_download\");var len = qunar.length;for(var i = 0; i < len; i++) {qunar[i].parentNode.removeChild(qunar[i]);}}()) ",
			js10 = "(function() { var qunar = document.getElementsByClassName(\"ad\");var len = qunar.length;for(var i = 0; i < len; i++) {qunar[i].parentNode.removeChild(qunar[i]);}}()) ",
			js11 = "(function() { var qunar = document.getElementsByClassName(\"mp-download\");var len = qunar.length;for(var i = 0; i < len; i++) {qunar[i].parentNode.removeChild(qunar[i]);}}()) ",
			js12 = "(function() { var qunar = document.getElementsByClassName(\"mp-download-inner\");var len = qunar.length;for(var i = 0; i < len; i++) {qunar[i].parentNode.removeChild(qunar[i]);}}())";

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (check_webvew.canGoBack()) {
					banckiamge.setEnabled(true);
				} else {
					banckiamge.setEnabled(false);
				}
				if (check_webvew.canGoForward()) {
					advance.setEnabled(true);
				} else {
					advance.setEnabled(false);
				}
			} else if (msg.what == 2) {
				upwebimage.setBackgroundResource(R.drawable.webimageupclose);
			} else if (msg.what == 3) {
				upwebimage.setBackgroundResource(R.drawable.webimageup);
			} else if (msg.what == 4) {
				android.view.ViewGroup.LayoutParams lp = jindutiao_q
						.getLayoutParams();
				lp.width = (int) (jindutiao_h.getWidth() * ((double) nNewProgress / (double) 100));
				jindutiao_q.setLayoutParams(lp);
			} else if (msg.what == 5) {
				barlineview.setVisibility(View.GONE);
			} else if (msg.what == 6) {
				barlineview.setVisibility(View.VISIBLE);
			}
		}
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkwebview);
		title_name = getIntent().getStringExtra("flag_name");
		url_view = getIntent().getStringExtra("url_view");
		if (url_view == null || "".equals(url_view)) {
			finish();
			return;
		}
		if(!url_view.contains("http://")&&!url_view.contains("https://")){
			url_view = "http://" + url_view;
		}
		if (title_name == null || "".equals(title_name)) {
			title_name = "";
		}
		checkwebview_linlayout = (LinearLayout) this
				.findViewById(R.id.checkwebview_linlayout);
		setting_chechweb_back = (ImageView) this
				.findViewById(R.id.setting_chechweb_back);
		setting_chechweb_text = (TextView) this
				.findViewById(R.id.setting_chechweb_text);
		check_webvew = (WebView) this.findViewById(R.id.check_webvew);
		tabbarlayout = (LinearLayout) this.findViewById(R.id.tabbarlayout);
		barlineview = this.findViewById(R.id.barlineview);
		topbarimage = (ImageView) this.findViewById(R.id.topbarimage);
		banckiamge = (TextView) this.findViewById(R.id.banckiamge);
		advance = (TextView) this.findViewById(R.id.advance);
		upwebimage = (TextView) this.findViewById(R.id.upwebimage);
		jindutiaolayout = (RelativeLayout) this
				.findViewById(R.id.jindutiaolayout);
		jindutiao_h = (TextView) this.findViewById(R.id.jindutiao_h);
		jindutiao_q = (TextView) this.findViewById(R.id.jindutiao_q);
		setting_chechweb_back.setOnClickListener(this);
		banckiamge.setOnClickListener(this);
		advance.setOnClickListener(this);
		upwebimage.setOnClickListener(this);
		topbarimage.setOnClickListener(this);
		setting_chechweb_text.setText(title_name);
		check_webvew.requestFocus();
		WebSettings web = check_webvew.getSettings();
		web.setJavaScriptEnabled(true);
		web.setDefaultTextEncodingName("UTF-8");
		web.setAllowContentAccess(true);
		web.setAllowFileAccess(true);
		web.setLoadWithOverviewMode(true);
		web.setUseWideViewPort(true);
		web.setAppCacheEnabled(true);//
		web.setSupportZoom(true);
		web.setBuiltInZoomControls(true);
		web.setDisplayZoomControls(false);
		web.setDomStorageEnabled(true);
		check_webvew.setLayerType(View.LAYER_TYPE_HARDWARE, null);

		// 初始化pupwindow
		View popView = LayoutInflater.from(this).inflate(
				R.layout.red_popupwindow, null);
		popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		ColorDrawable dw = new ColorDrawable(-00000);
		popupWindow.setBackgroundDrawable(dw);
		TextView ppwindow_redall = (TextView) popView
				.findViewById(R.id.ppwindow_redall);
		TextView sendoutred_bt = (TextView) popView
				.findViewById(R.id.sendoutred_bt);
		View popred_line = popView.findViewById(R.id.popred_line);
		TextView ppwindow_rednot = (TextView) popView
				.findViewById(R.id.ppwindow_rednot);
		ppwindow_redall.setText("在浏览器中打开");
		ppwindow_redall.setTextSize(10);
		sendoutred_bt.setText("复制连接");
		sendoutred_bt.setTextSize(10);
		popred_line.setVisibility(View.GONE);
		ppwindow_rednot.setVisibility(View.GONE);
		ppwindow_redall.setOnClickListener(this);
		sendoutred_bt.setOnClickListener(this);

		check_webvew.setWebViewClient(new WebViewClient() {// 响应webview点击事件不跳转到浏览器显示
					public boolean shouldOverrideUrlLoading(WebView view,
							String url) {
						if (url.indexOf("tel:") < 0) {
							if(url.indexOf("http") < 0){
								return false;
							}
							url_view = url;
							view.loadUrl(url);
						}
						return true;
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						// TODO Auto-generated method stub
						super.onPageFinished(view, url);

					}

					@Override
					public void onPageStarted(WebView view, String url,
							Bitmap favicon) {
						// TODO Auto-generated method stub
						super.onPageStarted(view, url, favicon);
						// prd.show();
					}
				});
		check_webvew.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				nNewProgress = newProgress;
				mHandler.sendEmptyMessage(4);
				if (uporstop && newProgress > 0) {
					uporstop = false;
					jindutiaolayout.setVisibility(View.VISIBLE);
					mHandler.sendEmptyMessage(1);
					mHandler.sendEmptyMessage(2);
				}
				if (newProgress == 100) {
					uporstop = true;
					jindutiaolayout.setVisibility(View.INVISIBLE);
					mHandler.sendEmptyMessage(3);
					view.loadUrl("javascript:" + js0);
					view.loadUrl("javascript:" + js1);
					view.loadUrl("javascript:" + js2);
					view.loadUrl("javascript:" + js3);
					view.loadUrl("javascript:" + js4);
					view.loadUrl("javascript:" + js5);
					view.loadUrl("javascript:" + js6);
					view.loadUrl("javascript:" + js7);
					view.loadUrl("javascript:" + js8);
					view.loadUrl("javascript:" + js9);
					view.loadUrl("javascript:" + js10);
					view.loadUrl("javascript:" + js11);
					view.loadUrl("javascript:" + js12);
				}
			}
		});
		check_webvew.loadUrl(url_view);
		check_webvew.setOnTouchListener(this);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.setting_chechweb_back) {
			finish();
		} else if (id == R.id.banckiamge) {
			check_webvew.goBack();
		} else if (id == R.id.advance) {
			check_webvew.goForward();
		} else if (id == R.id.upwebimage) {
			if (uporstop) {
				check_webvew.loadUrl(url_view);
			} else {
				check_webvew.stopLoading();
			}
		} else if (id == R.id.topbarimage) {
			showPop(v);
		} else if (id == R.id.ppwindow_redall) {
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			// 浏览器中打开
			Intent uppdate = new Intent(Intent.ACTION_VIEW,
					Uri.parse(url_view));
			CheckWebView.this.startActivity(uppdate);
		} else if (id == R.id.sendoutred_bt) {
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			// 复制连接
			if (PhoneInfo.SDKVersion < 11) {
				android.text.ClipboardManager clip = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				// clip.getText(); // 粘贴
				clip.setText(url_view); // 复制
			} else {
				android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData cd = ClipData.newPlainText("url", url_view);
				clip.setPrimaryClip(cd);
			}
			Toast.makeText(CheckWebView.this, "复制成功", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.ACTION_UP) {
			onmybanck();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void onmybanck() {
		if (check_webvew.canGoBack()) {
			check_webvew.goBack();// 返回上一页面
		} else {
			this.finish();
		}
	}

	/**
	 * 显示popWindow
	 * */
	public void showPop(View parent) {
		if (popupWindow == null) {
			return;
		}
		// 设置popwindow显示位置
		popupWindow.showAsDropDown(parent);

		// 获取popwindow焦点 popupWindow.setFocusable(true); //
		// 设置popwindow如果点击外面区域，便关闭。
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();

	}

	@Override
	protected void onDestroy() {
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
		super.onDestroy();
		 RecoveryTools.unbindDrawables(checkwebview_linlayout);// 回收容
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		// 继承了Activity的onTouchEvent方法，直接监听点击事件
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 当手指按下的时候
			x1 = event.getX();
			y1 = event.getY();
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			x2 = event.getX();
			y2 = event.getY();
			if (y1 - y2 > 5) {
				if (updordown) {
					BarDownOrUp();
				}
			} else if (y2 - y1 > 5) {
				if (!updordown) {
					BarDownOrUp();
				}
			}
			x1 = x2;
			y1 = y2;
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// 当手指离开的时候
			x2 = event.getX();
			y2 = event.getY();
			if (y1 - y2 > 50) {
				// Toast.makeText(CheckWebView.this, "向上滑", Toast.LENGTH_SHORT)
				// .show();
				if (updordown) {
					// BarDownOrUp();
				}
			} else if (y2 - y1 > 50) {
				// Toast.makeText(CheckWebView.this, "向下滑", Toast.LENGTH_SHORT)
				// .show();
				if (!updordown) {
					// BarDownOrUp();
				}
			} else if (x1 - x2 > 50) {
				// Toast.makeText(CheckWebView.this, "向左滑", Toast.LENGTH_SHORT)
				// .show();
			} else if (x2 - x1 > 50) {
				// Toast.makeText(CheckWebView.this, "向右滑", Toast.LENGTH_SHORT)
				// .show();
			}
		}
		return false;
	}

	private void BarDownOrUp() {
		AnimationSet animup = new AnimationSet(true);
		animup.setFillAfter(true);
		if (updordown) {// 向下
			updordown = false;
			TranslateAnimation mup = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					0f, Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, +1f);
			mup.setDuration(200);
			animup.addAnimation(mup);
			tabbarlayout.startAnimation(animup);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mHandler.sendEmptyMessage(5);
				}
			}, 200);
		} else {// 向上
			updordown = true;
			TranslateAnimation mup = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					0f, Animation.RELATIVE_TO_SELF, +1f,
					Animation.RELATIVE_TO_SELF, 0f);
			mup.setDuration(200);
			animup.addAnimation(mup);
			tabbarlayout.startAnimation(animup);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mHandler.sendEmptyMessage(6);
				}
			}, 200);
		}
	}

}
