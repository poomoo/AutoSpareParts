package pm.poomoo.autospareparts.view.activity.client;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseActivity;
import pm.poomoo.autospareparts.util.PmGlide;
import pm.poomoo.autospareparts.view.custom.bigimage.ImagePagerActivity;

/**
 * 客户详细信息显示
 */
public class ClientInformationActivity extends PmBaseActivity {

    private final String TAG = ClientInformationActivity.class.getSimpleName();
    @ViewInject(R.id.client_information_glide)
    private PmGlide mGlide;//滑动广告条
    @ViewInject(R.id.client_information_client_name)
    private TextView mTxtClientName;//名称
    @ViewInject(R.id.client_information_explain)
    private TextView mTxtExplain;//说明
    @ViewInject(R.id.client_information_name)
    private TextView mTxtName;//联系人
    @ViewInject(R.id.client_information_number)
    private TextView mTxtNumber;//座机号码
    @ViewInject(R.id.client_information_phone_number_one)
    private TextView mTxtPhoneNumberOne;//手机号
    @ViewInject(R.id.client_information_phone_number_two)
    private TextView mTxtPhoneNumberTwo;//手机号
    @ViewInject(R.id.client_information_phone_number_three)
    private TextView mTxtPhoneNumberThree;//手机号
    @ViewInject(R.id.client_information_phone_number_four)
    private TextView mTxtPhoneNumberFour;//手机号
    @ViewInject(R.id.client_information_qq)
    private TextView mTxtQQ;//QQ号
    @ViewInject(R.id.client_information_fax)
    private TextView mTxtFax;//传真
    @ViewInject(R.id.client_information_email)
    private TextView mTxtEmail;//邮箱地址
    @ViewInject(R.id.client_information_weixin)
    private TextView mTxtWeixin;//微信号
    @ViewInject(R.id.client_information_address)
    private TextView mTxtAddress;//地址
    @ViewInject(R.id.client_information_txt_collect)
    private TextView mTxtCollect;//显示“收藏”

    @ViewInject(R.id.client_information_linear_contact)
    private LinearLayout mLinearContact;
    @ViewInject(R.id.client_information_linear_number)
    private LinearLayout mLinearNumber;
    @ViewInject(R.id.client_information_linear_phone_number)
    private LinearLayout mLinearPhoneNumber;
    @ViewInject(R.id.client_information_linear_qq)
    private LinearLayout mLinearQQ;
    @ViewInject(R.id.client_information_linear_fax)
    private LinearLayout mLinearFax;
    @ViewInject(R.id.client_information_linear_email)
    private LinearLayout mLinearEmail;
    @ViewInject(R.id.client_information_linear_weixin)
    private LinearLayout mLinearWeixin;
    @ViewInject(R.id.client_information_linear_address)
    private LinearLayout mLinearAddress;

    private boolean mIsCollect = false;//是否收藏
    private ArrayList<String> list = new ArrayList<>();

    private String weburl = "http://www.gyqphy.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_information);
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("客户资料");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });

        String[] array = PmApplication.getInstance().getShowClientInfo().get(0).getPic().split("[,]");
        for (int i = 0; i < array.length; i++) {
            array[i] = PIC_RUL + array[i].substring(2);
            list.add(array[i]);
        }

        mGlide.initPic(array, this);
        mGlide.startAnimation();
        mGlide.setPicClickListener(new PmGlide.picOnClickListener() {
            @Override
            public void onPicClick(int index) {
                imageBrowse(index, list);
            }
        });

        onIsCollectCompany();//判断是否收藏

        mTxtClientName.setText(PmApplication.getInstance().getShowClientInfo().get(0).getName());
        mTxtExplain.setText("商家简介:" + PmApplication.getInstance().getShowClientInfo().get(0).getExplain());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowClientInfo().get(0).getContact()))
            mLinearContact.setVisibility(View.GONE);
        else
            mTxtName.setText(PmApplication.getInstance().getShowClientInfo().get(0).getContact());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowClientInfo().get(0).getSpecialNumber()))
            mLinearNumber.setVisibility(View.GONE);
        else
            mTxtNumber.setText(PmApplication.getInstance().getShowClientInfo().get(0).getSpecialNumber());
//        mTxtNumber.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTxtNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (PmApplication.getInstance().getShowClientInfo().get(0).getSpecialNumber())));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivityInFromRight();
            }
        });
        final String[] phoneNumber = PmApplication.getInstance().getShowClientInfo().get(0).getCellPhone().split("[@]");
        for (int i = 0; i < phoneNumber.length; i++) {
            switch (i) {
                case 0:
                    mTxtPhoneNumberOne.setVisibility(View.VISIBLE);
                    mTxtPhoneNumberOne.setText(phoneNumber[i]);
//                    mTxtPhoneNumberOne.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    mTxtPhoneNumberOne.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber[0]));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivityInFromRight();
                        }
                    });
                    break;
                case 1:
                    mTxtPhoneNumberTwo.setVisibility(View.VISIBLE);
                    mTxtPhoneNumberTwo.setText(phoneNumber[i]);
//                    mTxtPhoneNumberTwo.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    mTxtPhoneNumberTwo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber[1]));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivityInFromRight();
                        }
                    });
                    break;
                case 2:
                    mTxtPhoneNumberThree.setVisibility(View.VISIBLE);
                    mTxtPhoneNumberThree.setText(phoneNumber[i]);
//                    mTxtPhoneNumberThree.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    mTxtPhoneNumberThree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber[2]));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivityInFromRight();
                        }
                    });
                    break;
                case 3:
                    mTxtPhoneNumberFour.setVisibility(View.VISIBLE);
                    mTxtPhoneNumberFour.setText(phoneNumber[i]);
