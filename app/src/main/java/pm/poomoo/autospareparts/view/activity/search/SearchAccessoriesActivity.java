package pm.poomoo.autospareparts.view.activity.search;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseActivity;
import pm.poomoo.autospareparts.mode.ClientInfo;
import pm.poomoo.autospareparts.util.RefreshableView;
import pm.poomoo.autospareparts.view.activity.client.ClientInformationActivity;

/**
 * 查找客户
 */
public class SearchAccessoriesActivity extends PmBaseActivity {

    private final String TAG = SearchAccessoriesActivity.class.getSimpleName();
    @ViewInject(R.id.search_client_list_refreshable)
    private RefreshableView mRefreshableView;//刷新控件
    @ViewInject(R.id.search_client_list_view)
    private ListView mListView;//列表

    private String mSearchContent = "";//搜索关键字
    private int mIndex = 0;//分页标记
    private MyAdapter mAdapter;
    private List<ClientInfo> mClientInfos;
    private boolean mIsAddMore = false;
    private boolean mIsComplete = true;//是否加载完成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_accessories);
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
        setOnClickListener();
    }

    /**
     * 初始化
     */
    public void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("搜索客户");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });
        mSearchContent = getIntent().getExtras().getString("content");
        mClientInfos = new ArrayList<>();
        mClientInfos.clear();
        mAdapter = new MyAdapter(SearchAccessoriesActivity.this);
        mListView.setAdapter(mAdapter);
        onSearchClient(false);
    }

    /**
     * 设置监听
     */
    public void setOnClickListener() {
        //列表点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PmApplication.getInstance().getShowClientInfo().clear();
                PmApplication.getInstance().getShowClientInfo().add(mClientInfos.get(position));
                startActivity(new Intent(SearchAccessoriesActivity.this, ClientInformationActivity.class));
                getActivityInFromRight();
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mIsAddMore && scrollState == 0 && mIsComplete) {
                    onSearchClient(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mIsAddMore = (firstVisibleItem + visibleItemCount == totalItemCount);
            }
        });

        //刷新
        mRefreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                mIndex = 0;
                onSearchClient(true);
            }
        }, 0);
    }

    class MyAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public int getCount() {
            return mClientInfos.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            HolderView holderView;
            if (view == null) {
                holderView = new HolderView();
                view = inflater.inflate(R.layout.item_for_company_list, null);
                holderView.name = (TextView) view.findViewById(R.id.item_for_company_list_name);
                holderView.explain = (TextView) view.findViewById(R.id.item_for_company_list_explain);
                holderView.linearCall = (LinearLayout) view.findViewById(R.id.item_for_company_list_call);
                view.setTag(holderView);
            } else {
                holderView = (HolderView) view.getTag();
            }
            holderView.name.setText(mClientInfos.get(i).getName());
            holderView.explain.setText(mClientInfos.get(i).getExplain());

            final int number = i;
            holderView.linearCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //调用拨打电话界面
                    choicePhoneNumberCall(number);
                }
            });
            return view;
        }

        class HolderView {
            public TextView name;
            public TextView explain;
            public LinearLayout linearCall;
        }
    }

    /**
     * 弹出选择拨打电话界面
     */
    public void choicePhoneNumberCall(final int number) {
        final Dialog dialog = new Dialog(SearchAccessoriesActivity.this, R.style.no_frame_dialog);
        dialog.setContentView(R.layout.dlg_choice_get_pic);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = (int) PmApplication.getInstance().getScreenWidth() * 98 / 100;

        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.dlg_choice_linear);
        ListView listView = (ListView) dialog.findViewById(R.id.dlg_choice_list);
        final Button cancel = (Button) dialog.findViewById(R.id.dlg_choice_cancel);
        DialogAnimation(linearLayout, cancel);

        //        mClientInfos.get(number).getSpecialNumber();
        final String[] phone = mClientInfos.get(number).getCellPhone().split("[@]");
        listView.setAdapter(new ArrayAdapter<String>(SearchAccessoriesActivity.this, android.R.layout.simple_list_item_1, phone));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone[position]));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivityInFromRight();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 对话框动画
     */
    public void DialogAnimation(LinearLayout linearLayout, Button cancel) {
        TranslateAnimation cameraAnimation = new TranslateAnimation(0, 0, 300, 0);
        cameraAnimation.setDuration(300);
        linearLayout.setAnimation(cameraAnimation);
        TranslateAnimation cancelAnimation = new TranslateAnimation(0, 0, 100, 0);
        cancelAnimation.setDuration(300);
        cancel.setAnimation(cancelAnimation);
    }

    /**
     * 搜索客户资料
     */
    public void onSearchClient(final boolean isRefreshable) {
        mIsComplete = false;
        RequestParams params = new RequestParams();
        params.addBodyParameter(KEY_PACKNAME, 1008 + "");
        params.addBodyParameter("search_name", mSearchContent);
        params.addBodyParameter("index", mIndex + "");
        showLog(TAG, "search_name" + mSearchContent+" params"+params.toString());

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mIsComplete = true;
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (isRefreshable) {
                                mRefreshableView.finishRefreshing();
                                mClientInfos.clear();
                            }
                            JSONArray array = result.getJSONArray(KEY_LIST);
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    int typeId = 0;
                                    if (!(object.get("type_id").toString()).equals("")) {
                                        typeId = Integer.parseInt(object.get("type_id").toString());
                                    }
                                    mClientInfos.add(new ClientInfo(object.getInt("id"), typeId, object.getString("name"), object.getString("description"),
                                            object.getString("pic"), object.getString("cellphone"), object.getString("landline"), object.getString("fax"), object.getString("qq"),
                                            object.getString("email"), object.getString("address"), 0, object.getString("people"), object.getString("wechat")));
                                }
                                mIndex++;
                                mAdapter.notifyDataSetChanged();
                            }
                            break;
                        case RET_FAIL:
                            if (isRefreshable) {
                                mRefreshableView.finishRefreshing();
                            }
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mIsComplete = false;
                if (isRefreshable) {
                    mRefreshableView.finishRefreshing();
                }
            }
        });
    }

}
