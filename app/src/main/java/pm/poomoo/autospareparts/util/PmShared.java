package pm.poomoo.autospareparts.util;

import android.app.Application;
import android.content.SharedPreferences;

import pm.poomoo.autospareparts.config.PmConfig;


/**
 * sharedPreferences管理类
 *
 * @author pm004
 */
public class PmShared {

    private SharedPreferences shared;

    public PmShared(Application aApplication) {
        if (aApplication != null) {
            shared = aApplication.getSharedPreferences(PmConfig.SHARED_NAME, Application.MODE_PRIVATE);
        }
    }

    public void clear() {
        shared.edit().clear().commit();
    }

    // ********************** put 方法集合********************************

    /**
     * 存String
     *
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        shared.edit().putString(key, value).commit();
    }

    /**
     * 存int
     *
     * @param key
     * @param value
     */
    public void putInt(String key, int value) {
        shared.edit().putInt(key, value).commit();
    }

    /**
     * 存flaot
     *
     * @param key
     * @param value
     */
    public void putFloat(String key, float value) {
        shared.edit().putFloat(key, value).commit();
    }

    /**
     * 存long
     *
     * @param key
     * @param value
     */
    public void putLong(String key, long value) {
        shared.edit().putLong(key, value).commit();
    }

    /**
     * 存boolean
     *
     * @param key
     * @param value
     */
    public void putBoolean(String key, boolean value) {
        shared.edit().putBoolean(key, value).commit();
    }

    // ************************** get 方法集合*********************************
    public String getString(String key, String defaultValue) {
        if (shared == null) {
            return null;
        }
        return shared.getString(key, defaultValue);
    }

    /**
     * 如果获取不到，则返回 ""
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        if (shared == null) {
            return null;
        }
        return shared.getString(key, "");
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (shared == null) {
            return false;
        }
        return shared.getBoolean(key, defaultValue);
    }

    /**
     * 如果获取不到，返回 false
     *
     * @param key
     * @return
     */
    public boolean getBoolean(String key) {
        if (shared == null) {
            return false;
        }
        return shared.getBoolean(key, false);
    }

    public float getFloat(String key, float defualtValue) {
        if (shared == null) {
            return 0;
        }
        return shared.getFloat(key, defualtValue);
    }

    /**
     * 如果获取不到，返回 0.0f
     *
     * @param key
     * @return
     */
    public float getFloat(String key) {
        if (shared == null) {
            return 0;
        }
        return shared.getFloat(key, 0.0f);
    }

    public int getInt(String key, int defualtValue) {
        if (shared == null) {
            return 0;
        }
        return shared.getInt(key, defualtValue);
    }

    /**
     * 如果获取不到，返回 0
     *
     * @param key
     * @return
     */
    public int getInt(String key) {
        if (shared == null) {
            return 0;
        }
        return shared.getInt(key, 0);
    }

    public long getLong(String key, long defualtValue) {
        if (shared == null) {
            return 0;
        }
        return shared.getLong(key, defualtValue);
    }

    /**
     * 如果获取不到，返回 0
     *
     * @param key
     * @return
     */
    public long getLong(String key) {
        if (shared == null) {
            return 0;
        }
        return shared.getLong(key, 0);
    }

}
