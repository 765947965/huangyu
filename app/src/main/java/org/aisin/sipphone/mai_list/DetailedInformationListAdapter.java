package org.aisin.sipphone.mai_list;

import java.util.ArrayList;

import org.aisin.sipphone.CallPhoneManage;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.directcall.AisinManager;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailedInformationListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<String> phonelist;
	private ArrayList<String> phonelist_gsd;
	private byte[] bitmapByte;
	private String name;
	private String aixinphone;

	DetailedInformationListAdapter(Context context,
			ArrayList<String> phonelist, ArrayList<String> phonelist_gsd,
			byte[] bitmapByte, String name, String aixinphone) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.phonelist = phonelist;
		this.phonelist_gsd = phonelist_gsd;
		this.bitmapByte = bitmapByte;
		this.name = name;
		this.aixinphone = aixinphone;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return phonelist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder4 holder;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.detailed_information_list_item, parent, false);
			holder = new ViewHolder4();
			holder.detailed_information_list_item_rl = (RelativeLayout) convertView
					.findViewById(R.id.detailed_information_list_item_rl);
			holder.aixinphone = (ImageView) convertView
					.findViewById(R.id.aixinphone);
			holder.detailed_information_list_item_phone = (TextView) convertView
					.findViewById(R.id.detailed_information_list_item_phone);
			holder.list_item_phone_info = (TextView) convertView
					.findViewById(R.id.list_item_phone_info);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder4) convertView.getTag();
		}
		final int position_final = position;
		holder.detailed_information_list_item_phone.setText(phonelist
				.get(position));
		if (aixinphone != null && aixinphone.equals(phonelist.get(position))) {
			holder.aixinphone.setVisibility(View.VISIBLE);
		} else {
			holder.aixinphone.setVisibility(View.INVISIBLE);
		}
		try {
			holder.list_item_phone_info.setText(phonelist_gsd.get(position));
		} catch (Exception e1) {
			holder.list_item_phone_info.setText("");
		}
		holder.detailed_information_list_item_rl
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						boolean OKB = true;
						try {
							// 打电话
							LinphoneCore lc = AisinManager.getLc();
							LinphoneCall call = lc.getCurrentCall();

							if (call != null) {
								Toast toast = Toast.makeText(context, "正在通话中！",
										3000);

								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								OKB = false;
							}
						} catch (Exception e) {
						}
						if (OKB) {
							CallPhoneManage.callPhone(context, bitmapByte,
									name, phonelist.get(position_final));
						}
					}
				});
		return convertView;
	}

}

class ViewHolder4 {
	TextView detailed_information_list_item_phone, list_item_phone_info;
	ImageView aixinphone;
	RelativeLayout detailed_information_list_item_rl;
}
