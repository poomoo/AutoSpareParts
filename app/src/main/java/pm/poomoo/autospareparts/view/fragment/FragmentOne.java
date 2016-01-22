package pm.poomoo.autospareparts.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLStreamHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseFragment;
import pm.poomoo.autospareparts.mode.CompanyInfo;
import pm.poomoo.autospareparts.mode.MessageInfo;
import pm.poomoo.autospareparts.mode.SupplyInfo;
import pm.poomoo.autospareparts.mode.TypeInfo;
import pm.poomoo.autospareparts.util.DateUtil;
import pm.poomoo.autospareparts.util.PmGlide;
import pm.poomoo.autospareparts.util.pullDownScrollView.PullDownElasticImp;
import pm.poomoo.autospareparts.util.pullDownScrollView.PullDownScrollView;
import pm.poomoo.autospareparts.view.activity.more.MyMessageInfoActivity;
import pm.poomoo.autospareparts.view.activity.start.SupplyInformationActivity;
import pm.poomoo.autospareparts.view.activity.company.CompanyInformationActivity;
import pm.poomoo.autospareparts.view.activity.company.CompanyListActivity;
import pm.poomoo.autospareparts.view.custom.MyListView;
import pm.poomoo.autospareparts.view.custom.MyListViewMainPager;
import pm.poomoo.autospareparts.view.custom.NoScrollListView;


/**
 * 显示当前商铺列表
 *
 * @author AADC
 */
public class FragmentOne extends PmBaseFragment implements PullDownScrollView.RefreshListener, AdapterView.OnItemClickListener {

    private final String TAG = FragmentOne.class.getSimpleName();
    @ViewInject(R.id.frag_one_pull_scroll_view)
    private PullDownScrollView mPullDownScrollView;//下拉刷新控件
    @ViewInject(R.id.frag_one_glide)
    private PmGlide mGlide;//广告控件
    @ViewInject(R.id.frag_one_linear)
    private LinearLayout mLinearLayout;//加载子视图控件
    @ViewInject(R.id.frag_one_listview)
    private MyListViewMainPager mListView;//供求发布展示

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");//下拉时间格式
    private int mTypePicWidth = 0;//图片宽
    private int mTypePicHeight = 0;//图片高
    private List<CompanyInfo> advertisement = new ArrayList<>();//广告
    private List<MessageInfo> messageInfos = new ArrayList<>();//消息列表


