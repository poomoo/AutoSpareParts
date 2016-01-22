package pm.poomoo.autospareparts.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pm.poomoo.autospareparts.R;
import pm.poomoo.autospareparts.base.PmApplication;
import pm.poomoo.autospareparts.base.PmBaseFragment;
import pm.poomoo.autospareparts.util.Bimp;
import pm.poomoo.autospareparts.util.BottomBar;
import pm.poomoo.autospareparts.util.FileUtils;
import pm.poomoo.autospareparts.util.MyUtil;
import pm.poomoo.autospareparts.view.activity.pics.PhotoActivity;
import pm.poomoo.autospareparts.view.activity.pics.PhotosActivity;
import pm.poomoo.autospareparts.view.activity.start.LogActivity;


/**
 * 供求发布
 *
 * @author AADC
 */
public class FragmentThree extends PmBaseFragment {

    private final String TAG = FragmentThree.class.getSimpleName();
    @ViewInject(R.id.frag_three_edt_input)
    private EditText mEdtRequirement;
    @ViewInject(R.id.frag_three_edt_input_phone_number)
    private EditText mEdtPhoneNumber;
    @ViewInject(R.id.frag_three_edt_input_address)
    private EditText mEdtAddress;
    @ViewInject(R.id.frag_three_gridview)
    private GridView gridView;

    private GridAdapter adapter;
    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";
    private List<File> files = new ArrayList<>();
    private File file;
    private String pictures = "";
    private int index = 0;
    private String content = "";
    private String contact = "";
    private String address = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.frag_three, container, false);
        ViewUtils.inject(this, layout);
        init(layout);

        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(getActivity());
        adapter.update();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == Bimp.bmp.size()) {
                    new PopupWindows(getActivity(), gridView);
                } else {
                    Intent intent = new Intent(getActivity(), PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.update();
    }

    /**
     * 初始化组件
     */
    public void init(View layout) {
        HeaderViewHolder headerViewHolder = getHeaderView(layout);
        headerViewHolder.title.setText("供求发布");
    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            int size = Bimp.bmp.size();
            if (size == 0)
                return 1;
            else if (size == 9)
                return 9;
            else
                return size + 1;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_for_supply_upload_gridview, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.item_for_supply_upload_imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Log.i("lmf", "position:" + position + "Bimp.bmp.size:" + Bimp.bmp.size());
            if (position == Bimp.bmp.size()) {
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                } else
                    holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
            } else {
                holder.image.setImageBitmap(Bimp.bmp.get(position));
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.drr.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            try {
                                String path = Bimp.drr.get(Bimp.max);
                                System.out.println(path);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
                                String newStr = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
                                files.add(FileUtils.saveBitmap(bm, "" + newStr));
                                Bimp.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    public class PopupWindows extends PopupWindow {
        private View view;

        public PopupWindows(Context mContext, View parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.popup_supply, null);
            LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.popup_supply_layout);

            setContentView(view);
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            setBackgroundDrawable(dw);
            setFocusable(true);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);

            Button bt1 = (Button) view.findViewById(R.id.popup_supply_camera);
            Button bt2 = (Button) view.findViewById(R.id.popup_supply_photo);
            Button bt3 = (Button) view.findViewById(R.id.popup_supply_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PhotosActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int height_top = view.findViewById(R.id.popup_supply_layout).getTop();
                    int height_bottom = view.findViewById(R.id.popup_supply_layout).getBottom();
                    int y = (int) motionEvent.getY();
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (y < height_top || y > height_bottom) {
                            dismiss();
                        }
                    }
                    return true;
                }
            });
        }
    }

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory() + "/myimage/",
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        path = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        getActivity().startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.drr.size() < 9 && resultCode == -1) {
                    Bimp.drr.add(path);
                    files.add(file);
                }
                break;
        }
    }

    /**
     * 设置监听
     *
     * @param view 点击的控件
     */
    @OnClick(R.id.frag_three_btn_commit)
    public void setOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.frag_three_btn_commit:
                content = mEdtRequirement.getText().toString().trim();
                contact = mEdtPhoneNumber.getText().toString().trim();
                address = mEdtAddress.getText().toString().trim();
                if (checkInput(content, contact)) {
                    showLoadingDialog("发布中...");
                    if (Bimp.drr.size() > 0)
                        uploadPics();
                    else
                        commit();
                }
                break;
        }
    }

    private boolean checkInput(String content, String contact) {
        if (TextUtils.isEmpty(content)) {
            showToast("输入内容不能为空");
            return false;
        }

        if (TextUtils.isEmpty(contact)) {
            showToast("请输入联系方式");
            return false;
        }
        return true;
    }

    public void uploadPics() {
        RequestParams params = new RequestParams();
        params.addBodyParameter(KEY_PACKNAME, 1019 + "");
        params.addBodyParameter("pic", files.get(index++));
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            String url = result.getString("url");
                            if (TextUtils.isEmpty(pictures))
                                pictures = url + ",";
                            else
                                pictures += url + ",";
                            Message message = new Message();
                            message.what = 1;
                            myHandler.sendMessage(message);
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("图片上传失败", false);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                showDismissLoadingDialog("网络错误", false);
            }
        });
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                if (index == Bimp.drr.size()) {
                    commit();
                } else {
                    uploadPics();
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 发布供求到服务器
     */
    public void commit() {
        RequestParams params = new RequestParams();
        params.addBodyParameter(KEY_PACKNAME, 1024 + "");
        params.addBodyParameter("user_id", PmApplication.getInstance().getShared().getInt(USER_ID) + "");
        params.addBodyParameter("feedback_content", content);
        params.addBodyParameter("phone_or_email", contact);
        params.addBodyParameter("pictures", pictures);
        params.addBodyParameter("address", address);

        showLog(TAG, "params:" + params.toString());
        new HttpUtils().configTimeout(TIME_OUT).send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showLog(TAG, responseInfo.result);
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    switch (result.getInt(KEY_RESULT)) {
                        case RET_SUCCESS:
                            showDismissLoadingDialog();
                            new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("供求发布成功，我们会尽快跟进").setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mEdtRequirement.setText("");
                                            mEdtPhoneNumber.setText("");
                                            mEdtAddress.setText("");
                                            BottomBar.onItemChangedListener.onItemChanged(1);
                                            BottomBar.instance.cancelLinearBackground(1);
                                        }
                                    }).create().show();
                            break;
                        case RET_FAIL:
                            showDismissLoadingDialog("发布失败", false);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                showDismissLoadingDialog("网络错误", false);
            }
        });
    }
}
