package pm.poomoo.autospareparts.view.activity.start;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseActivity;
import pm.poomoo.autospareparts.mode.ReplyInfo;
import pm.poomoo.autospareparts.mode.SupplyInfo;

/**
 * Created by Android_PM on 2015/11/3.
 * 供求发布详情
 */
public class SupplyInformationActivity extends PmBaseActivity {
    @ViewInject(R.id.activity_supply_information_name)
    private TextView textView_name;
    @ViewInject(R.id.activity_supply_information_dateTime)
    private TextView textView_dateTime;
    @ViewInject(R.id.activity_supply_information_content)
    private TextView textView_content;
    @ViewInject(R.id.activity_supply_information_gridview)
    private GridView gridView;
    @ViewInject(R.id.activity_supply_information_listView)
    private ListView listView;
    @ViewInject(R.id.activity_supply_information_editText_comment)
    private EditText editText_comment;
    @ViewInject(R.id.activity_supply_information_btn_comment)
    private Button button_comment;
    @ViewInject(R.id.activity_supply_information_editText_reply)
    private EditText editText_reply;
    @ViewInject(R.id.activity_supply_information_btn_reply)
    private Button button_reply;
    @ViewInject(R.id.activity_supply_information_layout_reply)
    private RelativeLayout reply_layout;
    @ViewInject(R.id.activity_supply_information_layout_comment)
    private LinearLayout comment_layout;

    private final String TAG = SupplyInformationActivity.class.getSimpleName();
    private BitmapUtils bitmapUtils;
    private List<ReplyInfo> list_replyInfos = new ArrayList<>();
    private String name = "跑马科技", dateTime = "2015-11-03 15:21", content = "测试", commentName = "";
    private String[] Urls = {"http://pic1a.nipic.com/2008-12-04/2008124215522671_2.jpg", "http://pic.nipic.com/2007-11-09/2007119122519868_2.jpg", "http://pic14.nipic.com/20110522/7411759_164157418126_2.jpg", "http://img.taopic.com/uploads/allimg/130501/240451-13050106450911.jpg", "http://pic25.nipic.com/20121209/9252150_194258033000_2.jpg", "http://pic.nipic.com/2007-11-09/200711912230489_2.jpg"};
    private ReplyAdapter replyAdapter = null;
    private SupplyInfo supplyInfo = null;
    private int index = 0;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_information);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        clearAllActivity();
        addActivityToArrayList(this);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        HeaderViewHolder headerViewHolder = getHeaderView();
        headerViewHolder.title.setText("贵阳汽配");
        headerViewHolder.linearLayout.setVisibility(View.VISIBLE);
        headerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLastActivity();
            }
        });
        supplyInfo = (SupplyInfo) getIntent().getSerializableExtra("SupplyInfo");
        Log.i(TAG, "supplyInfo:" + supplyInfo);

        textView_name.setText(supplyInfo.getContact());
        textView_dateTime.setText(supplyInfo.getDateTime());
        textView_content.setText(supplyInfo.getContent());
//
        Urls = supplyInfo.getUrls().split(",");
        gridView.setAdapter(new GridViewAdapter(this, Urls));

//        initTestData();
        getReplyInformation(false);
        replyAdapter = new ReplyAdapter(this, list_replyInfos);
        listView.setAdapter(replyAdapter);

        reply_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reply_layout.getVisibility() == View.VISIBLE) {
                    reply_layout.setVisibility(View.GONE);
                }
                if (comment_layout.getVisibility() == View.INVISIBLE)
                    comment_layout.setVisibility(View.VISIBLE);
                //隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void initTestData() {
        list_replyInfos = new ArrayList<>();
        ReplyInfo replyInfo = new ReplyInfo();
        replyInfo.setFloor_user_name("贵阳汽配");
        replyInfo.setContent("你好吗？");
        list_replyInfos.add(replyInfo);
        replyInfo = new ReplyInfo();
        replyInfo.setFloor_user_name("贵阳汽配");
        replyInfo.setRevert_user_name("跑马科技");
        replyInfo.setContent("我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^");
        list_replyInfos.add(replyInfo);
        replyInfo = new ReplyInfo();
        replyInfo.setFloor_user_name("贵阳汽配");
        replyInfo.setRevert_user_name("跑马科技");
        replyInfo.setContent("我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^");
        list_replyInfos.add(replyInfo);
    }

    @OnClick({R.id.activity_supply_information_btn_comment, R.id.activity_supply_information_btn_reply})
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.activity_supply_information_btn_comment:
                ReplyInfo replyInfo = new ReplyInfo();
