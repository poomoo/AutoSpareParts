package pm.poomoo.autospareparts.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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

import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseFragment;
import pm.poomoo.autospareparts.mode.SupplyInfo;
import pm.poomoo.autospareparts.util.DateUtil;
import pm.poomoo.autospareparts.util.RefreshableView;
import pm.poomoo.autospareparts.view.activity.start.SupplyInformationActivity;
import pm.poomoo.autospareparts.view.custom.MyListView;
import pm.poomoo.autospareparts.view.custom.bigimage.ImagePagerActivity;


/**
 * 系统消息
 */
public class MySupplyFragment extends PmBaseFragment implements AdapterView.OnItemClickListener {

    private final String TAG = MySupplyFragment.class.getSimpleName();
    @ViewInject(R.id.fragment_my_supply_refreshable)
    private RefreshableView refreshableView;
    @ViewInject(R.id.fragment_my_supply_listView)
    private MyListView mListView;

    private int index = 0;//分页标记
    private ListViewAdapter adapter = null;
    private Gson gson = new Gson();
    private List<SupplyInfo> list_supply = new ArrayList<>();//供求列表
    private String[] Urls;
    private BitmapUtils bitmapUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.fragment_my_supply, container, false);
        ViewUtils.inject(this, layout);
        init();
        return layout;
    }

    /**
     * 初始化
     */
    public void init() {
        getMySupplyInformation(false);//获取供求列表
        adapter = new ListViewAdapter(getActivity());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        mListView.setonRefreshListener(new MyListView.OnRefreshListener() {
            public void onRefresh() {
                getMySupplyInformation(false);
            }
        });
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
                getMySupplyInformation(true);//刷新供求列表
            }
        }, 0);

    }

    /**
     * 获取供求列表
     */
    public void getMySupplyInformation(final boolean isRefreshable) {
        RequestParams params = new RequestParams();

        params.addBodyParameter(KEY_PACKNAME, "1025");
        params.addBodyParameter("user_id", PmApplication.getInstance().getShared().getInt(USER_ID) + "");
        params.addBodyParameter("index", Integer.toString(index));

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    showLog(TAG, "供求列表返回:" + responseInfo.result);
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (isRefreshable) {
                                refreshableView.finishRefreshing();
                                list_supply = new ArrayList<>();
                            } else
                                mListView.onRefreshComplete();

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
            holderView.dateTime.setText(DateUtil.getDateWith10Time(list_supply.get(position).getTime()));
            holderView.content.setText(list_supply.get(position).getContent());

            Urls = list_supply.get(position).getPictures().split(",");
            if (Urls.length > 0 && !TextUtils.isEmpty(Urls[0])) {
                holderView.gridView.setVisibility(View.VISIBLE);
                holderView.gridView.setAdapter(new GridViewAdapter(getActivity(), Urls));
            } else {
                holderView.gridView.setVisibility(View.GONE);
            }
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
        private ArrayList<String> list = new ArrayList<>();

        public GridViewAdapter(Context context, String[] Urls) {
            inflater = LayoutInflater.from(context);
            this.Urls = Urls;
            for (int i = 0; i < Urls.length; i++)
                list.add(PIC_RUL + Urls[i].substring(2));


            if (bitmapUtils == null) {
                bitmapUtils = new BitmapUtils(getActivity());
                bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);

                bitmapUtils.configDiskCacheEnabled(true);
                bitmapUtils.configMemoryCacheEnabled(false);
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
            convertView = inflater.inflate(R.layout.item_for_supply_gridview, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.item_for_supply_imageView);

            bitmapUtils.display(imageView, PIC_RUL + Urls[position].substring(2));
            imageView.setOnClickListener(new imgClickListener(position, list));
            return convertView;
        }

        public class imgClickListener implements View.OnClickListener {
            int position;
            ArrayList<String> list = new ArrayList<>();

            public imgClickListener(int position, ArrayList<String> list) {
                this.position = position;
                this.list = list;
            }

            @Override
            public void onClick(View v) {
                imageBrowse(position, list);
            }
        }

        protected void imageBrowse(int position, ArrayList<String> urls2) {
            Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
            // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
            getActivity().startActivity(intent);
        }

    }
}