//                    mTxtPhoneNumberFour.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    mTxtPhoneNumberFour.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber[3]));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivityInFromRight();
                        }
                    });
                    break;
            }
        }
        if (TextUtils.isEmpty(PmApplication.getInstance().getShowClientInfo().get(0).getQQ()))
            mLinearQQ.setVisibility(View.GONE);
        else
            mTxtQQ.setText(PmApplication.getInstance().getShowClientInfo().get(0).getQQ());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowClientInfo().get(0).getFax()))
            mLinearFax.setVisibility(View.GONE);
        else
            mTxtFax.setText(PmApplication.getInstance().getShowClientInfo().get(0).getFax());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowClientInfo().get(0).getEmail()))
            mLinearEmail.setVisibility(View.GONE);
        else
            mTxtEmail.setText(PmApplication.getInstance().getShowClientInfo().get(0).getEmail());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowClientInfo().get(0).getWeixin()))
            mLinearWeixin.setVisibility(View.GONE);
        else
            mTxtWeixin.setText(PmApplication.getInstance().getShowClientInfo().get(0).getWeixin());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowClientInfo().get(0).getAddress()))
            mLinearAddress.setVisibility(View.GONE);
        else
            mTxtAddress.setText(PmApplication.getInstance().getShowClientInfo().get(0).getAddress());

        onWriteBitmapToSdcard(BitmapFactory.decodeResource(getResources(), R.drawable.icon));
    }

    /**
     * 设置按钮点击监听
     */
    @OnClick({R.id.client_information_linear_collect, R.id.client_information_linear_shared})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.client_information_linear_collect:
                if (mIsCollect) {
                    onCancelCollectClient();
                } else {
                    onCollectClient();
                }
                break;
            case R.id.client_information_linear_shared:
                //分享
                ShareSDK.initSDK(this);
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();
                // 分享时Notification的图标和文字
                oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                oks.setTitle(getString(R.string.app_name));
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                oks.setTitleUrl(weburl);
                // text是分享文本，所有平台都需要这个字段
                oks.setText(SHARE_TEXT);
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                oks.setUrl(weburl);
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite(getString(R.string.app_name));
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl(weburl);
                oks.setImagePath(Environment.getExternalStorageDirectory() + "/icon.jpg");
                // 启动分享GUI
                oks.show(this);
                break;
        }
    }

    /**
     * 收藏客户
     */
    public void onCollectClient() {
        if (PmApplication.getInstance().getShared().getInt(USER_ID) == 0) {
            showToast("你还没有登陆，请先登录");
            return;
        }
        if (PmApplication.getInstance().getShared().getInt(IS_VIP) == 0) {
            showToast("你不是VIP，不能收藏客户资料");
            return;
        }
        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(KEY_PACKNAME, 1009);
//            jsonObject.put("vip_id", PmApplication.getInstance().getShared().getInt(USER_ID));
//            jsonObject.put("user_id", PmApplication.getInstance().getShowClientInfo().get(0).getId());
//            jsonObject.put("type", 2);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        params.addBodyParameter(KEY_PACKNAME, "1009");
        params.addBodyParameter("vip_id", PmApplication.getInstance().getShared().getInt(USER_ID) + "");
        params.addBodyParameter("user_id", PmApplication.getInstance().getShowClientInfo().get(0).getId() + "");
        params.addBodyParameter("type", 2 + "");

        showLoadingDialog("收藏客户");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog("收藏成功", true);
                            mIsCollect = true;
                            mTxtCollect.setText("取消收藏");
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("收藏失败", false);
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
     * 判断是否收藏
     */
    public void onIsCollectCompany() {
        if (PmApplication.getInstance().getShared().getInt(USER_ID) == 0) {
            return;
        }
        RequestParams params = new RequestParams();
        params.addBodyParameter(KEY_PACKNAME, "1030 ");
        params.addBodyParameter("user_id", PmApplication.getInstance().getShared().getInt(USER_ID) + "");
        params.addBodyParameter("company_id", PmApplication.getInstance().getShowClientInfo().get(0).getId() + "");
        params.addBodyParameter("type", 2 + "");

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (result.getInt("code") == 0) {
                                //没有收藏
                                mIsCollect = false;
                                mTxtCollect.setText("收藏");
                            } else {
                                //已经收藏
                                mIsCollect = true;
                                mTxtCollect.setText("取消收藏");
                            }
                            break;
                        case RET_FAIL:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
            }
        });
    }


    /**
     * 取消收藏客户
     */
    public void onCancelCollectClient() {
        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(KEY_PACKNAME, 1010);
//            jsonObject.put("vip_id", PmApplication.getInstance().getShared().getInt(USER_ID));
//            jsonObject.put("user_id", PmApplication.getInstance().getShowClientInfo().get(0).getId());
//            jsonObject.put("type", 2);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        params.addBodyParameter(KEY_PACKNAME, "1010");
        params.addBodyParameter("vip_id", PmApplication.getInstance().getShared().getInt(USER_ID) + "");
        params.addBodyParameter("user_id", PmApplication.getInstance().getShowClientInfo().get(0).getId() + "");
        params.addBodyParameter("type", 2 + "");

        showLoadingDialog("取消收藏该客户");
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog("取消收藏成功", true);
                            mIsCollect = false;
                            mTxtCollect.setText("收藏");
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("取消收藏失败", false);
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

    private void imageBrowse(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(this, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGlide.stopAnimation();
    }
}
