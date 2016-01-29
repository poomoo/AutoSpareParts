package pm.poomoo.autospareparts.view.activity.company;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
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
 * 显示商家详细信息
 */
public class CompanyInformationActivity extends PmBaseActivity {

    private final String TAG = CompanyInformationActivity.class.getSimpleName();
    @ViewInject(R.id.company_information_glide)
    private PmGlide mGlide;
    @ViewInject(R.id.company_information_company_name)
    private TextView mTxtCompanyName;
    @ViewInject(R.id.company_information_explain)
    private TextView mTxtExplain;
    @ViewInject(R.id.company_information_name)
    private TextView mTxtName;
    @ViewInject(R.id.company_information_number)
    private TextView mTxtNumber;
    @ViewInject(R.id.company_information_phone_number_one)
    private TextView mTxtPhoneNumberOne;
    @ViewInject(R.id.company_information_phone_number_two)
    private TextView mTxtPhoneNumberTwo;
    @ViewInject(R.id.company_information_phone_number_three)
    private TextView mTxtPhoneNumberThree;
    @ViewInject(R.id.company_information_phone_number_four)
    private TextView mTxtPhoneNumberFour;
    @ViewInject(R.id.company_information_qq)
    private TextView mTxtQQ;
    @ViewInject(R.id.company_information_fax)
    private TextView mTxtFax;
    @ViewInject(R.id.company_information_email)
    private TextView mTxtEmail;
    @ViewInject(R.id.company_information_weixin)
    private TextView mTxtWeixin;
    @ViewInject(R.id.company_information_address)
    private TextView mTxtAddress;
    @ViewInject(R.id.company_information_txt_collect)
    private TextView mCollect;

    @ViewInject(R.id.company_information_linear_contact)
    private LinearLayout mLinearContact;
    @ViewInject(R.id.company_information_linear_number)
    private LinearLayout mLinearNumber;
    @ViewInject(R.id.company_information_linear_phone_number)
    private LinearLayout mLinearPhoneNumber;
    @ViewInject(R.id.company_information_linear_qq)
    private LinearLayout mLinearQQ;
    @ViewInject(R.id.company_information_linear_fax)
    private LinearLayout mLinearFax;
    @ViewInject(R.id.company_information_linear_email)
    private LinearLayout mLinearEmail;
    @ViewInject(R.id.company_information_linear_weixin)
    private LinearLayout mLinearWeixin;
    @ViewInject(R.id.company_information_linear_address)
    private LinearLayout mLinearAddress;


    private boolean mIsCollect = false;//false没有  true已经收藏
    private ArrayList<String> picUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_information);
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("公司详情");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });

        onIsCollectCompany();

        String pic = PmApplication.getInstance().getShowCompanyInfos().get(0).getPic();
        String[] picList = pic.split("[,]");
        for (int i = 0; i < picList.length; i++) {
            picUrls.add(PIC_RUL + picList[i].substring(2));
            picList[i] = PIC_RUL + picList[i].substring(2);
        }
        mGlide.initPic(picList, this);
        mGlide.startAnimation();
        mGlide.setPicClickListener(new PmGlide.picOnClickListener() {
            @Override
            public void onPicClick(int index) {
                imageBrowse(index, picUrls);
            }
        });

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowCompanyInfos().get(0).getName()))
            mLinearContact.setVisibility(View.GONE);
        else
            mTxtCompanyName.setText(PmApplication.getInstance().getShowCompanyInfos().get(0).getName());

        mTxtExplain.setText("商家简介:" + PmApplication.getInstance().getShowCompanyInfos().get(0).getExplain());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowCompanyInfos().get(0).getContact()))
            mLinearContact.setVisibility(View.GONE);
        else
            mTxtName.setText(PmApplication.getInstance().getShowCompanyInfos().get(0).getContact());


        mTxtNumber.setText(PmApplication.getInstance().getShowCompanyInfos().get(0).getSpecialNumber());
//        mTxtNumber.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTxtNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PmApplication.getInstance().getShowCompanyInfos().get(0).getSpecialNumber()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivityInFromRight();
            }
        });

        String phoneNumber = PmApplication.getInstance().getShowCompanyInfos().get(0).getCellPhone();
        final String[] phone = phoneNumber.split("[@]");
        for (int i = 0; i < phone.length; i++) {
            switch (i) {
                case 0:
                    mTxtPhoneNumberOne.setVisibility(View.VISIBLE);
                    mTxtPhoneNumberOne.setText(phone[i]);
//                    mTxtPhoneNumberOne.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    mTxtPhoneNumberOne.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone[0]));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivityInFromRight();
                        }
                    });
                    break;
                case 1:
                    mTxtPhoneNumberTwo.setVisibility(View.VISIBLE);
                    mTxtPhoneNumberTwo.setText(phone[i]);
//                    mTxtPhoneNumberTwo.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    mTxtPhoneNumberTwo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone[1]));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivityInFromRight();
                        }
                    });
                    break;
                case 2:
                    mTxtPhoneNumberThree.setVisibility(View.VISIBLE);
                    mTxtPhoneNumberThree.setText(phone[i]);
//                    mTxtPhoneNumberThree.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    mTxtPhoneNumberThree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone[2]));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivityInFromRight();
                        }
                    });
                    break;
                case 3:
                    mTxtPhoneNumberFour.setVisibility(View.VISIBLE);
                    mTxtPhoneNumberFour.setText(phone[i]);
