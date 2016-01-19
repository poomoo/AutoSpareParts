package pm.poomoo.autospareparts.view.activity.start;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseActivity;

/**
 * 登录界面
 */
public class LogActivity extends PmBaseActivity {

    private final String TAG = LogActivity.class.getSimpleName();
    @ViewInject(R.id.log_edt_id)
    private EditText mEdtId;
    @ViewInject(R.id.log_edt_password)
    private EditText mEdtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        clearAllActivity();
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("登录");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });
    }

    /**
     * 设置监听
     */
    @OnClick({R.id.log_btn_commit, R.id.log_txt_register, R.id.log_txt_forget_password})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.log_btn_commit:
                //登录
                String id = mEdtId.getText().toString().trim();
                String password = mEdtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(id)) {
                    showToast("帐号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    showToast("密码不能为空");
                    return;
                }
                logIn(id, password);
                break;
            case R.id.log_txt_register:
                //注册
                Intent intent = new Intent(LogActivity.this, RegisterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(SKIP_NUMBER, 0);
                intent.putExtras(bundle);
                startActivity(intent);
                getActivityInFromRight();
                break;
            case R.id.log_txt_forget_password:
                //忘记密码
                startActivity(new Intent(LogActivity.this, ForgetPasswordActivity.class));
                getActivityInFromRight();
                break;
        }
    }

    /**
     * 登录
     *
     * @param phoneNumber 用户名
     * @param password    密码
     */
    public void logIn(String phoneNumber, String password) {
        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(KEY_PACKNAME, 1018);
//            jsonObject.put("phone_number", phoneNumber);
//            jsonObject.put("password", MD5(password));
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        params.addBodyParameter(KEY_PACKNAME, "1018");
        params.addBodyParameter("phone_number", phoneNumber);
        params.addBodyParameter("password", MD5(password));

        showLoadingDialog("登录中...");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            //登录成功
                            showDismissLoadingDialog("登录成功", true);
                            JSONObject userInfo = new JSONObject(result.getString("userinfo"));
                            PmApplication.getInstance().getShared().putInt(USER_ID, userInfo.getInt("id"));
                            PmApplication.getInstance().getShared().putInt(IS_VIP, userInfo.getInt("isvip"));//0是会员  1是vip
                            goBackLastActivity();
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("登录失败", false);
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


}
