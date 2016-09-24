package org.aisin.sipphone.tools;

import org.aisin.sipphone.tianyu.R;

public class GetTutorialShowMaps {

	public static String[] TutorialTitles(String titleflag) {
		if ("samsung".equals(titleflag)) {
			return new String[] { "1、点击“设置”按钮进入应用管理页面",
					"2、点击右上“更多”按钮进入权限设置页面，找到“应用程序许可”", "3、在应用程序许可中找到“环宇”",
					"4、在应用列表中找到“环宇”，点击勾选" };
		} else if ("htc".equals(titleflag)) {
			return new String[] { "1、点击“设置”按钮进入设置页面",
					"2、点击“全部设置”选项卡，再点击“安全”选项", "3、点击“应用权限”选项卡",
					"4、分别选择“读取联系人”和“读取通话记录”", "5、并设置为“总是允许”，完成" };
		} else if ("lenovo".equals(titleflag)) {
			return new String[] { "1、点击“乐安全”按钮进入权限设置页面", "2、点击“敏感行为监控”选项卡",
					"3、在应用管理中找到“环宇”，点击", "4、将关于“读取短信”和“读取联系人”的权限勾选，完成" };
		} else if ("meizu".equals(titleflag)) {
			return new String[] { "1、点击“设置”按钮进入应用管理页面",
					"2、在左边的列表里找到应用管理图标在右边全部选项卡里找到“环宇”", "3、点击“权限管理”",
					"4、将里面“读取联系人记录”和“读取通话记录”的权限全部设置为“允许”即可" };
		} else if ("xiaomi".equals(titleflag)) {
			return new String[] { "1、点击“设置”按钮进入设置页面", "2、点击“其他应用管理”选项",
					"3、找到“环宇”选项，点击", "4、找到“权限管理”选项，点击",
					"5、分别勾选“读取联系人”和“读取通话记录”即可" };
		} else if ("huawei".equals(titleflag)) {
			return new String[] { "1、点击“设置”按钮进入设置页面",
					"2、点击“全部”选项卡，再点击“权限管理”选项", "3、分别点击“读取联系人”和“读取短信彩信”选项卡",
					"4、找到“环宇”将提示设置为允许", "5、找到“环宇”将提示设置为允许" };
		} else if ("vivo".equals(titleflag)) {
			return new String[] { "1、点击“i管家”按钮进入应用管理页面", "2、点击软件管理按钮",
					"3、点击软件权限管理", "4、点击软件选项找到“环宇”",
					"5、找到“读取联系人”和“读取通话记录”选项，设置为“允许”，完成" };
		} else if ("tencent".equals(titleflag)) {
			return new String[] { "1、点击“腾讯手机管家”按钮进入权限设置页面", "2、点击“软件管理”选项卡",
					"3、点击“软件权限管理”选项卡", "4、找到“环宇”选项，点击",
					"5、把“获取联系人”和“获取通话记录”设置为“允许”" };
		} else if ("gj360".equals(titleflag)) {
			return new String[] { "1、点击“360卫士”按钮进入权限设置页面", "2、点击“软件管理”选项卡",
					"3、点击“权限管理”选项卡", "4、找到“环宇”",
					"5、分别点击选项卡“读取联系人信息”和“读取通话记录”并设置为“允许”完成" };
		}
		return null;
	}

	public static Integer[] TutorialImagesID(String titleflag) {
		if ("samsung".equals(titleflag)) {
			return new Integer[] { R.drawable.samsung_t1,
					R.drawable.samsung_t2, R.drawable.samsung_t3,
					R.drawable.samsung_t4 };
		} else if ("htc".equals(titleflag)) {
			return new Integer[] { R.drawable.htc_t1, R.drawable.htc_t2,
					R.drawable.htc_t3, R.drawable.htc_t4, R.drawable.htc_t5 };
		} else if ("lenovo".equals(titleflag)) {
			return new Integer[] { R.drawable.lenovo_t1, R.drawable.lenovo_t2,
					R.drawable.lenovo_t3, R.drawable.lenovo_t4 };
		} else if ("meizu".equals(titleflag)) {
			return new Integer[] { R.drawable.meizu_t1, R.drawable.meizu_t2,
					R.drawable.meizu_t3, R.drawable.meizu_t4 };
		} else if ("xiaomi".equals(titleflag)) {
			return new Integer[] { R.drawable.xiaomi_t1, R.drawable.xiaomi_t2,
					R.drawable.xiaomi_t3, R.drawable.xiaomi_t4,
					R.drawable.xiaomi_t5 };
		} else if ("huawei".equals(titleflag)) {
			return new Integer[] { R.drawable.huawei_t1, R.drawable.huawei_t2,
					R.drawable.huawei_t3, R.drawable.huawei_t4,
					R.drawable.huawei_t5 };
		} else if ("vivo".equals(titleflag)) {
			return new Integer[] { R.drawable.vivo_t1, R.drawable.vivo_t2,
					R.drawable.vivo_t3, R.drawable.vivo_t4, R.drawable.vivo_t5 };
		} else if ("tencent".equals(titleflag)) {
			return new Integer[] { R.drawable.tencent_t1,
					R.drawable.tencent_t2, R.drawable.tencent_t3,
					R.drawable.tencent_t4, R.drawable.tencent_t5 };
		} else if ("gj360".equals(titleflag)) {
			return new Integer[] { R.drawable.gj360_t1, R.drawable.gj360_t2,
					R.drawable.gj360_t3, R.drawable.gj360_t4,
					R.drawable.gj360_t5 };
		}
		return null;
	}
}
