package pm.poomoo.autospareparts.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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

import java.util.ArrayList;
import java.util.List;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseFragment;
import pm.poomoo.autospareparts.mode.MessageInfo;
import pm.poomoo.autospareparts.util.RefreshableView;
import pm.poomoo.autospareparts.view.activity.more.MyMessageInfoActivity;


/**
 * 系统消息
 */
public class SystemMessageFragment extends PmBaseFragment {

    private final String TAG = SystemMessageFragment.class.getSimpleName();
    @ViewInject(R.id.fragment_system_message_refreshable)
    private RefreshableView refreshableView;
    @ViewInject(R.id.fragment_system_message_listView)
    private ListView mListView;

    private int mIndex = 1;//分页标记
    private myAdapter adapter;
    private List<MessageInfo> messageInfos = new ArrayList<>();//消息列表


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.fragment_system_message, container, false);
        ViewUtils.inject(this, layout);
        init();
        return layout;
    }

    /**
     * 初始化
     */
    public void init() {
        adapter = new myAdapter(getActivity());
        mListView.setAdapter(adapter);
        messageInfos.clear();
        setOnClickListener();
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
                Intent intent = new Intent(getActivity(), MyMessageInfoActivity.class);
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
            return 1;
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
                convertView = inflater.inflate(R.layout.item_for_system_message_list, null);
                holderView.title=(TextView)convertView.findViewById(R.id.item_for_system_message_textView_title);
                holderView.dateTime=(TextView)convertView.findViewById(R.id.item_for_system_message_textView_date);
                holderView.content=(TextView)convertView.findViewById(R.id.item_for_system_message_textView_content);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }

            return convertView;
        }

        class HolderView {
            public TextView title;
            public TextView dateTime;
            public TextView content;
        }
    }

    /**
     * 获取消息列表
     */
    public void onGetMessageList(final boolean isRefresh) {
        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(KEY_PACKNAME, 1013);
//            jsonObject.put("index", mIndex);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        params.addBodyParameter(KEY_PACKNAME, "1013");
        params.addBodyParameter("index",mIndex+"");

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
