package pm.poomoo.autospareparts.config;

/**
 * 存储配置信息
 *
 * @author ysy
 */
public interface PmConfig {
    // ********************** int 型参数 *************************************
    // 消失对话框等待时间：1.5秒
    public static final int DIG_DISMISS_DELAY = 1500;
    //数据库版本
    public static final int DATABASE_VERSION = 1;

    // **************** boolean 型参数****************************************
    // 是否需要屏蔽手机返回按键
    public static final boolean IS_NEED_SHIELD = false;
    // 软件是否需要注册
    public static final boolean IS_NEED_REGISTER = false;

    // ***************** String 型参数****************************************
    // shared存储是否需要引导界面键名
    public static final String IS_NEED_GUIDE = "isGuide";
    // 工程数据共享文件名字
    public static final String SHARED_NAME = "pm";
    // 默认日志前缀
    public static final String LOG_PREFIX = " -pm- ";
    //跳转界面键值对名称（int型）
    public static final String SKIP_NUMBER = "number";
    //跳转界面键值对名称（string型）
    public static final String SKIP_NAME = "name";
    //"关于我们-说明"键值对名称
    public static final String ABOUT_ME_NAME_EXPLAIN = "about_me_name_explain";
    //"关于我们-客户电话"键值对名称
    public static final String ABOUT_ME_NAME_CLIENT = "about_me_name_client";
    //"关于我们-投诉电话"键值对名称
    public static final String ABOUT_ME_NAME_COMPLAINT = "about_me_name_complaint";
    //用户ID字段
    public static final String USER_ID = "userId";
    //是否是VIP
    public static final String IS_VIP = "isVip";
    //手机号
    public static final String TEL = "tel";
    //姓名
    public static final String NAME = "name";
    //分享文本
    public static final String SHARE_TEXT = "贵阳汽配手册APP，找配件，更方便，欢迎下载";
}
