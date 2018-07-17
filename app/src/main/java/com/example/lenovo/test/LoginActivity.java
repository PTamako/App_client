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


public class LoginActivity extends Activity {
    // 定义界面中文本框
    EditText password;
    EditText userName;
    // 定义界面中按钮
    Button bnLogin;
    Button bnRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 获取界面中编辑框
        userName = (EditText) findViewById(R.id.login_edit_account);
        password = (EditText) findViewById(R.id.login_edit_pwd);
        // 获取界面中的按钮
        bnLogin = (Button) findViewById(R.id.login_btn_login);
        bnRegister = (Button) findViewById(R.id.login_btn_register);
        bnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString();
                String userpwd = password.getText().toString();
                //从空间获取输入的数据，构造登录对象
                LoginFormRequest request = new LoginFormRequest();
                request.setUserName(username);
                request.setPassword(userpwd);
                doLogin(request);
            }
        });
        bnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    Toast.makeText(LoginActivity.this, "连接服务器成功", Toast.LENGTH_SHORT).show();
                    //跳转到下一个页面

                    String user = userName.getText().toString();
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    intent1.putExtra("data",user);
                    startActivity(intent1);
                    break;
                case 201:
                    Toast.makeText(LoginActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 用于发送登录请求到服务器端
     *
     * @param request 发送给服务器的登录对象
     */
    public void doLogin(final LoginFormRequest request) {
        final String url = Constants.BASE_URL + "/user/doLogin";
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