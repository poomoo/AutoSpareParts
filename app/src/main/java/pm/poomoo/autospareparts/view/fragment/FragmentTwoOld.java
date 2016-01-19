package pm.poomoo.autospareparts.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import pm.poomoo.autospareparts.view.activity.search.SearchAccessoriesActivity;


/**
 * 客户资料
 *
 * @author AADC
 */
public class FragmentTwoOld extends PmBaseFragment {

    private final String TAG = FragmentTwoOld.class.getSimpleName();
    @ViewInject(R.id.frag_two_list_refreshable)
    private RefreshableView refreshableView;//刷新控件
    @ViewInject(R.id.frag_two_list_view)
    private ListView mListView;//列表
//    @ViewInject(R.id.client_list_search)
//    private EditText mEdtSearch;//搜索框
    @ViewInject(R.id.frag_two_linear)
    private LinearLayout mLinearlayout;

    private myAdapter adapter;
    private int mIndex = 1;//分页标记
    private boolean mIsComplete = true;//是否加载完成
    private boolean mIsAddMore = false;
    private List<ClientInfo> clientInfos = new ArrayList<ClientInfo>();//客户列表


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.frag_two, container, false);
        ViewUtils.inject(this, layout);
        init(layout);
        setOnClickListener();
        return layout;
    }

    public void init(View layout) {

        HeaderViewHolder headerViewHolder = getHeaderView(layout);
        headerViewHolder.title.setText("客户资料");
        clientInfos.clear();
        adapter = new myAdapter(getActivity());
        mListView.setAdapter(adapter);
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
        onGetClientList(false);
    }

    /**
     * 设置按钮点击监听
     */
    public void setOnClickListener() {
        //列表点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PmApplication.getInstance().getShowClientInfo().clear();
                PmApplication.getInstance().getShowClientInfo().add(clientInfos.get(position));
                startActivity(new Intent(getActivity(), ClientInformationActivity.class));
                getActivityInFromRight();
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mIsAddMore && scrollState == 0 && mIsComplete) {
                    onGetClientList(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mIsAddMore = (firstVisibleItem + visibleItemCount == totalItemCount);
            }
        });

        //刷新
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                mIndex = 1;
                onGetClientList(true);
            }
        }, 0);

        //软键盘上的搜索按钮点击
//        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
//                    String content = mEdtSearch.getText().toString().trim();
//                    if (!content.equals("")) {
//                        mEdtSearch.setText("");
//                        Intent intent = new Intent(getActivity(), SearchAccessoriesActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("content", content);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        getActivityInFromRight();
//                    } else showToast("查询的内容不能为空");
//                }
//                return false;
//            }
//        });
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

        //        clientInfos.get(number).getSpecialNumber();
        final String[] phone = clientInfos.get(number).getCellPhone().split("[@]");
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

//        one.setText("拨打座机：" + clientInfos.get(number).getSpecialNumber());
//        two.setText("拨打手机");
//
//        one.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + clientInfos.get(number).getSpecialNumber()));
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                getActivityInFromRight();
//            }
//        });
//        two.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                final String[] phone = clientInfos.get(number).getCellPhone().split("[@]");
//                new AlertDialog.Builder(getActivity()).setTitle("提示").setItems(phone, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone[i]));
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        getActivityInFromRight();
//                    }
//                }).create().show();
//            }
//        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
     * 获取客户列表
     */
    public void onGetClientList(final boolean isRefreshable) {
        mIsComplete = false;
        if (PmApplication.getInstance().getShared().getInt(USER_ID) == 0) {
            if (isRefreshable) {
                refreshableView.finishRefreshing();
            }
            return;
        }
        if (PmApplication.getInstance().getShared().getInt(IS_VIP) == 0) {
            return;
        }
        RequestParams params = new RequestParams();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_PACKNAME, 1007);
            jsonObject.put("vip_id", PmApplication.getInstance().getShared().getInt(USER_ID));
            jsonObject.put("index", mIndex);
            params.addBodyParameter(KEY, jsonObject.toString());
            showLog(TAG, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                                refreshableView.finishRefreshing();
                                clientInfos.clear();
                            }
                            JSONArray array = result.getJSONArray(KEY_LIST);
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    int typeId = 0;
                                    if (!(object.get("type_id").toString()).equals("")) {
                                        typeId = Integer.parseInt(object.get("type_id").toString());
                                    }
                                    clientInfos.add(new ClientInfo(object.getInt("id"), typeId, object.getString("name"), object.getString("description"),
                                            object.getString("pic"), object.getString("cellphone"), object.getString("landline"), object.getString("fax"), object.getString("qq"),
                                            object.getString("email"), object.getString("address"), 0, object.getString("people"), object.getString("wechat")));
                                }
                                mIndex++;
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case RET_FAIL:
                            if (isRefreshable) {
                                refreshableView.finishRefreshing();
                                showToast(result.getString("msg"));
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

    @Override
    public void onResume() {
        //判断是不是VIP，如果不是VIP这个模块就不给用
        if (PmApplication.getInstance().getShared().getInt(IS_VIP) == 0) {
            mLinearlayout.setVisibility(View.VISIBLE);
            mLinearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        } else {
            mLinearlayout.setVisibility(View.GONE);
        }
        super.onResume();
    }
}
