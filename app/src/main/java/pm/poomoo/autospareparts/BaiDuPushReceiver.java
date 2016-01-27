/**
 * Copyright (c) 2016. 李苜菲 Inc. All rights reserved.
 */
package pm.poomoo.autospareparts;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;

import java.util.List;

import pm.poomoo.autospareparts.view.activity.start.StartActivity;
import pm.poomoo.autospareparts.view.fragment.MainFragmentActivity;

/**
 * 接收百度云推送的消息
 * 作者: 李苜菲
 * 日期: 2016/1/25 14:08.
 */
public class BaiDuPushReceiver extends PushMessageReceiver {
    private final String TAG = MainFragmentActivity.class.getSimpleName();

    @Override
    public void onBind(Context context, int i, String s, String s1, String s2, String s3) {
        Log.i(TAG, "onBind:" + "errorCode:" + i + " appid:" + s + " userId:" + s1 + " channelId:" + s2 + " requestId:" + s3);
    }

    @Override
    public void onUnbind(Context context, int i, String s) {
        Log.i(TAG, "onUnbind");
    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {
        Log.i(TAG, "onSetTags");
    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {
        Log.i(TAG, "onDelTags");
    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {
        Log.i(TAG, "onListTags");
    }

    @Override
    public void onMessage(Context context, String s, String s1) {
        Log.i(TAG, "onMessage:" + s + "  ---  " + s1);
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        Log.i(TAG, "onNotificationClicked");
        updateContent(context);
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {
        Log.i(TAG, "onNotificationArrived");
    }

    private void updateContent(Context context) {
        Log.d(TAG, "updateContent");
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }

}
