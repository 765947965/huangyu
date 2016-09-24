package org.aisin.sipphone_call_records;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.TreeSet;

import org.aisin.sipphone.CallPhoneManage;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.CallhistoryInfo;
import org.aisin.sipphone.commong.OBCallhistoryInfo;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.onMyItemClickListener;
import org.aisin.sipphone.myview.CircleImageView;
import org.aisin.sipphone.sqlitedb.CallhistoryDBTOOls;
import org.aisin.sipphone.tools.AvatarID;
import org.aisin.sipphone.tools.CursorTools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Call_records_Adapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<OBCallhistoryInfo> list = new ArrayList<OBCallhistoryInfo>();
	private TreeSet<OBCallhistoryInfo> set;
	private Handler handler;

	Call_records_Adapter(Context context, TreeSet<OBCallhistoryInfo> set,
			Handler handler) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.set = set;
		this.handler = handler;
		list.clear();
		try {
			if (set != null) {
				list.addAll(set);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		list.clear();
		try {
			if (set != null) {
				list.addAll(set);
			}
		} catch (Exception e) {
		}
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
		ViewHolder holder;
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.call_records_listview_item,
					parent, false);
			holder = new ViewHolder();
			holder.call_records_list_item_phone_1 = (TextView) convertView
					.findViewById(R.id.call_records_list_item_phone_1);
			holder.call_records_list_item_phone_2 = (TextView) convertView
					.findViewById(R.id.call_records_list_item_phone_2);
			holder.call_records_list_item_flag_image = (ImageView) convertView
					.findViewById(R.id.call_records_list_item_flag_image);
			holder.calllist_tx = (CircleImageView) convertView
					.findViewById(R.id.calllist_tx);
			holder.call_records_list_item_time = (TextView) convertView
					.findViewById(R.id.call_records_list_item_time);
			holder.call_records_list_item_image = (ImageView) convertView
					.findViewById(R.id.call_records_list_item_image);
			holder.call_records_list_item = (LinearLayout) convertView
					.findViewById(R.id.call_records_list_item);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();

		}

		final OBCallhistoryInfo obch = list.get(position);
		final CallhistoryInfo clhi = obch.getSet().first();
		final String name = clhi.getName();
		final String phone = clhi.getPhone();
		if (name == null || "".equals(name)) {
			holder.call_records_list_item_phone_1.setText(phone);
			holder.call_records_list_item_phone_2.setText("");
		} else {
			holder.call_records_list_item_phone_1.setText(name);
			holder.call_records_list_item_phone_2.setText(phone);
		}
		final int avatarid = AvatarID.getAvatarID();
		try {
			if (obch.getHeadbitmap() != null) {
				holder.calllist_tx.setImageBitmap(obch.getHeadbitmap());
			} else {
				holder.calllist_tx.setImageResource(avatarid);
			}
		} catch (Exception e) {
			holder.calllist_tx.setImageResource(avatarid);
		}
		holder.call_records_list_item_flag_image.setImageResource(clhi
				.getCall_type_imageid());

		holder.call_records_list_item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 打电话
				if (obch.getHeadbitmap() != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					obch.getHeadbitmap().compress(Bitmap.CompressFormat.PNG,
							100, baos);
					byte[] bitmapByte = baos.toByteArray();
					CallPhoneManage.callPhone(context, bitmapByte, name, phone);
				} else {
					CallPhoneManage.callPhone(context, null, name, phone);
				}

			}
		});
		holder.call_records_list_item
				.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						AisinBuildDialog mybuild = new AisinBuildDialog(context);
						mybuild.setListViewItem(new String[] { "清除联系人最近通话记录",
								"清除联系人全部通话记录" }, new onMyItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								if (position == 0) {
									if (clhi.isIsself()) {
										CallhistoryDBTOOls.DeletedataBYW(
												context, "ID=?",
												new String[] { clhi.get_id() });
									} else {
										CursorTools.DeletedataBYW(context,
												"_id=?",
												new String[] { clhi.get_id() });
									}
									if (handler != null) {
										handler.sendEmptyMessage(2);
									}
									new AisinBuildDialog(context, "提示", "删除成功！");
								} else if (position == 1) {
									CallhistoryDBTOOls.DeletedataBYW(context,
											"phone=?",
											new String[] { clhi.getPhone() });
									CursorTools.DeletedataBYW(context,
											CallLog.Calls.NUMBER + "=?",
											new String[] { clhi.getPhone() });
									if (handler != null) {
										handler.sendEmptyMessage(2);
									}
									new AisinBuildDialog(context, "提示", "删除成功！");
								}
							}
						});
						mybuild.setOnDialogCancelListener("取消", null);
						mybuild.dialogShow();
						return true;
					}
				});
		holder.call_records_list_item_time.setText(clhi.getChainal_call_time());
		holder.call_records_list_item_image
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								context,
								org.aisin.sipphone_call_records.Call_details.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("TXID", avatarid);
						bundle.putSerializable("phone", obch.getPhone());
						bundle.putSerializable("set", obch.getSet());
						Bitmap bitmap = obch.getHeadbitmap();
						if (bitmap != null) {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.PNG, 100,
									baos);
							byte[] bitmapByte = baos.toByteArray();
							bundle.putSerializable("history.tx", bitmapByte);
						}
						intent.putExtras(bundle);
						context.startActivity(intent);
					}
				});
		return convertView;
	}
}

class ViewHolder {
	TextView call_records_list_item_phone_1;
	TextView call_records_list_item_phone_2;
	ImageView call_records_list_item_flag_image;
	CircleImageView calllist_tx;
	TextView call_records_list_item_time;
	ImageView call_records_list_item_image;
	LinearLayout call_records_list_item;
}
