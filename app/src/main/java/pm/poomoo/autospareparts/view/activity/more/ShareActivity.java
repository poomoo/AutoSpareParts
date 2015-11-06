package pm.poomoo.autospareparts.view.activity.more;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseActivity;

/**
 * Created by Android_PM on 2015/11/5.
 * 软件分享
 */
public class ShareActivity extends PmBaseActivity {
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        clearAllActivity();
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("分享");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
    }

    /**
     * 设置单击监听
     */
    @OnClick({R.id.activity_share_relative_wechat, R.id.activity_share_relative_qq, R.id.activity_share_relative_qzon, R.id.activity_share_relative_weibo,
            R.id.activity_share_relative_renren, R.id.activity_share_relative_tent_weibo, R.id.activity_share_relative_sms, R.id.activity_share_relative_friend})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.activity_share_relative_wechat:
                //分享到微信
                Wechat.ShareParams wechat = new Wechat.ShareParams();
                wechat.setText(SHARE_TEXT);
                wechat.setImageData(bitmap);
                ShareSDK.getPlatform(this, Wechat.NAME).share(wechat);
                break;
            case R.id.activity_share_relative_qq:
                //分享到QQ
                QQ.ShareParams qq = new QQ.ShareParams();
                qq.setText(SHARE_TEXT);
                ShareSDK.getPlatform(this, QQ.NAME).share(qq);
                break;
            case R.id.activity_share_relative_qzon:
                //分享到空间
                QZone.ShareParams qzone = new QZone.ShareParams();
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                qzone.setTitle(getString(R.string.share));
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                qzone.setTitleUrl("http://www.poomoo.cn/");
                // url仅在微信（包括好友和朋友圈）中使用
                qzone.setUrl("http://www.poomoo.cn/");
                // site是分享此内容的网站名称，仅在QQ空间使用
                qzone.setSite(getString(R.string.app_name));
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                qzone.setSiteUrl("http://www.poomoo.cn/");
                qzone.setText(SHARE_TEXT);
                qzone.setImageData(bitmap);
                ShareSDK.getPlatform(this, QZone.NAME).share(qzone);
                break;
            case R.id.activity_share_relative_weibo:
                //分享到微博
                SinaWeibo.ShareParams weibo = new SinaWeibo.ShareParams();
                weibo.setText(SHARE_TEXT);
                weibo.setImageData(bitmap);
                ShareSDK.getPlatform(SinaWeibo.NAME).share(weibo);
                break;
            case R.id.activity_share_relative_renren:
                //分享到人人
                Renren.ShareParams renren = new Renren.ShareParams();
                renren.setText(SHARE_TEXT);
                renren.setImageData(bitmap);
                ShareSDK.getPlatform(Renren.NAME).share(renren);
                break;
            case R.id.activity_share_relative_tent_weibo:
                //分享到腾讯微博
                TencentWeibo.ShareParams tencent = new TencentWeibo.ShareParams();
                tencent.setText(SHARE_TEXT);
                tencent.setImageData(bitmap);
                ShareSDK.getPlatform(TencentWeibo.NAME).share(tencent);
                break;
            case R.id.activity_share_relative_sms:
                //分享到短信
                ShortMessage.ShareParams shortMessage = new ShortMessage.ShareParams();
                shortMessage.setText(SHARE_TEXT);
                ShareSDK.getPlatform(ShortMessage.NAME).share(shortMessage);
                break;
            case R.id.activity_share_relative_friend:
                //分享到朋友圈
                WechatMoments.ShareParams wechatMomentsShare = new WechatMoments.ShareParams();
                wechatMomentsShare.setText(SHARE_TEXT);
                wechatMomentsShare.setImageData(bitmap);
                ShareSDK.getPlatform(WechatMoments.NAME).share(wechatMomentsShare);
//                WechatMoments.ShareParams wechatMomentsShare = new WechatMoments.ShareParams();
//                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//                wechatMomentsShare.setTitle(getString(R.string.share));
//                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//                wechatMomentsShare.setTitleUrl("http://www.poomoo.cn/");
//                // url仅在微信（包括好友和朋友圈）中使用
//                wechatMomentsShare.setUrl("http://www.poomoo.cn/");
//                // site是分享此内容的网站名称，仅在QQ空间使用
//                wechatMomentsShare.setSite(getString(R.string.app_name));
//                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//                wechatMomentsShare.setSiteUrl("http://www.poomoo.cn/");
//                wechatMomentsShare.setText(SHARE_TEXT);
//                wechatMomentsShare.setImagePath(Environment.getExternalStorageDirectory() + "/icon.jpg");
//                ShareSDK.getPlatform(this, WechatMoments.NAME).share(wechatMomentsShare);
                break;
        }
    }
}
