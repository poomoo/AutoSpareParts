package pm.poomoo.autospareparts.view.activity.more;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import pm.poomoo.autospareparts.base.PmBaseActivity;

/**
 * 供求发布
 */
public class RequirementActivity extends PmBaseActivity {

    private final String TAG = RequirementActivity.class.getSimpleName();
    @ViewInject(R.id.requirement_edt_input)
    private EditText mEdtRequirement;
    @ViewInject(R.id.requirement_edt_input_phone_number)
    private EditText mEdtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirement);
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        onInit();
    }

    /**
     * 初始化
     */
    public void onInit() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("供求发布");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackLastActivity();
            }
        });
    }

    /**
     * 设置监听
     *
     * @param view 点击的控件
     */
    @OnClick(R.id.requirement_btn_commit)
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.requirement_btn_commit:
                String content = mEdtRequirement.getText().toString().trim();
                String contact = mEdtPhoneNumber.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    commit(content, contact);
                }
                showToast("输入内容不能为空");
                break;
        }
    }

    /**
     * 发布供求到服务器
     *
     * @param content 内容
     */
    public void commit(String content, String phoneNumber) {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_PACKNAME, 1024);
            jsonObject.put("feedback_content", content);
            jsonObject.put("phone_or_email", phoneNumber);
            params.addBodyParameter(KEY, jsonObject.toString());
            showLog(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoadingDialog("发布中...");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog();
                            new AlertDialog.Builder(RequirementActivity.this).setTitle("提示").setMessage("供求发布成功，我们会尽快跟进").setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            goBackLastActivity();
                                        }
                                    }).create().show();
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("发布失败", false);
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
