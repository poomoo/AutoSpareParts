package pm.poomoo.autospareparts.view.activity.more;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
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
import pm.poomoo.autospareparts.mode.CompanyInfo;
import pm.poomoo.autospareparts.util.RefreshableView;
import pm.poomoo.autospareparts.view.activity.client.ClientInformationActivity;
import pm.poomoo.autospareparts.view.activity.company.CompanyInformationActivity;

/**
 * 我的收藏
 */
public class MyCollectActivity extends PmBaseActivity {

    private final String TAG = MyCollectActivity.class.getSimpleName();
    @ViewInject(R.id.my_collect_one)
    private TextView mTxtOne;
    @ViewInject(R.id.my_collect_two)
    private TextView mTxtTwo;
    @ViewInject(R.id.my_collect_refreshable)
    private RefreshableView refreshableView;
    @ViewInject(R.id.my_collect_list)
    private ListView mListView;

    private List<CompanyInfo> companyInfoList = new ArrayList<>();//公司
    private List<ClientInfo> clientInfos = new ArrayList<>();//客户
    private List<CompanyInfo> showList = new ArrayList<>();//显示的数据源
    private int mIndexCompany = 0;//分页标记
    private int mIndexVip = 0;//分页标记
    private boolean isShowCompanyList = true;//true显示公司，false显示客户
    private myAdapter adapter;
    private myAdapter2 adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        clearAllActivity();
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
        setOnClickListener();
        showLog(TAG, "我的收藏");
    }

    /**
     * 初始化
     */
    public void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("我的收藏");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });

        mIndexCompany = 0;
        adapter = new myAdapter(this);
        adapter2 = new myAdapter2(this);
        mListView.setAdapter(adapter);
        onGetCollectCompanyList(false);//获取收藏公司列表
    }

    /**
     * 设置监听
     */
    public void setOnClickListener() {
        //列表点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PmApplication.getInstance().getShowCompanyInfos().clear();
                if (isShowCompanyList) {
                    PmApplication.getInstance().getShowCompanyInfos().add(companyInfoList.get(position));
                    startActivity(new Intent(MyCollectActivity.this, CompanyInformationActivity.class));
                } else {
                    PmApplication.getInstance().getShowClientInfo().add(clientInfos.get(position));
                    startActivity(new Intent(MyCollectActivity.this, ClientInformationActivity.class));
                }
                getActivityInFromRight();
            }
        });
        mTxtOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShowCompanyList = true;
                mTxtOne.setBackgroundColor(Color.parseColor("#ffb5b5b5"));
                mTxtTwo.setBackgroundColor(Color.parseColor("#00000000"));
                mListView.setAdapter(adapter);
                //TODO 替换显示的数据结构
                if (companyInfoList.size() == 0) {
                    onGetCollectCompanyList(false);
                }
            }
        });

        mTxtTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShowCompanyList = false;
                mTxtOne.setBackgroundColor(Color.parseColor("#00000000"));
                mTxtTwo.setBackgroundColor(Color.parseColor("#ffb5b5b5"));
                mListView.setAdapter(adapter2);
                //TODO 替换显示的数据结构
                if (clientInfos.size() == 0) {
                    onGetCollectVipList(false);
                }
            }
        });

        //下拉刷新
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                if (isShowCompanyList) {
                    mIndexCompany = 0;
                    onGetCollectCompanyList(true);//获取收藏公司列表
                } else {
                    mIndexVip = 0;
                    onGetCollectVipList(true);//获取收藏的客户列表
                }
            }
        }, 0);
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
            return companyInfoList.size();
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
            holderView.name.setText(companyInfoList.get(position).getName());
            holderView.explain.setText(companyInfoList.get(position).getExplain());

            final int number = position;
            holderView.linearCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //调用拨打电话界面
                    choicePhoneNumberCall(true, number);
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

    public class myAdapter2 extends BaseAdapter {

        private LayoutInflater inflater;

        public myAdapter2(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return clientInfos.size();
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
            holderView.name.setText(clientInfos.get(position).getName());
            holderView.explain.setText(clientInfos.get(position).getExplain());

            final int number = position;
            holderView.linearCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //调用拨打电话界面
                    choicePhoneNumberCall(false, number);
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
     * 弹出选择拨打电话界面
     */
    public void choicePhoneNumberCall(boolean isShowCompanyList, final int number) {
        final Dialog dialog = new Dialog(MyCollectActivity.this, R.style.no_frame_dialog);
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

        //        showList.get(number).getSpecialNumber();
        final String[] phone;
        if (isShowCompanyList)
            phone = companyInfoList.get(number).getCellPhone().split("[@]");
        else
            phone = clientInfos.get(number).getCellPhone().split("[@]");
        listView.setAdapter(new ArrayAdapter<>(MyCollectActivity.this, android.R.layout.simple_list_item_1, phone));

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
     * 获取收藏的公司列表
     */
    public void onGetCollectCompanyList(final boolean isRefreshable) {
        if (PmApplication.getInstance().getShared().getInt(USER_ID) == 0) {
            if (isRefreshable) {
                refreshableView.finishRefreshing();
            }
            showToast("你还没有登陆，请先登录");
            return;
        }
        RequestParams params = new RequestParams();
        params.addBodyParameter(KEY_PACKNAME, "1011");
        params.addBodyParameter("id", PmApplication.getInstance().getShared().getInt(USER_ID) + "");
        params.addBodyParameter("index", mIndexCompany + "");
        params.addBodyParameter("type", "1");
        showLog(TAG, "UserId:" + PmApplication.getInstance().getShared().getInt(USER_ID) + " mIndexCompany:" + mIndexCompany);

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);

                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (isRefreshable) {
                                refreshableView.finishRefreshing();
                                companyInfoList.clear();
                            }
                            JSONArray array = result.getJSONArray(KEY_LIST);
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    companyInfoList.add(new CompanyInfo(object.getInt("id"), object.getInt("type"), object.getString("name"), object.getString("description"),
                                            object.getString("pic"), object.getString("cellphone"), object.getString("landline"), object.getString("fax"), object.getString("qq"),
                                            object.getString("email"), object.getString("address"), object.getLong("time"), object.getString("people"), object.getString("wechat")));
                                }
                                mIndexCompany++;
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
                if (isRefreshable) {
                    refreshableView.finishRefreshing();
                }
            }
        });
    }

    /**
     * 获取收藏的客户列表
     */
    public void onGetCollectVipList(final boolean isRefreshable) {
        if (PmApplication.getInstance().getShared().getInt(USER_ID) == 0) {
            if (isRefreshable) {
                refreshableView.finishRefreshing();
            }
            showToast("你还没有登陆，请先登录");
            return;
        }
        RequestParams params = new RequestParams();
        params.addBodyParameter(KEY_PACKNAME, "1012");
        params.addBodyParameter("vip_id", PmApplication.getInstance().getShared().getInt(USER_ID) + "");
        params.addBodyParameter("index", mIndexVip + "");
        params.addBodyParameter("type", "2");

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);

                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (isRefreshable) {
                                refreshableView.finishRefreshing();
                                clientInfos.clear();
                            }
                            JSONArray array = result.getJSONArray(KEY_LIST);
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    clientInfos.add(new ClientInfo(object.getInt("id"), object.getInt("type"), object.getString("name"), object.getString("description"),
                                            object.getString("pic"), object.getString("cellphone"), object.getString("landline"), object.getString("fax"), object.getString("qq"),
                                            object.getString("email"), object.getString("address"), object.getLong("time"), object.getString("people"), object.getString("wechat")));
                                }
                                mIndexVip++;
                                adapter2.notifyDataSetChanged();
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
                if (isRefreshable) {
                    refreshableView.finishRefreshing();
                }
            }
        });
    }
}
