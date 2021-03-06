package pm.poomoo.autospareparts.view.activity.more;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.sharesdk.framework.Platform;
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
    private String weburl = "http://www.gyqphy.com";
    private String path;

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
        getPath();
    }

    /**
     * 设置单击监听
     * <p/>
     * ShareSDK.initSDK(this);
     * OnekeyShare oks = new OnekeyShare();
     * //关闭sso授权
     * oks.disableSSOWhenAuthorize();
     * <p/>
     * // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
     * //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
     * // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
     * oks.setTitle(getString(R.string.share));
     * // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
     * oks.setTitleUrl("http://sharesdk.cn");
     * // text是分享文本，所有平台都需要这个字段
     * oks.setText("我是分享文本");
     * // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
     * oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
     * // url仅在微信（包括好友和朋友圈）中使用
     * oks.setUrl("http://sharesdk.cn");
     * // comment是我对这条分享的评论，仅在人人网和QQ空间使用
     * oks.setComment("我是测试评论文本");
     * // site是分享此内容的网站名称，仅在QQ空间使用
     * oks.setSite(getString(R.string.app_name));
     * // siteUrl是分享此内容的网站地址，仅在QQ空间使用
     * oks.setSiteUrl("http://sharesdk.cn");
     * <p/>
     * // 启动分享GUI
     * oks.show(this);
     */
    @OnClick({R.id.activity_share_relative_wechat, R.id.activity_share_relative_qq, R.id.activity_share_relative_qzon, R.id.activity_share_relative_weibo,
            R.id.activity_share_relative_renren, R.id.activity_share_relative_tent_weibo, R.id.activity_share_relative_sms, R.id.activity_share_relative_friend})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.activity_share_relative_wechat:
                //分享到微信
                Wechat.ShareParams wechat = new Wechat.ShareParams();
                wechat.setTitle(getString(R.string.app_name));
                wechat.setText(SHARE_TEXT);
                wechat.setImageData(bitmap);
                wechat.setUrl(weburl);
                wechat.setShareType(Platform.SHARE_WEBPAGE);
                Log.i("lmf", "分享到微信:" + wechat);
                ShareSDK.getPlatform(this, Wechat.NAME).share(wechat);
                break;
            case R.id.activity_share_relative_friend:
                //分享到朋友圈
                WechatMoments.ShareParams wechatMomentsShare = new WechatMoments.ShareParams();
                wechatMomentsShare.setTitle(SHARE_TEXT);
                wechatMomentsShare.setImageData(bitmap);
                wechatMomentsShare.setUrl(weburl);
                wechatMomentsShare.setShareType(Platform.SHARE_WEBPAGE);
                ShareSDK.getPlatform(WechatMoments.NAME).share(wechatMomentsShare);
                break;
            case R.id.activity_share_relative_qq:
                //分享到QQ
                QQ.ShareParams qq = new QQ.ShareParams();
                qq.setTitle(getString(R.string.app_name));
                qq.setTitleUrl(weburl);
                qq.setText(SHARE_TEXT);
                qq.setImagePath(path);
//                qq.setImagePath(getResources().getAssets().toString() + "share.png");
//                qq.setImageUrl("http://pic2.ooopic.com/01/03/51/25b1OOOPIC19.jpg");
                Log.i("lmf", "分享到QQ:" + qq);
                ShareSDK.getPlatform(this, QQ.NAME).share(qq);
                break;
            case R.id.activity_share_relative_qzon:
                //分享到空间
                QZone.ShareParams qzone = new QZone.ShareParams();
                qzone.setTitle(getString(R.string.app_name));
                qzone.setTitleUrl(weburl);
                qzone.setText(SHARE_TEXT);
                qzone.setImageData(bitmap);
                ShareSDK.getPlatform(this, QZone.NAME).share(qzone);
                break;
            case R.id.activity_share_relative_weibo:
                //分享到微博
                SinaWeibo.ShareParams weibo = new SinaWeibo.ShareParams();
                weibo.setText(SHARE_TEXT);
                weibo.setImageData(bitmap);
                weibo.setUrl(weburl);
                ShareSDK.getPlatform(SinaWeibo.NAME).share(weibo);
                break;
            case R.id.activity_share_relative_renren:
                //分享到人人
                Renren.ShareParams renren = new Renren.ShareParams();
                renren.setText(SHARE_TEXT);
                renren.setImageData(bitmap);
                renren.setUrl(weburl);
                ShareSDK.getPlatform(Renren.NAME).share(renren);
                break;
            case R.id.activity_share_relative_tent_weibo:
                //分享到腾讯微博
                TencentWeibo.ShareParams tencent = new TencentWeibo.ShareParams();
                tencent.setText(SHARE_TEXT);
                tencent.setImageData(bitmap);
                tencent.setUrl(weburl);
                ShareSDK.getPlatform(TencentWeibo.NAME).share(tencent);
                break;
            case R.id.activity_share_relative_sms:
                //分享到短信
                ShortMessage.ShareParams shortMessage = new ShortMessage.ShareParams();
                shortMessage.setText(SHARE_TEXT + weburl);
                ShareSDK.getPlatform(ShortMessage.NAME).share(shortMessage);
                break;

        }
    }

    private void getPath() {
        try {// 判断SD卡中是否存在此文件夹
            File baseFile = new File(Environment.getExternalStorageDirectory(), "share");
            if (!baseFile.exists()) {
                baseFile.mkdir();
            }
            path = baseFile.getAbsolutePath() + "/share.png";
            File file = new File(path);
            // 判断图片是否存此文件夹中
            if (!file.exists()) {
                file.createNewFile();
                Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            path = null;
        }
    }
}
