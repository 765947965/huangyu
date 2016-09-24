package com.weixin.paydemo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import android.util.Xml;

import org.aisin.sipphone.aipay.Productinfos;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.tools.UserInfo_db;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.xmlpull.v1.XmlPullParser;

public class PayActivity {
	private Context context;
	private PayReq req;
	private IWXAPI msgApi;
	private Map<String, String> resultunifiedorder;
	private StringBuffer sb;
	private final String TAG = "PayActivity";
	private Productinfos ppcfnoew;
	private Handler MFhandler;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 生成签名参数
				genPayReq();
				// 支付
				sendPayReq();
				String packageSign = MD5.getMessageDigest(
						sb.toString().getBytes()).toUpperCase();
			}
		}
	};

	public PayActivity(Context context, Productinfos ppcfnoew, Handler MFhandler) {
		this.context = context;
		this.ppcfnoew = ppcfnoew;
		this.MFhandler = MFhandler;
		msgApi = WXAPIFactory.createWXAPI(context, null);
		req = new PayReq();
		sb = new StringBuffer();
		msgApi.registerApp(WXContacts.APP_ID);
	}

	public void Pay() {
		// 生成prepay_id
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();
	}

	/**
	 * 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(WXContacts.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		return packageSign;
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(WXContacts.API_KEY);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		return appSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		return sb.toString();
	}

	private class GetPrepayIdTask extends
			AsyncTask<Void, Void, Map<String, String>> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (result == null) {
				MFhandler.sendEmptyMessage(3);
				return;
			}
			sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
			resultunifiedorder = result;
			mHandler.sendEmptyMessage(1);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {

			try {
				String url = String
						.format("http://user.zjtytx.com:8060/wxpay/pay/appapi.php");
				String entity = genProductArgs();


				byte[] buf = Util.httpPost(url, entity);
				if (buf == null) {
					return null;
				}

				String content = new String(buf);

				JSONObject json = new JSONObject(content);
				int result = json.optInt("result", -14);
				if (result != 0) {
					if (result == -14) {
						new AisinBuildDialog(context, "提示", "网络不可用,请检查网络连接!");
					} else if (result == -1) {
						new AisinBuildDialog(context, "提示", "提交参数不正确!");
					} else if (result == -2) {
						new AisinBuildDialog(context, "提示", "欲充值的用户不存在!");
					} else if (result == -3) {
						new AisinBuildDialog(context, "提示", "欲购买的商品不存在!");
					} else if (result == -4 || result == -5) {
						new AisinBuildDialog(context, "提示", "微信下单失败!");
					}
					return null;
				}
				content = genMyxml(json);
				Map<String, String> xml = decodeXml(content);
				return xml;
			} catch (Exception e) {
				new AisinBuildDialog(context, "提示", "网络不可用,请检查网络连接!");
				return null;
			}
		}
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
		}
		return null;

	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
		Date date = new Date();
		String strKey = format.format(date);
		java.util.Random r = new java.util.Random();
		strKey = strKey + Math.abs(r.nextInt());
		strKey = strKey.substring(0, 17);
		strKey = strKey + "629502";
		return strKey;
	}

	private String genMyxml(JSONObject json) {
		try {
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("return_code", "<![CDATA["
					+ json.optString("return_code") + "]]>"));
			packageParams.add(new BasicNameValuePair("return_msg", "<![CDATA["
					+ json.optString("return_msg") + "]]>"));
			packageParams.add(new BasicNameValuePair("appid", "<![CDATA["
					+ WXContacts.APP_ID + "]]>"));
			packageParams.add(new BasicNameValuePair("mch_id", "<![CDATA["
					+ json.optString("mch_id") + "]]>"));
			packageParams.add(new BasicNameValuePair("nonce_str", "<![CDATA["
					+ json.optString("nonceStr") + "]]>"));
			packageParams.add(new BasicNameValuePair("sign", "<![CDATA["
					+ json.optString("paySign") + "]]>"));
			packageParams.add(new BasicNameValuePair("result_code", "<![CDATA["
					+ json.optString("result_code") + "]]>"));
			packageParams.add(new BasicNameValuePair("prepay_id", "<![CDATA["
					+ json.optString("prepay_id") + "]]>"));
			packageParams.add(new BasicNameValuePair("trade_type",
					"<![CDATA[APP]]>"));
			String xmlstring = toXml(packageParams);
			return xmlstring;
		} catch (Exception e) {
			return null;
		}
	}

	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams
					.add(new BasicNameValuePair("appid", WXContacts.APP_ID));
			packageParams
					.add(new BasicNameValuePair("body", ppcfnoew.getBody()));// 商品简述
			packageParams.add(new BasicNameValuePair("product_id", ppcfnoew
					.getGoodsId()));
			// packageParams.add(new BasicNameValuePair("mch_id",
			// WXContacts.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			// packageParams.add(new BasicNameValuePair("notify_url",
			// WXContacts.NOTIFY_URL));
			// packageParams.add(new BasicNameValuePair("out_trade_no",
			// genOutTradNo()));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					"8.8.8.8"));
			packageParams.add(new BasicNameValuePair("total_fee", ppcfnoew
					.getPrice() * 100 + ""));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			// String sign = genPackageSign(packageParams);
			// packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);

			return "uid=" + UserInfo_db.getUserInfo(context).getUid() + "&xml="
					+ xmlstring;

		} catch (Exception e) {
			return null;
		}

	}

	private void genPayReq() {

		req.appId = WXContacts.APP_ID;
		req.partnerId = WXContacts.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n" + req.sign + "\n\n");


	}

	private void sendPayReq() {

		msgApi.registerApp(WXContacts.APP_ID);
		msgApi.sendReq(req);
	}

}