    private String[] Urls;
    private int mIndex = 0;//分页标记
    private myAdapter adapter;
    private BitmapUtils bitmapUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.frag_one, container, false);
        ViewUtils.inject(this, layout);
        init(layout);
        onClickListener();
        return layout;
    }

    /**
     * 初始化
     */
    public void init(View layout) {
        advertisement.clear();
        //初始化头部
        HeaderViewHolder headerViewHolder = getHeaderView(layout);
        headerViewHolder.title.setText("贵阳汽配");

        mTypePicWidth = (int) PmApplication.getInstance().getScreenWidth() / 6;
        mTypePicHeight = mTypePicWidth;

        PmApplication.getInstance().getTypeInfos().clear();
        getAdvertisement();//获取广告列表
        getTypeList(false);//获取类型


        adapter = new myAdapter(getActivity());
        mListView.setAdapter(adapter);
        mListView.setonRefreshListener(new MyListViewMainPager.OnRefreshListener() {
            public void onRefresh() {
                onGetMessageList(false);
            }
        });
        mListView.setOnItemClickListener(this);
        onGetMessageList(false);

        //初始化广告
        ViewGroup.LayoutParams params = mGlide.getLayoutParams();
        params.height = (int) PmApplication.getInstance().getScreenHeight() >> 2;
        mGlide.setLayoutParams(params);
        //初始化下拉刷新
        mPullDownScrollView.setRefreshListener(this);
        mPullDownScrollView.setPullDownElastic(new PullDownElasticImp(getActivity()));
        Log.i(TAG, "init------");
    }

    /**
     * 设置监听
     */
    public void onClickListener() {
        //广告图片点击事件
        mGlide.setPicClickListener(new PmGlide.picOnClickListener() {
            @Override
            public void onPicClick(int index) {
                PmApplication.getInstance().getShowCompanyInfos().clear();
                PmApplication.getInstance().getShowCompanyInfos().add(advertisement.get(index));
                startActivity(new Intent(getActivity(), CompanyInformationActivity.class));
                getActivityInFromRight();
            }
        });
    }

    /**
     * 下拉刷新
     *
     * @param view 刷新控件
     */
    @Override
    public void onRefresh(PullDownScrollView view) {
        getAdvertisement();//下拉刷新广告
        getTypeList(true);//获取类型
        mIndex = 0;
        onGetMessageList(true);
    }

    /**
     * 加载子视图
     */
    public void initChildView() {
        Log.i(TAG, "initChildView开始");
        mLinearLayout.removeAllViews();
        int n = -1;
        //计算有多少行
        int maxLine = 0;
        if (PmApplication.getInstance().getTypeInfos().size() % 2 == 0)
            maxLine = PmApplication.getInstance().getTypeInfos().size() / 2;
        else maxLine = PmApplication.getInstance().getTypeInfos().size() / 2 + 1;
        Log.i(TAG, "initChildView-- maxLine" + maxLine);
        //循环所有行，并且取得每行的控件
        for (int i = 0; i < maxLine; i++) {
            Log.i(TAG, "initChildView-- 循环开始" + i);
            //得到子视图第一个视图控件
            n++;
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_for_fragment_one_scroll, null);
            LinearLayout linearOne = (LinearLayout) linearLayout.findViewById(R.id.item_for_fragment_one_linear_one);
            ImageView imageViewOne = (ImageView) linearLayout.findViewById(R.id.item_for_fragment_one_pic_one);
            Log.i(TAG, "initChildView-- 循环进行中" + n);
            ViewGroup.LayoutParams params = imageViewOne.getLayoutParams();
            params.width = mTypePicWidth;
            params.height = mTypePicHeight;
            imageViewOne.setLayoutParams(params);
            TextView textViewNameOne = (TextView) linearLayout.findViewById(R.id.item_for_fragment_one_name_one);
            TextView textViewExplainOne = (TextView) linearLayout.findViewById(R.id.item_for_fragment_one_explain_one);

            getBitmap(imageViewOne, PmApplication.getInstance().getTypeInfos().get(n).getPicPath());
            Log.i(TAG, "initChildView-- 循环进行中 getBitmap：" + PmApplication.getInstance().getTypeInfos().get(n).getPicPath());
            textViewNameOne.setText(PmApplication.getInstance().getTypeInfos().get(n).getName());
            textViewExplainOne.setText(PmApplication.getInstance().getTypeInfos().get(n).getExplain());
            final int numberOne = n;
            linearOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipActivity(numberOne);
                }
            });
            Log.i(TAG, "initChildView-- 第一个视图控件" + i);
            //得到子视图第二个视图控件(首先要判断是否要显示)
            n++;
            if (n < PmApplication.getInstance().getTypeInfos().size()) {
                LinearLayout linearTwo = (LinearLayout) linearLayout.findViewById(R.id.item_for_fragment_one_linear_two);
                linearTwo.setVisibility(View.VISIBLE);
                ImageView imageViewTwo = (ImageView) linearLayout.findViewById(R.id.item_for_fragment_one_pic_two);
                ViewGroup.LayoutParams params1 = imageViewTwo.getLayoutParams();
                params1.width = mTypePicWidth;
                params1.height = mTypePicHeight;
                imageViewTwo.setLayoutParams(params1);
                TextView textViewNameTwo = (TextView) linearLayout.findViewById(R.id.item_for_fragment_one_name_two);
                TextView textViewExplainTwo = (TextView) linearLayout.findViewById(R.id.item_for_fragment_one_explain_two);

                getBitmap(imageViewTwo, PmApplication.getInstance().getTypeInfos().get(n).getPicPath());
                textViewNameTwo.setText(PmApplication.getInstance().getTypeInfos().get(n).getName());
                textViewExplainTwo.setText(PmApplication.getInstance().getTypeInfos().get(n).getExplain());
                final int numberTwo = n;
                linearTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skipActivity(numberTwo);
                    }
                });
            }
            Log.i(TAG, "initChildView-- 第二个视图控件" + i);
            mLinearLayout.addView(linearLayout);
            Log.i(TAG, "initChildView-- 循环结束" + i);
        }
        Log.i(TAG, "initChildView结束");
    }

    /**
     * 类型点击事件-跳转到公司列表界面
     *
     * @param number 选择的类型
     */
    public void skipActivity(int number) {
        Intent intent = new Intent(getActivity(), CompanyListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(SKIP_NUMBER, number);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivityInFromRight();
    }

    /**
     * 获取广告接口
     */
    public void getAdvertisement() {
        RequestParams params = new RequestParams();

        params.addBodyParameter(KEY_PACKNAME, "1001");
        params.addBodyParameter("adver_type", "0");


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
                                mGlide.initPic(glide, getActivity());
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
                showLog(TAG, msg);
            }
        });
    }


    /**
     * 获取类型接口
     */
    public void getTypeList(final boolean isRefreshable) {
        RequestParams params = new RequestParams();

        params.addBodyParameter(KEY_PACKNAME, "1002");

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    showLog(TAG, "getTypeList返回:" + responseInfo.result);
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (isRefreshable) {
                                mPullDownScrollView.finishRefresh(format.format(new Date(System.currentTimeMillis())));
                                PmApplication.getInstance().getTypeInfos().clear();
                                mLinearLayout.removeAllViews();
                            }
                            JSONArray array = result.getJSONArray("list");
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    if (object.getInt("id") != 18) {
                                        //去掉ID为18的类型
                                        PmApplication.getInstance().getTypeInfos().add(new TypeInfo(object.getInt("id"), object.getString("icon"),
                                                object.getString("name"), object.getString("depict")));
                                    }
                                }
                                initChildView();
                            }
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
     * 获取消息列表
     */
    public void onGetMessageList(final boolean isRefreshable) {
        Log.i(TAG, "onGetMessageList------");
        RequestParams params = new RequestParams();
        params.addBodyParameter(KEY_PACKNAME, "1013");
        params.addBodyParameter("index", mIndex + "");

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, "系统消息返回:" + responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            if (isRefreshable) {
                                mPullDownScrollView.finishRefresh(format.format(new Date(System.currentTimeMillis())));
                                messageInfos.clear();
                            }
                            JSONArray arrayList = result.getJSONArray("list");
                            if (arrayList.length() > 0) {
                                for (int i = 0; i < arrayList.length(); i++) {
                                    JSONObject resultList = new JSONObject(arrayList.get(i).toString());
                                    messageInfos.add(new MessageInfo(resultList.getInt("id"), resultList.getString("pictures"), resultList.getString("title"),
                                            resultList.getString("content"), resultList.getLong("time")));
                                }
                            } else showToast("没有更多的数据");
                            mIndex++;
                            adapter.notifyDataSetChanged();
                            break;
                        case RET_FAIL:
                            if (isRefreshable)
                                mPullDownScrollView.finishRefresh(format.format(new Date(System.currentTimeMillis())));
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                if (isRefreshable)
                    mPullDownScrollView.finishRefresh(format.format(new Date(System.currentTimeMillis())));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGlide.stopAnimation();
    }

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
            return messageInfos.get(position);
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
                convertView = inflater.inflate(R.layout.item_for_system_message_list, null);
                holderView.title = (TextView) convertView.findViewById(R.id.item_for_system_message_textView_title);
                holderView.dateTime = (TextView) convertView.findViewById(R.id.item_for_system_message_textView_date);
                holderView.content = (TextView) convertView.findViewById(R.id.item_for_system_message_textView_content);
                holderView.gridView = (GridView) convertView.findViewById(R.id.item_for_system_message_gridview);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
