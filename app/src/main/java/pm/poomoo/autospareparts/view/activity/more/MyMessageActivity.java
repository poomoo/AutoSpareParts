package pm.poomoo.autospareparts.view.activity.more;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseActivity;
import pm.poomoo.autospareparts.view.fragment.SystemMessageFragment;

/**
 * 消息中心
 */
public class MyMessageActivity extends PmBaseActivity {

    private final String TAG = MyMessageActivity.class.getSimpleName();

    @ViewInject(R.id.activity_message_center_radiobutton_system_message)
    private RadioButton radioButton_system_message;
    @ViewInject(R.id.activity_message_center_radiobutton_my_supply)
    private RadioButton radioButton_my_supply;
    @ViewInject(R.id.activity_message_center_radiobutton_my_reply)
    private RadioButton radioButton_my_reply;

    private SystemMessageFragment fragment_system_message;
    private Fragment fragment_my_supply;
    private Fragment fragment_my_reply;
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
    }

    private void setDefaultFragment() {
        // TODO 自动生成的方法存根
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment_system_message = new SystemMessageFragment();
        fragmentTransaction.add(R.id.activity_message_center_frameLayout,
                curFragment);
//        curFragment = (Fragment) fragment_system_message;
        fragmentTransaction.commit();
    }

    /**
     * 设置监听
     */
    @OnClick({R.id.activity_message_center_radiobutton_system_message, R.id.activity_message_center_radiobutton_my_supply, R.id.activity_message_center_radiobutton_my_reply})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.activity_message_center_radiobutton_system_message:
                break;
            case R.id.activity_message_center_radiobutton_my_supply:

                break;
            case R.id.activity_message_center_radiobutton_my_reply:

                break;
        }
    }


}
