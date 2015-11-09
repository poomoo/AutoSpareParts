package pm.poomoo.autospareparts.view.activity.more;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseActivity;
import pm.poomoo.autospareparts.view.fragment.MyReplyFragment;
import pm.poomoo.autospareparts.view.fragment.MySupplyFragment;
import pm.poomoo.autospareparts.view.fragment.SystemMessageFragment;

/**
 * 消息中心
 */
public class MyMessageActivity extends PmBaseActivity {

    private final String TAG = MyMessageActivity.class.getSimpleName();

    private SystemMessageFragment fragment_system_message;
    private MySupplyFragment fragment_my_supply;
    private MyReplyFragment fragment_my_reply;
    private Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        clearAllActivity();
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        final HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("消息中心");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });

        setDefaultFragment();
    }

    private void setDefaultFragment() {
        // TODO 自动生成的方法存根
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment_system_message = new SystemMessageFragment();
        curFragment = fragment_system_message;
        fragmentTransaction.add(R.id.activity_message_center_frameLayout,
                fragment_system_message);
        fragmentTransaction.commit();
    }

    /**
     * 设置监听
     */
    @OnClick({R.id.activity_message_center_radiobutton_system_message, R.id.activity_message_center_radiobutton_my_supply, R.id.activity_message_center_radiobutton_my_reply})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.activity_message_center_radiobutton_system_message:
                if (fragment_system_message == null)
                    fragment_system_message = new SystemMessageFragment();
                switchFragment(fragment_system_message);
                curFragment = fragment_system_message;
                break;
            case R.id.activity_message_center_radiobutton_my_supply:
                if (fragment_my_supply == null)
                    fragment_my_supply = new MySupplyFragment();
                switchFragment(fragment_my_supply);
                curFragment = fragment_my_supply;
                break;
            case R.id.activity_message_center_radiobutton_my_reply:
                if (fragment_my_reply == null)
                    fragment_my_reply = new MyReplyFragment();
                switchFragment(fragment_my_reply);
                curFragment = fragment_my_reply;
                break;
        }
    }

    private void switchFragment(Fragment to) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        if (!to.isAdded()) { // 先判断是否被add过
            fragmentTransaction.hide(curFragment).add(
                    R.id.activity_message_center_frameLayout, to); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            fragmentTransaction.hide(curFragment).show(to); // 隐藏当前的fragment，显示下一个
        }
        fragmentTransaction.commit();
    }

}
