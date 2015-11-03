package pm.poomoo.autospareparts.view.activity;

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
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmBaseActivity;
import pm.poomoo.autospareparts.mode.ReplyInfo;

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
    @ViewInject(R.id.activity_supply_information_editText)
    private EditText editText;
    @ViewInject(R.id.activity_supply_information_btn)
    private Button button;


    private BitmapUtils bitmapUtils;
    private List<ReplyInfo> replyInfos = null;
    private String name = "跑马科技", dateTime = "2015-11-03 15:21", content = "测试";
    private String[] Utrls = {"http://pic1a.nipic.com/2008-12-04/2008124215522671_2.jpg", "http://pic.nipic.com/2007-11-09/2007119122519868_2.jpg", "http://pic14.nipic.com/20110522/7411759_164157418126_2.jpg", "http://img.taopic.com/uploads/allimg/130501/240451-13050106450911.jpg", "http://pic25.nipic.com/20121209/9252150_194258033000_2.jpg", "http://pic.nipic.com/2007-11-09/200711912230489_2.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_information);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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

        textView_name.setText(name);
        textView_dateTime.setText(dateTime);
        textView_content.setText(content);

        gridView.setAdapter(new GridViewAdapter(this, Utrls));

        replyInfos = new ArrayList<ReplyInfo>();
        ReplyInfo replyInfo = new ReplyInfo();
        replyInfo.setCommentName("贵阳汽配");
        replyInfo.setReplyName("");
        replyInfo.setReplayContent("你好吗？");
        replyInfos.add(replyInfo);
        replyInfo = new ReplyInfo();
        replyInfo.setCommentName("贵阳汽配");
        replyInfo.setReplyName("跑马科技");
        replyInfo.setReplayContent("我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^我很好呀^_^");
        replyInfos.add(replyInfo);

        listView.setAdapter(new ReplyAdapter(this, replyInfos));
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

    public class ReplyAdapter extends BaseAdapter {

        private List<ReplyInfo> list;
        private LayoutInflater inflater;
        private TextView replyContent;
        private SpannableString ss;
        private Context context;
        private String replyName;
        private String commentName;
        private String replyContentStr;
        private boolean isComment;//true-单独评论 false-回复评论

        public ReplyAdapter(Context context, List<ReplyInfo> list) {
            this.list = list;
            this.context = context;
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
            ReplyInfo replyInfo = list.get(position);
            convertView = inflater.inflate(R.layout.item_for_relay, null);
            replyContent = (TextView)
                    convertView.findViewById(R.id.item_for_reply_content);

            replyName = replyInfo.getReplyName();
            commentName = replyInfo.getCommentName();
            replyContentStr = replyInfo.getReplayContent();
            //用来标识在 Span 范围内的文本前后输入新的字符时是否把它们也应用这个效果
            //Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)
            //Spanned.SPAN_INCLUSIVE_EXCLUSIVE(前面包括，后面不包括)
            //Spanned.SPAN_EXCLUSIVE_INCLUSIVE(前面不包括，后面包括)
            //Spanned.SPAN_INCLUSIVE_INCLUSIVE(前后都包括)
            if (TextUtils.isEmpty(replyName)) {
                ss = new SpannableString(commentName + ":" + replyContentStr);
                ss.setSpan(new ForegroundColorSpan(Color.RED), 0,
                        commentName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                isComment = true;
            } else {
                ss = new SpannableString(replyName + "回复" + commentName
                        + "：" + replyContentStr);
                ss.setSpan(new ForegroundColorSpan(Color.RED), 0,
                        replyName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(Color.RED), replyName.length() + 2,
                        replyName.length() + commentName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                isComment = false;
            }
            Log.i("replyName", replyName + "----" + isComment);
            //为回复的人昵称添加点击事件
//            ss.setSpan(new TextSpanClick(true), 0,
//                    replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            //为评论的人的添加点击事件
//            ss.setSpan(new TextSpanClick(false), replyNickName.length() + 2,
//                    replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            replyContent.setText(ss);
            //添加点击事件时，必须设置
            replyContent.setMovementMethod(LinkMovementMethod.getInstance());
            replyContent.setOnClickListener(new TextClick());
            return convertView;
        }

        private final class TextClick implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                if (isComment)
                    editText.setHint("@" + commentName);
                else
                    editText.setHint("@" + replyName);
                editText.setHintTextColor(Color.GRAY);
                editText.setFocusable(true);
                editText.requestFocus();
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }

    }
}
