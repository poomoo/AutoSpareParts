package pm.poomoo.autospareparts.view.activity.start;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseActivity;
import pm.poomoo.autospareparts.base.PmBaseFragment;
import pm.poomoo.autospareparts.view.fragment.MainFragmentActivity;

import static pm.poomoo.autospareparts.base.PmApplication.showLog;

/**
 * 启动界面
 */
public class StartActivity extends PmBaseActivity {

    private final String TAG = StartActivity.class.getSimpleName();
    private static String PATH = "/data/data/pm.poomoo.autospareparts/files";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        PmApplication.getInstance().setScreenWidth(dm.widthPixels);
        PmApplication.getInstance().setScreenHeight(dm.heightPixels);
        checkVersion();
//        startTimer();
        showLog = false;//不显示日志
    }

    private Timer mTimer;
    private TimerTask mTimerTask;

    private void startTimer() {
        releaseTimer();
        mTimer = new Timer();
        mTimer.schedule(getTimerTask(), 2000);
    }

    private TimerTask getTimerTask() {

        mTimerTask = new TimerTask() {
            public void run() {
                startActivity(new Intent(StartActivity.this, MainFragmentActivity.class));
                finish();
                releaseTimer();
            }
        };
        return mTimerTask;
    }

    private void releaseTimer() {
        try {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 版本更新检查
     */
    public void checkVersion() {
        RequestParams params = new RequestParams();

        params.addBodyParameter(KEY_PACKNAME, "1014");
        params.addBodyParameter("system_type", "0");
        params.addBodyParameter("version_number", getVersionName());

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    showLog(TAG, responseInfo.result);
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            final String downAddress = result.getString("down_address");
//                            final String downAddress = "http://a.app.qq.com/o/simple.jsp?pkgname=pm.poomoo.autospareparts";
                            if (!downAddress.equals("")) {
                                new AlertDialog.Builder(StartActivity.this).setTitle("提示").setMessage("检测到最新版，是否更新").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downAddress));
                                        startActivity(intent);
                                        StartActivity.this.finish();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startTimer();
                                    }
                                }).setCancelable(false).create().show();
                            } else
                                startTimer();
                            break;
                        case RET_FAIL:
                            startTimer();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                showLog(TAG, error.toString() + " " + msg);
                startTimer();
            }
        });
    }
}