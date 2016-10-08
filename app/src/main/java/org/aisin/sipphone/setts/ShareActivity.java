package org.aisin.sipphone.setts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: xiewenliang
 * @Filename:
 * @Description:
 * @Copyright: Copyright (c) 2016 Tuandai Inc. All rights reserved.
 * @date: 2016/10/8 15:46
 */

public class ShareActivity extends Activity implements View.OnClickListener {

    private SharedPreferences sharedShare, shared;
    private static final String mSKey = "my_invite_flag";

    private TextView tv_share_code;

    private String mStrTips;

    private String invite_url;
    private String invite_sns_message;
    private String invite_sms_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);
        tv_share_code = (TextView) findViewById(R.id.tv_share_code);
        findViewById(R.id.bt_WX).setOnClickListener(this);
        findViewById(R.id.bt_QQ).setOnClickListener(this);
        findViewById(R.id.bt_DX).setOnClickListener(this);
        findViewById(R.id.setinviteriends_bar_back).setOnClickListener(this);
        sharedShare = SharedPreferencesTools.getSharedPreferences_msglist_date_share(this);
        shared = SharedPreferencesTools.getSharedPreferences_msglist_date_share(this);
        mStrTips = sharedShare.getString(mSKey, "");
        invite_url = sharedShare.getString(
                SharedPreferencesTools.SPF_msglist_date_INVITE_URL, "");
        invite_sns_message = sharedShare.getString(
                SharedPreferencesTools.SPF_msglist_date_INVITE_SNS_MESSAGE, "");
        invite_sms_message = sharedShare.getString(
                SharedPreferencesTools.SPF_msglist_date_INVITE_SMS_MESSAGE, "");
        if (!TextUtils.isEmpty(mStrTips)) {
            tv_share_code.setText(mStrTips);
        }
        new CkeckUrl().execute("");
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,
                org.aisin.sipphone.setts.SharedActivity.class);
        intent.putExtra("invite_sns_message", invite_sns_message);
        switch (v.getId()) {
            case R.id.setinviteriends_bar_back:
                finish();
                break;
            case R.id.bt_WX:
                shared.edit().putInt("weixin", 0).commit();
                intent.putExtra("shareflag", "weixin");
                startActivity(intent);
                break;
            case R.id.bt_QQ:
                intent.putExtra("shareflag", "QQ");
                startActivity(intent);
                break;
            case R.id.bt_DX:
                Uri smsToUri = Uri.parse("smsto:");// 联系人地址
                Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
                        smsToUri);
                mIntent.putExtra(
                        "sms_body",
                        invite_sms_message
                                + invite_url.replace("phone=%s",
                                "phone=" + UserInfo_db.getUserInfo(this).getPhone()).replace(
                                "channel=%s", "channel=sms"));
                startActivity(mIntent);
                break;
        }
    }

    class CkeckUrl extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = URLTools.GetHttpURL_4getShareSurl(ShareActivity.this);
            String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject json = null;
            int doresult = 0;
            try {
                if (result != null) {
                    json = new JSONObject(result);
                    doresult = Integer.parseInt(json
                            .optString("result", "0"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (doresult) {
                case 0:
                    try {
                        String mStrTips = json.optString(mSKey);
                        if (!TextUtils.isEmpty(mStrTips)) {
                            sharedShare.edit().putString(mSKey, mStrTips).commit();
                            tv_share_code.setText(mStrTips);
                            ShareActivity.this.mStrTips = mStrTips;
                        }
                    } catch (Exception e) {
                    }
                    break;
                default:
                    break;
            }

        }
    }
}
