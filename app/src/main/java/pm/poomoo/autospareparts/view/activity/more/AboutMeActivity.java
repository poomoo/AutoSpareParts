package pm.poomoo.autospareparts.view.activity.more;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONObject;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseActivity;

/**
 * 关于我们
 */
public class AboutMeActivity extends PmBaseActivity {

    private final String TAG = AboutMeActivity.class.getSimpleName();
    @ViewInject(R.id.about_me_explain)
    private TextView mTxtExplain;//显示说明
    @ViewInject(R.id.about_me_phone)
    private TextView mTxtPhoneOne;//显示客户电话
    @ViewInject(R.id.about_me_address)
    private TextView mTxtPhoneTwo;//显示投诉电话
    @ViewInject(R.id.about_me_version)
    private TextView mTxtPhoneVersion;//显示版本号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
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
        headerViewHolder.title.setText("关于我们");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });
//        mTxtExplain.setText(PmApplication.getInstance().getShared().getString(ABOUT_ME_NAME_EXPLAIN));
        mTxtPhoneVersion.setText("版本:v" + getVersionName());
//        mTxtPhoneOne.setText("客户电话: 15254574584");
//        mTxtPhoneTwo.setText("投诉电话: 16235489784");
        //获取网络的数据
//        onGetAboutMeInformation();
    }

    /**
     * 设置监听
     */
    public void setOnClickListener() {
    }

//    /**
//     * 获取“关于我们”信息
//     */
//    public void onGetAboutMeInformation() {
//        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(KEY_PACKNAME, 1020);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {
//                try {
//                    showLog(TAG, responseInfo.result);
//                    JSONObject result = new JSONObject(responseInfo.result);
//                    switch (result.getInt(KEY_RESULT)) {
//                        case RET_SUCCESS:
//                            JSONObject object = new JSONObject(result.getString("list"));
//                            String explain = object.getString("descrption");
//                            PmApplication.getInstance().getShared().putString(ABOUT_ME_NAME_EXPLAIN, explain);
//                            mTxtExplain.setText(explain);
//                            break;
//                        case RET_FAIL:
//                            break;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(HttpException error, String msg) {
//
//            }
//        });
//    }
}
