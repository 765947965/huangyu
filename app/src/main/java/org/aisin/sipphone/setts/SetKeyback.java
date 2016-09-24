package org.aisin.sipphone.setts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.onMyItemClickListener;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.FileManager;
import org.aisin.sipphone.tools.GetPathFromUri4kitkat;
import org.aisin.sipphone.tools.RecoveryTools;

import com.lidroid.xutils.BitmapUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SetKeyback extends Activity implements OnClickListener {
	private LinearLayout removelayout;
	private ImageView setkeyback_bar_back;
	private ListView listview;
	private SetKeybackAdapter adapter;
	private BitmapUtils bitmapUtils;
	private keyBroadcast brodcast;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private String ALBUM_PATH = FileManager.getFileDir() + "/"
			+ Constants.BrandName + "/" + Constants.KEYBACK;
	private File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());
	private File cutfile;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				adapter.notifyDataSetChanged();
			} else if (msg.what == 2) {
				showheadimagechange();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setkeybacklayout);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		setkeyback_bar_back = (ImageView) this
				.findViewById(R.id.setkeyback_bar_back);
		listview = (ListView) this.findViewById(R.id.listview);
		removelayout.setOnClickListener(this);
		setkeyback_bar_back.setOnClickListener(this);
		bitmapUtils = new BitmapUtils(this);
		// listview.setOnScrollListener(new PauseOnScrollListener(bitmapUtils, false, true));
		adapter = new SetKeybackAdapter(this, mHandler,bitmapUtils);
		listview.setAdapter(adapter);
		brodcast = new keyBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BrandName + ".keybackUP");
		registerReceiver(brodcast, filter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.setkeyback_bar_back) {
			setResult(2, null);
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(2, null);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (brodcast != null) {
			unregisterReceiver(brodcast);
		}
		RecoveryTools.unbindDrawables(removelayout);// 回收容器
	}

	// 接收更新红包事物提醒的广播 清除拨号盘广播
	private class keyBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.BrandName + ".keybackUP")) {
				mHandler.sendEmptyMessage(1);
			}
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

	private void showheadimagechange() {
		AisinBuildDialog mybuild = new AisinBuildDialog(SetKeyback.this);
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

					// File file = new File(FileManager.getFileDir() + "/"
					// + Constants.BrandName + "/" + Constants.KEYBACK);
					// if (!file.exists()) {
					// file.mkdirs();
					// }
					// cutfile = new File(FileManager.getFileDir() + "/"
					// + Constants.BrandName + "/" + Constants.KEYBACK
					// + "/" + System.currentTimeMillis() + ".jpg");
					// Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
					// null)
					// .setType("image/*")
					// .putExtra("crop", "true")
					// .putExtra("aspectX", 1)
					// .putExtra("aspectY", 1)
					// .putExtra("outputX", 480)
					// .putExtra("outputY", 367)
					// .putExtra("scale", true)
					// // 黑边
					// .putExtra("noFaceDetection", true)
					// .putExtra("scaleUpIfNeeded", true)
					// // 黑边
					// .putExtra(MediaStore.EXTRA_OUTPUT,
					// Uri.fromFile(cutfile))
					// .putExtra("outputFormat",
					// Bitmap.CompressFormat.JPEG.toString());
					// startActivityForResult(intent, PHOTO_REQUEST_CUT);
				}
			}
		});
		mybuild.setOnDialogCancelListener("取消", null);
		mybuild.dialogShow();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:// 当选择拍照时调用
			startPhotoZoom(Uri.fromFile(tempFile));
			break;
		case PHOTO_REQUEST_GALLERY:// 当选择从本地获取图片时
			// 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
			if (data != null) {
				String path = GetPathFromUri4kitkat.getPath(SetKeyback.this,
						data.getData());
				startPhotoZoom(Uri.parse("file:///" + path));
			} else {
			}
			break;
		case PHOTO_REQUEST_CUT:// 返回的结果
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					String path = GetPathFromUri4kitkat.getPath(
							SetKeyback.this, data.getData());
					// 保存图像
					try {
						String fileabname = Uri.parse("file:///" + path)
								.getPath();
						String filename_to = ALBUM_PATH + "/"
								+ System.currentTimeMillis() + ".png";
						File file = new File(ALBUM_PATH);
						if (!file.exists()) {
							file.mkdirs();
						}
						FileInputStream finput = new FileInputStream(fileabname);
						FileOutputStream outf = new FileOutputStream(
								filename_to);
						byte[] bts = new byte[1024];
						int num = 0;
						while ((num = finput.read(bts)) != -1) {
							outf.write(bts, 0, num);
						}
						outf.flush();
						outf.close();
						finput.close();
						new File(fileabname).delete();
						Intent intent = new Intent(SetKeyback.this,
								org.aisin.sipphone.setts.ShowSetKeyback.class);
						intent.putExtra("filename", filename_to);
						intent.putExtra("zdyflag", true);
						SetKeyback.this.startActivity(intent);
					} catch (Exception e) {
						new AisinBuildDialog(SetKeyback.this, "提示",
								"该截图工具与软件不兼容,请重新选择截图工具!");
					}
				} else {
					if (cutfile != null && cutfile.exists()) {
						Intent intent = new Intent(SetKeyback.this,
								org.aisin.sipphone.setts.ShowSetKeyback.class);
						intent.putExtra("filename", cutfile.getAbsolutePath());
						intent.putExtra("zdyflag", true);
						SetKeyback.this.startActivity(intent);
					} else {
						new AisinBuildDialog(SetKeyback.this, "提示",
								"该截图工具与软件不兼容,请重新选择截图工具!");
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		File file = new File(FileManager.getFileDir() + "/"
				+ Constants.BrandName + "/" + Constants.KEYBACK);
		if (!file.exists()) {
			file.mkdirs();
		}
		cutfile = new File(FileManager.getFileDir() + "/" + Constants.BrandName
				+ "/" + Constants.KEYBACK + "/" + System.currentTimeMillis()
				+ ".jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cutfile));// 图片输出
		intent.putExtra("scale", true);
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 480);
		intent.putExtra("aspectY", 367);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", 480);
		intent.putExtra("outputY", 367);
		intent.putExtra("return-data", false);// 是否返回图像数据
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("scaleUpIfNeeded", true);// 图片不够大的话 就自动拉伸
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}
}
