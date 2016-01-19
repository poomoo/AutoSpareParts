package pm.poomoo.autospareparts.config;

/**
 * 存储网络交互信息
 *
 * @author ysy
 */
public interface PmNetWorkInterface {

    //====================== 外网服务器 =========================
    /**
     * 服务器地址(本地)
     */
    public static final String LOCAL_URL = "http://192.168.0.110/app/call.html";
    /**
     * 服务器地址(远程)
     */
    public static final String REMOTE_URL = "http://192.168.0.122/app/call.html";
    /**
     * 服务器地址
     */
    public static final String URL = LOCAL_URL;
    /**
     * 图片下载地址
     */
    public static final String PIC_RUL = "http://gyqp.poomoo.cn/Uploads/";

    //====================== 网络配置参数 =========================
    /**
     * 服务器域名后缀
     */
    public static final String KEY = "param";
    /**
     * 服务器超时
     */
    public static final int TIME_OUT = 5000;

    // **************************** 传递值对应键名 ***************************
    public static final String KEY_RESULT_CODE = "key_result_code";

    // **************************** 请求包与返回包键值定义 ***************************
    public static final String KEY_TASK_FLAG = "taskFlag";
    public static final String KEY_PACKNAME = "packname";
    public static final String KEY_RESULT = "result";
    public static final String KEY_LIST = "list";

    // **************************** 返回值定义 ***************************
    public static final int RET_SUCCESS = 0;
    public static final int RET_FAIL = -1;
    public static final int RET_EXIST = 1;
    public static final int RET_UNKNOW_ERROR = -2;
    public static final int RET_PACKNAME_ERROR = -3;
    public static final int RET_NET_ERROR = -4;

}
