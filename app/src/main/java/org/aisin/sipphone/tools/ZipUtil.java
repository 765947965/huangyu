package org.aisin.sipphone.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;

/**
 * Android Zip压缩解压缩
 * 
 * @author ronald ([url]www.r-base.net[/url])
 */
public final class ZipUtil {
	
	private ZipUtil() {}

	/**
	 * 取得压缩包中的 文件列表(文件夹,文件自选)
	 * 
	 * @param zipFileString
	 *            压缩包名字
	 * @param bContainFolder
	 *            是否包括 文件夹
	 * @param bContainFile
	 *            是否包括 文件
	 * @return
	 * @throws Exception
	 */
	public static List<File> getFileList(String zipFileString, 
			boolean bContainFolder, boolean bContainFile) throws Exception {
		
		List<File> fileList = new ArrayList<File>();
		ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
		ZipEntry zipEntry;
		String szName = "";
		while ((zipEntry = inZip.getNextEntry()) != null) {
			szName = zipEntry.getName();
			if (zipEntry.isDirectory()) {
				// get the folder name of the widget
				szName = szName.substring(0, szName.length() - 1);
				File folder = new File(szName);
				if (bContainFolder) {
					fileList.add(folder);
				}
			} else {
				File file = new File(szName);
				if (bContainFile) {
					fileList.add(file);
				}
			}
		}// end of while
		inZip.close();
		return fileList;
	}

	/**
	 * 返回压缩包中的文件InputStream
	 * 
	 * @param zipFilePath
	 *            压缩文件的名字
	 * @param fileString
	 *            解压文件的名字
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream upZip(String zipFilePath,
			String fileString) throws Exception {
		ZipFile zipFile = new ZipFile(zipFilePath);
		ZipEntry zipEntry = zipFile.getEntry(fileString);

		return zipFile.getInputStream(zipEntry);
	}

	/**
	 * 解压一个压缩文档 到指定位置
	 * 
	 * @param zipFileString
	 *            压缩包的名字
	 * @param outPathString
	 *            指定的路径
	 * @throws Exception
	 */
	public static void unZipFolder(InputStream input, String outPathString)
			throws Exception {
		ZipInputStream inZip = new ZipInputStream(input);
		ZipEntry zipEntry = null;
		String szName = "";

		while ((zipEntry = inZip.getNextEntry()) != null) {
			szName = zipEntry.getName();
			if (zipEntry.isDirectory()) {
				// get the folder name of the widget
				szName = szName.substring(0, szName.length() - 1);
				File folder = new java.io.File(outPathString
						+ File.separator + szName);
				folder.mkdirs();
			} else {
				File file = new File(outPathString + File.separator + szName);
				file.createNewFile();
				// get the output stream of the file
				FileOutputStream out = new FileOutputStream(file);
				int len;
				byte[] buffer = new byte[1024];
				// read (len) bytes into buffer
				while ((len = inZip.read(buffer)) != -1) {
					// write (len) byte from buffer at the position 0
					out.write(buffer, 0, len);
					out.flush();
				}
				out.close();
			}
		}// end of while
		inZip.close();
	}

	/**
	 * 解压一个压缩文档 到指定位置
	 * 
	 * @param zipFileString
	 *            压缩包的名字
	 * @param outPathString
	 *            指定的路径
	 * @throws Exception
	 */
	public static void unZipFolder(String zipFileString, String outPathString)
			throws Exception {
		unZipFolder(new FileInputStream(zipFileString), outPathString);
	}// end of func

	/**
	 * 压缩文件,文件夹
	 * 
	 * @param srcFilePath
	 *            要压缩的文件/文件夹名字
	 * @param zipFilePath
	 *            指定压缩的目录和名字
	 * @throws Exception
	 */
	//public static void zipFolder(String srcFilePath, String zipFilePath)
	public static void zipFolder(String srcFilePath, String zipFilePath)
			throws Exception {
		// 创建Zip包
		ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFilePath));

		// 打开要输出的文件
		File file = new File(srcFilePath);

		// 压缩
		zipFiles(file.getParent() + File.separator, file.getName(),outZip);

		// 完成,关闭
		outZip.finish();
		outZip.close();
	}// end of func

	/**
	 * 压缩文件
	 * 
	 * @param folderPath
	 * @param filePath
	 * @param zipOut
	 * @throws Exception
	 */
	private static void zipFiles(String folderPath, String filePath,
			ZipOutputStream zipOut) throws Exception {
		if (zipOut == null) {
			return;
		}

		File file = new File(folderPath + filePath);

		// 判断是不是文件
		if (file.isFile()) {
			ZipEntry zipEntry = new ZipEntry(filePath);
			FileInputStream inputStream = new FileInputStream(file);
			zipOut.putNextEntry(zipEntry);

			int len;
			byte[] buffer = new byte[4096];

			while ((len = inputStream.read(buffer)) != -1) {
				zipOut.write(buffer, 0, len);
			}

			zipOut.closeEntry();
		} else {
			// 文件夹的方式,获取文件夹下的子文件
			String fileList[] = file.list();

			// 如果没有子文件, 则添加进去即可
			if (fileList.length <= 0) {
				ZipEntry zipEntry = new ZipEntry(filePath + java.io.File.separator);
				zipOut.putNextEntry(zipEntry);
				zipOut.closeEntry();
			}

			// 如果有子文件, 遍历子文件
			for (int i = 0; i < fileList.length; i++) {
				zipFiles(folderPath, filePath + File.separator
						+ fileList[i], zipOut);
			}// end of for

		}// end of if

	}// end of func
}
