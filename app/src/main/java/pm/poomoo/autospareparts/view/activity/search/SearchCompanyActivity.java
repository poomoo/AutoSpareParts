package pm.poomoo.autospareparts.view.activity.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import pm.poomoo.autospareparts.mode.CompanyInfo;
import pm.poomoo.autospareparts.util.RefreshableView;
import pm.poomoo.autospareparts.view.activity.company.CompanyInformationActivity;

/**
 * 查找商户
 */
public class SearchCompanyActivity extends PmBaseActivity {

    private final String TAG = SearchCompanyActivity.class.getSimpleName();
    @ViewInject(R.id.search_company_list_refreshable)
    private RefreshableView refreshableView;
    @ViewInject(R.id.search_company_list_view)
    private ListView mListView;

    private boolean mIsComplete = true;//上次发送的获取数据包是否完成
    private boolean mIsAddMore = false;//是否加载更多
    private int mIndex = 0;//分页标记
    private int mTypeId = -1;//类型id
    private String mSearchCompanyName = "";//查询的字符串
    private boolean mIsTrue = true;//true根据类型搜索   false搜索配件
    private List<CompanyInfo> companyList = new ArrayList<CompanyInfo>();//公司
    private myAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_company);
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
        headerViewHolder.title.setText("搜索商户");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });
        //获取上个界面传过来的类型ID和查询的关键字
        mTypeId = getIntent().getExtras().getInt("typeId");
        mSearchCompanyName = getIntent().getExtras().getString("content");
        mIsTrue = getIntent().getExtras().getBoolean("isTrue");
        if (mIsTrue) {
            onSearchCompanyList(false);
        } else {
            getCompanyList(false);
        }

        companyList.clear();
        adapter = new myAdapter(SearchCompanyActivity.this);
        mListView.setAdapter(adapter);
    }

    /**
     * 设置监听
     */
    public void setOnClickListener() {
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                mIndex = 0;
                if (mIsTrue) {
                    onSearchCompanyList(true);
                } else {
                    getCompanyList(true);
                }
            }
        }, 0);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PmApplication.getInstance().getShowCompanyInfos().clear();
                PmApplication.getInstance().getShowCompanyInfos().add(companyList.get(i));
                startActivity(new Intent(SearchCompanyActivity.this, CompanyInformationActivity.class));
                getActivityInFromRight();
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mIsAddMore && scrollState == 0 && mIsComplete) {
                    if (mIsTrue) {
                        onSearchCompanyList(false);
                    } else {
                        getCompanyList(false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mIsAddMore = (firstVisibleItem + visibleItemCount == totalItemCount);
            }
        });
    }

    /**
     * 从搜索公司进来
     */
    public void onSearchCompanyList(final boolean isRefreshable) {
        mIsComplete = false;
        RequestParams params = new RequestParams();
//        try {
//            JSONObject jsonObject = new JSONObject();
//            params.addBodyParameter(KEY_PACKNAME, 1004);
//            params.addBodyParameter("type_id", mTypeId);
//            params.addBodyParameter("company_name", mSearchCompanyName);
//            params.addBodyParameter("index", mIndex);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        params.addBodyParameter(KEY_PACKNAME, 1004 + "");
        params.addBodyParameter("type_id", mTypeId == -1 ? "" : (mTypeId + ""));
        params.addBodyParameter("company_name", mSearchCompanyName);
        params.addBodyParameter("index", mIndex + "");

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                mIsComplete = true;
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (isRefreshable) {
                                refreshableView.finishRefreshing();
                                companyList.clear();
                            }
                            JSONArray array = result.getJSONArray("list");
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    int typeId = 0;
                                    if ((object.get("type_id").toString()).equals("")) {
                                        typeId = Integer.parseInt(object.get("type_id").toString());
                                    }
                                    companyList.add(new CompanyInfo(object.getInt("id"), typeId, object.getString("name"), object.getString("description"),
                                            object.getString("pic"), object.getString("cellphone"), object.getString("landline"), object.getString("fax"), object.getString("qq"),
                                            object.getString("email"), object.getString("address"), object.getLong("time"), object.getString("people"), object.getString("wechat")));
                                }
                                mIndex++;
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case RET_FAIL:
                            if (isRefreshable) {
                                refreshableView.finishRefreshing();
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mIsComplete = true;
                if (isRefreshable) {
                    refreshableView.finishRefreshing();
                }
            }
        });
    }


    /**
     * 从搜索配件进来
     */
    public void getCompanyList(final boolean isRefreshable) {
        mIsComplete = false;
        RequestParams params = new RequestParams();
//        final JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put(KEY_PACKNAME, 1023);
//            jsonObject.put("name", mSearchCompanyName);
//            jsonObject.put("type_id", 18);
//            jsonObject.put("index", mIndex);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        params.addBodyParameter(KEY_PACKNAME, 1023 + "");
        params.addBodyParameter("name", mSearchCompanyName);
        params.addBodyParameter("type_id", 18 + "");
        params.addBodyParameter("index", mIndex + "");

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mIsComplete = true;
                try {
                    showLog(TAG, responseInfo.result);
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (isRefreshable) {
                                refreshableView.finishRefreshing();
                                companyList.clear();
                            }
                            JSONArray array = result.getJSONArray(KEY_LIST);
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    companyList.add(new CompanyInfo(object.getInt("id"), object.getInt("type_id"), object.getString("name"), object.getString("description"),
                                            object.getString("pic"), object.getString("cellphone"), object.getString("landline"), object.getString("fax"), object.getString("qq"),
                                            object.getString("email"), object.getString("address"), object.getLong("time"), object.getString("people"), object.getString("wechat")));
                                }
                                mIndex++;
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case RET_FAIL:
                            if (isRefreshable) {
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
                mIsComplete = true;
                if (isRefreshable) {
                    refreshableView.finishRefreshing();
                }
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
            return companyList.size();
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
                convertView = inflater.inflate(R.layout.item_for_company_list, null);
                holderView.name = (TextView) convertView.findViewById(R.id.item_for_company_list_name);
                holderView.explain = (TextView) convertView.findViewById(R.id.item_for_company_list_explain);
                holderView.linearCall = (LinearLayout) convertView.findViewById(R.id.item_for_company_list_call);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            holderView.name.setText(companyList.get(position).getName());
            holderView.explain.setText(companyList.get(position).getExplain());

            final int number = position;
            holderView.linearCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //调用拨打电话界面
                    choicePhoneNumberCall(number);
                }
            });
            return convertView;
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
        final Dialog dialog = new Dialog(SearchCompanyActivity.this, R.style.no_frame_dialog);
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

        //        companyList.get(number).getSpecialNumber();
        final String[] phone = companyList.get(number).getCellPhone().split("[@]");
        listView.setAdapter(new ArrayAdapter<String>(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, phone));

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

}
