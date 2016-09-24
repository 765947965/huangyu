package org.aisin.sipphone.tools;

import java.io.File;

import org.aisin.sipphone.AisinApp;

import android.content.Context;
import android.os.Environment;

public class FileManager {

	/**
	 * 判断该文件是否存在且文件大小大于0
	 * 
	 * @param path
	 *            文件的路径
	 * @return 文件存在返回true，否则返回false
	 */
	public static boolean isExist(String path) {
		File file = new File(path);
		return (file.exists() && file.length() > 0);
	}

	/**
	 * 删除单个文件
	 * 
	 * @param filePath
	 *            被删除文件的文件名
	 * @return 文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}

	/**
	 * 删除文件夹以及目录下的文件
	 * 
	 * @param filePath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteFileDirectory(String filePath) {
		boolean flag = false;
		// 如果filePath不以文件分隔符结尾，自动添加文件分隔符
		if (!filePath.endsWith(File.separator)) {
			filePath += File.separator;
		}
		File dirFile = new File(filePath);
		if (dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		File[] files = dirFile.listFiles();
		// 遍历删除文件夹下的所有文件(包括子目录)
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				// 删除子文件
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				// 删除子目录
				flag = deleteFileDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前空目录
		return dirFile.delete();
	}

	// /**
	// * 根据路径删除指定的目录或文件，无论存在与否
	// *
	// * @param filePath
	// * 要删除的目录或文件
	// * @return 删除成功返回 true，否则返回 false。
	// */
	// public static boolean DeleteFolder(String filePath) {
	// File file = new File(filePath);
	// if (!file.exists()) {
	// return false;
	// } else {
	// if (file.isFile()) {
	// // 为文件时调用删除文件方法
	// return deleteFile(filePath);
	// } else {
	// // 为目录时调用删除目录方法
	// return deleteFileDirectory(filePath);
	// }
	// }
	// }

	/**
	 * 根据路径删除指定的目录下指定后缀的文件
	 * 
	 * @param filePath
	 *            要删除的目录
	 * @param fileSuffix
	 *            要删除文件的后缀
	 * @return
	 */
	public static void DeleteSuffix(String filePath, String Suffix) {
		File file = new File(filePath);// 里面输入特定目录
		File temp = null;
		File[] filelist = file.listFiles();
		for (int i = 0; i < filelist.length; i++) {
			temp = filelist[i];
			// 获得文件名，如果后缀为“”，这个你自己写，就删除文件
			if (temp.getName().endsWith(Suffix)) {
				temp.delete();// 删除文件
			}
		}
	}

	public static boolean isExistExternalStorage() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;

	}

	// 获取SD卡路径
	public static String getFileDir() {

		Context context = AisinApp.getInstance().getApplicationContext();

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory() + "";
		} else {
			return context.getFilesDir() + "";
		}
	}
}