//                    mTxtPhoneNumberFour.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    mTxtPhoneNumberFour.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone[3]));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivityInFromRight();
                        }
                    });
                    break;
            }
        }

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowCompanyInfos().get(0).getQQ()))
            mLinearQQ.setVisibility(View.GONE);
        else
            mTxtQQ.setText(PmApplication.getInstance().getShowCompanyInfos().get(0).getQQ());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowCompanyInfos().get(0).getFax()))
            mLinearFax.setVisibility(View.GONE);
        else
            mTxtFax.setText(PmApplication.getInstance().getShowCompanyInfos().get(0).getFax());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowCompanyInfos().get(0).getEmail()))
            mLinearEmail.setVisibility(View.GONE);
        else
            mTxtEmail.setText(PmApplication.getInstance().getShowCompanyInfos().get(0).getEmail());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowCompanyInfos().get(0).getWeixin()))
            mLinearWeixin.setVisibility(View.GONE);
        else
            mTxtWeixin.setText(PmApplication.getInstance().getShowCompanyInfos().get(0).getWeixin());

        if (TextUtils.isEmpty(PmApplication.getInstance().getShowCompanyInfos().get(0).getAddress()))
            mLinearAddress.setVisibility(View.GONE);
        else
            mTxtAddress.setText(PmApplication.getInstance().getShowCompanyInfos().get(0).getAddress());

        onWriteBitmapToSdcard(BitmapFactory.decodeResource(getResources(), R.drawable.icon));
    }

    /**
     * 设置按钮点击监听
     */
    @OnClick({R.id.company_information_linear_collect, R.id.company_information_linear_shared})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.company_information_linear_collect:
                //收藏，取消收藏公司
                if (mIsCollect) {
                    onCancelCollectCompany();
                } else {
                    onCollectCompany();
                }
                break;
            case R.id.company_information_linear_shared:
                //分享
//                ShareSDK.initSDK(CompanyInformationActivity.this);
//                OnekeyShare oks = new OnekeyShare();
//                //关闭sso授权
//                oks.disableSSOWhenAuthorize();
//
//                // 分享时Notification的图标和文字
//                oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//                oks.setTitle(getString(R.string.share));
//                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//                //oks.setTitleUrl("http://sharesdk.cn");
//                // text是分享文本，所有平台都需要这个字段
//                oks.setText(SHARE_TEXT);
//                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//                oks.setImagePath(Environment.getExternalStorageDirectory() + "/icon.jpg");
//                // 启动分享GUI
//                oks.show(CompanyInformationActivity.this);

                ShareSDK.initSDK(this);
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();

                // 分享时Notification的图标和文字
                oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                oks.setTitle(getString(R.string.share));
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                oks.setTitleUrl("http://sharesdk.cn");
                // text是分享文本，所有平台都需要这个字段
                oks.setText("我是分享文本");
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//                oks.setImagePath("/sdcard/test.jpg");
                // url仅在微信（包括好友和朋友圈）中使用
                oks.setUrl("http://sharesdk.cn");
                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//                oks.setComment(SHARE_TEXT);
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite(getString(R.string.app_name));
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl("http://sharesdk.cn");
                oks.setText(SHARE_TEXT);
                oks.setImagePath(Environment.getExternalStorageDirectory() + "/icon.jpg");
                // 启动分享GUI
                oks.show(this);
                break;
        }
    }

    /**
     * 收藏公司
     */
    public void onCollectCompany() {
        if (PmApplication.getInstance().getShared().getInt(USER_ID) == 0) {
            showToast("你还没有登陆，请先登录");
            return;
        }
        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(KEY_PACKNAME, 1005);
//            jsonObject.put("id", PmApplication.getInstance().getShared().getInt(USER_ID));
//            jsonObject.put("company_id", PmApplication.getInstance().getShowCompanyInfos().get(0).getId());
//            jsonObject.put("type", 1);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        params.addBodyParameter(KEY_PACKNAME, "1005");
        params.addBodyParameter("id", PmApplication.getInstance().getShared().getInt(USER_ID) + "");
        params.addBodyParameter("company_id", PmApplication.getInstance().getShowCompanyInfos().get(0).getId() + "");
        params.addBodyParameter("type", 1 + "");

        showLoadingDialog("收藏商家");
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
                            mCollect.setText("取消收藏");
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
        params.addBodyParameter("company_id", PmApplication.getInstance().getShowCompanyInfos().get(0).getId() + "");
        params.addBodyParameter("type", 1 + "");

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
                                mCollect.setText("收藏");
                            } else {
                                //已经收藏
                                mIsCollect = true;
                                mCollect.setText("取消收藏");
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
     * 取消收藏公司
     */
    public void onCancelCollectCompany() {
        if (PmApplication.getInstance().getShared().getInt(USER_ID) == 0) {
            showToast("你还没有登陆，请先登录");
            return;
        }
        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(KEY_PACKNAME, 1006);
//            jsonObject.put("id", PmApplication.getInstance().getShared().getInt(USER_ID));
//            jsonObject.put("company_id", PmApplication.getInstance().getShowCompanyInfos().get(0).getId());
//            jsonObject.put("type", 1);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        params.addBodyParameter(KEY_PACKNAME, "1006");
        params.addBodyParameter("id", PmApplication.getInstance().getShared().getInt(USER_ID) + "");
        params.addBodyParameter("company_id", PmApplication.getInstance().getShowCompanyInfos().get(0).getId() + "");
        params.addBodyParameter("type", 1 + "");


        showLoadingDialog("取消收藏该公司");
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
                            mCollect.setText("收藏");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGlide.stopAnimation();
    }

    protected void imageBrowse(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(this, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        this.startActivity(intent);
    }
}
