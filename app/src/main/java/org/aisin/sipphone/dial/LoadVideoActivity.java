package org.aisin.sipphone.dial;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildCancelListener;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.FileManager;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class LoadVideoActivity extends Activity implements OnClickListener,
		OnPreparedListener, OnTouchListener, OnErrorListener {
	private VideoView videoView;

	private TextView tv_currenttimer, tv_endtimer;// 当前时间,视频总时间
	private SeekBar seekBar;
	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;
	private upDateSeekBar update; // 更新进度条
	private boolean isPlay, isFinish;
	private ImageView img_press;
	private HttpUtils httputils;
	private NumberCircleProgressBar numcirleBar; // 圆形进度条
	private FrameLayout frametop, framebottom;
	private LinearLayout lin_heard;
	private String url, currtimer;
	private Context mContext;
	private SharedPreferences shared;
	private int location;
	private ImageView img_load, img_loadback; // 向下动画
	private int width, height;
	private View viewtopblank;
	private FrameLayout viewblank;
	private byte[] bytes;
	private RelativeLayout real;
	private LinearLayout.LayoutParams lp;
	private static int position;

	private boolean advposition;// 动画的方向，true表示top->center,false bottom->center
	private int percent;
	private HttpHandler<File> hander;
	private Animation anim = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_load_video);
		// setAttribute();
		mContext = this;
		httputils = new HttpUtils(); // xUtils实例化
		Intent intent = getIntent();
		url = intent.getStringExtra("url_view");
		location = intent.getIntExtra("position", 0);
		width = intent.getIntExtra("width", 0);
		height = intent.getIntExtra("height", 0);
		advposition = intent.getBooleanExtra("advRedict", true); // 视频动画方向
		bytes = intent.getByteArrayExtra("bitmap");
		shared = SharedPreferencesTools.getSharedPreferences_Video(mContext);
		initView(); // 初始化控件
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager m = getWindowManager();
		m.getDefaultDisplay().getMetrics(metric);
		int width_temp = PhoneInfo.width;
		int height_temp = (int) (((float)PhoneInfo.width/(float)width)*(float)height);
		lp = new LinearLayout.LayoutParams(width_temp, height_temp);
		width = width_temp;
		height = height_temp;
		framebottom.setLayoutParams(lp);
		img_load.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0,
				bytes.length));

		if (TextUtils.isEmpty(url))
			this.finish();
		
		// 保存的url地址
		String urlOld = shared.getString("viedo" + location, "location"
				+ location);

		// 判断是否下载成功
		boolean downloadSucess = shared.getBoolean(
				SharedPreferencesTools.DIAL_VIDEO, false);
		final Uri uripath = Uri.parse(getVideoPath(mContext, location));
		// 是否是同一个视频
		if (url.equals(urlOld)) {
			File file = new File(getVideoPath(mContext, location));
			/*
			 * 文件存在并且下载成功直接播放，否则重新下载
			 */
			if (file.exists() && downloadSucess) {
				numcirleBar.setVisibility(View.GONE);
				framebottom.setBackgroundColor(Color.WHITE);

				if (advposition)
					anim = AnimationUtils.loadAnimation(mContext,
							R.anim.video_enter);
				else
					anim = AnimationUtils.loadAnimation(mContext,
							R.anim.video_enter_from_bottom);
				anim.setFillAfter(true);
				framebottom.startAnimation(anim);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {

						playVideo(uripath);

					}
				});

			} else {

				downLoadVideo(mContext, url, getVideoPath(mContext, location));
			}
		} else {

			shared.edit().putString("viedo" + location, url).commit();
			downLoadVideo(mContext, url, getVideoPath(mContext, location));

		}

	}

	// 播放视频
	private void playVideo(final Uri url) {

		// 播放时图片，下载圆形进度条消失
		framebottom.setBackgroundColor(Color.TRANSPARENT);
		img_load.setVisibility(View.GONE);
		numcirleBar.setVisibility(View.GONE);

		if (advposition)
			anim = AnimationUtils.loadAnimation(mContext, R.anim.video_out1);
		else
			anim = AnimationUtils.loadAnimation(mContext,
					R.anim.video_out_from_bottom1);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				framebottom.setVisibility(View.GONE);
				if (TextUtils.isEmpty(url.toString())) {

				} else {

					// 播放视频
					frametop.setVisibility(View.VISIBLE);
					frametop.setLayoutParams(lp);
					viewblank.setVisibility(View.VISIBLE);
					viewtopblank.setVisibility(View.VISIBLE);
					videoView.setVideoURI(url);

				}
			}
		});
		framebottom.startAnimation(anim);

	}

	
	// 下载视频
	private void downLoadVideo(Context context, final String url,
			final String path) {

		if (Check_network.isNetworkAvailable(mContext)) {
			hander = httputils.download(url, path, true,
					new RequestCallBack<File>() {

						@Override
						public void onSuccess(ResponseInfo<File> arg0) {

							mHandler.sendEmptyMessage(1);

						}

						@Override
						public void onStart() {
							numcirleBar.setVisibility(View.VISIBLE);

							super.onStart();
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							numcirleBar.setVisibility(View.GONE);

							AisinBuildDialog mybuild = new AisinBuildDialog(
									mContext);
							mybuild.setTitle("提示");
							mybuild.setMessage("缓存失败");
							mybuild.setOnDialogCancelListener("取消",
									new DialogBuildCancelListener() {

										@Override
										public void dialogCancel() {
											numcirleBar
													.setVisibility(View.VISIBLE);

										}
									});
							mybuild.setOnDialogConfirmListener("重试",
									new DialogBuildConfirmListener() {
										@Override
										public void dialogConfirm() {
											numcirleBar
													.setVisibility(View.VISIBLE);
											Message msg = mHandler
													.obtainMessage(2);
											msg.arg1 = 0;
											mHandler.sendMessage(msg);
											String path = getVideoPath(
													mContext, location);
											File file = new File(path);
											if (file.exists())
												file.delete();
											downLoadVideo(mContext, url, path);
										}
									});

							mybuild.dialogShow();

						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							percent = (int) current * 100 / (int) total;

							if (percent >= 90 && percent <= 100) {

								Message msg = mHandler.obtainMessage(2);
								msg.arg1 = percent;
								mHandler.sendMessageDelayed(msg, 2000);

							} else if (percent >= 70 && percent < 90) {
								Message msg = mHandler.obtainMessage(2);
								msg.arg1 = percent;
								mHandler.sendMessageDelayed(msg, 1000);
							} else if (percent < 70) {
								Message msg = mHandler.obtainMessage(2);
								msg.arg1 = percent;
								mHandler.sendMessage(msg);

							}

						}
					});
		} else {

			new AisinBuildDialog(mContext, "提示", "请检查你的网络");
		}

	}

	// 控件初始化
	private void initView() {
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

		videoView = (VideoView) findViewById(R.id.asinvideoview);
		videoView.setOnErrorListener(this);
		videoView.setOnPreparedListener(LoadVideoActivity.this);
		videoView.setOnTouchListener(LoadVideoActivity.this);
        videoView.setZOrderOnTop(true); // Backgroud 及所有View都会被覆盖
		videoView.setZOrderMediaOverlay(true); // Backgroud也会显示在前面, 覆盖画面
		videoView.getHolder().setFormat(PixelFormat.RGBX_8888);
		
		tv_currenttimer = (TextView) findViewById(R.id.play_time);
		tv_endtimer = (TextView) findViewById(R.id.play_end_time);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		numcirleBar = (NumberCircleProgressBar) findViewById(R.id.numbercircleprogress_bar);
		frametop = (FrameLayout) findViewById(R.id.load_frametop);
		framebottom = (FrameLayout) findViewById(R.id.load_framebottom);
		lin_heard = (LinearLayout) findViewById(R.id.video_heard);
		lin_heard.setOnClickListener(this);
		viewblank = (FrameLayout) findViewById(R.id.load_framebank);
		viewtopblank = (View) findViewById(R.id.load_frametopbank);
		img_load = (ImageView) findViewById(R.id.img_load);
		real = (RelativeLayout) findViewById(R.id.load_real);
		img_loadback = (ImageView) findViewById(R.id.img_loadback);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
				height);
		if (advposition)
			anim = AnimationUtils.loadAnimation(mContext, R.anim.video_enter);
		else
			anim = AnimationUtils.loadAnimation(mContext,
					R.anim.video_enter_from_bottom);
		anim.setFillAfter(true);
		framebottom.setBackgroundColor(Color.WHITE);
		framebottom.startAnimation(anim);
		img_press = (ImageView) findViewById(R.id.img_video_press);
		img_press.setImageResource(R.drawable.video_play);
		setListener();
		update = new upDateSeekBar();
		new Thread(update).start();

	}

	// 更新滚动条
	class upDateSeekBar implements Runnable {

		@Override
		public void run() {
			if (!isFinish) {
				mHandler.sendMessage(Message.obtain());
				mHandler.postDelayed(update, 1000);
			}

		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (videoView == null) {
				return;
			}

			tv_currenttimer.setText(stringForTime(videoView
					.getCurrentPosition()));
			if (videoView != null) {
				seekBar(videoView.getCurrentPosition());
			}
			if (msg.what == 1) { // 下载完成，播放视频

				shared.edit()
						.putBoolean(SharedPreferencesTools.DIAL_VIDEO, true)
						.commit();
				Uri uripath = Uri.parse(getVideoPath(mContext, location));

				playVideo(uripath);
			}
			if (msg.what == 2) { // 更新进度条

				int percentcurr = msg.arg1;
				// 第三方计算进度条的方式

				numcirleBar.incrementProgressBy(percentcurr);

			}
			if (msg.what == 3) {
				if (advposition)
					anim = AnimationUtils.loadAnimation(mContext,
							R.anim.video_out);
				else

					anim = AnimationUtils.loadAnimation(mContext,
							R.anim.video_out_from_bottom);
				anim.setFillAfter(true);
				framebottom.startAnimation(anim);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						LoadVideoActivity.this.finish();

					}
				});
			}

		};
	};

	// 设置进度条
	@SuppressLint("NewApi")
	private void seekBar(long size) {
		if (videoView.isPlaying()) {
			videoView.setBackground(null);
			long mMax = videoView.getDuration();
			int sMax = seekBar.getMax();
			seekBar.setProgress((int) (size * sMax / mMax));

		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);

	}

	// 监听进度条
	private void setListener() {
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				int value = (int) (seekBar.getProgress()
						* videoView.getDuration() / seekBar.getMax());

				videoView.seekTo(value);
				videoView.start();
				isPlay = true;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				isPlay = false;
				videoView.pause();
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});

	}

	@Override
	protected void onRestart() {

		super.onRestart();
	}

	@Override
	protected void onResume() {

		if (isPlay) {
			videoView.setBackgroundColor(Color.BLACK);
			videoView.start();
		}
		if (videoView != null) {
			videoView.resume();
			isPlay = true;
		}
		if (tv_currenttimer != null)
			tv_currenttimer.setText(currtimer);

		if (hander != null)
			hander.resume();
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (hander != null) {
			hander.pause();
			hander.cancel();
		}
		if(real!=null)
			real.setVisibility(View.INVISIBLE);
		currtimer = tv_currenttimer.getText().toString();
		position = videoView.getCurrentPosition();
		isPlay = false;
		videoView.suspend();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (videoView != null) {
			videoView.stopPlayback();
			videoView = null;
		}
		super.onDestroy();
	}

	// 格式化时间
	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_heard:

			if (frametop.getVisibility() == View.GONE) {

				mHandler.sendEmptyMessageDelayed(3, 300);

			} else {

				viewtopblank.setVisibility(View.GONE);
				viewblank.setVisibility(View.GONE);
				real.setVisibility(View.INVISIBLE);
				videoView.setVisibility(View.INVISIBLE);
				img_loadback.setVisibility(View.VISIBLE);
				viewblank.setVisibility(View.VISIBLE);
				img_loadback.setImageBitmap(BitmapFactory.decodeByteArray(
						bytes, 0, bytes.length));

				if (advposition)
					anim = AnimationUtils.loadAnimation(mContext,
							R.anim.video_out);
				else
					anim = AnimationUtils.loadAnimation(mContext,
							R.anim.video_out_from_bottom);
				anim.setFillAfter(true);
				anim.setDuration(500);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						frametop.setVisibility(View.GONE);
						LoadVideoActivity.this.finish();

					}
				});

				frametop.startAnimation(anim);

			}

			break;

		default:
			break;
		}

	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onPrepared(MediaPlayer mp) {

		videoView.seekTo(position);
		if(real!=null)
		real.setVisibility(View.VISIBLE);
		videoView.start();
		isPlay = true;
		videoView.requestFocus();
		tv_endtimer.setText(stringForTime(videoView.getDuration()));
		img_press.setVisibility(View.GONE);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (isPlay) {
				videoView.pause();

				img_press.setVisibility(View.VISIBLE);
				img_press.setImageResource(R.drawable.video_play);
			} else {
				videoView.start();
				img_press.setVisibility(View.GONE);
				img_press.setVisibility(View.VISIBLE);
				img_press.setImageResource(R.drawable.video_pause);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						img_press.setVisibility(View.GONE);

					}
				}, 1000);
			}
			isPlay = !isPlay;
			return true;
		}
		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {

		shared.edit().putBoolean(SharedPreferencesTools.DIAL_VIDEO, false)
				.commit();
		new AisinBuildDialog(mContext, "提示", "无法播放此视频");
		return true;
	}

	private String getVideoPath(Context context, int location) {
		String videoPath = null;
		if (FileManager.isExistExternalStorage()) {
			
			
			videoPath = Environment.getExternalStorageDirectory()
					 + "/video_a" + location + ".mp4";
			
			
		} else {
			videoPath = context.getFilesDir().getPath() + "/video_a" + location
					+ ".mp4";
		}

		return videoPath;
	}

}
