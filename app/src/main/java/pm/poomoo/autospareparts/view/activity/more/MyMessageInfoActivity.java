package pm.poomoo.autospareparts.view.activity.more;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseActivity;

public class MyMessageInfoActivity extends PmBaseActivity {

    @ViewInject(R.id.my_message_info_title)
    private TextView mTxtTitle;
    @ViewInject(R.id.my_message_info_content)
    private TextView mTxtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message_info);
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        onInit();
    }

    /**
     * 初始化
     */
    public void onInit() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("消息详情");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackLastActivity();
            }
        });
        mTxtTitle.setText(getIntent().getExtras().getString("title"));
        mTxtContent.setText("\t\t" + getIntent().getExtras().getString("content"));
    }

    /**
     * 设置监听
     */
    public void onClickListener() {

    }


}
