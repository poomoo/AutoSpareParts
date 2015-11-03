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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseFragment;
import pm.poomoo.autospareparts.mode.CompanyInfo;
import pm.poomoo.autospareparts.mode.TypeInfo;
import pm.poomoo.autospareparts.util.PmGlide;
import pm.poomoo.autospareparts.util.pullDownScrollView.PullDownElasticImp;
import pm.poomoo.autospareparts.util.pullDownScrollView.PullDownScrollView;
import pm.poomoo.autospareparts.view.activity.SupplyInformationActivity;
import pm.poomoo.autospareparts.view.activity.company.CompanyInformationActivity;
import pm.poomoo.autospareparts.view.activity.company.CompanyListActivity;


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
    private ListView mListView;//供求发布展示

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");//下拉时间格式
    private int mTypePicWidth = 0;//图片宽
    private int mTypePicHeight = 0;//图片高
    private List<CompanyInfo> advertisement = new ArrayList<CompanyInfo>();//广告
    private ListViewAdapter adapter = null;
    private BitmapUtils bitmapUtils;
    private String[] Utrls = {"http://pic1a.nipic.com/2008-12-04/2008124215522671_2.jpg", "http://pic.nipic.com/2007-11-09/2007119122519868_2.jpg", "http://pic14.nipic.com/20110522/7411759_164157418126_2.jpg", "http://img.taopic.com/uploads/allimg/130501/240451-13050106450911.jpg", "http://pic25.nipic.com/20121209/9252150_194258033000_2.jpg", "http://pic.nipic.com/2007-11-09/200711912230489_2.jpg"};


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
        adapter = new ListViewAdapter(getActivity());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);

        //初始化广告
        ViewGroup.LayoutParams params = mGlide.getLayoutParams();
        params.height = (int) PmApplication.getInstance().getScreenHeight() >> 2;
        mGlide.setLayoutParams(params);
        //初始化下拉刷新
        mPullDownScrollView.setRefreshListener(this);
        mPullDownScrollView.setPullDownElastic(new PullDownElasticImp(getActivity()));
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
    }

    /**
     * 加载子视图
     */
    public void initChildView() {
        mLinearLayout.removeAllViews();
        int n = -1;
        //计算有多少行
        int maxLine = 0;
        if (PmApplication.getInstance().getTypeInfos().size() % 2 == 0)
            maxLine = PmApplication.getInstance().getTypeInfos().size() / 2;
        else maxLine = PmApplication.getInstance().getTypeInfos().size() / 2 + 1;

        //循环所有行，并且取得每行的控件
        for (int i = 0; i < maxLine; i++) {
            //得到子视图第一个视图控件
            n++;
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_for_fragment_one_scroll, null);
            LinearLayout linearOne = (LinearLayout) linearLayout.findViewById(R.id.item_for_fragment_one_linear_one);
            ImageView imageViewOne = (ImageView) linearLayout.findViewById(R.id.item_for_fragment_one_pic_one);
            ViewGroup.LayoutParams params = imageViewOne.getLayoutParams();
            params.width = mTypePicWidth;
            params.height = mTypePicHeight;
            imageViewOne.setLayoutParams(params);
            TextView textViewNameOne = (TextView) linearLayout.findViewById(R.id.item_for_fragment_one_name_one);
            TextView textViewExplainOne = (TextView) linearLayout.findViewById(R.id.item_for_fragment_one_explain_one);

            getBitmap(imageViewOne, PmApplication.getInstance().getTypeInfos().get(n).getPicPath());
            textViewNameOne.setText(PmApplication.getInstance().getTypeInfos().get(n).getName());
            textViewExplainOne.setText(PmApplication.getInstance().getTypeInfos().get(n).getExplain());
            final int numberOne = n;
            linearOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipActivity(numberOne);
                }
            });

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
            mLinearLayout.addView(linearLayout);
        }
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
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_PACKNAME, 1001);
            jsonObject.put("adver_type", 0);
            params.addBodyParameter(KEY, jsonObject.toString());
            showLog(TAG, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

            }
        });
    }


    /**
     * 获取类型接口
     */
    public void getTypeList(final boolean isRefreshable) {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_PACKNAME, 1002);
            params.addBodyParameter(KEY, jsonObject.toString());
            showLog(TAG, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    showLog(TAG, responseInfo.result);
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
     * 获取供求列表
     */
    public void getSupplyInformation() {
        RequestParams params = new RequestParams();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_PACKNAME, 1001);
            jsonObject.put("adver_type", 0);
            params.addBodyParameter(KEY, jsonObject.toString());
            showLog(TAG, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        Log.i(TAG, "onItemClick");
        startActivity(new Intent(getActivity(), SupplyInformationActivity.class));
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
            return Utrls.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
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
            holderView.gridView.setAdapter(new GridViewAdapter(getActivity(), Utrls));

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
