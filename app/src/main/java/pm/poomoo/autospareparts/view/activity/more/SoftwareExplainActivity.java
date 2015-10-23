package pm.poomoo.autospareparts.view.activity.more;

import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseActivity;

/**
 * 软件说明
 */
public class SoftwareExplainActivity extends PmBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_explain);
        clearAllActivity();
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("软件说明");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });
    }

    /**
     * 设置监听
     */
    public void setOnClickListener() {
    }


}
