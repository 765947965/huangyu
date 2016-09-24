package org.aisin.sipphone.setts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.tools.CursorTools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Red_listviewAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<RedObject> redobs_arry;
	private SimpleDateFormat sdformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdformat_2 = new SimpleDateFormat("MM月dd日-HH:mm");
	private SimpleDateFormat sdformat_3 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdformat_4 = new SimpleDateFormat("MM月dd日");
	private SimpleDateFormat sdformat_5 = new SimpleDateFormat("yyyyMMdd");
	// 今天
	private int datestr_today;

	private int listnums;// 展示的条目数

	private TreeSet<Contact> friendslist = new TreeSet<Contact>();

	public Red_listviewAdapter(Context context, ArrayList<RedObject> redobs_arry) {
		this.context = context;
		friendslist.clear();
		friendslist.addAll(CursorTools.friendslist);
		this.redobs_arry = redobs_arry;
		if (redobs_arry.size() > 10) {
			listnums = 10;
		} else {
			listnums = redobs_arry.size();
		}
		datestr_today = Integer.parseInt(sdformat_5.format(new Date()));
	}

	public int getListnums() {
		return listnums;
	}

	public void setListnums(int listnums) {
		this.listnums = listnums;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listnums;
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
		ViewHolder8 holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.red_listviewadapter, parent, false);
			holder = new ViewHolder8();
			holder.eltvsdapter_item = (LinearLayout) convertView
					.findViewById(R.id.eltvsdapter_item);
			holder.red_has_open_image = (ImageView) convertView
					.findViewById(R.id.red_has_open_image);
			holder.red_type_text = (TextView) convertView
					.findViewById(R.id.red_type_text);
			holder.red_moneys = (TextView) convertView
					.findViewById(R.id.red_moneys);
			holder.red_creatime_text = (TextView) convertView
					.findViewById(R.id.red_creatime_text);
			holder.red_has_open_text = (TextView) convertView
					.findViewById(R.id.red_has_open_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder8) convertView.getTag();
		}

		RedObject robct = redobs_arry.get(position);
		if (robct.getDirect().equals("sended")) {
			try {
				holder.red_type_text.setText("发出: "
						+ sdformat_2.format(sdformat.parse(robct
								.getCreate_time().trim())));

				String status = robct.getStatus();
				if ("has_sended".equals(status)) {
					holder.red_has_open_text.setText("领取中...");
				} else if ("has_packed".equals(status)) {
					holder.red_has_open_text.setText("已包好");
				} else if ("has_ended".equals(status)) {
					if (robct.getHas_open() == Integer.parseInt(robct
							.getSplitsnumber().trim())) {
						holder.red_has_open_text.setText("已领完");
					} else {
						holder.red_has_open_text.setText("已过期");
					}
				} else {
					holder.red_has_open_text.setText("");
				}
				if ("logindaily".equals(robct.getType().trim())
						|| robct.getType().trim().endsWith("_money")
						|| robct.getType().trim().endsWith("_right")) {
					if (robct.getType().trim().endsWith("_right")) {
						holder.red_has_open_image
								.setImageResource(R.drawable.red_quan);
					} else {
						holder.red_has_open_image
								.setImageResource(R.drawable.red_huafei);
					}
					// 环宇每日登录红包或者金钱红包
					double money_temp = Double.parseDouble(robct.getMoney())
							/ (double) 100;
					double returned_money_d = Double.parseDouble(robct
							.getReturned_money()) / (double) 100;
					double received_money_d = Double.parseDouble(robct
							.getReceived_money()) / (double) 100;
					holder.red_creatime_text.setText("已领取 "
							+ robct.getHas_open() + "/"
							+ robct.getSplitsnumber() + "个,共"
							+ received_money_d + "元/" + money_temp + "元");
					if (returned_money_d != 0) {
						holder.red_has_open_text.setText("已结束  已退回"
								+ returned_money_d + "元");
					}
				} else if (robct.getType().trim().endsWith("_month")) {
					holder.red_has_open_image
							.setImageResource(R.drawable.red_huafei);
					holder.red_creatime_text.setText("已领取 "
							+ robct.getHas_open() + "/"
							+ robct.getSplitsnumber() + "个,共"
							+ robct.getReceived_money() + "天/"
							+ robct.getMoney() + "天");
					// 赠送的天红包
					if (!"0".equals(robct.getReturned_money())) {
						holder.red_has_open_text.setText("已结束  已退回"
								+ robct.getReturned_money() + "天");
					}
				} else if (robct.getType().trim().endsWith("_4gdata")) {
					holder.red_has_open_image
							.setImageResource(R.drawable.red_liuliang);
					if (Integer.parseInt(robct.getMoney().trim()) > 1024) {
						holder.red_creatime_text
								.setText("已领取 "
										+ robct.getHas_open()
										+ "/"
										+ robct.getSplitsnumber()
										+ "个,共"
										+ Math.round((Integer.parseInt(robct
												.getReceived_money().trim()) / (double) 1024) * 10)
										/ 10.0
										+ "MB/"
										+ Math.round((Integer.parseInt(robct
												.getMoney().trim()) / (double) 1024) * 10)
										/ 10.0 + "MB");
					} else {
						holder.red_creatime_text.setText("已领取 "
								+ robct.getHas_open() + "/"
								+ robct.getSplitsnumber() + "个,共"
								+ robct.getReceived_money() + "KB/"
								+ robct.getMoney() + "KB");
					}
					if (!"0".equals(robct.getReturned_money())) {
						if (Integer.parseInt(robct.getMoney().trim()) > 1024) {
							holder.red_has_open_text
									.setText("已结束  已退回"
											+ Math.round((Integer
													.parseInt(robct
															.getReturned_money()
															.trim()) / (double) 1024) * 10)
											/ 10.0 + "MB");

						} else {
							holder.red_has_open_text.setText("已结束  已退回"
									+ robct.getReturned_money() + "KB");
						}
					}
				}

				// 设置金额或者流量
				if ("logindaily".equals(robct.getType())
						|| robct.getType().endsWith("_money")
						|| robct.getType().endsWith("_right")) {
					holder.red_moneys.setText(Double.parseDouble(robct
							.getMoney().trim()) / (double) 100 + "元");
				} else if (robct.getType().endsWith("_month")) {
					holder.red_moneys.setText(robct.getMoney().trim() + "天");
				} else if (robct.getType().endsWith("_4gdata")) {
					double tempd = Double.parseDouble(robct.getMoney().trim());
					if (tempd > 1024) {
						holder.red_moneys.setText(tempd / (double) 1024 + "MB");
					} else {
						holder.red_moneys.setText(tempd + "KB");
					}
				}
			} catch (Exception e) {
			}
		} else {
			try {
				// 设置发红包方
				if (robct.getFromnickname() != null
						&& !"".equals(robct.getFromnickname().trim())) {
					holder.red_type_text.setText("红包: "
							+ robct.getFromnickname().trim() + "的"
							+ robct.getName());
				} else {
					if (robct.getFrom() != null
							&& !"".equals(robct.getFrom().trim())) {
						if ("system".equals(robct.getFrom().trim())) {
							holder.red_type_text.setText("红包: 环宇的"
									+ robct.getName());
						} else {
							// 匹配通讯录或者UID
							boolean tempf = true;
							// 匹配通讯录
							for (Long lg : CursorTools.cttmap.keySet()) {
								Contact ctt = CursorTools.cttmap.get(lg);
								if (ctt == null) {
									continue;
								}
								for (String strp : ctt.getPhonesList()) {
									if (robct.getFrom().trim()
											.equals(strp.trim())) {
										if (ctt.getRemark() != null
												&& !"".equals(ctt.getRemark())) {
											holder.red_type_text.setText("红包: "
													+ ctt.getRemark().trim()
													+ "的" + robct.getName());
											tempf = false;// 成功匹配到 置为假
											break;
										}
									}
								}
							}
							if (tempf) {
								// 匹配UID
								Iterator<Contact> ittor = friendslist
										.iterator();
								while (ittor.hasNext()) {
									Contact ctt = ittor.next();
									if (robct.getFrom().trim()
											.equals(ctt.getUid())) {
										String name = ctt.getFriendname();
										if (name == null || "".equals(name)
												|| "null".equals(name)) {
											name = ctt.getFriendphone();
										}
										holder.red_type_text.setText("红包: "
												+ name + "的" + robct.getName());
										tempf = false;// 成功匹配到 置为假
										break;
									}
								}
							}
							// 都没有匹配成功直接设置号码
							if (tempf) {
								// 没有成功匹配到
								holder.red_type_text.setText("红包: "
										+ robct.getFrom().trim() + "的"
										+ robct.getName());
							}
						}
					}
				}

				String type = robct.getType();
				if (type == null) {
					type = "";
				}
				// 设置红包金额或者流量
				if (robct.getHas_open() == 1) {
					if ("logindaily".equals(type) || type.endsWith("_money")
							|| type.endsWith("_right")) {
						if (type.trim().endsWith("_right")) {
							holder.red_has_open_image
									.setImageResource(R.drawable.red_quan);
						} else {
							holder.red_has_open_image
									.setImageResource(R.drawable.red_huafei);
						}
						holder.red_moneys.setText(Double.parseDouble(robct
								.getMoney().trim()) / (double) 100 + "元");
					} else if (type.endsWith("_month")) {
						holder.red_has_open_image
								.setImageResource(R.drawable.red_huafei);
						holder.red_moneys
								.setText(robct.getMoney().trim() + "天");
					} else if (type.endsWith("_4gdata")) {
						holder.red_has_open_image
								.setImageResource(R.drawable.red_liuliang);
						double tempd = Double.parseDouble(robct.getMoney()
								.trim());
						if (tempd > 1024) {
							holder.red_moneys.setText(Math
									.round((tempd / (double) 1024) * 10)
									/ 10.0
									+ "MB");
						} else {
							holder.red_moneys.setText(tempd + "KB");
						}
					}
				} else {
					holder.red_moneys.setText("");
				}

				String creatime = robct.getCreate_time();
				if (creatime != null && creatime.trim().length() == 19) {
					try {
						Date date = sdformat.parse(creatime.trim());
						holder.red_creatime_text.setText("收到: "
								+ sdformat_2.format(date));
					} catch (ParseException e) {
					}
				}
				// (如果是未拆红包)定义未拆红包是否过期
				boolean pasedata = false;// true为过期
				if (robct.getHas_open() == 0) {
					try {
						String exp_time = robct.getExp_time();
						if (exp_time != null && exp_time.trim().length() == 10) {
							// 过期日期
							int datestr = Integer.parseInt(robct.getExp_time()
									.trim().replaceAll("-", "")
									.replaceAll(" ", "").replaceAll(":", ""));
							pasedata = datestr_today > datestr;
						}
					} catch (Exception e) {
					}
				}

				if (robct.getHas_open() == 0) {
					// 设置图片
					holder.red_has_open_image
							.setImageResource(R.drawable.not_open);
					if (pasedata) {
						holder.red_moneys.setText("已失效");
					}
					// 格式化日期
					String exp_time = robct.getExp_time();
					if (exp_time != null && exp_time.trim().length() == 10) {
						try {
							Date date = sdformat_3.parse(exp_time.trim());
							holder.red_has_open_text.setText("未拆: "
									+ sdformat_4.format(date) + " 前有效");
						} catch (ParseException e) {
						}
					}
				} else if (robct.getHas_open() == 1) {
					// 设置图片
					// holder.red_has_open_image
					// .setImageResource(R.drawable.has_been_opened_less);
					// 格式化日期
					String open_time = robct.getOpen_time();
					if (open_time != null && open_time.length() == 19) {
						try {
							Date date = sdformat.parse(open_time.trim());
							holder.red_has_open_text.setText("拆开: "
									+ sdformat_2.format(date));
						} catch (ParseException e) {
						}
					}
				}
			} catch (Exception e) {
			}
		}

		return convertView;
	}

	class ViewHolder8 {
		ImageView red_has_open_image;
		TextView red_type_text;
		TextView red_moneys;
		TextView red_creatime_text;
		TextView red_has_open_text;
		LinearLayout eltvsdapter_item;
	}

}
