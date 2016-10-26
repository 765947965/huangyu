package org.aisin.sipphone.tools;

public class Constants {

	public static final String BrandName = "aixin";
	public static final String servicePagefileName = "/servicepagefile.txt";
	public static final String servicePagedirName = "/servicePageImg";
	public static final String DOMAIN = "sip.zjtytx.com:10090";
	public static final String KEYBACK = "backgrounds2";// 键盘背景图片存放文件夹名称
	public static final String KEYBACK_WRIGHT = "0000000000001.png";// 白色键盘背景图片
	public static final String KEYBACK_MR = "0000000002.png";// 默认键盘背景图片
	public static final String KEYBACK_ZDY = "0000000000000.jpg";// 自定义键盘背景图片
	public static boolean showbaidumap = false;// 是否开启百度地图附近门店
	public static final String nearbyname = "环宇";// 附近门店搜索默认搜索词
	public static final String musicogg = "musicogg";// 存放按键音频的目录
	public static final String musicp = "musicp";// 存放谱的目录
	public static final int YcodeTime = 60;// 重发验证码等待时间
	public static final String calloutactivity = "2"; // 1 启动V1版 2 启动V2版
	public static final boolean WXZF = false; // true 开启微信支付 false 关闭微信支付
	/** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
	public static final String APP_KEY = "3764910459";
	/**
	 * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
	 * 
	 * <p>
	 * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响， 但是没有定义将无法使用 SDK 认证登录。
	 * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
	 * </p>
	 */
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	/**
	 * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
	 * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利 选择赋予应用的功能。
	 * 
	 * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的 使用权限，高级权限需要进行申请。
	 * 
	 * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
	 * 
	 * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
	 * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
	 */
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
}
