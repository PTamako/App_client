package com.example.lenovo.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;

import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.Text;
import com.example.lenovo.test.model.BaseResponse;
import com.example.lenovo.test.model.Comment;
import com.example.lenovo.test.model.CommentFormRequest;
import com.example.lenovo.test.model.CommentList;
import com.example.lenovo.test.model.SearchFormRequest;
import com.example.lenovo.test.model.Store;
import com.example.lenovo.test.model.StoreList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.lenovo.test.R.drawable.marker;


public class CommentActivity extends Activity implements View.OnClickListener {

    RatingBar ratingBar;
//    Button button1;
    public static String TAG = "CommentActivity";
    private ImageView comment;
    private TextView hide_down;
    private EditText comment_content;
    private Button comment_send;


    private LinearLayout rl_enroll;
    private RelativeLayout rl_comment;

    private AdapterComment adapterComment;
    private ListView comment_list;
    private List<Comment> data = new ArrayList<Comment>();
    private float score = 1;//评分
    private String storeName;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        ratingBar.setOnRatingBarChangeListener(new RatingBarChangeListener());
//        button1 = (Button) findViewById(R.id.buttn1);
//        button1.setOnClickListener(new ClickListener());
        Intent intent = getIntent();
        storeName = intent.getStringExtra("extra_data");
        username = intent.getStringExtra("data1");
        initView();
        CommentFormRequest request = new CommentFormRequest();
        request.setUserName(username);
        request.setStoreName(storeName);
        getCommentData(request);//发送获取评论数据请求，在请求返回中刷新列表
    }

    private void initView() {

        // 初始化评论列表
        comment_list = (ListView) findViewById(R.id.comment_list);
        // 初始化数据
        data = new ArrayList<>();
        // 初始化适配器
        adapterComment = new AdapterComment(getApplicationContext(), data);
        // 为评论列表设置适配器
        comment_list.setAdapter(adapterComment);


        comment = (ImageView) findViewById(R.id.comment);
        hide_down = (TextView) findViewById(R.id.hide_down);
        comment_content = (EditText) findViewById(R.id.comment_content);
        comment_send = (Button) findViewById(R.id.comment_send);

        rl_enroll = (LinearLayout) findViewById(R.id.rl_enroll);
        rl_comment = (RelativeLayout) findViewById(R.id.rl_comment);

        setListener();
    }

    /**
     * 设置监听
     */
    public void setListener() {
        comment.setOnClickListener(this);

        hide_down.setOnClickListener(this);
        comment_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment:
                // 弹出输入法
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                // 显示评论框
                rl_enroll.setVisibility(View.GONE);
                rl_comment.setVisibility(View.VISIBLE);
                break;
            case R.id.hide_down:
                // 隐藏评论框
                rl_enroll.setVisibility(View.VISIBLE);
                rl_comment.setVisibility(View.GONE);
                // 隐藏输入法，然后暂存当前输入框的内容，方便下次使用
                InputMethodManager im = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(comment_content.getWindowToken(), 0);
                break;
            case R.id.comment_send:
                String content = comment_content.getText().toString();
                CommentFormRequest request = new CommentFormRequest();
                Comment comment = new Comment();
                request.setReview(content);
                request.setUserName(username);
                request.setStoreName(storeName);
                request.setGrade((int) score);
                //向服务器添加数据
                sendComment(request);
                //从显示提交的数据
                adapterComment.addComment(comment);
                break;
            default:
                break;
        }
    }
//    public void sendComment() {
//        if (comment_content.getText().toString().equals("")) {
//            Toast.makeText(getApplicationContext(), "评论不能为空！", Toast.LENGTH_SHORT).show();
//        } else {
//            // 生成评论数据
//            CommentFormRequest request = new CommentFormRequest();
//            doSearch(request);
//            Comment comment = new Comment();
//            request.setUserName("评论者" + (data.size() + 1) + "：");
//            request.setReview(comment_content.getText().toString());
//            adapterComment.addComment(comment);
//            // 发送完，清空输入框
//            comment_content.setText("");
//
//            Toast.makeText(getApplicationContext(), "评论成功！", Toast.LENGTH_SHORT).show();
//        }
//    }

    class RatingBarChangeListener implements RatingBar.OnRatingBarChangeListener {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating,
                                    boolean fromUser) {
            Log.i(TAG, "当前分数=" + rating);
            System.out.println("当前分数=" + rating);
            score = rating;
        }
    }

//    class ClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            //评分提交给服务器
//            CommentFormRequest request1 = new CommentFormRequest();
//            request1.setGrade((int)score);
//            sendComment(request1);
//        }
//    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    Toast.makeText(CommentActivity.this, "连接服务器成功", Toast.LENGTH_SHORT).show();
                    // 发送完，清空输入框
                    comment_content.setText("");
                    break;
                case 201:
                    Toast.makeText(CommentActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0x113:
                    CommentList response = (CommentList) msg.obj;
                    data.clear();//清除老数据
                    data.addAll(response.getList());//添加获取之后的新数据
                    adapterComment.notifyDataSetChanged();//调用Adapter中的notifyDataSetChanged方法，通知ListView数据有变动，需要刷新
                    break;
            }
        }
    };

    /**
     * 发送评论
     *
     * @param request
     */
    public void sendComment(final CommentFormRequest request) {
        final String url = Constants.BASE_URL + "/review/reviewAdd";
        if (comment_content.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "评论不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            try {
                String requetStr = JSONHelper.toJSON(request);
                Log.i("TEST", requetStr);
                HttpUtils.post(url, JSONHelper.toJSON(request), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String content = response.body().string();
                        BaseResponse gatewayResponse = JSONHelper.fromJSON(content, BaseResponse.class);
                        Log.i("TEST", "返回的状态码:" + gatewayResponse.getStatus());
                        //使用Handler异步处理UI
                        Message msg = Message.obtain();
                        if (gatewayResponse.getStatus() == 200) {
                            msg.what = gatewayResponse.getStatus();
                            msg.obj = gatewayResponse;
                        } else {
                            msg.what = gatewayResponse.getStatus();
                        }
                        handler.sendMessage(msg);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //发送获取评论请求，在回调中设置列表数据并刷新列表
    public void getCommentData(CommentFormRequest request) {
        final String url = Constants.BASE_URL + "/review/reviewQuery";
        try {
            String requetStr = JSONHelper.toJSON(request);
            Log.i("TEST", requetStr);
            HttpUtils.post(url, JSONHelper.toJSON(request), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String content = response.body().string();
                    if (content != null && content != "") {
                        final CommentList commentResponse = JSONHelper.fromJSON(content, CommentList.class);
                        Log.i("TEST", "返回的状态码:" + commentResponse.getStatus());
                        //使用Handler异步处理UI
                        Message msg = Message.obtain();
                        if (commentResponse.getStatus() == 200 && null != commentResponse.getList()) {
                            msg.what = 0x113;//刷新列表的标识码
                            msg.obj = commentResponse;
                            handler.sendMessage(msg);
                        }
                    } else {
                        Toast.makeText(CommentActivity.this, "暂无评论", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


