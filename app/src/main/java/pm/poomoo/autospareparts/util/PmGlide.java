package pm.poomoo.autospareparts.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import pm.poomoo.autospareparts.R;


public class PmGlide extends RelativeLayout {

    private static ViewPager mViewPager;// 滑动显示控件
    private boolean mAnimationFlag = true;// 动画是否启动
    private int mNumber = 0;// 需要显示的第几个动画
    private final int mTime = 3000;// 动画间隔时间
    private List<View> mArrayShowView;// 视图集合
    private ImageView[] mArrayImgSpot;// 显示点的ImageView数组
    private int flag = 1;
    private BitmapUtils bitmapUtils;
    private String[] arrayImage;
    //    private String gray = "#FFA5A6A8";
//    private String blue = "#FF2B72CE";
    private picOnClickListener picOnClickListener;

    public PmGlide(Context context) {
        super(context);
    }

    public PmGlide(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    Handler myhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mViewPager.setCurrentItem(msg.what);
        }
    };

    /**
     * 初始化图片
     */
    public void initPic(String[] arrayPic, Context context) {

        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils(context);
//            bitmapUtils.configDefaultLoadingImage(R.drawable.ic_launcher);
            bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);

            bitmapUtils.configDiskCacheEnabled(true);
            bitmapUtils.configMemoryCacheEnabled(false);
        }
        arrayImage = new String[arrayPic.length];
        mArrayImgSpot = new ImageView[arrayPic.length];
        System.arraycopy(arrayPic, 0, arrayImage, 0, arrayPic.length);
        initView(context);
    }


    /**
     * 初始化界面
     *
     * @param context 上下文
     */
    public void initView(Context context) {
        this.removeAllViews();
        mViewPager = new ViewPager(context);
        mArrayShowView = new ArrayList<View>();

        LayoutParams relativeParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        RelativeLayout relative = new RelativeLayout(context);
        relative.setGravity(Gravity.BOTTOM);
        // 横条布局
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linear = new LinearLayout(context);

        linear.setOrientation(LinearLayout.HORIZONTAL);
        linear.setGravity(Gravity.CENTER);
        // 图片布局
        LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(8, 8);
        paramsImage.setMargins(6, 6, 6, 10);

        for (int i = 0; i < arrayImage.length; i++) {
            ImageView image = new ImageView(context);
            image.setScaleType(ImageView.ScaleType.FIT_XY);

            final int number = i;
            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    picOnClickListener.onPicClick(number);
                }
            });
            bitmapUtils.display(image, arrayImage[i]);
            mArrayShowView.add(image);
            mArrayImgSpot[i] = new ImageView(context);
            mArrayImgSpot[i].setBackgroundResource(R.drawable.gray_circle);
            linear.addView(mArrayImgSpot[i], paramsImage);
        }
        mArrayImgSpot[0].setBackgroundResource(R.drawable.blue_circle);
        mViewPager.setAdapter(mypager);
        mViewPager.setPadding(0, 0, 0, 30);
        this.addView(mViewPager);
        relative.addView(linear, params);
        this.addView(relative, relativeParams);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == arrayImage.length - 1 || arg0 == 0) {
                    flag = -flag;
                }
                mNumber = arg0 + flag;
                choiceSpot(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    //设置标点颜色
    public void choiceSpot(int spot) {
        for (int i = 0; i < mArrayImgSpot.length; i++) {
            mArrayImgSpot[i].setBackgroundResource(R.drawable.gray_circle);
        }
        mArrayImgSpot[spot].setBackgroundResource(R.drawable.blue_circle);
    }

    /**
     * 启动动画
     */
    public void startAnimation() {
        mAnimationFlag = true;
        new Thread() {
            public void run() {
                while (mAnimationFlag) {
                    myhandler.sendEmptyMessage(mNumber);
                    mNumber++;
                    try {
                        sleep(mTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 停止动画
     */
    public void stopAnimation() {
        mAnimationFlag = false;
    }

    /**
     * 动画适配器
     */
    PagerAdapter mypager = new PagerAdapter() {

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public int getCount() {
            return mArrayShowView.size();
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mArrayShowView.get(position));
            return mArrayShowView.get(position);
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    };

    //====================================== 图片点击接口 =================================================================
    public interface picOnClickListener {
        public void onPicClick(int index);
    }

    public void setPicClickListener(picOnClickListener picClickListener) {
        this.picOnClickListener = picClickListener;
    }


}
