package pm.poomoo.autospareparts.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
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
import java.util.Date;
import java.util.List;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseFragment;
import pm.poomoo.autospareparts.mode.MessageInfo;
import pm.poomoo.autospareparts.mode.SupplyInfo;
import pm.poomoo.autospareparts.util.RefreshableView;
import pm.poomoo.autospareparts.view.activity.more.MyMessageInfoActivity;
import pm.poomoo.autospareparts.view.activity.start.SupplyInformationActivity;


/**
 * 系统消息
 */
public class SupplyListFragment extends PmBaseFragment implements AdapterView.OnItemClickListener {

    private final String TAG = SupplyListFragment.class.getSimpleName();
    @ViewInject(R.id.fragment_supply_list_refreshable)
    private RefreshableView refreshableView;
    @ViewInject(R.id.fragment_supply_list_listView)
    private ListView mListView;

    private int index = 0;//分页标记
    private ListViewAdapter adapter = null;
    private Gson gson = new Gson();
    private List<SupplyInfo> list_supply = new ArrayList<>();//供求列表
    private String[] Urls = {};
    private BitmapUtils bitmapUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.fragment_supply_list, container, false);
        ViewUtils.inject(this, layout);
        init();
        return layout;
    }

    /**
     * 初始化
     */
    public void init() {
        getSupplyInformation(false);//获取供求列表
        adapter = new ListViewAdapter(getActivity());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        setOnClickListener();
    }

    /**
     * 设置监听
     */
    public void setOnClickListener() {
        //下拉刷新
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                index = 0;
                getSupplyInformation(true);//刷新供求列表
            }
        }, 0);
    }

    /**
     * 获取供求列表
     */
    public void getSupplyInformation(final boolean isRefreshable) {
        RequestParams params = new RequestParams();

        params.addBodyParameter(KEY_PACKNAME, "1025");
        params.addBodyParameter("index", Integer.toString(index));

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    showLog(TAG, "供求列表返回:" + responseInfo.result);
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (isRefreshable)
                                refreshableView.finishRefreshing();

                            JSONArray array = result.getJSONArray(KEY_LIST);
                            int len = array.length();
                            if (len > 0) {
                                for (int i = 0; i < len; i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    SupplyInfo supplyInfo;
                                    supplyInfo = gson.fromJson(object.toString(), SupplyInfo.class);
                                    list_supply.add(supplyInfo);
                                }
                                index++;
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case RET_FAIL:
                            if (isRefreshable)
                                refreshableView.finishRefreshing();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i(TAG, "onItemClick");
        Intent intent = new Intent(getActivity(), SupplyInformationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("SupplyInfo", list_supply.get(i));
        intent.putExtras(bundle);
        startActivity(intent);
        getActivityInFromRight();
    }

    /**
     * 适配器
     */
    public class ListViewAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ListViewAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list_supply.size();
        }

        @Override
        public Object getItem(int position) {
            return list_supply.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("getview", position + "");
            HolderView holderView;
            if (convertView == null) {
                holderView = new HolderView();
                convertView = inflater.inflate(R.layout.item_for_supply, null);
                holderView.name = (TextView) convertView.findViewById(R.id.item_for_supply_name);
                holderView.dateTime = (TextView) convertView.findViewById(R.id.item_for_supply_dateTime);
                holderView.content = (TextView) convertView.findViewById(R.id.item_for_supply_content);
                holderView.gridView = (GridView) convertView.findViewById(R.id.item_for_supply_gridview);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }

            //屏蔽掉gridview的点击事件，保持listview的点击事件
            holderView.gridView.setClickable(false);
            holderView.gridView.setPressed(false);
            holderView.gridView.setEnabled(false);

            holderView.name.setText(list_supply.get(position).getContact());
            holderView.dateTime.setText(list_supply.get(position).getDateTime());
            holderView.content.setText(list_supply.get(position).getContent());

            Urls = list_supply.get(position).getUrls().split(",");
            holderView.gridView.setAdapter(new GridViewAdapter(getActivity(), Urls));

            return convertView;
        }

        class HolderView {
            public TextView name;
            public TextView dateTime;
            public TextView content;
            public GridView gridView;
        }
    }

    /**
     * 适配器
     */
    public class GridViewAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private String[] Urls;

        public GridViewAdapter(Context context, String[] Urls) {
            inflater = LayoutInflater.from(context);
            this.Urls = Urls;

            if (bitmapUtils == null) {
                bitmapUtils = new BitmapUtils(getActivity());
                bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);
            }
        }

        @Override
        public int getCount() {
            return Urls == null ? 0 : Urls.length;
        }

        @Override
        public Object getItem(int position) {
            return Urls[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HolderView holderView;
            if (convertView == null) {
                holderView = new HolderView();
                convertView = inflater.inflate(R.layout.item_for_supply_gridview, null);
                holderView.imageView = (ImageView) convertView.findViewById(R.id.item_for_supply_imageView);

                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            bitmapUtils.display(holderView.imageView, Urls[position]);
            return convertView;
        }

        class HolderView {
            public ImageView imageView;
        }
    }

}
