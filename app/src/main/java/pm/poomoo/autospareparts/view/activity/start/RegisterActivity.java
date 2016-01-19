package pm.poomoo.autospareparts.view.activity.start;

import android.graphics.Color;
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

import org.json.JSONException;
import org.json.JSONObject;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseActivity;

/**
 * 注册界面
 */
public class RegisterActivity extends PmBaseActivity {

    private final String TAG = RegisterActivity.class.getSimpleName();
    @ViewInject(R.id.register_choice_one)
    private TextView mTxtChoiceOne;
    @ViewInject(R.id.register_choice_two)
    private TextView mTxtChoiceTwo;
    @ViewInject(R.id.register_get_check_number)
    private TextView mTxtGetCheckNumber;
    @ViewInject(R.id.register_edt_phone_number)
    private EditText mEdtPhoneNumber;
    @ViewInject(R.id.register_edt_check_number)
    private EditText mEdtCheck;
    @ViewInject(R.id.register_edt_password_one)
    private EditText mEdtPasswordOne;
    @ViewInject(R.id.register_edt_password_two)
    private EditText mEdtPasswordTwo;

    private int mNumber = 0;// 0:为会员   1:VIP注册
    private int mThreadTime = 60;//现成计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                mTxtGetCheckNumber.setText("获取验证码");
                mTxtGetCheckNumber.setBackgroundResource(R.drawable.register_get_check_number_normal);
            } else {
                mTxtGetCheckNumber.setText("获取中(" + msg.what + ")");
                mTxtGetCheckNumber.setBackgroundResource(R.drawable.register_get_check_number_down);
            }
        }
    };

    /**
     * 初始化
     */
    public void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("注册");
        mNumber = getIntent().getExtras().getInt(SKIP_NUMBER);
        if (mNumber == 0) {
            mTxtChoiceOne.setBackgroundColor(Color.parseColor("#20FFFFFF"));
            mTxtChoiceTwo.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        } else if (mNumber == 1) {
            mTxtChoiceOne.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            mTxtChoiceTwo.setBackgroundColor(Color.parseColor("#20FFFFFF"));
        }
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadTime = 1;//停止线程
                goBackLastActivity();
            }
        });
    }

    /**
     * 设置监听
     */
    @OnClick({R.id.register_choice_one, R.id.register_choice_two, R.id.register_get_check_number, R.id.register_btn_complete})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.register_choice_one:
                //选择会员注册
                mNumber = 0;
                mTxtChoiceOne.setBackgroundColor(Color.parseColor("#20FFFFFF"));
                mTxtChoiceTwo.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                break;
            case R.id.register_choice_two:
                //选择VIP注册
                mNumber = 1;
                mTxtChoiceOne.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                mTxtChoiceTwo.setBackgroundColor(Color.parseColor("#20FFFFFF"));
                break;
            case R.id.register_get_check_number:
                //获取验证码
                String phoneNumber = mEdtPhoneNumber.getText().toString().trim();
                if (phoneNumber.equals("")) {
                    showToast("请输入手机号码");
                    return;
                }
                onGetCheckNumber(phoneNumber);
                mTxtGetCheckNumber.setBackgroundResource(R.drawable.register_get_check_number_down);
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
                break;
            case R.id.register_btn_complete:
                //完成按钮
                String phone = mEdtPhoneNumber.getText().toString().trim();
                String check = mEdtCheck.getText().toString().trim();
                String passwordOne = mEdtPasswordOne.getText().toString().trim();
                String passwordTwo = mEdtPasswordTwo.getText().toString().trim();
                if (phone.equals("")) {
                    showToast("请输入手机号码");
                    return;
                }
                if (check.equals("")) {
                    showToast("请输入验证码");
                    return;
                }
                if (passwordOne.length() < 6 || passwordTwo.length() < 6) {
                    showToast("密码长度应该大于6位");
                    return;
                }
                if (!passwordOne.equals(passwordTwo)) {
                    showToast("两次密码不一致");
                    return;
                }
                onRegister(phone, check, passwordOne);
                mThreadTime = 1;//停止线程
                break;
        }
    }

    /**
     * 获取验证码
     *
     * @param phoneNumber 电话号码
     */
    public void onGetCheckNumber(String phoneNumber) {
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


        showLoadingDialog("验证码获取中...");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog("获取成功，等待短信", true);
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
     * 注册
     *
     * @param phoneNumber 电话号码
     * @param checkNumber 验证码
     * @param password    密码
     */
    public void onRegister(final String phoneNumber, String checkNumber, final String password) {
        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(KEY_PACKNAME, 1017);
//            jsonObject.put("phone_number", phoneNumber);
//            jsonObject.put("check_number", checkNumber);
//            jsonObject.put("password", MD5(password));
//            jsonObject.put("user_type", mNumber);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        params.addBodyParameter(KEY_PACKNAME, "1017");
        params.addBodyParameter("phone_number", phoneNumber);
        params.addBodyParameter("check_number", checkNumber);
        params.addBodyParameter("password", MD5(password));
        params.addBodyParameter("user_type", mNumber + "");

        showLoadingDialog("正在注册，请稍候...");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog("注册成功", true);
                            logIn(phoneNumber, password);
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("注册失败", false);
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
