package pm.poomoo.autospareparts.view.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import m.framework.utils.Utils;
import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseFragmentActivity;
import pm.poomoo.autospareparts.util.BottomBar;
import pm.poomoo.autospareparts.util.MyUtil;
import pm.poomoo.autospareparts.view.activity.search.SearchCompanyActivity;
import pm.poomoo.autospareparts.view.activity.start.LogActivity;


/**
 * 程序主界面的框框
 *
 * @author ysy
 */
public class MainFragmentActivity extends PmBaseFragmentActivity {

    private final String TAG = MainFragmentActivity.class.getSimpleName();
    @ViewInject(R.id.fragment_relative_layout_one)
    private RelativeLayout mRelativeOne;//显示首页
    @ViewInject(R.id.fragment_relative_layout_two)
    private RelativeLayout mRelativeTwo;//显示客户资料
    @ViewInject(R.id.fragment_relative_layout_three)
    private RelativeLayout mRelativeThree;//显示分享
    @ViewInject(R.id.fragment_relative_layout_five)
    private RelativeLayout mRelativeFive;//显示更多
    @ViewInject(R.id.main_fragment_linear_search)
    private LinearLayout mLinearClickSearch;//搜索点击控件
    @ViewInject(R.id.fragment_relative_layout_search)
    private RelativeLayout mRelativeSearch;//搜索界面
    @ViewInject(R.id.fragment_relative_layout_linear_search)
    private LinearLayout mLinearSearch;//搜索控件
    @ViewInject(R.id.fragment_relative_layout_edt_search)
    private EditText mEdtSearch;//搜索输入框

