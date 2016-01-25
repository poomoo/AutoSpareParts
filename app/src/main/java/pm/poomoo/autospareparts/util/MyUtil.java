/**
 * Copyright (c) 2016. 李苜菲 Inc. All rights reserved.
 */
package pm.poomoo.autospareparts.util;

import android.text.TextUtils;

import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.config.PmConfig;

/**
 * 作者: 李苜菲
 * 日期: 2016/1/22 17:07.
 */
public class MyUtil {
    /**
     * 是否登录
     *
     * @return
     */
    public static boolean isLogin() {
        if (TextUtils.isEmpty(PmApplication.getInstance().getShared().getString(PmConfig.TEL)))
            return false;
        return true;
    }
}
