package pm.poomoo.autospareparts.base;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;

import com.lidroid.xutils.DbUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import pm.poomoo.autospareparts.config.PmConfig;
import pm.poomoo.autospareparts.config.PmNetWorkInterface;
import pm.poomoo.autospareparts.mode.ClientInfo;
import pm.poomoo.autospareparts.mode.CompanyInfo;
import pm.poomoo.autospareparts.mode.TypeInfo;
import pm.poomoo.autospareparts.util.PmShared;

/**
 * 跟随程序启动
 *
 * @author ysy
 */
public class PmApplication extends Application implements PmNetWorkInterface, PmConfig {

    private static PmApplication instance;//当前对象
    private List<Activity> activityList;//activity栈
    private DbUtils db;//数据库操作对象
    private PmShared shared;//存储工具
    private float screenWidth;//屏幕宽度
    private float screenHeight;//屏幕高度
    private List<TypeInfo> typeInfos;//类型

    private List<CompanyInfo> showCompanyInfo;//显示公司详细信息
    private List<ClientInfo> showClientInfo;//显示客户详细信息

    public PmApplication() {
        super();
    }

    public void onCreate() {
        super.onCreate();
        ShareSDK.initSDK(this);
        instance = this;
        activityList = new ArrayList<>();
        shared = new PmShared(this);
        typeInfos = new ArrayList<>();
        showCompanyInfo = new ArrayList<>();
        showClientInfo = new ArrayList<>();

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
                .cacheInMemory(true) //
                .cacheOnDisk(false) //
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置最低配置
                .build();//
        ImageLoaderConfiguration config = new ImageLoaderConfiguration//
                .Builder(getApplicationContext())//
                .defaultDisplayImageOptions(defaultOptions)//
                .writeDebugLogs()//
                .build();//
        ImageLoader.getInstance().init(config);
    }

    public static PmApplication getInstance() {
        if (instance == null) {
            instance = new PmApplication();
        }
        return instance;
    }

    //================================ 程序用到的数据 ===============================================
    public List<Activity> getActivityList() {
        if (activityList == null) {
            activityList = new ArrayList<Activity>();
        }
        return activityList;
    }

    public PmShared getShared() {
        if (shared == null) {
            shared = new PmShared(this);
        }
        return shared;
    }

    public DbUtils getDb() {
        if (db == null) {
            db = DbUtils.create(this);
        }
        return db;
    }

    public List<TypeInfo> getTypeInfos() {
        return typeInfos;
    }

    public List<CompanyInfo> getShowCompanyInfos() {
        return showCompanyInfo;
    }

    public List<ClientInfo> getShowClientInfo() {
        return showClientInfo;
    }

    //================================ 屏幕参数 ===============================================
    public float getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(float screenWidth) {
        this.screenWidth = screenWidth;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(float screenHeight) {
        this.screenHeight = screenHeight;
    }
}