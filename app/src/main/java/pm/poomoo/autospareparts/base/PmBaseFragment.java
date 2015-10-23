package pm.poomoo.autospareparts.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;

import java.util.Timer;
import java.util.TimerTask;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.config.PmConfig;
import pm.poomoo.autospareparts.config.PmNetWorkInterface;


public class PmBaseFragment extends Fragment implements PmNetWorkInterface, PmConfig {

    private final String TAG = PmBaseFragment.class.getSimpleName();
    // 等待操作的对话框
    private LoadingDialog mLoadingDialog;
    //下载图片工具
    private BitmapUtils bitmapUtils;

    /**
     * 得到当前版本号
     */
    public String getVersionName() {
        PackageInfo info = null;
        String versionName = "";
        try {
            info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionName = info.versionName;
            int versionCode = info.versionCode;
            String packageNames = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取图片
     *
     * @param imageView 显示图片控件
     * @param path      图片路径
     */
    public void getBitmap(ImageView imageView, String path) {
        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils(getActivity());
        }
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);
        bitmapUtils.configDefaultLoadingImage(R.drawable.ic_launcher);
        if (!TextUtils.isEmpty(path)) {
            bitmapUtils.display(imageView, PIC_RUL + path.substring(2));
        }
        showLog(TAG, PIC_RUL + path);
    }

    // =============================下面是toast======================================

    /**
     * 弹出Toast提示
     *
     * @param aText
     */
    protected void showToast(String aText) {
        Toast.makeText(getActivity(), aText, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int aResId) {
        Toast.makeText(getActivity(), aResId, Toast.LENGTH_SHORT).show();
    }

    // =============================下面是log======================================

    /**
     * 日志
     *
     * @param Tag
     * @param content
     */
    protected void showLog(String Tag, String content) {
        Log.i(Tag, PmConfig.LOG_PREFIX + "-------------------->" + content);
    }

    // =============================下面是提示对话框======================================

    /**
     * 显示系统提示对话框
     *
     * @param title   标题
     * @param message 显示的内容
     */
    protected void showPromptDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message).setPositiveButton("确定", null).setNegativeButton("取消", null);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
            mLoadingDialog = new LoadingDialog(getActivity());
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
    protected void showDismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }

    /**
     * 消除加载对话框
     *
     * @param content 显示的内容
     * @param flag    是否成功标志
     */
    protected void showDismissLoadingDialog(String content, boolean flag) {
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
            if (mDialog != null) {
                return mDialog.isShowing();
            }
            return false;
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
        getActivity().overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
    }

    /**
     * activity切换-> 左进右出
     */
    protected void getActivityLeftToRight() {
        getActivity().overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    /**
     * activity切换-> 向下进(覆盖)
     */
    protected void getActivityInFromTop() {
        getActivity().overridePendingTransition(R.anim.activity_in_from_top, R.anim.activity_center);
    }

    /**
     * activity切换-> 向上进(覆盖)
     */
    protected void getActivityInFromBottom() {
        getActivity().overridePendingTransition(R.anim.activity_in_from_bottom, R.anim.activity_center);
    }

    /**
     * activity切换-> 向左进(覆盖)
     */
    protected void getActivityInFromRight() {
        getActivity().overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_center);
    }

    /**
     * activity切换-> 向右进(覆盖)
     */
    protected void getActivityInFromLeft() {
        getActivity().overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_center);
    }

    /**
     * activity切换-> 向下出(抽出)
     */
    protected void getActivityOutToBottom() {
        getActivity().overridePendingTransition(R.anim.activity_center, R.anim.activity_out_to_bottom);
    }

    /**
     * activity切换-> 向上出(抽出)
     */
    protected void getActivityOutToTop() {
        getActivity().overridePendingTransition(R.anim.activity_center, R.anim.activity_out_to_top);
    }

    /**
     * activity切换-> 向左出(抽出)
     */
    protected void getActivityOutToLeft() {
        getActivity().overridePendingTransition(R.anim.activity_center, R.anim.activity_out_to_left);
    }

    /**
     * activity切换-> 向右出(抽出)
     */
    protected void getActivityOutToRight() {
        getActivity().overridePendingTransition(R.anim.activity_center, R.anim.activity_out_to_right);
    }

    /**
     * activity切换-> 渐变动画
     */
    protected void getActivityAlphaAnimation() {
        getActivity().overridePendingTransition(R.anim.activity_in_alpha, R.anim.activity_out_alpha);
    }

    // =============================下面是头部条======================================

    /**
     * 统一头部条
     *
     * @return lHeaderViewHolder 头部条对象
     */
    protected HeaderViewHolder getHeaderView(View layout) {
        HeaderViewHolder headerViewHolder = new HeaderViewHolder();
        headerViewHolder.title = (TextView) layout.findViewById(R.id.head_title);
        headerViewHolder.linearLayout = (LinearLayout) layout.findViewById(R.id.head_linear);
        headerViewHolder.leftButton = (TextView) layout.findViewById(R.id.head_left_button);
        headerViewHolder.leftButtonText = (TextView) layout.findViewById(R.id.head_leftButton_text);
        headerViewHolder.rightButton = (TextView) layout.findViewById(R.id.head_right_button);
        return headerViewHolder;
    }

    protected class HeaderViewHolder {
        public TextView title, leftButton, leftButtonText, rightButton;
        public LinearLayout linearLayout;
    }

    /**
     * 释放资源
     */
    @Override
    public void onDestroyView() {
        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.release();
            }
            releaseTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

}