//            showLog(TAG, "position:" + position + messageInfos.get(position).toString());
            holderView.title.setText(messageInfos.get(position).getTitle());
//            Calendar calendar = Calendar.getInstance();
//            Log.i(TAG, "当前日期:" + calendar.getTime());
//            calendar.setTimeInMillis(messageInfos.get(position).getTime());
//            Log.i(TAG, "时间:" + calendar.getTimeInMillis() + "  日历:" + calendar.getTime());
            holderView.dateTime.setText(DateUtil.getDateWith10Time(messageInfos.get(position).getTime()));
            holderView.content.setText(messageInfos.get(position).getContent());
            Urls = messageInfos.get(position).getPictures().split(",");
            if (Urls.length > 0 && !TextUtils.isEmpty(Urls[0])) {
                holderView.gridView.setVisibility(View.VISIBLE);
                holderView.gridView.setAdapter(new GridViewAdapter(getActivity(), Urls));
            } else {
                holderView.gridView.setVisibility(View.GONE);
            }
            return convertView;
        }

        class HolderView {
            public TextView title;
            public TextView dateTime;
            public TextView content;
            public GridView gridView;
        }
    }

    /**
     * 适配器
     */
    public class GridViewAdapter extends BaseAdapter {

        private String[] Urls;
        private Context context;

        public GridViewAdapter(Context context, String[] Urls) {
            this.Urls = Urls;
            this.context = context;

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
            View view = View.inflate(context, R.layout.item_for_supply_gridview, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.item_for_supply_imageView);
            bitmapUtils.display(imageView, PIC_RUL + Urls[position].substring(2));
            return view;
        }
    }
}
