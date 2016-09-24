package org.aisin.sipphone.myview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.myview.ListViewForScrollView;
import org.aisin.sipphone.tools.DisplayUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class AisinBuildDialog extends Builder implements OnClickListener {
	private Context mcontext;
	private TextView tv_title;
	private TextView tv_message;
	private View listviewline;
	private ListViewForScrollView listitem;
	private TextView tv_confirm, tv_cancel;
	private String title, message;
	private AlertDialog.Builder build;
	private AlertDialog dialog;
	public DialogBuildConfirmListener confirmDialogListener;
	public DialogBuildCancelListener cancelDialogListener;
	private View view;

	public interface DialogBuildConfirmListener {

		void dialogConfirm();

	}

	public interface DialogBuildCancelListener {

		void dialogCancel();

	}

	public void setOnDialogConfirmListener(String name,
			DialogBuildConfirmListener confirmDialogListener) {
		this.confirmDialogListener = confirmDialogListener;
		tv_confirm.setVisibility(View.VISIBLE);
		tv_confirm.setText(name);
		tv_confirm.setOnClickListener(this);

	}

	public void setOnDialogCancelListener(String name,
			DialogBuildCancelListener cancelDialogListener) {
		this.cancelDialogListener = cancelDialogListener;
		tv_cancel.setVisibility(View.VISIBLE);
		tv_cancel.setText(name);
		tv_cancel.setOnClickListener(this);

	}

	@SuppressLint("NewApi")
	public AisinBuildDialog(Context arg0, int arg1) {
		super(arg0, arg1);
	}

	public AisinBuildDialog(Context arg0) {
		super(arg0);
		this.mcontext = arg0;
		initView();
		dialogBuild();
		dialogCreate();
	}

	private void initView() {
		view = LayoutInflater.from(mcontext)
				.inflate(R.layout.dialog_item, null);
		tv_title = (TextView) view.findViewById(R.id.dialog_title);
		tv_message = (TextView) view.findViewById(R.id.dialog_message);
		listviewline = view.findViewById(R.id.listviewline);
		listitem = (ListViewForScrollView) view.findViewById(R.id.listitem);
		tv_confirm = (TextView) view.findViewById(R.id.dialog_confirm);
		tv_cancel = (TextView) view.findViewById(R.id.dialog_cancel);
	}

	public AisinBuildDialog(Context arg0, String title, String message) {
		super(arg0);
		this.mcontext = arg0;
		initView();
		setTitle(title);
		setMessage(message);
		dialogBuild();
		dialogCreate();
		dialogShow();
		tv_confirm.setVisibility(View.VISIBLE);
		tv_confirm.setText("确定");
		tv_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();

			}
		});

	}

	private AlertDialog.Builder dialogBuild() {

		build = new AlertDialog.Builder(mcontext);
		return build;
	}

	private void setDialogsetAttributes() {
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) mcontext
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);

		int screenWidth = dm.widthPixels;

		params.width = screenWidth * 3 / 4;
		params.height = LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
	}

	public void dialogShow() {
		dialog.show();
		setDialogView();
		setDialogCancelAble(false);
		setDialogsetAttributes();
	}

	private AlertDialog dialogCreate() {
		dialog = build.create();
		return dialog;
	}

	private void setDialogView() {
		Window window = dialog.getWindow();
		window.setContentView(view);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		tv_title.setVisibility(View.VISIBLE);
		tv_title.setText(title);
	}

	private String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		tv_message.setVisibility(View.VISIBLE);
		tv_message.setText(message);
	}

	public void setMessage(Spanned message) {
		tv_message.setVisibility(View.VISIBLE);
		tv_message.setText(message);
	}

	public void setListViewItem(String[] values,
			final onMyItemClickListener oiclist) {
		setListViewItem(values, null, null, oiclist);
	}

	public void setListViewItem(ArrayList<String> arraylist,
			final onMyItemClickListener oiclist) {
		String[] values = new String[arraylist.size()];
		for (int i = 0; i < arraylist.size(); i++) {
			values[i] = arraylist.get(i);
		}
		setListViewItem(values, null, null, oiclist);
	}

	public void setListViewItem(String[] values, String[] values_f,
			Bitmap[] bitmaps, final onMyItemClickListener oiclist) {
		if (values == null || values.length == 0) {
			return;
		}
		if (bitmaps != null && values.length != bitmaps.length) {
			return;
		}
		List<Map<String, Object>> listdata = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < values.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("itemtext", values[i]);
			if (bitmaps != null) {
				map.put("itemimage", bitmaps[i]);
			}
			if (values_f != null) {
				map.put("itemtext_f", values_f[i]);
			}
			listdata.add(map);
		}
		SimpleAdapter adapter = null;
		if (bitmaps != null && values_f != null) {
			adapter = new SimpleAdapter(mcontext, listdata,
					R.layout.aisinbuilddialoglistviewitem, new String[] {
							"itemtext", "itemtext_f", "itemimage" }, new int[] {
							R.id.dialoglistitem, R.id.dialoglistitem_f,
							R.id.dialoglistimage });
			adapter.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					// 判断是否为我们要处理的对象
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView iv = (ImageView) view;
						LayoutParams lp = iv.getLayoutParams();
						lp.width = DisplayUtil.dip2px(mcontext, 50f);
						lp.height = DisplayUtil.dip2px(mcontext, 50f);
						iv.setLayoutParams(lp);
						iv.setImageBitmap((Bitmap) data);
						return true;
					}
					if (view.getId() == R.id.dialoglistitem_f) {
						if (!((String) data).equals("")) {
							((TextView) view).setText((String) data);
							view.setVisibility(View.VISIBLE);
						} else {
							view.setVisibility(View.GONE);
						}
						return true;
					}
					return false;
				}
			});
		}
		if (bitmaps != null) {
			adapter = new SimpleAdapter(mcontext, listdata,
					R.layout.aisinbuilddialoglistviewitem, new String[] {
							"itemtext", "itemimage" }, new int[] {
							R.id.dialoglistitem, R.id.dialoglistimage });
			adapter.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					// 判断是否为我们要处理的对象
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView iv = (ImageView) view;
						LayoutParams lp = iv.getLayoutParams();
						lp.width = DisplayUtil.dip2px(mcontext, 50f);
						lp.height = DisplayUtil.dip2px(mcontext, 50f);
						iv.setLayoutParams(lp);
						iv.setImageBitmap((Bitmap) data);
						return true;
					} else
						return false;
				}
			});

		} else if (values_f != null) {
			adapter = new SimpleAdapter(mcontext, listdata,
					R.layout.aisinbuilddialoglistviewitem, new String[] {
							"itemtext", "itemtext_f" }, new int[] {
							R.id.dialoglistitem, R.id.dialoglistitem_f });
			adapter.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					if (view.getId() == R.id.dialoglistitem_f) {
						if (!((String) data).equals("")) {
							((TextView) view).setText((String) data);
							view.setVisibility(View.VISIBLE);
						} else {
							view.setVisibility(View.GONE);
						}
						return true;
					}
					return false;
				}
			});
		} else {
			adapter = new SimpleAdapter(mcontext, listdata,
					R.layout.aisinbuilddialoglistviewitem,
					new String[] { "itemtext" },
					new int[] { R.id.dialoglistitem });
		}
		listitem.setAdapter(adapter);
		listitem.setVisibility(View.VISIBLE);
		listviewline.setVisibility(View.VISIBLE);
		listitem.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (oiclist != null) {
					oiclist.onItemClick(parent, view, position, id);
				}
				dialog.cancel();
			}
		});
	}

	public void setListViewItem(String[] values, String[] values2,
			Integer[] bitmapids, final onMyItemClickListener oiclist, boolean b) {
		List<Map<String, Object>> listdata = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < values.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("itemtext", values[i]);
			map.put("itemimage", bitmapids[i]);
			map.put("itemtext2", values2[i]);
			listdata.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(mcontext, listdata,
				R.layout.aisinbuilddialoglistviewitem, new String[] {
						"itemtext", "itemtext2", "itemimage" },
				new int[] { R.id.dialoglistitem, R.id.dltext,
						R.id.dialoglistimage });
		adapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// 判断是否为我们要处理的对象
				if (view.getId() == R.id.dltext) {
					if (!((String) data).equals("")) {
						((TextView) view).setText((String) data);
						view.setVisibility(View.VISIBLE);
					} else {
						view.setVisibility(View.GONE);
					}
					return true;
				}
				if (view.getId() == R.id.dialoglistimage) {
					LayoutParams lp = view.getLayoutParams();
					lp.width = DisplayUtil.dip2px(mcontext, 50f);
					lp.height = DisplayUtil.dip2px(mcontext, 50f);
					view.setLayoutParams(lp);
					return false;
				}
				return false;
			}
		});

		listitem.setAdapter(adapter);
		listitem.setVisibility(View.VISIBLE);
		listviewline.setVisibility(View.VISIBLE);
		listitem.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (oiclist != null) {
					oiclist.onItemClick(parent, view, position, id);
				}
				dialog.cancel();
			}
		});
	}

	private void setDialogCancelAble(boolean flag) {
		dialog.setCancelable(flag);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_confirm:
			if (confirmDialogListener != null) {
				confirmDialogListener.dialogConfirm();
			}
			dialog.cancel();
			break;
		case R.id.dialog_cancel:
			if (cancelDialogListener != null) {
				cancelDialogListener.dialogCancel();
			}
			dialog.cancel();
			break;
		default:
			break;
		}

	}

	public interface onMyItemClickListener {
		void onItemClick(AdapterView<?> parent, View view, int position, long id);
	}

}
