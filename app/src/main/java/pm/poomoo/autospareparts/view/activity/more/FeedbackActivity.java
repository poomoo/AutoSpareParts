package pm.poomoo.autospareparts.view.activity.more;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import pm.poomoo.autospareparts.base.PmBaseActivity;

/**
 * 意见反馈
 */
public class FeedbackActivity extends PmBaseActivity {

    private final String TAG = FeedbackActivity.class.getSimpleName();
    @ViewInject(R.id.feedback_count)
    private TextView mTxtCount;//字数统计
    @ViewInject(R.id.feedback_edt_input_content)
    private EditText mEdtContent;//反馈内容
    @ViewInject(R.id.feedback_edt_input_phone_number)
    private EditText mEdtPhoneNumber;//反馈者联系方式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
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
        headerViewHolder.title.setText("意见反馈");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });

        mEdtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = mEdtContent.getText().toString().trim();
                if (string.length() > 300) {
                    mEdtContent.setText(string.substring(0, 300));
                }
                mTxtCount.setText(mEdtContent.getText().toString().length() + "/300");
            }
        });
    }

    /**
     * 设置监听
     */
    @OnClick({R.id.feedback_btn_commit})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.feedback_btn_commit:
                //提交反馈
                final String content = mEdtContent.getText().toString().trim();
                final String contact = mEdtPhoneNumber.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    new AlertDialog.Builder(FeedbackActivity.this).setTitle("提示").setMessage("确认提交").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            commitFeedbackContent(content, contact);
                        }
                    }).setNegativeButton("取消", null).create().show();
                } else showToast("请输入反馈内容");
                break;
        }
    }

    /**
     * 提交反馈内容到服务器
     *
     * @param content 反馈的内容
     */
    public void commitFeedbackContent(String content, String phoneNumber) {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_PACKNAME, 1015);
            jsonObject.put("feedback_content", content);
            jsonObject.put("phone_or_email", phoneNumber);
            params.addBodyParameter(KEY, jsonObject.toString());
            showLog(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoadingDialog("反馈中...");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog();
                            new AlertDialog.Builder(FeedbackActivity.this).setTitle("提示").setMessage("反馈成功，谢谢你的的建议，我们会尽快跟进的").setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            goBackLastActivity();
                                        }
                                    }).create().show();
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("反馈失败", false);
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
