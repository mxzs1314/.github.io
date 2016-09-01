package com.example.administrator.civilaviation.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import com.example.administrator.civilaviation.R;
import com.example.administrator.civilaviation.common.CivilConst;

/**
 * HelloWorld Activity显示值
 */
public class HelloWorldActivity extends Activity{
    // WSDL文档中的命名空间
    private static final String targetNameSpace = "http://58.213.128.132/";

    // WSDL文档中的URL
    private static final String WSDL = "http://58.213.128.132:888/TestWBS/NIATAMessage.asmx";

    // 需要调用的方法名(获取HelloWorld的值)
    private static final String getSupportProvince = "HelloWorld";
    private TextView showTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helloworld);
        new HellAsyTask().execute();
        showTv = (TextView) findViewById(R.id.hello_tv);
    }

    class HellAsyTask extends AsyncTask<Object, Object, String> {
        Object resultObj;
        @Override
        protected String doInBackground(Object... params) {
            // 根据命名空间和方法得到SoapObject对象
            SoapObject soapObject = new SoapObject(targetNameSpace,
                    getSupportProvince);

            // 通过SOAP1.1协议得到envelop对象
            SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            // 将soapObject对象设置为envelop对象，传出消息
            envelop.dotNet = true;
            envelop.setOutputSoapObject(soapObject);

            // 或者envelop.bodyOut = soapObject;
            HttpTransportSE httpSE = new HttpTransportSE(WSDL);

            // 开始调用远程方法
            try {
                httpSE.call(CivilConst.targetNameSpace + getSupportProvince, envelop);
                // 得到远程方法返回的SOAP对象
                resultObj = envelop.getResponse().toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException";
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return "XmlPullParserException";
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("success")) {
//                Toast.makeText(HelloWorldActivity.this, resultObj+"", Toast.LENGTH_LONG).show();
                showTv.setText(resultObj.toString());
            } else {
                Toast.makeText(HelloWorldActivity.this, "无数据返回", Toast.LENGTH_LONG).show();
            }
        }
    }
}
