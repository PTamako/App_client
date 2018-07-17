package com.example.lenovo.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.lenovo.test.model.BaseResponse;
import com.example.lenovo.test.model.LoginFormRequest;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends Activity {
    private Button reg;
    private EditText name, pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.register_edit_account);
        pwd = (EditText) findViewById(R.id.register_edit_pwd);
        reg = (Button) findViewById(R.id.register_btn_register);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = name.getText().toString();
                String userpwd = pwd.getText().toString();
                //从空间获取输入的数据，构造注册对象
                LoginFormRequest request = new LoginFormRequest();
                request.setUserName(username);
                request.setPassword(userpwd);
                doRegister(request);

            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    Toast.makeText(RegisterActivity.this, "连接服务器成功", Toast.LENGTH_SHORT).show();
                    //跳转到下一个页面
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
                case 201:
                    Toast.makeText(RegisterActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void doRegister(final LoginFormRequest request) {
        final String url = Constants.BASE_URL + "/user/zhuce";
        try {
            HttpUtils.post(url, JSONHelper.toJSON(request), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String content = response.body().string();
                    final BaseResponse gatewayResponse = JSONHelper.fromJSON(content, BaseResponse.class);
                    Log.i("TEST", "返回的状态码:" + gatewayResponse.getStatus());
                    //使用Handler异步处理UI
                    Message msg = Message.obtain();
                    if (gatewayResponse.getStatus() == 200) {
                        msg.what = gatewayResponse.getStatus();
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


