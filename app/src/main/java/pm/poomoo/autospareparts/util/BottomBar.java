package pm.poomoo.autospareparts.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import pm.poomoo.autospareparts.R;


/**
 * @author ysy
 * @Function 底部选项封装
 */
public class BottomBar extends LinearLayout implements OnClickListener {

    private Context context;
    private OnItemChangedListener onItemChangedListener;
    private LinearLayout[] mLinearLayout = new LinearLayout[5];
    //    private TextView[] mTxtView = new TextView[5];
    private ImageView[] mImageView = new ImageView[5];

    public BottomBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bottom_bar, null);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));
        mImageView[0] = (ImageView) view.findViewById(R.id.bottom_image_one);
        mImageView[1] = (ImageView) view.findViewById(R.id.bottom_image_two);
        mImageView[2] = (ImageView) view.findViewById(R.id.bottom_image_three);
        mImageView[3] = (ImageView) view.findViewById(R.id.bottom_image_four);
        mImageView[4] = (ImageView) view.findViewById(R.id.bottom_image_five);

        mLinearLayout[0] = (LinearLayout) view.findViewById(R.id.linear_one);
        mLinearLayout[1] = (LinearLayout) view.findViewById(R.id.linear_two);
        mLinearLayout[2] = (LinearLayout) view.findViewById(R.id.linear_three);
        mLinearLayout[3] = (LinearLayout) view.findViewById(R.id.linear_four);
        mLinearLayout[4] = (LinearLayout) view.findViewById(R.id.linear_five);

        for (LinearLayout linear : mLinearLayout) {
            linear.setOnClickListener(this);
        }
        addView(view);
        mImageView[0].setImageResource(R.drawable.bottom_bar_main_down);
//        mLinearLayout[0].setBackgroundColor(Color.parseColor("#034094"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_one:
                onItemChangedListener.onItemChanged(0);
                cancelLinearBackground(0);
                break;
            case R.id.linear_two:
                onItemChangedListener.onItemChanged(1);
                cancelLinearBackground(1);
                break;
            case R.id.linear_three:
//                onItemChangedListener.onItemChanged(2);
//                cancelLinearBackground(2);
                break;
            case R.id.linear_four:
                onItemChangedListener.onItemChanged(3);
                cancelLinearBackground(3);
                break;
            case R.id.linear_five:
                onItemChangedListener.onItemChanged(4);
                cancelLinearBackground(4);
                break;
        }
    }

    public void cancelLinearBackground(int number) {
        mImageView[0].setImageResource(R.drawable.bottom_bar_main_normal);
        mImageView[1].setImageResource(R.drawable.bottom_bar_client_normal);
        mImageView[3].setImageResource(R.drawable.bottom_bar_supply_normal);
        mImageView[4].setImageResource(R.drawable.bottom_bar_more_normal);
        switch (number) {
            case 0:
                mImageView[0].setImageResource(R.drawable.bottom_bar_main_down);
                break;
            case 1:
                mImageView[1].setImageResource(R.drawable.bottom_bar_client_down);
                break;
            case 3:
                mImageView[3].setImageResource(R.drawable.bottom_bar_supply_down);
                break;
            case 4:
                mImageView[4].setImageResource(R.drawable.bottom_bar_more_down);
                break;
        }
    }

    public interface OnItemChangedListener {
        public void onItemChanged(int index);
    }

    public void setOnItemChangedListener(OnItemChangedListener onItemChangedListener) {
        this.onItemChangedListener = onItemChangedListener;
    }

}
