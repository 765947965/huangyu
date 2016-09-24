package org.aisin.sipphone.setts;

import java.util.ArrayList;
import java.util.TreeMap;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.tools.Highlightmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonalRedEnvelopeAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Contact> contacts_w;
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private TreeMap<String, Integer> szm_map;
	private String[] searchs;
	private TreeMap<String, Contact> addreds;

	public PersonalRedEnvelopeAdapter(Context context,
			ArrayList<Contact> contacts, TreeMap<String, Integer> szm_map,
			String[] searchs, TreeMap<String, Contact> addreds) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.contacts.addAll(contacts);
		this.contacts_w = contacts;
		this.szm_map = szm_map;
		this.searchs = searchs;
		this.addreds = addreds;
	}

	@Override
	public void notifyDataSetChanged() {
		contacts.clear();
		contacts.addAll(contacts_w);
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contacts.size();
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
		ViewHolder_personredeven holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.personalredenvelopeadapter,
					parent, false);
			holder = new ViewHolder_personredeven();
			holder.item_radiobutton = (ImageView) convertView
					.findViewById(R.id.item_radiobutton);
			holder.pre_tx = (ImageView) convertView.findViewById(R.id.pre_tx);
			holder.nametext = (TextView) convertView
					.findViewById(R.id.nametext);
			holder.phonetext = (TextView) convertView
					.findViewById(R.id.phonetext);
			holder.item_szm = (TextView) convertView
					.findViewById(R.id.item_szm);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder_personredeven) convertView.getTag();
		}
		Contact ctt = contacts.get(position);
		if (ctt.isShowflag()) {

			try {
				if (ctt.getTx_fread() != null) {
					holder.pre_tx.setImageBitmap(ctt.getTx_fread());
				} else {
					holder.pre_tx.setImageResource(ctt.getAvatarid());
				}
			} catch (Exception e) {
				holder.pre_tx.setImageResource(ctt.getAvatarid());
			}
			if (ctt.getRemark() != null) {
				if (searchs[0] != null && !"".equals(searchs[0])) {
					Highlightmatch.nameMatch(ctt, ctt.getRemark(),
							holder.nametext, searchs[0]);// 对姓名匹配的高亮处
				} else {
					holder.nametext.setText(ctt.getRemark());
				}
			} else if (ctt.getFriendname() != null) {
				if (searchs[0] != null && !"".equals(searchs[0])) {
					Highlightmatch.nameMatch(ctt, ctt.getFriendname(),
							holder.nametext, searchs[0]);// 对姓名匹配的高亮处
				} else {
					holder.nametext.setText(ctt.getFriendname());
				}
			} else {
				if (searchs[0] != null && !"".equals(searchs[0])) {
					Highlightmatch.nameMatch(ctt, ctt.getFriendphone(),
							holder.nametext, searchs[0]);// 对姓名匹配的高亮处
				} else {
					holder.nametext.setText(ctt.getFriendphone());
				}
			}
			if (searchs[0] != null && !"".equals(searchs[0])) {
				Highlightmatch.phonenumMatch(ctt, ctt.getFriendphone(),
						holder.phonetext, searchs[0]);// 对号码进行高亮处理
			} else {
				holder.phonetext.setText(ctt.getFriendphone());
			}
			if (addreds.get(ctt.getUid()) != null) {
				holder.item_radiobutton.setImageResource(R.drawable.aor);
			} else {
				holder.item_radiobutton.setImageResource(R.drawable.aos);
			}
		} else {
			try {
				if (ctt.getTx() != null) {
					holder.pre_tx.setImageBitmap(ctt.getTx());
				} else {
					holder.pre_tx.setImageResource(ctt.getAvatarid());
				}
			} catch (Exception e) {
				holder.pre_tx.setImageResource(ctt.getAvatarid());
			}
			if (ctt.getRemark() != null) {
				if (searchs[0] != null && !"".equals(searchs[0])) {
					Highlightmatch.nameMatch(ctt, ctt.getRemark(),
							holder.nametext, searchs[0]);
				} else {
					holder.nametext.setText(ctt.getRemark());
				}
			} else {
				if (searchs[0] != null && !"".equals(searchs[0])) {
					Highlightmatch.nameMatch(ctt, ctt.getPhonesList().get(0),
							holder.nametext, searchs[0]);
				} else {
					holder.nametext.setText(ctt.getPhonesList().get(0));
				}
			}
			if (searchs[0] != null && !"".equals(searchs[0])) {
				Highlightmatch.phonenumMatch(ctt, ctt.getPhonesList().get(0),
						holder.phonetext, searchs[0]);// 对号码进行高亮处理
			} else {
				holder.phonetext.setText(ctt.getPhonesList().get(0));
			}
			if (addreds.get(ctt.getPhonesList().get(0)) != null) {
				holder.item_radiobutton.setImageResource(R.drawable.aor);
			} else {
				holder.item_radiobutton.setImageResource(R.drawable.aos);
			}
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
		return convertView;
	}
}

class ViewHolder_personredeven {
	ImageView item_radiobutton;
	ImageView pre_tx;
	TextView nametext;
	TextView phonetext;
	TextView item_szm;
}