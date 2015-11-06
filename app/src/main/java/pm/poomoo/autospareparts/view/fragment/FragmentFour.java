package pm.poomoo.autospareparts.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONObject;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseFragment;
import pm.poomoo.autospareparts.view.activity.more.ShareActivity;
import pm.poomoo.autospareparts.view.activity.more.AboutMeActivity;
import pm.poomoo.autospareparts.view.activity.more.FeedbackActivity;
import pm.poomoo.autospareparts.view.activity.more.MyMessageActivity;
import pm.poomoo.autospareparts.view.activity.more.SoftwareExplainActivity;
import pm.poomoo.autospareparts.view.activity.more.collect.CollectFragmentActivity;
import pm.poomoo.autospareparts.view.activity.start.LogActivity;
import pm.poomoo.autospareparts.view.activity.start.RegisterActivity;


/**
 * 更多界面
 *
 * @author yangsongyu
 */
public class FragmentFour extends PmBaseFragment {

    private final String TAG = FragmentFour.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.frag_four, container, false);
        ViewUtils.inject(this, layout);
        init(layout);
        return layout;
    }

    /**
     * 初始化
     *
     * @param layout 当前界面布局控件
     */
    public void init(View layout) {
        HeaderViewHolder headerViewHolder = getHeaderView(layout);
        headerViewHolder.title.setText("更多");
        headerViewHolder.rightButton.setVisibility(View.VISIBLE);
        headerViewHolder.rightButton.setBackgroundResource(R.drawable.head_right_background);
        headerViewHolder.rightButton.setPadding(10, 2, 10, 2);
        headerViewHolder.rightButton.setText("登录");
        headerViewHolder.rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LogActivity.class));
                getActivityInFromRight();
            }
        });
    }

    /**
     * 设置监听
     *
     * @param view 点击的控件
     */
    @OnClick({R.id.frag_four_collect, R.id.frag_four_share, R.id.frag_four_message, R.id.frag_four_about_me, R.id.frag_four_check_version,
            R.id.frag_four_data_down, R.id.frag_four_software_explain, R.id.frag_four_register, R.id.frag_four_feedback, R.id.frag_four_alter})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.frag_four_collect:
                //我的收藏
                startActivity(new Intent(getActivity(), CollectFragmentActivity.class));
                getActivityInFromRight();
                break;
            case R.id.frag_four_share:
                //供求发布
                startActivity(new Intent(getActivity(), ShareActivity.class));
                getActivityInFromRight();
                break;
            case R.id.frag_four_message:
                //消息中心
                startActivity(new Intent(getActivity(), MyMessageActivity.class));
                getActivityInFromRight();
                break;
            case R.id.frag_four_about_me:
                //关于我们
                startActivity(new Intent(getActivity(), AboutMeActivity.class));
                getActivityInFromRight();
                break;
            case R.id.frag_four_check_version:
                //检查更新
                checkVersion();
                break;
            case R.id.frag_four_data_down:
                //数据下载
                showToast("下载中...");
//                new Thread() {
//                    @Override
//                    public void run() {
//                        super.run();
//                        try {
//                            sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        showToast("下载完成");
//                    }
//                }.start();
                break;
            case R.id.frag_four_software_explain:
                //软件说明
                startActivity(new Intent(getActivity(), SoftwareExplainActivity.class));
                getActivityInFromRight();
                break;
            case R.id.frag_four_register:
                //会员注册
                skipActivity(0);
                break;
//            case R.id.frag_four_vip_register:
//                //VIP注册
//                skipActivity(1);
//                break;
            case R.id.frag_four_feedback:
                //意见反馈
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
                getActivityInFromRight();
                break;
            case R.id.frag_four_alter:
                //信息修改
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
                getActivityInFromRight();
                break;
        }
    }

    /**
     * 跳转到注册界面
     *
     * @param number 0:会员注册   1:VIP注册
     */
    public void skipActivity(int number) {
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(SKIP_NUMBER, number);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivityInFromRight();
    }

    /**
     * 版本更新检查
     */
    public void checkVersion() {
        RequestParams params = new RequestParams();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_PACKNAME, 1014);
            jsonObject.put("system_type", 0);
            jsonObject.put("version_number", getVersionName());
            params.addBodyParameter(KEY, jsonObject.toString());
            showLog(TAG, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        showLoadingDialog("检查更新中...");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    showLog(TAG, responseInfo.result);
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            final String downAddress = result.getString("download_address");
                            if (downAddress.equals("")) {
                                showDismissLoadingDialog("当前已是最新版", true);
                            } else {
                                showDismissLoadingDialog();
                                new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("检测到最新版，是否更新").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downAddress));
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("取消", null).create().show();
                            }
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("失败", false);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                showDismissLoadingDialog("网络错误", false);
            }
        });
    }

}