//                replyInfo.setCommentName("安卓");
//                replyInfo.setReplyName("");
//                replyInfo.setReplyContent(editText_comment.getText().toString().trim());
                list_replyInfos.add(replyInfo);
                replyAdapter.notifyDataSetChanged();
                editText_comment.setText("");
                Toast.makeText(SupplyInformationActivity.this, "留言成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_supply_information_btn_reply:
                replyInfo = new ReplyInfo();
                Log.i("lmf", "commentName:" + commentName);
//                replyInfo.setCommentName(commentName);
//                replyInfo.setReplyName("安卓");
//                replyInfo.setReplyContent(editText_reply.getText().toString().trim());
                list_replyInfos.add(replyInfo);
                replyAdapter.notifyDataSetChanged();
                editText_reply.setText("");
                if (reply_layout.getVisibility() == View.VISIBLE)
                    reply_layout.setVisibility(View.GONE);
                if (comment_layout.getVisibility() == View.INVISIBLE)
                    comment_layout.setVisibility(View.VISIBLE);
                //隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Toast.makeText(SupplyInformationActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    /**
     * @param isRefreshable true-刷新
     *                      获取回复信息
     */
    public void getReplyInformation(final boolean isRefreshable) {
        RequestParams params = new RequestParams();

        params.addBodyParameter(KEY_PACKNAME, "1025");
        params.addBodyParameter("demand_id", supplyInfo.getId());
        params.addBodyParameter("index", Integer.toString(index));

        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    showLog(TAG, responseInfo.result);
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            JSONArray array = result.getJSONArray(KEY_LIST);
                            int len = array.length();
                            if (len > 0) {
                                for (int i = 0; i < len; i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    ReplyInfo replyInfo;
                                    replyInfo = gson.fromJson(object.toString(), ReplyInfo.class);
                                    list_replyInfos.add(replyInfo);
                                }
                                index++;
                                replyAdapter.notifyDataSetChanged();
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
     * 适配器
     */
    public class GridViewAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private String[] Urls;

        public GridViewAdapter(Context context, String[] Urls) {
            inflater = LayoutInflater.from(context);
            this.Urls = Urls;

            if (bitmapUtils == null) {
                bitmapUtils = new BitmapUtils(SupplyInformationActivity.this);
                bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);
            }
        }

        @Override
        public int getCount() {
            return Urls.length;
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

    public class ReplyAdapter extends BaseAdapter {

        private List<ReplyInfo> list;
        private LayoutInflater inflater;
        private SpannableString ss;
        private String replyName;
        private String replyContentStr;

        public ReplyAdapter(Context context, List<ReplyInfo> list) {
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
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
                convertView = inflater.inflate(R.layout.item_for_relay, null);
                holderView.textView = (TextView) convertView.findViewById(R.id.item_for_reply_content);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            ReplyInfo replyInfo = list.get(position);

            replyName = replyInfo.getRevert_user_name();
            commentName = replyInfo.getFloor_user_name();
            replyContentStr = replyInfo.getContent();
            //用来标识在 Span 范围内的文本前后输入新的字符时是否把它们也应用这个效果
            //Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)
            //Spanned.SPAN_INCLUSIVE_EXCLUSIVE(前面包括，后面不包括)
            //Spanned.SPAN_EXCLUSIVE_INCLUSIVE(前面不包括，后面包括)
            //Spanned.SPAN_INCLUSIVE_INCLUSIVE(前后都包括)
            if (TextUtils.isEmpty(replyName)) {
                ss = new SpannableString(commentName + ":" + replyContentStr);
                ss.setSpan(new ForegroundColorSpan(Color.RED), 0,
                        commentName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                ss = new SpannableString(replyName + "回复" + commentName
                        + "：" + replyContentStr);
                ss.setSpan(new ForegroundColorSpan(Color.RED), 0,
                        replyName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(Color.RED), replyName.length() + 2,
                        replyName.length() + commentName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentName = replyName;
            }
            holderView.textView.setText(ss);
            //添加点击事件时，必须设置
            holderView.textView.setMovementMethod(LinkMovementMethod.getInstance());
            holderView.textView.setOnClickListener(new TextClick(commentName));
            return convertView;
        }

        private final class TextClick implements View.OnClickListener {
            private String name;

            public TextClick(String name) {
                super();
                this.name = name;
            }

            @Override
            public void onClick(View v) {
                Log.i("lmf", "name:" + this.name);
                reply_layout.setVisibility(View.VISIBLE);
                comment_layout.setVisibility(View.INVISIBLE);
                editText_reply.setHint("@" + this.name);
                commentName = this.name;
                editText_reply.setHintTextColor(Color.GRAY);
                editText_reply.setFocusable(true);
                editText_reply.requestFocus();
                InputMethodManager inputManager =
                        (InputMethodManager) editText_reply.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText_reply, 0);
            }
        }

        class HolderView {
            public TextView textView;
        }
    }
}
