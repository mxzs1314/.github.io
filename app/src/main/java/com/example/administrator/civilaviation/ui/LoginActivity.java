package com.example.administrator.civilaviation.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.civilaviation.R;

public class LoginActivity extends Activity implements View.OnClickListener, View.OnLongClickListener{
    private EditText et_name;
    private EditText et_pass;
    private Button mLoginButton;
    private Button mLoginError;
    private Button mResgister;

    // 天气测试接口的btn（后期删除）
    private Button weatherBtn;

    // 检查更新版本的btn
    private Button updateBtn;

    private TextView ONLYTEST;
    int selectIndex = 1;
    int tempSelect = selectIndex;
    boolean isReLogin = false;
    private int SERVER_FLAG = 0;
    private RelativeLayout countryselete;
    private TextView coutry_phone_sn;
    private TextView coutryName;
    public final static int LOGIN_ENABLE = 0x01;
    public final static int LOGIN_UNABLE = 0x02;
    public final static int PASS_ERROR = 0x03;
    public final static int NAME_ERROR = 0x04;

    // 账号密码
    private String userName;
    private String userPass;

    final Handler UiMangerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_ENABLE:
                    mLoginButton.setClickable(true);
                    break;
                case LOGIN_UNABLE:
                    mLoginButton.setClickable(false);
                    break;
                case  PASS_ERROR:
                    break;
                case NAME_ERROR:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Button bt_username_clear;
    private Button bt_pwd_clear;
    private Button bt_pwd_eye;
    private TextWatcher username_watcher;
    private TextWatcher password_watcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_name = (EditText) findViewById(R.id.username);
        et_pass = (EditText) findViewById(R.id.password);
        bt_username_clear = (Button) findViewById(R.id.bt_username_clear);
        bt_pwd_clear = (Button) findViewById(R.id.bt_pwd_clear);
        bt_pwd_eye = (Button) findViewById(R.id.bt_pwd_eye);
        bt_username_clear.setOnClickListener(this);
        bt_pwd_clear.setOnClickListener(this);
        bt_pwd_eye.setOnClickListener(this);

        initWatch();
        et_name.addTextChangedListener(username_watcher);
        et_pass.addTextChangedListener(password_watcher);

        mLoginButton = (Button) findViewById(R.id.login);
        mLoginError = (Button) findViewById(R.id.login_error);
        mResgister = (Button) findViewById(R.id.register);
        ONLYTEST = (Button) findViewById(R.id.registfer);

        // 天气测试
        weatherBtn = (Button) findViewById(R.id.weather);

        // 检查版本的btn
        updateBtn = (Button) findViewById(R.id.update_btn);

        ONLYTEST.setOnClickListener(this);
        ONLYTEST.setOnLongClickListener(this);
        mLoginButton.setOnClickListener(this);
        mResgister.setOnClickListener(this);
        mLoginError.setOnClickListener(this);

        // 测试weather接口
        weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });

        // 版本更新btn的点击事件
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, UpdateActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 手机号，密码输入控件公用这一个watcher
     */
    private void initWatch(){
        username_watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_pass.setText("");
                if (s.toString().length() > 0) {
                    bt_username_clear.setVisibility(View.VISIBLE);
                } else {
                    bt_username_clear.setVisibility(View.GONE);
                }

            }
        };

        password_watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    bt_pwd_clear.setVisibility(View.VISIBLE);
                } else {
                    bt_pwd_clear.setVisibility(View.GONE);
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 登录
            case R.id.login:
                login();
                break;

            // 无法登录（忘记密码了）
            case R.id.login_error:
                break;

            // 注册新用户
            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, HelloWorldActivity.class);
                startActivity(intent);
                break;

            case R.id.registfer:
                if (SERVER_FLAG > 10) {
                    Toast.makeText(this, "测试",Toast.LENGTH_LONG).show();
                }
                SERVER_FLAG++;
                break;

            case R.id.bt_username_clear:
                et_pass.setText("");
                et_name.setText("");
                break;

            case R.id.bt_pwd_clear:
                et_pass.setText("");
                break;

            case R.id.bt_pwd_eye:
                if(et_pass.getInputType() == (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                    bt_pwd_eye.setBackgroundResource(R.drawable.eye_btn);
                    et_pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
                }else{
                    bt_pwd_eye.setBackgroundResource(R.drawable.eye_btn);
                    et_pass.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                et_pass.setSelection(et_pass.getText().toString().length());
                break;

            default:
                break;
        }
    }

    private void login() {
        userName = et_name.getText().toString();
        userPass = et_pass.getText().toString();
        if (userName.equals("18252543532") && userPass.equals("123456")){
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请输入正确的用户名和密码",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.registfer:
                if(SERVER_FLAG>9){

                }
                //   SERVER_FLAG++;
                break;
        }
        return true;
    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(isReLogin){
                Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
                mHomeIntent.addCategory(Intent.CATEGORY_HOME);
                mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                LoginActivity.this.startActivity(mHomeIntent);
            }else{
                LoginActivity.this.finish();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
