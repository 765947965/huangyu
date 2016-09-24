package org.aisin.sipphone.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class DBManager {
	private int Rid;// 数据库文件原始ID
	private String DB_NAME; // 保存的数据库文件名
	private String PACKAGE_NAME;
	private String DB_PATH; // 在手机里存放数据库的位置

	private Context context;

	public DBManager(Context context, int Rid, String outfilepath) {
		this.context = context;
		this.Rid = Rid;
		this.DB_NAME = outfilepath;
		PACKAGE_NAME = context.getPackageName();
		DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()
				+ "/" + PACKAGE_NAME;
	}

	public void openDatabase() {
		this.openDatabase(DB_PATH + "/" + DB_NAME);
	}

	private void openDatabase(String dbfile) {
		try {
			if (!(new File(dbfile).exists())) {// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
				InputStream is = context.getResources().openRawResource(Rid); // 欲导入的数据库
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[102400];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
		}
	}

	public void openDatabaseFromZIP() {
		String absolutePath = DB_PATH;
		try {
			if (!new File(DB_PATH + "/" + DB_NAME).exists()) {
				ZipUtil.unZipFolder(
						context.getResources().openRawResource(Rid),
						absolutePath);
			}
		} catch (Exception e) {
		}
	}
}
