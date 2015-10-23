package pm.poomoo.autospareparts.view.activity.more.collect;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseFragmentActivity;

public class CollectFragmentActivity extends PmBaseFragmentActivity {

    private final String TAG = CollectFragmentActivity.class.getSimpleName();
    @ViewInject(R.id.collect_fragment_activity_relative_one)
    private RelativeLayout mRelativeOne;
    @ViewInject(R.id.collect_fragment_activity_relative_two)
    private RelativeLayout mRelativeTwo;
    @ViewInject(R.id.collect_fragment_activity_one)
    private TextView mTxtOne;
    @ViewInject(R.id.collect_fragment_activity_two)
    private TextView mTxtTwo;

    private HeaderViewHolder headerViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_fragment);
        ViewUtils.inject(this);
        onInit();
    }

    public void onInit() {

        headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("我的收藏");

        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.leftButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FragmentTransaction one = getSupportFragmentManager().beginTransaction();
        one.replace(R.id.collect_fragment_activity_relative_one, new FragmentCompany());
        one.setTransition(FragmentTransaction.TRANSIT_NONE);
        one.commit();

        FragmentTransaction two = getSupportFragmentManager().beginTransaction();
        two.replace(R.id.collect_fragment_activity_relative_two, new FragmentClient());
        two.setTransition(FragmentTransaction.TRANSIT_NONE);
        two.commit();
    }

    /**
     * 设置监听
     *
     * @param view
     */
    @OnClick({R.id.collect_fragment_activity_one, R.id.collect_fragment_activity_two})
    public void onSetClickListener(View view) {
        switch (view.getId()) {
            case R.id.collect_fragment_activity_one:
                //选择公司
                mRelativeOne.setVisibility(View.VISIBLE);
                mRelativeTwo.setVisibility(View.GONE);
                mTxtOne.setBackgroundResource(R.drawable.table_choice_collect_down);
                mTxtTwo.setBackgroundColor(Color.parseColor("#00000000"));
                break;
            case R.id.collect_fragment_activity_two:
                //选择客户
                mRelativeOne.setVisibility(View.GONE);
                mRelativeTwo.setVisibility(View.VISIBLE);
                mTxtOne.setBackgroundColor(Color.parseColor("#00000000"));
                mTxtTwo.setBackgroundResource(R.drawable.table_choice_collect_down);
                break;
        }
    }
}
