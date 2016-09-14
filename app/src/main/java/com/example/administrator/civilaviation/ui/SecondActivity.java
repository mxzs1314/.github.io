package com.example.administrator.civilaviation.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.civilaviation.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * 第二个界面
 */
public class SecondActivity extends Activity {
    // 命名空间
    private final static String NAME_SPACE = "http://58.213.128.132/";

    // 调用方法名称
    private final static String METHOD_NAME = "ChangePassword";

    // WSDL中文档URL
    private final static String WSDL_URL = "http://58.213.128.132:888/TestWBS/NIATAMessage.asmx";

    private static final String  TAG = "SecondActivity";

    // 新密码
    private String passWorldOne;
    private String passWorldTwo;

    // 用户名和密码
    private String userName;
    private String userPass;

    private EditText pass1;
    private EditText pass2;
    private Object requestObj;
    private  String ErrString = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();
    }

    private void initView() {
        Button changePass = (Button) findViewById(R.id.change_btn);
        pass1 = (EditText)findViewById(R.id.change_pass1);
        pass2 = (EditText)findViewById(R.id.change_pass2);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = getIntent().getStringExtra("userName");
                userPass = getIntent().getStringExtra("userPass");
                passWorldOne = pass1.getText().toString().trim();
                passWorldTwo = pass2.getText().toString().trim();
//                Toast.makeText(SecondActivity.this, passWorld1 + passWorld2, Toast.LENGTH_LONG).show();
                new changePass().execute();
            }
        });
    }

    class changePass extends AsyncTask<Object, Object, String> {

        @Override
        protected void onPostExecute(String request) {
            if (request.equals("success") && !ErrString.equals("")) {
                Toast.makeText(SecondActivity.this, ErrString, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SecondActivity.this, "修改成功", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(request);
        }

        @Override
        protected String doInBackground(Object... params) {
            Log.d(TAG, "doInBackground enter");

            // 根据命名空间和方法得到SoapObject对象
            SoapObject soapObject = new SoapObject(NAME_SPACE, METHOD_NAME);

            // 传递参数
            soapObject.addProperty("aHeader", userName + userPass);
            soapObject.addProperty("newPassword1",passWorldOne);
            soapObject.addProperty("newPassword2",passWorldTwo);
            soapObject.addProperty("ErrString", ErrString);

            // 通过SOAP1.1协议得到envelop对象
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            // 将soapObject对象设置为envelop对象，传出消息
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE httpsSE = new HttpTransportSE(WSDL_URL);
            // 开始调用远程方法
            try {
                httpsSE.call(NAME_SPACE + METHOD_NAME, envelope);

                // 得到远程传回的数据
//                requestObj = envelope.getResponse();
//                if (requestObj.equals(false)) {
                    SoapObject soapObject1 = (SoapObject) envelope.bodyIn;
                    ErrString = soapObject1.getProperty("ErrString").toString();
                    Log.d("tag", "++++++++" + ErrString);
//                }

            } catch (IOException e) {
                e.printStackTrace();
                return "IOException";
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return "XmlPullParserException";
            }
            return "success";
        }
    }

}
