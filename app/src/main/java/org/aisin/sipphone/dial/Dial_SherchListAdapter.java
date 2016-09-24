package org.aisin.sipphone.dial;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.TreeSet;

import org.aisin.sipphone.CallPhoneManage;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.sqlitedb.GetPhoneInfo4DB;
import org.aisin.sipphone.tools.Highlightmatch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Dial_SherchListAdapter extends BaseAdapter {
	public static final int MLIST = 3, DF = 1;
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Contact> contacts_scresult = new ArrayList<Contact>();
	private int flag;
	private TreeSet<Contact> contacts_scresult_set;
	private boolean showinfo = false;

	public Dial_SherchListAdapter(Context context,
			TreeSet<Contact> contacts_scresult_set, int flag) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.flag = flag;
		if (contacts_scresult_set != null) {
			this.contacts_scresult_set = contacts_scresult_set;
			this.contacts_scresult.clear();
			this.contacts_scresult.addAll(contacts_scresult_set);
			if (contacts_scresult.size() < 10) {
				showinfo = true;
			} else {
				showinfo = false;
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		if (contacts_scresult_set != null) {
			contacts_scresult.clear();
			contacts_scresult.addAll(contacts_scresult_set);
		}
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return contacts_scresult.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder5 holder;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.dial_sherchlistadapter_item, parent, false);
			holder = new ViewHolder5();
			holder.dial_sherchlistadapter_item_layout = (LinearLayout) convertView
					.findViewById(R.id.dial_sherchlistadapter_item_layout);
			holder.dial_sherchlistadapter_item_name = (TextView) convertView
					.findViewById(R.id.dial_sherchlistadapter_item_name);
			holder.dial_sherchlistadapter_item_phone = (TextView) convertView
					.findViewById(R.id.dial_sherchlistadapter_item_phone);
			holder.dial_sherchlistadapter_item_image = (ImageView) convertView
					.findViewById(R.id.dial_sherchlistadapter_item_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder5) convertView.getTag();
		}
		final Contact contact = contacts_scresult.get(position);
		String phonenum = null;
		String mysherchtext = null;
		if (flag == this.DF) {
			mysherchtext = contact.getMatchstr();
		} else if (flag == this.MLIST) {
			mysherchtext = contact.getMatchstr_mailist();
		}
		if (contact.isShowflag()) {
			phonenum = contact.getFriendphone();
		} else {
			for (String phone : contact.getPhonesList()) {
				if (phone.indexOf(mysherchtext) > -1) {
					phonenum = phone;
					break;
				}
			}
			if (phonenum == null) {
				phonenum = contact.getPhonesList().get(0);// 没有匹配到号码
			}
		}
		if (showinfo) {
			ArrayList<String> phones = new ArrayList<String>();
			phones.add(phonenum);
			try {
				phonenum = phonenum + " "
						+ GetPhoneInfo4DB.getInfo(context, phones).get(0);
			} catch (Exception e) {
			}
		}
		Highlightmatch.phonenumMatch(contact, phonenum,
				holder.dial_sherchlistadapter_item_phone, mysherchtext);// 对号码进行高亮处理
		String name = contact.getRemark();
		if (name == null || "".equals(name)) {
			name = phonenum;
		}
		Highlightmatch.nameMatch(contact, name,
				holder.dial_sherchlistadapter_item_name, mysherchtext);// 对姓名匹配的高亮处理
		holder.dial_sherchlistadapter_item_layout
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 打电话
						CallPhoneManage.callPhone(context, contact);
					}
				});

		holder.dial_sherchlistadapter_item_image
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {// 进入联系人详细信息

						// 响应点击条目
						Intent intent = new Intent(
								context,
								org.aisin.sipphone.mai_list.Detailed_information.class);
						if (contact.getRemark() != null
								&& !"".equals(contact.getRemark())) {
							intent.putExtra("Contact.name", contact.getRemark());
						} else {
							if (contact.isShowflag()) {
								if (contact.getFriendname() != null
										&& !"".equals(contact.getFriendname())
										&& !"null".equals(contact
												.getFriendname())) {
									intent.putExtra("Contact.name",
											contact.getFriendname());
								} else {
									intent.putExtra("Contact.name",
											contact.getFriendphone());
								}
							} else {
								intent.putExtra("Contact.name", contact
										.getPhonesList().get(0));
							}
						}
						intent.putExtra("Contact.contactID",
								contact.getContractID());
						intent.putExtra("Contact.showflag",
								contact.isShowflag());
						intent.putExtra("Contact.isIsfreand",
								contact.isIsfreand());
						intent.putExtra("Contact.isMyfriend",
								contact.isMyfriend());
						intent.putStringArrayListExtra("Contact.phonelist",
								contact.getPhonesList());
						Bitmap bitmap = null;
						if (contact.isIsfreand()) {
							intent.putExtra("Contact.sex", contact.getSex());
							intent.putExtra("Contact.bthday",
									contact.getBthday());
							intent.putExtra("Contact.friendname",
									contact.getFriendname());
							intent.putExtra("Contact.freanduid",
									contact.getUid());
							intent.putExtra("Contact.friendphone",
									contact.getFriendphone());
							intent.putExtra("Contact.signature",
									contact.getSignature());
							intent.putExtra(
									"Contact.address",
									contact.getProvince() + "-"
											+ contact.getCity());
						}
						if (contact.isShowflag()) {
							if (contact.getTx_fread() != null) {
								intent.putExtra("Contact.tx_url",
										contact.getUid() + "headimage.jpg");
							}
						} else {
							bitmap = contact.getTx();
						}
						if (bitmap != null) {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.PNG, 100,
									baos);
							byte[] bitmapByte = baos.toByteArray();
							intent.putExtra("Contact.tx", bitmapByte);
						}
						intent.putExtra("Contact.avatarid",
								contact.getAvatarid());
						context.startActivity(intent);

					}
				});
		return convertView;
	}
}

class ViewHolder5 {
	LinearLayout dial_sherchlistadapter_item_layout;
	TextView dial_sherchlistadapter_item_name;
	TextView dial_sherchlistadapter_item_phone;
	ImageView dial_sherchlistadapter_item_image;
}
