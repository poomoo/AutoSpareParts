package pm.poomoo.autospareparts.view.activity.start;

import android.os.Bundle;
import android.os.Message;
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
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONObject;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseActivity;

/**
 * 忘记密码
 */
public class ForgetPasswordActivity extends PmBaseActivity {

    private final String TAG = ForgetPasswordActivity.class.getSimpleName();
    @ViewInject(R.id.forget_edt_phone_number)
    private EditText mEdtPhoneNumber;//号码输入框
    @ViewInject(R.id.forget_edt_check_number)
    private EditText mEdtCheckNumber;//验证码输入框
    @ViewInject(R.id.forget_edt_password_one)
    private EditText mEdtPasswordOne;//密码输入框
    @ViewInject(R.id.forget_edt_password_two)
    private EditText mEdtPasswordTwo;//密码输入框
    @ViewInject(R.id.forget_get_check_number)
    private TextView mTxtGetCheck;//获取验证码按钮

    private int mThreadTime = 60;//线程计量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                mTxtGetCheck.setText("获取验证码");
                mTxtGetCheck.setBackgroundResource(R.drawable.register_get_check_number_normal);
            } else {
                mTxtGetCheck.setText("获取中(" + msg.what + ")");
                mTxtGetCheck.setBackgroundResource(R.drawable.register_get_check_number_down);
            }
        }
    };

    /**
     * 初始化
     */
    public void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("忘记密码");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mThreadTime = 1;//停止线程
                goBackLastActivity();
            }
        });
    }

    /**
     * 设置监听
     */
    @OnClick({R.id.forget_get_check_number, R.id.forget_btn_complete})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.forget_get_check_number:
                //获取验证码
                String phone = mEdtPhoneNumber.getText().toString().trim();
                if (phone.length() == 11) {
                    getCheckNumber(phone);
                    mTxtGetCheck.setBackgroundResource(R.drawable.register_get_check_number_down);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mThreadTime = 60;
                                while (mThreadTime != 0) {
                                    mThreadTime--;
                                    handler.sendEmptyMessage(mThreadTime);
                                    sleep(1000);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else showToast("请输入正确的手机号码");
                break;
            case R.id.forget_btn_complete:
                //确定按钮
                String phoneNumber = mEdtPhoneNumber.getText().toString().trim();
                String checkNumber = mEdtCheckNumber.getText().toString().trim();
                String passwordOne = mEdtPasswordOne.getText().toString().trim();
                String passwordTwo = mEdtPasswordTwo.getText().toString().trim();
                if (phoneNumber.equals("")) {
                    showToast("手机号码不能为空");
                    return;
                }
                if (checkNumber.equals("")) {
                    showToast("验证码不能为空");
                    return;
                }
                if (passwordOne.length() < 6 || passwordTwo.length() < 6) {
                    showToast("密码不能小于6位");
                    return;
                }
                if (!passwordOne.equals(passwordTwo)) {
                    showToast("两次输入的密码不一致");
                    return;
                }
                mThreadTime = 1;//停止线程
                updatePassword(phoneNumber, checkNumber, passwordOne);
                break;
        }
    }

    /**
     * 获取验证码
     */
    public void getCheckNumber(String phoneNumber) {
        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(KEY_PACKNAME, 1016);
//            jsonObject.put("phone_number", phoneNumber);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        params.addBodyParameter(KEY_PACKNAME, "1016");
        params.addBodyParameter("phone_number", phoneNumber);

        showLoadingDialog("获取验证码");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog("获取成功，请等待短信", true);
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("获取失败", false);
                            mThreadTime = 1;//停止线程
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                showDismissLoadingDialog("网络错误", false);
                mThreadTime = 1;//停止线程
            }
        });
    }

    /**
     * 修改密码
     */
    public void updatePassword(String phoneNumber, String checkNumber, String password) {
        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(KEY_PACKNAME, 1021);
//            jsonObject.put("phone_number", phoneNumber);
//            jsonObject.put("check_number", checkNumber);
//            jsonObject.put("password", MD5(password));
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        params.addBodyParameter(KEY_PACKNAME, "1021");
        params.addBodyParameter("phone_number", phoneNumber);
        params.addBodyParameter("check_number", checkNumber);
        params.addBodyParameter("password", MD5(password));

        showLoadingDialog("修改密码中");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog("修改成功", true);
                            goBackLastActivity();
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog(result.getString("msg"), false);
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
