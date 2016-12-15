package pm.poomoo.autospareparts.view.activity.company;

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
import android.widget.ImageView;
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
import pm.poomoo.autospareparts.util.PmGlide;
import pm.poomoo.autospareparts.util.RefreshableView;
import pm.poomoo.autospareparts.view.activity.search.SearchCompanyActivity;

/**
 * 显示商家列表
 */
public class CompanyListActivity extends PmBaseActivity {

    private final String TAG = CompanyListActivity.class.getSimpleName();
    @ViewInject(R.id.company_list_glide)
    private PmGlide mGlide;//广告控件
    @ViewInject(R.id.company_list_refreshable)
    private RefreshableView refreshableView;//刷新控件
    @ViewInject(R.id.company_list_view)
    private ListView mListView;//下拉列表
    @ViewInject(R.id.company_list_add_more)
    private ImageView mImageAddMore;//加载更多
    @ViewInject(R.id.company_list_search_company)
    private EditText mEditSearContent;//查询到内容

    private myAdapter adapter;
    private int mTypeNumber = -1;
    private boolean mIsAddMore = false;
    private boolean mIsComplete = true;//加载数据是否完成
    private int mIndex = 0;//分页标记
    private List<CompanyInfo> advertisement = new ArrayList<CompanyInfo>();//广告
    private List<CompanyInfo> companyList = new ArrayList<>();//公司

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);
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
        mTypeNumber = getIntent().getExtras().getInt(SKIP_NUMBER);
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText(PmApplication.getInstance().getTypeInfos().get(mTypeNumber).getName());
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });
        //初始化广告
        ViewGroup.LayoutParams params = mGlide.getLayoutParams();
        params.height = (int) PmApplication.getInstance().getScreenHeight() >> 2;
        mGlide.setLayoutParams(params);

        companyList.clear();
        adapter = new myAdapter(this);
        mListView.setAdapter(adapter);
        getAdvertisement();//获取广告
        getCompanyList(false);//获取公司列表
    }

    /**
     * 设置按钮点击监听
     */
    public void setOnClickListener() {
        //列表点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PmApplication.getInstance().getShowCompanyInfos().clear();
                PmApplication.getInstance().getShowCompanyInfos().add(companyList.get(position));
                startActivity(new Intent(CompanyListActivity.this, CompanyInformationActivity.class));
                getActivityInFromRight();
            }
        });

        //下拉刷新
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                mIndex =0;
                getCompanyList(true);
            }
        }, 0);

        //广告点击事件
        mGlide.setPicClickListener(new PmGlide.picOnClickListener() {
            @Override
            public void onPicClick(int index) {
                PmApplication.getInstance().getShowCompanyInfos().clear();
                PmApplication.getInstance().getShowCompanyInfos().add(advertisement.get(index));
                startActivity(new Intent(CompanyListActivity.this, CompanyInformationActivity.class));
                getActivityInFromRight();
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mIsAddMore && scrollState == 0 && mIsComplete) {
                    getCompanyList(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mIsAddMore = (firstVisibleItem + visibleItemCount == totalItemCount);
            }
        });

        //加载更多
        mImageAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCompanyList(false);
            }
        });

        //软键盘上的搜索按钮点击
        mEditSearContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    String content = mEditSearContent.getText().toString().trim();
                    if (!content.equals("")) {
                        mEditSearContent.setText("");
                        Intent intent = new Intent(CompanyListActivity.this, SearchCompanyActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isTrue", true);
                        bundle.putInt("typeId", PmApplication.getInstance().getTypeInfos().get(mTypeNumber).getId());
                        bundle.putString("content", content);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        getActivityInFromRight();
                    } else showToast("查询的内容不能为空");

                }
                return false;
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
     * 弹出选择拨打电话界面
     */
    public void choicePhoneNumberCall(final int number) {
        final Dialog dialog = new Dialog(CompanyListActivity.this, R.style.no_frame_dialog);
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
        listView.setAdapter(new ArrayAdapter<String>(CompanyListActivity.this, android.R.layout.simple_list_item_1, phone));

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
     * 获取广告接口
     */
    public void getAdvertisement() {
        RequestParams params = new RequestParams();
//        final JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put(KEY_PACKNAME, 1001);
//            jsonObject.put("adver_type", PmApplication.getInstance().getTypeInfos().get(mTypeNumber).getId());
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        params.addBodyParameter(KEY_PACKNAME, "1001");
        params.addBodyParameter("adver_type", PmApplication.getInstance().getTypeInfos().get(mTypeNumber).getId()+"");

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    showLog(TAG, responseInfo.result);
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            advertisement.clear();
                            mGlide.stopAnimation();
                            JSONArray array = result.getJSONArray(KEY_LIST);
                            if (array.length() > 0) {
                                String[] glide = new String[array.length()];
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    glide[i] = PIC_RUL + object.getString("ads_pic").substring(2);
                                    showLog(TAG, glide[i]);
                                    advertisement.add(new CompanyInfo(object.getInt("id"), 0, object.getString("name"), object.getString("description"),
                                            object.getString("pic"), object.getString("cellphone"), object.getString("landline"), object.getString("fax"), object.getString("qq"),
                                            object.getString("email"), "", 0, object.getString("people"), object.getString("wechat")));
                                }
                                mGlide.initPic(glide, CompanyListActivity.this);
                                mGlide.startAnimation();
                            } else mGlide.setVisibility(View.GONE);
                            break;
                        case RET_FAIL:
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

    /**
     * 获取公司列表
     */
    public void getCompanyList(final boolean isRefreshable) {
        mIsComplete = false;
        RequestParams params = new RequestParams();
//        final JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put(KEY_PACKNAME, 1003);
//            jsonObject.put("type_id", PmApplication.getInstance().getTypeInfos().get(mTypeNumber).getId());
//            jsonObject.put("index", mIndex);
//            params.addBodyParameter(KEY, jsonObject.toString());
//            showLog(TAG, jsonObject.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        params.addBodyParameter(KEY_PACKNAME, "1003");
        params.addBodyParameter("type_id", PmApplication.getInstance().getTypeInfos().get(mTypeNumber).getId()+"");
        params.addBodyParameter("index", mIndex+"");

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGlide.stopAnimation();
    }
}
