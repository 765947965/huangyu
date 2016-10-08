package org.aisin.sipphone.setts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Common;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.MD5;
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
 * @date: 2016/10/8 14:19
 */

public class SetInviteriendsTips extends Activity implements View.OnClickListener {
    private SharedPreferences sharedShare;
    private ShareTipsModel mShareTipsModel;
    private static final String mSKey = "mShareTipsStr";

    private TextView tv_share_tips, tv_share_num;
    private Button bt_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_share_layout);
        sharedShare = SharedPreferencesTools.getSharedPreferences_msglist_date_share(this);
        String mStrTips = sharedShare.getString(mSKey, "");
        if (!TextUtils.isEmpty(mStrTips)) {
            try {
                mShareTipsModel = JSON.parseObject(mStrTips, ShareTipsModel.class);
            } catch (Exception e) {
                mShareTipsModel = null;
            }
        }
        initView();
        new CkeckUrl().execute("");
    }

    private void initView() {
        tv_share_tips = (TextView) findViewById(R.id.tv_share_tips);
        tv_share_num = (TextView) findViewById(R.id.tv_share_num);
        bt_share = (Button) findViewById(R.id.bt_share);
        tv_share_num.setOnClickListener(this);
        bt_share.setOnClickListener(this);
        findViewById(R.id.setinviteriends_bar_back).setOnClickListener(this);
        if (mShareTipsModel != null) {
            tv_share_tips.setText(mShareTipsModel.getContent());
            tv_share_num.setText(Html.fromHtml("<u>" + mShareTipsModel.getInvite_charge_success_num() + "</u>"));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setinviteriends_bar_back:
                finish();
                break;
            case R.id.bt_share://分享
                startActivity(new Intent(this, ShareActivity.class));
                break;
            case R.id.tv_share_num://分享人数
                String url = "http://user.8hbao.com:8060/my_invite_list.php?uid=" + UserInfo_db.getUserInfo(this).getUid() + "&sign=" + MD5.toMD5(UserInfo_db.getUserInfo(this).getUid() + Common.SIGN_KEY);
                startActivity(new Intent(this, org.aisin.sipphone.setts.CheckWebView.class).putExtra("flag_name", "我的邀请人列表").putExtra("url_view", url));
                break;
        }
    }


    class CkeckUrl extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = URLTools.GetHttpURL_4Shareurl(SetInviteriendsTips.this);
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
                        ShareTipsModel mShareTipsModel = JSON.parseObject(result, ShareTipsModel.class);
                        if (mShareTipsModel != null) {
                            sharedShare.edit().putString(mSKey, result).commit();
                            tv_share_tips.setText(mShareTipsModel.getContent());
                            tv_share_num.setText(Html.fromHtml("<u>" + mShareTipsModel.getInvite_charge_success_num() + "</u>"));
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
