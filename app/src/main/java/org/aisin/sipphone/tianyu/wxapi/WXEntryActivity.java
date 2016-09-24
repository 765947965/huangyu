package org.aisin.sipphone.tianyu.wxapi;

import org.aisin.sipphone.HttpTask_SharedAdd_Red;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weixin.paydemo.WXContacts;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI mWxApi;
	private String appid = WXContacts.APP_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mWxApi = WXAPIFactory.createWXAPI(this, appid, false);
		mWxApi.handleIntent(getIntent(), this);
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		mWxApi.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResp(BaseResp baseResp) {
		SharedPreferences shared = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(WXEntryActivity.this);
		int num = shared.getInt("weixin", 0);
		shared.edit().putInt("weixin", 0).commit();//不管成功失败 都应该置0
		switch (baseResp.errCode) {
		case BaseResp.ErrCode.ERR_OK:

			if (num == 1)
				//new HttpTask_SharedAdd_Red(this).execute("shared_add");
			break;

		case BaseResp.ErrCode.ERR_SENT_FAILED:

			break;

		case BaseResp.ErrCode.ERR_USER_CANCEL:

			break;

		}
		this.finish();

	}

}
