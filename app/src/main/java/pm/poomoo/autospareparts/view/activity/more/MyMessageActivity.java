package pm.poomoo.autospareparts.view.activity.more;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseActivity;
import pm.poomoo.autospareparts.mode.MessageInfo;
import pm.poomoo.autospareparts.util.RefreshableView;

/**
 * 消息中心
 */
public class MyMessageActivity extends PmBaseActivity {

    private final String TAG = MyMessageActivity.class.getSimpleName();
    @ViewInject(R.id.message_refreshable)
    private RefreshableView refreshableView;
    @ViewInject(R.id.message_list)
    private ListView mListView;

    private myAdapter adapter;
    private boolean isShowDelete = false;//列表是否显示删除按钮
    private int mIndex = 0;//分页标记
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();//消息列表

    private boolean mIsAddMore = false;//是否加载更多
    private long mCurrentTimeMillis = 0;//时间戳

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        clearAllActivity();
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
        setOnClickListener();
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
//        headerViewHolder.rightButton.setVisibility(View.VISIBLE);
//        headerViewHolder.rightButton.setText("编辑");
//        headerViewHolder.rightButton.setPadding(10, 2, 10, 2);
//        headerViewHolder.rightButton.setBackgroundResource(R.drawable.head_right_background);
//        headerViewHolder.rightButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isShowDelete) {
//                    headerViewHolder.rightButton.setText("编辑");
//                    isShowDelete = false;
//                } else {
//                    headerViewHolder.rightButton.setText("完成");
//                    isShowDelete = true;
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });

        adapter = new myAdapter(this);
        mListView.setAdapter(adapter);

        messageInfos.clear();
        onGetMessageList(false);
    }

    /**
     * 设置监听
     */
    public void setOnClickListener() {
        //下拉刷新
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                mIndex = 0;
                onGetMessageList(true);
            }
        }, 0);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyMessageActivity.this, MyMessageInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", messageInfos.get(i).getTitle());
                bundle.putString("content", messageInfos.get(i).getContent());
                intent.putExtras(bundle);
                startActivity(intent);
                getActivityInFromRight();
            }
        });
    }

    /**
     * 适配器
     */
    public class myAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public myAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return messageInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HolderView holderView;
            if (convertView == null) {
                holderView = new HolderView();
                convertView = inflater.inflate(R.layout.item_for_message_list, null);
                holderView.name = (TextView) convertView.findViewById(R.id.item_for_message_name);
                holderView.explain = (TextView) convertView.findViewById(R.id.item_for_message_explain);
                holderView.time = (TextView) convertView.findViewById(R.id.item_for_message_time);
                holderView.delete = (ImageView) convertView.findViewById(R.id.item_for_message_delete);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            if (isShowDelete) {
                holderView.delete.setVisibility(View.VISIBLE);
            } else {
                holderView.delete.setVisibility(View.GONE);
            }
            holderView.name.setText(messageInfos.get(position).getTitle());
            holderView.explain.setText(messageInfos.get(position).getContent());
            holderView.time.setText(format.format(new Date(messageInfos.get(position).getTime() * 1000)));
            final int number = position;
            //删除消息
            holderView.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return convertView;
        }

        class HolderView {
            public TextView name;
            public TextView explain;
            public TextView time;
            public ImageView delete;
        }
    }

    /**
     * 获取消息列表
     */
    public void onGetMessageList(final boolean isRefresh) {
        RequestParams params = new RequestParams();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_PACKNAME, 1013);
            jsonObject.put("index", mIndex);
            params.addBodyParameter(KEY, jsonObject.toString());
            showLog(TAG, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (isRefresh) {
                                refreshableView.finishRefreshing();
                                messageInfos.clear();
                            }
                            JSONArray arrayList = result.getJSONArray("list");
                            if (arrayList.length() > 0) {
                                for (int i = 0; i < arrayList.length(); i++) {
                                    JSONObject resultList = new JSONObject(arrayList.get(i).toString());
                                    messageInfos.add(new MessageInfo(resultList.getInt("id"), "", resultList.getString("title"),
                                            resultList.getString("content"), resultList.getLong("time")));
                                }
                            } else showToast("没有数据可显示");
                            mIndex++;
                            adapter.notifyDataSetChanged();
                            break;
                        case RET_FAIL:
                            if (isRefresh) {
                                refreshableView.finishRefreshing();
                            }
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                if (isRefresh) {
                    refreshableView.finishRefreshing();
                }
            }
        });
    }
}
