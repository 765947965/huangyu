package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.UserInfo_db;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SetAccount extends Activity implements OnClickListener {
	private LinearLayout setaccount_linlayout;
	private ImageView setaccount_bar_back;
	private RelativeLayout setts_switch_on_relayout;
	private RelativeLayout setts_change_number_relayout;
	private RelativeLayout setts_retrieve_password_relayout;
	private RelativeLayout setts_change_password_relayout;
	private TextView setaccout_sign_out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setaccount);
		setaccount_linlayout = (LinearLayout) this
				.findViewById(R.id.setaccount_linlayout);
		setaccount_bar_back = (ImageView) this
				.findViewById(R.id.setaccount_bar_back);
		setts_switch_on_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_switch_on_relayout);
		setts_change_number_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_change_number_relayout);
		setts_retrieve_password_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_retrieve_password_relayout);
		setts_change_password_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_change_password_relayout);
		setaccout_sign_out = (TextView) this
				.findViewById(R.id.setaccout_sign_out);
		setaccount_bar_back.setOnClickListener(this);
		setts_switch_on_relayout.setOnClickListener(this);
		setts_change_number_relayout.setOnClickListener(this);
		setts_retrieve_password_relayout.setOnClickListener(this);
		setts_change_password_relayout.setOnClickListener(this);
		setaccout_sign_out.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.setaccount_bar_back:
			finish();
			break;
		case R.id.setts_switch_on_relayout:
			Intent intent = new Intent(SetAccount.this,
					org.aisin.sipphone.setts.Switch_on.class);
			startActivity(intent);
			break;
		case R.id.setts_change_number_relayout:
			Intent intent2 = new Intent(SetAccount.this,
					org.aisin.sipphone.setts.SetCnumber_account.class);
			startActivity(intent2);
			break;
		case R.id.setts_retrieve_password_relayout:
			Intent intent3 = new Intent(SetAccount.this,
					org.aisin.sipphone.setts.SetRetrievePassword4V2.class);
			startActivity(intent3);
			break;
		case R.id.setts_change_password_relayout:
			Intent intent4 = new Intent(SetAccount.this,
					org.aisin.sipphone.setts.ChangePassword.class);
			startActivity(intent4);
			break;
		case R.id.setaccout_sign_out:
			AisinBuildDialog mybuild = new AisinBuildDialog(SetAccount.this);
			mybuild.setTitle("提示");
			mybuild.setMessage("确定退出登录吗?");
			mybuild.setOnDialogCancelListener("取消", null);
			mybuild.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							// 清除数据
							CheckUpadateTime.ReSetValue(SetAccount.this);
							// 清除用户数据
							UserInfo_db.SaveUserInfo(SetAccount.this, "", "",
									"", "");
							SetAccount.this.finish();
						}
					});
			mybuild.dialogShow();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(setaccount_linlayout);// 回收容器
	}
}
