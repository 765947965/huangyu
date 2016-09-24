package org.aisin.sipphone.tianyu.wxapi;

import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weixin.paydemo.WXContacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, WXContacts.APP_ID);

		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, resp.errStr + "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			String message = "";
			if (resp.errCode == 0) {
				message = "支付成功,请稍后查询余额！";
			} else if (resp.errCode == -1) {
				message = "支付失败！";
			} else if (resp.errCode == -2) {
				message = "交易取消！";
			}
			AisinBuildDialog mybuild = new AisinBuildDialog(
					WXPayEntryActivity.this);
			mybuild.setTitle("提示");
			mybuild.setMessage(message);
			mybuild.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							WXPayEntryActivity.this.finish();
						}
					});
			mybuild.dialogShow();
		}
	}
}