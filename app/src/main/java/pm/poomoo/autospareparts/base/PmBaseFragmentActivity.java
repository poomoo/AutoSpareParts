package pm.poomoo.autospareparts.base;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.config.PmConfig;
import pm.poomoo.autospareparts.config.PmNetWorkInterface;

import static pm.poomoo.autospareparts.base.PmApplication.showLog;


public class PmBaseFragmentActivity extends FragmentActivity implements PmConfig, PmNetWorkInterface {
    // 等待操作的对话框
    private LoadingDialog mLoadingDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // 去掉默认标题栏
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);

    }

    // =============================下面是toast======================================

    /**
     * 弹出Toast提示
     *
     * @param aText 显示的字符串
     */
    protected void showToast(String aText) {
        Toast.makeText(this, aText, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast提示
     *
     * @param aResId 需要显示的int型
     */
    protected void showToast(int aResId) {
        Toast.makeText(this, aResId, Toast.LENGTH_SHORT).show();
    }

    // =============================下面是log======================================

    /**
     * 日志
     *
     * @param Tag     日志标记
     * @param content 日志内容
     */
    protected void showLog(String Tag, String content) {
        if (showLog)
            Log.i(Tag, PmConfig.LOG_PREFIX + "-------------------->" + content);
    }

    // =============================下面是判断是否需要引导界面===========================

    /**
     * 程序启动的时候，是否需要引导界面
     *
     * @return 返回引导标记
     */
    protected boolean isNeedGuide() {
        return PmApplication.getInstance().getShared().getBoolean(PmConfig.IS_NEED_GUIDE);
    }

    // =============================下面是加载对话框======================================

    /**
     * 显示加载对话框
     *
     * @param content 显示的内容
     */
    protected void showLoadingDialog(String content) {
        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
                mLoadingDialog = null;
            }
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setMessage(content);
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示加载对话框
     *
     * @param context 上下文
     * @param content 显示的内容
     */
    protected void showLoadingDialog(Context context, String content) {
        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
                mLoadingDialog = null;
            }
            mLoadingDialog = new LoadingDialog(context);
            mLoadingDialog.setMessage(content);
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================下面是取消加载对话框======================================

    /**
     * 消除加载对话框
     */
    protected void ShowDismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }

    /**
     * 消除加载对话框
     *
     * @param content 显示的内容
     * @param flag    是否成功标志
     */
    protected void ShowDismissLoadingDialog(String content, boolean flag) {
        try {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.setCancelable(false);
                mLoadingDialog.setMessage(content);
                if (flag) {
                    mLoadingDialog.setSuccess(true);
                } else {
                    mLoadingDialog.setSuccess(false);
                }
                startTimer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =======================下面是定时器Timer=========================
    private Timer mTimer;
    private TimerTask mTimerTask;

    private void startTimer() {
        releaseTimer();
        mTimer = new Timer();
        mTimer.schedule(getTimerTask(), PmConfig.DIG_DISMISS_DELAY);
    }

    private TimerTask getTimerTask() {

        mTimerTask = new TimerTask() {
            public void run() {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
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

    // =============================下面是自定义加载对话框======================================

    /**
     * 自定义等待操作框
     *
     * @author pm004
     */
    private class LoadingDialog {

        private Context mContext;
        private Dialog mDialog;
        private ProgressBar mProgressBar;
        private TextView mTextView;
        private ImageView mImageView;

        public LoadingDialog(Context aContext) {
            mContext = aContext;
            init();
        }

        private void init() {
            mDialog = new Dialog(mContext, R.style.no_frame_dialog);
            mDialog.setContentView(R.layout.dlg_loading);
            mProgressBar = (ProgressBar) mDialog.findViewById(R.id.dlg_loading_progress);
            mTextView = (TextView) mDialog.findViewById(R.id.dlg_loading_tv);
            mImageView = (ImageView) mDialog.findViewById(R.id.dlg_loading_img);
        }

        public void setSuccess(boolean aSuccess) {
            if (aSuccess) {
                mImageView.setImageResource(R.drawable.pmlbs_success);
            } else {
                mImageView.setImageResource(R.drawable.pmlbs_fail);
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            mImageView.setVisibility(View.VISIBLE);
        }

        public boolean isShowing() {
            return mDialog.isShowing();
        }

        public void setMessage(String aText) {
            mTextView.setText(aText);
        }

        public void show() {
            if (mDialog != null && !mDialog.isShowing()) {
                mDialog.show();
            }
        }

        public void dismiss() {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }

        public void setCancelable(boolean aCancelable) {
            mDialog.setCancelable(aCancelable);
        }

        public void release() {
            dismiss();
            mImageView = null;
            mTextView = null;
            mProgressBar = null;
            mDialog = null;
        }

    }

    // =============================下面是activity进出动画======================================

    /**
     * activity切换-> 右进左出
     */
    protected void getActivityRightToLeft() {
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
    }

    /**
     * activity切换-> 左进右出
     */
    protected void getActivityLeftToRight() {
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    /**
     * activity切换-> 向下进(覆盖)
     */
    protected void getActivityInFromTop() {
        overridePendingTransition(R.anim.activity_in_from_top, R.anim.activity_center);
    }

    /**
     * activity切换-> 向上进(覆盖)
     */
    protected void getActivityInFromBottom() {
        overridePendingTransition(R.anim.activity_in_from_bottom, R.anim.activity_center);
    }

    /**
     * activity切换-> 向左进(覆盖)
     */
    protected void getActivityInFromRight() {
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_center);
    }

    /**
     * activity切换-> 向右进(覆盖)
     */
    protected void getActivityInFromLeft() {
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_center);
    }

    /**
     * activity切换-> 向下出(抽出)
     */
    protected void getActivityOutToBottom() {
        overridePendingTransition(R.anim.activity_center, R.anim.activity_out_to_bottom);
    }

    /**
     * activity切换-> 向上出(抽出)
     */
    protected void getActivityOutToTop() {
        overridePendingTransition(R.anim.activity_center, R.anim.activity_out_to_top);
    }

    /**
     * activity切换-> 向左出(抽出)
     */
    protected void getActivityOutToLeft() {
        overridePendingTransition(R.anim.activity_center, R.anim.activity_out_to_left);
    }

    /**
     * activity切换-> 向右出(抽出)
     */
    protected void getActivityOutToRight() {
        overridePendingTransition(R.anim.activity_center, R.anim.activity_out_to_right);
    }

    /**
     * activity切换-> 渐变动画
     */
    protected void getActivityAlphaAnimation() {
        overridePendingTransition(R.anim.activity_in_alpha, R.anim.activity_out_alpha);
    }

    // =============================下面是头部条======================================

    /**
     * 统一头部条
     *
     * @return lHeaderViewHolder 头部条对象
     */
    protected HeaderViewHolder getHeaderView() {
        HeaderViewHolder headerViewHolder = new HeaderViewHolder();
        headerViewHolder.title = (TextView) findViewById(R.id.head_title);
        headerViewHolder.linearLayout = (LinearLayout) findViewById(R.id.head_linear);
        headerViewHolder.leftButton = (TextView) findViewById(R.id.head_left_button);
        headerViewHolder.leftButtonText = (TextView) findViewById(R.id.head_leftButton_text);
        headerViewHolder.rightButton = (TextView) findViewById(R.id.head_right_button);
        return headerViewHolder;
    }

    protected class HeaderViewHolder {
        public TextView title, leftButton, leftButtonText, rightButton;
        public LinearLayout linearLayout;
    }

    // =============================下面手机按键处理===================================

//    /**
//     * 是否屏蔽手机按键
//     */
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
//            return true;
//        }
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            // 是否需要屏幕手机返回按键
//            if (PmConfig.IS_NEED_SHIELD) {
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        PmApplication.getInstance().getShared().putBoolean(IS_START, true);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (PmApplication.getInstance().getShared().getBoolean(IS_START)) {
//            //启动新界面
//            PmApplication.getInstance().getShared().putBoolean(IS_BACK, false);
//            PmApplication.getInstance().getShared().putBoolean(IS_START, false);
//        } else {
//            //后台运行
//            PmApplication.getInstance().getShared().putBoolean(IS_BACK, true);
//        }
//    }

    /**
     * 释放资源
     */
    protected void onDestroy() {
        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.release();
            }
            releaseTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
