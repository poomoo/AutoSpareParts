package pm.poomoo.autospareparts.view.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qq.QQClientNotExistException;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseFragment;


/**
 * 分享
 *
 * @author AADC
 */
public class FragmentThree extends PmBaseFragment {

    private final String TAG = FragmentThree.class.getSimpleName();
    private Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.frag_three, container, false);
        ViewUtils.inject(this, layout);
        init(layout);
        return layout;
    }

    /**
     * 初始化组件
     */
    public void init(View layout) {
        HeaderViewHolder headerViewHolder = getHeaderView(layout);
        headerViewHolder.title.setText("分享");

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
    }

    /**
     * 设置单击监听
     */
    @OnClick({R.id.three_shared_relative_wechat, R.id.three_shared_relative_qq, R.id.three_shared_relative_qzon, R.id.three_shared_relative_weibo,
            R.id.three_shared_relative_renren, R.id.three_shared_relative_tent_weibo, R.id.three_shared_relative_sms, R.id.three_shared_relative_friend})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.three_shared_relative_wechat:
                //分享到微信
                Wechat.ShareParams wechat = new Wechat.ShareParams();
                wechat.setText(SHARE_TEXT);
                wechat.setImageData(bitmap);
                ShareSDK.getPlatform(getActivity(), Wechat.NAME).share(wechat);
                break;
            case R.id.three_shared_relative_qq:
                //分享到QQ
                QQ.ShareParams qq = new QQ.ShareParams();
                qq.setText(SHARE_TEXT);
                ShareSDK.getPlatform(getActivity(), QQ.NAME).share(qq);
                break;
            case R.id.three_shared_relative_qzon:
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
                ShareSDK.getPlatform(getActivity(), QZone.NAME).share(qzone);
                break;
            case R.id.three_shared_relative_weibo:
                //分享到微博
                SinaWeibo.ShareParams weibo = new SinaWeibo.ShareParams();
                weibo.setText(SHARE_TEXT);
                weibo.setImageData(bitmap);
                ShareSDK.getPlatform(SinaWeibo.NAME).share(weibo);
                break;
            case R.id.three_shared_relative_renren:
                //分享到人人
                Renren.ShareParams renren = new Renren.ShareParams();
                renren.setText(SHARE_TEXT);
                renren.setImageData(bitmap);
                ShareSDK.getPlatform(Renren.NAME).share(renren);
                break;
            case R.id.three_shared_relative_tent_weibo:
                //分享到腾讯微博
                TencentWeibo.ShareParams tencent = new TencentWeibo.ShareParams();
                tencent.setText(SHARE_TEXT);
                tencent.setImageData(bitmap);
                ShareSDK.getPlatform(TencentWeibo.NAME).share(tencent);
                break;
            case R.id.three_shared_relative_sms:
                //分享到短信
                ShortMessage.ShareParams shortMessage = new ShortMessage.ShareParams();
                shortMessage.setText(SHARE_TEXT);
                ShareSDK.getPlatform(ShortMessage.NAME).share(shortMessage);
                break;
            case R.id.three_shared_relative_friend:
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
//                ShareSDK.getPlatform(getActivity(), WechatMoments.NAME).share(wechatMomentsShare);
                break;
        }
    }
}
