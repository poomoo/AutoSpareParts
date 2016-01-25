package pm.poomoo.autospareparts.view.activity.more;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseActivity;
import pm.poomoo.autospareparts.util.TimeCountDownUtil;

/**
 * 修改用户资料
 */
public class ChangeUserInformationActivity extends PmBaseActivity {
    private final String TAG = ChangeUserInformationActivity.class.getSimpleName();
    @ViewInject(R.id.edt_name)
    private EditText mEdtName;//真实姓名
    @ViewInject(R.id.edt_code)
    private EditText mEdtCode;//验证码
    @ViewInject(R.id.edt_passWord)
    private EditText mEdtPassWord;//新密码
    @ViewInject(R.id.txt_getCode)
    private TextView mTxtGetCode;//获取验证码
    @ViewInject(R.id.txt_tel)
    private TextView mTxtTel;//手机号码


    private TimeCountDownUtil timeCountDownUtil;//倒计时
    private String name = "";
    private String passWord = "";
    private String code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_client_information);
        clearAllActivity();
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("信息修改");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });

        mTxtTel.setText(hiddenTel(PmApplication.getInstance().getShared().getString(TEL)));
        mEdtName.setText(PmApplication.getInstance().getShared().getString(NAME));
    }

    /**
     * 获取验证码
     *
     * @param view
     */
    public void toGetCode(View view) {
        timeCountDownUtil = new TimeCountDownUtil(60 * 1000, 1000, mTxtGetCode);
        timeCountDownUtil.start();
        getCode();
    }

    /**
     * 确认修改
     *
     * @param view
     */
    public void toConfirm(View view) {
        if (checkInput())
            commit();
    }

    private boolean checkInput() {
        name = mEdtName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showToast("请填写姓名");
            return false;
        }
        passWord = mEdtPassWord.getText().toString().trim();
        if (!TextUtils.isEmpty(passWord)) {
            code = mEdtCode.getText().toString().trim();
            if (TextUtils.isEmpty(passWord)) {
                showToast("请输入验证码");
                return false;
            }
        }
        return true;
    }

    /**
     * 获取验证码
     */
    public void getCode() {
        RequestParams params = new RequestParams();
        params.addBodyParameter(KEY_PACKNAME, "1016");
        params.addBodyParameter("phone_number", PmApplication.getInstance().getShared().getString(TEL));

        showLoadingDialog("获取验证码中...");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog("获取验证码成功", true);
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("获取验证码失败", false);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                showDismissLoadingDialog("网络错误", false);
            }
        });
    }

    /**
     * 修改资料
     */
    public void commit() {
        RequestParams params = new RequestParams();
        params.addBodyParameter(KEY_PACKNAME, "1028");
        params.addBodyParameter("id", PmApplication.getInstance().getShared().getInt(USER_ID) + "");
        params.addBodyParameter("name", name);
        params.addBodyParameter("password", MD5(passWord));
        params.addBodyParameter("phone_number", PmApplication.getInstance().getShared().getString(TEL));

        showLoadingDialog("提交修改中...");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog("修改成功", true);
                            PmApplication.getInstance().getShared().putString(NAME, name);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 2 * 1000);
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("修改失败", false);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                showDismissLoadingDialog("网络错误", false);
            }
        });
    }

    /**
     * 隐藏手机号
     *
     * @param tel
     * @return
     */
    public String hiddenTel(String tel) {
        if (TextUtils.isEmpty(tel))
            return "";
        String temp;
        temp = tel.substring(0, 3) + tel.substring(3, 7).replaceAll("[0123456789]", "*")
                + tel.substring(7, tel.length());
        return temp;
    }
}
