package org.aisin.sipphone.mai_list;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.tools.Constants;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddAisinFriend_Adapter extends BaseAdapter {
	private Context context;
	private TreeSet<Contact> contacts;
	private LayoutInflater inflater;
	private ArrayList<Contact> arraylists = new ArrayList<Contact>();

	public AddAisinFriend_Adapter(Context context, TreeSet<Contact> contacts) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		if (contacts != null) {
			this.contacts = contacts;
			this.arraylists.clear();
			this.arraylists.addAll(contacts);
		}

	}

	@Override
	public void notifyDataSetChanged() {
		if (contacts != null) {
			arraylists.clear();
			arraylists.addAll(contacts);
		}
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arraylists.size();
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
		ViewHolder_addfriend holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.addaisinfriend_adapter,
					parent, false);
			holder = new ViewHolder_addfriend();
			holder.itemaddfriend = (RelativeLayout) convertView
					.findViewById(R.id.itemaddfriend);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.phone = (TextView) convertView.findViewById(R.id.phone);
			holder.friendist_tx = (ImageView) convertView
					.findViewById(R.id.friendist_tx);
			holder.seximage = (ImageView) convertView
					.findViewById(R.id.seximage);
			holder.addbt = (TextView) convertView.findViewById(R.id.addbt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder_addfriend) convertView.getTag();
		}
		final Contact ctt = arraylists.get(position);
		String name = ctt.getFriendname();
		if (name != null && !"".equals(name) && !"null".equals(name)) {
			holder.name.setText(name);
		}
		String sex = ctt.getSex();
		if ("男".equals(sex)) {
			holder.seximage
					.setImageResource(R.drawable.one_profile_male_left_dark);
			holder.seximage.setVisibility(View.VISIBLE);
		} else if ("女".equals(sex)) {
			holder.seximage
					.setImageResource(R.drawable.one_profile_female_left_dark);
			holder.seximage.setVisibility(View.VISIBLE);
		} else {
			holder.seximage.setVisibility(View.INVISIBLE);
		}
		String phone = ctt.getFriendphone();
		if (phone != null && !"".equals(phone) && !"null".equals(phone)) {
			holder.phone.setText(phone);
		}
		File file = context.getFileStreamPath(ctt.getUid() + "headimage.jpg");
		if (file.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			if (bitmap != null) {
				holder.friendist_tx.setImageBitmap(bitmap);
			} else {
				holder.friendist_tx
						.setImageResource(R.drawable.defaultuserimage);
			}
		} else {
			holder.friendist_tx.setImageResource(R.drawable.defaultuserimage);
		}
		if (ctt.isMyfriend()) {
			holder.addbt.setText("已添加");
			holder.addbt.setEnabled(false);
		} else {
			holder.addbt.setText("添加");
			holder.addbt.setEnabled(true);
			holder.addbt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 发送广播通知添加好友
					Intent intent = new Intent(new Intent(Constants.BrandName
							+ ".addfriend"));
					intent.putExtra("uid", ctt.getUid());
					context.sendBroadcast(intent);
				}
			});
		}
		holder.itemaddfriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						org.aisin.sipphone.mai_list.Detailed_information.class);
				if (ctt.getRemark() != null && !"".equals(ctt.getRemark())
						&& !"null".equals(ctt.getRemark())) {
					intent.putExtra("Contact.name", ctt.getRemark());
				} else {
					intent.putExtra("Contact.name", ctt.getFriendphone());
				}
				intent.putExtra("Contact.showflag", ctt.isShowflag());
				intent.putExtra("Contact.isIsfreand", ctt.isIsfreand());
				intent.putExtra("Contact.isMyfriend", ctt.isMyfriend());
				intent.putStringArrayListExtra("Contact.phonelist",
						ctt.getPhonesList());
				intent.putExtra("Contact.sex", ctt.getSex());
				intent.putExtra("Contact.bthday", ctt.getBthday());
				intent.putExtra("Contact.friendname", ctt.getFriendname());
				intent.putExtra("Contact.freanduid", ctt.getUid());
				intent.putExtra("Contact.friendphone", ctt.getFriendphone());
				intent.putExtra("Contact.signature", ctt.getSignature());
				intent.putExtra("Contact.address", ctt.getProvince() + "-"
						+ ctt.getCity());
				intent.putExtra("Contact.tx_url", ctt.getUid()
						+ "headimage.jpg");
				intent.putExtra("Contact.avatarid", ctt.getAvatarid());
				context.startActivity(intent);
			}
		});
		return convertView;
	}

}

class ViewHolder_addfriend {
	RelativeLayout itemaddfriend;
	TextView name;// 昵称
	TextView phone;// 手机号
	ImageView friendist_tx;// 头像
	ImageView seximage;// 性别
	TextView addbt;// 是否添加按钮
}
