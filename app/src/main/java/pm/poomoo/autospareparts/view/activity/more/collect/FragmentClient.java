package pm.poomoo.autospareparts.view.activity.more.collect;

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
import pm.poomoo.autospareparts.base.PmBaseFragment;
import pm.poomoo.autospareparts.mode.ClientInfo;
import pm.poomoo.autospareparts.util.RefreshableView;
import pm.poomoo.autospareparts.view.activity.client.ClientInformationActivity;

public class FragmentClient extends PmBaseFragment {

    private final String TAG = FragmentClient.class.getSimpleName();
    @ViewInject(R.id.fragment_client_refreshable)
    private RefreshableView mRefreshableView;
    @ViewInject(R.id.fragment_client_list)
    private ListView mListView;
    @ViewInject(R.id.fragment_client_linear)
    private LinearLayout mLinearlayout;

    private int mIndex = 1;//分页标记
    private myAdapter adapter;
    private boolean mIsAddMore = false;
    private boolean mIsComplete = true;//加载网络数据是否完成
    private List<ClientInfo> mClientInfoInfoList = new ArrayList<ClientInfo>();//客户

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.fragment_fragment_client, container, false);
        ViewUtils.inject(this, layout);
        onInit();
        return layout;
    }

    public void onInit() {

        //判断是不是VIP，如果不是VIP这个模块就不给用
        if (PmApplication.getInstance().getShared().getInt(IS_VIP) == 0) {
            mLinearlayout.setVisibility(View.VISIBLE);
            mLinearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return;
        }

        adapter = new myAdapter(getActivity());
        mListView.setAdapter(adapter);
        onGetCollectVipList(false);//获取收藏客户列表

        //下拉刷新
        mRefreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                mIndex = 1;
                onGetCollectVipList(true);
            }
        }, 0);

        //列表点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PmApplication.getInstance().getShowClientInfo().clear();
                PmApplication.getInstance().getShowClientInfo().add(mClientInfoInfoList.get(i));
                startActivity(new Intent(getActivity(), ClientInformationActivity.class));
                getActivityInFromRight();
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mIsAddMore && scrollState == 0 && mIsComplete) {
                    onGetCollectVipList(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mIsAddMore = (firstVisibleItem + visibleItemCount == totalItemCount);
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
            return mClientInfoInfoList.size();
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
            holderView.name.setText(mClientInfoInfoList.get(position).getName());
            holderView.explain.setText(mClientInfoInfoList.get(position).getExplain());

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
    public void choicePhoneNumberCall(final int number) {
        final Dialog dialog = new Dialog(getActivity(), R.style.no_frame_dialog);
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

        //        mClientInfoInfoList.get(number).getSpecialNumber();
        final String[] phone = mClientInfoInfoList.get(number).getCellPhone().split("[@]");
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, phone));

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
     * 获取收藏的客户列表
     */
    public void onGetCollectVipList(final boolean isRefreshable) {
        mIsComplete = false;
        if (PmApplication.getInstance().getShared().getInt(USER_ID) == 0) {
            if (isRefreshable) {
                mRefreshableView.finishRefreshing();
            }
            showToast("你还没有登陆，请先登录");
            return;
        }
        RequestParams params = new RequestParams();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_PACKNAME, 1012);
            jsonObject.put("vip_id", PmApplication.getInstance().getShared().getInt(USER_ID));
            jsonObject.put("type", 2);
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
                            if (isRefreshable) {
                                mRefreshableView.finishRefreshing();
                                mClientInfoInfoList.clear();
                            }
                            JSONArray array = result.getJSONArray(KEY_LIST);
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    mClientInfoInfoList.add(new ClientInfo(object.getInt("id"), 0, object.getString("name"), object.getString("description"),
                                            object.getString("pic"), object.getString("cellphone"), object.getString("landline"), object.getString("fax"), object.getString("qq"),
                                            object.getString("email"), object.getString("address"), object.getLong("time"), object.getString("people"), object.getString("wechat")));
                                }
                                mIndex++;
                                adapter.notifyDataSetChanged();
                            }
                            mIsComplete = true;
                            break;
                        case RET_FAIL:
                            mIsComplete = true;
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
                if (isRefreshable) {
                    mRefreshableView.finishRefreshing();
                }
                mIsComplete = true;
            }
        });
    }


}
