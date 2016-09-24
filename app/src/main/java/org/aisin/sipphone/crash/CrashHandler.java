package org.aisin.sipphone.crash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aisin.sipphone.AisinApp;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.FileManager;
import org.aisin.sipphone.tools.UploadUtil;
import org.aisin.sipphone.tools.UserInfo_db;
import org.aisin.sipphone.tools.ZipUtil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

/**
 * 捕捉应用奔溃日志
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";
	public static final String URL_UPLOAD_RUNNINGLOG = "http://mobile.zjtytx.com:8899/testupload";
	public String path = FileManager.getFileDir() + "/" + Constants.BrandName
			+ "/crash";
	public static File zipFileName;
	private String fileNametext;
	private boolean flag = true;

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE;
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			String fileName = "";
			try {
				// Thread.sleep(1000);
				if (FileManager.isExist(path)) {
					UserInfo user = UserInfo_db.getUserInfo(mContext);
					long timestamp = System.currentTimeMillis();
					String time = formatter.format(new Date());
					// 得到程序版本
					PackageManager manager = mContext.getPackageManager();
					PackageInfo info;
					String version = null;
					try {
						info = manager.getPackageInfo(
								mContext.getPackageName(), 0);
						version = info.versionName;
					} catch (NameNotFoundException e) {
					}
					// 将日志文件压缩成.zip格式,压缩包格式为： 手机号+时间
					fileName = path + "/" + user.getPhone() + "_"
							+ Constants.BrandName + "_" + version + "_" + time
							+ timestamp + ".zip";
					zipFileName = new File(fileName);
					ZipUtil.zipFolder(fileNametext, fileName);
				}
			} catch (InterruptedException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 将日志文件上传至服务器,如果压缩包存在 ,并且设备已经连接网络
			if (!fileName.equals("")
					&& FileManager.isExist(fileName)
					&& Check_network.isNetworkAvailable(AisinApp.getInstance()
							.getApplicationContext())) {
				new CallTask().execute(URL_UPLOAD_RUNNINGLOG);
			}
			try {
				Thread.sleep(1000 * 3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				/*
				 * Looper.prepare(); Toast.makeText(mContext,
				 * "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG) .show();
				 * Looper.loop();
				 */
			}
		}.start();
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		if (result.indexOf("versionName=") == -1) {// 如果没有发现本应用版本
			flag = false;
		}
		if (getErroreisold(result)) {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			fileNametext = path + "/crash-" + time + "-" + timestamp + ".log";
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			FileOutputStream fos = new FileOutputStream(fileNametext);
			fos.write(sb.toString().getBytes());
			fos.close();
			return fileNametext;
		} catch (Exception e) {
		}
		return null;
	}

	private class CallTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			int re = -14;
			if (flag) {
				// re = UploadUtil.uploadFile(params[0], zipFileName);
			} else {
				re = 0;
			}
			try {
				File fl = new File(path);
				if (fl.exists()) {
					for (File flz : fl.listFiles()) {
						flz.delete();
					}
				}
			} catch (Exception e) {
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}

	// 获取历史类型
	private boolean getErroreisold(String error) {
		try {
			int num0 = error.indexOf("at org.aisin.sipphone");
			if (num0 == -1) {
				return false;
			}
			int num1 = error.indexOf("(", num0);
			int num2 = error.indexOf(")", num0);
			if (num1 == -1 || num2 == -1) {
				return false;
			}
			error = error.substring(num1 + 1, num2);
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info;
			String version = "";
			try {
				info = manager.getPackageInfo(mContext.getPackageName(), 0);
				version = info.versionName;
			} catch (NameNotFoundException e) {
			}
			File fl = new File(path + "/errors" + version);
			if (fl.exists()) {
				FileInputStream in = new FileInputStream(fl);
				StringBuilder strb = new StringBuilder();
				byte[] byts = new byte[1024];
				int num = 0;
				while ((num = in.read(byts)) != -1) {
					strb.append(new String(byts, 0, num, "UTF-8"));
				}
				in.close();
				for (String e : strb.toString().split("aerr")) {
					if (error.equals(e)) {
						return true;
					}
				}
			}
			FileOutputStream out = new FileOutputStream(fl, true);
			out.write((error + "aerr").getBytes("UTF-8"));
			out.flush();
			out.close();
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
