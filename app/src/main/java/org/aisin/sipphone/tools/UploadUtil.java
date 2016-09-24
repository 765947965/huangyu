package org.aisin.sipphone.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.aisin.sipphone.AisinApp;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

public class UploadUtil {
	
	private static final String TAG = "uploadFile";
    
	 /**
     * android上传文件到服务器并在上传完成后删除日志文件
     * 
     * @param RequestURL  请求的rul 
     * @param upFileName 上传文件名字
     * 
     * @return 服务器返回值，为0时说明上传成功
     */
    public static int uploadFile(String RequestURL, File upFileName) {
    	String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		String result = "";
		try
		{
			URL url = new URL(RequestURL);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
			
			DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
							+ upFileName.getName() + "\"" + end);
			dos.writeBytes(end);

			FileInputStream fis;
			fis = new FileInputStream(upFileName);
				
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();
			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();
			int res = httpURLConnection.getResponseCode();
			Log.e(TAG, "response code:" + res);
			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			result = br.readLine();
			dos.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int re = -14;
		JSONObject json = null;
		try{
			if (result != null) {
				json = new JSONObject(result);
				re = json.optInt("result",-14);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return re;
    }

}
