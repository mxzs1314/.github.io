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

    private String passWorld1;
    private String passWorld2;

    private EditText pass1;
    private EditText pass2;
    private Object requestObj;
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
//                new changePass().execute();
//                passWorld1 = pass1.getText().toString().trim();
//                passWorld2 = pass2.getText().toString().trim();
//                Toast.makeText(SecondActivity.this, passWorld1 + passWorld2, Toast.LENGTH_LONG).show();
                new changePass().execute();
            }
        });
    }

    class changePass extends AsyncTask<Object, Object, String> {

        @Override
        protected void onPostExecute(String request) {
            if (request.equals("success")) {
                Toast.makeText(SecondActivity.this, requestObj.toString(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SecondActivity.this, "失败", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(request);
        }

        @Override
        protected String doInBackground(Object... params) {
            Log.d(TAG, "doInBackground enter");
            // 根据命名空间和方法得到SoapObject对象
            SoapObject soapObject = new SoapObject(NAME_SPACE, METHOD_NAME);

            // 传递参数
            soapObject.addProperty("aHeader", "abc,123");
            soapObject.addProperty("newPassword1","111111");
            soapObject.addProperty("newPassword2","111111");
//            soapObject.addProperty("ErrString", "false");

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
                requestObj = envelope.getResponse();
                Log.d("tag", "++++++++" + requestObj);

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