    private long mTime = 0;//退出时用到的时间戳
    private int mNumberOfNowShowView = 0;//当前显示的界面是第几个
    private Resources resource;
    private final String pkgName = "pm.poomoo.autospareparts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        ViewUtils.inject(this);
        PmApplication.getInstance().getShared().putBoolean(IS_NEED_GUIDE, true);//关闭引导界面
        init();
        resource = getResources();
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                resource.getIdentifier("notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", pkgName),
                resource.getIdentifier("notification_title", "id", pkgName),
                resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(R.drawable.icon);
        // 推送高级设置，通知栏样式设置为下面的ID
        PushManager.setNotificationBuilder(this, 1, cBuilder);
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "GMfmiNDGy4QcZ12leQa7Tr8L");//GMfmiNDGy4QcZ12leQa7Tr8L
        showLog(TAG,"startWork END");
    }

    /**
     * 初始化
     */
    public void init() {
        showLog(TAG, "init");
        //加载“首页”
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction one = fragmentManager.beginTransaction();
//        FragmentTransaction one = getSupportFragmentManager().beginTransaction();
        one.replace(R.id.fragment_relative_layout_one, new FragmentOne());
        one.setTransition(FragmentTransaction.TRANSIT_NONE);
        one.commit();
        showLog(TAG, "1");
        //加载“消息中心”
        FragmentTransaction two = fragmentManager.beginTransaction();
//        FragmentTransaction two = getSupportFragmentManager().beginTransaction();
        two.replace(R.id.fragment_relative_layout_two, new FragmentTwo());
        two.setTransition(FragmentTransaction.TRANSIT_NONE);
        two.commit();
        showLog(TAG, "2");
        //加载“供求发布”
        FragmentTransaction three = fragmentManager.beginTransaction();
//        FragmentTransaction three = getSupportFragmentManager().beginTransaction();
        three.replace(R.id.fragment_relative_layout_three, new FragmentThree());
        three.setTransition(FragmentTransaction.TRANSIT_NONE);
        three.commit();
        showLog(TAG, "3");
        //加载“更多”
        FragmentTransaction four = fragmentManager.beginTransaction();
//        FragmentTransaction four = getSupportFragmentManager().beginTransaction();
        four.replace(R.id.fragment_relative_layout_five, new FragmentFour());
        four.setTransition(FragmentTransaction.TRANSIT_NONE);
        four.commit();
        showLog(TAG, "4");

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnItemChangedListener(new BottomBar.OnItemChangedListener() {
            @Override
            public void onItemChanged(int index) {
                //如何搜索界面显示的画就影藏
                if (mRelativeSearch.getVisibility() == View.VISIBLE) {
                    mRelativeSearch.setVisibility(View.GONE);
                }
                if (mNumberOfNowShowView != index) {
                    if (index == 0) {
                        mRelativeOne.setVisibility(View.VISIBLE);
                        mRelativeTwo.setVisibility(View.GONE);
                        mRelativeThree.setVisibility(View.GONE);
                        mRelativeFive.setVisibility(View.GONE);
                        mNumberOfNowShowView = index;
                    } else if (index == 1) {
                        if (!MyUtil.isLogin()) {
                            showToast("请登录");
                            startActivity(new Intent(MainFragmentActivity.this, LogActivity.class));
                        }
                        mRelativeOne.setVisibility(View.GONE);
                        mRelativeTwo.setVisibility(View.VISIBLE);
                        mRelativeThree.setVisibility(View.GONE);
                        mRelativeFive.setVisibility(View.GONE);
                        mNumberOfNowShowView = index;
                    } else if (index == 3) {
                        if (!MyUtil.isLogin()) {
                            showToast("请登录");
                            startActivity(new Intent(MainFragmentActivity.this, LogActivity.class));
                        }
                        mRelativeOne.setVisibility(View.GONE);
                        mRelativeTwo.setVisibility(View.GONE);
                        mRelativeThree.setVisibility(View.VISIBLE);
                        mRelativeFive.setVisibility(View.GONE);
                        mNumberOfNowShowView = index;
                    } else {
                        mRelativeOne.setVisibility(View.GONE);
                        mRelativeTwo.setVisibility(View.GONE);
                        mRelativeThree.setVisibility(View.GONE);
                        mRelativeFive.setVisibility(View.VISIBLE);
                        mNumberOfNowShowView = index;
                    }
                }
            }
        });

        //软键盘上的搜索按钮点击
        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    String content = mEdtSearch.getText().toString().trim();
                    if (!content.equals("")) {
                        mEdtSearch.setText("");
                        Intent intent = new Intent(MainFragmentActivity.this, SearchCompanyActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isTrue", false);
                        bundle.putInt("typeId", 18);
                        bundle.putString("content", content);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        getActivityInFromRight();
                        mRelativeSearch.setVisibility(View.GONE);
                    } else showToast("查询的内容不能为空");
                }
                return false;
            }
        });

        //点击空白地方隐藏掉搜索框
        mRelativeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果搜索界面显示的话就影藏
                if (mRelativeSearch.getVisibility() == View.VISIBLE) {
                    mRelativeSearch.setVisibility(View.GONE);
                }
                //隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        //点击搜索按钮
        mLinearClickSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果搜索界面影藏的就显示
                if (mRelativeSearch.getVisibility() == View.GONE) {
                    mRelativeSearch.setVisibility(View.VISIBLE);
                    mLinearSearch.clearAnimation();
                    Animation animation = AnimationUtils.loadAnimation(MainFragmentActivity.this, R.anim.scale_search);
                    mLinearSearch.startAnimation(animation);
                }
            }
        });
    }

    /**
     * 监听键盘按键点击事件
     *
     * @param keyCode 按键代码
     * @param event   按键事件
     * @return 返回真假
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //如果搜索控件显示就影藏搜索控件
            if (mRelativeSearch.getVisibility() == View.VISIBLE) {
                mRelativeSearch.setVisibility(View.GONE);
                return true;
            }
            long time = System.currentTimeMillis();
            if (time - mTime <= 2000) {
                finish();
                return true;
            }
            showToast("再按一次退出");
            mTime = time;
        }
        return true;
    }
}
