package org.aisin.sipphone.mai_list;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.myview.CircleImageView;

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

public class MailList_Adapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Contact> contacts_self = new ArrayList<Contact>();
	private TreeMap<String, Integer> szm_map_old;
	private TreeMap<String, Integer> szm_map = new TreeMap<String, Integer>();
	private ArrayList<Contact> contacts_old;

	public MailList_Adapter(Context context, ArrayList<Contact> contacts_old,
			TreeMap<String, Integer> szm_map) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		TreeSet<Contact> contacts_px = new TreeSet<Contact>();// 排序集合
		this.contacts_old = contacts_old;
		contacts_px.addAll(contacts_old);
		contacts_self.clear();
		this.contacts_self.addAll(contacts_px);
		this.szm_map_old = szm_map;
		this.szm_map.putAll(szm_map);
		contacts_px.clear();
		contacts_px = null;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		contacts_self.clear();
		TreeSet<Contact> contacts_px = new TreeSet<Contact>();// 排序集合
		contacts_px.addAll(contacts_old);
		contacts_self.addAll(contacts_px);
		contacts_px.clear();
		contacts_px = null;
		szm_map.clear();
		szm_map.putAll(szm_map_old);
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contacts_self.size();
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
		final Contact ctt = contacts_self.get(position);
		ViewHolder3 holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.maillist_adapter_item,
					parent, false);
			holder = new ViewHolder3();
			holder.mllai_item = (LinearLayout) convertView
					.findViewById(R.id.mllai_item);
			holder.item_szm = (TextView) convertView
					.findViewById(R.id.item_szm);
			holder.maillist_tx = (CircleImageView) convertView
					.findViewById(R.id.maillist_tx);
			holder.isaixinfriendimage = (ImageView) convertView
					.findViewById(R.id.isaixinfriendimage);
			holder.maillist_name_2_phone = (TextView) convertView
					.findViewById(R.id.maillist_name_2_phone);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder3) convertView.getTag();
		}
		try {
			if (ctt.isShowflag()) {
				if (ctt.getTx_fread() != null) {
					holder.maillist_tx.setImageBitmap(ctt.getTx_fread());
				} else {
					holder.maillist_tx.setImageResource(ctt.getAvatarid());
				}
			} else {
				if (ctt.getTx() != null) {
					holder.maillist_tx.setImageBitmap(ctt.getTx());
				} else {
					holder.maillist_tx.setImageResource(ctt.getAvatarid());
				}
			}
		} catch (Exception e) {
			holder.maillist_tx.setImageResource(ctt.getAvatarid());
		}
		if (ctt.getRemark() == null || "".equals(ctt.getRemark())) {
			if (ctt.isShowflag()) {
				if (ctt.getFriendname() != null
						&& !"".equals(ctt.getFriendname())
						&& !"null".equals(ctt.getFriendname())) {
					holder.maillist_name_2_phone.setText(ctt.getFriendname());
				} else {
					holder.maillist_name_2_phone.setText(ctt.getFriendphone());
				}
				holder.maillist_name_2_phone.setText(ctt.getFriendphone());
			} else {
				holder.maillist_name_2_phone
						.setText(ctt.getPhonesList().get(0));
			}
		} else {
			holder.maillist_name_2_phone.setText(ctt.getRemark());
		}

		if (position == szm_map.get(ctt.getF_PY())) {
			if ("~".equals(ctt.getF_PY())) {
				holder.item_szm.setText("#");
			} else {
				holder.item_szm.setText(ctt.getF_PY().toUpperCase());
			}
			holder.item_szm.setVisibility(View.VISIBLE);
		} else {
			holder.item_szm.setVisibility(View.GONE);
		}
		if (ctt.isIsfreand()) {
			holder.isaixinfriendimage.setVisibility(View.VISIBLE);
		} else {
			holder.isaixinfriendimage.setVisibility(View.INVISIBLE);
		}
		holder.mllai_item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {// 响应点击条目
				Intent intent = new Intent(context,
						org.aisin.sipphone.mai_list.Detailed_information.class);
				if (ctt.getRemark() != null && !"".equals(ctt.getRemark())) {
					intent.putExtra("Contact.name", ctt.getRemark());
				} else {
					if (ctt.isShowflag()) {
						if (ctt.getFriendname() != null
								&& !"".equals(ctt.getFriendname())
								&& !"null".equals(ctt.getFriendname())) {
							intent.putExtra("Contact.name", ctt.getFriendname());
						} else {
							intent.putExtra("Contact.name",
									ctt.getFriendphone());
						}
					} else {
						intent.putExtra("Contact.name", ctt.getPhonesList()
								.get(0));
					}
				}
				intent.putExtra("Contact.contactID", ctt.getContractID());
				intent.putExtra("Contact.showflag", ctt.isShowflag());
				intent.putExtra("Contact.isIsfreand", ctt.isIsfreand());
				intent.putExtra("Contact.isMyfriend", ctt.isMyfriend());
				intent.putStringArrayListExtra("Contact.phonelist",
						ctt.getPhonesList());
				Bitmap bitmap = null;
				if (ctt.isIsfreand()) {
					intent.putExtra("Contact.sex", ctt.getSex());
					intent.putExtra("Contact.bthday", ctt.getBthday());
					intent.putExtra("Contact.friendname", ctt.getFriendname());
					intent.putExtra("Contact.freanduid", ctt.getUid());
					intent.putExtra("Contact.friendphone", ctt.getFriendphone());
					intent.putExtra("Contact.signature", ctt.getSignature());
					intent.putExtra("Contact.address", ctt.getProvince() + "-"
							+ ctt.getCity());
				}
				if (ctt.isShowflag()) {
					if (ctt.getTx_fread() != null) {
						intent.putExtra("Contact.tx_url", ctt.getUid()
								+ "headimage.jpg");
					}
				} else {
					bitmap = ctt.getTx();
				}
				if (bitmap != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					byte[] bitmapByte = baos.toByteArray();
					intent.putExtra("Contact.tx", bitmapByte);
				}
				intent.putExtra("Contact.avatarid", ctt.getAvatarid());
				context.startActivity(intent);
			}
		});
		return convertView;
	}
}

class ViewHolder3 {
	LinearLayout mllai_item;
	TextView item_szm;
	CircleImageView maillist_tx;
	ImageView isaixinfriendimage;
	TextView maillist_name_2_phone;
}
