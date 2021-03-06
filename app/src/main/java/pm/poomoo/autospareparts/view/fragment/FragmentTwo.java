package pm.poomoo.autospareparts.view.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseFragment;


/**
 * 消息中心
 *
 * @author AADC
 */
public class FragmentTwo extends PmBaseFragment {


    private SupplyListFragment fragment_supply_list;
    private MySupplyFragment fragment_my_supply;
    private MyReplyFragment fragment_my_reply;
    private Fragment curFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.activity_message_center, container, false);
        ViewUtils.inject(this, layout);
        init(layout);
        return layout;
    }

    public void init(View layout) {

        HeaderViewHolder headerViewHolder = getHeaderView(layout);
        headerViewHolder.title.setText("消息中心");
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        // TODO 自动生成的方法存根
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment_supply_list = new SupplyListFragment();
        curFragment = fragment_supply_list;
        fragmentTransaction.add(R.id.activity_message_center_frameLayout, fragment_supply_list);
        fragmentTransaction.commit();
    }

    /**
     * 设置监听
     */
    @OnClick({R.id.activity_message_center_radiobutton_system_message, R.id.activity_message_center_radiobutton_my_supply, R.id.activity_message_center_radiobutton_my_reply})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.activity_message_center_radiobutton_system_message:
                if (fragment_supply_list == null)
                    fragment_supply_list = new SupplyListFragment();
                switchFragment(fragment_supply_list);
                curFragment = fragment_supply_list;
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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!to.isAdded()) { // 先判断是否被add过
            fragmentTransaction.hide(curFragment).add(R.id.activity_message_center_frameLayout, to); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            fragmentTransaction.hide(curFragment).show(to); // 隐藏当前的fragment，显示下一个
        }
        fragmentTransaction.commit();
    }

}
