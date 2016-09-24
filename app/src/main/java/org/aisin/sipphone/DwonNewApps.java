package org.aisin.sipphone;

import java.io.File;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.FileManager;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class DwonNewApps extends Activity implements OnClickListener {

	private String durl;
	private String ALBUM_PATH = FileManager.getFileDir() + "/"
			+ Constants.BrandName + "/";
	private String fileabsoulutename;

	private long mTotal;// 文件长度
	private long mCurrent;// 已下载长度

	private boolean downallFlag = false;// 是否下载完毕
	private boolean ztjx = true;// 暂停或者继续下载

	private TextView appname;
	private TextView size;
	private TextView sizijindu;
	private TextView cancel;
	private TextView sureoraz;
	private TextView jindutiao_h;
	private TextView jindutiao_q;
	private HttpHandler handler;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				int progress = (int) (((double) mCurrent / (double) mTotal) * 100d);
				sizijindu.setText(progress + "%");
				String temp = ((mTotal / ((double) (1024 * 1024))) * 10) / 10.0
						+ "";
				temp = temp.substring(0, temp.indexOf(".") + 2) + "MB";
				size.setText(temp);
				LayoutParams lp = jindutiao_q.getLayoutParams();
				lp.width = (int) (jindutiao_h.getWidth() * ((double) progress / (double) 100));
				jindutiao_q.setLayoutParams(lp);
			} else if (msg.what == 2) {
				downallFlag = true;
				sizijindu.setText("100%");
				sureoraz.setText("安装");
				LayoutParams lp = jindutiao_q.getLayoutParams();
				lp.width = jindutiao_h.getWidth();
				jindutiao_q.setLayoutParams(lp);
				Intent install = new Intent(Intent.ACTION_VIEW);
				install.setDataAndType(
						Uri.fromFile(new File(fileabsoulutename)),
						"application/vnd.android.package-archive");
				install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				DwonNewApps.this.startActivity(install);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		durl = this.getIntent().getStringExtra("durl");
		if (durl == null || !durl.startsWith("http")) {
			finish();
			return;
		}
		setContentView(R.layout.downnewapp);
		appname = (TextView) this.findViewById(R.id.appname);
		size = (TextView) this.findViewById(R.id.size);
		sizijindu = (TextView) this.findViewById(R.id.sizijindu);
		cancel = (TextView) this.findViewById(R.id.cancel);
		sureoraz = (TextView) this.findViewById(R.id.sureoraz);
		jindutiao_h = (TextView) this.findViewById(R.id.jindutiao_h);
		jindutiao_q = (TextView) this.findViewById(R.id.jindutiao_q);
		cancel.setOnClickListener(this);
		sureoraz.setOnClickListener(this);
		String filename = Constants.BrandName
				+ SharedPreferencesTools
						.getSharedPreferences_msglist_date_share(this)
						.getString(SharedPreferencesTools.upAPPVer, "0.0");
		fileabsoulutename = ALBUM_PATH + filename + ".apk";
		appname.setText(filename);
		sizijindu.setText("0%");
		sureoraz.setText("暂停");
		downlonds();
	}

	private void downlonds() {
		HttpUtils http = new HttpUtils();
		handler = http.download(durl, fileabsoulutename, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
				false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				new RequestCallBack<File>() {
					@Override
					public void onStart() {
						// testTextView.setText("conn...");
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						mTotal = total;
						mCurrent = current;
						mHandler.sendEmptyMessage(1);
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						downallFlag = true;
						sureoraz.setText("安装");
						Intent install = new Intent(Intent.ACTION_VIEW);
						install.setDataAndType(
								Uri.fromFile(new File(fileabsoulutename)),
								"application/vnd.android.package-archive");
						install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						DwonNewApps.this.startActivity(install);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						if (416 == error.getExceptionCode()) {
							mHandler.sendEmptyMessage(2);
						} else if (0 == error.getExceptionCode()) {
							appname.setText("文件下载失败,请检查网络连接");
						} else {
							appname.setText("文件下载失败,请稍后重试");
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.cancel) {
			if (!downallFlag) {
				handler.cancel();
			}
			DwonNewApps.this.finish();
		} else if (id == R.id.sureoraz && !downallFlag) {
			if (ztjx) {
				ztjx = false;
				sureoraz.setText("继续");
				handler.cancel();
			} else {
				ztjx = true;
				sureoraz.setText("暂停");
				downlonds();
			}
		} else if (id == R.id.sureoraz && downallFlag) {
			Intent install = new Intent(Intent.ACTION_VIEW);
			install.setDataAndType(Uri.fromFile(new File(fileabsoulutename)),
					"application/vnd.android.package-archive");
			install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			DwonNewApps.this.startActivity(install);
		}
	}
}
