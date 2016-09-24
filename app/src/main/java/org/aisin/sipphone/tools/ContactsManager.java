package org.aisin.sipphone.tools;

import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.widget.Toast;

public class ContactsManager {

	private Context mContext = null;

	public ContactsManager(Context context) {
		mContext = context;
	}

	/**
	 * 添加一个新的联系人数据
	 * 
	 * @param name
	 * @param phoneNumber
	 * @return 是否添加成功
	 */
	public void add(String name, List<String> phoneNumbers) {
		// 根据号码找数据，如果存在则不添加，因为有号码但无名字是不允许的
		// if (!findNameByPhoneNumber(phoneNumber)) {
		// 插入raw_contacts表，并获取_id属性
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = mContext.getContentResolver();
		ContentValues values = new ContentValues();
		long contact_id = ContentUris.parseId(resolver.insert(uri, values));
		// 插入data表
		uri = Uri.parse("content://com.android.contacts/data");
		// 添加姓名
		values.put("raw_contact_id", contact_id);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/name");
		values.put("data2", name);
		// values.put("data1", "Jack");
		resolver.insert(uri, values);
		values.clear();
		// 添加手机号码
		for (String num : phoneNumbers) {
			values.put("raw_contact_id", contact_id);
			values.put(Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
			values.put("data2", "2"); // 2表示手机
			values.put("data1", num);
			resolver.insert(uri, values);
			values.clear();
		}
	}

	/**
	 * 删除单个联系人数据
	 * 
	 * @param name
	 * @return 是否删除成功
	 */
	public boolean delete(String name) {
		try {
			// 根据姓名求id
			Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
			ContentResolver resolver = mContext.getContentResolver();
			Cursor cursor = resolver.query(uri, new String[] { Data._ID },
					"display_name = ?", new String[] { name }, null);
			if (cursor.moveToFirst()) {
				int id = cursor.getInt(0);
				// 根据id删除data中的相应数据
				resolver.delete(uri, "display_name = ?", new String[] { name });
				uri = Uri.parse("content://com.android.contacts/data");
				resolver.delete(uri, "raw_contact_id = ?", new String[] { id
						+ "" });
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @description 更新联系人数据
	 * 
	 * @param name
	 * @param phoneNumber
	 * @return 是否更新成功
	 */
	public boolean update(String name, String phoneNumber) {
		try {
			// 根据姓名求id,再根据id删除
			Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
			ContentResolver resolver = mContext.getContentResolver();
			Cursor cursor = resolver.query(uri, new String[] { Data._ID },
					"display_name = ?", new String[] { name }, null);
			if (cursor.moveToFirst()) {
				int id = cursor.getInt(0);
				Uri mUri = Uri.parse("content://com.android.contacts/data");// 对data表的所有数据操作
				ContentResolver mResolver = mContext.getContentResolver();
				ContentValues values = new ContentValues();
				values.put("data1", phoneNumber);
				mResolver.update(mUri, values,
						"mimetype=? and raw_contact_id=?", new String[] {
								"vnd.android.cursor.item/phone_v2", id + "" });
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @description 根据电话号码查询姓名
	 * 
	 * @param phoneNumber
	 * @return 查询是否成功
	 */
	public boolean findNameByPhoneNumber(String phoneNumber) {
		// URI = content://com.android.contacts/data/phones/filter/#
		Uri uri = Uri
				.parse("content://com.android.contacts/data/phones/filter/"
						+ phoneNumber);
		ContentResolver resolver = mContext.getContentResolver();
		// 从raw_contact表中返回display_name
		Cursor cursor = resolver.query(uri, new String[] { Data.DATA4 }, null,
				null, null);
		if (cursor.moveToFirst()) {
			Log.d("ContactsManager", "找到这个号码");
			return true;
		} else {
			Log.e("ContactsManager", "没找到这个号码");
			return false;
		}
	}

}
